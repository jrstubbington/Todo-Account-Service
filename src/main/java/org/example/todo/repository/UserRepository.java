package org.example.todo.repository;

import org.example.todo.model.User;
import org.example.todo.util.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

	List<User> findByLastName(String lastName);

	List<User> findByFirstNameLike(String firstName);

	User findById(long id);

	Optional<User> findByUuid(UUID uuid);

	List<User> findByStatus(Status status);
}
