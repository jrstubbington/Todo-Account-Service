package org.example.todo.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

	@Mock
	private WebRequest request;

	private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

	@Test
	void resourceNotFoundException() {
		ResourceNotFoundException exception = new ResourceNotFoundException("Resource Not Found");

		when(request.getDescription(false)).thenReturn("This is a test description");

		ResponseEntity<ErrorDetails> errorResponse = ResponseEntity.badRequest().body(new ErrorDetails(Instant.now().atOffset(ZoneOffset.UTC), "Resource Not Found", "This is a test description"));

		assertEquals(errorResponse, globalExceptionHandler.resourceNotFoundException(exception, request));
	}

	@Test
	void globalExceptionHandler() {
		NullPointerException exception = new NullPointerException("This could be a stacktrace here");

		when(request.getDescription(false)).thenReturn("This is a test description");

		ResponseEntity<ErrorDetails> errorResponse = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDetails(Instant.now().atOffset(ZoneOffset.UTC), "This could be a stacktrace here", "This is a test description"));

		assertEquals(errorResponse, globalExceptionHandler.globalExceptionHandler(exception, request));
	}
}