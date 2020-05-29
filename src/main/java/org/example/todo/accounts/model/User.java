package org.example.todo.accounts.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.todo.common.util.Status;
import org.hibernate.annotations.NaturalId;
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
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class User implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Type(type="uuid-binary")
	@NaturalId
	@Column(name = "UUID", nullable = false, updatable = false)
	@GeneratedValue(generator = "hibernate-uuid")
	@NotNull
	private final UUID uuid = UUID.randomUUID();

	@Enumerated(EnumType.STRING)
	@NotNull
	private Status status;

	@OneToOne(fetch = FetchType.LAZY,
			cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "profile_id", referencedColumnName = "id")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private UserProfile userProfile;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
			cascade = CascadeType.ALL)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	private Set<Membership> memberships;

	@Column
	@CreatedDate
	private OffsetDateTime dateCreated;

	@OneToOne(fetch = FetchType.LAZY,
			cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "login_id", referencedColumnName = "id")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	private Login login;
}
