package com.notevault.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GettingStartedActivity extends Activity{
	Button loginButton, signUpButton;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homescreen);
		loginButton=(Button)findViewById(R.id.login_button);
		loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(GettingStartedActivity.this,LoginActivity.class);
				startActivity(intent);
			}
		});
		signUpButton = (Button)findViewById(R.id.signup_button);
		signUpButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(GettingStartedActivity.this,SignUpActivity.class);
				startActivity(intent);
			}
		});
	}
}
