package org.example.todo.accounts.controller;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class TestController {

	private final String clientId;

	@Autowired
	private KeycloakPrincipal<KeycloakSecurityContext> keycloakPrincipal;

	@Autowired
	private Keycloak keycloak;

	@Autowired
	public TestController(@Value("${keycloak.resource}") String clientId) {
		this.clientId = clientId;
	}

	@GetMapping("/test")
	@PreAuthorize("ownsLessThanWorkspaces(10)")
	public ResponseEntity<String> test() {
		RealmResource realmResource = keycloak.realm("TodoProject"); //TODO: Get from properties file
		UUID clientUUID = UUID.fromString(realmResource.clients().findByClientId(clientId).get(0).getId());
		ClientResource clientRsc = realmResource.clients().get(clientUUID.toString());
//		log.info("CLIENT NAME: " + clientRsc.toRepresentation().getClientId());
//		ClientRepresentation client = clientRsc.toRepresentation();
//		log.info("SERVICE ENABLED?: " + client.isServiceAccountsEnabled());
//		log.info("AUTH SERVICE ENABLED?: " + client.getAuthorizationServicesEnabled());
		AuthorizationResource authRsc = clientRsc.authorization();
//		ResourceServerRepresentation authRep = authRsc.exportSettings();


		ResourceRepresentation resourceRepresentation = new ResourceRepresentation();
		UUID workspaceUuid = UUID.randomUUID();
		resourceRepresentation.setId(workspaceUuid.toString());
		resourceRepresentation.setOwner(keycloakPrincipal.getName());
		resourceRepresentation.setName("workspace_"+workspaceUuid);
		resourceRepresentation.setType("urn:" + clientId + ":resources:workspace");
		resourceRepresentation.setUris(Collections.singleton("/workspaces/"+workspaceUuid+"/*"));
		resourceRepresentation.setOwnerManagedAccess(true);
		Set<ScopeRepresentation> scopes = authRsc.scopes().scopes()
				.stream()
				.filter(scopeRepresentation -> scopeRepresentation.getName().startsWith("workspace"))
				.collect(Collectors.toSet());
		resourceRepresentation.setScopes(scopes);

		GroupRepresentation groupRepresentation = new GroupRepresentation();
		groupRepresentation.setName("workspace_" + workspaceUuid);

		UUID topFolderUuid = UUID.fromString(realmResource.groups().groups("workspace", 0, 1).get(0).getId());
		String folderUuid = null;
		try (Response response = realmResource.groups().group(topFolderUuid.toString()).subGroup(groupRepresentation)){
			if (response.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL) {
				folderUuid = response.readEntity(GroupRepresentation.class).getId();
				log.info("Created Subgroup with id: {}", folderUuid);

			} else {
				log.error("Error Creating Subgroup : " + groupRepresentation + ", Error Message : " + response.getStatus());
			}
		}

		GroupRepresentation adminGroup = new GroupRepresentation();
		adminGroup.setName("admins");

		GroupRepresentation membersGroup = new GroupRepresentation();
		membersGroup.setName("members");
		try (Response response = realmResource.groups().group(folderUuid).subGroup(adminGroup)) {
			String groupUuid = response.readEntity(GroupRepresentation.class).getId();
			realmResource.users().get(keycloakPrincipal.getName()).joinGroup(groupUuid);
		}
		try (Response response = realmResource.groups().group(folderUuid).subGroup(membersGroup)) {
			String groupUuid = response.readEntity(GroupRepresentation.class).getId();
			realmResource.users().get(keycloakPrincipal.getName()).joinGroup(groupUuid);
		}

		List<GroupRepresentation> groupRepresentationList = realmResource.groups().group(folderUuid).toRepresentation().getSubGroups();

		for (GroupRepresentation group : groupRepresentationList) {
			realmResource.groups().group(group.getId()).members().forEach(userRepresentation -> log.info("USER: {}", userRepresentation.getUsername()));
		}

		try (Response response = clientRsc.authorization().resources().create(resourceRepresentation)) {
			String resourceUuid = response.readEntity(ResourceRepresentation.class).getId();
			log.info("EQUAL {}", workspaceUuid.toString().equals(resourceUuid));
		}

		return ResponseEntity.ok("Cool");
	}
}
