package com.notevault.activities;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
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

import com.notevault.arraylistsupportclasses.ECmpany;
import com.notevault.arraylistsupportclasses.MNameDb;
import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

public class MaterialNameList extends Activity{
	
	Singleton singleton;
	ListView nameListView;
	ImageView addImageView, backImageView;
	ServerUtilities jsonDataPost = new ServerUtilities();
    DBAdapter dbAdapter;
    ArrayList<String> name = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.glossorylist_activity);
		
		singleton = Singleton.getInstance();
        dbAdapter = DBAdapter.get_dbAdapter(this);
		TextView textname=(TextView)findViewById(R.id.textView12);
		textname.setText("ADD MATERIAL");
		
		TextView textView=(TextView)findViewById(R.id.textname);
		textView.setText("Name");
		
		MaterialNameTask mName = new MaterialNameTask();
        if(singleton.isOnline()) {
            mName.execute();
        }else{
            readGlossaryFromDB();
        }
		LinearLayout addImageLayout = (LinearLayout)findViewById(R.id.image_layout);
		addImageView = (ImageView)findViewById(R.id.addimage);
		addImageLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(singleton.isOnline()) {
				Intent intent = new Intent(MaterialNameList.this,AddMaterialName.class);
				startActivity(intent);
				}else{
					Toast.makeText(getApplicationContext(), "Cannot add name while offline!", Toast.LENGTH_LONG).show();
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
	
	private class MaterialNameTask extends AsyncTask<Void, Void, String> {

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
					jsonObject.put("GlossaryCategoryID", singleton.getMNCID());
					return jsonDataPost.getMaterialName(jsonObject);

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
                    JSONObject materialNamesObj = new JSONObject(response);
                    JSONArray materialNamesArray = new JSONArray(materialNamesObj.getString("Materials"));
                    name.clear();
                    dbAdapter.deleteGlossary(singleton.getMNCID());
                    for(int i=0; i < materialNamesArray.length(); i++) {
                        JSONObject materialName = materialNamesArray.getJSONObject(i);
                        name.add(materialName.getString("W").replace("\\", ""));
                        dbAdapter.insertGlossary(singleton.getMNCID(), materialName.getString("W").replace("\\", ""));
                    }
                    setAdapter();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
		}
	}

    public  void setAdapter(){
        nameListView=(ListView)findViewById(R.id.listView1);
        ArrayAdapter<String> ad=new ArrayAdapter<String>(MaterialNameList.this, android.R.layout.simple_list_item_1, name);
        nameListView.setAdapter(ad);
        nameListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                singleton.setSelectedMaterialName((String) (nameListView.getItemAtPosition(arg2)));
                onBackPressed();
            }
        });
    }

    public  void readGlossaryFromDB(){
    	name.clear();
    	Log.d("data", "--->" + singleton.getMNCID());
		List<MNameDb> data = dbAdapter.getAllMNameRecords(singleton.getMNCID());

		for (MNameDb val : data) {

			name.add(val.getMName());

		}
		Collections.sort(name);
		for (int i = 0; i < name.size(); i++) {
			Log.d("data", "---->" + name.get(i));
		}
        dbAdapter.Close();
        setAdapter();
    }
}