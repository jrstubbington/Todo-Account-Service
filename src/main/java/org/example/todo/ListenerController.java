package org.example.todo;

import org.example.todo.user.User;
import org.example.todo.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ListenerController {

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/greeting")
	@Transactional(readOnly = true)
	public ResponseEntity<ResponseContainer<User>> greeting() {
		List<User> users = userRepository.findAll();
		return ResponseEntity.ok(new ResponseContainer<User>("OK", null, users));
	}
}
