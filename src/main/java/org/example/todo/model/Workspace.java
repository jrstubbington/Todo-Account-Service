package org.example.todo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
	@NotBlank
	@Size(max = 100)
	@Pattern(regexp = "^(?U)[\\p{L}][ \\p{L}'-]*?[\\p{L}]$") //Ensure that only characters from any language are allowed
	private String name;

	@NotNull
	private Status status;

	private int workspaceType;

	private OffsetDateTime dateCreated;

	@OneToMany(mappedBy = "workspace")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	private Set<Membership> memberships;
}
