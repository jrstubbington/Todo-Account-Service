package org.example.todo.accounts.dto;

import lombok.Data;

@Data
public class JobProcessResponse {
	private String message;
	private int read;
	private int processed;
	private int skipped;
	private int errored;
}
