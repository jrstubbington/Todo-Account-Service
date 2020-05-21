package org.example.todo.service;

import lombok.extern.slf4j.Slf4j;
import org.example.todo.model.Workspace;
import org.example.todo.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WorkspaceService {

	@Autowired
	private WorkspaceRepository workspaceRepository;

	public Page<Workspace> getAllWorkspaces(PageRequest pageRequest) {
		return workspaceRepository.findAll(pageRequest);
	}


}
