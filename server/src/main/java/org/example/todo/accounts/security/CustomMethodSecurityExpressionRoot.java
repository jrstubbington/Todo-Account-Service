package org.example.todo.accounts.security;

import lombok.extern.slf4j.Slf4j;
import org.example.todo.common.exceptions.ImproperResourceSpecification;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@SuppressWarnings("unused")
public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

	private Object filterObject;
	private Object returnObject;

	private final String clientId;

	private final Keycloak keycloak;

	public CustomMethodSecurityExpressionRoot(Authentication authentication, String clientId, Keycloak keycloak) {
		super(authentication);
		this.clientId = clientId;
		this.keycloak = keycloak;
	}

	public boolean ownsLessThanWorkspaces(int maximumWorkspaceCount) {
		@SuppressWarnings("unchecked")
		KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) this.getPrincipal();
		KeycloakSecurityContext session = principal.getKeycloakSecurityContext();

		//TODO: Get from properties file
		RealmResource realmResource = keycloak.realm("TodoProject");
		UUID clientUUID = UUID.fromString(realmResource.clients().findByClientId(clientId).get(0).getId());
		ClientResource clientRsc = realmResource.clients().get(clientUUID.toString());

		//TODO Create custom exception
		if (clientRsc.authorization().resources().findByName("", session.getToken().getEmail()).size() >= maximumWorkspaceCount) {
			throw new ImproperResourceSpecification("You can't have more than " + maximumWorkspaceCount + " workspaces");
		}

		return true;
	}

	//https://www.keycloak.org/docs/latest/authorization_services/index.html#_service_obtaining_permissions
	public boolean canViewWorkspace(UUID workspaceUuid) {

		@SuppressWarnings("unchecked")
		KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) this.getPrincipal();
		KeycloakSecurityContext session = principal.getKeycloakSecurityContext();

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Authorization", "bearer " + session.getTokenString());

		log.trace("CLIENT ID: {}", clientId);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", OAuth2Constants.UMA_GRANT_TYPE);
		map.add("audience", clientId);
		map.add("response_mode", "decision"); //permissions or decision
		map.add("response_include_resource_name", "true");
		map.add("permission", "workspace_"+workspaceUuid+"#workspace:view");

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

		ResponseEntity<AuthorizationDecision> response =
				restTemplate.exchange("http://keycloak.trullingham.com/auth/realms/TodoProject/protocol/openid-connect/token",
						HttpMethod.POST,
						entity,
						AuthorizationDecision.class);

		AuthorizationDecision decision = Objects.requireNonNull(response.getBody());
		return decision.getResult();


//		//TODO: Convert token introspection endpoint to environment variable
//		ResponseEntity<AuthorizationScope[]> response =
//				restTemplate.exchange("http://keycloak.trullingham.com/auth/realms/TodoProject/protocol/openid-connect/token",
//						HttpMethod.POST,
//						entity,
//						AuthorizationScope[].class);
//		AuthorizationScope[] objects = Objects.requireNonNull(response.getBody());
//		for (AuthorizationScope object : objects) {
//			if (object.getScopes().contains("workspace:view")) {
//				return true;
//			}
//		}
	}

	@Override
	public Object getFilterObject() {
		return this.filterObject;
	}

	@Override
	public Object getReturnObject() {
		return this.returnObject;
	}

	@Override
	public Object getThis() {
		return this;
	}

	@Override
	public void setFilterObject(Object obj) {
		this.filterObject = obj;
	}

	@Override
	public void setReturnObject(Object obj) {
		this.returnObject = obj;
	}
}
