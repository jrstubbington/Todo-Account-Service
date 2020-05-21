package org.example.todo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name = "memberships")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Membership implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_uuid", referencedColumnName = "uuid")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private User user;

	@ManyToOne
	@JoinColumn(name = "workspace_uuid", referencedColumnName = "uuid")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Workspace workspace;

	private Long roleId;

	private OffsetDateTime dateCreated;
}
