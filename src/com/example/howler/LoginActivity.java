package com.example.howler;

import com.example.howler.WebRequest.JsonSpiceService;
import com.example.howler.WebRequest.LoginRegisterRequest;
import com.example.howler.WebRequest.User;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity implements OnClickListener {
	
	protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);
	
	private DatabaseHelper dh;
	private EditText userNameEditableField;
	private EditText passwordEditableField;
	private final static String TAG = "LoginActivity Lifecycle logs";

	@Override
	public void onStart() {
		super.onStart();
		 spiceManager.start(this);
	}
	
	@Override
	public void onStop() {
		spiceManager.shouldStop();
		Log.d(TAG, "On Stop");
		super.onStop();
	}
	
	// bs lifecycle methods
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "On Pause");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "On Resume");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "On Destroy");
	}
	
	//onCreate is in the Activity base class, so we use Override annotation
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//call to onCreate of Activity super class
		super.onCreate(savedInstanceState);
		
		Log.d(TAG, "On Create");
		
		dh = new DatabaseHelper(this.getApplicationContext());
		
		// check for already existing user. send to recorder if auth token
		if (dh.authToken() != null) {
			  Intent intent = new Intent(LoginActivity.this, RecorderActivity.class);
			  LoginActivity.this.startActivity(intent);
			  finish();
			  return;
		}
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_login);

		//gives the left hand variable the id of the username_text 
		userNameEditableField = (EditText) findViewById(R.id.username_text);
		passwordEditableField = (EditText) findViewById(R.id.password_text);
		//login button
		View btnLogin = (Button) findViewById(R.id.login_button);
		btnLogin.setOnClickListener(this);
		//new user button
		View btnNewUser = (Button) findViewById(R.id.create_user_button);
		btnNewUser.setOnClickListener(this);
	}

	private User makeUser() {
		User user = new User();
		user.setUsername(this.userNameEditableField.getText().toString());
		user.setPassword(this.passwordEditableField.getText().toString());
		// TODO: need to do email too!
		if (user.validLogin()) {
			return user;
		} else {
			// Try again?
			new AlertDialog.Builder(this)
					.setTitle("Login Failed")
					.setMessage("Invalid Fields")
					.setNeutralButton("Try Again",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		}
		return null;
	}
	
	private void performLoginRegistration(User user) {
		  LoginActivity.this.setProgressBarIndeterminateVisibility(true);
		  LoginRegisterRequest request = new LoginRegisterRequest(user);
		  spiceManager.execute(request, user, DurationInMillis.ALWAYS_EXPIRED, new LoginRegisterRequestListener());
	}
	
	private class LoginRegisterRequestListener implements RequestListener<User> {

	  @Override
	  public void onRequestFailure(SpiceException e) {
	    //update your UI
		  Log.d(TAG, e.getMessage());
	  }

	  @Override
	  public void onRequestSuccess(User user) {
	    //update your UI
		  if (user != null && user.getAuthtoken() != null && user.getAuthtoken().length() > 0) {
			  // successful login
			  dh.setPersistentUser(user);
			  Intent intent = new Intent(LoginActivity.this, RecorderActivity.class);
			  LoginActivity.this.startActivity(intent);
			  finish();
			  
		  } else {
			  String message = "Invalid Fields";
			  if (user.getMessage() != null) {
				  message = user.getMessage();
			  }
				new AlertDialog.Builder(LoginActivity.this)
				.setTitle("Login Failed")
				.setMessage(message)
				.setNeutralButton("Try Again",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();
		  }
			  
	  }
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_button:
			User user = makeUser();
			if (user != null) {
				performLoginRegistration(user);
			}
			break;
		case R.id.create_user_button:
			startActivity(new Intent(this, CreateAccount.class));
			break;
		}
	}
}
