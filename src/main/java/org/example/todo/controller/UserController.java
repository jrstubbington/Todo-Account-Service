package org.example.todo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.todo.dto.UserDto;
import org.example.todo.dto.WorkspaceDto;
import org.example.todo.exception.ErrorDetails;
import org.example.todo.exception.ImproperResourceSpecification;
import org.example.todo.exception.ResourceNotFoundException;
import org.example.todo.model.Workspace;
import org.example.todo.service.UserService;
import org.example.todo.util.Create;
import org.example.todo.util.ResponseContainer;
import org.example.todo.util.ResponseUtils;
import org.example.todo.util.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@Tag(name = "User Management", description = "A collection of APIs designed to handle functions related to user management")
@Validated
@Slf4j
public class UserController {

	@Autowired
	private UserService userService;

	@Operation(summary = "View a list of available users")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "500", description = "Internal error has occurred", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
	})
	@GetMapping(value = "/", produces={"application/json"})
	@Transactional
	public ResponseEntity<ResponseContainer<UserDto>> getUsersV1(
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
		try {
			ResponseContainer<UserDto> responseContainer = ResponseUtils.pageToDtoResponseContainer(userService.getAllUsers(PageRequest.of(page, pageSize)), UserDto.class);
			return ResponseEntity.ok(responseContainer);
		}
		catch (Exception e) {
			log.error("Error", e);
			throw e;
		}
	}

	@Operation(summary = "Get a specific user's information")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode  = "500", description = "Internal error has occurred", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
	})
	@GetMapping(value = "/{id}", produces={"application/json"})
	public ResponseEntity<ResponseContainer<UserDto>> getUserByUUID(
			@Param(value = "User id to get user object with") @PathVariable UUID id) throws ResourceNotFoundException {
		List<UserDto> userDTOs = ResponseUtils.convertToDtoList(Collections.singletonList(userService.findUserByUuid(id)), UserDto.class);
		return ResponseEntity.ok(new ResponseContainer<>(true, null, userDTOs));
	}

	@Operation(summary = "Update an existing user")
	@ApiResponses(value = {
			@ApiResponse(responseCode  = "200", description = "OK"),
			@ApiResponse(responseCode  = "400", description = "Client Error", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
			@ApiResponse(responseCode  = "500", description = "Internal error has occurred", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
	})
	@PutMapping(value = "/", produces={"application/json"})
	public ResponseEntity<ResponseContainer<UserDto>> updateUser(
			@Validated(Update.class) @RequestBody UserDto userDto) throws ResourceNotFoundException, ImproperResourceSpecification {
		List<UserDto> userDTOs = ResponseUtils.convertToDtoList(Collections.singletonList(userService.updateUser(userDto)), UserDto.class);
		return ResponseEntity.ok(new ResponseContainer<>(true, null, userDTOs));
	}

	@Operation(summary = "Create a new user")
	@ApiResponses(value = {
			@ApiResponse(responseCode  = "201", description = "Created"),
			@ApiResponse(responseCode  = "400", description = "Client Error", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
			@ApiResponse(responseCode  = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
	})
	@ResponseStatus(value = HttpStatus.CREATED)
	@PostMapping(value = "/", produces={"application/json"})
	public ResponseEntity<ResponseContainer<UserDto>> createUser(
			@Validated(Create.class) @RequestBody UserDto userDto) throws URISyntaxException, ImproperResourceSpecification {
		List<UserDto> userDTOs = ResponseUtils.convertToDtoList(Collections.singletonList(userService.createUser(userDto)), UserDto.class);
		return ResponseEntity.created(new URI("/users/id/" + userDTOs.get(0).getUuid())).body(new ResponseContainer<>(true, null, userDTOs));
	}

	@Operation(summary = "Get the specified user's available workspaces")
	@ApiResponses(value = {
			@ApiResponse(responseCode  = "200", description = "OK"),
			@ApiResponse(responseCode  = "400", description = "Client Error", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
			@ApiResponse(responseCode  = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
	})
	@GetMapping(value = "/{id}/workspaces", produces={"application/json"})
	public ResponseEntity<ResponseContainer<WorkspaceDto>> getUserWorkspaces(
			@RequestParam(value = "Status to get user object with") @PathVariable UUID uuid) throws ResourceNotFoundException {
		Set<Workspace> workspaces = userService.getAllWorkspacesForUserUuid(uuid);
		List<WorkspaceDto> workspaceDTOs = ResponseUtils.convertToDtoList(new ArrayList<>(workspaces), WorkspaceDto.class);
		return ResponseEntity.ok(new ResponseContainer<>(true, null, workspaceDTOs));
	}

	@Operation(summary = "Delete the specified user")
	@ApiResponses(value = {
			@ApiResponse(responseCode  = "200", description = "OK", links = {@Link(name = "Test", ref = "http://google.com", operationId = "ah")}),
			@ApiResponse(responseCode  = "400", description = "Client Error", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
			@ApiResponse(responseCode  = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
	})
	@DeleteMapping(value = "/{id}", produces={"application/json"})
	public ResponseEntity<ResponseContainer<UserDto>> deleteUser(
			@RequestParam(value = "Status to get user object with") @PathVariable UUID uuid) throws ResourceNotFoundException {
		userService.deleteUser(uuid);
		return ResponseEntity.ok(new ResponseContainer<>(true, null, new ArrayList<>()));
	}
}
