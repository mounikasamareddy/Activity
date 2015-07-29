package com.notevault.activities;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.notevault.arraylistsupportclasses.ActivityDB;
import com.notevault.arraylistsupportclasses.ActivityData;
import com.notevault.arraylistsupportclasses.TasksDB;
import com.notevault.arraysupportclasses.CalenderActivity;
import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;
import com.notevault.support.Utilities;

public class ActivitiesListActivity extends Activity {

	Singleton singleton;
	String Projectday;
	public static String values[];
	public static String keys[];
	public static String dates[];
	String dateFilteredValues[];
	String dateFilteredKeys[];
	ImageView calendarImageView;
	ImageView orangeDot;
	ServerUtilities jsonDataPost = new ServerUtilities();
	TextView dateTextView;
	Dialog calendarDialog;
	String tempKey, tempValue;
	private Calendar _calendar;
	private int month, year;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	GridCellAdapter adapter;
	EditText activityNameToCopy;
	private TextView currentMonth,text1;
	public String statusMessageForActivities;
	private static final String dateTemplate = "MMMM yyyy";
	RelativeLayout calendarClick;
	private ListView customListView;
	private SwipeListAdapter listAdapter;
	private ProgressDialog mdialog;
	public static HashMap<String, String> activityListStatus = new HashMap<String, String>();
	public String yearMonthdate;
	DBAdapter dbAdapter;
	List<CalenderActivity> data1;
	private TextView backTask;
	boolean newName = false;
	LinearLayout messageDefaultHint,emptyText,llayout;
	String datevalues[]= new String[0];
	SharedPreferences settingPreferences;
	int k = 0;
	String runningYMDdata;
	String shiftdata[] = { "None", "One", "Two", "Three" };
	ArrayAdapter<String> spinnerAdapter;
	Spinner spinner;
	int spinnerSelectedItem;
	Button laborSummary;
	
int shiftval=0;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		
		setContentView(R.layout.taskactivities);
		laborSummary=(Button)findViewById(R.id.laboursummary);
		emptyText=(LinearLayout) findViewById(R.id.emptytext);
		llayout=(LinearLayout) findViewById(R.id.llayout);
		messageDefaultHint = (LinearLayout) findViewById(R.id.activity_layout1);
		messageDefaultHint.setVisibility(View.VISIBLE);
		singleton = Singleton.getInstance();
		
