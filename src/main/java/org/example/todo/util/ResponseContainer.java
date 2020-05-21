package org.example.todo.util;

import lombok.Data;

import java.util.List;

@Data
public class ResponseContainer<T> {
	private boolean success;
	private String statusDescription;
	private long totalElements;
	private long totalPages = 1L;
	private long pageSize;
	private long page;
	private boolean last = true;
	private final int size;
	private final String type;
	private final List<T> data;

	public ResponseContainer(boolean success, String statusDescription, List<T> data) {
		this.success = success;
		this.statusDescription = statusDescription;
		this.size = data.size();
		this.type = !data.isEmpty() ? data.get(0).getClass().getName(): null;
		this.data = data;
		this.totalElements = this.size;
	}
}
