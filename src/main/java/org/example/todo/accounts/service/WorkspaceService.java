package org.example.todo.accounts.service;

import lombok.extern.slf4j.Slf4j;
import org.example.todo.accounts.model.Workspace;
import org.example.todo.accounts.repository.WorkspaceRepository;
import org.example.todo.common.dto.WorkspaceDto;
import org.example.todo.common.exceptions.ImproperResourceSpecification;
import org.example.todo.common.exceptions.ResourceNotFoundException;
import org.example.todo.common.kafka.KafkaOperation;
import org.example.todo.common.kafka.KafkaProducer;
import org.example.todo.common.util.ResponseContainer;
import org.example.todo.common.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class WorkspaceService {

	private static final String KAFKA_TOPIC = "workspaces";

	private KafkaProducer<WorkspaceDto> kafkaProducer;

	private WorkspaceRepository workspaceRepository;

	public Page<Workspace> getAllWorkspaces(PageRequest pageRequest) {
		return workspaceRepository.findAll(pageRequest);
	}

	@Transactional
	public Workspace findWorkspaceByUuid(UUID uuid) throws ResourceNotFoundException {
		return workspaceRepository.findByUuid(uuid).orElseThrow(() -> new ResourceNotFoundException(String.format("Workspace not found with id: %s", uuid)));
	}

	@Transactional
	public ResponseContainer<WorkspaceDto> findWorkspaceByUuidResponse(UUID uuid) throws ResourceNotFoundException {
		return ResponseUtils.pageToDtoResponseContainer(Collections.singletonList(findWorkspaceByUuid(uuid)), WorkspaceDto.class);
	}

	@Transactional
	public Workspace createWorkspace(WorkspaceDto workspaceDto) throws ImproperResourceSpecification {
		if (Objects.isNull(workspaceDto.getUuid())) {
			Workspace workspace = Workspace.builder()
					.name(workspaceDto.getName())
					.status(workspaceDto.getStatus())
					.workspaceType(1) //TODO: change workspaceTypes to mean something
					.memberships(new HashSet<>())
					.build();
			Workspace savedWorkspace = workspaceRepository.saveAndFlush(workspace);
			kafkaProducer.sendMessage(KAFKA_TOPIC, KafkaOperation.CREATE,
					ResponseUtils.convertToDto(savedWorkspace, WorkspaceDto.class));
			return savedWorkspace;

		}
		else {
			throw new ImproperResourceSpecification("Cannot specify a UUID when creating a workspace");
		}
	}

	@Transactional
	public ResponseContainer<WorkspaceDto> createWorkspaceResponse(WorkspaceDto workspaceDto) throws ImproperResourceSpecification {
		return ResponseUtils.pageToDtoResponseContainer(Collections.singletonList(createWorkspace(workspaceDto)), WorkspaceDto.class);
	}

	@Autowired
	public void setWorkspaceRepository(WorkspaceRepository workspaceRepository) {
		this.workspaceRepository = workspaceRepository;
	}

	@Autowired
	public void setKafkaProducer(KafkaProducer<WorkspaceDto> kafkaProducer) {
		this.kafkaProducer = kafkaProducer;
	}
}
