package org.example.todo.accounts.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.example.todo.accounts.generated.controller.UserManagementApi;
import org.example.todo.accounts.generated.dto.AccountCreationRequest;
import org.example.todo.accounts.generated.dto.JobProcessResponse;
import org.example.todo.accounts.generated.dto.ResponseContainerUserDto;
import org.example.todo.accounts.generated.dto.ResponseContainerWorkspaceDto;
import org.example.todo.accounts.generated.dto.UserDto;
import org.example.todo.accounts.service.UserService;
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
	public ResponseEntity<ResponseContainerUserDto> createUser(@Valid AccountCreationRequest accountCreationRequest) {
		return ResponseEntity.ok(userService.createUserResponse(accountCreationRequest));
	}

	@Override
	@Transactional
	public ResponseEntity<ResponseContainerUserDto> getUsersV1(@Valid Integer page, @Valid Integer pageSize) {
		return ResponseEntity.ok(userService.getAllUsersResponse(PageRequest.of(page, pageSize)));
	}

	@Override
	public ResponseEntity<ResponseContainerUserDto> updateUser(@Valid UserDto userDto) {
		return ResponseEntity.ok(userService.updateUserResponse(userDto));
	}

	@Override
	public ResponseEntity<ResponseContainerUserDto> getUserByUUID(
			@PathVariable UUID uuid){
		return ResponseEntity.ok(userService.findUserByUuidResponse(uuid));
	}

	@Override
	public ResponseEntity<ResponseContainerWorkspaceDto> getUserWorkspaces(
			@PathVariable UUID uuid){
		return ResponseEntity.ok(userService.getAllWorkspacesForUserUuidResponse(uuid));
	}

	@Override
	public ResponseEntity<ResponseContainerUserDto> deleteUser(
			@PathVariable UUID uuid) {
		return ResponseEntity.ok(userService.deleteUserResponse(uuid));
	}

	@Override
	public ResponseEntity<JobProcessResponse> uploadFile(@RequestPart(required = true) MultipartFile file) throws IOException {
		StopWatch stopwatch = new StopWatch();
		stopwatch.start();
		//TODO convert back to file to avoid large memory spikes
		InputStream inputStream = new BufferedInputStream(file.getInputStream());
		JobProcessResponse jobProcessResponse = userService.batchUpload(inputStream);
		inputStream.close();
		jobProcessResponse.setMessage("Done!");
		log.info("Processing report took {}", stopwatch.getTotalTimeSeconds());
		return ResponseEntity.ok(jobProcessResponse);
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
