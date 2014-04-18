package com.example.howler;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.example.howler.WebRequest.AddFriendRequest;
import com.example.howler.WebRequest.ConfirmFriendRequest;
import com.example.howler.WebRequest.CreateMessageRequest;
import com.example.howler.WebRequest.ErrorResponseObject;
import com.example.howler.WebRequest.Friend;
import com.example.howler.WebRequest.FriendsListRequest;
import com.example.howler.WebRequest.JsonSpiceService;
import com.example.howler.WebRequest.MessageUploadRequest;
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
	private List<LinearLayout> dynamicallyAddedViews = new ArrayList<LinearLayout>();
	private List<String> selectedUsernames = new ArrayList<String>();

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
				dh.clearPesistentUser();
				Intent intent = new Intent(FriendsList.this, LoginActivity.class);
				FriendsList.this.startActivity(intent);
				finish();
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
				// TODO: check input fields

				com.example.howler.WebRequest.Message message = new com.example.howler.WebRequest.Message();
				message.setTitle(RecorderActivity.messageTitle());
				message.setUsernames(selectedUsernames);

				CreateMessageRequest request = new CreateMessageRequest(dh, message);
				spiceManager.execute(request, message, DurationInMillis.ALWAYS_EXPIRED, new CreateMessageRequestListener());

			}
		});

		// send request
		FriendsList.this.setProgressBarIndeterminateVisibility(true);
		FriendsListRequest request = new FriendsListRequest(dh.authToken());
		spiceManager.execute(request, new FriendsListRequestListener());

		friend_list = dh.getAllFriends();
		Log.d(TAG, "Retrieved friend list: "+friend_list.size());
		displayFriends();
	}

	private class CreateMessageRequestListener implements RequestListener<com.example.howler.WebRequest.Message> {

		@Override
		public void onRequestFailure(SpiceException exception) {
			Log.v(TAG, exception.getMessage());
			if (exception.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException e = (HttpClientErrorException)exception.getCause();
				if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
					dh.clearPesistentUser();
					Intent intent = new Intent(FriendsList.this, LoginActivity.class);
					FriendsList.this.startActivity(intent);
					finish();
				}
			}
		}

		@Override
		public void onRequestSuccess(com.example.howler.WebRequest.Message message) {
			// multipart upload of data
			Log.v(TAG, "created message id: " + message.getMessage_id() + ", title: " + message.getTitle());

			message.setTitle(RecorderActivity.messageTitle());
			try {
				RandomAccessFile f = new RandomAccessFile(RecorderActivity.filePath(), "r");
				byte[] data = new byte[(int)f.length()];
				f.read(data);
				Log.v(TAG, "datalength = " + data.length);
				message.setData(data);
				MessageUploadRequest  request = new MessageUploadRequest(dh, message);
				spiceManager.execute(request, message, DurationInMillis.ALWAYS_EXPIRED, new UploadMessageRequestListener());
				f.close();
			} catch (Exception e) {
				Log.v(TAG, "failed to upload multipart. could not read audio file");
			}
		}
	}

	private class UploadMessageRequestListener implements RequestListener<String> {

		@Override
		public void onRequestFailure(SpiceException exception) {
			Log.v(TAG, exception.getMessage());
			if (exception.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException e = (HttpClientErrorException)exception.getCause();
				if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
					dh.clearPesistentUser();
					Intent intent = new Intent(FriendsList.this, LoginActivity.class);
					FriendsList.this.startActivity(intent);
					finish();
				}
			}
		}

		@Override
		public void onRequestSuccess(String s) {
			// multipart upload of data
			//Log.v(TAG, "uploaded message id: " + message.getMessage_id() + ", title: " + message.getTitle());
			Log.v(TAG, "upload success string: " + s);
		}

	}

	public void displayFriends() {
		for (LinearLayout l : dynamicallyAddedViews) {
			main.removeView(l);
		}
		dynamicallyAddedViews.clear();

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

							Username username = new Username();
							username.setUsername(((Button)v).getText().toString());
							Log.d(TAG, "username: "+username.getUsername());
							FriendsList.this.setProgressBarIndeterminateVisibility(true);
							Log.d(TAG, "friend id = " + friend.getIdentifier());
							ConfirmFriendRequest request = new ConfirmFriendRequest(friend, dh.authToken());

							spiceManager.execute(request, username, DurationInMillis.ALWAYS_EXPIRED, new ConfirmFriendRequestListener());
						}
					});
				} else {


					btnMessage.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

							if (selectedUsernames.contains(friend.getUsername())) {
								selectedUsernames.remove(friend.getUsername());
								v.setBackgroundColor(Color.GRAY);

							} else {
								selectedUsernames.add(friend.getUsername());
								v.setBackgroundColor(Color.RED);
							}

							Log.v(TAG, "friend = " + friend.getUsername());
						}
					});

				}

				layout.addView(btnMessage);
				main.addView(layout);

				dynamicallyAddedViews.add(layout);
			}	
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friends_list, menu);
		return true;
	}

	private class FriendsListRequestListener implements RequestListener<Friend.List> {

		@Override
		public void onRequestFailure(SpiceException exception) {
			Log.e(TAG, "failure" + exception.getMessage());
			Toast.makeText(getApplicationContext(), "Failed to load friends", Toast.LENGTH_LONG).show();
			if (exception.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException e = (HttpClientErrorException)exception.getCause();
				if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
					dh.clearPesistentUser();
					Intent intent = new Intent(FriendsList.this, LoginActivity.class);
					FriendsList.this.startActivity(intent);
					finish();
				}
			}
		}

		@Override
		public void onRequestSuccess(Friend.List friends) {
			Log.d(TAG, "success, number of friends: " + friends.getFriends().size() + " friends: " + friends.getFriends());

			friend_list = friends.getFriends();
			saveFriendsToDb(friend_list);
			displayFriends();
		}

	}

	private void saveFriendsToDb(List<Friend> friends) {
		for (int i=0; i<friends.size(); i++) {
			dh.insertFriend(friends.get(i));
		}
	}

	private class AddFriendRequestListener implements RequestListener<ErrorResponseObject> {

		@Override
		public void onRequestFailure(SpiceException exception) {
			Log.e(TAG, "failure" + exception.getMessage());
			Toast.makeText(getApplicationContext(), "Failed to add friend", Toast.LENGTH_LONG).show();
			if (exception.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException e = (HttpClientErrorException)exception.getCause();
				if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
					dh.clearPesistentUser();
					Intent intent = new Intent(FriendsList.this, LoginActivity.class);
					FriendsList.this.startActivity(intent);
					finish();
				}
			}
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
			if (exception.getCause() instanceof HttpClientErrorException) {
				HttpClientErrorException e = (HttpClientErrorException)exception.getCause();
				if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
					dh.clearPesistentUser();
					Intent intent = new Intent(FriendsList.this, LoginActivity.class);
					FriendsList.this.startActivity(intent);
					finish();
				}
			}
		}

		@Override
		public void onRequestSuccess(ErrorResponseObject error) {
			Log.d(TAG, "success: "+error.isSuccess()+" message: " + error.getMessage());
			if (error.isSuccess()) {
				Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

			} else {
				Toast.makeText(getApplicationContext(), "Failed to confirm friend: "+error.getMessage(), Toast.LENGTH_LONG).show();
			}
			// send request
			FriendsList.this.setProgressBarIndeterminateVisibility(true);
			FriendsListRequest request = new FriendsListRequest(dh.authToken());
			spiceManager.execute(request, new FriendsListRequestListener());

		}

	}
}
