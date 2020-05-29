package org.example.todo.accounts.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "$$_hibernate_interceptor"})
public interface DtoEntity {
	UUID getUuid();
}
