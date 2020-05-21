package org.example.todo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;
import org.example.todo.util.Status;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "workspaces")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Workspace implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Type(type="uuid-binary")
	@NaturalId
	@Column(name = "UUID", nullable = false, updatable = false)
	@GeneratedValue(generator = "hibernate-uuid")
	@Builder.Default
	private final UUID uuid = UUID.randomUUID();

	@NotNull
	private String name;

	@NotNull
	private Status status;

	private int workspaceType;

	private OffsetDateTime dateCreated;

	@Singular
	@OneToMany(mappedBy = "account")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private transient Set<Membership> memberships;
}
