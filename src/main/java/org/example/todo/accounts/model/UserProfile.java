package org.example.todo.accounts.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "user_profiles")
@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@NotNull
	@Size(max = 50)
	@Pattern(regexp = "^(?U)[\\p{L}][ \\p{L}'-]*?[\\p{L}]$") //Ensure that only characters from any language are allowed
	private  String firstName;

	@NotBlank
	@NotNull
	@Size(max = 50)
	@Pattern(regexp = "^(?U)[\\p{L}][ \\p{L}'-]*?[\\p{L}]$") //Ensure that only characters from any language are allowed
	private String lastName;

	@NotNull
	@Size(max = 254) //Theoretical max email length is 320 characters, but in practice should only be a max of 254
	@Column(unique = true)
	@Email
	private String email;
}