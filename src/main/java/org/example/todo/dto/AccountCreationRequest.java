package org.example.todo.dto;

import lombok.Data;

@Data
public class AccountCreationRequest {
	private UserDto user;
	private WorkspaceDto workspace;
	private LoginDto login;
}