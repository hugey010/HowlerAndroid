package com.example.howler.WebRequest;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
	
	
	private String message_id;
	private Boolean read;
	private double timestamp;
	private String title;
	private String username;
	private double volume;
	
	public static class List {
		
		private boolean success;
		
		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}		
        private static final long serialVersionUID = 6836514467436078182L;
		
		private ArrayList<Message> messages;
		
		public ArrayList<Message> getMessages() {
			return messages;
		}
		
		public void setMessages(ArrayList<Message> messages) {
			this.messages = messages;
		}
		
		public static long getSerialversionuid() {
			return serialVersionUID;
		}
	}
	
	public String getMessage_id() {
		return message_id;
	}
	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}
	public Boolean getRead() {
		return read;
	}
	public void setRead(Boolean read) {
		this.read = read;
	}
	public double getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(double timestamp) {
		this.timestamp = timestamp;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public double getVolume() {
		return volume;
	}
	public void setVolume(double volume) {
		this.volume = volume;
	}

}
