package org.example.todo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.todo.util.Create;
import org.example.todo.util.Status;
import org.example.todo.util.Update;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto implements DtoEntity {

	@Null(groups = Create.class)
	@NotNull(groups = Update.class)
	private UUID uuid = null;

	private Status status;

	private UserProfileDto userProfile;

	@Null(groups = {Create.class, Update.class})
	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	@EqualsAndHashCode.Exclude
	private OffsetDateTime dateCreated;
}
