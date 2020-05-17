package org.example.todo.user;

import org.example.todo.status.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

	List<User> findByLastName(String lastName);

	User findById(long id);

	User findByUuid(UUID uuid);

	List<User> findByStatus(Status status);
}
