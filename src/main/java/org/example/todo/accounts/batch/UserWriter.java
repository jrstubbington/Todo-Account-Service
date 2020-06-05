package org.example.todo.accounts.batch;

import lombok.extern.slf4j.Slf4j;
import org.example.todo.accounts.model.User;
import org.example.todo.accounts.repository.UserRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class UserWriter implements ItemWriter<User> {

	@Autowired //TODO: Create autowired setter
	private UserRepository userRepository;

	@Override
	public void write(List<? extends User> users) throws Exception {
		log.info("Received the information of {} users", users.size());

		users.forEach(i -> log.debug("Received the information of a user: {}", i));


		userRepository.saveAll(users);
	}
}
