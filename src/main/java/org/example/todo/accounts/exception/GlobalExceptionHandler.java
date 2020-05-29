package org.example.todo.accounts.exception;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorDetails> resourceNotFoundException(ResourceNotFoundException e, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(Instant.now().atOffset(ZoneOffset.UTC), e.getMessage(), null, request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ImproperResourceSpecification.class)
	public ResponseEntity<ErrorDetails> improperResourceSpecification(ImproperResourceSpecification e, WebRequest request) {
		ErrorDetails errorDetails = new ErrorDetails(Instant.now().atOffset(ZoneOffset.UTC), e.getMessage(), e.getDetails(), request.getDescription(false));
		return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetails> globalExceptionHandler(Exception e, WebRequest request) {
		Throwable throwable = ExceptionUtils.getRootCause(e);
		if (throwable instanceof ConstraintViolationException) {
			Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) throwable).getConstraintViolations();
			List<String> violationMessages = new ArrayList<>();
			for (ConstraintViolation<?> violationException : constraintViolations) {
				log.trace("Field {} {}", violationException.getPropertyPath(), violationException.getMessage());
				violationMessages.add(String.format("Field '%s' %s", violationException.getPropertyPath(), violationException.getMessage()));
			}
			log.debug("ConstraintViolationException:", e);
			return new ResponseEntity<>(new ErrorDetails(Instant.now().atOffset(ZoneOffset.UTC), "Cannot save due to specification issues.", violationMessages, request.getDescription(false)), HttpStatus.BAD_REQUEST);
		}
		//Test if exception is a child class of JsonProcessing Exception, such as JsonParseException or InvalidFormatException
		else if (JsonProcessingException.class.isAssignableFrom(throwable.getClass())) {
			log.debug("JsonProcessingException", e);
			String originalMessage = ((JsonProcessingException) throwable).getOriginalMessage();
			JsonLocation jsonLocation = ((JsonProcessingException) throwable).getLocation();
			String errorMessage = String.format("%s at line: %d, column: %d", originalMessage, jsonLocation.getLineNr(), jsonLocation.getColumnNr());
			return new ResponseEntity<>(new ErrorDetails(Instant.now().atOffset(ZoneOffset.UTC), "JSON Validation Failure", Collections.singletonList(errorMessage), request.getDescription(false)), HttpStatus.BAD_REQUEST);
		}
		else {
			log.error("Caught Unhandled Error {}, with root cause {}", e.getClass(), throwable.getClass(), e);
			ErrorDetails errorDetails = new ErrorDetails(Instant.now().atOffset(ZoneOffset.UTC), "An Internal Server error has occurred.", null, request.getDescription(false));
			return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
