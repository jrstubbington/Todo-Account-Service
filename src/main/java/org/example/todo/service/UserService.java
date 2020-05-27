package org.example.todo.service;

import lombok.extern.slf4j.Slf4j;
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
import org.example.todo.util.ResponseContainer;
import org.example.todo.util.ResponseUtils;
import org.example.todo.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

	private UserRepository userRepository;

	private MembershipRepository membershipRepository;

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

/*	public List<User> findUsersByStatus(Status status) {
		return userRepository.findByStatus(status);
	}*/

	public User createUser(UserDto userCreate) throws ImproperResourceSpecification {
		if (Objects.isNull(userCreate.getUuid())) {
			throw new UnsupportedOperationException("Functionality to create new users has not been implemented");
		}
		else {
			throw new ImproperResourceSpecification("Cannot specify UUID when creating a resource");
		}
	}

	public ResponseContainer<UserDto> createUserResponse(UserDto userCreate) {
		throw new UnsupportedOperationException("Functionality to create new users has not been implemented");

		//return ResponseUtils.pageToDtoResponseContainer(Collections.singletonList(createUser(userCreate)), UserDto.class)
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

	@Transactional
	public ResponseContainer<UserDto> deleteUserResponse(UUID uuid) throws ResourceNotFoundException {
		return ResponseUtils.pageToDtoResponseContainer(Collections.singletonList(deleteUser(uuid)), UserDto.class);
	}

/*	@Transactional
	public ResponseContainer<UserDto> findByLastNameResponse(String lastName) {
		// fetch customers by last name
		return ResponseUtils.pageToDtoResponseContainer(userRepository.findByUserProfile_LastName(lastName), UserDto.class);
	}*/

	@Autowired
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Autowired
	public void setMembershipRepository(MembershipRepository membershipRepository) {
		this.membershipRepository = membershipRepository;
	}
}
