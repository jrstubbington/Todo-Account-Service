package org.example.todo.accounts.security;

import lombok.Setter;

@Setter
public class AuthorizationDecision {

	private boolean result;

	public boolean getResult() {
		return result;
	}
}
