package org.example.todo.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.todo.logins.Login;
import org.example.todo.memberships.Membership;
import org.example.todo.status.Status;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Type(type="uuid-binary")
	@Column(name = "UUID", nullable = false, updatable = false)
	@GeneratedValue(generator = "hibernate-uuid")
	private final UUID uuid = UUID.randomUUID();

	@Enumerated(EnumType.STRING)
	private Status status;

	@NotBlank
	@Size(max = 50)
	private  String firstName;

	@NotBlank
	@Size(max = 50)
	private String lastName;

	@NotNull
	@Size(max = 100)
	@Column(unique = true)
	@Email
	private String email;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
			cascade = CascadeType.ALL)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	private Set<Membership> memberships;

	@Column
	@Generated
	@CreatedDate
	private OffsetDateTime dateCreated;

	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY,
			cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	private Login login;

	/*@PrePersist
	protected void prePersist() {
		if (this.dateCreated == null) {
			dateCreated = OffsetDateTime.now(ZoneOffset.UTC);
		}
		//if (this.dataChangeLastModifiedTime == null) dataChangeLastModifiedTime = new Date();
	}

	@PreUpdate
	protected void preUpdate() {
		this.dataChangeLastModifiedTime = new Date();
	}

	@PreRemove
	protected void preRemove() {
		this.dataChangeLastModifiedTime = new Date();
	}*/
}
