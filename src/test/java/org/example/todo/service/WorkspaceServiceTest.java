package org.example.todo.service;

import org.example.todo.model.Workspace;
import org.example.todo.repository.WorkspaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkspaceServiceTest {

	@Mock
	private WorkspaceRepository workspaceRepository;

	private WorkspaceService workspaceService;

	@Mock
	private Page<Workspace> page;

	@BeforeEach
	private void setup(){
		workspaceService = new WorkspaceService();
		workspaceService.setWorkspaceRepository(workspaceRepository);
	}

	@Test
	void getAllWorkspaces() {
		PageRequest pageable = PageRequest.of(0, 10);

		Workspace workspace = new Workspace();

		List<Workspace> userList = new ArrayList<>(Collections.singletonList(workspace));
		when(page.getContent()).thenReturn(userList);

		when(workspaceRepository.findAll(isA(PageRequest.class))).thenReturn(page);
		assertEquals(workspace, workspaceService.getAllWorkspaces(pageable).getContent().get(0));
	}
}
