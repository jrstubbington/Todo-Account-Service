package org.example.todo.service;

import org.example.todo.dto.UserDto;
import org.example.todo.dto.UserProfileDto;
import org.example.todo.dto.WorkspaceDto;
import org.example.todo.exception.ImproperResourceSpecification;
import org.example.todo.exception.ResourceNotFoundException;
import org.example.todo.model.Membership;
import org.example.todo.model.User;
import org.example.todo.model.UserProfile;
import org.example.todo.model.Workspace;
import org.example.todo.repository.MembershipRepository;
import org.example.todo.repository.UserRepository;
import org.example.todo.util.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

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
	private User user;

	private UserService userService;

	@BeforeEach
	void setup(){
		userService = new UserService();
		userService.setUserRepository(userRepository);
		userService.setMembershipRepository(membershipRepository);
	}

	@Test
	void getAllUsers() {
		when(mockedUserList.get(0)).thenReturn(user);

		when(userRepository.findAll()).thenReturn(mockedUserList);
		assertEquals(user, userService.getAllUsers().get(0));
	}

	@Test
	void getAllUsersThrowsException() {
		when(userRepository.findAll()).thenThrow(NullPointerException.class);

		assertThrows(NullPointerException.class, () -> userService.getAllUsers());
	}

	@Test
	void getAllUsersResponse() {
		PageRequest pageable = PageRequest.of(0, 10);

		List<User> userList = new ArrayList<>(Collections.singletonList(user));
		when(page.getContent()).thenReturn(userList);
		when(page.getPageable()).thenReturn(pageable);

		when(userRepository.findAll(isA(PageRequest.class))).thenReturn(page);
		assertEquals(new UserDto(), userService.getAllUsersResponse(pageable).getData().get(0));
	}

	@Test
	void getAllUsersResponseThrowsException() {
		PageRequest pageable = PageRequest.of(0, 10);

		when(userRepository.findAll(isA(PageRequest.class))).thenThrow(NullPointerException.class);

		assertThrows(NullPointerException.class, () -> userService.getAllUsersResponse(pageable));
	}

	@Test
	void findUserByUuid() throws ResourceNotFoundException {
		Optional<User> optionalUser = Optional.of(user);

		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		assertEquals(user, userService.findUserByUuid(UUID.randomUUID()));
	}

	@Test
	void findUserByUuidResponse() throws ResourceNotFoundException {
		Optional<User> optionalUser = Optional.of(user);

		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		assertEquals(new UserDto(), userService.findUserByUuidResponse(UUID.randomUUID()).getData().get(0));
	}


	@Test
	void findUserByUuidThrowsNotFoundException() {
		Optional<User> optionalUser = Optional.empty();

		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		assertThrows(ResourceNotFoundException.class, () -> userService.findUserByUuid(UUID.randomUUID()));
	}

