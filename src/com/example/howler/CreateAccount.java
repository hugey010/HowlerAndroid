package com.example.howler;

import com.example.howler.WebRequest.JsonSpiceService;
import com.example.howler.WebRequest.LoginRegisterRequest;
import com.example.howler.WebRequest.User;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

public class CreateAccount extends Activity implements OnClickListener {
	
	private final static String TAG = "CreateAccount Activity";

	protected SpiceManager spiceManager = new SpiceManager(JsonSpiceService.class);

	private EditText etUsername;
	private EditText etPassword;
	private EditText etConfirm;
	private EditText etEmail;
	private DatabaseHelper dh;

	@Override
	public void onStart() {
		super.onStart();
		 spiceManager.start(this);
	}
	
	@Override
	public void onStop() {
		spiceManager.shouldStop();
		super.onStop();
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		setContentView(R.layout.activity_create_account);

		etUsername = (EditText) findViewById(R.id.username_text);
		etPassword = (EditText) findViewById(R.id.password_text);
		etConfirm = (EditText) findViewById(R.id.confirm_password_text);
		etEmail = (EditText) findViewById(R.id.email_text);
		View btnAdd = (Button) findViewById(R.id.done_button);
		btnAdd.setOnClickListener(this);
		View btnCancel = (Button) findViewById(R.id.cancel_button);
		btnCancel.setOnClickListener(this);
	}
	
	private void createAccount() {
		String username = etUsername.getText().toString();
		String password = etPassword.getText().toString();
		String confirm = etConfirm.getText().toString();
		String email = etEmail.getText().toString();
		
		if (!password.equals(confirm)) {
			new AlertDialog.Builder(this)
			.setTitle("Registration Failed")
			.setMessage("Passwords do not match.")
			.setNeutralButton("Try Again",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
						}
					})
				.show();
			
			return;
		}
		
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setEmail(email);
		
		if (user.validRegistration()) {
			performLoginRegistration(user);
			
		} else {
			new AlertDialog.Builder(this)
			.setTitle("Invalid Registration Credentials")
			.setNeutralButton("Try Again",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
						}
					})

			.show();
		}
	}
	
	private void performLoginRegistration(User user) {
		  CreateAccount.this.setProgressBarIndeterminateVisibility(true);
		  LoginRegisterRequest request = new LoginRegisterRequest(user);
		  spiceManager.execute(request, user, DurationInMillis.ALWAYS_EXPIRED, new LoginRegisterRequestListener());
	}
	
	private class LoginRegisterRequestListener implements RequestListener<User> {

		  @Override
		  public void onRequestFailure(SpiceException e) {
				new AlertDialog.Builder(CreateAccount.this)
				.setTitle("Registration Failed")
				.setNeutralButton("Try Again",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();	
				}

		  @SuppressLint("InlinedApi")
		@Override
		  public void onRequestSuccess(User user) {
		    //update your UI
			  if (user != null && user.getAuthtoken() != null && user.getAuthtoken().length() > 0) {
				  // successful login
				  //dh.
				  Intent intent = new Intent(CreateAccount.this, RecorderActivity.class);
				  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				  CreateAccount.this.startActivity(intent);
				  finish();
				  
			  } else {
				  String message = "Invalid Fields";
				  if (user.getMessage() != null) {
					  message = user.getMessage();
				  }
					new AlertDialog.Builder(CreateAccount.this)
					.setTitle("Registration Failed")
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
		case R.id.done_button:
			createAccount();
			break;
		case R.id.cancel_button:
			finish();
			break;
		}
	}
}
