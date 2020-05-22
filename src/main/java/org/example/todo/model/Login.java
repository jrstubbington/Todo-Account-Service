package org.example.todo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name = "logins")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Login implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;

	private String passwordHash;


	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_uuid", nullable = false)
	@ToString.Exclude
	private User user;

	private OffsetDateTime lastSuccessfulLogin;

	private OffsetDateTime lastFailedLogin;
}
