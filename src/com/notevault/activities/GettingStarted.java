package com.notevault.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GettingStarted extends Fragment {

	int mCurrentPage;
	private Button loginButton, signUpButton;
	private RelativeLayout trail;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.homescreen, container, false);
		trail = (RelativeLayout) v.findViewById(R.id.trail);
		loginButton = (Button) v.findViewById(R.id.login_button);
		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
			}
		});
		trail.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Log.d("trail","--->");
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://support.notevault.com"));
				startActivity(browserIntent);
				
			}
		});
		signUpButton = (Button) v.findViewById(R.id.signup_button);
		signUpButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SignUpActivity.class);
				startActivity(intent);
			}
		});
		return v;
	}
}
