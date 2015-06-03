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

public class EquipmentStatusList extends Activity{

    Singleton singleton;
    ListView nameListView;
    ImageView addImageView,backImageView;
    ServerUtilities jsonDataPost = new ServerUtilities();
    DBAdapter dbAdapter;
    ArrayList<String> status = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.glossorylist_activity);

        singleton = Singleton.getInstance();
        dbAdapter = DBAdapter.get_dbAdapter(this);
        EqupimentStatusTask eqstatus = new EqupimentStatusTask();
        if(singleton.isOnline())
            eqstatus.execute();
        else
            readGlossaryFromDB();

        TextView textname=(TextView)findViewById(R.id.textView12);
        textname.setText("ADD EQUIPMENT");

        TextView textView=(TextView)findViewById(R.id.textname);
        textView.setText("Status");
        addImageView=(ImageView)findViewById(R.id.addimage);
        addImageView.setVisibility(View.INVISIBLE);
		
		/*
		addImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EquipmentStatusList.this,AddNameActivity.class);
				startActivity(intent);
			}
		});*/

        backImageView = (ImageView)findViewById(R.id.imageView1);
        LinearLayout backLayout = (LinearLayout)findViewById(R.id.back_layout);
        backLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }



    private class EqupimentStatusTask extends AsyncTask<Void, Void, String> {

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
                        // TODO Auto-generated method stub
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
                    return jsonDataPost.getEquipmentStatus(jsonObject);

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
                    JSONObject equipmentStatusObj = new JSONObject(response);
                    JSONArray equipmentStatusArray = new JSONArray(equipmentStatusObj.getString("Estatus"));
                    dbAdapter.deleteGlossary(singleton.getESCID());
                    status.clear();
                    for(int i=0; i < equipmentStatusArray.length(); i++) {
                        status.add(equipmentStatusArray.getString(i).replace("\\", ""));
                        dbAdapter.insertGlossary(singleton.getESCID(), equipmentStatusArray.getString(i));
                    }
                    setAdapter();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setAdapter(){
        nameListView = (ListView)findViewById(R.id.listView1);
        ArrayAdapter<String> ad = new ArrayAdapter<String>(EquipmentStatusList.this, android.R.layout.simple_list_item_1, status);
        nameListView.setAdapter(ad);
        nameListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                singleton.setSelectedEquipmentStatus((String) (nameListView.getItemAtPosition(arg2)));
                onBackPressed();
            }
        });
    }

    private void readGlossaryFromDB() {
        Cursor c = dbAdapter.queryGlossary(singleton.getESCID());
        if (c != null ) {
            status.clear();
            if  (c.moveToFirst()) {
                do {
                    status.add(c.getString(c.getColumnIndex("GName")).replace("\\", ""));
                }while (c.moveToNext());
            }else{
                //System.out.println("No Glossary items found in DB for this user account.");
            }
        }
        dbAdapter.Close();
        setAdapter();
    }
}