package com.notevault.activities;

import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.notevault.adapter.TaskAdapter;
import com.notevault.arraylistsupportclasses.TNetworkData;
import com.notevault.arraylistsupportclasses.TaskData;
import com.notevault.arraylistsupportclasses.TaskNetworkDB;
import com.notevault.arraylistsupportclasses.TasksDB;
import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;
import com.notevault.support.Utilities;

public class TasksListActivity extends Activity {

	Singleton singleton;
	int keys[];
	String values[];
	ListView taskListView;
	ServerUtilities jsonDataPost = new ServerUtilities();
	public static HashMap<Integer, String> taskListStatus = new HashMap<Integer, String>();
	DBAdapter dbAdapter;
	private ProgressDialog mProgressDialog;
	TaskAdapter tAdapter;
	int pid;
	String date1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.tasks_activity);

		mProgressDialog = new ProgressDialog(TasksListActivity.this);
		System.out.println("onCreate called.");
		singleton = Singleton.getInstance();
		TextView projectName = (TextView) findViewById(R.id.projectname_text);
		projectName.setText(singleton.getSelectedProjectName());

		taskListView = (ListView) findViewById(R.id.list);

		dbAdapter = DBAdapter.get_dbAdapter(this);
		if (singleton.isOnline()) {
			GetProjectTask projectTask = new GetProjectTask();
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.show();
			projectTask.execute();
		} else {
			Toast.makeText(getApplicationContext(), "Ur in offline",
					Toast.LENGTH_LONG).show();
			readTasksFromDb();

		}

		LinearLayout addImageLayout = (LinearLayout) findViewById(R.id.image_layout);
		addImageLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (singleton.isOnline()) {
					Intent taskIntent = new Intent(TasksListActivity.this,
							AddTaskActivity.class);
					startActivity(taskIntent);
				} else {
					Toast.makeText(getApplicationContext(), "Ur in  offline!",
							Toast.LENGTH_LONG).show();
					Intent taskIntent = new Intent(TasksListActivity.this,
							AddTaskActivity.class);
					startActivity(taskIntent);
				}
			}
		});

		LinearLayout backLayout = (LinearLayout) findViewById(R.id.back_layout);
		backLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

	}

	@Override
	protected void onResume() {
		System.out.println("Task activity onResume called.");
		super.onResume();
		if (singleton.isOnline()) {

			if (singleton.isReloadPage()) {

				int size = singleton.getTaskList().size();
				keys = new int[size];
				values = new String[size];
				int i = 0;
				// System.out.println("getTaskList(): "+singleton.getTaskList());
				for (Integer key : singleton.getTaskList().keySet()) {
					keys[i] = key;
					values[i++] = singleton.getTaskList().get(key);
				}
				singleton.setReloadPage(false);
				processListAndSetAdapter();

			}
		} else {

			if (singleton.isReloadPage()) {
				System.out.println("Offline");
				taskListView = (ListView) findViewById(R.id.list);

				Utilities.tdata.clear();
				List<TasksDB> data = dbAdapter.getAllTaskRecords(pid);

				for (TasksDB val : data) {
					TaskData details = new TaskData(val.getTID(),
							val.getTName(), val.getHasData(),
							val.getTIdentity());
					details.setTIdentity(val.getTIdentity());
					details.setTID(val.getTID());
					details.setTName(val.getTName());
					details.setHasData(val.getHasData());

					Utilities.tdata.add(details);

				}
				Log.d("arraylength", "---->" + Utilities.tdata.size());
				dbAdapter.Close();

				Collections.sort(Utilities.tdata, new TaskData.OrderByTName());
				tAdapter = new TaskAdapter(this);

				taskListView.setAdapter(tAdapter);

				singleton.setReloadPage(false);
			}
		}

	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return singleton.getTaskList().size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (values != null && values.length > 0) {
				LayoutInflater li = getLayoutInflater();
				convertView = li.inflate(R.layout.customlist, null);
				TextView tv = (TextView) convertView
						.findViewById(R.id.textView1);
				tv.setText(values[position]);

				ImageView orangeArrow = (ImageView) convertView
						.findViewById(R.id.name_imageView2);
				ImageView greyArrow = (ImageView) convertView
						.findViewById(R.id.name_imageView1);
				if (taskListStatus.get(keys[position]).equals("T")) {
					orangeArrow.setVisibility(View.VISIBLE);
					greyArrow.setVisibility(View.INVISIBLE);
				}

				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						System.out.println("Task on click called!");

						Date curDate = new Date();
						// System.out.println("Cur Date : ##################################### : "+
						// curDate);
						SimpleDateFormat format1 = new SimpleDateFormat(
								"dd-MM-yyyy");
						try {
							singleton.setCurrentSelectedDateFormatted(format1
									.parse(format1.format(curDate)).toString()
									.replace(" 00:00:00 GMT+05:30", ","));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						singleton.setCurrentSelectedDate(new SimpleDateFormat(
								"yyyyMMdd").format(curDate));
						Intent intent = new Intent(TasksListActivity.this,
								ActivitiesListActivity.class);
						singleton.setSelectedTaskName(values[position]);
						singleton.setSelectedTaskID(keys[position]);
						System.err.println("TaskName: "
								+ singleton.getSelectedTaskName());
						System.err.println("TaskID: "
								+ singleton.getSelectedTaskID());
						startActivity(intent);
					}
				});
				return convertView;
			}
			return null;
		}
	}

	private class GetProjectTask extends AsyncTask<Void, Void, String> {

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
					JSONObject jsonTaskRequest = new JSONObject();
					jsonTaskRequest.put("ProjectId",
							singleton.getSelectedProjectID());
					// jsonTaskRequest.put("UserId", singleton.getUserId());
					jsonTaskRequest.put("ProjectDay",
							singleton.getCurrentSelectedDate());
					return jsonDataPost.getAllProjectTasks(jsonTaskRequest);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				readTasksFromDb();
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String response) {
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;
				Toast.makeText(getApplicationContext(), "Ur in offline",
						Toast.LENGTH_LONG).show();

			} else {
				String taskResponse = response;
				if (taskResponse != null) {
					try {
						Object json = new JSONTokener(taskResponse).nextValue();
						if (json instanceof JSONObject) {
							System.out.println("Response is an object.");
						} else if (json instanceof JSONArray) {
							System.out.println("Response is an array.");
						}

						JSONObject taskJSONResponse = new JSONObject(
								taskResponse);
						singleton.getTaskList().clear();
						taskListStatus.clear();

						if ((taskJSONResponse.getInt("Status") == 0)
								|| (taskJSONResponse.getInt("Status") == 200)) {

							JSONArray responseTasksArray = taskJSONResponse
									.getJSONArray("tasks");

							int responseTasksArrayLength = responseTasksArray
									.length();
							keys = new int[responseTasksArrayLength];
							values = new String[responseTasksArrayLength];
							for (int i = 0; i < responseTasksArrayLength; i++) {
								JSONObject task = responseTasksArray
										.getJSONObject(i);// JSONObject
								keys[i] = task.getInt("TI");
								values[i] = task.getString("TN");
								singleton.getTaskList().put(keys[i], values[i]);
								taskListStatus
										.put(keys[i], task.getString("F"));
							}

							int delResponse = dbAdapter.deleteTasks(singleton
									.getSelectedProjectID());
							Log.d("Tasks deletion response: ", "---->"
									+ delResponse);
							if (singleton.getTaskList().size() > 0) {
								writeTasksToDb();
							}
							processListAndSetAdapter();
						} else {
							Toast.makeText(getApplicationContext(),
									"Tasks not found!", Toast.LENGTH_LONG)
									.show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					System.out
							.println("An error occurred! Could not fetch tasks");
				}
			}
			mProgressDialog.dismiss();
		}
	}

	public void processListAndSetAdapter() {
		// values = singleton.getTaskList().keySet().toArray(new
		// String[singleton.getTaskList().size()]);
		// Arrays.sort(values);
		if (values != null) {
			System.out.println("List of Tasks: " + Arrays.toString(values));

			/**
			 * Applying bubble sort for sorting tasks.
			 */
			int tempKey;
			String tempValue;
			// System.out.println("values.length: " + values.length);

			for (int l = 0; l < values.length; l++) {
				for (int j = 0; j < values.length - l - 1; j++) {
					// System.out.println("_____ J: "+j);
					if (values[j].compareToIgnoreCase(values[j + 1]) > 0) {
						tempKey = keys[j];
						keys[j] = keys[j + 1];
						keys[j + 1] = tempKey;
						tempValue = values[j];
						values[j] = values[j + 1];
						values[j + 1] = tempValue;
					}
				}
			}
		}
		MyAdapter myad = new MyAdapter();
		taskListView = (ListView) findViewById(R.id.list);
		taskListView.setAdapter(myad);
	}

	public void writeTasksToDb() {
		// String[] values = singleton.getTaskList().keySet().toArray(new
		// String[singleton.getTaskList().size()]);
		long insertResponse = 0;
		int Flag;
		for (int key : keys) {
			Flag = taskListStatus.get(key).equals("T") ? 1 : 0;
			// System.out.println(key + " " + singleton.getTaskList().get(key) +
			// " " + singleton.getSelectedProjectID() + " " + Flag);
			insertResponse = dbAdapter.insertTask(key, singleton.getTaskList()
					.get(key), singleton.getSelectedProjectID(), Flag);
		}
		System.out.println("Tasks insertion response: " + insertResponse);
	}

	public void readTasksFromDb() {

		tAdapter = new TaskAdapter(TasksListActivity.this);
		Log.d("here", "---->" + singleton.getSelectedProjectID());
		pid = singleton.getSelectedProjectID();
		date1 = singleton.getCurrentSelectedDate();

		Utilities.tdata.clear();
		Log.d("length", "--->" + Utilities.tdata.size());
		//specify string "" becoz that "getAllTaskRecords" using in other place also there need to pass status
		List<TasksDB> data = dbAdapter.getAllTaskRecords(pid);
		Log.d("length", "--->" + data.size());
		for (TasksDB val : data) {
			TaskData details = new TaskData(val.getTID(), val.getTName(),
					val.getHasData(), val.getTIdentity());
			details.setTID(val.getTID());
			details.setTIdentity(val.getTIdentity());
			details.setTName(val.getTName());
			details.setHasData(val.getHasData());
			details.setTIdentity(val.getTIdentity());
			details.setStatus(val.getStatus());
			Utilities.tdata.add(details);

		}
		Log.d("arraylength", "---->" + Utilities.tdata.size());
		dbAdapter.Close();
		for (int i = 0; i < Utilities.tdata.size(); i++) {
			Log.d("taskdataid", "---->" + Utilities.tdata.get(i).getTID());
			Log.d("taskdataname", "---->"
					+ Utilities.tdata.get(i).getTIdentity());
			Log.d("taskdataname", "---->" + Utilities.tdata.get(i).getStatus());

		}
		Collections.sort(Utilities.tdata, new TaskData.OrderByTName());
		Log.d("taskdata", "---->" + Utilities.tdata);

		taskListView.setAdapter(tAdapter);

	}

	
}