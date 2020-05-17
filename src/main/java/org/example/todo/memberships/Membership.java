package org.example.todo.memberships;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.todo.accounts.Account;
import org.example.todo.user.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "memberships")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Membership {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private User user;

	@ManyToOne
	@JoinColumn(name = "account_id")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Account account;

	private Long roleId;

	private OffsetDateTime dateCreated;
}
