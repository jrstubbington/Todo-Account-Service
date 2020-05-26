package org.example.todo.controller;

import org.example.todo.dto.UserDto;
import org.example.todo.dto.WorkspaceDto;
import org.example.todo.exception.ImproperResourceSpecification;
import org.example.todo.exception.ResourceNotFoundException;
import org.example.todo.service.UserService;
import org.example.todo.util.ResponseContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	@Mock
	private UserService userService;

	private UserController userController;

	@BeforeEach
	private void setup() {
		userController = new UserController();
		userController.setUserService(userService);
	}

	@Test
	void getUsersV1() {
		PageRequest pageRequest = PageRequest.of(0, 10);

		ResponseContainer<UserDto> responseContainer = new ResponseContainer<>(true, null, Collections.singletonList(new UserDto()));
		ResponseEntity<ResponseContainer<UserDto>> userResponse = new ResponseEntity<>(responseContainer, HttpStatus.OK);

		when(userService.getAllUsersResponse(pageRequest)).thenReturn(responseContainer);

		assertEquals(HttpStatus.OK, userController.getUsersV1(0, 10).getStatusCode());
		assertEquals(userResponse, userController.getUsersV1(0, 10));
	}

	@Test
	void getUserByUUID() throws ResourceNotFoundException {
		ResponseContainer<UserDto> responseContainer = new ResponseContainer<>(true, null, Collections.singletonList(new UserDto()));
		ResponseEntity<ResponseContainer<UserDto>> userResponse = new ResponseEntity<>(responseContainer, HttpStatus.OK);

		when(userService.findUserByUuidResponse(isA(UUID.class))).thenReturn(responseContainer);

		assertEquals(HttpStatus.OK, userController.getUserByUUID(UUID.randomUUID()).getStatusCode());
		assertEquals(userResponse, userController.getUserByUUID(UUID.randomUUID()));
	}

	@Test
	void updateUser() throws ResourceNotFoundException, ImproperResourceSpecification {
		UserDto userDto = new UserDto();
		ResponseContainer<UserDto> responseContainer = new ResponseContainer<>(true, null, Collections.singletonList(userDto));
		ResponseEntity<ResponseContainer<UserDto>> userResponse = new ResponseEntity<>(responseContainer, HttpStatus.OK);

		when(userService.updateUserResponse(isA(UserDto.class))).thenReturn(responseContainer);

		assertEquals(HttpStatus.OK, userController.updateUser(userDto).getStatusCode());
		assertEquals(userResponse, userController.updateUser(userDto));
	}

	@Test
	void createUser() throws ImproperResourceSpecification, URISyntaxException {
		UserDto userDto = new UserDto();
		ResponseContainer<UserDto> responseContainer = new ResponseContainer<>(true, null, Collections.singletonList(userDto));
		ResponseEntity<ResponseContainer<UserDto>> userResponse = ResponseEntity.created(new URI("")).body(responseContainer);

		when(userService.createUserResponse(isA(UserDto.class))).thenReturn(responseContainer);

		assertEquals(HttpStatus.CREATED, userController.createUser(userDto).getStatusCode());
		assertEquals(userResponse, userController.createUser(userDto));
	}

	@Test
	void getUserWorkspaces() throws ResourceNotFoundException {
		ResponseContainer<WorkspaceDto> responseContainer = new ResponseContainer<>(true, null, Collections.singletonList(new WorkspaceDto()));
		ResponseEntity<ResponseContainer<WorkspaceDto>> userResponse = new ResponseEntity<>(responseContainer, HttpStatus.OK);

		when(userService.getAllWorkspacesForUserUuidResponse(isA(UUID.class))).thenReturn(responseContainer);

		assertEquals(HttpStatus.OK, userController.getUserWorkspaces(UUID.randomUUID()).getStatusCode());
		assertEquals(userResponse, userController.getUserWorkspaces(UUID.randomUUID()));
	}

	@Test
	void deleteUser() throws ResourceNotFoundException {
		UserDto userDto = new UserDto();
		ResponseContainer<UserDto> responseContainer = new ResponseContainer<>(true, null, Collections.singletonList(userDto));
		ResponseEntity<ResponseContainer<UserDto>> userResponse = new ResponseEntity<>(responseContainer, HttpStatus.OK);

		when(userService.deleteUserResponse(isA(UUID.class))).thenReturn(responseContainer);

		assertEquals(HttpStatus.OK, userController.deleteUser(UUID.randomUUID()).getStatusCode());
		assertEquals(userResponse, userController.deleteUser(UUID.randomUUID()));

	}

/*	@Test
	void getByLastName() {
	}*/
}