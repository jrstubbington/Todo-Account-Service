package org.example.todo.accounts.batch;

import lombok.extern.slf4j.Slf4j;
import org.example.todo.accounts.model.User;
import org.example.todo.accounts.model.UserProfile;
import org.example.todo.accounts.repository.UserProfileRepository;
import org.example.todo.common.dto.UserDto;
import org.example.todo.common.dto.UserProfileDto;
import org.example.todo.common.util.Status;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.validation.ConstraintViolation;
import java.util.Set;

@Slf4j
public class UserProcessor implements ItemProcessor<UserDto, User> {

	@Autowired //TODO: Create autowired setter
	private UserProfileRepository userProfileRepository;

	@Autowired
	private SpringValidatorAdapter validator;

	@Override
	public User process(UserDto item) throws Exception {
		log.info("Processing User information: {}", item);
		User user = new User();
		UserProfileDto userProfileDto = item.getUserProfile();

		//TODO: Verify user doesn't already exist or needs update
		//TODO: Add functionality for creating temporary logins
		//TODO: Add functionality for assigning to workspace(s) with role(s)

		//Instead of doing this, push everything into a staging table and execute updates/inserts via a stored procedure
		if (userProfileRepository.existsByEmail(userProfileDto.getEmail())) {
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

		Set<ConstraintViolation<User>> userViolations = validator.validate(user);
		Set<ConstraintViolation<UserProfile>> profileViolations = validator.validate(userProfile);
//		log.info("{} violoations", userViolations.size() + profileViolations.size());
		if (!userViolations.isEmpty() || !profileViolations.isEmpty()) {
			return null;
		}
		return user;
	}
}
