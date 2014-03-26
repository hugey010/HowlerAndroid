package com.example.howler.WebRequest;

import java.util.ArrayList;

public class Friend {
	
	private String identifier;
	private boolean pending;
	private String username;
	
	public static class List {
        private static final long serialVersionUID = 124123123164624L;
		private ArrayList<Friend> friends;
		private boolean success;
		
		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}		

		public ArrayList<Friend> getFriends() {
			return friends;
		}
		
		public void setFriends(ArrayList<Friend> friends) {
			this.friends = friends;
		}
		
		public static long getSerialversionuid() {
			return serialVersionUID;
		}
	}
	
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public boolean isPending() {
		return pending;
	}
	public void setPending(boolean pending) {
		this.pending = pending;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
}
