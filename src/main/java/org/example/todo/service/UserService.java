package org.example.todo.service;

import lombok.extern.slf4j.Slf4j;
import org.example.todo.dto.AccountCreationRequest;
import org.example.todo.dto.LoginDto;
import org.example.todo.dto.UserDto;
import org.example.todo.dto.UserProfileDto;
import org.example.todo.dto.WorkspaceDto;
import org.example.todo.exception.ImproperResourceSpecification;
import org.example.todo.exception.ResourceNotFoundException;
import org.example.todo.kafka.KafkaProducer;
import org.example.todo.model.Login;
import org.example.todo.model.Membership;
import org.example.todo.model.User;
import org.example.todo.model.UserProfile;
import org.example.todo.model.Workspace;
import org.example.todo.repository.MembershipRepository;
import org.example.todo.repository.UserRepository;
import org.example.todo.repository.WorkspaceRepository;
import org.example.todo.util.ResponseContainer;
import org.example.todo.util.ResponseUtils;
import org.example.todo.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
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

	private MembershipRepository membershipRepository;

	//TODO: this shouldn't be in the UserService, move to WorkspaceService and cleanup associated logic
	private WorkspaceRepository workspaceRepository;

	private WorkspaceService workspaceService;

	private PasswordEncoder passwordEncoder;

	private KafkaProducer<UserDto> kafkaProducer;

	//TODO: Enable filtering and sorting
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	//TODO: Enable filtering and sorting
	public ResponseContainer<UserDto> getAllUsersResponse(PageRequest pageRequest) {
		return ResponseUtils.pageToDtoResponseContainer(userRepository.findAll(pageRequest), UserDto.class);
	}

	public User findUserByUuid(UUID uuid) throws ResourceNotFoundException {
		return userRepository.findByUuid(uuid).orElseThrow(() -> new ResourceNotFoundException(String.format("User not found with id: %s", uuid)));
	}

	public ResponseContainer<UserDto> findUserByUuidResponse(UUID uuid) throws ResourceNotFoundException {
		return ResponseUtils.pageToDtoResponseContainer(Collections.singletonList(findUserByUuid(uuid)), UserDto.class);
	}

	@Transactional
	public User createUser(AccountCreationRequest accountCreationRequest) throws ImproperResourceSpecification, ResourceNotFoundException {
		UserDto userDto = accountCreationRequest.getUser();
		WorkspaceDto workspaceDto = accountCreationRequest.getWorkspace();
		LoginDto loginDto = accountCreationRequest.getLogin();

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
			UserProfileDto userProfileDto = userDto.getUserProfile();
			UserProfile userProfile = UserProfile.builder()
					.firstName(userProfileDto.getFirstName())
					.lastName(userProfileDto.getLastName())
					.email(userProfileDto.getEmail())
					.build();

			//Create user
			user = User.builder()
					.status(userDto.getStatus())
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
			workspace = workspaceService.createWorkspace(workspaceDto);
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
		user.setMemberships(userMemberships);
		membership.setUser(user);

		//Get current Workspace memberships and add new one, if any
		Set<Membership> workspaceMemberships = workspace.getMemberships();
		workspaceMemberships.add(membership);
		workspace.setMemberships(workspaceMemberships);
		membership.setWorkspace(workspace);

		workspaceRepository.save(workspace);
		userRepository.save(user);

		kafkaProducer.sendMessage(KAFKA_TOPIC, ResponseUtils.convertToDto(user, UserDto.class));

		return user;
	}

	@Transactional
	public ResponseContainer<UserDto> createUserResponse(AccountCreationRequest accountCreationRequest) throws ImproperResourceSpecification, ResourceNotFoundException {
		return ResponseUtils.pageToDtoResponseContainer(Collections.singletonList(createUser(accountCreationRequest)), UserDto.class);
	}

	@Transactional
	public User updateUser(UserDto userUpdate) throws ResourceNotFoundException, ImproperResourceSpecification {
		if (Objects.nonNull(userUpdate.getUuid())) {
			// User is being updated
			log.debug("Updating User {}", userUpdate);
			User user = findUserByUuid(userUpdate.getUuid());

			UserProfile userProfile = user.getUserProfile();

			UserProfileDto updateProfile = userUpdate.getUserProfile();

			userProfile.setFirstName(updateProfile.getFirstName());
			userProfile.setLastName(updateProfile.getLastName());
			userProfile.setEmail(updateProfile.getEmail());

			user.setStatus(userUpdate.getStatus());
			return userRepository.save(user);
		}
		else {
			throw new ImproperResourceSpecification("Must specify a UUID when updating a resource");
		}
	}

	@Transactional
	public ResponseContainer<UserDto> updateUserResponse(UserDto userUpdate) throws ResourceNotFoundException, ImproperResourceSpecification {
		return ResponseUtils.pageToDtoResponseContainer(Collections.singletonList(updateUser(userUpdate)), UserDto.class);
	}

	@Transactional(readOnly = true)
	public Set<Workspace> getAllWorkspacesForUserUuid(UUID uuid) throws ResourceNotFoundException {
		User user = findUserByUuid(uuid);
		Set<Membership> memberships = user.getMemberships();
		return memberships.stream().map(Membership::getWorkspace).collect(Collectors.toSet());
	}

	@Transactional
	public ResponseContainer<WorkspaceDto> getAllWorkspacesForUserUuidResponse(UUID uuid) throws ResourceNotFoundException {
		return ResponseUtils.pageToDtoResponseContainer(new ArrayList<>(getAllWorkspacesForUserUuid(uuid)), WorkspaceDto.class);
	}

	@Transactional
	public User deleteUser(UUID uuid) throws ResourceNotFoundException {
		User user = findUserByUuid(uuid);
		user.setStatus(Status.DELETED);

		Set<Membership> memberships = user.getMemberships();
		membershipRepository.deleteAll(memberships);

		user.setLogin(null);
		user.setMemberships(new HashSet<>());
		user.setUserProfile(null);


		return userRepository.save(user);
	}

	//Service cannot receive messages from itself, but this would be the basic config for setting up the listener
/*	@KafkaListener(topics = KAFKA_TOPIC)
	public void listen(UserDto userDto,
	                   Acknowledgment acknowledgment) {
		log.debug("Received Message: {}", userDto);
		acknowledgment.acknowledge();
	}*/

	@Transactional
	public ResponseContainer<UserDto> deleteUserResponse(UUID uuid) throws ResourceNotFoundException {
		return ResponseUtils.pageToDtoResponseContainer(Collections.singletonList(deleteUser(uuid)), UserDto.class);
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
	public void setWorkspaceRepository(WorkspaceRepository workspaceRepository) {
		this.workspaceRepository = workspaceRepository;
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
}
