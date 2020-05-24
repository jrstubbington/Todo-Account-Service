package org.example.todo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "$$_hibernate_interceptor"})
public interface DtoEntity {
}
