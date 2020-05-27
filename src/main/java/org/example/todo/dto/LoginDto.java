package org.example.todo.dto;

import lombok.Data;
import lombok.ToString;

@Data
public class LoginDto {
	private String username;

	@ToString.Exclude
	private String plainTextPassword;
}
