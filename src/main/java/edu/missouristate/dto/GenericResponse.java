package edu.missouristate.dto;

public class GenericResponse {
	private String message;
	private String messageType;
	
	public GenericResponse() {
		super();
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
	
	@Override
	public String toString() {
		return "GenericResponse [message=" + message + ", messageType=" + messageType + "]";
	}
	
}
