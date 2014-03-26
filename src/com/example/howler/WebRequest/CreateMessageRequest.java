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

import com.example.howler.DatabaseHelper;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class CreateMessageRequest extends SpringAndroidSpiceRequest<Message> {
	private static final String TAG = "MessageUploadRequest";
	private Message message;
	private DatabaseHelper db;
	
	public CreateMessageRequest(DatabaseHelper database, Message message) {
		super(Message.class);
		this.message = message;
		this.db = database;

	}

	@Override
	public Message loadDataFromNetwork() throws Exception {
		String url = JsonSpiceService.baseURL + "createMessage";
		
		ClientHttpRequestFactory fac = new HttpComponentsClientHttpRequestFactory() {

		    @Override
		    protected HttpUriRequest createHttpRequest(HttpMethod httpMethod, URI uri) {
		        HttpUriRequest uriRequest = super.createHttpRequest(httpMethod, uri);
		        // Add request headers
		        uriRequest.addHeader(
		                "Content-Type",
		                MediaType.APPLICATION_JSON_VALUE);
		        uriRequest.addHeader("Authorization", db.authToken());
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
		return restTemplate.postForObject(url, this.message, Message.class);
	}

}
