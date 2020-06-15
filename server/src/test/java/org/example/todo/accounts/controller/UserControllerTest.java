package org.example.todo.accounts.controller;

import org.example.todo.accounts.generated.dto.AccountCreationRequest;
import org.example.todo.accounts.generated.dto.ResponseContainerUserDto;
import org.example.todo.accounts.generated.dto.ResponseContainerWorkspaceDto;
import org.example.todo.accounts.generated.dto.UserDto;
import org.example.todo.accounts.generated.dto.WorkspaceDto;
import org.example.todo.accounts.service.UserService;
import org.example.todo.common.exceptions.ImproperResourceSpecification;
import org.example.todo.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
	void testGetUsersV1() {
		PageRequest pageRequest = PageRequest.of(0, 10);

//		ResponseContainer<UserDto> responseContainer = new ResponseContainer<>(true, null, Collections.singletonList(new UserDto()));
		ResponseContainerUserDto responseContainer = new ResponseContainerUserDto();
		responseContainer.setSuccess(true);
		responseContainer.setData(Collections.singletonList(new UserDto()));
		ResponseEntity<ResponseContainerUserDto> userResponse = new ResponseEntity<>(responseContainer, HttpStatus.OK);

		when(userService.getAllUsersResponse(pageRequest)).thenReturn(responseContainer);

		assertEquals(HttpStatus.OK, userController.getUsersV1(0, 10).getStatusCode(),
				"Status code should be OK (200)");
		assertEquals(userResponse, userController.getUsersV1(0, 10),
				"Response should match expected format");
	}

	@Test
	void testGetUserByUUID() throws ResourceNotFoundException {
//		ResponseContainer<UserDto> responseContainer = new ResponseContainer<>(true, null, Collections.singletonList(new UserDto()));
		ResponseContainerUserDto responseContainer = new ResponseContainerUserDto();
		responseContainer.setSuccess(true);
		responseContainer.setData(Collections.singletonList(new UserDto()));
		ResponseEntity<ResponseContainerUserDto> userResponse = new ResponseEntity<>(responseContainer, HttpStatus.OK);

		when(userService.findUserByUuidResponse(isA(UUID.class))).thenReturn(responseContainer);

		assertEquals(HttpStatus.OK, userController.getUserByUUID(UUID.randomUUID()).getStatusCode(),
				"Status code should be OK (200)");
		assertEquals(userResponse, userController.getUserByUUID(UUID.randomUUID()),
				"Response should match expected format");
	}

	@Test
	void testUpdateUser() throws ResourceNotFoundException, ImproperResourceSpecification {
		UserDto userDto = new UserDto();
		ResponseContainerUserDto responseContainer = new ResponseContainerUserDto();
		responseContainer.setSuccess(true);
		responseContainer.setData(Collections.singletonList(new UserDto()));
		ResponseEntity<ResponseContainerUserDto> userResponse = new ResponseEntity<>(responseContainer, HttpStatus.OK);

		when(userService.updateUserResponse(isA(UserDto.class))).thenReturn(responseContainer);

		assertEquals(HttpStatus.OK, userController.updateUser(userDto).getStatusCode(),
				"Status code should be OK (200)");
		assertEquals(userResponse, userController.updateUser(userDto),
				"Response should match expected format");
	}

	@Test
	void testCreateUser() throws ImproperResourceSpecification, ResourceNotFoundException {
		UserDto userDto = new UserDto();
		WorkspaceDto workspaceDto = new WorkspaceDto();

		AccountCreationRequest accountCreationRequest = new AccountCreationRequest();
		accountCreationRequest.setUser(userDto);
		accountCreationRequest.setWorkspace(workspaceDto);

		ResponseContainerUserDto responseContainer = new ResponseContainerUserDto();
		responseContainer.setSuccess(true);
		responseContainer.setData(Collections.singletonList(new UserDto()));
		ResponseEntity<ResponseContainerUserDto> userResponse = new ResponseEntity<>(responseContainer, HttpStatus.OK);

		when(userService.createUserResponse(isA(AccountCreationRequest.class))).thenReturn(responseContainer);

		assertEquals(HttpStatus.OK, userController.createUser(accountCreationRequest).getStatusCode(),
				"Status code should be OK (200)");
		assertEquals(userResponse, userController.createUser(accountCreationRequest),
				"Response should match expected format");
	}

	@Test
	void testGetUserWorkspaces() throws ResourceNotFoundException {
		ResponseContainerWorkspaceDto responseContainer = new ResponseContainerWorkspaceDto();
		responseContainer.setSuccess(true);
		responseContainer.setData(Collections.singletonList(new WorkspaceDto()));
		ResponseEntity<ResponseContainerWorkspaceDto> userResponse = new ResponseEntity<>(responseContainer, HttpStatus.OK);


		when(userService.getAllWorkspacesForUserUuidResponse(isA(UUID.class))).thenReturn(responseContainer);

		assertEquals(HttpStatus.OK, userController.getUserWorkspaces(UUID.randomUUID()).getStatusCode(),
				"Status code should be OK (200)");
		assertEquals(userResponse, userController.getUserWorkspaces(UUID.randomUUID()),
				"Response should match expected format");
	}

	@Test
	void testDeleteUser() throws ResourceNotFoundException {
		UserDto userDto = new UserDto();
		ResponseContainerUserDto responseContainer = new ResponseContainerUserDto();
		responseContainer.setSuccess(true);
		responseContainer.setData(Collections.singletonList(new UserDto()));
		ResponseEntity<ResponseContainerUserDto> userResponse = new ResponseEntity<>(responseContainer, HttpStatus.OK);

		when(userService.deleteUserResponse(isA(UUID.class))).thenReturn(responseContainer);

		assertEquals(HttpStatus.OK, userController.deleteUser(UUID.randomUUID()).getStatusCode(),
				"Status code should be OK (200)");
		assertEquals(userResponse, userController.deleteUser(UUID.randomUUID()),
				"Response should match expected format");

	}
}
