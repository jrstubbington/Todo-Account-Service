package org.example.todo.logins;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login, Long> {

	Login findById(long id);

	Login findByUsername(String username);

//	Login findByUser(User user);
}
