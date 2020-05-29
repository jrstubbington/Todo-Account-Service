package org.example.todo.accounts.service;

import org.example.todo.accounts.exception.ResourceNotFoundException;
import org.example.todo.accounts.model.Workspace;
import org.example.todo.accounts.repository.WorkspaceRepository;
import org.example.todo.accounts.dto.WorkspaceDto;
import org.example.todo.accounts.exception.ImproperResourceSpecification;
import org.example.todo.accounts.util.Status;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
	void testGetAllWorkspaces() {
		PageRequest pageable = PageRequest.of(0, 10);

		Workspace workspace = new Workspace();

		List<Workspace> workspaceList = new ArrayList<>(Collections.singletonList(workspace));
		when(page.getContent()).thenReturn(workspaceList);

		when(workspaceRepository.findAll(isA(PageRequest.class))).thenReturn(page);
		assertEquals(workspace, workspaceService.getAllWorkspaces(pageable).getContent().get(0),
				"Correct workspaces should be returned and in the correct format");
	}

	@Test
	void testFindWorkspaceByUuid() throws ResourceNotFoundException {
		Workspace workspace = new Workspace();
		Optional<Workspace> optionalWorkspace = Optional.of(workspace);

		when(workspaceRepository.findByUuid(isA(UUID.class))).thenReturn(optionalWorkspace);
		assertEquals(workspace, workspaceService.findWorkspaceByUuid(UUID.randomUUID()),
				"Workspace should be returned when found by UUID");
	}

	@Test
	void testFindWorkspaceByUuidResponse() throws ResourceNotFoundException {
		Workspace workspace = new Workspace();

		WorkspaceDto workspaceDto = new WorkspaceDto();
		workspaceDto.setUuid(workspace.getUuid());

		Optional<Workspace> optionalWorkspace = Optional.of(workspace);

		when(workspaceRepository.findByUuid(isA(UUID.class))).thenReturn(optionalWorkspace);
		assertEquals(workspaceDto, workspaceService.findWorkspaceByUuidResponse(UUID.randomUUID()).getData().get(0),
				"Workspace should be returned via response entity list");
	}

	@Test
	void testFindWorkspaceByUuidThrowsNotFoundException() {
		Optional<Workspace> optionalWorkspace = Optional.empty();

		when(workspaceRepository.findByUuid(isA(UUID.class))).thenReturn(optionalWorkspace);
		assertThrows(ResourceNotFoundException.class, () -> workspaceService.findWorkspaceByUuid(UUID.randomUUID()),
				"Resource Not Found Exception should be thrown when an empty optional is returned");
	}

	@Test
	void testCreateWorkspace() throws ImproperResourceSpecification {
		WorkspaceDto workspaceDto = new WorkspaceDto();
		workspaceDto.setName("Workspace");
		workspaceDto.setStatus(Status.ACTIVE);
		workspaceDto.setWorkspaceType(1);

		Workspace workspace = workspaceService.createWorkspace(workspaceDto);
		assertEquals(workspaceDto.getName(), workspace.getName(),
				"Name value should transfer to new object");
		assertEquals(workspaceDto.getStatus(), workspace.getStatus(),
				"Status value should transfer to new object");
		assertEquals(workspaceDto.getWorkspaceType(), workspace.getWorkspaceType(),
				"Workspace Type value should transfer to new object");
	}

	@Test
	void testCreateWorkspaceThrowsImproperResourceSpecification() {
		UUID uuid = UUID.randomUUID();
		WorkspaceDto workspaceDto = new WorkspaceDto();
		workspaceDto.setName("Workspace");
		workspaceDto.setUuid(uuid);
		workspaceDto.setStatus(Status.ACTIVE);
		workspaceDto.setWorkspaceType(1);

		assertThrows(ImproperResourceSpecification.class, () -> workspaceService.createWorkspace(workspaceDto),
				"An ImproperResourceSpecification should be thrown when specifying a UUID creating a new workspace");
	}
}
