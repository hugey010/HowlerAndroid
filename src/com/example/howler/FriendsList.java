package com.example.howler;

import java.util.ArrayList;
import java.util.List;

import com.example.howler.WebRequest.AddFriendRequest;
import com.example.howler.WebRequest.ConfirmFriendRequest;
import com.example.howler.WebRequest.ErrorResponseObject;
import com.example.howler.WebRequest.Friend;
import com.example.howler.WebRequest.FriendListObject;
import com.example.howler.WebRequest.FriendsListRequest;
import com.example.howler.WebRequest.JsonSpiceService;
import com.example.howler.WebRequest.Username;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsList extends Activity {
	private static final String TAG = "Friends List Activity";
	private EditText searchFriend;
	protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
	private DatabaseHelper dh;
	private List<Friend> friend_list;
	private LinearLayout main;

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


		dh = new DatabaseHelper(this.getApplicationContext());

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
				Username username = new Username();
				username.setUsername(searchFriend.getText().toString());
				Log.d(TAG, "username to add: "+username.getUsername());
				FriendsList.this.setProgressBarIndeterminateVisibility(true);
				AddFriendRequest request = new AddFriendRequest(username, dh.authToken());
				spiceManager.execute(request, username, DurationInMillis.ALWAYS_EXPIRED, new AddFriendRequestListener());
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

		// send request
		FriendsList.this.setProgressBarIndeterminateVisibility(true);
		FriendListObject friendList = new FriendListObject();
		FriendsListRequest request = new FriendsListRequest(dh.authToken());
		spiceManager.execute(request, friendList, DurationInMillis.ALWAYS_EXPIRED, new FriendsListRequestListener());


	}

	public void populateFriendList(FriendListObject friendList){
		friend_list = friendList.getFriends();
	}

	public void displayFriends() {
		main =(LinearLayout) findViewById(R.id.friends);
		if (friend_list.size() == 0) {
			Log.d(TAG, "No Friends");
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			LinearLayout layout = new LinearLayout(getApplicationContext());
			LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setLayoutParams(params);

			TextView none = new TextView(getApplicationContext());
			none.setLayoutParams(textparams);
			none.setText("No Friends :(");

			layout.addView(none);
			main.addView(layout);
		} else {
			Log.d(TAG, "Num of friends: "+friend_list.size());
			for(final Friend friend : friend_list){
				Log.d(TAG, "username: "+friend.getUsername()+" pending: "+friend.isPending());
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				LinearLayout layout = new LinearLayout(getApplicationContext());
				LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

				layout.setOrientation(LinearLayout.VERTICAL);
				layout.setLayoutParams(params);
				Button btnMessage = new Button(getApplicationContext());
				btnMessage.setLayoutParams(buttonParams);
				btnMessage.setText(friend.getUsername());

				if (friend.isPending()) {
					btnMessage.setBackgroundColor(Color.BLUE);
					//btnMessage.append(" - Click to Confirm Friend");
					btnMessage.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO action for confirming friend
							Username username = new Username();
							username.setUsername(((Button)v).getText().toString());
							Log.d(TAG, "username: "+username.getUsername());
							FriendsList.this.setProgressBarIndeterminateVisibility(true);
							ConfirmFriendRequest request = new ConfirmFriendRequest(username, dh.authToken());
							spiceManager.execute(request, username, DurationInMillis.ALWAYS_EXPIRED, new ConfirmFriendRequestListener());
						}
					});
				} else {
					// TODO select friend for sending
				}

				layout.addView(btnMessage);
				main.addView(layout);	
			}	
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friends_list, menu);
		return true;
	}

	private class FriendsListRequestListener implements RequestListener<FriendListObject> {

		@Override
		public void onRequestFailure(SpiceException exception) {
			Log.e(TAG, "failure" + exception.getMessage());
			Toast.makeText(getApplicationContext(), "Failed to load friends", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onRequestSuccess(FriendListObject friends) {
			Log.d(TAG, "success, number of messages: " + friends.getFriends().size() + " friends: " + friends.getFriends());

			populateFriendList(friends);
			displayFriends();
		}

	}

	private class AddFriendRequestListener implements RequestListener<ErrorResponseObject> {

		@Override
		public void onRequestFailure(SpiceException exception) {
			Log.e(TAG, "failure" + exception.getMessage());
			Toast.makeText(getApplicationContext(), "Failed to add friend", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onRequestSuccess(ErrorResponseObject error) {
			Log.d(TAG, "success: "+error.isSuccess()+" message: " + error.getMessage());
			if (error.isSuccess()) {
				Toast.makeText(getApplicationContext(), "Sent friend request", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "Failed to add friend: "+error.getMessage(), Toast.LENGTH_LONG).show();
			}
			searchFriend.setText("");
		}

	}

	private class ConfirmFriendRequestListener implements RequestListener<ErrorResponseObject> {

		@Override
		public void onRequestFailure(SpiceException exception) {
			Log.e(TAG, "failure" + exception.getMessage());
			Toast.makeText(getApplicationContext(), "Failed to confirm friend", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onRequestSuccess(ErrorResponseObject error) {
			Log.d(TAG, "success: "+error.isSuccess()+" message: " + error.getMessage());
			if (error.isSuccess()) {
				Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "Failed to confirm friend: "+error.getMessage(), Toast.LENGTH_LONG).show();
			}
		}

	}
}
