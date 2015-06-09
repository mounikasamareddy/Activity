package com.notevault.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.notevault.arraylistsupportclasses.LoginData;
import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;
import com.notevault.support.Utilities;

@SuppressLint("ResourceAsColor")
public class LoginActivity extends Activity {

	Singleton singleton = Singleton.getInstance();
	public static final String MyPREFERENCES = "MyPrefs";
	SharedPreferences sharedPreferences;
	EditText userEditText, passwordEditText;
	String username, password;
	ServerUtilities jsonDataPost = new ServerUtilities();
	private ProgressDialog mProgressDialog;
	CheckBox rememberMe;
	public static HashMap<Integer, String> projectsListStatus = new HashMap<Integer, String>();
	public static HashMap<Integer, String> projectsListActivityStatus = new HashMap<Integer, String>();
	DBAdapter dbAdapter;
	LoginTask sign;
	private EditText userName, phoneNumber;

	public boolean checkRememberMe() {
		if (sharedPreferences.contains("rememberMe")) {
			if (sharedPreferences.getString("rememberMe", "").equalsIgnoreCase(
					"true")) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	public String getUsernameFromPreferences() {
		username = "";
		if (sharedPreferences.contains("username")) {
			System.out.println("Contains uname");
			username = sharedPreferences.getString("username", "");
			System.out.println("username: " + username);
		}
		return username;
	}

	public String getPasswordFromPreferences() {
		password = "";
		if (sharedPreferences.contains("password")) {
			System.out.println("Contains password");
			password = sharedPreferences.getString("password", "");
			System.out.println("password: " + password);
		}
		return password;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginscreen);

		TextView forgotpasswordTextView = (TextView) findViewById(R.id.text_forgotpassword);
		final TextView clickhereTextView = (TextView) findViewById(R.id.text_clickhere);
		TextView msgTextView1 = (TextView) findViewById(R.id.text_msg1);
		TextView msgTextView2 = (TextView) findViewById(R.id.text_msg2);
		TextView msgTextView3 = (TextView) findViewById(R.id.text_msg3);
		TextView msgContact = (TextView) findViewById(R.id.text_msg4);
		Typeface custom_font = Typeface.createFromAsset(getAssets(),
				"fonts/ufonts.com_gotham-book.ttf");
		forgotpasswordTextView.setTypeface(custom_font);
		clickhereTextView.setTypeface(custom_font);
		msgTextView1.setTypeface(custom_font);
		msgTextView2.setTypeface(custom_font);
		msgTextView3.setTypeface(custom_font);
		msgContact.setTypeface(custom_font);

		clickhereTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),
						"submit Button clicked", Toast.LENGTH_LONG).show();
				Dialog clickHereDialog = new Dialog(LoginActivity.this);

				clickHereDialog.getWindow().requestFeature(
						Window.FEATURE_NO_TITLE);

				clickHereDialog.setCancelable(true);

