package edu.missouristate.dto;

public class LoginResponse {

	private boolean loggedIn;
	private String message;
	private String messageType;
	private String username;
	private String firstName;
	private String lastName;
	private int centralLoginId;
	
	//TODO: GET THIS OUT OF HERE. Or at least encrypt it
	public int getCentralLoginId() {
		return centralLoginId;
	}

	public void setCentralLoginId(int centralLoginId) {
		this.centralLoginId = centralLoginId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public LoginResponse() {
		super();
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	
	public boolean isLoggedIn() {
		return loggedIn;
	}
	
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessageType() {
		return messageType;
	}
	
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	
}
