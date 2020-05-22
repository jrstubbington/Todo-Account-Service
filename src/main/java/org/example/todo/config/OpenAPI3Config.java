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

	@Autowired
	public OpenAPI3Config(@Value("${project.package}") String packageName){
		this.packageName = packageName;
	}


	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("Account Management API")
						.license(new License().name("Apache 2.0"))
						.contact(new Contact().name("James Stubbington")));
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
				.pathsToMatch("/**/v1/**")
				.packagesToScan(packageName)
				.addOpenApiCustomiser(openApi -> openApi.setInfo(openApi.getInfo().version("v1")))
				.build();
	}

	@Bean
	public GroupedOpenApi publicApiV2() {
		return GroupedOpenApi.builder()
				.setGroup("Version 2")
				.pathsToMatch("/**/v2/**")
				.packagesToScan(packageName)
				.addOpenApiCustomiser(openApi -> openApi.setInfo(openApi.getInfo().version("v2")))
				.build();
	}
}
