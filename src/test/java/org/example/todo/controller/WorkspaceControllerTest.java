package org.example.todo.controller;

import org.example.todo.dto.WorkspaceDto;
import org.example.todo.model.Workspace;
import org.example.todo.service.WorkspaceService;
import org.example.todo.util.ResponseContainer;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkspaceControllerTest {

	@Mock
	private WorkspaceService workspaceService;

	@Mock
	private Page<Workspace> workspacePage;

	@Mock
	private Pageable pageable;

	private WorkspaceController workspaceController;

	@BeforeEach
	private void setup(){
		workspaceController = new WorkspaceController();
		workspaceController.setWorkspaceService(workspaceService);
	}

	@Test
	void testGetWorkspacesV1() {
		PageRequest pageRequest = PageRequest.of(0, 10);

		Workspace workspace = new Workspace();
		WorkspaceDto workspaceDto = new WorkspaceDto();
		workspaceDto.setUuid(workspace.getUuid());

		ResponseContainer<WorkspaceDto> responseContainer = new ResponseContainer<>(true, null, Collections.singletonList(workspaceDto));
		ResponseEntity<ResponseContainer<WorkspaceDto>> workspaceResponse = new ResponseEntity<>(responseContainer, HttpStatus.OK);

		when(workspaceService.getAllWorkspaces(pageRequest)).thenReturn(workspacePage);
		when(workspacePage.getContent()).thenReturn(Collections.singletonList(workspace));
		when(workspacePage.getPageable()).thenReturn(pageable);
		when(workspacePage.getTotalPages()).thenReturn(1);
		when(workspacePage.getTotalElements()).thenReturn(1L);
		when(workspacePage.isLast()).thenReturn(true);
		when(pageable.getPageNumber()).thenReturn(0);

		assertEquals(HttpStatus.OK, workspaceController.getWorkspacesV1(0, 10).getStatusCode(),
				"Status code should be OK (200)");
		assertEquals(workspaceResponse, workspaceController.getWorkspacesV1(0, 10),
				"Response should match expected format");
	}
}
