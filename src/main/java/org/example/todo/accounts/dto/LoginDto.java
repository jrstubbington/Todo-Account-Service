package org.example.todo.accounts.dto;

import lombok.Data;
import lombok.ToString;

@Data
public class LoginDto {
	private String username;

	@ToString.Exclude
	private String plainTextPassword;
}
