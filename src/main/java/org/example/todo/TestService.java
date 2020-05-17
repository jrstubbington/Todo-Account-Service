package org.example.todo;

import lombok.extern.slf4j.Slf4j;
import org.example.todo.logins.Login;
import org.example.todo.logins.LoginRepository;
import org.example.todo.memberships.Membership;
import org.example.todo.status.Status;
import org.example.todo.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@EnableScheduling
public class TestService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LoginRepository loginRepository;


	@Transactional
	@Scheduled(initialDelay = 1000, fixedDelay = 1000000)
	public void test() {
		userRepository.findByStatus(Status.DELETED).forEach(user -> {
			log.info(user.toString());
			log.info(user.getLogin().toString());
			Set<Long> roles = user.getMemberships().stream().map(Membership::getRoleId).collect(Collectors.toSet());

			Login login = user.getLogin();
			user.setLogin(null);
			loginRepository.delete(login);
			userRepository.save(user);
		});
	}
}
