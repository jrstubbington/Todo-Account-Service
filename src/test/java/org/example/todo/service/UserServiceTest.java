package org.example.todo.service;

import org.example.todo.dto.UserDto;
import org.example.todo.dto.UserProfileDto;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
	private List<User> userList;

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
		when(userList.get(0)).thenReturn(user);

		when(userRepository.findAll()).thenReturn(userList);
		Assertions.assertEquals(user, userService.getAllUsers().get(0));
	}

	@Test
	void getAllUsersThrowsException() {
		when(userRepository.findAll()).thenThrow(NullPointerException.class);

		Assertions.assertThrows(NullPointerException.class, () -> userService.getAllUsers());
	}

	@Test
	void getAllUsersPaged() {
		PageRequest pageable = PageRequest.of(0, 10);

		when(page.getContent()).thenReturn(userList);
		when(userList.get(0)).thenReturn(user);

		when(userRepository.findAll(isA(PageRequest.class))).thenReturn(page);
		Assertions.assertEquals(user, userService.getAllUsersPaged(pageable).getContent().get(0));
	}

	@Test
	void getAllUsersPagedThrowsException() {
		PageRequest pageable = PageRequest.of(0, 10);

		when(userRepository.findAll(isA(PageRequest.class))).thenThrow(NullPointerException.class);

		Assertions.assertThrows(NullPointerException.class, () -> userService.getAllUsersPaged(pageable));
	}

	@Test
	void findUserByUuid() throws ResourceNotFoundException {
		Optional<User> optionalUser = Optional.of(user);

		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		Assertions.assertEquals(user, userService.findUserByUuid(UUID.randomUUID()));
	}

	@Test
	void findUserByUuidThrowsNotFoundException() {
		Optional<User> optionalUser = Optional.empty();

		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.findUserByUuid(UUID.randomUUID()));
	}

	@Test
	void findUsersByStatus() {
		when(userRepository.findByStatus(isA(Status.class))).thenReturn(userList);
		when(userList.get(0)).thenReturn(user);

		Assertions.assertEquals(user, userService.findUsersByStatus(Status.ACTIVE).get(0));
	}

	@Test
	void createUser() {
		UserDto userDto = new UserDto();
		Assertions.assertThrows(UnsupportedOperationException.class, () -> userService.createUser(userDto));
	}

	@Test
	void createUserThrowsImproperResourceSpecification() {
		UserDto userDto = new UserDto();
		userDto.setUuid(UUID.randomUUID());
		Assertions.assertThrows(ImproperResourceSpecification.class, () -> userService.createUser(userDto));
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
		Assertions.assertEquals(UserProfile.builder()
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
		Assertions.assertThrows(ImproperResourceSpecification.class, () -> userService.updateUser(userDto));
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

		Assertions.assertEquals(workspaces, userService.getAllWorkspacesForUserUuid(UUID.randomUUID()));
	}

	@Test
	void getAllWorkspacesForUserUuidThrowsResourceNotFoundException() {
		Optional<User> optionalUser = Optional.empty();

		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.getAllWorkspacesForUserUuid(UUID.randomUUID()));
	}

	@Test
	void deleteUser() {
		Workspace workspace = new Workspace();
		workspace.setId(1L);

		Membership membership = new Membership();
		membership.setWorkspace(workspace);
		Set<Membership> memberships = new HashSet<>();
		memberships.add(membership);

		Optional<User> optionalUser = Optional.of(user);
		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		when(user.getMemberships()).thenReturn(memberships);

		Assertions.assertDoesNotThrow(() -> userService.deleteUser(UUID.randomUUID()));
	}

	@Test
	void deleteUserThrowsResourceNotFoundException() {
		Optional<User> optionalUser = Optional.empty();

		when(userRepository.findByUuid(isA(UUID.class))).thenReturn(optionalUser);
		Assertions.assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(UUID.randomUUID()));
	}

	@Test
	void deleteUserThrowsException() {

		when(userRepository.findByUuid(isA(UUID.class))).thenThrow(new NullPointerException());
		Assertions.assertThrows(NullPointerException.class, () -> userService.deleteUser(UUID.randomUUID()));
	}
}