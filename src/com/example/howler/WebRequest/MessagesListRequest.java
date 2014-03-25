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

import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class MessagesListRequest extends SpringAndroidSpiceRequest<MessageListObject> {

	private static final String TAG = "Messages List Request";
	private MessageListObject messageList;
	
	public MessagesListRequest(MessageListObject messageList) {
		super(MessageListObject.class);
		this.messageList = messageList;
	}
	
	@Override
	public MessageListObject loadDataFromNetwork() throws Exception {
		String url = JsonSpiceService.baseURL + "messages";
		
		MappingJacksonHttpMessageConverter converter = new MappingJacksonHttpMessageConverter();
		//converter.getObjectMapper().configure(Feature.UNWRAP_ROOT_VALUE, true);
		
				
		ClientHttpRequestFactory fac = new HttpComponentsClientHttpRequestFactory() {

		    @Override
		    protected HttpUriRequest createHttpRequest(HttpMethod httpMethod, URI uri) {
		        HttpUriRequest uriRequest = super.createHttpRequest(httpMethod, uri);
		        // Add request headers
		        uriRequest.addHeader(
		                "Content-Type",
		                MediaType.APPLICATION_JSON_VALUE);
		        uriRequest.addHeader("Authorization", "RHzITQk2rltEjUjx1B813I0zTJ3gaqIemq6G7PJ4W7FBuyMHYK");
		        return uriRequest;
		    }

		    @Override
		    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod)
		            throws IOException {
		            Log.d(TAG, uri.toString());
		        return super.createRequest(uri, httpMethod);
		    }
		};
		
		RestTemplate restTemplate = new RestTemplate(fac);
		restTemplate.getMessageConverters().add(converter);
		return restTemplate.getForObject(url, MessageListObject.class);
		//restTemplate.set
		
		
		//return getRestTemplate().getForObject(url, MessageListObject.class);
	}
	


}
