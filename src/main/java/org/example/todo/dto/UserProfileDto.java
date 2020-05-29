package org.example.todo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

//This class doesn't extend DtoEntity as it _should_ never be returned by itself, always as part of the user object
// maybe the DtoEntity should be named ReturnableDtoEntity
@Data
@NoArgsConstructor
public class UserProfileDto {

	private  String firstName;

	private String lastName;

	private String email;
}
