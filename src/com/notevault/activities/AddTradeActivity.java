package com.notevault.activities;

import java.security.SecureRandom;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;

import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AddTradeActivity extends Activity{
	
	Singleton singleton;
	TextView textView, saveButton,cancel;
	EditText trade_editText;
	ProjectListActivity proj = new ProjectListActivity();
	ServerUtilities jsonDataPost = new ServerUtilities();
	String newTradeName;
	ImageView addTradeImageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	setContentView(R.layout.dataedit_dialog);
	
	singleton = Singleton.getInstance();
	textView = (TextView)findViewById(R.id.textdata);
	textView.setText("Add Trade");
	trade_editText = (EditText)findViewById(R.id.editText1);
	trade_editText.setHint("Trade");
	
	LinearLayout addImageLayout = (LinearLayout)findViewById(R.id.image_layout);
	addTradeImageView=(ImageView)findViewById(R.id.save);
	addImageLayout.setOnClickListener(new OnClickListener() {
		
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			if(singleton.isOnline()) {
			newTradeName = trade_editText.getText().toString().trim();
			if(newTradeName.equals("")){
				AlertDialog alertDialog = new AlertDialog.Builder(AddTradeActivity.this).create();
				alertDialog.setMessage("Please enter trade name");
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				alertDialog.show();
			}else{
			AddTradeTask persionnelTask = new AddTradeTask();
			persionnelTask.execute();
			}
			}else{
				Toast.makeText(getApplicationContext(), "Cannot add trade while offline!", Toast.LENGTH_LONG).show();
			}
		}
	});
	cancel = (TextView)findViewById(R.id.cancel);
	cancel.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			onBackPressed();			
		}
	});
	}
	private class AddTradeTask extends AsyncTask<Void, Void, String> {

		String addPersoinnelString;
		
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
					
					JSONObject jsonaddPersionnel = new JSONObject();
					jsonaddPersionnel.put("ProjectID", singleton.getSelectedProjectID());
					jsonaddPersionnel.put("CompanyID", singleton.getCompanyId());
					jsonaddPersionnel.put("GlossaryWord",newTradeName);
					jsonaddPersionnel.put("GlossaryCategoryID", singleton.getLTCID());
					addPersoinnelString=jsonDataPost.addLaborTrade(jsonaddPersionnel);
				
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}catch (Exception e) {
				e.printStackTrace();
			}
	
			return null;
		}

		@Override
		protected void onPostExecute(String param) {
			onBackPressed();
			/*Intent gcmIntent = new Intent(AddTradeActivity.this,TradeListActivity.class);
			startActivity(gcmIntent);
			finish();*/
		}
	}
}
