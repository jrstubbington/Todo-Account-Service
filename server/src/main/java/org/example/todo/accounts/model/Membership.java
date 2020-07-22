package org.example.todo.accounts.model;

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
import java.util.UUID;

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

	//TODO: Convert to user UUID as users will be moved out of this application
//	@ManyToOne
////	@JoinColumn(name = "user_uuid", referencedColumnName = "uuid")
//	@ToString.Exclude
//	@EqualsAndHashCode.Exclude
//	private User user;

	private UUID userUuid;

	@ManyToOne
	@JoinColumn(name = "workspace_uuid", referencedColumnName = "uuid")
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Workspace workspace;

	private Long roleId;

	private OffsetDateTime dateCreated;
}
