package org.example.todo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
@Getter
public class ImproperResourceSpecification extends Exception {

	private final List<String> details;

	public ImproperResourceSpecification(String message){
		super(message);
		this.details = null;
	}


	public ImproperResourceSpecification(String message, List<String> details){
		super(message);
		this.details = Objects.nonNull(details) ? new ArrayList<>(details) : null;
	}
}
