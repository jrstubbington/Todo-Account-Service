package org.example.todo.accounts.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.todo.accounts.dto.AccountCreationRequest;
import org.example.todo.accounts.dto.JobProcessResponse;
import org.example.todo.accounts.model.Login;
import org.example.todo.accounts.model.Membership;
import org.example.todo.accounts.model.User;
import org.example.todo.accounts.model.UserProfile;
import org.example.todo.accounts.model.Workspace;
import org.example.todo.accounts.repository.MembershipRepository;
import org.example.todo.accounts.repository.UserRepository;
import org.example.todo.common.dto.LoginDto;
import org.example.todo.common.dto.UserDto;
import org.example.todo.common.dto.UserProfileDto;
import org.example.todo.common.dto.WorkspaceDto;
import org.example.todo.common.exceptions.ImproperResourceSpecification;
import org.example.todo.common.exceptions.ResourceNotFoundException;
import org.example.todo.common.kafka.KafkaOperation;
import org.example.todo.common.kafka.KafkaProducer;
import org.example.todo.common.util.ResponseContainer;
import org.example.todo.common.util.ResponseUtils;
import org.example.todo.common.util.Status;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
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

	//TODO: this shouldn't be in the UserService, move to MembershipService and cleanup associated logic
	private MembershipRepository membershipRepository;

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

		User savedUser = userRepository.saveAndFlush(user);

		kafkaProducer.sendMessage(KAFKA_TOPIC, KafkaOperation.CREATE,
				ResponseUtils.convertToDto(savedUser, UserDto.class));

		return savedUser;
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
	public ResponseContainer<UserDto> updateUserResponse(UserDto userUpdate) throws ResourceNotFoundException, ImproperResourceSpecification {
		return ResponseUtils.pageToDtoResponseContainer(Collections.singletonList(updateUser(userUpdate)), UserDto.class);
	}

	public Set<User> getAllUsersInWorkspace(UUID uuid) throws ImproperResourceSpecification, ResourceNotFoundException {
		if (Objects.nonNull(uuid)) {
			//Attempt to find workspace or else throw from workspaceService
			workspaceService.findWorkspaceByUuid(uuid);
			return userRepository.findDistinctByMemberships_workspaceUuid(uuid);
		}
		else {
			throw new ImproperResourceSpecification("Must specify UUID of workspace when searching for users in that workspace");
		}
	}

	public ResponseContainer<UserDto> getAllUsersInWorkspaceResponse(UUID uuid) throws ImproperResourceSpecification, ResourceNotFoundException {
		return ResponseUtils.pageToDtoResponseContainer(new ArrayList<>(getAllUsersInWorkspace(uuid)), UserDto.class);
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

		User savedUser = userRepository.saveAndFlush(user);

		kafkaProducer.sendMessage(KAFKA_TOPIC, KafkaOperation.DELETE,
				ResponseUtils.convertToDto(savedUser, UserDto.class));

		return savedUser;
	}

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

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private @Qualifier("excelFileToDatabaseJob") Job job;

	public JobProcessResponse batchUpload(String fileLocation) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, ResourceNotFoundException, IOException, ImproperResourceSpecification, InterruptedException {
		JobParametersBuilder jobBuilder= new JobParametersBuilder();
		jobBuilder.addString("fileLocation", fileLocation);
		jobBuilder.addString("timestamp", OffsetDateTime.now().toString());
		JobParameters jobParameters = jobBuilder.toJobParameters();

		try (Workbook workbook = new XSSFWorkbook(new File(fileLocation))) {
			//File format is correct
			log.trace("Uploaded file format is a valid Excel file");
		}
		catch (NotOfficeXmlFileException | InvalidFormatException e) {
			log.trace("Error thrown while trying to verify file format", e);
			throw new ImproperResourceSpecification("Specified file is not an Excel File");
		}
//		StopWatch stopwatch = new StopWatch();
//		stopwatch.start();
//		new ReportSplitter(fileLocation, 1000);
//		stopwatch.stop();
//		log.info("Splitting report took {}", stopwatch.getTotalTimeSeconds());

/*		ExecutorService es = Executors.newCachedThreadPool();

		for (int i = 0; i < 11; i++) {
			String newFileName = fileLocation.substring(0, fileLocation.length() - 5);
			String splitFile = newFileName + "_" + (i + 1) + ".xlsx";

			JobParametersBuilder jobBuilder= new JobParametersBuilder();
			jobBuilder.addString("fileLocation", splitFile);
			jobBuilder.addString("timestamp", OffsetDateTime.now().toString());
			JobParameters jobParameters = jobBuilder.toJobParameters();


			log.info("Executing task {} for file", i, splitFile);
			*//*  your task *//*
			es.execute(() -> {
				try {
					jobLauncher.run(job, jobParameters);
				} catch (JobExecutionAlreadyRunningException e) {
					e.printStackTrace();
				} catch (JobRestartException e) {
					e.printStackTrace();
				} catch (JobInstanceAlreadyCompleteException e) {
					e.printStackTrace();
				} catch (JobParametersInvalidException e) {
					e.printStackTrace();
				}
			});
			log.info("Finished task request {} for file {}", i, splitFile);
//			if (jobExecution.getStatus().isUnsuccessful()) {
//				throw new ResourceNotFoundException("Failed to complete job"); //TODO: CREATE FAILED JOB EXCEPTION
//			}
		}
		boolean finished = es.awaitTermination(999, TimeUnit.HOURS);*/


		JobExecution jobExecution = jobLauncher.run(job, jobParameters); //TODO: runs synchronously, investigate asynchronous run
		if (jobExecution.getStatus().isUnsuccessful()) {
			throw new ResourceNotFoundException("Failed to complete job"); //TODO: CREATE FAILED JOB EXCEPTION
		}
		log.debug("{} items processed", afterJob(jobExecution));
		return afterJob(jobExecution);
	}

	public JobProcessResponse afterJob(JobExecution jobExecution) {
		int itemsRead = 0;
		int itemsProcessed = 0;
		int itemsSkipped = 0;
		for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
			itemsRead += stepExecution.getReadCount();
			itemsProcessed += stepExecution.getWriteCount();
			itemsSkipped += stepExecution.getFilterCount();
			log.trace("{}", stepExecution.toString());
		}
		JobProcessResponse jobProcessResponse = new JobProcessResponse();
		jobProcessResponse.setRead(itemsRead);
		jobProcessResponse.setProcessed(itemsProcessed);
		jobProcessResponse.setSkipped(itemsSkipped);
		return jobProcessResponse;
	}
}
