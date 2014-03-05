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
		String url = JsonSpiceService.baseURL + "users";
		return getRestTemplate().postForObject(url, this.user, User.class);
	}

}
