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

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.notevault.adapter.LabourSummaryAdapter;
import com.notevault.arraylistsupportclasses.EntityDB;
import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;

public class LaborSummary extends Activity {
	private static final boolean EntityDB = false;
	TextView backname, projectname,selectedDate;
	Singleton singleton;
	private LinearLayout backMark;
	private ListView laborDetails;
	LabourSummaryAdapter adapter;
	DBAdapter DbAdapter;
	ServerUtilities jsonDataPost = new ServerUtilities();
	public static ArrayList<String> labourname= new ArrayList<String>();
	public static ArrayList<Integer> hours= new ArrayList<Integer>();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.laborsummary_layout);
		singleton = singleton.getInstance();
		DbAdapter=DBAdapter.get_dbAdapter(this);
		laborDetails=(ListView)findViewById(R.id.list2);
		
		selectedDate=(TextView)findViewById(R.id.Calendar_text);
		selectedDate.setText(singleton.getCurrentSelectedDateFormatted());
		
		projectname = (TextView) findViewById(R.id.projectname_text);
		projectname.setText(singleton.getSelectedProjectName());
		backMark=(LinearLayout)findViewById(R.id.back_layout);
		
		if(singleton.isOnline())
		{
			new GetLaborSummaryData().execute();
		}
		else{
			readFromDb();
		}
		backMark.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		singleton.setReloadPage(true);
	}
	public class GetLaborSummaryData extends AsyncTask<Void,Void,String>{

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

					Log.d("copy","--->"+singleton.getCurrentSelectedDate());
					
					JSONObject jsonlabor = new JSONObject();
					
					jsonlabor.put("UserId", singleton.getUserId());
					jsonlabor.put("ProjectDay",
							singleton.getCurrentSelectedDate());
					
					Log.d("copy","--->"+jsonlabor);
					return jsonDataPost.getLaborSummary(jsonlabor);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		
		}

		@Override
		protected void onPostExecute(String result) {
			
			Log.d("result","--->"+result);
			try {
				if (result != null) {
					JSONObject jobj = new JSONObject(result);
					JSONArray json = jobj.getJSONArray("response");
					labourname.clear();
					hours.clear();
					if(json.length()>0)
					{
						labourname.add("LABOR");
						hours.add(0);
					}
					for (int i = 0; i < json.length(); i++) {
						JSONObject c = json.getJSONObject(i);
						labourname.add(c.getString("name").replace("\\", ""));
						hours.add(c.getInt("Quantity"));

					}
					for (int i = 0; i < labourname.size(); i++) {
						Log.d("dataval", "--->" + labourname.get(i));
					}
					adapter=new LabourSummaryAdapter(LaborSummary.this);
					laborDetails.setAdapter(adapter);
				} else {
					System.out
					.println("An error occurred! Could not fetch tasks");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
	}
	private void readFromDb() {
		labourname.clear();
		hours.clear();
	List<EntityDB> laboursummary= DbAdapter.getAllEntityRecordsByLNameAndHrs(singleton.getCurrentSelectedDate());
	if(laboursummary.size()>0)
	{
	labourname.add("LABOR");
	hours.add(0);
	}
		for(EntityDB val:laboursummary)
		{
			labourname.add(val.getNAME());
			hours.add(Integer.parseInt(val.getHR_QTY()));
			
		}
		adapter=new LabourSummaryAdapter(this);
		laborDetails.setAdapter(adapter);
	}
}
