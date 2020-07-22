package org.example.todo.accounts.security;

import lombok.Data;

import java.util.List;

@Data
public class AuthorizationScope {
	@SuppressWarnings("SpellCheckingInspection")
	String rsname;
	@SuppressWarnings("SpellCheckingInspection")
	String rsid;
	List<String> scopes;
}
