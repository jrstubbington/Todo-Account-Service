package org.example.todo.accounts.controller;

import org.example.todo.accounts.generated.dto.ResponseContainerUserDto;
import org.example.todo.accounts.generated.dto.ResponseContainerWorkspaceDto;
import org.example.todo.accounts.generated.dto.UserDto;
import org.example.todo.accounts.generated.dto.WorkspaceDto;
import org.example.todo.accounts.model.Workspace;
import org.example.todo.accounts.service.UserService;
import org.example.todo.accounts.service.WorkspaceService;
import org.example.todo.common.exceptions.ImproperResourceSpecification;
import org.example.todo.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkspaceControllerTest {

	@Mock
	private WorkspaceService workspaceService;

	@Mock
	private UserService userService;

	@Mock
	private Page<Workspace> workspacePage;

	@Mock
	private Pageable pageable;

	private WorkspaceController workspaceController;

	@BeforeEach
	private void setup(){
		workspaceController = new WorkspaceController();
		workspaceController.setWorkspaceService(workspaceService);
		workspaceController.setUserService(userService);
	}

	@Test
	void testGetWorkspacesV1() {
		PageRequest pageRequest = PageRequest.of(0, 10);

		Workspace workspace = new Workspace();
		WorkspaceDto workspaceDto = new WorkspaceDto();
		workspaceDto.setUuid(workspace.getUuid());
		workspaceDto.setWorkspaceType(0);

		ResponseContainerWorkspaceDto responseContainer = new ResponseContainerWorkspaceDto();
		responseContainer.setData(Collections.singletonList(workspaceDto));
		ResponseEntity<ResponseContainerWorkspaceDto> workspaceResponse = new ResponseEntity<>(responseContainer, HttpStatus.OK);

		when(workspaceService.getAllWorkspacesResponse(pageRequest)).thenReturn(responseContainer);

		assertEquals(HttpStatus.OK, workspaceController.getWorkspacesV1(0, 10).getStatusCode(),
				"Status code should be OK (200)");
		assertEquals(workspaceResponse, workspaceController.getWorkspacesV1(0, 10),
				"Response should match expected format");
	}

	@Test
	void testGetUserWorkspaces() throws ImproperResourceSpecification, ResourceNotFoundException {
		UserDto userDto = new UserDto();

		ResponseContainerUserDto responseContainer = new ResponseContainerUserDto();
		responseContainer.setSuccess(true);
		responseContainer.setData(Collections.singletonList(new UserDto()));
		ResponseEntity<ResponseContainerUserDto> userResponse = new ResponseEntity<>(responseContainer, HttpStatus.OK);

		when(userService.getAllUsersInWorkspaceResponse(isA(UUID.class))).thenReturn(responseContainer);
		assertEquals(HttpStatus.OK, workspaceController.getUsersInWorkspace(UUID.randomUUID()).getStatusCode(),
				"Status code should be OK (200)");
		assertNotNull(workspaceController.getUsersInWorkspace(UUID.randomUUID()).getBody(),
				"Response body should not be null");
		assertDoesNotThrow(() -> workspaceController.getUsersInWorkspace(UUID.randomUUID()),
				"Controller should not throw exception when getting user workspaces");
	}
}
