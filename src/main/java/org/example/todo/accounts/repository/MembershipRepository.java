package org.example.todo.accounts.repository;

import org.example.todo.accounts.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
}
