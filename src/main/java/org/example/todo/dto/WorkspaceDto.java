package org.example.todo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.todo.util.Status;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@Data
public class WorkspaceDto implements DtoEntity {

	private  UUID uuid;

	private String name;

	private Status status;

	private int workspaceType;

	private OffsetDateTime dateCreated;
}
