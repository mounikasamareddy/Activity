package com.notevault.activities;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;

import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddActivity extends Activity{

	Singleton singleton;
	EditText editText;
	String newActivityName;
	ServerUtilities jsonDataPost = new ServerUtilities();
	DBAdapter dbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dataedit_dialog);

		singleton = Singleton.getInstance();
		dbAdapter = DBAdapter.get_dbAdapter(this);
		TextView textView = (TextView)findViewById(R.id.textdata);
		textView.setText("Add Activity");
		editText = (EditText)findViewById(R.id.editText1);
		editText.setHint("Enter a short description of the activity");

		LinearLayout addImageLayout = (LinearLayout)findViewById(R.id.image_layout);
		addImageLayout.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				newActivityName = editText.getText().toString().trim();
				if(newActivityName.equals("")){
					AlertDialog alertDialog = new AlertDialog.Builder(AddActivity.this).create();
					alertDialog.setMessage("Please enter an activity name");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					alertDialog.show();
				}else{
					AddActivityProject addActivity = new AddActivityProject();
					addActivity.execute();
				}
			}
		});

		TextView cancel = (TextView)findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	private class AddActivityProject extends AsyncTask<Void, Void, String> {

		//String addActivityResponseString;

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
						return false;
					}
				};
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(hv);


				try {
                    String GMTdateTime = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(new SimpleDateFormat("yyyyMMd").parse(singleton.getCurrentSelectedDate())) + " " + new SimpleDateFormat("HH:mm:ss").format(new Date());
                    JSONObject jsonAddActivity = new JSONObject();
                    jsonAddActivity.put("TaskId", singleton.getSelectedTaskID());
                    jsonAddActivity.put("Name", newActivityName);
                    jsonAddActivity.put("UserId", singleton.getUserId());
                    jsonAddActivity.put("DateCreated", GMTdateTime);
                    jsonAddActivity.put("ProjectDay", singleton.getCurrentSelectedDate());

                    System.out.println("Request: jsonAddActivity: "+jsonAddActivity);
                    //System.out.println("GMT Date: %%%%%%%%%%%%%%%%%%%%%%%%%%%% : "+ GMTdateTime);
                    return jsonDataPost.addActivityToTask(jsonAddActivity);

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
			System.out.println("Response add activity.................."+param);
			try {
                JSONObject json = new JSONObject(param);
                int Status = json.getInt("Status");
				if(Status == 0 || Status == 200){
					singleton.setSelectedActivityID(json.getString("AI"));
					singleton.getActivitiesList().put(json.getString("AI"), Singleton.toTitleCase(newActivityName));
					System.out.println("...data......"+newActivityName+"..........activityid........." +singleton.getSelectedActivityID());								
					singleton.setSelectedActivityName(Singleton.toTitleCase(newActivityName));
					ActivitiesListActivity.activityListStatus.put(json.getString("AI"), "F");
                    TasksListActivity.taskListStatus.put(singleton.getSelectedTaskID(), "T");
					long insertResponse = dbAdapter.insertActivity(json.getString("AI"), Singleton.toTitleCase(newActivityName), singleton.getSelectedTaskID(), singleton.getCurrentSelectedDate(), 0, singleton.getUserId());
					dbAdapter.updateTask(singleton.getSelectedProjectID(), singleton.getSelectedTaskID(), singleton.getCurrentSelectedDate());
					Intent intent = new Intent(AddActivity.this, EntriesListActivity.class);
					startActivity(intent);
					finish();
				}else if(Status == 201){
					Toast.makeText(getApplicationContext(), "An activity with this name already exists!", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(getApplicationContext(), "An error occurred while adding activity!", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}