package com.example.howler;

import com.example.howler.WebRequest.MessageDataRequest;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.util.support.Base64;
import org.springframework.web.client.HttpClientErrorException;

import com.example.howler.WebRequest.Message;
import com.example.howler.WebRequest.MessageDataRequest;
import com.example.howler.WebRequest.MessageDownload;
import com.example.howler.WebRequest.MessagesListRequest;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.example.howler.WebRequest.JsonSpiceService;



import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MessagesList extends Activity {

	
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
		this.DisplayInitialMessageList();
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
		MessagesListRequest request = new MessagesListRequest(dh);
		spiceManager.execute(request, new MessageListRequestListener());
	
	}
	/*
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
	*/
	public void DisplayInitialMessageList() {
		
	}
	
	public void DisplayMessageList(List<Message> messagesList){
		main =(LinearLayout) findViewById(R.id.messages);
		for(final Message m : messagesList){
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			LinearLayout layout = new LinearLayout(getApplicationContext());
			LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setLayoutParams(params);
			Button btnMessage = new Button(getApplicationContext());
			btnMessage.setLayoutParams(buttonParams);
			btnMessage.setText(m.getTitle());
			btnMessage.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					playBack(m);
				}
			});
			layout.addView(btnMessage);
			main.addView(layout);		
		}	
	}
	
	public void playBack(Message m){
		Toast.makeText(MessagesList.this, "message trying to play is:" + m.getUsername() + m.getMessage_id(), Toast.LENGTH_LONG).show();
		//AlertDialog.Builder builder = new AlertDialog.Builder(getApplication());
		//builder.setMessage("MESSAGE:" + message)
		//		.setTitle("TITLE");
		//AlertDialog dialog = builder.create();
		//dialog.show();
		MessagesList.this.setProgressBarIndeterminateVisibility(true);
		MessageDataRequest request = new MessageDataRequest(dh, m.getMessage_id());
		spiceManager.execute(request, new MessageDownloadRequestListener());
	
			
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.messages_list, menu);
		
		return true;
	}
	
	private class MessageDownloadRequestListener implements RequestListener<MessageDownload> {
		
		@Override
		public void onRequestFailure(SpiceException exception) {
			Log.e(TAG, "message download failure" + exception.getMessage());

			// detect invalid auth
			if (exception.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException e = (HttpClientErrorException)exception.getCause();
				if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
					dh.clearPesistentUser();
					Intent intent = new Intent(MessagesList.this, LoginActivity.class);
					MessagesList.this.startActivity(intent);
					finish();
				}
			}
		}

		@Override
		public void onRequestSuccess(MessageDownload download) {

			Log.d(TAG, "success messages data length: " + download.getData().length);	
			playAudio(download.getData());
			
		}
		
	}
	
	private void PlayShortAudioFileViaAudioTrack(String filePath) throws IOException
	{
		// We keep temporarily filePath globally as we have only two sample sounds now..
		if (filePath==null)
			return;
	
		//Reading the file..
		byte[] byteData = null; 
		File file = null; 
		file = new File(filePath); // for ex. path= "/sdcard/samplesound.pcm" or "/sdcard/samplesound.wav"
		byteData = new byte[(int) file.length()];
		FileInputStream in = null;
		try {
			in = new FileInputStream( file );
			in.read( byteData );
			in.close(); 
	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// Set and push to audio track..
		int intSize = android.media.AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_STEREO,
		AudioFormat.ENCODING_PCM_16BIT); 
		AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, intSize, AudioTrack.MODE_STREAM);
		if (at!=null) { 
			at.play();
			// Write the byte array to the track
			at.write(byteData, 0, byteData.length); 
			at.stop();
			at.release();
		}

	}
	
	private void playAudio(byte[] soundArray) {
	    try {
	        // create temp file that will hold byte array
	    	//byte[] decoded = Base64.decode(soundArray);
	    	
	    	File outputDir = this.getApplicationContext().getExternalCacheDir();
	    	File temp = File.createTempFile("temporary", ".pcm", outputDir);
	        String path = temp.getAbsolutePath();

	    	FileOutputStream fos = new FileOutputStream(path);
	    	fos.write(soundArray);
	    	fos.close();


	        Log.v(TAG, "playing audio wuut: " + path);
	        PlayShortAudioFileViaAudioTrack(path);	        
	    	
	        
	        //temp.deleteOnExit();
	        //FileOutputStream fos = new FileOutputStream(temp);
	        //fos.write(soundArray);
	        //fos.close();
	        /*
	        MediaPlayer mediaPlayer = new MediaPlayer();
	        //FileInputStream fis = new FileInputStream(temp);
	        
	        Uri uri = Uri.fromFile(temp);


	        mediaPlayer.setDataSource(getApplicationContext(), uri);

	        mediaPlayer.prepare();
	        mediaPlayer.start();
	        */
	    } catch (IOException ex) {
	        String s = ex.toString();
	        ex.printStackTrace();
	    }
	    
	}
	
	private class MessageListRequestListener implements RequestListener<Message.List> {

		@Override
		public void onRequestFailure(SpiceException exception) {
			Log.e(TAG, "message list failure" + exception.getMessage());
			// detect invalid auth
			if (exception.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException e = (HttpClientErrorException)exception.getCause();
				if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
					dh.clearPesistentUser();
					Intent intent = new Intent(MessagesList.this, LoginActivity.class);
					MessagesList.this.startActivity(intent);
					finish();
				}
			}
		}

		@Override
		public void onRequestSuccess(Message.List messages) {
			DisplayMessageList(messages.getMessages());
			Log.d(TAG, "success, number of messages: " + messages.getMessages().size());	
		}
		
	}

}
