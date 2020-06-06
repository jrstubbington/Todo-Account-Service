package org.example.todo.accounts.batch;

import lombok.extern.slf4j.Slf4j;
import org.example.todo.common.dto.UserDto;
import org.example.todo.common.dto.UserProfileDto;
import org.example.todo.common.util.Status;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;

@Slf4j
public class UserExcelRowMapper implements RowMapper<UserDto>  {

	@Override
	public UserDto mapRow(RowSet rowSet) {
		UserDto user = new UserDto();

		UserProfileDto userProfileDto = new UserProfileDto();
		userProfileDto.setFirstName(rowSet.getColumnValue(0));
		userProfileDto.setLastName(rowSet.getColumnValue(1));
		userProfileDto.setEmail(rowSet.getColumnValue(2));

		//TODO: Add functionality for creating temporary logins
		//TODO: Add functionality for assigning to workspace(s) with role(s)

		user.setUserProfile(userProfileDto);
		user.setStatus(Status.ACTIVE); //TODO: Set inactive and require login creation to become active
		return user;
	}
}
