package org.example.todo.memberships;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class MembershipKey implements Serializable {
	@Column(name = "user_id")
	Long userId;

	@Column(name = "account_id")
	Long accountId;
}
