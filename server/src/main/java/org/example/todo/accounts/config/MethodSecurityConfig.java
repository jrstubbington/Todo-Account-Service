package org.example.todo.accounts.config;

import org.example.todo.accounts.security.CustomMethodSecurityExpressionHandler;
import org.example.todo.accounts.security.CustomPermissionEvaluator;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

	private final String clientId;

	@Autowired
	private Keycloak keycloak;

	public MethodSecurityConfig(@Value("${keycloak.resource}") String clientId) {
		super();
		this.clientId = clientId;
	}

	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		CustomMethodSecurityExpressionHandler expressionHandler =
				new CustomMethodSecurityExpressionHandler(clientId, keycloak);
		expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator());
		return expressionHandler;
	}
}