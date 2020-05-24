package org.example.todo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

	@JsonIgnore
	@ToString.Exclude
	private String passwordHash;

	private OffsetDateTime lastSuccessfulLogin;

	private OffsetDateTime lastFailedLogin;
}
