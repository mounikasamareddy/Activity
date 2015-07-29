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

import com.notevault.arraylistsupportclasses.NAmeDb;
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

public class TradeListActivity extends Activity{
	
	Singleton singleton;
	ListView tradeListView;
	ImageView addtradeImageView, backImageView;
	ServerUtilities jsonDataPost = new ServerUtilities();
    DBAdapter dbAdapter;
    ArrayList<String> trade = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.glossorylist_activity);
	
		singleton = Singleton.getInstance();
        dbAdapter = DBAdapter.get_dbAdapter(this);
		GetLaborTrades getLT = new GetLaborTrades();
        if(singleton.isOnline()) {
            getLT.execute();
        }else{
            readGlossaryFromDB();
        }

		TextView textView=(TextView)findViewById(R.id.textname);
		textView.setText("Trade");
		LinearLayout addImageLayout = (LinearLayout)findViewById(R.id.image_layout);
		addtradeImageView=(ImageView)findViewById(R.id.addimage);
		addImageLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(singleton.isOnline()) {
				Intent intent = new Intent(TradeListActivity.this, AddTradeActivity.class);
				startActivity(intent);
				}else{
					Toast.makeText(getApplicationContext(), "Cannot add trade while offline!", Toast.LENGTH_LONG).show();
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

	private class GetLaborTrades extends AsyncTask<Void, Void, String> {

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
				JSONObject jsonTrade = new JSONObject();
				jsonTrade.put("ProjectID", singleton.getSelectedProjectID());
				jsonTrade.put("CompanyID", singleton.getCompanyId());
				jsonTrade.put("GlossaryCategoryID", singleton.getLTCID());
				return jsonDataPost.getLaborTrade(jsonTrade);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(final String result) {
            if(result!=null){
                try {
                    JSONObject laborTradesObj = new JSONObject(result);
                    JSONArray laborTradesArray = new JSONArray(laborTradesObj.getString("TradeNames"));
                    trade.clear();
                    dbAdapter.deleteGlossary(singleton.getLTCID());
                    for(int i=0; i < laborTradesArray.length(); i++) {
                        JSONObject tradeName = laborTradesArray.getJSONObject(i);
                        trade.add(tradeName.getString("W").replace("\\", ""));
                        dbAdapter.insertGlossary(singleton.getLTCID(), tradeName.getString("W").replace("\\", ""));
                    }
                    setAdapter();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
		}
	}

    public void setAdapter(){
        tradeListView = (ListView)findViewById(R.id.listView1);
        ArrayAdapter<String> ad = new ArrayAdapter<String>(TradeListActivity.this, android.R.layout.simple_list_item_1, trade);
        tradeListView.setAdapter(ad);

        tradeListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                singleton.setSelectedLaborTrade((String) (tradeListView.getItemAtPosition(arg2)));
                onBackPressed();
            }
        });
    }

    private void readGlossaryFromDB() {
    	trade.clear();
    	 Log.d("data","--->"+singleton.getLTCID());
		List<TradeDb> data = dbAdapter.getAllnTradeRecords(singleton.getLTCID());

		for (TradeDb val : data) {
			
			trade.add(val.getTname());
			

		}
		Collections.sort(trade);
		for(int i=0;i<trade.size();i++)
		{
			Log.d("data","---->"+trade.get(i));
		}
        dbAdapter.Close();
        setAdapter();
    }
}