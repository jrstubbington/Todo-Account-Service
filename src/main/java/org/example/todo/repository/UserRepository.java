package org.example.todo.repository;

import org.example.todo.model.User;
import org.example.todo.util.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//Suppressing warning for Sonarqube function naming convention as JPA defines rules that don't comply
@SuppressWarnings("squid:S00100")
public interface UserRepository extends JpaRepository<User, Long> {
	List<User> findByUserProfile_LastName(String lastName);

	User findById(long id);

	Optional<User> findByUuid(UUID uuid);

	List<User> findByStatus(Status status);
}
