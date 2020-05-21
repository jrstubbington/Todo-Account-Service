package org.example.todo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.todo.util.ResponseContainer;
import org.example.todo.dto.WorkspaceDto;
import org.example.todo.exception.ErrorDetails;
import org.example.todo.service.WorkspaceService;
import org.example.todo.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/workspaces")
@Tag(name = "Workspace Management", description = "A collection of APIs designed to handle functions related to workspace management")
@Validated
public class WorkspaceController {

	@Autowired
	private WorkspaceService workspaceService;

	@Operation(summary = "View a list of available workspaces")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "500", description = "Internal error has occurred", content = @Content(schema = @Schema(implementation = ErrorDetails.class)))
	})
	@GetMapping(value = "/v1", produces={"application/json"})
	public ResponseEntity<ResponseContainer<WorkspaceDto>> getWorkspacesV1(@RequestParam(value = "page", required = false, defaultValue = "0") Integer page, @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
		ResponseContainer<WorkspaceDto> responseContainer = ResponseUtils.pageToDtoResponseContainer(workspaceService.getAllWorkspaces(PageRequest.of(page, pageSize)), WorkspaceDto.class);
		return ResponseEntity.ok(responseContainer);
	}
}
