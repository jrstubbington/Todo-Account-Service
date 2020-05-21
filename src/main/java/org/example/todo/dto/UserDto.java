package org.example.todo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.todo.util.Status;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
//@JsonInclude(value = JsonInclude.Include.NON_NULL)
//@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class UserDto implements DtoEntity {

//	@Null(groups = Create.class)
//	@NotNull(groups = Update.class)
//	@Schema(nullable = true)
	private UUID uuid = null;

	private Status status;

	private  String firstName;

	private String lastName;

	private String email;

//	@Null(groups = {Create.class, Update.class})
	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	private OffsetDateTime dateCreated;
}
