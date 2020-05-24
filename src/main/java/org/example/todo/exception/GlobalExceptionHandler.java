package org.example.todo.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.time.ZoneOffset;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ExceptionHandler({ResourceNotFoundException.class, ImproperResourceSpecification.class})
	public ResponseEntity<ErrorDetails> resourceNotFoundException(Exception ex, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(Instant.now().atOffset(ZoneOffset.UTC), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetails> globalExceptionHandler(Exception ex, WebRequest request) {
		log.error("Caught Error", ex);
		ErrorDetails errorDetails = new ErrorDetails(Instant.now().atOffset(ZoneOffset.UTC), ex.getMessage(), request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
