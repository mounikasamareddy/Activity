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
import android.widget.AdapterView.OnItemClickListener;

public class MaterialStatusList extends Activity{
	Singleton singleton;
	ListView nameListView;
	ImageView addImageView,backImageView;
	ServerUtilities jsonDataPost= new ServerUtilities();
    DBAdapter dbAdapter;
    ArrayList<String> status = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.glossorylist_activity);
		
		singleton = Singleton.getInstance();
        dbAdapter = DBAdapter.get_dbAdapter(this);
		TextView textname = (TextView)findViewById(R.id.textView12);
		textname.setText("ADD MATERIAL");
		
		TextView textView=(TextView)findViewById(R.id.textname);
		textView.setText("Status");
		
		backImageView = (ImageView)findViewById(R.id.imageView1);
		LinearLayout backLayout = (LinearLayout)findViewById(R.id.back_layout);
		backLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			onBackPressed();
			}
		});
		
		MaterialStatusTask mStatus = new MaterialStatusTask();
        if(singleton.isOnline())
		    mStatus.execute();
        else
            readGlossaryFromDB();
		
		addImageView=(ImageView)findViewById(R.id.addimage);
		addImageView.setVisibility(View.INVISIBLE);
	}
	
	private class MaterialStatusTask extends AsyncTask<Void, Void, String> {

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
					jsonObject.put("AccountId", singleton.getAccountId());
					return jsonDataPost.getMaterialStatus(jsonObject);

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
                    JSONObject materialStatus = new JSONObject(response);
                    JSONArray materialStatusArray = new JSONArray(materialStatus.getString("Mstatus"));
                    dbAdapter.deleteGlossary(singleton.getMSCID());
                    for(int i = 0; i < materialStatusArray.length(); i++){
                        status.add(materialStatusArray.getString(i).replace("\\",""));
                        dbAdapter.insertGlossary(singleton.getMSCID(), materialStatusArray.getString(i));
                    }
                    setAdapter();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
		}
	}

    public  void setAdapter(){
        nameListView = (ListView)findViewById(R.id.listView1);
        nameListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                singleton.setSelectedMaterialStatus((String) (nameListView.getItemAtPosition(arg2)));
                onBackPressed();
            }
        });
        ArrayAdapter<String> ad = new ArrayAdapter<String>(MaterialStatusList.this, android.R.layout.simple_list_item_1, status);
        nameListView.setAdapter(ad);
    }

    public  void readGlossaryFromDB(){
        Cursor c = dbAdapter.queryGlossary(singleton.getMSCID());
        if (c != null ) {
            status.clear();
            if  (c.moveToFirst()) {
                do {
                    status.add(c.getString(c.getColumnIndex("GName")));
                }while (c.moveToNext());
            }else{
                //System.out.println("No Glossary items found in DB for this user account.");
            }
        }
        dbAdapter.Close();
        setAdapter();
    }
}