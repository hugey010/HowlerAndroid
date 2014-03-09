package com.example.howler;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class RecorderActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		setContentView(R.layout.activity_recorder);
		
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
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.friends_button:
			
			break;
		case R.id.message_list_button:
			
			
			break;
		}
	}

}