		settingPreferences = getSharedPreferences(
				SettingActivity.EnableSHIFTPREFERENCES, Context.MODE_PRIVATE);
		if (settingPreferences.contains(String.valueOf(singleton.getUserId()))) {
			if (settingPreferences.getString(
					String.valueOf(singleton.getUserId()), "")
					.equalsIgnoreCase("true")) {
				singleton.setEnableShiftTracking(true);
			}
		}
		laborSummary.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent= new Intent(getApplicationContext(),LaborSummary.class);
				startActivity(intent);
				
			}
		});
		TextView projectName = (TextView) findViewById(R.id.projectname_text);

		// Log.d("projectname", "---->" + singleton.getSelectedProjectName());
		projectName.setText(singleton.getSelectedProjectName());
		ImageView breadcrumb_separator = (ImageView) findViewById(R.id.breadcrumb_separator);
		TextView taskName = (TextView) findViewById(R.id.task_text);
		// ****************
		backTask = (TextView) findViewById(R.id.textView1);
		if (singleton.isEnableTasks()) {

			backTask.setText("TASKS");
			taskName.setText(singleton.getSelectedTaskName());
			breadcrumb_separator.setVisibility(View.VISIBLE);
			taskName.setVisibility(View.VISIBLE);
		} else {

			backTask.setText("PROJECTS");

		}

		dateTextView = (TextView) findViewById(R.id.Calendar_text);
		yearMonthdate = singleton.getCurrentSelectedDate();
		dateTextView.setText(singleton.getCurrentSelectedDateFormatted());

		dbAdapter = DBAdapter.get_dbAdapter(this);
		if (singleton.isOnline()) {

			
			
			new Datesvalues().execute();
			
			if (singleton.isEnableTasks()) {



				GetTaskActivities taskActivities = new GetTaskActivities();
				taskActivities.execute();
				

				
				
			} else {

				new ProjectData().execute();
				
			}
		} else {

			//Log.d("offline", "---->");
			String str = singleton.getCurrentSelectedDate();
			
				readDbData(str);
			
		}

		calendarClick = (RelativeLayout) findViewById(R.id.calendare_layout);
		calendarClick.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				calendarDialog = new Dialog(ActivitiesListActivity.this);
				// calendarDialog.setTitle("Select Date:");
				calendarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				calendarDialog.setContentView(R.layout.my_calendar_view);
				_calendar = Calendar.getInstance(Locale.getDefault());
				month = _calendar.get(Calendar.MONTH) + 1;
				year = _calendar.get(Calendar.YEAR);
				// selectedDayMonthYearButton = (Button) calenderDialog
				// .findViewById(R.id.selectedDayMonthYear);
				// Log.d(tag, "Calendar Instance:= " + "Month: " + month + " " +
				// "Year: "+ year);
				prevMonth = (ImageView) calendarDialog
						.findViewById(R.id.prevMonth);
				prevMonth.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v1) {
						if (month <= 1) {
							month = 12;
							year--;
						} else {
							month--;
						}
						/*
						 * Log.d(tag, "Setting Prev Month in GridCellAdapter: "
						 * + "Month: " + month + " Year: " + year);
						 */
						setGridCellAdapterToDate(month, year);
					}

					private void setGridCellAdapterToDate(int month, int year) {
						adapter = new GridCellAdapter(getApplicationContext(),
								R.id.calendar_day_gridcell, month, year);
						_calendar.set(year, month - 1,
								_calendar.get(Calendar.DAY_OF_MONTH));
						currentMonth.setText(DateFormat.format(dateTemplate,
								_calendar.getTime()));
						adapter.notifyDataSetChanged();
						calendarView.setAdapter(adapter);
					}
				});

				currentMonth = (TextView) calendarDialog
						.findViewById(R.id.currentMonth);
				currentMonth.setText(DateFormat.format(dateTemplate,
						_calendar.getTime()));

				nextMonth = (ImageView) calendarDialog
						.findViewById(R.id.nextMonth);
				nextMonth.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v1) {
						if (month > 11) {
							month = 1;
							year++;
						} else {
							month++;
						}
						/*
						 * Log.d(tag, "Setting Next Month in GridCellAdapter: "
						 * + "Month: " + month + " Year: " + year);
						 */
						setGridCellAdapterToDate(month, year);
					}

					private void setGridCellAdapterToDate(int month, int year) {
						adapter = new GridCellAdapter(getApplicationContext(),
								R.id.calendar_day_gridcell, month, year);
						_calendar.set(year, month - 1,
								_calendar.get(Calendar.DAY_OF_MONTH));
						currentMonth.setText(DateFormat.format(dateTemplate,
								_calendar.getTime()));
						adapter.notifyDataSetChanged();
						calendarView.setAdapter(adapter);
					}
				});

				calendarView = (GridView) calendarDialog
						.findViewById(R.id.calendar);
				adapter = new GridCellAdapter(ActivitiesListActivity.this,
						R.id.calendar_day_gridcell, v, month, year);
				adapter.notifyDataSetChanged();
				calendarView.setAdapter(adapter);

				calendarDialog.show();
			}
		});

		LinearLayout addImageLayout = (LinearLayout) findViewById(R.id.image_layout);
		addImageLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (singleton.isOnline()) {
					Intent intent = new Intent(ActivitiesListActivity.this,
							AddActivity.class);
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(), "Ur in  offline!",
							Toast.LENGTH_LONG).show();
					Intent intent = new Intent(ActivitiesListActivity.this,
							AddActivity.class);
					startActivity(intent);
				}
			}
		});

		LinearLayout backLayout = (LinearLayout) findViewById(R.id.back_layout);
		backLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				singleton.setCopyEntryFlag(false);
				onBackPressed();
			}
		});

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		singleton.setReloadPage(true);
	}

	ListViewSwipeGesture.TouchCallbacks swipeListener = new ListViewSwipeGesture.TouchCallbacks() {

		@Override
		public void FullSwipeListView(int position) {

			System.out.println("Current selected Activity ID: "
					+ dateFilteredKeys[position]);
			System.out.println("Current selected Activity Name: "
					+ dateFilteredValues[position]);
			Date curDate = new Date();
			singleton.setToDate(new SimpleDateFormat("yyyyMMdd")
					.format(curDate));

			if (singleton.getCurrentSelectedDate()
					.equals(singleton.getToDate())) {

				Toast.makeText(
						getApplicationContext(),
						"Cannot copy from today to today! Please select another date.",
						Toast.LENGTH_SHORT).show();

			} else {
//				Toast.makeText(getApplicationContext(), "copy to today",
//						Toast.LENGTH_SHORT).show();
				singleton.setSelectedActivityName(dateFilteredValues[position]);
				singleton.setSelectedActivityID(Integer
						.parseInt(dateFilteredKeys[position]));
				System.out.println("copy to today");
				if(singleton.isOnline())
				{
					namePopupWindow();
				}
				else{
					Toast.makeText(getApplicationContext(), "You are in Offline.",
							Toast.LENGTH_SHORT).show();
				}
			}
		}

		private void namePopupWindow() {

			final Dialog dialog = new Dialog(ActivitiesListActivity.this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.popup);
			LinearLayout shift= (LinearLayout)dialog.findViewById(R.id.shift);
			Button close=(Button)dialog.findViewById(R.id.cancel);
			
			if(singleton.isEnableShiftTracking()){
				
				shift.setVisibility(View.VISIBLE);
				spinner = (Spinner)dialog.findViewById(R.id.shiftspinner);
				spinnerAdapter = new ArrayAdapter<String>(ActivitiesListActivity.this,
						android.R.layout.simple_spinner_item, shiftdata);
				spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner.setAdapter(spinnerAdapter);
				

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
//						Log.d("shif selected item", "-->"
//								+ parent.getItemAtPosition(pos).toString());
//						singleton.setResentShiftItem(parent.getItemAtPosition(pos)
//								.toString());

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}

				});
			}
			else{
				shift.setVisibility(View.GONE);
			}
			
			activityNameToCopy = (EditText) dialog
					.findViewById(R.id.editText1);
			close.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					activityNameToCopy.setText("");
					
				}
			});
			activityNameToCopy.setText(singleton.getSelectedActivityName());
			Button dialogButton = (Button) dialog
					.findViewById(R.id.btn_close_popup);

			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					
				
					if (!activityNameToCopy.getText().toString()
							.equals(singleton.getSelectedActivityName()))
						singleton.setSelectedActivityName(activityNameToCopy
								.getText().toString());
					newName = true;
					CopyEntriesTask copyTask = new CopyEntriesTask();
					copyTask.execute();
					dialog.dismiss();
					Log.d("checking","--->"+singleton.isCopyEntryFlag());
					singleton.setCopyEntryFlag(true);
					Intent intent = new Intent(
							ActivitiesListActivity.this,
							TasksListActivity.class);
					startActivity(intent);
				}
			});

			dialog.show();
		}

		@Override
		public void HalfSwipeListView(int position) {
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			singleton.setToDate(new SimpleDateFormat("yyyyMMdd").format(cal
					.getTime()));
			if (singleton.getCurrentSelectedDate()
					.equals(singleton.getToDate())) {
				Toast.makeText(
						getApplicationContext(),
						"Cannot copy from yesterday to yesterday! Please select another date.",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "copy to yesterday",
						Toast.LENGTH_SHORT).show();
				singleton.setSelectedActivityName(dateFilteredValues[position]);
				singleton.setSelectedActivityID(Integer
						.parseInt(dateFilteredKeys[position]));

				System.err.println("toDate : " + singleton.getToDate());
				System.out.println("copy to yesterday");
				namePopupWindow();
			}
		}

		@Override
		public void LoadDataForScroll(int count) {
		}

		@Override
		public void onDismiss(ListView listView, int[] reverseSortedPositions) {
			Toast.makeText(getApplicationContext(), "Delete",
					Toast.LENGTH_SHORT).show();
			for (int i : reverseSortedPositions) {
				if (singleton.isOnline()) {
					listAdapter.notifyDataSetChanged();
				}
			}
		}

		@Override
		public void OnClickListView(int position) {

			singleton.setSelectedActivityName(dateFilteredValues[position]);
			singleton.setSelectedActivityID(Integer
					.parseInt(dateFilteredKeys[position]));
			if (Utilities.adata.size() != 0) {
				singleton.setselectedActivityIdentityoffline(Utilities.adata
						.get(position).getAIdentity());
			}

			Log.d("Aidentity",
					"-->" + singleton.getselectedActivityIdentityoffline());
			System.err.println("activity name :"
					+ singleton.getSelectedActivityName());
			System.err.println("activity id :"
					+ singleton.getSelectedActivityID());
			Intent intent = new Intent(getApplicationContext(),
					EntriesListActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("index", 2);

			intent.putExtras(bundle);
			startActivity(intent);
		}
	};

	// get activity list for selected task.
	private class GetTaskActivities extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mhandler.sendEmptyMessage(0);
		}
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
					JSONObject jsonActivitiesRequest = new JSONObject();
					jsonActivitiesRequest.put("TaskId",
							singleton.getSelectedTaskID());
					jsonActivitiesRequest.put("ProjectDay",
							singleton.getCurrentSelectedDate());
					jsonActivitiesRequest.put("UserId", singleton.getUserId());

					System.out.println("TaskId: "
							+ singleton.getSelectedTaskID());

					System.out.println("ProjectDay: "
							+ singleton.getCurrentSelectedDate());
					Projectday = singleton.getCurrentSelectedDate();
					System.out.println("get activities for taskid Request: "
							+ jsonActivitiesRequest);
					return jsonDataPost
							.getActivitiesByTask(jsonActivitiesRequest);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String response) {
			// System.out.println("");
			// String activitiesResponse = response;
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;
				Toast.makeText(getApplicationContext(),
						"Sorry! Server could not be reached.",
						Toast.LENGTH_LONG).show();
			} else {
				if (response != null) {
					try {
						
						/*
						 * Object json = new
						 * JSONTokener(activitiesResponse).nextValue(); if (json
						 * instanceof JSONObject) { } else if (json instanceof
						 * JSONArray) { }
						 */

						JSONObject activitiesJSONResponse = new JSONObject(
								response);
						if (activitiesJSONResponse.getInt("Status") == 0
								|| activitiesJSONResponse.getInt("Status") == 200) {
							llayout.setVisibility(View.VISIBLE);
							emptyText.setVisibility(View.GONE);
							JSONArray jsonResponseActivitiesArray = activitiesJSONResponse
									.getJSONArray("Activities");
							System.out.println("Activities :"
									+ jsonResponseActivitiesArray.toString());
							singleton.getActivitiesList().clear();
							activityListStatus.clear();
							int activitiesArrayLength = jsonResponseActivitiesArray
									.length();
							keys = new String[activitiesArrayLength];
							values = new String[activitiesArrayLength];
							dates = new String[activitiesArrayLength];
							
							
							for (int i = 0; i < activitiesArrayLength; i++) {
								JSONObject activity = jsonResponseActivitiesArray
										.getJSONObject(i);
								keys[i] = activity.getString("AI");// Integer.valueOf();
								String str=activity.getString("AN");
								Log.d("data","--->"+str);
								
									
								
								values[i] = str.replace("\\", "");
							Log.d("data","--->"+values[i]+" "+str);
								dates[i] = singleton.getCurrentSelectedDate();// activity.getString("D").split(" ")[0].replace("-","");
								// System.out.println(dates[i]);
								activityListStatus.put(keys[i],
										activity.getString("F"));
								// activityIDs.put(keys[i],
								// activity.getInt("Id"));
								singleton.getActivitiesList().put(keys[i],
										values[i]);

							}

							int delResponse = dbAdapter.deleteActivities(
									singleton.getSelectedTaskID(),
									singleton.getCurrentSelectedDate());
							System.out.println("Activities deletion response: "
									+ delResponse);
							Log.d("activity delected", "--->" + delResponse
									+ "  "
									+ singleton.getActivitiesList().size());
							if (singleton.getActivitiesList().size() > 0) {

								writeActivitiesToDB(Projectday);
							}
							processListsAndSetAdapter();

						} else if (activitiesJSONResponse.getInt("Status") == 230) {
							singleton.getActivitiesList().clear();
							llayout.setVisibility(View.GONE);
							emptyText.setVisibility(View.VISIBLE);
							activityListStatus.clear();
							keys = new String[0];
							values = new String[0];
							dates = new String[0];
							processListsAndSetAdapter();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					System.out
							.println("An error occurred! Could not fetch tasks");

				}
			}
			mhandler.sendEmptyMessage(1);
		}
	}

	Handler mhandler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				mdialog = ProgressDialog
						.show(ActivitiesListActivity.this, "", "Loading...!");
				removeMessages(0);
				break;
			
			case 1:
				if (mdialog.isShowing()) 
				{
					mdialog.cancel();
				}
				removeMessages(2);
				break;

			}
		}

	};

	private class GetMyTaskActivities extends AsyncTask<Void, Void, String> {

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
					
					JSONObject jsonMyTaskActivitiesRequest = new JSONObject();

					jsonMyTaskActivitiesRequest.put("TaskId",
							singleton.getSelectedTaskID());
					jsonMyTaskActivitiesRequest.put("ProjectDay",
							singleton.getCurrentSelectedDate());
					jsonMyTaskActivitiesRequest.put("UserId",
							singleton.getUserId());
					// jsonMyTaskActivitiesRequest.put("ProjectId",
					// singleton.getSelectedProjectID());
					// jsonMyTaskActivitiesRequest.put("UserId",
					// singleton.getUserId());
					// jsonMyTaskActivitiesRequest.put("ProjectDay",
					// singleton.getCurrentSelectedDate());
					// System.out.println("My Task Activities Request: "
					// + jsonMyTaskActivitiesRequest);
					Log.d("get activities for taskid Request: ","--->"
							+ jsonMyTaskActivitiesRequest);
					System.out.println("get activities for taskid Request: "
							+ jsonMyTaskActivitiesRequest);
					return jsonDataPost
							.getActivitiesByTask(jsonMyTaskActivitiesRequest);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String response) {
			Log.d("MyTask Activities Response: ","--->" + response);
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;
				Toast.makeText(getApplicationContext(),
						"Sorry! Server could not be reached.",
						Toast.LENGTH_LONG).show();
			} else {
				if (response != null) {
					try {
						
						JSONObject myTaskActivitiesJSONResponse = new JSONObject(
								response);
						singleton.getTaskList().clear();
						TasksListActivity.taskListStatus.clear();
						Log.d("MyTask Activities Response: ","--->" + myTaskActivitiesJSONResponse
								.getInt("Status"));
						if ((myTaskActivitiesJSONResponse.getInt("Status") == 0)
								|| (myTaskActivitiesJSONResponse
										.getInt("Status") == 200)
								|| (myTaskActivitiesJSONResponse
										.getInt("Status") == 230)) {

							if(myTaskActivitiesJSONResponse
									.getInt("Status") == 230)
							{
								llayout.setVisibility(View.GONE);
								emptyText.setVisibility(View.VISIBLE);
							}
							else{
							llayout.setVisibility(View.VISIBLE);
							emptyText.setVisibility(View.GONE);
							}
							LoginActivity.projectsListActivityStatus.put(
									singleton.getSelectedProjectID(), "T");

							// singleton
							// .setSelectedTaskID(myTaskActivitiesJSONResponse
							// .getInt("TI"));
							// singleton
							// .setSelectedTaskName(myTaskActivitiesJSONResponse
							// .getString("TN"));

							JSONArray myTaskActivitiesArray = new JSONArray(
									myTaskActivitiesJSONResponse
											.getString("Activities"));// myTaskActivitiesJSONResponse.getJSONArray("tasks");

							int myTaskActivitiesArrayLength = myTaskActivitiesArray
									.length();

							Log.d("arraylength", "--->"
									+ myTaskActivitiesArrayLength);
							
							keys = new String[myTaskActivitiesArrayLength];
							values = new String[myTaskActivitiesArrayLength];
							dates = new String[myTaskActivitiesArrayLength];
							for (int i = 0; i < myTaskActivitiesArrayLength; i++) {
								JSONObject activity = myTaskActivitiesArray
										.getJSONObject(i);// JSONObject
								keys[i] = activity.getString("AI");
							String str=activity.getString("AN").replace("\\", "");
							
								values[i] = str;
								Log.d("values","--->"+values[i]);
								dates[i] = singleton.getCurrentSelectedDate();// activity.getString("D").split(" ")[0].replace("-","");
								singleton.getActivitiesList().put(keys[i],
										values[i]);
								activityListStatus.put(keys[i],
										activity.getString("F"));

								Log.d("datajson", "--->" + keys[i] + " "
										+ values[i] + dates[i]);
							}
							int delResponse = dbAdapter.deleteMyTaskActivities(
									singleton.getSelectedTaskID(),singleton.getCurrentSelectedDate()
									);
							Log.d("delete","--->"+delResponse);
							System.out
									.println("MyTaskActivities deletion response: "
											+ delResponse);
							if (singleton.getActivitiesList().size() > 0) {
								
									writeActivitiesToDB(singleton.getCurrentSelectedDate());
							}
							processListsAndSetAdapter();
						} else {
							Toast.makeText(getApplicationContext(),
									"Activities not found!", Toast.LENGTH_LONG)
									.show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					System.out
							.println("An error occurred! Could not fetch activities.");
				}
			}
			
		}
	}

	// calendar class in activity list activity page.
	public class GridCellAdapter extends BaseAdapter implements OnClickListener {
		private static final String tag = "GridCellAdapter";
		private final Context _context;
		private final List<String> list;
		private static final int DAY_OFFSET = 1;
		private final String[] weekdays = new String[] { "Sun", "Mon", "Tue",
				"Wed", "Thu", "Fri", "Sat" };
		private final String[] months = { "01", "02", "03", "04", "05", "06",
				"07", "08", "09", "10", "11", "12" };
		private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
				31, 30, 31 };
		private int daysInMonth;
		private int currentDayOfMonth;
		private int currentWeekDay;
		private Button gridcell;
		private TextView num_events_per_day;
		// private final HashMap<String, Integer> eventsPerMonthMap;
		private String[] activityDates;

		// Days in Current Month
		public GridCellAdapter(Context context, int textViewResourceId, View v,
				int month, int year) {
			this._context = context;
			this.list = new ArrayList<String>();

			Calendar calendar = Calendar.getInstance();
			setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

			calendarClick = (RelativeLayout) v;
			// Print Month
			printMonth(month, year);
		}

		public GridCellAdapter(Context context, int textViewResourceId,
				int month, int year) {

			this._context = context;
			this.list = new ArrayList<String>();
			Calendar calendar = Calendar.getInstance();
			setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

			// Print Month
			printMonth(month, year);
		}

		private String getMonthAsString(int i) {
			return months[i];
		}

		private String getWeekDayAsString(int i) {
			return weekdays[i];
		}

		private int getNumberOfDaysOfMonth(int i) {
			return daysOfMonth[i];
		}

		public String getItem(int position) {
			return list.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		private void printMonth(int mm, int yy) {

			int trailingSpaces = 0;
			int daysInPrevMonth = 0;
			int prevMonth = 0;
			int prevYear = 0;
			int nextMonth = 0;
			int nextYear = 0;

			int currentMonth = mm - 1;
			String currentMonthName = getMonthAsString(currentMonth);
			daysInMonth = getNumberOfDaysOfMonth(currentMonth);

			// Log.d(tag, "Current Month: " + " " + currentMonthName +
			// " having "
			// + daysInMonth + " days.");

			GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
			// Log.d(tag, "Gregorian Calendar:= " + cal.getTime().toString());

			if (currentMonth == 11) {
				prevMonth = currentMonth - 1;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 0;
				prevYear = yy;
				nextYear = yy + 1;
				// Log.d(tag, "*->PrevYear: " + prevYear + " PrevMonth:"
				// + prevMonth + " NextMonth: " + nextMonth
				// + " NextYear: " + nextYear);
			} else if (currentMonth == 0) {
				prevMonth = 11;
				prevYear = yy - 1;
				nextYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 1;
				// Log.d(tag, "**--> PrevYear: " + prevYear + " PrevMonth:"
				// + prevMonth + " NextMonth: " + nextMonth
				// + " NextYear: " + nextYear);
			} else {
				prevMonth = currentMonth - 1;
				nextMonth = currentMonth + 1;
				nextYear = yy;
				prevYear = yy;
				daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
				// Log.d(tag, "***---> PrevYear: " + prevYear + " PrevMonth:"
				// + prevMonth + " NextMonth: " + nextMonth
				// + " NextYear: " + nextYear);
			}

			int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
			trailingSpaces = currentWeekDay;

			// Log.d(tag, "Week Day:" + currentWeekDay + " is "
			// + getWeekDayAsString(currentWeekDay));
			// Log.d(tag, "No. Trailing space to Add: " + trailingSpaces);
			// Log.d(tag, "No. of Days in Previous Month: " + daysInPrevMonth);

			if (cal.isLeapYear(cal.get(Calendar.YEAR)))
				if (mm == 2)
					++daysInMonth;
				else if (mm == 3)
					++daysInPrevMonth;

			// Trailing Month days
			for (int i = 0; i < trailingSpaces; i++) {
				// Log.d(tag,
				// "PREV MONTH:= "
				// + prevMonth
				// + " => "
				// + getMonthAsString(prevMonth)
				// + " "
				// + String.valueOf((daysInPrevMonth
				// - trailingSpaces + DAY_OFFSET)
				// + i));
				list.add(String
						.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
								+ i)
						+ "-GREY"
						+ "-"
						+ getMonthAsString(prevMonth)
						+ "-"
						+ prevYear);
			}

			// Current Month Days
			for (int i = 1; i <= daysInMonth; i++) {
				// Log.d(currentMonthName, String.valueOf(i) + " "
				// + getMonthAsString(currentMonth) + " " + yy);
				if (i == getCurrentDayOfMonth()) {
					list.add(String.valueOf(i) + "-BLUE" + "-"
							+ getMonthAsString(currentMonth) + "-" + yy);
				} else {
					list.add(String.valueOf(i) + "-WHITE" + "-"
							+ getMonthAsString(currentMonth) + "-" + yy);
				}
			}

			for (int i = 0; i < list.size(); i++) {

				// Log.d("listdata", "--->" + list.get(i));

			}
			// Leading Month days
			for (int i = 0; i < list.size() % 7; i++) {
				// Log.d(tag, "NEXT MONTH:= " + getMonthAsString(nextMonth));
				list.add(String.valueOf(i + 1) + "-GREY" + "-"
						+ getMonthAsString(nextMonth) + "-" + nextYear);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) _context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.screen_gridcell, parent, false);
			}

			// Get a reference to the Day gridcell
			gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
			orangeDot = (ImageView) row.findViewById(R.id.orangedot);
			gridcell.setOnClickListener(this);

			// ACCOUNT FOR SPACING

			// Log.d(tag, "Current Day: " + getCurrentDayOfMonth());
			String[] day_color = list.get(position).split("-");
			// System.out.println("***********************"+day_color[1]);
			String theday = day_color[0];
			String themonth = day_color[2];
			String theyear = day_color[3];

			// Set the Day GridCell
			gridcell.setText(theday);
			gridcell.setTag(theyear + "-" + themonth + "-" + theday);
			// Log.d("year month day", "--->" + theyear + "-" + themonth + "-"
			// + theday.length());
			if (theday.length() == 1) {
				runningYMDdata = theyear + "" + themonth + "0" + theday;
			} else {
				runningYMDdata = theyear + "" + themonth + "" + theday;
			}
			if (day_color[1].equals("GREY")) {
				gridcell.setTextColor(getResources()
						.getColor(R.color.lightgray));
			}
			if (day_color[1].equals("WHITE")) {
				
				
				if(singleton.isOnline())
				{
					Log.d("online","--->"+datevalues.length);
				if(datevalues.length==0)
				{
				//Toast.makeText(getApplicationContext(), "Calender Dates not at loading", 100).show();
				//offline dates
				List<ActivityDB> data;
				data1 = dbAdapter.getAllActivityRecordsForCalender();
				datevalues = new String[data1.size()];
				if (data1.size() > 0) {
					Log.d("length", "--->" + data1.size());
					datevalues = new String[data1.size()];
					k = 0;
					for (CalenderActivity val : data1) {

						Log.d("DB data", "--->" + val.getDate() + " " + val.getAId()
								+ " " + val.getAName() + " " + val.getTid());
						datevalues[k] = val.getDate();
						k++;
					}
					for (int i = 0; i < datevalues.length; i++) {
						Log.d("present date", "--->" + datevalues[i] + "  "
								+ runningYMDdata);
						if (datevalues[i].equals(runningYMDdata)) {
							orangeDot.setVisibility(View.VISIBLE);
						}

					}

				}
				}
				else{
					for (int i = 0; i < datevalues.length; i++) {
						Log.d("present date", "--->" + datevalues[i] + "  "
								+ runningYMDdata);
						if (datevalues[i].equals(runningYMDdata)) {
							orangeDot.setVisibility(View.VISIBLE);
						}

					}
				}
				}
				else{
					if(datevalues.length>0)
					{
						Log.d("offline","--->"+datevalues.length);
					for (int i = 0; i < datevalues.length; i++) {
						Log.d("present date", "--->" + datevalues[i] + "  "
								+ runningYMDdata);
						if (datevalues[i].equals(runningYMDdata)) {
							orangeDot.setVisibility(View.VISIBLE);
						}

					}
					}	
					
				}
				gridcell.setTextColor(getResources().getColor(
						R.color.lightgray02));

			}
			if (day_color[1].equals("BLUE")) {

				// Log.d("present date", "--->" + yearMonthdate.substring(4, 6)
				// + " " + yearMonthdate.substring(0, 4));

				for (int i = 0; i < datevalues.length; i++) {
					// Log.d("present date", "--->" + datevalues[i] + "  "
					// + runningYMDdata);
					if (datevalues[i].equals(runningYMDdata)) {
						orangeDot.setVisibility(View.VISIBLE);
					}

				}
				if (themonth.equals(yearMonthdate.substring(4, 6))
						&& theyear.equals(yearMonthdate.substring(0, 4))) {
					gridcell.setTextColor(getResources().getColor(
							R.color.orrange));
				} else {
					gridcell.setTextColor(getResources().getColor(
							R.color.lightgray02));
				}
			}

			if (checkIfDayHasActivity(theyear + themonth + theday)) {
				gridcell.setTextColor(getResources().getColor(R.color.navy));
			}
			return row;
		}

		public boolean checkIfDayHasActivity(String day) {
			// System.out.println("____________________________________________________");
			// System.out.println("Dates Array: "+ Arrays.toString(dates));
			if (dates != null) {
				for (String d : dates) {
					System.out.println("curDate: " + day);
					try {
						d = new SimpleDateFormat("yyyyMMd", Locale.ENGLISH)
								.format(new SimpleDateFormat("yyyyMMdd")
										.parse(d));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					if (d.equals(day))
						return true;
				}
			}
			return false;
		}

		@Override
		public void onClick(View view) {
			
			String date_month_year = (String) view.getTag();
			// Log.d("clickdate", "---->" + date_month_year);
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			try {
				singleton.setCurrentSelectedDate(new SimpleDateFormat(
						"yyyyMMdd").format(new SimpleDateFormat("yyyy-MM-d")
						.parse(date_month_year)));
				
				String[] dateFormattedArray = df2.parse(date_month_year).toString().split(" ");
				String dateFormatted = dateFormattedArray[0] + " " + dateFormattedArray[1] + " " + dateFormattedArray[2] + ", " + dateFormattedArray[5];
				//System.err.println("dateFormatted: "+ dateFormatted);
				singleton.setCurrentSelectedDateFormatted(dateFormatted);
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// date_month_year.replace("-", ""));
			// System.out.println(singleton.getCurrentSelectedDate());
			// filterActivitiesByDates();

			dateTextView.setText(singleton.getCurrentSelectedDateFormatted());
			dateTextView.setTextSize(16);
			// tv.setTextColor(getResources().getColor(R.color.blue));
			calendarDialog.dismiss();
			if (singleton.isOnline()) {

				if (singleton.isEnableTasks()) {
					GetTaskActivities taskActivities = new GetTaskActivities();
					taskActivities.execute();
				} else {
					GetMyTaskActivities getMyTaskActivities = new GetMyTaskActivities();
					getMyTaskActivities.execute();
				}

			} else {
				// sqlite data
				// Log.d("offline", "--->");
				String dateupdate = date_month_year.replace("-", "");
				Log.d("check", "--->" + dateupdate.length());
				if (dateupdate.length() == 8) {
					dateupdate = dateupdate.substring(0, 6) + ""
							+ dateupdate.substring(6, dateupdate.length());
					Log.d("date1", "---->" + dateupdate);
				} else {
					dateupdate = dateupdate.substring(0, 6) + "0"
							+ dateupdate.substring(6, dateupdate.length());
					Log.d("date1", "---->" + dateupdate);

				}
				readDbData(dateupdate);
			}
		}

		public int getCurrentDayOfMonth() {
			return currentDayOfMonth;
		}

		private void setCurrentDayOfMonth(int currentDayOfMonth) {
			this.currentDayOfMonth = currentDayOfMonth;
		}

		public void setCurrentWeekDay(int currentWeekDay) {
			this.currentWeekDay = currentWeekDay;
		}

		public int getCurrentWeekDay() {
			return currentWeekDay;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.d("onresume","--->"+singleton.isReloadPage());
		if (singleton.isOnline()) {
			singleton.setReloadPage(true);
			if (singleton.isReloadPage()) {
				
				

				this.onCreate(null);
				singleton.setReloadPage(false);
			}

		} else {
			String str = singleton.getCurrentSelectedDate();

			Log.d("offine onresume", "--->" + str);

			readDbData(str);
		}
	}

	// copy to today and copy to yesterday api calls.
	private class CopyEntriesTask extends AsyncTask<Void, Void, String> {

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
					String dc = new SimpleDateFormat("yyyy-MM-dd")
							.format(new SimpleDateFormat("yyyyMMdd")
									.parse(singleton.getToDate()))
							+ " "
							+ new SimpleDateFormat("HH:mm:ss")
									.format(new Date());
					System.out.println("DC             :::::::       " + dc);
					JSONObject jsonlabor = new JSONObject();
					jsonlabor.put("Id", singleton.getSelectedActivityID());
					System.out.println("activity id..................."
							+ singleton.getSelectedActivityID());
					jsonlabor.put("UserId", singleton.getUserId());
					jsonlabor.put("FromProjectDay",
							singleton.getCurrentSelectedDate());
					System.out.println("selected date ..................."
							+ singleton.getCurrentSelectedDate());
					jsonlabor.put("ToProjectDay", singleton.getToDate());
					jsonlabor.put("DateCreated", dc);
					if(singleton.isEnableShiftTracking()){
						
						jsonlabor.put("Shift", shiftval);
					}
					
					Log.d("copy checking","--->"+activityNameToCopy.getText().toString());
					if (newName)
						jsonlabor.put("Name",singleton.getSelectedActivityName()
								);
					System.out.println(jsonlabor);
					Log.d("copy","--->"+jsonlabor);
					return jsonDataPost.copyActivityToDay(jsonlabor);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(final String result) {
		//	mProgressDialog.dismiss();
			Log.d("responce","--->"+result);
			if (result != null) {
				try {
					JSONObject jsonObject = new JSONObject(result);
					int Status = jsonObject.getInt("Status");
					if (Status == 0 || Status == 200) {
						// singleton.setSelectedActivityName("");
						if (jsonObject.getString("AI").equals("null")) {
							System.out.println("AI: "
									+ jsonObject.getString("AI"));
							
						} else {
							try {
								singleton
										.setCurrentSelectedDateFormatted(new SimpleDateFormat(
												"E MMM dd, yyyy")
												.format(new SimpleDateFormat(
														"yyyyMMdd")
														.parse(singleton
																.getToDate())));
							} catch (ParseException e) {
								e.printStackTrace();
							}
							singleton.setCurrentSelectedDate(singleton
									.getToDate());
							singleton.setSelectedActivityID(Integer
									.parseInt(jsonObject.getString("AI")));// Integer.parseInt();
							
							Intent intent = new Intent(
									ActivitiesListActivity.this,
									TasksListActivity.class);
							startActivity(intent);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class SwipeListAdapter extends BaseAdapter {
		private Activity activity;

		// public ArrayList<String> activitiesListContent = new
		// ArrayList<String>();
		// private Map <Integer, String> activityDetails = new
		// HashMap<Integer,String>();

		public SwipeListAdapter(Activity a, String[] dateFilteredValues) {// Map
																			// <Integer,
																			// String>
																			// activityDetailsMap,
			activity = a;
			// activityDetails = activityDetailsMap;
		}

		@Override
		public int getCount() {

			if (dateFilteredValues.length > 0) {
				LinearLayout messageHint = (LinearLayout) findViewById(R.id.activity_layout);
				messageHint.setVisibility(View.VISIBLE);
				messageDefaultHint.setVisibility(View.INVISIBLE);

			}
			return dateFilteredValues.length;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv;
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.test_layout, null);
			tv = (TextView) convertView.findViewById(R.id.textView1);
			ImageView orangeArrow = (ImageView) convertView
					.findViewById(R.id.name_imageView2);
			ImageView greyArrow = (ImageView) convertView
					.findViewById(R.id.name_imageView1);

			if (singleton.isOnline()) {
				tv.setText(dateFilteredValues[position]);

				if (ActivitiesListActivity.activityListStatus.get(
						dateFilteredKeys[position]).equals("T")) {
					orangeArrow.setVisibility(View.VISIBLE);
					greyArrow.setVisibility(View.INVISIBLE);
				}
			} else {
				tv.setText(dateFilteredValues[position]);
				if (Utilities.adata.get(position).getHasdata() == 1) {
					orangeArrow.setVisibility(View.VISIBLE);
					greyArrow.setVisibility(View.INVISIBLE);
				} else {
					orangeArrow.setVisibility(View.INVISIBLE);
					greyArrow.setVisibility(View.VISIBLE);
				}
			}

			return convertView;
		}
	}

	public void processListsAndSetAdapter() {
		System.out.println("activitiesMap " + singleton.getActivitiesList());
		// values = singleton.getActivitiesList().keySet().toArray(new
		// String[singleton.getActivitiesList().size()]);
		// Arrays.sort(values);
		// System.out.println("List of Projects: " + Arrays.toString(values));
		/**
		 * Applying bubble sort for sorting tasks.
		 */
		if (values != null) {
			for (int l = 0; l < values.length; l++) {
				for (int j = 0; j < values.length - l - 1; j++) {
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
			filterActivitiesByDates();
		}
	}

	public void writeActivitiesToDB(String Projectday) {
		// String[] values = singleton.getActivitiesList().keySet().toArray(new
		// String[singleton.getActivitiesList().size()]);
		long insertResponse = 0;
		Log.d("@!#$@$#%$^","--->"+values.length);
		for (int i = 0; i < values.length; i++) {

			Log.d("insert val", "--->" + keys[i] + "  " + values[i] + " "
					+ Projectday);
			insertResponse = dbAdapter.insertActivity(keys[i], values[i],
					singleton.getSelectedTaskID(), Projectday,
					activityListStatus.get(keys[i]).equals("T") ? 1 : 0,
					singleton.getUserId());

			Log.d("Activities insertion response: ", "--->" + insertResponse);
		}
		System.out.println("Activities insertion response: " + insertResponse);

	}

	public void filterActivitiesByDates() {
		System.out.println("filterActivitiesByDates() called.");
		System.out.println("keys: " + Arrays.toString(keys));
		System.out.println("values: " + Arrays.toString(values));
		System.out.println(Arrays.toString(dates));
		Log.d("keys", "--->" + Arrays.toString(keys));
		Log.d("values", "--->" + Arrays.toString(values));
		Log.d("dates", "--->" + Arrays.toString(dates));
		int size = 0;

		String date = null;
		try {
			date = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
					.format(new SimpleDateFormat("yyyyMMdd").parse(singleton
							.getCurrentSelectedDate()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println("Current selected date: " + date);
		for (int i = 0; i < dates.length; i++) {
			if (date != null) {
				if (date.equals(dates[i])) {
					size++;
				}
			}
		}
		// System.out.println("Size of dateFileteredArrays: " + size);
		dateFilteredValues = new String[size];

		// Log.d("hashmap size", "--->" + size);
		dateFilteredKeys = new String[size];
		int j = 0;
		for (int i = 0; i < values.length; i++) {
			// System.out.println("dates[i]: "+ dates[i]);
			// System.out.println("date: "+ date);
			if (dates[i].equals(date)) {
				// System.out.println("j: "+ j);
				// System.out.println("keys[i]: "+keys[i]);
				// System.out.println("values[i]: "+values[i]);
				dateFilteredKeys[j] = keys[i];
				dateFilteredValues[j++] = values[i];
			}
		}
		System.out.println("dateFilteredKeys: "
				+ Arrays.toString(dateFilteredKeys));
		System.out.println("dateFilteredValues: "
				+ Arrays.toString(dateFilteredValues));
		// Log.d("Id", "---->" + Arrays.toString(dateFilteredKeys));
		// Log.d("Name", "---->" + Arrays.toString(dateFilteredValues));

		Log.d("swipe values", "--->" + dateFilteredValues);
		listAdapter = new SwipeListAdapter(ActivitiesListActivity.this,
				dateFilteredValues);// values);
		customListView = (ListView) findViewById(R.id.list2);
		final ListViewSwipeGesture touchListener = new ListViewSwipeGesture(
				customListView, swipeListener, this);
		touchListener.SwipeType = ListViewSwipeGesture.Double; // Set two
																// options at
																// background of
																// list item
		customListView.setOnTouchListener(touchListener);

		customListView.setAdapter(listAdapter);

		/*
		 * customListView.invalidate(); listAdapter.notifyDataSetChanged();
		 */
		// ((ListAdapterSwipe)cmn_list_view.getAdapter()).notifyDataSetInvalidated();
	}

	public class ProjectData extends AsyncTask<Void, Void, String> {

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
					System.out.println("request" + jsonTaskRequest);
					return jsonDataPost.getAllProjectTasks(jsonTaskRequest);
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
			Log.d("result", "-->" + response);
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
						if ((taskJSONResponse.getInt("Status") == 0)
								|| (taskJSONResponse.getInt("Status") == 200)) {

							JSONArray responseTasksArray = taskJSONResponse
									.getJSONArray("tasks");
							int responseTasksArrayLength = responseTasksArray
									.length();

							for (int i = 0; i < responseTasksArrayLength; i++) {
								JSONObject task = responseTasksArray
										.getJSONObject(i);// JSONObject
								String requiredTaskName = task.getString("TN");
								Log.d("test", "--->" + requiredTaskName);
								if (requiredTaskName.equals("My Task")) {
									Log.d("tasktest",
											"--->" + task.getInt("TI"));
									singleton.setSelectedTaskID(task
											.getInt("TI"));
									Log.d("tasktest",
											"--->"
													+ singleton
															.getSelectedTaskID());
									GetMyTaskActivities getMyTaskActivities = new GetMyTaskActivities();
									getMyTaskActivities.execute();
//									Log.d("mapval",
//											"--->"
//													+ singleton
//															.getSelectedTaskID());
//
//									data1 = dbAdapter
//											.getAllActivityRecordsForCalender(singleton
//													.getSelectedTaskID());
//									Log.d("mapval", "--->" + data1.size());
//									datevalues = new String[data1.size()];
//									if (data1.size() > 0) {
//
//										for (CalenderActivity val : data1) {
//											datevalues[k] = val.getDate();
//											Log.d("mapval",
//													"--->" + val.getDate()
//															+ " "
//															+ val.getAId()
//															+ " "
//															+ val.getAName()
//															+ " "
//															+ val.getTid());
//											k++;
//										}
//										for (int j = 0; j < datevalues.length; j++) {
//											Log.d("dataval", "--->"
//													+ datevalues[j]);
//										}
//
//									}
								}
							}
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					System.out
							.println("An error occurred! Could not fetch tasks");
				}
			}

		}

	}

	private class Datesvalues extends AsyncTask<Void, Void, String> {

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

					JSONObject jsondates = new JSONObject();
					jsondates
							.put("ProjectId", singleton.getSelectedProjectID());
					System.out.println("activity id..................."
							+ singleton.getSelectedActivityID());
					jsondates.put("LimitDays", 60);
					jsondates.put("UserId", singleton.getUserId());
					Log.d("dates", "--->" + jsondates);
					return jsonDataPost.getActivityCount(jsondates);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(final String result) {
			Log.d("result", "--->" + result);
			int length=0;
			int k=0;
			try {
				if (result != null) {
					JSONObject jobj = new JSONObject(result);
					JSONArray json = jobj.getJSONArray("labor");
					length=length+json.length();
					JSONArray json1 = jobj.getJSONArray("equipment");
					length=length+json1.length();
					JSONArray json2 = jobj.getJSONArray("material");
					length=length+json2.length();
				
					datevalues = new String[length];
					Log.d("dataval", "--->" + datevalues.length);
					for (int i = 0; i < json.length(); i++) {
						JSONObject c = json.getJSONObject(i);

						datevalues[k] = c.getString("Pday");

						Log.d("result", "--->" + datevalues[k]);
						k++;

					}
					for (int i = 0; i < json1.length(); i++) {
						JSONObject c = json1.getJSONObject(i);

						datevalues[k] = c.getString("Pday");

						Log.d("result", "--->" + datevalues[k]);
						k++;
					}
					for (int i = 0; i < json2.length(); i++) {
						JSONObject c = json2.getJSONObject(i);

						datevalues[k] = c.getString("Pday");

						Log.d("result", "--->" + datevalues[k]);
						k++;
					}
					
					for (int i = 0; i < datevalues.length; i++) {
						Log.d("dataval", "--->" + datevalues[i]);
					}
					
				} else {

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}

	private void readDbData(String date) {
		
	
		Utilities.adata.clear();
		List<ActivityDB> data;
		data1 = dbAdapter.getAllActivityRecordsForCalender();
		datevalues = new String[data1.size()];
		if (data1.size() > 0) {
			Log.d("length", "--->" + data1.size());
			datevalues = new String[data1.size()];
			k = 0;
			for (CalenderActivity val : data1) {

				Log.d("DB data", "--->" + val.getDate() + " " + val.getAId()
						+ " " + val.getAName() + " " + val.getTid());
				datevalues[k] = val.getDate();
				k++;
			}
			for (int i = 0; i < datevalues.length; i++) {
				Log.d("dataval", "--->" + datevalues[i]);
			}

		}
		
		Log.d("task", "--->" +singleton.isEnableTasks());
		
		// Tid table identity value stored in Tid in Activity table
		if(singleton.isEnableTasks())
		{
			int Tid = singleton.getSelectedTaskID();
			
			
		}
		else{
			
			List<TasksDB> taskdata= dbAdapter.getAllTaskRecords(singleton.getSelectedProjectID());
			Log.d("project id","--->"+singleton.getSelectedProjectID()+"..."+taskdata.size());
			Log.d("task len", "--->" +taskdata.size());
			singleton.setSelectedTaskID(0);
			Log.d("tid", "--->" + singleton.getSelectedTaskID());
			if(taskdata.size()>0){
				
			for (TasksDB val : taskdata) {
			
				if(val.getTName().equals("My Task"))
				{	
					singleton.setSelectedTaskID(val.getTID());
					Log.d("tid", "--->" + singleton.getSelectedTaskID());
					break;
				}
			
			}
			}
			else{

			}
		}
			
		if(!(singleton.getSelectedTaskID()==0))
		{
		data = dbAdapter.getAllActivityRecords(singleton.getSelectedTaskID(), date);
		Log.d("tid", "--->" + singleton.getSelectedTaskID() + "   " + date + " " + data.size());
		if(data.size()>0)
		{
			llayout.setVisibility(View.VISIBLE);
			emptyText.setVisibility(View.GONE);
		for (ActivityDB val : data) {
			ActivityData details = new ActivityData();
			details.setAIdentity(val.getAIdentity());
			details.setAId(val.getAId());
			details.setAName(val.getAName());
			details.setHasdata(val.getHasdata());
			details.setTid(val.getTid());
			Utilities.adata.add(details);

		}}
		else{
			llayout.setVisibility(View.GONE);
			emptyText.setVisibility(View.VISIBLE);
		}
		// Log.d("arraylength", "---->" + Utilities.tdata.size());
		dateFilteredValues = new String[Utilities.adata.size()];
		dateFilteredKeys = new String[Utilities.adata.size()];
		for (int i = 0; i < Utilities.adata.size(); i++) {
			dateFilteredValues[i] = Utilities.adata.get(i).getAName();
			dateFilteredKeys[i] = Utilities.adata.get(i).getAId() + "";
			// Log.d("taskdata", "---->" + Utilities.adata.get(i).getAId());
			// Log.d("taskdata", "---->" +
			// Utilities.adata.get(i).getAIdentity());
			Log.d("taskdata", "---->" + Utilities.adata.get(i).getTDate());
		}
		// Log.d("oflineId", "---->" + Arrays.toString(dateFilteredKeys));
		// Log.d("oflineName", "---->" + Arrays.toString(dateFilteredValues));
		dbAdapter.Close();
		listAdapter = new SwipeListAdapter(ActivitiesListActivity.this,
				dateFilteredValues);// values);
		customListView = (ListView) findViewById(R.id.list2);
		final ListViewSwipeGesture touchListener = new ListViewSwipeGesture(
				customListView, swipeListener, this);
		touchListener.SwipeType = ListViewSwipeGesture.Double; // Set two
																// options at
																// background of
																// list item
		customListView.setOnTouchListener(touchListener);
		customListView.setAdapter(listAdapter);
		}
	}
	
	
}