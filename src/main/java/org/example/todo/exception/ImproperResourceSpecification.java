package org.example.todo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ImproperResourceSpecification extends Exception {
	public ImproperResourceSpecification(String message){
		super(message);
	}
}
