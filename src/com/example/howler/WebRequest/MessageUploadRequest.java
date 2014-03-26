package com.example.howler.WebRequest;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import com.example.howler.DatabaseHelper;
import com.example.howler.RecorderActivity;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

public class MessageUploadRequest extends SpringAndroidSpiceRequest<String>  {
	private static final String TAG = "MessageUploadRequest";
	private DatabaseHelper db;
	private Message message;
	
	public MessageUploadRequest(DatabaseHelper database, Message message) {
		super(String.class);
		this.db = database;
		this.message = message;
	}

	@Override
	public String loadDataFromNetwork() throws Exception {
		String url = JsonSpiceService.baseURL + "message";
		
	    MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
	    parts.add("message_id", message.getMessage_id());
	    parts.add("data", new FileSystemResource(RecorderActivity.filePath()));
	    
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
	    headers.add("Authorization", db.authToken());	    
	    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(parts, headers);
	    
		RestTemplate restTemplate = new RestTemplate(true);
	    return restTemplate.postForObject(url, request, String.class);
	}

	
	
}
