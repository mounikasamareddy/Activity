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
import android.util.Log;
import android.view.Gravity;
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

import com.notevault.adapter.ProjectAdapter;
import com.notevault.arraylistsupportclasses.ProjectDB;
import com.notevault.arraylistsupportclasses.ProjectData;

import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;
import com.notevault.support.Utilities;

public class ProjectListActivity extends Activity {

	Singleton singleton;
	SharedPreferences settingPreferences;
	ListView projectListView;
	int keys[];
	public SQLiteDatabase DB;
	String values[];
	ServerUtilities jsonDataPost = new ServerUtilities();
	Toast toast;
	private static long back_pressed;
	DBAdapter DbAdapter;
	ProjectAdapter pAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_projectselection);

		singleton = Singleton.getInstance();
		DbAdapter = DBAdapter.get_dbAdapter(this);
		TextView companyName;

		companyName = (TextView) findViewById(R.id.companyName_text);
		LinearLayout settingLayout = (LinearLayout) findViewById(R.id.image_layout);
		projectListView = (ListView) findViewById(R.id.list);
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
			companyName.setText(singleton.getCompanyName());
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
			MyAdapter myAdapter = new MyAdapter();
			projectListView.setAdapter(myAdapter);
		}
		// offline ls size
		else {
			companyName.setText(Utilities.lData.get(0).getCompany());
			pAdapter = new ProjectAdapter(ProjectListActivity.this);
			Log.d("arraylist", "---->" + Utilities.pdata.size());

			List<ProjectDB> data = DbAdapter.getAllProjectRecords();
			Utilities.pdata.clear();
			for (ProjectDB val : data) {
				ProjectData details = new ProjectData(val.getPID(),val.getPName(),val.getHasData(),val.getHasActivities());
				details.setPID(val.getPID());
				details.setPName(val.getPName());
				details.setHasData(val.getHasData());
				details.setHasActivities(val.getHasActivities());
				 
				Utilities.pdata.add(details);

			}
			
			for (int i = 0; i < Utilities.pdata.size(); i++) {
				//Log.d("arraylist", "---->" + Utilities.pdata.get(i).getHasData());
				//Log.d("arraylist", "---->" + Utilities.pdata.get(i).getHasActivities());
			}
			// Collections.sort(Utilities.pdata, new ProjectData.OrderByPid());
			
			 
			 
		       
			DbAdapter.Close();
			projectListView.setAdapter(pAdapter);
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

		 System.out.println("INSIDE ON-RESUME");
		if(singleton.isOnline()){
			if (singleton.isReloadPage()) {
				// System.out.println("RELOADING VIEW.");
				projectListView = (ListView) findViewById(R.id.list);
				MyAdapter myAdapter = new MyAdapter();
				projectListView.setAdapter(myAdapter);
				projectListView.invalidate();
				myAdapter.notifyDataSetChanged();
				myAdapter.notifyDataSetInvalidated();
				singleton.setReloadPage(false);
			}
			else{
				if (singleton.isReloadPage()) {
				System.out.println("Offline");
				projectListView = (ListView) findViewById(R.id.list);
				List<ProjectDB> data = DbAdapter.getAllProjectRecords();
				Utilities.pdata.clear();
				for (ProjectDB val : data) {
					ProjectData details = new ProjectData(val.getPID(),val.getPName(),val.getHasData(),val.getHasActivities());
					details.setPID(val.getPID());
					details.setPName(val.getPName());
					details.setHasData(val.getHasData());
					details.setHasActivities(val.getHasActivities());
					 
					Utilities.pdata.add(details);

				}
				pAdapter= new ProjectAdapter(this);
				projectListView.setAdapter(pAdapter);
				projectListView.invalidate();
				pAdapter.notifyDataSetChanged();
				pAdapter.notifyDataSetInvalidated();
				singleton.setReloadPage(false);
				}
			}
		}
		
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return values.length;
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

			// System.out.println("GET VIEW CALLED AGAIN!");

			LayoutInflater li = getLayoutInflater();
			convertView = li.inflate(R.layout.customlist, null);
			TextView tv = (TextView) convertView.findViewById(R.id.textView1);
			// System.err.println("value"+" @"+position+" : "+values[position]);

			ImageView orangeArrow = (ImageView) convertView
					.findViewById(R.id.name_imageView2);
			ImageView greyArrow = (ImageView) convertView
					.findViewById(R.id.name_imageView1);

			tv.setText(values[position]);
			if (singleton.isEnableTasks()) {

				if (LoginActivity.projectsListStatus.get(keys[position])
						.equalsIgnoreCase("T")) {

					orangeArrow.setVisibility(View.VISIBLE);
					greyArrow.setVisibility(View.INVISIBLE);
				}
			} else {
				if (LoginActivity.projectsListActivityStatus
						.get(keys[position]).equalsIgnoreCase("T")) {
					orangeArrow.setVisibility(View.VISIBLE);
					greyArrow.setVisibility(View.INVISIBLE);
				}
			}

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					singleton.setSelectedProjectName(values[position]);
					singleton.setSelectedProjectID(keys[position]);
					LoginActivity.projectsListActivityStatus.put(
							keys[position], "T");
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
						intent = new Intent(ProjectListActivity.this,
								TasksListActivity.class);
					} else {
						intent = new Intent(ProjectListActivity.this,
								ActivitiesListActivity.class);

					}
					startActivity(intent);
				}
			});
			return convertView;
		}
	}
}