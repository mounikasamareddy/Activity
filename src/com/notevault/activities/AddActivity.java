package com.notevault.activities;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.notevault.arraylistsupportclasses.ActivityDB;
import com.notevault.arraylistsupportclasses.EntityDB;
import com.notevault.arraylistsupportclasses.EntityData;
import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;

public class AddActivity extends Activity {

	Singleton singleton;
	EditText editText;
	String newActivityName;
	SharedPreferences settingPreferences;
	ServerUtilities jsonDataPost = new ServerUtilities();
	DBAdapter dbAdapter;
	LinearLayout shift;
	String shiftdata[] = { "None", "One", "Two", "Three" };
	ArrayAdapter<String> adapter;
	Spinner spinner;
	int spinnerSelectedItem;
int shiftval=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dataedit_dialog);

		shift = (LinearLayout) findViewById(R.id.shift);

		singleton = Singleton.getInstance();
		dbAdapter = DBAdapter.get_dbAdapter(this);

		
		settingPreferences = getSharedPreferences(
				SettingActivity.EnableSHIFTPREFERENCES, Context.MODE_PRIVATE);
		if (settingPreferences.contains(String.valueOf(singleton.getUserId()))) {
			if (settingPreferences.getString(
					String.valueOf(singleton.getUserId()), "")
					.equalsIgnoreCase("true")) {
				singleton.setEnableShiftTracking(true);
			}
		}
		Log.d("shift", "--->" + singleton.isEnableShiftTracking());

		if (singleton.isEnableShiftTracking()) {
			shift.setVisibility(View.VISIBLE);
			spinner = (Spinner) findViewById(R.id.shiftspinner);
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, shiftdata);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);

			for (int i = 0; i < shiftdata.length; i++) {
				if (shiftdata[i].equals(singleton.getResentShiftItem())) {
					spinnerSelectedItem = i;
				}

			}

			spinner.setSelection(spinnerSelectedItem);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View arg1,
						int pos, long arg3) {
					
					shiftval=pos;
					Log.d("shif selected item", "-->"
							+ parent.getItemAtPosition(pos).toString());
					singleton.setResentShiftItem(parent.getItemAtPosition(pos)
							.toString());

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}

			});

		} else {
			shift.setVisibility(View.GONE);
		}
		TextView textView = (TextView) findViewById(R.id.textdata);
		textView.setText("Add Activity");
		editText = (EditText) findViewById(R.id.editText1);
		editText.setHint("Enter a short description of the activity");

		LinearLayout addImageLayout = (LinearLayout) findViewById(R.id.image_layout);
		addImageLayout.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				newActivityName = editText.getText().toString().trim();
				if (newActivityName.equals("")) {
					AlertDialog alertDialog = new AlertDialog.Builder(
							AddActivity.this).create();
					alertDialog.setMessage("Please enter an activity name");
					alertDialog.setButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});
					alertDialog.show();
				} else {
					if (singleton.isOnline()) {
						Log.d("shift tracking","--->"+singleton.isEnableShiftTracking());
						if (singleton.isEnableShiftTracking()) {
							Log.d("add activity","--->"+newActivityName);
							AddActivityProjectShift addActivityShift = new AddActivityProjectShift();
							addActivityShift.execute();
							
						} else {
							Log.d("add activity","--->"+newActivityName);
							AddActivityProject addActivity = new AddActivityProject();
							addActivity.execute();
						}
					} else {
						Insertintodb();
					}

				}
			}
		});

		TextView cancel = (TextView) findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
	}

	

	private class AddActivityProject extends AsyncTask<Void, Void, String> {

		// String addActivityResponseString;

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
					String GMTdateTime = new SimpleDateFormat("yyyy-MM-dd",
							Locale.ENGLISH)
							.format(new SimpleDateFormat("yyyyMMd")
									.parse(singleton.getCurrentSelectedDate()))
							+ " "
							+ new SimpleDateFormat("HH:mm:ss")
									.format(new Date());
					JSONObject jsonAddActivity = new JSONObject();
					jsonAddActivity
							.put("TaskId", singleton.getSelectedTaskID());
					jsonAddActivity.put("Name", newActivityName);
					jsonAddActivity.put("UserId", singleton.getUserId());
					jsonAddActivity.put("DateCreated", GMTdateTime);
					jsonAddActivity.put("ProjectDay",
							singleton.getCurrentSelectedDate());

					System.out.println("Request: jsonAddActivity: "
							+ jsonAddActivity);
					// System.out.println("GMT Date: %%%%%%%%%%%%%%%%%%%%%%%%%%%% : "+
					// GMTdateTime);
					return jsonDataPost.addActivityToTask(jsonAddActivity);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String param) {
			System.out.println("Response add activity.................."
					+ param);
			try {
				JSONObject json = new JSONObject(param);
				int Status = json.getInt("Status");
				if (Status == 0 || Status == 200) {
					singleton.setSelectedActivityID(Integer.parseInt(json
							.getString("AI")));
					singleton.getActivitiesList().put(json.getString("AI"),
							Singleton.toTitleCase(newActivityName));
					System.out.println("...data......" + newActivityName
							+ "..........activityid........."
							+ singleton.getSelectedActivityID());
					singleton.setSelectedActivityName(Singleton
							.toTitleCase(newActivityName));
					ActivitiesListActivity.activityListStatus.put(
							json.getString("AI"), "F");
					TasksListActivity.taskListStatus.put(
							singleton.getSelectedTaskID(), "T");
					long insertResponse = dbAdapter.insertActivity(
							json.getString("AI"),
							Singleton.toTitleCase(newActivityName),
							singleton.getSelectedTaskID(),
							singleton.getCurrentSelectedDate(), 0,
							singleton.getUserId());
					Log.d("insert", "--->" + insertResponse);
					dbAdapter.updateTask(singleton.getSelectedProjectID(),
							singleton.getSelectedTaskID(),
							singleton.getCurrentSelectedDate());
					Intent intent = new Intent(AddActivity.this,
							EntriesListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("index", 1);

					intent.putExtras(bundle);
					startActivity(intent);
					finish();
				} else if (Status == 201) {
					Toast.makeText(getApplicationContext(),
							"An activity with this name already exists!",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(),
							"An error occurred while adding activity!",
							Toast.LENGTH_LONG).show();
				}
			
				
				} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
	}
	private class AddActivityProjectShift extends AsyncTask<Void, Void, String> {

		// String addActivityResponseString;

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
					String GMTdateTime = new SimpleDateFormat("yyyy-MM-dd",
							Locale.ENGLISH)
							.format(new SimpleDateFormat("yyyyMMd")
									.parse(singleton.getCurrentSelectedDate()))
							+ " "
							+ new SimpleDateFormat("HH:mm:ss")
									.format(new Date());
					JSONObject jsonAddActivityShift = new JSONObject();
					jsonAddActivityShift
							.put("TaskId", singleton.getSelectedTaskID());
					
					jsonAddActivityShift.put("DateCreated", GMTdateTime);
					
					jsonAddActivityShift.put("Name", newActivityName);
					
					jsonAddActivityShift.put("UserId", singleton.getUserId());
					jsonAddActivityShift.put("Shift", shiftval);
					
					Log.d("Request: jsonAddActivity: ","--->"
							+ jsonAddActivityShift);
					// System.out.println("GMT Date: %%%%%%%%%%%%%%%%%%%%%%%%%%%% : "+
					// GMTdateTime);
					return jsonDataPost.addActivityShiftToTask(jsonAddActivityShift);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(String param) {
			Log.d("Response add activity shift ..","--->"
					+ param);
			try {
				JSONObject json = new JSONObject(param);
				Log.d("status","--->"+json.getInt("AI"));
				int Status = json.getInt("Status");
				if (Status == 0 || Status == 200) {
					singleton.setSelectedActivityID(Integer.parseInt(json
							.getString("AI")));
					singleton.getActivitiesList().put(json.getString("AI"),
							Singleton.toTitleCase(newActivityName));
					System.out.println("...data......" + newActivityName
							+ "..........activityid........."
							+ singleton.getSelectedActivityID());
					singleton.setSelectedActivityName(Singleton
							.toTitleCase(newActivityName));
					ActivitiesListActivity.activityListStatus.put(
							json.getString("AI"), "F");
					TasksListActivity.taskListStatus.put(
							singleton.getSelectedTaskID(), "T");
					long insertResponse = dbAdapter.insertActivity(
							json.getString("AI"),
							Singleton.toTitleCase(newActivityName),
							singleton.getSelectedTaskID(),
							singleton.getCurrentSelectedDate(), 0,
							singleton.getUserId());
					Log.d("insert", "--->" + insertResponse);
					dbAdapter.updateTask(singleton.getSelectedProjectID(),
							singleton.getSelectedTaskID(),
							singleton.getCurrentSelectedDate());
					Intent intent = new Intent(AddActivity.this,
							EntriesListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("index", 1);

					intent.putExtras(bundle);
					startActivity(intent);
					finish();
					
				} else if (Status == 201) {
					Toast.makeText(getApplicationContext(),
							"An activity with this name already exists!",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(),
							"An error occurred while adding activity!",
							Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
	}
	protected void Insertintodb() {
		// Log.d("taskdata","---->"+singleton.getSelectedTaskID());
		List<ActivityDB> data;
		Log.d("checking",
				"-->"
						+ singleton.getSelectedTaskID()
						+ "  "
						+ singleton
								.getSelectedTaskIdentityoffline()
						+ " "
						+ singleton.getSelectedActivityID());
if(singleton.isEnableShiftTracking())
{
	long act1 = dbAdapter.inserActivityoffline(0,
			newActivityName.replace("\\", ""),
			singleton.getSelectedTaskID(), 0, "offline",shiftval);
	Log.d("activity created with task offline status",
			"--->" + act1 + " "
					+ singleton.getSelectedTaskID());
}else{
	long act1 = dbAdapter.inserActivityoffline(0,
			newActivityName.replace("\\", ""),
			singleton.getSelectedTaskID(), 0, "offline",10);
	Log.d("activity created with task offline status",
			"--->" + act1 + " "
					+ singleton.getSelectedTaskID());
}
		
			
			data = dbAdapter.getAllActivityRecords();
			for (ActivityDB val : data) {
				Log.d("activity created with task id=0", "--->"
						+ val.getAIdentity());
				singleton
						.setselectedActivityIdentityoffline(val
								.getAIdentity());
			}
			singleton.setSelectedActivityID(0);

			singleton.setSelectedActivityName(newActivityName);

		

		dbAdapter.updateTask(singleton.getSelectedProjectID(),
				singleton.getSelectedTaskID(),
				singleton.getCurrentSelectedDate());
		Intent intent = new Intent(AddActivity.this,
				EntriesListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("index", 1);

		intent.putExtras(bundle);
		startActivity(intent);

		finish();

	
		
	}
}