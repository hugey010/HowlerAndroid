package com.example.howler.WebRequest;

import java.io.IOException;
import java.net.URI;

import org.apache.http.client.methods.HttpUriRequest;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.util.Log;

import com.example.howler.DatabaseHelper;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class MessagesListRequest extends SpringAndroidSpiceRequest<Message.List> {

	private static final String TAG = "Messages List Request";
	private String authToken;
	
	public MessagesListRequest(String authToken) {
		super(Message.List.class);
		this.authToken = authToken;
	}
	
	@Override
	public Message.List loadDataFromNetwork() throws Exception {
		String url = JsonSpiceService.baseURL + "messages";
		
		/*
		MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();
		//converter.getObjectMapper().configure(Feature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
		//converter.getObjectMapper().configure(Feature.UNWRAP_ROOT_VALUE, true);
		
		converter.getObjectMapper().configure(Feature.AUTO_DETECT_SETTERS, true);
		converter.getObjectMapper().configure(Feature.AUTO_DETECT_FIELDS, true);
			*/
		ClientHttpRequestFactory fac = new HttpComponentsClientHttpRequestFactory() {

		    @Override
		    protected HttpUriRequest createHttpRequest(HttpMethod httpMethod, URI uri) {
		        HttpUriRequest uriRequest = super.createHttpRequest(httpMethod, uri);
		        // Add request headers
		        uriRequest.addHeader(
		                "Content-Type",
		                MediaType.APPLICATION_JSON_VALUE);
		        uriRequest.addHeader("Authorization", "VZ3z5rELK0FS91mqUWuwBn4fs7tceMWhfASuojf3wLOCH862yY");
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
		//restTemplate.getMessageConverters().add(converter);
		return restTemplate.getForObject(url, Message.List.class);
		//restTemplate.set
		
		
		//return getRestTemplate().getForObject(url, MessageListObject.class);
	}
	


}
