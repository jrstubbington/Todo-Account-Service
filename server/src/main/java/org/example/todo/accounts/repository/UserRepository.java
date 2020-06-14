package org.example.todo.accounts.repository;

import org.example.todo.accounts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

//Suppressing warning for Sonarqube function naming convention as JPA defines rules that don't comply
@SuppressWarnings("squid:S00100")
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	List<User> findByUserProfile_LastName(String lastName);

	User findById(long id);

	Optional<User> findByUuid(UUID uuid);

	Set<User> findDistinctByMemberships_workspaceUuid(UUID uuid);

	Optional<User> findByUserProfile_Email(String email);

	boolean existsByUserProfile_Email(String email);

}
