package com.example.howler;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.os.Bundle;
import android.os.Environment;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class RecorderActivity extends FragmentActivity implements OnClickListener {
	private static final String TAG = "RecorderActivity";
	
	private EditText titleEditableField;
	private AudioRecord voiceRecorder;
	private static String audioFile;
	private static String messageTitle;
	private boolean isRecording = false;
	private View btnPlay;
	private View btnRecord;
	private View btnDelete;
	private View btnSend;
	private View btnFriendsList;
	private View btnMessagesList;
	final Context context = this;
	private Thread recordingThread = null;
	private Thread playingThread = null;
	
	public static String filePath() {
		return audioFile;
	}
	public static String messageTitle() {
		return messageTitle;
	}
	
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
		Button sendButton = (Button) findViewById(R.id.send_button);

		String title = titleEditableField.getText().toString();
		messageTitle = title;

		if(title.length()>0){
			playButton.setEnabled(true);
			recordButton.setEnabled(true);
			deleteButton.setEnabled(true);
			sendButton.setEnabled(true);

		} else {
			playButton.setEnabled(false);
			recordButton.setEnabled(false);
			deleteButton.setEnabled(false);
			sendButton.setEnabled(false);
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
		btnSend = (Button) findViewById(R.id.send_button);
		btnSend.setOnClickListener(this);
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
	public void onStop() {
		if (voiceRecorder != null) {
			voiceRecorder.release();
			voiceRecorder = null;
		}
		super.onStop();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recorder, menu);
		return true;
	}
	
	private void playRecording() throws Exception{
		//voicePlayer = new MediaPlayer();
		if (audioFile != null) {
			Toast.makeText(RecorderActivity.this, "started playing file at " + audioFile, Toast.LENGTH_LONG).show();
		}
		//voicePlayer.setDataSource(audioFile);
		//voicePlayer.prepare();
		//voicePlayer.start();	
	    playingThread = new Thread(new Runnable() {
	        public void run() {
	    		try {
					PlayShortAudioFileViaAudioTrack(audioFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	    }, "AudioPlayer Thread");
	    playingThread.start();
	}
	
	private void deleteRecording(){
		//dialog pop-up when trying to delete a null audiofile
		if (audioFile == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("Error");
			builder.setMessage("No audio file to delete.");
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					titleEditableField.requestFocus();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();			
		}
		else {
			Toast.makeText(RecorderActivity.this, "Message deleted!", Toast.LENGTH_LONG).show();
			File outFile = new File(audioFile);
			outFile.delete();
			audioFile = null;
			btnPlay.setClickable(false);
			btnDelete.setClickable(false);
			btnRecord.setClickable(true);
			titleEditableField.setText("");
		}
	}
	
	private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };
	public AudioRecord findAudioRecord() {
	    for (int rate : mSampleRates) {
	        for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
	            for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
	                try {
	                    Log.d(TAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
	                            + channelConfig);
	                    int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

	                    if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
	                        // check if we can instantiate and have a success
	                        AudioRecord recorder = new AudioRecord(AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);
	                        

	                        if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
	                            return recorder;
	                    }
	                } catch (Exception e) {
	                    Log.e(TAG, rate + "Exception, keep trying.",e);
	                }
	            }
	        }
	    }
	    return null;
	}
	
	private void startRecording() {

        String title = this.titleEditableField.getText().toString();
        audioFile = Environment.getDataDirectory().getAbsolutePath() + "/data/com.example.howler/" + title + ".pcm";
        Toast.makeText(RecorderActivity.this, "Begin Recording", Toast.LENGTH_LONG).show();
        int buffSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);

        //findAudioRecord();
	    voiceRecorder =  new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, buffSize);
	    voiceRecorder.startRecording();
	    isRecording = true;
	    recordingThread = new Thread(new Runnable() {
	        public void run() {
	            writeAudioDataToFile();
	        }
	    }, "AudioRecorder Thread");
	    recordingThread.start();
	}

	    //convert short to byte
	private byte[] short2byte(short[] sData) {
	    int shortArrsize = sData.length;
	    byte[] bytes = new byte[shortArrsize * 2];
	    for (int i = 0; i < shortArrsize; i++) {
	        bytes[i * 2] = (byte) (sData[i] & 0x00FF);
	        bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
	        sData[i] = 0;
	    }
	    return bytes;

	}

	int BytesPerElement = 2; // 2 bytes in 16bit format
	int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024

	private void writeAudioDataToFile() {
	    // Write the output audio in byte

	    short sData[] = new short[BufferElements2Rec];

	    FileOutputStream os = null;
	    try {
	        os = new FileOutputStream(audioFile);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }

	    while (isRecording) {
	        // gets the voice output from microphone to byte format

	        voiceRecorder.read(sData, 0, BufferElements2Rec);
	        try {
	            // // writes the data to file from buffer
	            // // stores the voice buffer
	            byte bData[] = short2byte(sData);
	            os.write(bData, 0, BufferElements2Rec * BytesPerElement);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    try {
	        os.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private void stopRecording() {
	    // stops the recording activity

	    if (null != voiceRecorder) {
	        isRecording = false;
	        voiceRecorder.stop();
	        voiceRecorder.release();
	        voiceRecorder = null;
	        recordingThread = null;
	    }
	    Toast.makeText(RecorderActivity.this, "Recording Ended", Toast.LENGTH_LONG).show();	    
		btnPlay.setClickable(true);
		btnDelete.setClickable(true);
		btnRecord.setClickable(false);
	}
	
	private void PlayShortAudioFileViaAudioTrack(String filePath) throws IOException
	{
		if (filePath==null)
			return;
	
		//Reading the file..
		byte[] byteData = null; 
		File file = new File(filePath);
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
		int intSize = android.media.AudioTrack.getMinBufferSize(41000, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT); 
		AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 41000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, intSize, AudioTrack.MODE_STREAM);
		if (at!=null) { 
			at.play();
			// Write the byte array to the track
			at.write(byteData, 0, byteData.length); 
			at.stop();
			at.release();
		}

	}
	
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
		case R.id.send_button:
    		Intent intent= new Intent(getApplicationContext(), com.example.howler.FriendsList.class);
    		startActivity(intent);				
			break;			
		}	
	}
}
