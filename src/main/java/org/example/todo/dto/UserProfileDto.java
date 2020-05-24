package org.example.todo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserProfileDto implements DtoEntity {

	private  String firstName;

	private String lastName;

	private String email;
}
