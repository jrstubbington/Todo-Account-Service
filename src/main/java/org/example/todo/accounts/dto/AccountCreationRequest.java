package org.example.todo.accounts.dto;

import lombok.Data;
import org.example.todo.common.dto.LoginDto;
import org.example.todo.common.dto.UserDto;
import org.example.todo.common.dto.WorkspaceDto;

@Data
public class AccountCreationRequest {
	private UserDto user;
	private WorkspaceDto workspace;
	private LoginDto login;
}
