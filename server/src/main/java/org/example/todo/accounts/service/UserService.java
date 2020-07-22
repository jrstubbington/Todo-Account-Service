package org.example.todo.accounts.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.todo.accounts.generated.dto.AccountCreationRequest;
import org.example.todo.accounts.generated.dto.JobProcessResponse;
import org.example.todo.accounts.generated.dto.LoginDto;
import org.example.todo.accounts.generated.dto.ResponseContainerUserDto;
import org.example.todo.accounts.generated.dto.ResponseContainerWorkspaceDto;
import org.example.todo.accounts.generated.dto.UserDto;
import org.example.todo.accounts.generated.dto.UserProfileDto;
import org.example.todo.accounts.generated.dto.WorkspaceDto;
import org.example.todo.accounts.model.Login;
import org.example.todo.accounts.model.Membership;
import org.example.todo.accounts.model.User;
import org.example.todo.accounts.model.UserProfile;
import org.example.todo.accounts.model.Workspace;
import org.example.todo.accounts.repository.MembershipRepository;
import org.example.todo.accounts.repository.UserRepository;
import org.example.todo.common.exceptions.ImproperResourceSpecification;
import org.example.todo.common.exceptions.ResourceNotFoundException;
import org.example.todo.common.kafka.KafkaOperation;
import org.example.todo.common.kafka.KafkaProducer;
import org.example.todo.common.util.ResponseUtils;
import org.example.todo.common.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

	private static final String KAFKA_TOPIC = "users";

	private UserRepository userRepository;

	//TODO: this shouldn't be in the UserService, move to MembershipService and cleanup associated logic
	private MembershipRepository membershipRepository;

	private WorkspaceService workspaceService;

	private PasswordEncoder passwordEncoder;

	private KafkaProducer<UserDto> kafkaProducer;

	@Autowired
	private SpringValidatorAdapter validator;

	//TODO: Enable filtering and sorting
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	//TODO: Enable filtering and sorting
	@Transactional
	public ResponseContainerUserDto getAllUsersResponse(PageRequest pageRequest) {
		return ResponseUtils.convertToDtoResponseContainer(userRepository.findAll(pageRequest), UserDto.class, ResponseContainerUserDto.class);
	}
	@Transactional
	public User findUserByUuid(UUID uuid) {
		return userRepository.findByUuid(uuid).orElseThrow(() -> new ResourceNotFoundException(String.format("User not found with id: %s", uuid)));
	}

	@Transactional
	public ResponseContainerUserDto findUserByUuidResponse(UUID uuid){
		return ResponseUtils.convertToDtoResponseContainer(findUserByUuid(uuid), UserDto.class, ResponseContainerUserDto.class);
	}

	//TODO: Return an AccountCreationRequest here instead of a user
	@Transactional
	public User createUser(@Valid AccountCreationRequest accountCreationRequest) {
		@Valid UserDto userDto = accountCreationRequest.getUser();
		@Valid WorkspaceDto workspaceDto = accountCreationRequest.getWorkspace();
		@Valid LoginDto loginDto = accountCreationRequest.getLogin();

		if (Objects.isNull(userDto) || Objects.isNull(workspaceDto)) {
			throw new ImproperResourceSpecification("Need to specify both user and workspace information when creating a new user or workspace");
		}
		//Create or get existing user
		User user;
		if (Objects.isNull(userDto.getUuid())) {
			if (Objects.isNull(loginDto)) {
				throw new ImproperResourceSpecification("Need to specify login information when creating a new user");
			}
			//Create new User
			//Convert LoginDto to Login Object
			Login login = new Login();
			login.setUsername(loginDto.getUsername());
			login.setPasswordHash(passwordEncoder.encode(loginDto.getPlainTextPassword()));

			//Convert UserProfileDto to UserProfile
			@Valid UserProfileDto userProfileDto = userDto.getUserProfile();
			UserProfile userProfile = UserProfile.builder()
					.firstName(userProfileDto.getFirstName())
					.lastName(userProfileDto.getLastName())
					.email(userProfileDto.getEmail())
					.build();

			//Create user
			user = User.builder()
					.status(Status.valueOf(userDto.getStatus().toString()))
					.userProfile(userProfile)
					.login(login)
					.memberships(new HashSet<>())
					.build();
		}
		else {
			//User already exists, get that user
			user = findUserByUuid(userDto.getUuid());
		}
		//Create or get workspace
		Workspace workspace;
		if (Objects.isNull(workspaceDto.getUuid())) {
			//User is being added to a new Workspace, need to create that workspace
			workspace = workspaceService.createWorkspace(userDto.getUuid(), workspaceDto);
		}
		else {
			//User is being added to an already existing Workspace, get that workspace
			workspace = workspaceService.findWorkspaceByUuid(workspaceDto.getUuid());
		}

		//Create Memberships
		//TODO: Right now roles are meaningless, but these roles need to be grabbed from the request or DB
		Membership membership = Membership.builder()
				.roleId(1L)
				.build();

		//Assign Memberships
		//Get current User memberships and add new one, if any
		Set<Membership> userMemberships = user.getMemberships();
		userMemberships.add(membership);
//		user.setMemberships(userMemberships);
//		membership.setUser(user);
//		membership.setUserUuid(user.getUuid());

		//Get current Workspace memberships and add new one, if any
		Set<Membership> workspaceMemberships = workspace.getMemberships();
		workspaceMemberships.add(membership);
		workspace.setMemberships(workspaceMemberships);
		membership.setWorkspace(workspace);

		User savedUser = userRepository.saveAndFlush(user);

		kafkaProducer.sendMessage(KAFKA_TOPIC, KafkaOperation.CREATE,
				ResponseUtils.convertToDto(savedUser, UserDto.class));

		return savedUser;
	}

	@Transactional
	public ResponseContainerUserDto createUserResponse(@Valid AccountCreationRequest accountCreationRequest)  {
		return ResponseUtils.convertToDtoResponseContainer(createUser(accountCreationRequest), UserDto.class, ResponseContainerUserDto.class);
	}

	@Transactional
	public User updateUser(@Valid UserDto userUpdate){
		if (Objects.nonNull(userUpdate.getUuid())) {
			// User is being updated
			log.debug("Updating User {}", userUpdate);
			User user = findUserByUuid(userUpdate.getUuid());

			UserProfile userProfile = user.getUserProfile();

			@Valid UserProfileDto updateProfile = userUpdate.getUserProfile();

			userProfile.setFirstName(updateProfile.getFirstName());
			userProfile.setLastName(updateProfile.getLastName());
			userProfile.setEmail(updateProfile.getEmail());

			user.setStatus(Status.valueOf(userUpdate.getStatus().toString()));

			User savedUser = userRepository.saveAndFlush(user);

			kafkaProducer.sendMessage(KAFKA_TOPIC, KafkaOperation.UPDATE,
					ResponseUtils.convertToDto(savedUser, UserDto.class));

			return savedUser;
		}
		else {
			throw new ImproperResourceSpecification("Must specify a UUID when updating a user");
		}
	}

	@Transactional
	public ResponseContainerUserDto updateUserResponse(@Valid UserDto userUpdate)  {
		return ResponseUtils.convertToDtoResponseContainer(updateUser(userUpdate), UserDto.class, ResponseContainerUserDto.class);
	}

	@Transactional
	public Set<User> getAllUsersInWorkspace(UUID uuid) {
		if (Objects.nonNull(uuid)) {
			//Attempt to find workspace or else throw from workspaceService
			workspaceService.findWorkspaceByUuid(uuid);
			return userRepository.findDistinctByMemberships_workspaceUuid(uuid);
		}
		else {
			throw new ImproperResourceSpecification("Must specify UUID of workspace when searching for users in that workspace");
		}
	}

	@Transactional
	public ResponseContainerUserDto getAllUsersInWorkspaceResponse(UUID uuid) {
		return ResponseUtils.convertToDtoResponseContainer(new ArrayList<>(getAllUsersInWorkspace(uuid)), UserDto.class, ResponseContainerUserDto.class);
	}


	@Transactional(readOnly = true)
	public Set<Workspace> getAllWorkspacesForUserUuid(UUID uuid) {
//		User user = findUserByUuid(uuid);
//		Set<Membership> memberships = user.getMemberships();
		List<Membership> memberships = membershipRepository.findAllByUserUuid(uuid);
		return memberships.stream().map(Membership::getWorkspace).collect(Collectors.toSet());
	}

	@Transactional
	public ResponseContainerWorkspaceDto getAllWorkspacesForUserUuidResponse(UUID uuid) {
		return ResponseUtils.convertToDtoResponseContainer(new ArrayList<>(getAllWorkspacesForUserUuid(uuid)), WorkspaceDto.class, ResponseContainerWorkspaceDto.class);

	}

	@Transactional
	public User deleteUser(UUID uuid) {
		User user = findUserByUuid(uuid);
		user.setStatus(Status.DELETED);

		Set<Membership> memberships = user.getMemberships();
		membershipRepository.deleteAll(memberships);

		user.setLogin(null);
		user.setMemberships(new HashSet<>());
		user.setUserProfile(null);

		User savedUser = userRepository.saveAndFlush(user);

		kafkaProducer.sendMessage(KAFKA_TOPIC, KafkaOperation.DELETE,
				ResponseUtils.convertToDto(savedUser, UserDto.class));

		return savedUser;
	}

	@Transactional
	public ResponseContainerUserDto deleteUserResponse(UUID uuid) {
		return ResponseUtils.convertToDtoResponseContainer(deleteUser(uuid), UserDto.class, ResponseContainerUserDto.class);
	}

	@Autowired
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Autowired
	public void setMembershipRepository(MembershipRepository membershipRepository) {
		this.membershipRepository = membershipRepository;
	}

	@Autowired
	public void setKafkaProducer(KafkaProducer<UserDto> kafkaProducer) {
		this.kafkaProducer = kafkaProducer;
	}

	@Autowired
	public void setWorkspaceService(WorkspaceService workspaceService) {
		this.workspaceService = workspaceService;
	}

	@Autowired
	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public JobProcessResponse batchUpload(InputStream inputStream) {
		try (Workbook workbook = new XSSFWorkbook(inputStream)) {
			//File format is correct
			log.trace("Uploaded file format is a valid Excel file");
			List<User> users = new ArrayList<>();
			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				User user = new User();
				if (row.getRowNum() == 0) {
					continue;
				}
				UserProfile userProfile = UserProfile.builder()
						.firstName(row.getCell(0).getStringCellValue())
						.lastName(row.getCell(1).getStringCellValue())
						.email(row.getCell(2).getStringCellValue())
						.build();
				user.setUserProfile(userProfile);
				users.add(user);
				user.setStatus(Status.ACTIVE);
				Set<ConstraintViolation<User>> userViolations = validator.validate(user);
				Set<ConstraintViolation<UserProfile>> profileViolations = validator.validate(userProfile);
				if (!userViolations.isEmpty() || !profileViolations.isEmpty()) {
					return null;
				}
				if (users.size() >= 2000 || row.getRowNum() == sheet.getPhysicalNumberOfRows()-1) {
					StopWatch stopWatch = new StopWatch();
					stopWatch.start();
					userRepository.saveAll(users);
					stopWatch.stop();
					log.info("SAVED {} entities in {} seconds", users.size(), stopWatch.getTotalTimeSeconds());
					users = new ArrayList<>();
				}
			}
		}
		catch (NotOfficeXmlFileException | IOException e) {
			log.trace("Error thrown while trying to verify file format", e);
			throw new ImproperResourceSpecification("Specified file is not an Excel File");
		}
		return new JobProcessResponse();
	}
}
