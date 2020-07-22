package org.example.todo.accounts.security;

import org.aopalliance.intercept.MethodInvocation;
import org.keycloak.admin.client.Keycloak;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
	private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

	private final String clientId;
	private final Keycloak keycloak;

	public CustomMethodSecurityExpressionHandler(String clientId, Keycloak keycloak) {
		super();
		this.clientId = clientId;
		this.keycloak = keycloak;
	}

	@Override
	protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
		final CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication, clientId, keycloak);
		root.setPermissionEvaluator(getPermissionEvaluator());
		root.setTrustResolver(this.trustResolver);
		root.setRoleHierarchy(getRoleHierarchy());
		return root;
	}
}
