package org.example.todo.accounts;

import org.example.todo.accounts.repository.BatchRepositoryImpl;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("org.example.todo.*")
@EnableBatchProcessing
@EnableScheduling
@EnableJpaRepositories(repositoryBaseClass = BatchRepositoryImpl.class)
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
