package org.example.todo;

import lombok.extern.slf4j.Slf4j;
import org.example.todo.model.Login;
import org.example.todo.model.Membership;
import org.example.todo.model.User;
import org.example.todo.model.Workspace;
import org.example.todo.repository.UserRepository;
import org.example.todo.repository.WorkspaceRepository;
import org.example.todo.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
public class Setup {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	public CommandLineRunner demo(UserRepository repository, WorkspaceRepository workspaceRepository) {
		return args -> {

			try {
				Workspace workspace1 = Workspace.builder()
						.name("Unique Workspace")
						.workspaceType(1)
						.status(Status.SUSPENDED)
						.build();
				workspaceRepository.save(workspace1);

				Workspace workspace = Workspace.builder()
						.name("Test Workspace")
						.workspaceType(1)
						.status(Status.ACTIVE)
						.build();

				User user = User.builder()
						.firstName("John")
						.lastName("Doe")
						.email("jdoe@gmail.com")
						.status(Status.ACTIVE)
						.build();

				Login login = Login.builder()
						.username(user.getFirstName().substring(0,1)
								.concat(user.getLastName())
								.toLowerCase())
						.passwordHash(passwordEncoder.encode("test"))
						.build();


				Membership membership = Membership.builder()
						.roleId(1L)
						.build();

				Membership membership1 = Membership.builder()
						.roleId(2L)
						.build();

				Set<Membership> memberships = new HashSet<>(Arrays.asList(membership, membership1));

				workspace.setMemberships(memberships);
				user.setMemberships(memberships);
				user.setLogin(login);
				login.setUser(user);

				for (Membership memship : memberships) {
					memship.setWorkspace(workspace);
					memship.setUser(user);
				}


				workspaceRepository.save(workspace);
				repository.save(user);


				repository.save(User.builder()
						.firstName("Chloe")
						.lastName("O'Brian")
						.email("test@gmail.com")
						.status(Status.ACTIVE)
						.build());
				repository.save(User.builder()
						.firstName("Kim")
						.lastName("Bauer")
						.email("test1@gmail.com")
						.status(Status.ACTIVE)
						.build());

				Login login2 = Login.builder()
						.username("dpalmer")
						.passwordHash(passwordEncoder.encode("test"))
						.build();

				User user2 = User.builder()
						.firstName("David")
						.lastName("Palmer")
						.email("test2@gmail.com")
						.status(Status.ACTIVE)
						.login(login2)
						.build();

				login2.setUser(user2);
				repository.save(user2);
				repository.save(User.builder()
						.firstName("Michelle")
						.lastName("Dessler")
						.email("test3@gmail.com")
						.status(Status.ACTIVE)
						.build());
			}
			catch (DataIntegrityViolationException e) {
				log.error("Failed to add duplicate data");
			}
			catch (Exception e) {
				log.error("Failed to fully add test data.", e);
			}

			// fetch all customers
			log.info("Users found with findAll():");
			log.info("-------------------------------");
			for (User customer : repository.findAll()) {
				log.info(customer.toString());
			}
			log.info("");

			// fetch an individual customer by ID
			User customer = repository.findById(1L);
			if (Objects.nonNull(customer)) {
				log.info("User found with findById(1L):");
				log.info("--------------------------------");
				log.info(customer.toString());
				log.info("");
			}

			// fetch customers by last name
			log.info("User found with findByLastName('Doe'):");
			log.info("--------------------------------------------");
			repository.findByLastName("Doe").forEach(bauer -> {
				log.info(bauer.toString());
				bauer.setStatus(Status.DELETED);
				repository.save(bauer);
			});
			log.info("");
		};
	}
}
