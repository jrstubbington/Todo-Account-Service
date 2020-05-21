package org.example.todo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.todo.exception.ErrorDetails;
import org.example.todo.service.UserService;
import org.example.todo.util.ResponseContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/greeting")
@Tag(name = "Test APIs", description = "A collection of APIs for testing")
public class TestController {

	@Autowired
	private UserService userService;

	@Operation(description = "View a list of available users")
	@ApiResponses(value = {
			@ApiResponse(responseCode  = "200", description = "OK"),
			@ApiResponse(responseCode  = "500", description = "Internal error has occurred", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
	})
	@GetMapping(value = "/v1", produces={"application/json"})
	public ResponseEntity<ResponseContainer<String>> greetingv1() {
		return ResponseEntity.ok(new ResponseContainer<>(true, null, Arrays.asList("Hello there!", "GENERAL KENOBI!")));
	}

	@Operation(description = "View a list of available users")
	@ApiResponses(value = {
			@ApiResponse(responseCode  = "200", description = "OK"),
			@ApiResponse(responseCode  = "500", description = "Internal error has occurred", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
	})
	@GetMapping(value = "/v2", produces={"application/json"})
	public ResponseEntity<ResponseContainer<String>> greetingv2() {
		return ResponseEntity.ok(new ResponseContainer<>(true, null, Arrays.asList("Hello there!", "GENERAL KENOBI!")));
	}
}
