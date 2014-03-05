package com.example.howler.WebRequest;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class LoginRegisterRequest extends SpringAndroidSpiceRequest<User> {

	private User user;
	
	public LoginRegisterRequest(User user) {
		super(User.class);
		this.user = user;
	}

	@Override
	public User loadDataFromNetwork() throws Exception {
		return getRestTemplate().postForObject(JsonSpiceService.baseURL, this.user, User.class);
	}

}
