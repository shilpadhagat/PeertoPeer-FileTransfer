package assignment.exchange.dao;

import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	// host:port,server or host:port,client
	private String from;
	// Different types of messages such as register, query
	private String type;
	// Message format for each type of message will be different.
	private String message;
	
	public Message(){}
	
	public Message(String from, String type, String message) {
		this.from = from;
		this.type = type;
		this.message = message;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "Message [from=" + from + ", type=" + type + ", message=" + message + "]";
	}
}
