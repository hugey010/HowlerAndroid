package com.example.howler;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		setContentView(R.layout.activity_recorder);
		btnRecord = (ImageButton) findViewById(R.id.record_button);
		btnRecord.setOnClickListener(this);
		btnPlay = (Button) findViewById(R.id.play_button);
		btnPlay.setOnClickListener(this);
		btnDelete = (Button) findViewById(R.id.delete_button);
		btnDelete.setOnClickListener(this);
		titleEditableField = (EditText) findViewById(R.id.enter_title);
		// setup friends and messages fragments
		if (findViewById(R.id.friends_list_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            FriendsListFragment friendFragment = new FriendsListFragment();
            friendFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.friends_list_container, friendFragment).commit();
		}
		
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
            audioFile = Environment.getDataDirectory().getAbsolutePath() + "/data/com.example.howler/" + title + ".3gp";
    		Toast.makeText(RecorderActivity.this, "recording file in: " + audioFile, Toast.LENGTH_LONG).show();	
        	voiceRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        	voiceRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
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
		Toast.makeText(RecorderActivity.this, audioFile + "deleted", Toast.LENGTH_LONG).show();
		File outFile = new File(audioFile);
		outFile.delete();
		audioFile = null;
		btnPlay.setClickable(false);
		btnDelete.setClickable(false);
		btnRecord.setClickable(true);
		titleEditableField.setText("");
	}
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.friends_button:
			
			break;
		case R.id.message_list_button:
			
			
			break;
	    case R.id.record_button:
	    	isRecording = !isRecording;
	    	if(isRecording){
	            startRecording();
	            break;
	    	}
			else {
				stopRecording();
				break;
			}
	    case R.id.play_button:
	    	try {
				playRecording();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	break;
		case R.id.delete_button:
			deleteRecording();
		}	
	}
}
