package com.example.howler;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MessagesList extends Activity {
	
	private List<String> messageList;
	private LinearLayout main;
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
	}
	public void PopulateMessageList(){
		File audioPath = new File(Environment.getDataDirectory().getAbsolutePath() + "/data/com.example.howler/");
		messageList = Arrays.asList(audioPath.list(
				new FilenameFilter(){
					public boolean accept(File audioPath, String audioName){
						return audioName.endsWith(".3gp");
					}
				}
			));
		Toast.makeText(MessagesList.this, "Messages that exist:  " + messageList, Toast.LENGTH_LONG).show();
	}
	public void DisplayMessageList(){
		main =(LinearLayout) findViewById(R.id.messages);
		for(final String message : messageList){
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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

}
