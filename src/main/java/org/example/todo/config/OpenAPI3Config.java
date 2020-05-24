package org.example.todo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPI3Config {

	private final String packageName;

	private final String packageVersion;

	@Autowired
	public OpenAPI3Config(@Value("${project.package}") String packageName, @Value("${project.version}") String packageVersion){
		this.packageName = packageName;
		this.packageVersion = packageVersion;
	}


	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("Account Management API")
						.description("Service for managing identity and roles")
						.license(new License()
								.name("License: MIT")
								.url("https://github.com/jrstubbington/Todo-Account-Service/blob/master/LICENSE"))
						.version(packageVersion)
						.contact(new Contact()
								.name("James Stubbington")
								.email("jamesrstubbington@gmail.com")
								.url("https://github.com/jrstubbington")));
	}

	@Bean
	public GroupedOpenApi otherApis() {
		return GroupedOpenApi.builder()
				.setGroup("Other")
				.pathsToMatch("/**")
				.packagesToExclude(packageName)
				.build();
	}

	@Bean
	public GroupedOpenApi publicApiV1() {
		return GroupedOpenApi.builder()
				.setGroup("Version 1")
				.pathsToMatch("/v1/**")
				.packagesToScan(packageName)
//				.addOpenApiCustomiser(openApi -> openApi.setInfo(openApi.getInfo().version("v1")))
				.build();
	}

	@Bean
	public GroupedOpenApi publicApiV2() {
		return GroupedOpenApi.builder()
				.setGroup("Version 2")
				.pathsToMatch("/v2/**")
				.packagesToScan(packageName)
//				.addOpenApiCustomiser(openApi -> openApi.setInfo(openApi.getInfo().version("v2")))
				.build();
	}
}
