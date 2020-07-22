package org.example.todo.accounts.repository;

import org.example.todo.accounts.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

//Suppressing warning for Sonarqube function naming convention as JPA defines rules that don't comply
@SuppressWarnings("squid:S00100")
public interface MembershipRepository extends JpaRepository<Membership, Long> {
	List<Membership> findAllByWorkspace_uuid(UUID uuid);
	List<Membership> findAllByUserUuid(UUID uuid);
	List<Membership> findAllByUserUuidAndWorkspace_uuid(UUID userUuid, UUID workspaceUuid);
}
