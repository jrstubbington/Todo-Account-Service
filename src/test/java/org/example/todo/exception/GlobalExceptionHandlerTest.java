package org.example.todo.exception;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

	@Mock
	private WebRequest request;

	@Mock
	private ConstraintViolation<Object> constraintViolation;

	@Mock
	private JsonParser jsonParser;

	private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

	@Test
	void testResourceNotFoundException() {
		ResourceNotFoundException exception = new ResourceNotFoundException("Resource Not Found");

		when(request.getDescription(false)).thenReturn("This is a test description");

		ResponseEntity<ErrorDetails> errorResponse = ResponseEntity.badRequest().body(new ErrorDetails(Instant.now().atOffset(ZoneOffset.UTC), "Resource Not Found", null, "This is a test description"));

		assertEquals(errorResponse, globalExceptionHandler.resourceNotFoundException(exception, request),
				"Response should match expected format");
	}

	@Test
	void testImproperResourceSpecificationException() {
		ImproperResourceSpecification exception = new ImproperResourceSpecification("Resource not defined correctly");

		when(request.getDescription(false)).thenReturn("This is a test description");

		ResponseEntity<ErrorDetails> errorResponse = ResponseEntity.badRequest().body(new ErrorDetails(Instant.now().atOffset(ZoneOffset.UTC), "Resource not defined correctly", null, "This is a test description"));

		assertEquals(errorResponse, globalExceptionHandler.improperResourceSpecification(exception, request),
				"Response should match expected format");
	}

	@Test
	void testImproperResourceSpecificationWithDetailsException() {
		List<String> details = new ArrayList<>();
		details.add("This is example detail 1");
		details.add("This is example detail 2");
		ImproperResourceSpecification exception = new ImproperResourceSpecification("Resource not defined correctly", details);

		when(request.getDescription(false)).thenReturn("This is a test description");

		ResponseEntity<ErrorDetails> errorResponse = ResponseEntity.badRequest().body(new ErrorDetails(Instant.now().atOffset(ZoneOffset.UTC), "Resource not defined correctly", details, "This is a test description"));

		assertEquals(errorResponse, globalExceptionHandler.improperResourceSpecification(exception, request),
				"Response should match expected format");
	}

	@Test
	void testJsonValidationException() {
		JsonParseException jsonParseException = new JsonParseException(jsonParser, "Failed to do cool things", new JsonLocation(new Object(), 1L, 14, 14));

		ResponseEntity<ErrorDetails> errorResponse = ResponseEntity.badRequest().body(new ErrorDetails(Instant.now().atOffset(ZoneOffset.UTC), "JSON Validation Failure", Collections.singletonList("Failed to do cool things at line: 14, column: 14"), "This is a test description"));
		when(request.getDescription(false)).thenReturn("This is a test description");

		assertEquals(errorResponse, globalExceptionHandler.globalExceptionHandler(jsonParseException, request),
				"Response should match expected format");
	}


	@Test
	@DisplayName("Verify Cleaned Error Response")
	void testGlobalExceptionHandler() {
		NullPointerException exception = new NullPointerException("Potentially sensitive error message here");

		when(request.getDescription(false)).thenReturn("This is a test description");

		ResponseEntity<ErrorDetails> errorResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDetails(Instant.now().atOffset(ZoneOffset.UTC), "An Internal Server error has occurred.", null, "This is a test description"));

		assertEquals(errorResponse, globalExceptionHandler.globalExceptionHandler(exception, request),
				"Failed to verify that unhandled error messages always match expected response.");
	}

	@Test
	void testGlobalExceptionHandlerConstraintViolation() {
		Set<ConstraintViolation<Object>> violations = new HashSet<>();
		violations.add(constraintViolation);
		Exception exception = new Exception("This could be a stacktrace here", new ConstraintViolationException(violations));

		when(request.getDescription(false)).thenReturn("This is a test description");
		when(constraintViolation.getMessage()).thenReturn("This is a test");

		ResponseEntity<ErrorDetails> errorResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDetails(Instant.now().atOffset(ZoneOffset.UTC), "Cannot save due to specification issues.", Collections.singletonList("Field 'null' This is a test"), "This is a test description"));

		assertEquals(errorResponse.getStatusCode(), globalExceptionHandler.globalExceptionHandler(exception, request).getStatusCode(),
				"Status code should be BAD_REQUEST (400)");
		assertEquals(errorResponse.getBody(), globalExceptionHandler.globalExceptionHandler(exception, request).getBody(),
				"Response should match expected format");
	}
}