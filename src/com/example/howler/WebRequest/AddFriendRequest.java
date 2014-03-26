package com.example.howler.WebRequest;

import java.io.IOException;
import java.net.URI;

import org.apache.http.client.methods.HttpUriRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.util.Log;

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class AddFriendRequest extends SpringAndroidSpiceRequest<ErrorResponseObject> {
private static final String TAG = "Friends List Request";
	
	private Username username;
	private String auth_token;
	
	public AddFriendRequest(Username username, String auth_token) {
		super(ErrorResponseObject.class);
		this.username = username;
		this.auth_token = auth_token;
	}

	@Override
	public ErrorResponseObject loadDataFromNetwork() throws Exception {
		String url = JsonSpiceService.baseURL + "addFriend";

		ClientHttpRequestFactory fac = new HttpComponentsClientHttpRequestFactory() {

			@Override
			protected HttpUriRequest createHttpRequest(HttpMethod httpMethod, URI uri) {
				HttpUriRequest uriRequest = super.createHttpRequest(httpMethod, uri);
				// Add request headers
				uriRequest.addHeader(
						"Content-Type",
						MediaType.APPLICATION_JSON_VALUE);
				uriRequest.addHeader("Authorization", auth_token);
				return uriRequest;
			}

			@Override
			public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod)
					throws IOException {
				Log.d(TAG, uri.toString());
				return super.createRequest(uri, httpMethod);
			}
		};

		RestTemplate restTemplate = getRestTemplate();
		restTemplate.setRequestFactory(fac);
		return restTemplate.postForObject(url, this.username, ErrorResponseObject.class);
	}
}
