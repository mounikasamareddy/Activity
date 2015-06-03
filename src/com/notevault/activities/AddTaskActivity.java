package com.notevault.activities;

import java.security.SecureRandom;

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
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;

public class AddTaskActivity extends Activity{

	Singleton singleton;
	TextView textView, cancel;
	EditText editText;
	ServerUtilities jsonDataPost = new ServerUtilities();
	String newTaskName;
	Bundle bundle;
	DBAdapter dbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dataedit_dialog);

		singleton = Singleton.getInstance();
		dbAdapter = DBAdapter.get_dbAdapter(this);
		textView = (TextView)findViewById(R.id.textdata);
		textView.setText("Add Task");
		editText = (EditText)findViewById(R.id.editText1);
		editText.setHint("Add Task");

		LinearLayout addImageLayout = (LinearLayout)findViewById(R.id.image_layout);
		addImageLayout.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				
					newTaskName = editText.getText().toString().trim();
					System.out.println("new task name......." + newTaskName);
					if(newTaskName.equals("")){
						AlertDialog alertDialog = new AlertDialog.Builder(AddTaskActivity.this).create();
						alertDialog.setMessage("Please enter task name");
						alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {

							}
						});
						alertDialog.show();
					}else{
						if(singleton.isOnline()) {
						AddTaskProject addTask = new AddTaskProject();
						addTask.execute();
						}
					
				else{
					Toast.makeText(getApplicationContext(), "ur in offline!", Toast.LENGTH_LONG).show();
					newTaskName = editText.getText().toString().trim();
					System.out.println("new task name......." + newTaskName);
					if(newTaskName.equals("")){
						AlertDialog alertDialog = new AlertDialog.Builder(AddTaskActivity.this).create();
						alertDialog.setMessage("Please enter task name");
						alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {

							}
						});
						alertDialog.show();
					}else{
						Log.d("new task","---->"+Singleton.toTitleCase(newTaskName)+"   "+singleton.getSelectedProjectID());
						dbAdapter.insertTask(0, newTaskName, singleton.getSelectedProjectID(), 0);
						dbAdapter.updateProject(singleton.getSelectedProjectID(),1,0);
						singleton.setReloadPage(true);
						onBackPressed();
						
					}}
				}
			}
		});
		cancel = (TextView)findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	private class AddTaskProject extends AsyncTask<Void, Void, String> {

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
					JSONObject jsonAddTask = new JSONObject();
					jsonAddTask.put("ProjectId", singleton.getSelectedProjectID());
					jsonAddTask.put("Name", newTaskName);
					jsonAddTask.put("UserId", singleton.getUserId());
					return jsonDataPost.addTaskToProject(jsonAddTask);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "An error occurred while adding task!", Toast.LENGTH_LONG).show();
				onBackPressed();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String response) {

        System.out.println("Add task server response: "+response);
			int ID = 0;
			try {
				JSONObject jObject = new JSONObject(response);
				//System.out.println("TId: "+jObject.getString("TId"));
				int Status = jObject.getInt("Status");
				if(Status == 0 || Status == 200){
					ID = jObject.getInt("TI");
					
					singleton.getTaskList().put(ID, Singleton.toTitleCase(newTaskName));
					TasksListActivity.taskListStatus.put(ID, "false");
					Log.d("test","------>");
					dbAdapter.insertTask(ID, Singleton.toTitleCase(newTaskName), singleton.getSelectedProjectID(), 0);
					
					dbAdapter.updateProject(singleton.getSelectedProjectID(), LoginActivity.projectsListStatus.get(singleton.getSelectedProjectID()).equals("T")?1:0, LoginActivity.projectsListActivityStatus.get(singleton.getSelectedProjectID()).equals("T")?1:0 );
					singleton.setReloadPage(true);
				}else if(Status == 201){
					Toast.makeText(getApplicationContext(), "A task with this name already exists!", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(getApplicationContext(), "An error occurred while adding task!", Toast.LENGTH_LONG).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			onBackPressed();		
		}
	}
}