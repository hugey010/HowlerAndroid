package com.example.howler;

import java.util.List;

import com.example.howler.WebRequest.JsonSpiceService;
import com.octo.android.robospice.SpiceManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
	private final static String OPT_NAME = "name";
	private final static String TAG = "LoginActivity Lifecycle logs";

	@Override
	public void onStart() {
		super.onStart();
		 spiceManager.start(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		 spiceManager.shouldStop();
		Log.d(TAG, "On Stop");
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

	private void checkLogin() {
		String username = this.userNameEditableField.getText().toString();
		String password = this.passwordEditableField.getText().toString();
		this.dh = new DatabaseHelper(this);
		List<String> names = this.dh.selectAll(username, password);
		if (names.size() > 0) { // Login successful
			// Save username as the name of the player
			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(OPT_NAME, username);
			editor.commit();

			//TODO: bring up next RecorderActivity
			startActivity(new Intent(this, RecorderActivity.class));
			
			//finish deletes the current instance
			finish();
		} else {
			// Try again?
			new AlertDialog.Builder(this)
					.setTitle("Error")
					.setMessage("Login failed")
					.setNeutralButton("Try Again",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).show();
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_button:
			checkLogin();
			break;
		case R.id.create_user_button:
			startActivity(new Intent(this, CreateAccount.class));
			break;
		}
	}
}
