package com.example.howler.WebRequest;

import java.util.List;

public class FriendListObject {

	private List<Friend> friends;
	private boolean success;
	
	public List<Friend> getFriends() {
		return friends;
	}
	public void setFriends(List<Friend> friends) {
		this.friends = friends;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
