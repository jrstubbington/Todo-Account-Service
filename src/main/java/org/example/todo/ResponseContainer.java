package org.example.todo;

import lombok.Data;

import java.util.List;

@Data
public class ResponseContainer<T> {

	private final String status;
	private final String statusDescription;
	private final int size;
	private final String type;
	private final List<T> data;

	public ResponseContainer(String status, String statusDescription, List<T> data) {
		this.status = status;
		this.statusDescription = statusDescription;
		this.size = data.size();
		this.type = !data.isEmpty() ? data.get(0).getClass().getName(): null;
		this.data = data;
	}
}
