package com.notevault.activities;

import java.security.SecureRandom;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class EquipmentCompanyList extends Activity{
	
	Singleton singleton;
	ListView nameListView;
	ImageView addImageView, backImageView;
	ServerUtilities jsonDataPost= new ServerUtilities();
    DBAdapter dbAdapter;
    ArrayList<String> company = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.glossorylist_activity);
		
		singleton = Singleton.getInstance();
        dbAdapter = DBAdapter.get_dbAdapter(this);
		EqupimentCompanyTask eqName = new EqupimentCompanyTask();
        if(singleton.isOnline())
		    eqName.execute();
        else
            readGlossaryFromDB();
		
		TextView textname = (TextView)findViewById(R.id.textView12);
		textname.setText("ADD EQUIPMENT");
		
		TextView textView = (TextView)findViewById(R.id.textname);
		textView.setText("Company");
		LinearLayout addImageLayout = (LinearLayout)findViewById(R.id.image_layout);
		addImageView = (ImageView)findViewById(R.id.addimage);
		addImageLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(singleton.isOnline()) {
				Intent intent = new Intent(EquipmentCompanyList.this,AddEquipmentCompany.class);
				startActivity(intent);
				}else{
					Toast.makeText(getApplicationContext(), "Cannot add company while offline!", Toast.LENGTH_LONG).show();
				}
			}
		});
		backImageView = (ImageView)findViewById(R.id.imageView1);
		LinearLayout backLayout = (LinearLayout)findViewById(R.id.back_layout);
		backLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	
	}
	@Override
	protected void onResume() {
		super.onResume();
		this.onCreate(null);
	}
	private class EqupimentCompanyTask extends AsyncTask<Void, Void, String> {

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
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("ProjectID", singleton.getSelectedProjectID());
					jsonObject.put("CompanyID", singleton.getCompanyId());
					jsonObject.put("GlossaryCategoryID", singleton.getCCID());
					return jsonDataPost.getCompanyName(jsonObject);

				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String response) {
            if(response != null){

                try {
                    JSONObject equipmentCompanyObj = new JSONObject(response);
                    JSONArray equipmentCompanyArray = new JSONArray(equipmentCompanyObj.getString("CompanyNames"));
                    dbAdapter.deleteGlossary(singleton.getCCID());
                    company.clear();
                    for(int i=0; i < equipmentCompanyArray.length(); i++) {
                        JSONObject equipmentCompany = equipmentCompanyArray.getJSONObject(i);
                        company.add(equipmentCompany.getString("W").replace("\\", ""));
                        dbAdapter.insertGlossary(singleton.getCCID(), equipmentCompany.getString("W"));
                    }
                    setAdapter();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
		}
	}

    public void setAdapter(){
        nameListView=(ListView)findViewById(R.id.listView1);
        ArrayAdapter<String> ad=new ArrayAdapter<String>(EquipmentCompanyList.this, android.R.layout.simple_list_item_1, company);
        nameListView.setAdapter(ad);

        nameListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                singleton.setSelectedEquipmentCompany((String) (nameListView.getItemAtPosition(arg2)));
                onBackPressed();
            }
        });
    }

    private void readGlossaryFromDB() {
        Cursor c = dbAdapter.queryGlossary(singleton.getCCID());
        if (c != null ) {
            company.clear();
            if  (c.moveToFirst()) {
                do {
                    company.add(c.getString(c.getColumnIndex("GName")).replace("\\", ""));
                }while (c.moveToNext());
            }else{
                //System.out.println("No Glossary items found in DB for this user account.");
            }
        }
        dbAdapter.Close();
        setAdapter();
    }
}