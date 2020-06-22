package org.example.todo.accounts.config;

import org.example.todo.accounts.ApiClient;
import org.example.todo.accounts.generated.controller.UserManagementApi;
import org.example.todo.accounts.generated.controller.WorkspaceManagementApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AccountsApiClientConfig {

	@Bean
	public UserManagementApi userManagementApi(RestTemplate restTemplate) {
		UserManagementApi userManagementApi = new UserManagementApi();
		userManagementApi.setApiClient(new ApiClient(restTemplate));
		return userManagementApi;
	}

	@Bean
	public WorkspaceManagementApi workspaceManagementApi(RestTemplate restTemplate) {
		WorkspaceManagementApi workspaceManagementApi = new WorkspaceManagementApi();
		workspaceManagementApi.setApiClient(new ApiClient(restTemplate));
		return workspaceManagementApi;
	}
}
