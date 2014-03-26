package com.example.howler;


import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import com.example.howler.WebRequest.Message;
import com.example.howler.WebRequest.MessagesListRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.example.howler.WebRequest.JsonSpiceService;


import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MessagesList extends Activity {

	
	private List<String> messageList;
	private LinearLayout main;
	private DatabaseHelper dh;


	private static final String TAG = "Messages List Activity";
	protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
	
	@Override
	public void onStop() {
		spiceManager.shouldStop();
		super.onStop();
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
		this.PopulateMessageList();
		this.DisplayMessageList();
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
		
		dh = new DatabaseHelper(this.getApplicationContext());
		
		// send request
		MessagesList.this.setProgressBarIndeterminateVisibility(true);
		MessagesListRequest request = new MessagesListRequest(dh.authToken());
		spiceManager.execute(request, new MessageListRequestListener());
		//spiceManager.execute(request, Message.List, DurationInMillis.ALWAYS_EXPIRED, new MessageListRequestListener());
	
	}
	public void PopulateMessageList(){
		File audioPath = new File(Environment.getDataDirectory().getAbsolutePath() + "/data/com.example.howler/");
		messageList = Arrays.asList(audioPath.list(
				new FilenameFilter(){
					public boolean accept(File audioPath, String audioName){
						return audioName.endsWith(".mp4");
					}
				}
			));
		Toast.makeText(MessagesList.this, "Messages that exist:  " + messageList, Toast.LENGTH_LONG).show();
	}
	public void DisplayMessageList(){
		main =(LinearLayout) findViewById(R.id.messages);
		for(final String message : messageList){
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			LinearLayout layout = new LinearLayout(getApplicationContext());
			LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setLayoutParams(params);
			Button btnMessage = new Button(getApplicationContext());
			btnMessage.setLayoutParams(buttonParams);
			btnMessage.setText(message);
			
			layout.addView(btnMessage);
			main.addView(layout);
			
		}	
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.messages_list, menu);
		
		return true;
	}
	
	private class MessageListRequestListener implements RequestListener<Message.List> {

		@Override
		public void onRequestFailure(SpiceException exception) {
			Log.e(TAG, "failure" + exception.getMessage());
			
			
		}

		@Override
		public void onRequestSuccess(Message.List messages) {
			
			
			Log.d(TAG, "success, number of messages: " + messages.getMessages().size() + " msss: " + messages.getMessages().get(0).getTitle());	
			
		}
		
	}

}
