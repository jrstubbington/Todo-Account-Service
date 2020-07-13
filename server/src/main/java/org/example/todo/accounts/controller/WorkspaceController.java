package org.example.todo.accounts.controller;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.example.todo.accounts.generated.controller.WorkspaceManagementApi;
import org.example.todo.accounts.generated.dto.ResponseContainerUserDto;
import org.example.todo.accounts.generated.dto.ResponseContainerWorkspaceDto;
import org.example.todo.accounts.generated.dto.WorkspaceDto;
import org.example.todo.accounts.service.UserService;
import org.example.todo.accounts.service.WorkspaceService;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;
import java.util.UUID;

@Controller
@Api(tags = "Workspace Management")
@Validated
@Slf4j
public class WorkspaceController implements WorkspaceManagementApi {

	private WorkspaceService workspaceService;

	private UserService userService;

	@Autowired
	private HttpServletRequest request;

	@Override
	public ResponseEntity<ResponseContainerUserDto> getUsersInWorkspace(UUID uuid) {
		return ResponseEntity.ok(userService.getAllUsersInWorkspaceResponse(uuid));
	}

	@Override
	public ResponseEntity<ResponseContainerWorkspaceDto> getWorkspacesV1(@Valid Integer page, @Valid Integer pageSize) {
		return ResponseEntity.ok(workspaceService.getAllWorkspacesResponse(PageRequest.of(page, pageSize)));
	}

	@Override
	public ResponseEntity<ResponseContainerWorkspaceDto> getWorkspaceByUUID(UUID uuid) {
		return ResponseEntity.ok(workspaceService.findWorkspaceByUuidResponse(uuid));
	}

	/**
	 * POST /v1/workspaces/ : Create a new workspace
	 *
	 * @param workspaceDto (required)
	 * @return OK (status code 200)
	 * or Client Error (status code 400)
	 * or Unauthorized (status code 401)
	 * or Internal error has occurred (status code 500)
	 */
	@Override
	public ResponseEntity<ResponseContainerWorkspaceDto> createWorkspace(@Valid WorkspaceDto workspaceDto){
		Pair<String, AccessToken> authToken = processAccessToken();
		UUID userUuid = UUID.fromString(authToken.getKey());
		return ResponseEntity.ok(workspaceService.createWorkspaceResponse(userUuid, workspaceDto));
	}

	//TODO: Move to utility package
	private Pair<String, AccessToken> processAccessToken() {
		KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
		if (Objects.nonNull(token)) {
			@SuppressWarnings("unchecked")
			KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) token.getPrincipal();
			KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
			return new ImmutablePair<>(principal.getName(), session.getToken());
		}
		return new ImmutablePair<>("", new AccessToken());
	}

	@Autowired
	public void setWorkspaceService(WorkspaceService workspaceService) {
		this.workspaceService = workspaceService;
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
