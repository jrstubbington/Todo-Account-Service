package org.example.todo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.todo.dto.AccountCreationRequest;
import org.example.todo.dto.UserDto;
import org.example.todo.dto.WorkspaceDto;
import org.example.todo.exception.ErrorDetails;
import org.example.todo.exception.ImproperResourceSpecification;
import org.example.todo.exception.ResourceNotFoundException;
import org.example.todo.service.UserService;
import org.example.todo.util.Create;
import org.example.todo.util.ResponseContainer;
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

import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@Tag(name = "User Management", description = "A collection of APIs designed to handle functions related to user management")
@Validated
@Slf4j
public class UserController {

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
		return ResponseEntity.ok(userService.getAllUsersResponse(PageRequest.of(page, pageSize)));
	}

	@Operation(summary = "Get a specific user's information")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode  = "500", description = "Internal error has occurred", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
	})
	@GetMapping(value = "/{id}", produces={"application/json"})
	public ResponseEntity<ResponseContainer<UserDto>> getUserByUUID(
			@Param(value = "User id to get user object with") @PathVariable UUID id) throws ResourceNotFoundException {
		return ResponseEntity.ok(userService.findUserByUuidResponse(id));
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
		return ResponseEntity.ok(userService.updateUserResponse(userDto));
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
			@Validated(Create.class) @RequestBody AccountCreationRequest accountCreationRequest) throws ImproperResourceSpecification, ResourceNotFoundException {
		return ResponseEntity.ok(userService.createUserResponse(accountCreationRequest));
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
		return ResponseEntity.ok(userService.getAllWorkspacesForUserUuidResponse(uuid));
	}

	@Operation(summary = "Delete the specified user")
	@ApiResponses(value = {
			@ApiResponse(responseCode  = "200", description = "OK"),
			@ApiResponse(responseCode  = "400", description = "Client Error", content = @Content(schema = @Schema(implementation = ErrorDetails.class))),
			@ApiResponse(responseCode  = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
	})
	@DeleteMapping(value = "/{id}", produces={"application/json"})
	public ResponseEntity<ResponseContainer<UserDto>> deleteUser(
			@RequestParam(value = "Status to get user object with") @PathVariable UUID uuid) throws ResourceNotFoundException {
		return ResponseEntity.ok(userService.deleteUserResponse(uuid));
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
