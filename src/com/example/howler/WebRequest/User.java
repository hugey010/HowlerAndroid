package com.example.howler.WebRequest;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
	
	private static final int MIN_INPUT_LENGTH = 3;
	private static final int MAX_INPUT_LENGTH = 40;
	
	private String username;
	private String identifier;
	private String password;
	private String tempPassword;
	private String authtoken;
	private String email;
	
	private boolean pending;
	private String message;
	
	public boolean validLogin() {
		return username.length() > MIN_INPUT_LENGTH && 
				username.length() < MAX_INPUT_LENGTH &&
				password.length() > MIN_INPUT_LENGTH &&
				password.length() < MAX_INPUT_LENGTH;
	}
	
	public boolean validRegistration() {
		return validLogin() &&
				email.length() > MIN_INPUT_LENGTH &&
				email.length() < MAX_INPUT_LENGTH;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getTempPassword() {
		return tempPassword;
	}
	public void setTempPassword(String tempPassword) {
		this.tempPassword = tempPassword;
	}
	public String getAuthtoken() {
		return authtoken;
	}
	public void setAuthtoken(String authtoken) {
		this.authtoken = authtoken;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isPending() {
		return pending;
	}
	public void setPending(boolean pending) {
		this.pending = pending;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
