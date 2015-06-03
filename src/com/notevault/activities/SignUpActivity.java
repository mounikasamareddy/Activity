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

import org.json.JSONException;
import org.json.JSONObject;

import com.notevault.support.ServerUtilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignUpActivity extends Activity{

	private EditText companyNameEditText, userFirstNameEditText, userLastNameEditText, phoneEditText,
	emailIdEditText,passwordEditText, zipEditText, countryEditText;
	private String companyName, userFirstName, userLastName, phoneText, zipText, countryText, emailText, passwordText, leadSource;
	private	ProgressDialog mProgressDialog;
	private boolean validEmail;
	private boolean validPhone;
	TextView backTextView;
	ServerUtilities jsonDataPost = new ServerUtilities();
	//Spinner spinner;
	List<String> nameslist;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_registration);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		companyNameEditText=(EditText)findViewById(R.id.company_editText1);
		userFirstNameEditText=(EditText)findViewById(R.id.username_editText2);
		userLastNameEditText=(EditText)findViewById(R.id.lastname_editText3);
		phoneEditText=(EditText)findViewById(R.id.phone_editText4);
		zipEditText = (EditText)findViewById(R.id.zip_editText);
		countryEditText = (EditText)findViewById(R.id.country_editText);
		emailIdEditText=(EditText)findViewById(R.id.email_editText5);
		passwordEditText=(EditText)findViewById(R.id.password_editText6);
		passwordEditText.setTypeface(Typeface.DEFAULT);
		
		//spinner = (Spinner)findViewById(R.id.spinner1);

		backTextView = (TextView)findViewById(R.id.textView1);
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
		//ArrayAdapter<String> adapter = 	new ArrayAdapter<String> (this,android.R.layout.simple_spinner_item,nameslist);
		//adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		//spinner.setAdapter(adapter);
		/*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        Object item = parent.getItemAtPosition(pos);
		        leadSource = item.toString();
		        System.out.println("leadSource........"+leadSource);
		     
		    }
		    public void onNothingSelected(AdapterView<?> parent) {
		    }
		});*/
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	public void signUp(View v){
		mProgressDialog = new ProgressDialog(SignUpActivity.this);
		mProgressDialog = new ProgressDialog(SignUpActivity.this);
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
		System.out.println("entered value........................"+userFirstName);
		System.out.println("entered value........................"+userLastName);
		System.out.println("entered value........................"+phoneText);
		System.out.println("entered value........................"+zipText);
		System.out.println("entered value........................"+countryText);
		System.out.println("entered value........................"+emailText);
		System.out.println("entered value........................"+companyName);
		System.out.println("entered value........................"+passwordText);
		String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
		validEmail = emailText.matches(emailPattern);
		validPhone = android.util.Patterns.PHONE.matcher(phoneText).matches();
		System.out.println("email is =="+String.valueOf(validEmail)+" " +" phone is=="+String.valueOf(validPhone));
		if((!emailText.equals(""))){
			System.out.println("email text");
			if ((!passwordText.equals(""))) {
				System.out.println("passwordText");
				if ((!userFirstName.equals(""))) {
					System.out.println("userFirstName");
					if ((!userLastName.equals(""))) {
						System.out.println("userLastName");
						if ((!phoneText.equals(""))) {
							System.out.println("phoneText");
							if ((!zipText.equals(""))) {
								System.out.println("zipText");
								if ((!countryText.equals(""))) {
									System.out.println("countryText");
									if (validEmail) {
										System.out.println("validEmail");
										if (validPhone) {
											System.out.println("validPhone");
											mProgressDialog.show();
											RegisterTask registerTask=new RegisterTask();
											registerTask.execute();
											/*if (phoneText.length()==10) {
										System.out.println("phoneText");
										System.out.println("Balakrishna");

									}*/
										}
									}
								}
							}
						}
					}
				}
			}


		}
		else{
			Toast.makeText(SignUpActivity.this, "In Valid Fields.. Enter fields correctly", Toast.LENGTH_LONG).show();
		}

	}
	private class RegisterTask extends AsyncTask<Void, Void, String> {

		String userDetailsString;
		@Override
		protected String doInBackground(Void... params) {
			try{
				TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					@Override
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] arg0, String arg1) {
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
						// TODO Auto-generated method stub
						return false;
					}


				};
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(hv);

				System.out.println("Registration task............");
				try {

					JSONObject jsonobj = new JSONObject();
					jsonobj.put("FirstName",userFirstName);
					jsonobj.put("LastName",userLastName);
					jsonobj.put("Email",emailText);
					jsonobj.put("Password",passwordText);
					jsonobj.put("Phone",phoneText);
					jsonobj.put("Company",companyName);
					jsonobj.put("Country",countryText);
					jsonobj.put("Zip",zipText);
					jsonobj.put("LeadSource",leadSource);
					userDetailsString = jsonDataPost.signUp(jsonobj);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(String param) {
			mProgressDialog.dismiss();
			Toast.makeText(getApplicationContext(), "Registration Succesful", Toast.LENGTH_SHORT).show();
			Intent gcmIntent = new Intent(SignUpActivity.this,GettingStarted.class);
			startActivity(gcmIntent);
			finish();
		}
	}
}
