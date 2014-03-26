package com.example.howler.WebRequest;

import java.util.List;

public class MessageListObject {

	private List<Message> messages;
	private boolean success;

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
