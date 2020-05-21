package org.example.todo.model;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
public class MembershipKey implements Serializable {
	@Column(name = "user_uuid")
	@Type(type="uuid-binary")
	UUID userUuid;

	@Column(name = "workspace_uuid")
	@Type(type="uuid-binary")
	UUID workspaceUuid;
}
