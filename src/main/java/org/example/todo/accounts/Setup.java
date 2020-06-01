package org.example.todo.accounts;

import lombok.extern.slf4j.Slf4j;
import org.example.todo.accounts.model.Login;
import org.example.todo.accounts.model.Membership;
import org.example.todo.accounts.model.User;
import org.example.todo.accounts.model.UserProfile;
import org.example.todo.accounts.model.Workspace;
import org.example.todo.accounts.repository.MembershipRepository;
import org.example.todo.accounts.repository.UserRepository;
import org.example.todo.accounts.repository.WorkspaceRepository;
import org.example.todo.common.util.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
@Profile("local")
public class Setup {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MembershipRepository membershipRepository;

	@Bean
	@Transactional
	public CommandLineRunner demo(UserRepository repository, WorkspaceRepository workspaceRepository) {
		return args -> {

			UUID workspaceUuid = null;
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
						.status(Status.ACTIVE)
						.build();

				UserProfile userProfile = UserProfile.builder()
						.firstName("John")
						.lastName("Doe")
						.email("jdoe@gmail.com")
						.build();

				Login login = Login.builder()
						.username(userProfile.getFirstName().substring(0,1)
								.concat(userProfile.getLastName())
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
				user.setUserProfile(userProfile);

				for (Membership memship : memberships) {
					memship.setWorkspace(workspace);
					memship.setUser(user);
				}

				Workspace testingObject = workspaceRepository.save(workspace);
				repository.saveAndFlush(user);

				workspaceUuid = testingObject.getUuid();


				repository.save(User.builder()
						.status(Status.ACTIVE)
						.userProfile(UserProfile.builder()
								.firstName("Chloe")
								.lastName("O'Brian")
								.email("test@gmail.com")
								.build())
						.build());

				repository.save(User.builder()
						.status(Status.ACTIVE)
						.userProfile(UserProfile.builder()
								.firstName("Kim")
								.lastName("Bauer")
								.email("test1@gmail.com")
								.build())
						.build());

				Login login2 = Login.builder()
						.username("dpalmer")
						.passwordHash(passwordEncoder.encode("test"))
						.build();

				UserProfile userProfile2 = UserProfile.builder()
						.firstName("David")
						.lastName("Palmer")
						.email("test2@gmail.com")
						.build();



				User user2 = User.builder()
						.userProfile(userProfile2)
						.status(Status.ACTIVE)
						.login(login2)
						.build();

				repository.save(user2);

				repository.save(User.builder()
						.status(Status.ACTIVE)
						.userProfile(UserProfile.builder()
								.firstName("Michelle")
								.lastName("Dessler")
								.email("test3@gmail.com")
								.build())
						.build());
			}
			catch (DataIntegrityViolationException e) {
				log.error("Failed to add duplicate data");
				throw e;
			}
			catch (Exception e) {
				log.error("Failed to fully add test data.", e);
				throw e;
			}

			// fetch all customers
			log.info("Users found with findAll():");
			log.info("-------------------------------");
			List<User> users = repository.findAll();
			log.debug("{}", users);
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
			findByLastName("Doe");

			log.info("USERS IN WORKSPACE {}:\n {}", workspaceUuid, userRepository.findDistinctByMemberships_workspaceUuid(workspaceUuid));
		};
	}

	public void findByLastName(String lastName) {
		// fetch customers by last name
		log.info("User found with findByLastName('Doe'):");
		log.info("--------------------------------------------");
		userRepository.findByUserProfile_LastName(lastName).forEach(user -> log.info(user.toString()));
		log.info("");
	}
}
