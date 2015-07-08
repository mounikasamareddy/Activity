package com.notevault.activities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.notevault.arraylistsupportclasses.ProjectDB;
import com.notevault.arraylistsupportclasses.ProjectData;
import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;
import com.notevault.support.Utilities;

public class ProjectListActivity extends Activity {

	Singleton singleton;
	SharedPreferences settingPreferences;
	NumberPicker projects;
	int keys[], selectedpickerindex;
	public SQLiteDatabase DB;
	String values[], projectsValues[];
	ServerUtilities jsonDataPost = new ServerUtilities();
	Toast toast;
	private static long back_pressed;
	DBAdapter DbAdapter;
	Button chooseProject;
	private TextView welcomusername;
	int k = 0;
	String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_projectselection);

		singleton = Singleton.getInstance();
		DbAdapter = DBAdapter.get_dbAdapter(this);

		LinearLayout settingLayout = (LinearLayout) findViewById(R.id.image_layout);
		chooseProject = (Button) findViewById(R.id.chooseproject);
		projects = (NumberPicker) findViewById(R.id.numberpicker);
		welcomusername = (TextView) findViewById(R.id.welcomusername);
		settingLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent settingIntent = new Intent(ProjectListActivity.this,
						SettingActivity.class);
				startActivity(settingIntent);
			}
		});
		
		// online
		if (singleton.isOnline()) {
			
			welcomusername.setText("Welcome "+singleton.getUsername()+",");
			int i = 0;
			keys = new int[singleton.getProjectsList().size()];
			values = new String[singleton.getProjectsList().size()];
			for (int key : singleton.getProjectsList().keySet()) {
				keys[i] = key;
				values[i++] = singleton.getProjectsList().get(key);
			}
			// Arrays.sort(keys);
			int tempKey;
			String tempValue;
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
			System.out.println("List of Projects: " + Arrays.toString(values));
			Log.d("projectdata", "--->" + Arrays.toString(values));
			projectsValues = new String[values.length];
			for (int j = 0; j < values.length; j++) {
				Log.d("data", "--->" + values[j]);
				projectsValues[j] = values[j];
			}

			projects.setMaxValue(4);
			projects.setMinValue(0);
			projects.setDisplayedValues(projectsValues);

			projects.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

			projects.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker picker, int oldVal,
						int newVal) {
					selectedpickerindex = newVal;

					Log.d("newval", "-->" + selectedpickerindex);
				}
			});

			chooseProject.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					singleton
							.setSelectedProjectName(values[selectedpickerindex]);
					singleton.setSelectedProjectID(keys[selectedpickerindex]);
					LoginActivity.projectsListActivityStatus.put(
							keys[selectedpickerindex], "T");
					System.out.println("id : "
							+ singleton.getSelectedProjectID());
					System.out.println(singleton.getSelectedProjectName());

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
					Intent intent;

					if (singleton.isEnableTasks()) {
						if (k == 0) {
							intent = new Intent(ProjectListActivity.this,
									TasksListActivity.class);
							startActivity(intent);
							k++;
						}
					} else {
						if (k == 0) {
							intent = new Intent(ProjectListActivity.this,
									ActivitiesListActivity.class);
							startActivity(intent);
							k++;
						}

					}

				}
			});

		}
		// offline ls size
		else {
			Log.d("grouped details offline","--->"+singleton.getAccountId()+" "+singleton.getSubscriberId());
			welcomusername.setText("Welcome " +singleton.getUsername()+ ",");
			Log.d("arraylist", "---->" + Utilities.pdata.size());

			List<ProjectDB> data = DbAdapter.getAllProjectRecords();
			Utilities.pdata.clear();
			for (ProjectDB val : data) {
				ProjectData details = new ProjectData(val.getPID(),
						val.getPName(), val.getHasData(),
						val.getHasActivities());
				details.setPID(val.getPID());
				details.setPName(val.getPName());
				details.setHasData(val.getHasData());
				details.setHasActivities(val.getHasActivities());

				Utilities.pdata.add(details);

			}

			Collections.sort(Utilities.pdata, new ProjectData.OrderByPName());
			Log.d("offline", "--->" + Utilities.pdata.size());
			projectsValues = new String[Utilities.pdata.size()];
			for (int i = 0; i < Utilities.pdata.size(); i++) {
				Log.d("offline", "--->" + Utilities.pdata.get(i).getPName());
				projectsValues[i] = Utilities.pdata.get(i).getPName();
			}

			DbAdapter.Close();

			projects.setMaxValue(4);
			projects.setMinValue(0);
			projects.setDisplayedValues(projectsValues);
			projects.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

			projects.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker picker, int oldVal,
						int newVal) {

					selectedpickerindex = newVal;

					Log.d("newval", "-->" + selectedpickerindex);

				}
			});
			chooseProject.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					singleton.setSelectedProjectName(Utilities.pdata.get(
							selectedpickerindex).getPName());

					singleton.setSelectedProjectID(Utilities.pdata.get(
							selectedpickerindex).getPID());

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

					Intent intent;

					if (singleton.isEnableTasks()) {
						if (k == 0) {
							intent = new Intent(ProjectListActivity.this,
									TasksListActivity.class);
							startActivity(intent);
							k++;
						}
					} else {
						if (k == 0) {
							intent = new Intent(ProjectListActivity.this,
									ActivitiesListActivity.class);
							startActivity(intent);
							k++;
						}

					}

				}
			});
		}

		settingPreferences = getSharedPreferences(
				SettingActivity.EnableTaskPREFERENCES, Context.MODE_PRIVATE);
		if (settingPreferences.contains(String.valueOf(singleton.getUserId()))) {
			if (settingPreferences.getString(
					String.valueOf(singleton.getUserId()), "")
					.equalsIgnoreCase("true")) {
				singleton.setEnableTasks(true);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (back_pressed + 2000 > System.currentTimeMillis()) {
			// Need to cancel the toast here
			toast.cancel();
			// code for exit
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			// Ask user to press back button one more time to close app.
			toast = Toast.makeText(getBaseContext(),
					"Press once again to exit.", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
		}
		back_pressed = System.currentTimeMillis();
	}

	@Override
	protected void onResume() {
		super.onResume();
		k = 0;
		System.out.println("INSIDE ON-RESUME");

		if (singleton.isReloadPage()) {

			singleton.setReloadPage(false);

		}
	}

}
