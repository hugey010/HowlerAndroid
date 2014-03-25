package com.example.howler.WebRequest;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class MessagesListRequest extends SpringAndroidSpiceRequest<Message> {

	private Message message;
	
	public MessagesListRequest(Message message) {
		super(Message.class);
		this.message = message;
	}
	
	@Override
	public Message loadDataFromNetwork() throws Exception {
		String url = JsonSpiceService.baseURL + "messages";
		return getRestTemplate().postForObject(url, this.message, Message.class);
	}

}
