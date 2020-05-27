package org.example.todo.repository;

import org.example.todo.model.Workspace;
import org.example.todo.util.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

	Set<Workspace> findByStatus(Status status);

	Optional<Workspace> findByUuid(UUID uuid);
}
