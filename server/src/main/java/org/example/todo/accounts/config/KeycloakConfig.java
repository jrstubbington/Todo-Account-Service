package org.example.todo.accounts.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

	@Bean
	@Autowired
	public Keycloak keycloak(@Value("${keycloak.resource}") String clientId,
	                         @Value("${keycloak.credentials.secret}") String clientSecret){
		return KeycloakBuilder.builder()
				.serverUrl("http://keycloak.trullingham.com/auth") //TODO: get from properties file
				.grantType(OAuth2Constants.CLIENT_CREDENTIALS)
				.realm("TodoProject") //TODO: get from properties file
				.clientId(clientId)
				.clientSecret(clientSecret)
				.resteasyClient(
						new ResteasyClientBuilder()
								.connectionPoolSize(10).build()
				)
				.build();
	}
}
