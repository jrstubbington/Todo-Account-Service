package org.example.todo.accounts;

import org.example.todo.status.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface AccountRepository extends JpaRepository<Account, Long> {

	Account findById(long id);

	Set<Account> findByStatus(Status status);

}
