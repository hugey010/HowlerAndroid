package com.example.howler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateAccount extends Activity implements OnClickListener {
	private EditText etUsername;
	private EditText etPassword;
	private EditText etConfirm;
	private DatabaseHelper dh;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		setContentView(R.layout.activity_create_account);

		etUsername = (EditText) findViewById(R.id.username_text);
		etPassword = (EditText) findViewById(R.id.password_text);
		etConfirm = (EditText) findViewById(R.id.confirm_password_text);
		View btnAdd = (Button) findViewById(R.id.done_button);
		btnAdd.setOnClickListener(this);
		View btnCancel = (Button) findViewById(R.id.cancel_button);
		btnCancel.setOnClickListener(this);
	}

	private void CreateAccount() {
		// this.output = (TextView) this.findViewById(R.id.out_text);
		String username = etUsername.getText().toString();
		String password = etPassword.getText().toString();
		String confirm = etConfirm.getText().toString();
		if ((password.equals(confirm)) && (!username.equals(""))
				&& (!password.equals("")) && (!confirm.equals(""))) {
			this.dh = new DatabaseHelper(this);
			this.dh.insert(username, password);
			// this.labResult.setText("Added");
			Toast.makeText(CreateAccount.this, "new record inserted",
					Toast.LENGTH_SHORT).show();
			finish();
		} else if ((username.equals("")) || (password.equals(""))
				|| (confirm.equals(""))) {
			Toast.makeText(CreateAccount.this, "Missing entry", Toast.LENGTH_SHORT)
					.show();
		} else if (!password.equals(confirm)) {
			new AlertDialog.Builder(this)
					.setTitle("Error")
					.setMessage("passwords do not match")
					.setNeutralButton("Try Again",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							})

					.show();
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.done_button:
			CreateAccount();
			finish();
			break;
		case R.id.cancel_button:
			finish();
			break;
		}
	}
}