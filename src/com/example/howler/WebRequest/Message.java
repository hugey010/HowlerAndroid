package com.example.howler.WebRequest;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
	
	
	private String message_id;
	private Boolean read;
	private String timestamp;
	private String title;
	private String username;
	private double volume;
	
	// for message creation and uploading
	private byte[] data;
	private List<String> usernames;
	
	public static class MList {
		
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
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
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
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public List<String> getUsernames() {
		return usernames;
	}
	public void setUsernames(List<String> usernames) {
		this.usernames = usernames;
	}

}
