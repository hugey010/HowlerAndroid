package com.example.howler.WebRequest;

public class MessageDownload {
	
	private byte[] data;
	private boolean success;
	
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}

}
