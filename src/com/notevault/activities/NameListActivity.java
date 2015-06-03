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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NameListActivity extends Activity{
    Singleton singleton;
    ListView nameListView;
    ImageView addImageView, backImageView;
    ServerUtilities jsonDataPost = new ServerUtilities();
    DBAdapter dbAdapter;
    ArrayList<String> lbName = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.glossorylist_activity);

        /*if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }*/

        singleton = Singleton.getInstance();
        dbAdapter = DBAdapter.get_dbAdapter(this);
        GetLaborNames getLaborNames = new GetLaborNames();
        if(singleton.isOnline()) {
            getLaborNames.execute();
        }else{
            readGlossaryFromDB();
        }

        TextView textView = (TextView)findViewById(R.id.textname);
        textView.setText("Name");
        LinearLayout addImageLayout = (LinearLayout)findViewById(R.id.image_layout);
        addImageView = (ImageView)findViewById(R.id.addimage);
        addImageLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(singleton.isOnline()) {
                    Intent intent = new Intent(NameListActivity.this, AddLaborNameActivity.class);
                    startActivity(intent);
                    finish();
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

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }*/


    private class GetLaborNames extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... arg0) {

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
            }catch (Exception e) {
                e.printStackTrace();
            }

            try {
                JSONObject jsonLaborName = new JSONObject();
                jsonLaborName.put("ProjectID", singleton.getSelectedProjectID());
                jsonLaborName.put("CompanyID", singleton.getCompanyId());
                jsonLaborName.put("GlossaryCategoryID", singleton.getLNCID());
                return jsonDataPost.getLaborPersonnel(jsonLaborName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(final String response) {
            lbName.clear();
            if(response != null){
                try {
                    JSONObject laborNameResponseObj = new JSONObject(response);
                    JSONArray laborNameArray = new JSONArray(laborNameResponseObj.getString("Labors"));
                    lbName.clear();
                    dbAdapter.deleteGlossary(singleton.getLNCID());
                    for(int i=0; i < laborNameArray.length(); i++) {
                        JSONObject laborName = laborNameArray.getJSONObject(i);
                        lbName.add(laborName.getString("W").replace("\\", ""));
                        dbAdapter.insertGlossary(singleton.getLNCID(), laborName.getString("W"));
                    }
                setAdapter();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setAdapter(){
        System.out.println("labor name.........#################.............."+lbName);
        nameListView = (ListView)findViewById(R.id.listView1);
        ArrayAdapter<String> ad = new ArrayAdapter<String>(NameListActivity.this, android.R.layout.simple_list_item_1, lbName);
        nameListView.setAdapter(ad);
        nameListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                singleton.setSelectedLaborName((String) (nameListView.getItemAtPosition(arg2)));
                onBackPressed();
            }
        });
    }

    public  void readGlossaryFromDB(){
        Cursor c = dbAdapter.queryGlossary(singleton.getLNCID());
        if (c != null ) {
            if  (c.moveToFirst()) {
                do {
                    lbName.add(c.getString(c.getColumnIndex("GName")).replace("\\", ""));
                }while (c.moveToNext());
            }else{
                //System.out.println("No Glossary items found in DB for this user account.");
            }
        }
        dbAdapter.Close();
        setAdapter();
    }
}