package com.example.howler;

import com.example.howler.WebRequest.Message;
import com.example.howler.WebRequest.MessageListObject;
import com.example.howler.WebRequest.MessagesListRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.example.howler.WebRequest.JsonSpiceService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MessagesList extends Activity {

	private static final String TAG = "Messages List Activity";
	protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
	
	@Override
	public void onStop() {
		super.onStop();
		spiceManager.shouldStop();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		 spiceManager.start(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		setContentView(R.layout.activity_messages_list);
		
		//get the message button and make it not pressable
		Button messageButton = (Button) findViewById(R.id.message_list_button);
		messageButton.setEnabled(false);	
		
		//make the Back button go back to Recorder
		Button backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), com.example.howler.RecorderActivity.class);
				startActivity(i);		
			}
		});
		
		// send request
		MessagesList.this.setProgressBarIndeterminateVisibility(true);
		MessageListObject messageList = new MessageListObject();
		MessagesListRequest request = new MessagesListRequest(messageList);
		spiceManager.execute(request, new MessageListRequestListener());
		spiceManager.execute(request, messageList, DurationInMillis.ALWAYS_EXPIRED, new MessageListRequestListener());
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.messages_list, menu);
		
		return true;
	}
	
	private class MessageListRequestListener implements RequestListener<MessageListObject> {

		@Override
		public void onRequestFailure(SpiceException exception) {
			Log.e(TAG, "failure" + exception.toString());
			
			
		}

		@Override
		public void onRequestSuccess(MessageListObject messages) {
			
			
			Log.d(TAG, "success, number of messages: " + messages.getMessages().size());

			
		}
		
	}

}
