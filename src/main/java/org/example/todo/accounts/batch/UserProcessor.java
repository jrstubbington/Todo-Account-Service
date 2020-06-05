package org.example.todo.accounts.batch;

import lombok.extern.slf4j.Slf4j;
import org.example.todo.accounts.model.User;
import org.example.todo.accounts.model.UserProfile;
import org.example.todo.accounts.repository.UserRepository;
import org.example.todo.common.dto.UserDto;
import org.example.todo.common.dto.UserProfileDto;
import org.example.todo.common.util.Status;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class UserProcessor implements ItemProcessor<UserDto, User> {

	@Autowired //TODO: Create autowired setter
	private UserRepository userRepository;

	@Override
	public User process(UserDto item) throws Exception {
		log.info("Processing User information: {}", item);
		User user = new User();
		UserProfileDto userProfileDto = item.getUserProfile();

		//TODO: Verify user doesn't already exist or needs update
		//TODO: Add functionality for creating temporary logins
		//TODO: Add functionality for assigning to workspace(s) with role(s)

		if (userRepository.existsByUserProfile_Email(userProfileDto.getEmail())) {
			log.warn("User with email {} already exists", userProfileDto.getEmail());
			//User already exists... or there's an email conflict :(
			//NOTE: there should be some limitation on who can edit who's profile, memberships, roles.
			// Without this, in theory anyone could edit any other user's data.

			//To avoid issues, remove users that already exist from being processed
			return null;
		}

		UserProfile userProfile = UserProfile.builder()
				.firstName(userProfileDto.getFirstName())
				.lastName(userProfileDto.getLastName())
				.email(userProfileDto.getEmail().toLowerCase())
				.build();
		user.setUserProfile(userProfile);
		user.setStatus(Status.ACTIVE);
		return user;
	}
}
