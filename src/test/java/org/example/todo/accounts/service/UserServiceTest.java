package org.example.todo.accounts.service;

import org.example.todo.accounts.dto.AccountCreationRequest;
import org.example.todo.accounts.model.Login;
import org.example.todo.accounts.model.Membership;
import org.example.todo.accounts.model.User;
import org.example.todo.accounts.model.UserProfile;
import org.example.todo.accounts.model.Workspace;
import org.example.todo.accounts.repository.MembershipRepository;
import org.example.todo.accounts.repository.UserRepository;
import org.example.todo.accounts.repository.WorkspaceRepository;
import org.example.todo.common.dto.LoginDto;
import org.example.todo.common.dto.UserDto;
import org.example.todo.common.dto.UserProfileDto;
import org.example.todo.common.dto.WorkspaceDto;
import org.example.todo.common.exceptions.ImproperResourceSpecification;
import org.example.todo.common.exceptions.ResourceNotFoundException;
import org.example.todo.common.kafka.KafkaProducer;
import org.example.todo.common.util.ResponseUtils;
import org.example.todo.common.util.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private MembershipRepository membershipRepository;

	@Mock
	private Page<User> page;

	@Mock
	private List<User> mockedUserList;

	@Mock
	private WorkspaceService workspaceService;

	@Mock
	private WorkspaceRepository workspaceRepository;

	@Mock
	private KafkaProducer<UserDto> kafkaProducer;

	private UserService userService;

	private User user;

	private Workspace workspace;

	@BeforeEach
	void setup(){
		userService = new UserService();
		userService.setUserRepository(userRepository);
		userService.setWorkspaceService(workspaceService);
		userService.setMembershipRepository(membershipRepository);
		userService.setWorkspaceRepository(workspaceRepository);
		userService.setKafkaProducer(kafkaProducer);
		userService.setPasswordEncoder(new BCryptPasswordEncoder());

		workspace = new Workspace();
		workspace.setId(1L);

		Membership membership = new Membership();
		membership.setWorkspace(workspace);

		Set<Membership> memberships = new HashSet<>();
		memberships.add(membership);

		UserProfile userProfile = UserProfile.builder()
				.id(1L)
				.firstName("John")
				.lastName("Doe")
				.email("jdoe@example.org")
				.build();

		Login login = Login.builder()
				.id(1L)
				.username("jdoe")
				.passwordHash(new BCryptPasswordEncoder().encode("mysupersecretpassword"))
				.build();

		user = User.builder()
				.status(Status.ACTIVE)
				.userProfile(userProfile)
				.login(login)
				.memberships(memberships)
				.build();

	}

	@Test
	void testGetAllUsers() {
		when(mockedUserList.get(0)).thenReturn(user);

		when(userRepository.findAll()).thenReturn(mockedUserList);
		assertEquals(user, userService.getAllUsers().get(0),
				"User should have been returned from the list");
	}

	@Test
	void testGetAllUsersResponse() {
		PageRequest pageable = PageRequest.of(0, 10);

		List<User> userList = new ArrayList<>(Collections.singletonList(user));
		when(page.getContent()).thenReturn(userList);
		when(page.getPageable()).thenReturn(pageable);

		when(userRepository.findAll(isA(PageRequest.class))).thenReturn(page);
		assertEquals(ResponseUtils.convertToDto(user, UserDto.class), userService.getAllUsersResponse(pageable).getData().get(0),
				"UserDto should be returned from the list in the response body");
	}

	@Test
	void testFindUserByUuid() throws ResourceNotFoundException {
		Optional<User> optionalUser = Optional.of(user);

		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		assertEquals(user, userService.findUserByUuid(UUID.randomUUID()),
				"User should be returned when found by UUID");
	}

	@Test
	void testFindUserByUuidResponse() throws ResourceNotFoundException {
		Optional<User> optionalUser = Optional.of(user);

		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		assertEquals(ResponseUtils.convertToDto(user, UserDto.class), userService.findUserByUuidResponse(UUID.randomUUID()).getData().get(0),
				"User should be returned via response entity list");
	}

	@Test
	void testFindUserByUuidThrowsNotFoundException() {
		Optional<User> optionalUser = Optional.empty();

		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		assertThrows(ResourceNotFoundException.class, () -> userService.findUserByUuid(UUID.randomUUID()),
				"Resource Not Found Exception should be thrown when an empty optional is returned");
	}

	@Test
	void testCreateUserWithNullUserDto() {
		WorkspaceDto workspaceDto = new WorkspaceDto();
		AccountCreationRequest accountCreationRequest = new AccountCreationRequest();
		accountCreationRequest.setWorkspace(workspaceDto);

		assertThrows(ImproperResourceSpecification.class, () -> userService.createUser(accountCreationRequest),
				"Account Creation Request with no User specification should fail");
	}

	@Test
	void testCreateUserWithNullWorkspaceDto() {
		UserDto userDto = new UserDto();
		AccountCreationRequest accountCreationRequest = new AccountCreationRequest();
		accountCreationRequest.setUser(userDto);

		assertThrows(ImproperResourceSpecification.class, () -> userService.createUser(accountCreationRequest),
				"Account Creation Request with no Workspace specification should fail");
	}

	@Test
	void testCreateUserWithNullLoginDto() {
		UserDto userDto = new UserDto();
		WorkspaceDto workspaceDto = new WorkspaceDto();
		AccountCreationRequest accountCreationRequest = new AccountCreationRequest();
		accountCreationRequest.setUser(userDto);
		accountCreationRequest.setWorkspace(workspaceDto);

		assertThrows(ImproperResourceSpecification.class, () -> userService.createUser(accountCreationRequest),
				"Account Creation Request with no Login specification should fail");
	}

	@Test
	void testCreateUserWithExistingUserAndWorkspace() throws ResourceNotFoundException {
		UserDto userDto = new UserDto();
		userDto.setUuid(UUID.randomUUID());
		WorkspaceDto workspaceDto = new WorkspaceDto();
		workspaceDto.setUuid(UUID.randomUUID());
		AccountCreationRequest accountCreationRequest = new AccountCreationRequest();
		accountCreationRequest.setUser(userDto);
		accountCreationRequest.setWorkspace(workspaceDto);

		Workspace workspace = new Workspace();
		workspace.setMemberships(new HashSet<>());

		User user = User.builder()
				.login(new Login())
				.memberships(new HashSet<>())
				.userProfile(new UserProfile())
				.status(Status.ACTIVE)
				.build();

		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		when(workspaceService.findWorkspaceByUuid(isA(UUID.class))).thenReturn(workspace);
		when(userRepository.saveAndFlush(user)).thenReturn(user);

		Assertions.assertDoesNotThrow(() -> userService.createUser(accountCreationRequest),
				"Account Creation request with existing user and workspace should not fail");
	}

	@Test
	void testCreateUserThrowsImproperResourceSpecification() {
		UserDto userDto = new UserDto();
		userDto.setUuid(UUID.randomUUID());
		AccountCreationRequest accountCreationRequest = new AccountCreationRequest();
		accountCreationRequest.setUser(userDto);
		assertThrows(ImproperResourceSpecification.class, () -> userService.createUser(accountCreationRequest),
				"Requesting user creation while specifying a UUID should return ImproperResourceSpecification");
	}

	@Test
	void testCreateUserResponse() throws ImproperResourceSpecification, ResourceNotFoundException {
		UserDto userDto = new UserDto();

		LoginDto loginDto = new LoginDto();
		loginDto.setUsername("tstark");
		loginDto.setPlainTextPassword("supersecretpassword");

		UserProfile userProfile = UserProfile.builder()
				.firstName("Bob")
				.lastName("Smith")
				.email("bsmith@example.org")
				.build();

		//Generate input UserDto UserProfile
		UserProfileDto updateProfile = new UserProfileDto();
		updateProfile.setFirstName("Bob");
		updateProfile.setLastName("Smith");
		updateProfile.setEmail("bsmith@example.org");

		//Set generated UserProfile in generated userDto
		userDto.setUserProfile(updateProfile);

		WorkspaceDto workspaceDto = new WorkspaceDto();
		workspaceDto.setName("Workspace");
		workspaceDto.setStatus(Status.ACTIVE);

		AccountCreationRequest accountCreationRequest = new AccountCreationRequest();
		accountCreationRequest.setUser(userDto);
		accountCreationRequest.setLogin(loginDto);
		accountCreationRequest.setWorkspace(workspaceDto);

		User user = new User();
		user.setUserProfile(userProfile);


		Workspace workspace = Workspace.builder().name("Workspace").status(Status.ACTIVE).memberships(new HashSet<>()).build();

		when(workspaceService.createWorkspace(isA(WorkspaceDto.class))).thenReturn(workspace);
		when(userRepository.saveAndFlush(isA(User.class))).thenReturn(user);

		assertEquals(updateProfile, userService.createUserResponse(accountCreationRequest).getData().get(0).getUserProfile(),
				"Returned user profile should match passed in object");
		Assertions.assertDoesNotThrow(() -> userService.createUserResponse(accountCreationRequest),
				"Creating a user when specifying all necessary parameters should not throw an exception");
	}

	@Test
	void testUpdateUser() throws ResourceNotFoundException, ImproperResourceSpecification {
		//Generate input UserDto
		UserDto userDto = new UserDto();
		userDto.setUuid(user.getUuid());

		//Generate input UserDto UserProfile

		UserProfile updatedUserProfile = UserProfile.builder()
				.id(1L)
				.firstName("Bob")
				.lastName("Smith")
				.email("jsmith@example.org")
				.build();

		ModelMapper modelMapper = new ModelMapper();
		UserProfileDto updateProfile = modelMapper.map(updatedUserProfile, UserProfileDto.class);

		//Set generated UserProfile in generated userDto
		userDto.setUserProfile(updateProfile);

		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		when(userRepository.saveAndFlush(isA(User.class))).thenReturn(user);

		User user = userService.updateUser(userDto);
		assertEquals(updatedUserProfile, user.getUserProfile(),
				"User profile should be updated with passed in UserDto"
		);
	}

	@Test
	void testUpdateUserThrowsImproperResourceSpecification() {
		UserDto userDto = new UserDto();
		assertThrows(ImproperResourceSpecification.class, () -> userService.updateUser(userDto),
				"User update without UUID should throw ImproperResourceSpecification");
	}

	@Test
	void testUpdateUserResponse() throws ResourceNotFoundException, ImproperResourceSpecification {
		Optional<User> optionalUser = Optional.of(user);

		UserDto userDto = new UserDto();
		userDto.setUuid(UUID.randomUUID());

		//Generate input UserDto UserProfile
		UserProfileDto updateProfile = new UserProfileDto();
		updateProfile.setFirstName("John");
		updateProfile.setLastName("Smith");
		updateProfile.setEmail("jsmith@example.org");

		//Set generated UserProfile in generated userDto
		userDto.setUserProfile(updateProfile);

		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		when(userRepository.saveAndFlush(isA(User.class))).thenReturn(user);

		assertEquals(updateProfile, userService.updateUserResponse(userDto).getData().get(0).getUserProfile(),
				"A returned user's profile should be in the correct object body");
	}

	@Test
	void testGetAllWorkspacesForUserUuid() throws ResourceNotFoundException {
		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);

		Set<Workspace> workspaces = new HashSet<>();
		workspaces.add(workspace);

		assertEquals(workspaces, userService.getAllWorkspacesForUserUuid(UUID.randomUUID()),
				"Workspaces returned for user should match user's workspace set");
	}

	@Test
	void testGetAllWorkspacesForUserUuidThrowsResourceNotFoundException() {
		Optional<User> optionalUser = Optional.empty();

		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		assertThrows(ResourceNotFoundException.class, () -> userService.getAllWorkspacesForUserUuid(UUID.randomUUID()),
				"Getting a set of workspaces for a user that doesn't exist should throw ResourceNotFoundException");
	}

	@Test
	void testGetAllWorkspacesForUserUuidResponse() throws ResourceNotFoundException {
		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);

		WorkspaceDto workspaceDto = new WorkspaceDto();
		workspaceDto.setUuid(workspace.getUuid());

		List<WorkspaceDto> workspaceList = new ArrayList<>(Collections.singletonList(workspaceDto));

		assertEquals(workspaceList, userService.getAllWorkspacesForUserUuidResponse(UUID.randomUUID()).getData(),
				"List of workspaces should match and be returned from a response entity");
	}

	@Test
	void testDeleteUser() throws ResourceNotFoundException {
		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		when(userRepository.saveAndFlush(user)).thenReturn(user);

		Assertions.assertDoesNotThrow(() -> userService.deleteUser(UUID.randomUUID()),
				"Deleting a User should not throw exceptions");
		assertEquals(Status.DELETED, userService.deleteUser(UUID.randomUUID()).getStatus(),
				"The returned user object should have a DELETED status");
		Assertions.assertNull(userService.deleteUser(UUID.randomUUID()).getLogin(),
				"The user's login information should have been removed");
		Assertions.assertNull(userService.deleteUser(UUID.randomUUID()).getUserProfile(),
				"The user's profile information should have been deleted");
		assertEquals(new HashSet<>(), userService.deleteUser(UUID.randomUUID()).getMemberships(),
				"The user's memberships should have been deleted");
	}

	@Test
	void testDeleteUserThrowsResourceNotFoundException() {
		Optional<User> optionalUser = Optional.empty();

		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(UUID.randomUUID()),
				"Deleting a user that doesn't exist should throw ResourceNotFoundException");
	}

	@Test
	void testDeleteUserResponse() throws ResourceNotFoundException {
		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		when(userRepository.saveAndFlush(isA(User.class))).thenReturn(user);

		Assertions.assertNull(userService.deleteUserResponse(UUID.randomUUID()).getData().get(0).getUserProfile(),
				"Deleting a user should return data in the correct format");
	}
}