package com.notevault.activities;


import java.security.SecureRandom;

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

public class AddEquipmentCompany extends Activity{
	
	Singleton singleton;
	TextView textView;
	ImageView addImageView;
	EditText equipmentcompany_editText;
	private	ProgressDialog mProgressDialog;
	ProjectListActivity proj = new ProjectListActivity();
	ServerUtilities jsonDataPost = new ServerUtilities();
	String newCompanyText;
	TextView cancelTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	setContentView(R.layout.dataedit_dialog);
	
	singleton = Singleton.getInstance();

	textView = (TextView)findViewById(R.id.textdata);
	textView.setText("Add Company");
	equipmentcompany_editText=(EditText)findViewById(R.id.editText1);
	equipmentcompany_editText.setHint("Company");
	
	LinearLayout addImageLayout = (LinearLayout)findViewById(R.id.image_layout);
	addImageView=(ImageView)findViewById(R.id.save);
	addImageLayout.setOnClickListener(new OnClickListener() {
		
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			if(singleton.isOnline()) {
			newCompanyText = equipmentcompany_editText.getText().toString().trim();
			
			if(newCompanyText.equals("")){
				AlertDialog alertDialog = new AlertDialog.Builder(AddEquipmentCompany.this).create();
				alertDialog.setMessage("Please enter company name");
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				alertDialog.show();
			}else{
			mProgressDialog = new ProgressDialog(AddEquipmentCompany.this);
			mProgressDialog = new ProgressDialog(AddEquipmentCompany.this);
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.show();
			AddEqupimentTask persionnelTask=new AddEqupimentTask();
			persionnelTask.execute();
			}
			}else{
				Toast.makeText(getApplicationContext(), "Cannot add company while offline!", Toast.LENGTH_LONG).show();
			}
		}
	});
	cancelTextView = (TextView)findViewById(R.id.cancel);
	cancelTextView.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
		onBackPressed();
			
		}
	});
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	private class AddEqupimentTask extends AsyncTask<Void, Void, String> {

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
						// TODO Auto-generated method stub
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
					jsonaddPersionnel.put("GlossaryWord", newCompanyText);
					jsonaddPersionnel.put("GlossaryCategoryID", singleton.getCCID());
					addPersoinnelString=jsonDataPost.addCompany(jsonaddPersionnel);
				
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
			
			mProgressDialog.dismiss();
			/*Intent gcmIntent = new Intent(AddEquipmentCompany.this,EquipmentCompanyList.class);
			startActivity(gcmIntent);
			finish();*/
			onBackPressed();
		}
	}
}
