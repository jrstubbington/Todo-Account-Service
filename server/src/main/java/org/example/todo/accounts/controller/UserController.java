package org.example.todo.accounts.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.example.todo.accounts.dto.AccountCreationRequest;
import org.example.todo.accounts.dto.JobProcessResponse;
import org.example.todo.accounts.dto.ResponseContainerUserDto;
import org.example.todo.accounts.dto.ResponseContainerWorkspaceDto;
import org.example.todo.accounts.dto.UserDto;
import org.example.todo.accounts.service.UserService;
import org.example.todo.common.exceptions.ImproperResourceSpecification;
import org.example.todo.common.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@RestController
@Api(tags = "User Management")
@Validated
@Slf4j
public class UserController implements UserManagementApi {

	private UserService userService;

//	@Autowired //TODO: autowire setter
//	private StorageService storageService;



	@Override
	public ResponseEntity<ResponseContainerUserDto> createUser(@Valid AccountCreationRequest accountCreationRequest) throws ImproperResourceSpecification, ResourceNotFoundException {
		return ResponseEntity.ok(userService.createUserResponse(accountCreationRequest));
	}

	@Override
	@Transactional
	public ResponseEntity<ResponseContainerUserDto> getUsersV1(@Valid Optional<Integer> page, @Valid Optional<Integer> pageSize) {
		return ResponseEntity.ok(userService.getAllUsersResponse(PageRequest.of(page.orElse(0), pageSize.orElse(10))));
	}

	@Override
	public ResponseEntity<ResponseContainerUserDto> updateUser(@Valid UserDto userDto) throws ResourceNotFoundException, ImproperResourceSpecification {
		return ResponseEntity.ok(userService.updateUserResponse(userDto));
	}

	@Override
	public ResponseEntity<ResponseContainerUserDto> getUserByUUID(
			@PathVariable UUID uuid) throws ResourceNotFoundException {
		return ResponseEntity.ok(userService.findUserByUuidResponse(uuid));
	}

	@Override
	public ResponseEntity<ResponseContainerWorkspaceDto> getUserWorkspaces(
			@PathVariable UUID uuid) throws ResourceNotFoundException {
		return ResponseEntity.ok(userService.getAllWorkspacesForUserUuidResponse(uuid));
	}

	@Override
	public ResponseEntity<ResponseContainerUserDto> deleteUser(
			@PathVariable UUID uuid) throws ResourceNotFoundException {
		return ResponseEntity.ok(userService.deleteUserResponse(uuid));
	}

	@Override
	public ResponseEntity<JobProcessResponse> uploadFile(@RequestPart(required = true) MultipartFile file) throws IOException, ImproperResourceSpecification {
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		InputStream inputStream =  new BufferedInputStream(file.getInputStream()); //TODO convert back to file to avoid large memory spikes
		JobProcessResponse jobProcessResponse = userService.batchUpload(inputStream);
		inputStream.close();
		jobProcessResponse.setMessage("Done!");
		stopwatch.stop();
		log.info("Processing report took {}", stopwatch.getTotalTimeSeconds());
		return ResponseEntity.ok(jobProcessResponse);
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
