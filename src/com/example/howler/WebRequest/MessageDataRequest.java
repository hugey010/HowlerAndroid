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

public class MessageDataRequest extends SpringAndroidSpiceRequest<MessageDownload> {
	
	private DatabaseHelper db;
	private String messageId;
	private static final String TAG = "MessageDataRequest";
	
	public MessageDataRequest(DatabaseHelper database, String message_id) {
		super(MessageDownload.class);
		db = database;
		messageId = message_id;
	}

	@Override
	public MessageDownload loadDataFromNetwork() throws Exception {
		String url = JsonSpiceService.baseURL + "message" + "?message_id=" + messageId;
		
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
		return restTemplate.getForObject(url, MessageDownload.class);			
	}

}
