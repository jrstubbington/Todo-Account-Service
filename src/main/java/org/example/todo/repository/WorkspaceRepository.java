package org.example.todo.repository;

import org.example.todo.model.Workspace;
import org.example.todo.util.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

	Workspace findById(long id);

	Set<Workspace> findByStatus(Status status);

}
