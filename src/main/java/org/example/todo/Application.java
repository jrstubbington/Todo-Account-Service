package org.example.todo;

import lombok.extern.slf4j.Slf4j;
import org.example.todo.accounts.Account;
import org.example.todo.accounts.AccountRepository;
import org.example.todo.logins.Login;
import org.example.todo.memberships.Membership;
import org.example.todo.status.Status;
import org.example.todo.user.User;
import org.example.todo.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SpringBootApplication
@Slf4j
@EnableTransactionManagement
public class Application {

	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	public CommandLineRunner demo(UserRepository repository, AccountRepository accountRepository) {
		return args -> {

			try {
				Account account = Account.builder()
						.name("Test Account")
						.accountType(1)
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

				account.setMemberships(memberships);
				user.setMemberships(memberships);
				user.setLogin(login);
				login.setUser(user);

				for (Membership memship : memberships) {
					memship.setAccount(account);
					memship.setUser(user);
				}


				accountRepository.saveAndFlush(account);
				repository.saveAndFlush(user);


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
			catch (Exception e) {
				log.error("Failed to fully add test users.", e);
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
