package com.example.howler;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;


public class RecorderActivity extends FragmentActivity implements OnClickListener {
	private EditText titleEditableField;
	private MediaRecorder voiceRecorder;
	private MediaPlayer voicePlayer;
	private String audioFile;
	private boolean isRecording = false;
	private View btnPlay;
	private View btnRecord;
	private View btnDelete;
	private View btnFriendsList;
	private View btnMessagesList;
	final Context context = this;
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
		}

		@Override
		public void afterTextChanged(Editable editable) {
			// check Fields For Empty Values
			checkFieldsForEmptyValues();
		}

	};
	
	//check if titleEditableField has string length > 0, if yes enable record, play, and delete button.
	void checkFieldsForEmptyValues(){
		Button playButton = (Button) findViewById(R.id.play_button);
		ImageButton recordButton = (ImageButton) findViewById(R.id.record_button);
		Button deleteButton = (Button) findViewById(R.id.delete_button);

		String title = titleEditableField.getText().toString();

		if(title.length()>0){
			playButton.setEnabled(true);
			recordButton.setEnabled(true);
			deleteButton.setEnabled(true);

		} else {
			playButton.setEnabled(false);
			recordButton.setEnabled(false);
			deleteButton.setEnabled(false);
		}
	}		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		setContentView(R.layout.activity_recorder);
		btnRecord = (ImageButton) findViewById(R.id.record_button);
		btnRecord.setOnClickListener(this);
		btnFriendsList = (Button) findViewById(R.id.friends_button);
		btnFriendsList.setOnClickListener(this);
		btnMessagesList = (Button) findViewById(R.id.message_list_button);
		btnMessagesList.setOnClickListener(this);
		btnPlay = (Button) findViewById(R.id.play_button);
		btnPlay.setOnClickListener(this);
		btnDelete = (Button) findViewById(R.id.delete_button);
		btnDelete.setOnClickListener(this);
		titleEditableField = (EditText) findViewById(R.id.enter_title);
		titleEditableField.addTextChangedListener(mTextWatcher);
		
		checkFieldsForEmptyValues();			
		
		// setup friends and messages fragments
//		if (findViewById(R.id.friends_list_container) != null) {
//            if (savedInstanceState != null) {
//                return;
//            }
//
//            FriendsListFragment friendFragment = new FriendsListFragment();
//            friendFragment.setArguments(getIntent().getExtras());
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.friends_list_container, friendFragment).commit();
//		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recorder, menu);
		return true;
	}
	private void startRecording(){
        try{
        	voiceRecorder = new MediaRecorder();
            String title = this.titleEditableField.getText().toString();
            audioFile = Environment.getDataDirectory().getAbsolutePath() + "/data/com.example.howler/" + title + ".mp4";
    		Toast.makeText(RecorderActivity.this, "recording file in: " + audioFile, Toast.LENGTH_LONG).show();	
        	voiceRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        	voiceRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        	voiceRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        	voiceRecorder.setOutputFile(audioFile);
        	voiceRecorder.prepare();
        	voiceRecorder.start();
        	title = null;
        } catch(IOException e){
        	e.printStackTrace();
        }
	}
	
	private void stopRecording(){
		Toast.makeText(RecorderActivity.this, "stopped recording", Toast.LENGTH_LONG).show();
		voiceRecorder.stop();
		voiceRecorder.release();
		voiceRecorder = null;
		btnPlay.setClickable(true);
		btnDelete.setClickable(true);
		btnRecord.setClickable(false);
	}
	
	private void playRecording() throws Exception{
		voicePlayer = new MediaPlayer();
		Toast.makeText(RecorderActivity.this, "started playing file at" + audioFile, Toast.LENGTH_LONG).show();
		voicePlayer.setDataSource(audioFile);
		voicePlayer.prepare();
		voicePlayer.start();	
	}
	private void deleteRecording(){
		//to have a dialog pop-up when trying to delete a null audiofile
//		if (audioFile == null) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(context);
//			builder.setTitle("Error");
//			builder.setMessage("No audio file to delete.");
//			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//				
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.cancel();
//					titleEditableField.requestFocus();
//				}
//			});
//			AlertDialog alert = builder.create();
//			alert.show();			
//		}
//		else {
			Toast.makeText(RecorderActivity.this, audioFile + "deleted", Toast.LENGTH_LONG).show();
			File outFile = new File(audioFile);
			outFile.delete();
			audioFile = null;
			btnPlay.setClickable(false);
			btnDelete.setClickable(false);
			btnRecord.setClickable(true);
			titleEditableField.setText("");
		}
//	}
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.friends_button:
    		Intent i = new Intent(getApplicationContext(), com.example.howler.FriendsList.class);
    		startActivity(i);				
			break;
		case R.id.message_list_button:
    		Intent i2 = new Intent(getApplicationContext(), com.example.howler.MessagesList.class);
    		startActivity(i2);			
			break;
	    case R.id.record_button:
//	    	if(titleEditableField.length()==0)
//	    	{
//				AlertDialog.Builder builder = new AlertDialog.Builder(context);
//				builder.setTitle("Error");
//				builder.setMessage("Please enter a title first.");
//				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.cancel();
//						titleEditableField.requestFocus();
//					}
//				});
//				AlertDialog alert = builder.create();
//				alert.show();
//				break;
//			}	    
//	    	else
//	    	{
		    	isRecording = !isRecording;	    		
	    		if(isRecording){
	    			startRecording();
	    			break;
	    		}
	    		else {
	    			stopRecording();
	    			break;
	    		}
//	    	}
	    case R.id.play_button:
	    	try {
				playRecording();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	break;
		case R.id.delete_button:
			try {
				deleteRecording();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}
}
