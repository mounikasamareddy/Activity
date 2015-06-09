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

import com.notevault.arraylistsupportclasses.ClassDb;
import com.notevault.arraylistsupportclasses.TradeDb;
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

public class ClassificationList extends Activity{

    Singleton singleton;
    ListView classificListView;
    ImageView addclassficImageView, backImageView;
    ServerUtilities jsonDataPost = new ServerUtilities();
    DBAdapter dbAdapter;
    ArrayList<String> lbclassific = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.glossorylist_activity);

        singleton = Singleton.getInstance();
        dbAdapter = DBAdapter.get_dbAdapter(this);
        TextView textView=(TextView)findViewById(R.id.textname);
        textView.setText("Classification");

        GetLaborClassification getLC = new GetLaborClassification();
        if(singleton.isOnline()) {
            getLC.execute();
        }else{
            readGlossaryFromDB();
        }

        LinearLayout addImageLayout = (LinearLayout)findViewById(R.id.image_layout);
        addclassficImageView = (ImageView)findViewById(R.id.addimage);
        addImageLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(singleton.isOnline()) {
                    Intent intent = new Intent(ClassificationList.this, AddClassification.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(), "Cannot add classification while offline!", Toast.LENGTH_LONG).show();
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

    private class GetLaborClassification extends AsyncTask<Void, Void, String> {

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
                JSONObject jsonClassificationReq = new JSONObject();
                jsonClassificationReq.put("ProjectID", singleton.getSelectedProjectID());
                jsonClassificationReq.put("CompanyID", singleton.getCompanyId());
                jsonClassificationReq.put("GlossaryCategoryID", singleton.getLCCID());
                return jsonDataPost.getLaborClassification(jsonClassificationReq);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(final String result) {
            if(result != null) {
                try {
                    JSONObject laborClassificationObj = new JSONObject(result);
                    JSONArray laborClassificationArray = new JSONArray(laborClassificationObj.getString("classification"));
                    dbAdapter.deleteGlossary(singleton.getLCCID());
                    lbclassific.clear();
                    for (int i = 0; i < laborClassificationArray.length(); i++) {
                        JSONObject laborClassification = laborClassificationArray.getJSONObject(i);
                        lbclassific.add(laborClassification.getString("W").replace("\\", ""));
                        dbAdapter.insertGlossary(singleton.getLCCID(), laborClassification.getString("W"));
                    }
                    setAdapter();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setAdapter() {
        classificListView=(ListView)findViewById(R.id.listView1);
        ArrayAdapter<String> ad=new ArrayAdapter<String>(ClassificationList.this, android.R.layout.simple_list_item_1, lbclassific);
        classificListView.setAdapter(ad);
        classificListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                singleton.setSelectedLaborClassification((String) (classificListView.getItemAtPosition(arg2)));
                onBackPressed();
            }
        });
    }

    private void readGlossaryFromDB() {
    	lbclassific.clear();
    	 Log.d("data","--->"+singleton.getLCCID());
		List<ClassDb> data = dbAdapter.getAllnClassRecords(singleton.getLCCID());

		for (ClassDb val : data) {
			
			lbclassific.add(val.getCName());
			

		}
		Collections.sort(lbclassific);
		for(int i=0;i<lbclassific.size();i++)
		{
			Log.d("data","---->"+lbclassific.get(i));
		}
        dbAdapter.Close();
        setAdapter();
    }
}