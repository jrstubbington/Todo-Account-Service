package org.example.todo.accounts.repository;

import org.example.todo.accounts.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

	Optional<Workspace> findByUuid(UUID uuid);
}