				clickHereDialog.setContentView(R.layout.forgotpassword);
				userName = (EditText) clickHereDialog.findViewById(R.id.uname);
				phoneNumber = (EditText) clickHereDialog.findViewById(R.id.pno);
				Button forgotPasswordSubmit = (Button) clickHereDialog
						.findViewById(R.id.submit);
				forgotPasswordSubmit
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								Toast.makeText(getApplicationContext(),
										"Submit Button clicked",
										Toast.LENGTH_LONG).show();
								new GetData().execute();
							}
						});
				clickHereDialog.show();

			}
		});

		sharedPreferences = getSharedPreferences(MyPREFERENCES,
				Context.MODE_PRIVATE);
		userEditText = (EditText) findViewById(R.id.username);
		userEditText.setText("");
		userEditText.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		passwordEditText = (EditText) findViewById(R.id.password);
		passwordEditText.setTypeface(Typeface.DEFAULT);
		passwordEditText
				.setTransformationMethod(new PasswordTransformationMethod());
		passwordEditText.setText("");
		passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		rememberMe = (CheckBox) findViewById(R.id.checkBox1);
		Button loginButton = (Button) findViewById(R.id.login);
		mProgressDialog = new ProgressDialog(LoginActivity.this);
		mProgressDialog.setMessage("Loading...");
		mProgressDialog.setIndeterminate(false);

		System.out.println("checkRememberMe: " + checkRememberMe());
		if (checkRememberMe()) {
			rememberMe.setChecked(true);
			if (sharedPreferences.contains("username")
					&& sharedPreferences.contains("password")) {
				System.out.println("has uname & pwd");
				userEditText.setText(getUsernameFromPreferences());
				passwordEditText.setText(getPasswordFromPreferences());
			}
		} else {
			rememberMe.setChecked(false);
		}
		/*
		 * TextView backTextView = (TextView)findViewById(R.id.textView1);
		 * backTextView.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { onBackPressed(); } });
		 */
		TextView phoneNumberTextView = (TextView) findViewById(R.id.textView2);
		phoneNumberTextView.setTypeface(custom_font);
		phoneNumberTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				makeCall();
			}
		});

		loginButton.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				username = userEditText.getText().toString();
				password = passwordEditText.getText().toString();
				if (!username.equals("") && !password.equals("")) {

					if (singleton.isOnline()) {
						mProgressDialog.show();
						sign = new LoginTask();
						sign.execute();
					} else {

						// offline
						Toast.makeText(getApplicationContext(),
								"Ur in Offline.", Toast.LENGTH_SHORT).show();
						Cursor c = dbAdapter.queryCredentials(username,
								password);
						if (c.getCount() != 0) {

							Log.d("curfdsfdf", "--->" + c.getCount());

							if (c.moveToFirst()) {
								LoginData data = new LoginData();
								data.setUserID(c.getInt(c
										.getColumnIndex("UserID")));

								data.setAccountID(c.getInt(c
										.getColumnIndex("AccountID")));
								data.setSubID(c.getInt(c
										.getColumnIndex("SubID")));
								data.setCompanyID(c.getInt(c
										.getColumnIndex("CompanyID")));
								data.setCompany(c.getString(c
										.getColumnIndex("Company")));
								data.setLNPCID(c.getInt(c
										.getColumnIndex("LNPCID")));
								data.setLTCID(c.getInt(c
										.getColumnIndex("LTCID")));
								data.setLCCID(c.getInt(c
										.getColumnIndex("LCCID")));
								data.setENCID(c.getInt(c
										.getColumnIndex("ENCID")));
								data.setCCID(c.getInt(c.getColumnIndex("CCID")));
								data.setMNCID(c.getInt(c
										.getColumnIndex("MNCID")));

								singleton.setUserId(c.getInt(c
										.getColumnIndex("UserID")));
								singleton.setAccountId(c.getInt(c
										.getColumnIndex("AccountID")));
								singleton.setCompanyId(c.getInt(c
										.getColumnIndex("CompanyID")));
								singleton.setLNCID(c.getInt(c
										.getColumnIndex("LNPCID")));
								singleton.setLTCID(c.getInt(c
										.getColumnIndex("LTCID")));
								singleton.setLCCID(c.getInt(c
										.getColumnIndex("LCCID")));
								singleton.setENCID(c.getInt(c
										.getColumnIndex("ENCID")));
								singleton.setCCID(c.getInt(c
										.getColumnIndex("CCID")));
								singleton.setMNCID(c.getInt(c
										.getColumnIndex("MNCID")));
								singleton.setSubscriberId(c.getInt(c
										.getColumnIndex("SubID")));
								singleton.setCompanyName(c.getString(c
										.getColumnIndex("Company")));
								Utilities.lData.add(data);
							}

							startActivity(new Intent(getApplicationContext(),
									ProjectListActivity.class));
						} else {
							Toast.makeText(
									getApplicationContext(),
									"Ur the fresh user, need internet at first time",
									Toast.LENGTH_SHORT).show();
						}

					}
				} else if (username.equals("")) {
					Toast.makeText(getApplicationContext(),
							"Username is required", Toast.LENGTH_SHORT).show();
				} else if ((!password.equals(""))) {
					Toast.makeText(getApplicationContext(),
							"Password is required", Toast.LENGTH_SHORT).show();
				} else {
					AlertDialog alertDialog = new AlertDialog.Builder(
							LoginActivity.this).create();
					alertDialog
							.setMessage("Please enter username and password.");
					alertDialog.setButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});
					alertDialog.show();
				}
			}
		});
		dbAdapter = DBAdapter.get_dbAdapter(this);
	}

	protected void makeCall() {
		Log.i("Initiating call", "");
		Intent phoneIntent = new Intent(Intent.ACTION_CALL);
		phoneIntent.setData(Uri.parse("tel:+18587559800"));

		try {
			startActivity(phoneIntent);
			finish();
			Log.i("Finished making a call", "");
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(LoginActivity.this,
					"Call failed! Please try again later.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private class LoginTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

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
					JSONObject json = new JSONObject();
					json.put("UserName", username);
					json.put("Password", password);
					return jsonDataPost.authenticate(json);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String response) {
			// System.out.println();

			mProgressDialog.dismiss();
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;
				Toast.makeText(getApplicationContext(),
						"Sorry! Server could not be reached.",
						Toast.LENGTH_LONG).show();
			} else {
				if (response == null)
					Toast.makeText(getApplicationContext(),
							"Could not authenticate! Please try again later.",
							Toast.LENGTH_SHORT).show();
				else {
					try {
						JSONObject obj = new JSONObject(response);

						String statusMessage = obj.getJSONObject("id")
								.get("StatusMessage").toString();

						/**
						 * Check response status code.
						 */
						if ((obj.getJSONObject("id").getInt("Status") == 0)
								|| (obj.getJSONObject("id").getInt("Status") == 200)) {

							/**
							 * Cache credentials to Preferences on successful
							 * login.
							 */
							Editor editor = sharedPreferences.edit();
							editor.putString("loggedOut", "false");
							if (rememberMe.isChecked()) {
								editor.putString("username", userEditText
										.getText().toString());
								editor.putString("password", passwordEditText
										.getText().toString());
								editor.putString("rememberMe", "true");
							} else {
								if ((sharedPreferences.contains("username") && sharedPreferences
										.getString("username", "").equals(
												userEditText.getText()
														.toString()))
										&& (sharedPreferences
												.contains("password") && sharedPreferences
												.getString("password", "")
												.equals(passwordEditText
														.getText().toString()))) {
									editor.remove("username");
									editor.remove("password");
									editor.putString("rememberMe", "false");
								}
							}
							editor.apply();

							/**
							 * Get Enable Tasks switch preferences.
							 */
							SharedPreferences settingPreferences = getSharedPreferences(
									SettingActivity.EnableTaskPREFERENCES,
									Context.MODE_PRIVATE);

							if (settingPreferences.contains(String
									.valueOf(singleton.getUserId()))
									&& settingPreferences.getString(
											String.valueOf(singleton
													.getUserId()), "")
											.equalsIgnoreCase("true")) {
								singleton.setEnableTasks(true);
							} else {
								singleton.setEnableTasks(false);
							}

							/**
							 * Update Singleton with server response.
							 */
							singleton.setAccountId(obj.getJSONObject("id")
									.getInt("AccountID"));
							singleton.setUserId(obj.getJSONObject("id").getInt(
									"UserID"));
							singleton.setCompanyId(obj.getJSONObject("id")
									.getInt("CompanyID"));
							singleton.setLNCID(obj.getJSONObject("id").getInt(
									"LNPCID"));
							singleton.setLTCID(obj.getJSONObject("id").getInt(
									"LTCID"));
							singleton.setLCCID(obj.getJSONObject("id").getInt(
									"LCCID"));
							singleton.setENCID(obj.getJSONObject("id").getInt(
									"ENCID"));
							singleton.setCCID(obj.getJSONObject("id").getInt(
									"CCID"));
							singleton.setMNCID(obj.getJSONObject("id").getInt(
									"MNCID"));
							singleton.setSubscriberId(obj.getJSONObject("id")
									.getInt("SubID"));
							singleton.setCompanyName(obj.getJSONObject("id")
									.getString("Company"));

							Cursor c = dbAdapter.queryCredentials(username,
									password);

							if (c != null) {
								if (c.moveToFirst()) {
									dbAdapter.deleteCredentials(username,
											password);
								}
							}
							dbAdapter.insertCredentials(username, password);

							JSONArray projects = obj.getJSONArray("p");
							singleton.getProjectsList().clear();
							projectsListStatus.clear();
							for (int i = 0; i < projects.length(); i++) {
								JSONObject curProject = projects
										.getJSONObject(i);
								singleton.getProjectsList().put(
										Integer.valueOf(curProject
												.getString("PI")),
										curProject.getString("PN"));
								projectsListStatus.put(Integer
										.valueOf(curProject.getString("PI")),
										curProject.getString("F"));
								projectsListActivityStatus.put(Integer
										.valueOf(curProject.getString("PI")),
										curProject.getString("MF"));

								Log.d("data",
										"---->" + curProject.getString("PI")
												+ "---->"
												+ curProject.getString("MF"));
							}

							int delResponse = dbAdapter.deleteProjects();
							System.out.println("Projects deletion response: "
									+ delResponse);
							if (singleton.getProjectsList().size() > 0) {
								writeProjectsToDb();
							}
							System.out.println("Projects List map: "
									+ singleton.getProjectsList());
							startActivity(new Intent(getApplicationContext(),
									ProjectListActivity.class));
							finish();
						} else if (statusMessage
								.equals("Invalid Login credentials")
								|| (obj.getJSONObject("id").getInt("Status") == 201)) {
							Toast.makeText(getApplicationContext(),
									"Invalid username or password",
									Toast.LENGTH_LONG).show();
							userEditText.setText("");
							passwordEditText.setText("");
							userEditText.setHint("Enter correct username");
							passwordEditText.setHint("Enter correct password");
							userEditText.setHintTextColor(getResources()
									.getColor(color.holo_green_dark));
							passwordEditText.setHintTextColor(getResources()
									.getColor(color.holo_green_dark));
						} else if (statusMessage
								.equals("Username or password incorrect!")) {
							Toast.makeText(getApplicationContext(),
									"Password incorrect", Toast.LENGTH_LONG)
									.show();
							passwordEditText.setText("");
							passwordEditText.setHint("Enter correct password");
							passwordEditText.setHintTextColor(getResources()
									.getColor(color.holo_green_dark));
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void writeProjectsToDb() {
		String[] values = new String[singleton.getProjectsList().size()];
		long insertResponse = 0;
		int i = 0;
		for (Integer key : singleton.getProjectsList().keySet()) {
			values[i] = singleton.getProjectsList().get(key);
			int TF = projectsListStatus.get(key).equals("T") ? 1 : 0;
			int AF = projectsListActivityStatus.get(key).equals("T") ? 1 : 0;
			// System.out.println(key + " " + values[i] + " " + TF + " " + AF);
			insertResponse = dbAdapter.insertProject(key, values[i++],
					projectsListStatus.get(key).equals("T") ? 1 : 0,
					projectsListActivityStatus.get(key).equals("T") ? 1 : 0);
		}
		System.out.println("Projects insertion response: " + insertResponse);
	}

	// public void readProjectsFromDb() {
	// Cursor c = dbAdapter.queryProjects();
	// if (c != null) {
	// if (c.moveToFirst()) {
	// do {
	// String projectName = c.getString(c.getColumnIndex("PName"));
	// int projectID = c.getInt(c.getColumnIndex("PID"));
	// String hasData = c.getInt(c.getColumnIndex("hasData")) == 1 ? "T"
	// : "F";
	// String hasActivities = c.getInt(c
	// .getColumnIndex("hasActivities")) == 1 ? "T" : "F";
	// singleton.getProjectsList().put(projectID, projectName);
	// projectsListStatus.put(projectID, hasData);
	// projectsListActivityStatus.put(projectID, hasActivities);
	// // System.out.println("Name: " + projectName + ", ID: " +
	// // projectID + " hasData: " + hasData);
	// } while (c.moveToNext());
	// } else {
	// // System.out.println("No Projects found in DB for this user account.");
	// }
	// }
	// dbAdapter.Close();
	// }
	public class GetData extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

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
					JSONObject json = new JSONObject();
					json.put("Phones", "1234567890");
					json.put("username", "vishu.ghanakota");
					Log.d("values", "--->" + phoneNumber.getText().toString()
							+ "   " + userName.getText().toString());

					return jsonDataPost.passwordRecovery(json);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(final String response) {
			Log.d("string", "--->" + response);

			try {
				JSONObject obj = new JSONObject(response);
				Log.d("response", "--->" + obj.toString());
				String str = obj.getString("Message");
				System.out.println("Server Response : " + str);
				Log.d("final result", "---->" + str);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	//

	// }
}