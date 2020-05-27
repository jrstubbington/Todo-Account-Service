package org.example.todo.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
@ToString
@Slf4j
public class ErrorDetails {

	@EqualsAndHashCode.Exclude
	private final OffsetDateTime timestamp;
	private final String message;
	private final List<String> details;
	//Verbose, plain language description of the problem for the app developer with hints about how to fix it.
/*	private final String developerMessage =""
	//Pass this message on ot the app user, if needed.
	private final String userMessage = ""*/
	//Error Code
	private final String requestInformation;
	private final String errorCode;
	//Link to more info
	private final String moreInfo;

	public ErrorDetails(OffsetDateTime timestamp, String message, List<String> details, String requestInformation) {
		this.timestamp = timestamp;
		this.message = message;
		this.details = details;
		this.errorCode = "12345";
		this.moreInfo = "http://ourDocumentationWebsite.org/docs/errors/12345";
		this.requestInformation = requestInformation;

	}
}
