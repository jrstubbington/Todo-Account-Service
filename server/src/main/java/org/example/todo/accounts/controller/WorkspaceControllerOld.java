package org.example.todo.accounts.controller;
/*
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.todo.accounts.dto.UserDto;
import org.example.todo.accounts.dto.WorkspaceDto;
import org.example.todo.accounts.service.UserService;
import org.example.todo.accounts.service.WorkspaceService;
import org.example.todo.common.exceptions.ErrorDetails;
import org.example.todo.common.exceptions.ImproperResourceSpecification;
import org.example.todo.common.exceptions.ResourceNotFoundException;
import org.example.todo.common.util.ResponseContainer;
import org.example.todo.common.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/workspaces")
@Tag(name = "Workspace Management", description = "A collection of APIs designed to handle functions related to workspace management")
@Validated
public class WorkspaceController {

	private WorkspaceService workspaceService;

	private UserService userService;

	@Operation(summary = "View a list of available workspaces")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "500", description = "Internal error has occurred", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
	})
	@GetMapping(value = "/", produces={"application/json"})
	public ResponseEntity<ResponseContainer<WorkspaceDto>> getWorkspacesV1(
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
		ResponseContainer<WorkspaceDto> responseContainer = ResponseUtils.pageToDtoResponseContainer(workspaceService.getAllWorkspaces(PageRequest.of(page, pageSize)), WorkspaceDto.class);
		return ResponseEntity.ok(responseContainer);
	}

	@Operation(summary = "Get a list of users belonging to a workspace")
	@ApiResponses(value = {
			@ApiResponse(responseCode  = "200", description = "OK"),
			@ApiResponse(responseCode  = "400", description = "Client Error", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
			@ApiResponse(responseCode  = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
	})
	@GetMapping(value = "/{uuid}/users", produces={"application/json"})
	public ResponseEntity<ResponseContainer<UserDto>> getUsersInWorkspace(
			@Parameter(description = "UUID of workspace to get all users for") @PathVariable UUID uuid) throws ImproperResourceSpecification, ResourceNotFoundException {
		return ResponseEntity.ok(userService.getAllUsersInWorkspaceResponse(uuid));
	}

	@Operation(summary = "Get a specific workspace's information")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode  = "500", description = "Internal error has occurred", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
	})
	@GetMapping(value = "/{uuid}", produces={"application/json"})
	public ResponseEntity<ResponseContainer<WorkspaceDto>> getWorkspaceByUUID(
			@Parameter(description ="Workspace uuid to get workspace object with") @PathVariable UUID uuid) throws ResourceNotFoundException {
		return ResponseEntity.ok(workspaceService.findWorkspaceByUuidResponse(uuid));
	}

	@Autowired
	public void setWorkspaceService(WorkspaceService workspaceService) {
		this.workspaceService = workspaceService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
*/
