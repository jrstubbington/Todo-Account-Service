package org.example.todo.repository;

import org.example.todo.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login, Long> {

	Login findById(long id);

	Login findByUsername(String username);
}
