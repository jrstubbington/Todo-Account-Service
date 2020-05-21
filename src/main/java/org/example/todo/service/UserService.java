package org.example.todo.service;

import lombok.extern.slf4j.Slf4j;
import org.example.todo.dto.UserDto;
import org.example.todo.exception.ImproperResourceSpecification;
import org.example.todo.exception.ResourceNotFoundException;
import org.example.todo.model.Membership;
import org.example.todo.model.User;
import org.example.todo.model.Workspace;
import org.example.todo.repository.UserRepository;
import org.example.todo.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public Page<User> getAllUsers(PageRequest pageRequest) {
		try {
			return userRepository.findAll(pageRequest);
		}
		catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}

	public User findUserByUuid(UUID uuid) throws ResourceNotFoundException {
		return userRepository.findByUuid(uuid).orElseThrow(() -> new ResourceNotFoundException(String.format("User not found with id: %s", uuid)));
	}

	public List<User> findUsersByStatus(Status status) {
		return userRepository.findByStatus(status);
	}

	public User createUser(UserDto userCreate) throws ImproperResourceSpecification {
		if (Objects.isNull(userCreate.getUuid())) {
			throw new UnsupportedOperationException("Functionality to create new users has not been implemented");

		}
		else {
			throw new ImproperResourceSpecification("Cannot specify UUID when creating a resource");
		}
	}

	public User updateUser(UserDto userUpdate) throws ResourceNotFoundException, ImproperResourceSpecification {
		if (Objects.nonNull(userUpdate.getUuid())) {
			// User is being updated
			log.debug("Updating User {}", userUpdate);
			User user = findUserByUuid(userUpdate.getUuid());
			user.setFirstName(userUpdate.getFirstName());
			user.setLastName(userUpdate.getLastName());
			user.setEmail(userUpdate.getEmail());
			user.setStatus(userUpdate.getStatus());
			return userRepository.save(user);
		}
		else {
			throw new ImproperResourceSpecification("Must specify a UUID when updating a resource");
		}
	}

	@Transactional(readOnly = true)
	public Set<Workspace> getAllWorkspacesForUser(UserDto userDto) throws ResourceNotFoundException {
		User user = findUserByUuid(userDto.getUuid());
		Set<Membership> memberships = user.getMemberships();
		return memberships.stream().map(Membership::getWorkspace).collect(Collectors.toSet());
	}

	@Transactional(readOnly = true)
	public Set<Workspace> getAllWorkspacesForUserUuid(UUID uuid) throws ResourceNotFoundException {
		try {
			User user = findUserByUuid(uuid);
			Set<Membership> memberships = user.getMemberships();
			log.trace("MEMSHIPS: {}", memberships);
			return memberships.stream().map(Membership::getWorkspace).collect(Collectors.toSet());
		}
		catch (Exception e) {
			log.error("{}", e);
			throw e;
		}
	}
}
