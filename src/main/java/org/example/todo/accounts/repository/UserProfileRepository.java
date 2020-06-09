package org.example.todo.accounts.repository;

import org.example.todo.accounts.model.User;
import org.example.todo.accounts.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

}
