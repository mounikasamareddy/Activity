package com.notevault.activities;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.notevault.support.ServerUtilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends Activity {

	private EditText companyNameEditText, userFirstNameEditText,
			userLastNameEditText, phoneEditText, emailIdEditText,
			passwordEditText, zipEditText, countryEditText;
	private String companyName, userFirstName, userLastName, phoneText,
			zipText, countryText, emailText, passwordText, leadSource;
	private ProgressDialog mProgressDialog;
	private boolean validEmail;
	private boolean validPhone;
	TextView backTextView;
	ServerUtilities jsonDataPost;
	// Spinner spinner;
	List<String> nameslist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_registration);
		jsonDataPost = new ServerUtilities();
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		companyNameEditText = (EditText) findViewById(R.id.company_editText1);
		userFirstNameEditText = (EditText) findViewById(R.id.username_editText2);
		userLastNameEditText = (EditText) findViewById(R.id.lastname_editText3);
		phoneEditText = (EditText) findViewById(R.id.phone_editText4);
		zipEditText = (EditText) findViewById(R.id.zip_editText);
		countryEditText = (EditText) findViewById(R.id.country_editText);
		emailIdEditText = (EditText) findViewById(R.id.email_editText5);
		passwordEditText = (EditText) findViewById(R.id.password_editText6);
		passwordEditText.setTypeface(Typeface.DEFAULT);

		// spinner = (Spinner)findViewById(R.id.spinner1);

		backTextView = (TextView) findViewById(R.id.textView1);
		backTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();

			}
		});

		nameslist = new ArrayList<String>();
		nameslist.add("How did you refer us?");
		nameslist.add("Friend's Referral");
		nameslist.add("Web");
		nameslist.add("Magazine");
		nameslist.add("Conference/Trade Show");
		nameslist.add("Sales Rep");
		nameslist.add("Evernote");
		nameslist.add("Box");
		nameslist.add("TV/Radio");
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>
		// (this,android.R.layout.simple_spinner_item,nameslist);
		// adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		// spinner.setAdapter(adapter);
		/*
		 * spinner.setOnItemSelectedListener(new
		 * AdapterView.OnItemSelectedListener() { public void
		 * onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		 * Object item = parent.getItemAtPosition(pos); leadSource =
		 * item.toString(); System.out.println("leadSource........"+leadSource);
		 * 
		 * } public void onNothingSelected(AdapterView<?> parent) { } });
		 */
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	public void signUp(View v) {
		mProgressDialog = new ProgressDialog(SignUpActivity.this);
		// mProgressDialog = new ProgressDialog(SignUpActivity.this);
		mProgressDialog.setMessage("Loading...");
		mProgressDialog.setIndeterminate(false);
		companyName = companyNameEditText.getText().toString().trim();
		userFirstName = userFirstNameEditText.getText().toString().trim();
		userLastName = userLastNameEditText.getText().toString().trim();
		phoneText = phoneEditText.getText().toString().trim();
		zipText = zipEditText.getText().toString().trim();
		countryText = countryEditText.getText().toString().trim();
		emailText = emailIdEditText.getText().toString().trim();
		passwordText = passwordEditText.getText().toString();
		System.out.println("Entered User FirstName:	" + userFirstName);
		System.out.println("Entered User LastName:	" + userLastName);
		System.out.println("Entered Phone No.:		" + phoneText);
		System.out.println("Entered Zip Code:		" + zipText);
		System.out.println("Entered Country:		" + countryText);
		System.out.println("Entered Email:			" + emailText);
		System.out.println("Entered Company:		" + companyName);
		System.out.println("Entered Password:		" + passwordText);
		String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
		validEmail = emailText.matches(emailPattern);
		validPhone = android.util.Patterns.PHONE.matcher(phoneText).matches();
		System.out.println("email:	" + String.valueOf(validEmail) + ",		"
				+ "phone:	" + String.valueOf(validPhone));
		if ((!emailText.equals(""))) {
			if ((!passwordText.equals(""))) {
				if ((!userFirstName.equals(""))) {
					if ((!userLastName.equals(""))) {
						if ((!phoneText.equals(""))) {
							if ((!zipText.equals(""))) {
								if ((!countryText.equals(""))) {
									if (validEmail) {
										if (validPhone) {
											mProgressDialog.show();
											RegisterTask registerTask = new RegisterTask();
											registerTask.execute();
										}
									}
								}
							}
						}
					}
				}
			}

		} else {
			Toast.makeText(SignUpActivity.this,
					"In Valid Fields.. Enter fields correctly",
					Toast.LENGTH_LONG).show();
		}

	}

	private class RegisterTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			try {
				TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					@Override
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] arg0,
							String arg1) {
					}

					@Override
					public void checkServerTrusted(
							java.security.cert.X509Certificate[] chain,
							String authType) {
					}
				} };

				HostnameVerifier hv = new HostnameVerifier() {

					@Override
					public boolean verify(String hostname, SSLSession session) {
						return false;
					}
				};
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc
						.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(hv);

				try {

					JSONObject jsonobj = new JSONObject();
					jsonobj.put("FirstName", userFirstName);
					jsonobj.put("LastName", userLastName);
					jsonobj.put("Email", emailText);
					jsonobj.put("Password", passwordText);
					jsonobj.put("CompanyName", companyName);
					jsonobj.put("Country", countryText);
					jsonobj.put("Zip", zipText);
					// jsonobj.put("LeadSource", leadSource);

					System.out.println(jsonobj);

					return jsonDataPost.signUp(jsonobj);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String response) {
			Log.d("Signup response", "--->" + response);
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;
				Toast.makeText(getApplicationContext(), "You are offline",
						Toast.LENGTH_LONG).show();

			} else {
				System.out.println("Sign Up Response: " + response);
				String signUpResponse = response;

				if (signUpResponse != null && signUpResponse != "") {
					try {
						JSONObject signUpJSONResponse = new JSONObject(
								signUpResponse);

						if ((signUpJSONResponse.getInt("Status") == 0)
								|| (signUpJSONResponse.getInt("Status") == 200)) {

							Toast.makeText(getApplicationContext(),
									"Registration Succesful",
									Toast.LENGTH_SHORT).show();
							Intent i = new Intent(SignUpActivity.this,
									LoginActivity.class);
							startActivity(i);
							finish();

						} else {
							Toast.makeText(
									getApplicationContext(),
									"Could not Register! Please contact support.",
									Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					System.out.println("An error occurred! Could not SignUp");
					Toast.makeText(getApplicationContext(),
							"An error occurred while signing up!",
							Toast.LENGTH_LONG).show();
				}
			}
			mProgressDialog.dismiss();
		}
	}
}
