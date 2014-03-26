package com.example.howler;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class FriendsList extends Activity {
	private EditText searchFriend;
	
	
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
	
	//check if searchFriend has string length > 0, if yes enable addfriend button.
	void checkFieldsForEmptyValues(){
	    ImageButton addButton = (ImageButton) findViewById(R.id.addfriend_imagebutton);

	    String cause = searchFriend.getText().toString();

	    if(cause.length()>0){
	        addButton.setEnabled(true);
	        addButton.setImageResource(R.drawable.add50);

	    } else {
	        addButton.setEnabled(false);
	        addButton.setImageResource(R.drawable.add50inactive);
	    }
	}	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		setContentView(R.layout.activity_friends_list);
		
		//get friend search edittext and listen for text
		searchFriend = (EditText) findViewById(R.id.friendsearch_edittext);
		searchFriend.addTextChangedListener(mTextWatcher);
		checkFieldsForEmptyValues();			
		
		//get logout button and assign Click Listener
		Button logoutButton = (Button) findViewById(R.id.logout_button);
		logoutButton.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				// TODO
				
			}
		});
		
		//make the Back button go back to Recorder
		Button backButton = (Button) findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), com.example.howler.RecorderActivity.class);
				startActivity(i);				
			}
		});
		
		//set click listener to add friend Button
		ImageButton addFriendButton = (ImageButton) findViewById(R.id.addfriend_imagebutton);
		addFriendButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO action for add friend
				
			}
		});
		
		//set click listener to Send Button
		Button sendButton = (Button) findViewById(R.id.send_button);
		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO action for send button
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friends_list, menu);
		return true;
	}

}