/*	@Test
	void findUsersByStatus() {
		when(userRepository.findByStatus(isA(Status.class))).thenReturn(mockedUserList);
		when(mockedUserList.get(0)).thenReturn(user);

		Assertions.assertEquals(user, userService.findUsersByStatus(Status.ACTIVE).get(0));
	}*/

	@Test
	void createUser() {
		UserDto userDto = new UserDto();
		assertThrows(UnsupportedOperationException.class, () -> userService.createUser(userDto));
	}

	@Test
	void createUserThrowsImproperResourceSpecification() {
		UserDto userDto = new UserDto();
		userDto.setUuid(UUID.randomUUID());
		assertThrows(ImproperResourceSpecification.class, () -> userService.createUser(userDto));
	}

	@Test
	void createUserResponse() {
		Optional<User> optionalUser = Optional.of(user);

		UserDto userDto = new UserDto();

		UserProfile userProfile = new UserProfile();
		userProfile.setFirstName("Bob");
		userProfile.setLastName("Smith");
		userProfile.setEmail("bsmith@example.org");

		//Generate input UserDto UserProfile
		UserProfileDto updateProfile = new UserProfileDto();
		updateProfile.setFirstName("Bob");
		updateProfile.setLastName("Smith");
		updateProfile.setEmail("bsmith@example.org");

		//Set generated UserProfile in generated userDto
		userDto.setUserProfile(updateProfile);

/*		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		when(user.getUserProfile()).thenReturn(userProfile);
		when(userRepository.save(isA(User.class))).thenReturn(user);*/

//		assertEquals(updateProfile, userService.createUserResponse(userDto).getData().get(0).getUserProfile());
		assertThrows(UnsupportedOperationException.class, () -> userService.createUserResponse(userDto));
	}

	@Test
	void updateUser() throws ResourceNotFoundException, ImproperResourceSpecification {
		//Generate input UserDto
		UserDto userDto = new UserDto();
		userDto.setUuid(UUID.randomUUID());

		//Generate Fake Return User Profile
		UserProfile userProfile = new UserProfile();
		userProfile.setFirstName("Bob");
		userProfile.setLastName("Smith");
		userProfile.setEmail("bsmith@example.org");

		//Generate input UserDto UserProfile
		UserProfileDto updateProfile = new UserProfileDto();
		updateProfile.setFirstName("Bob");
		updateProfile.setLastName("Smith");
		updateProfile.setEmail("bsmith@example.org");

		//Set generated UserProfile in generated userDto
		userDto.setUserProfile(updateProfile);

		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		when(user.getUserProfile()).thenReturn(userProfile);
		when(userRepository.save(isA(User.class))).thenReturn(user);

		User user = userService.updateUser(userDto);
		assertEquals(UserProfile.builder()
				.firstName("Bob")
				.lastName("Smith")
				.email("bsmith@example.org")
				.build(),
				user.getUserProfile()
		);
	}

	@Test
	void updateUserThrowsImproperResourceSpecification() {
		UserDto userDto = new UserDto();
		assertThrows(ImproperResourceSpecification.class, () -> userService.updateUser(userDto));
	}

	@Test
	void updateUserResponse() throws ResourceNotFoundException, ImproperResourceSpecification {
		Optional<User> optionalUser = Optional.of(user);

		UserDto userDto = new UserDto();
		userDto.setUuid(UUID.randomUUID());

		UserProfile userProfile = new UserProfile();
		userProfile.setFirstName("Bob");
		userProfile.setLastName("Smith");
		userProfile.setEmail("bsmith@example.org");

		//Generate input UserDto UserProfile
		UserProfileDto updateProfile = new UserProfileDto();
		updateProfile.setFirstName("Bob");
		updateProfile.setLastName("Smith");
		updateProfile.setEmail("bsmith@example.org");

		//Set generated UserProfile in generated userDto
		userDto.setUserProfile(updateProfile);

		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		when(user.getUserProfile()).thenReturn(userProfile);
		when(userRepository.save(isA(User.class))).thenReturn(user);

		assertEquals(updateProfile, userService.updateUserResponse(userDto).getData().get(0).getUserProfile());
	}

	@Test
	void getAllWorkspacesForUserUuid() throws ResourceNotFoundException {

		Workspace workspace = new Workspace();
		workspace.setId(1L);

		Membership membership = new Membership();
		membership.setWorkspace(workspace);

		Set<Membership> memberships = new HashSet<>();
		memberships.add(membership);

		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		when(user.getMemberships()).thenReturn(memberships);

		Set<Workspace> workspaces = new HashSet<>();
		workspaces.add(workspace);

		assertEquals(workspaces, userService.getAllWorkspacesForUserUuid(UUID.randomUUID()));
	}

	@Test
	void getAllWorkspacesForUserUuidThrowsResourceNotFoundException() {
		Optional<User> optionalUser = Optional.empty();

		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		assertThrows(ResourceNotFoundException.class, () -> userService.getAllWorkspacesForUserUuid(UUID.randomUUID()));
	}

	@Test
	void getAllWorkspacesForUserUuidResponse() throws ResourceNotFoundException {
		Workspace workspace = new Workspace();
		workspace.setId(1L);

		Membership membership = new Membership();
		membership.setWorkspace(workspace);

		Set<Membership> memberships = new HashSet<>();
		memberships.add(membership);

		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		when(user.getMemberships()).thenReturn(memberships);

		WorkspaceDto workspaceDto = new WorkspaceDto();
		workspaceDto.setUuid(workspace.getUuid());

		List<WorkspaceDto> workspaceList = new ArrayList<>(Collections.singletonList(workspaceDto));

		assertEquals(workspaceList, userService.getAllWorkspacesForUserUuidResponse(UUID.randomUUID()).getData());
	}

	@Test
	void deleteUser() throws ResourceNotFoundException {
		Workspace workspace = new Workspace();
		workspace.setId(1L);

		Membership membership = new Membership();
		membership.setWorkspace(workspace);
		Set<Membership> memberships = new HashSet<>();
		memberships.add(membership);

		User user = new User();
		user.setStatus(Status.ACTIVE);
		user.setMemberships(memberships);

		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		when(userRepository.save(user)).thenReturn(user);

		Assertions.assertDoesNotThrow(() -> userService.deleteUser(UUID.randomUUID()));
		assertEquals(Status.DELETED, userService.deleteUser(UUID.randomUUID()).getStatus());
		Assertions.assertNull(userService.deleteUser(UUID.randomUUID()).getLogin());
		Assertions.assertNull(userService.deleteUser(UUID.randomUUID()).getUserProfile());
		assertEquals(new HashSet<>(), userService.deleteUser(UUID.randomUUID()).getMemberships());
	}

	@Test
	void deleteUserThrowsResourceNotFoundException() {
		Optional<User> optionalUser = Optional.empty();

		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(UUID.randomUUID()));
	}

	@Test
	void deleteUserResponse() throws ResourceNotFoundException {
		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		when(userRepository.save(isA(User.class))).thenReturn(user);

		Assertions.assertNull(userService.deleteUserResponse(UUID.randomUUID()).getData().get(0).getUserProfile());
	}

	@Test
	void deleteUserThrowsException() {

		when(userRepository.findByUuid(isA(UUID.class))).thenThrow(new NullPointerException());
		assertThrows(NullPointerException.class, () -> userService.deleteUser(UUID.randomUUID()));
	}
}