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
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class AddCompanyActivity extends Activity{
	
	Singleton singleton;
	private TextView textView,saveButton;
	private EditText addCompanyeditText;
	private	ProgressDialog mProgressDialog;
	ServerUtilities jsonDataPost = new ServerUtilities();
	int projectid, companyid;
	String companyNameText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	setContentView(R.layout.dataedit_dialog);
	
	singleton = Singleton.getInstance();
	projectid = singleton.getSelectedProjectID();
	companyid = singleton.getCompanyId();
	
	textView=(TextView)findViewById(R.id.textdata);
	textView.setText("Add Company");
	addCompanyeditText=(EditText)findViewById(R.id.editText1);
	addCompanyeditText.setHint("Add Company");
	
	saveButton=(TextView)findViewById(R.id.save);
	saveButton.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mProgressDialog = new ProgressDialog(AddCompanyActivity.this);
			mProgressDialog = new ProgressDialog(AddCompanyActivity.this);
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.show();
			companyNameText=addCompanyeditText.getText().toString();
			System.out.println("perssional name....................."+companyNameText);
			addCompanyeditText.setText(null);
			AddLaborCompanyTask persionnelTask=new AddLaborCompanyTask();
			persionnelTask.execute();
			
		}
	});
	
	}
	private class AddLaborCompanyTask extends AsyncTask<Void, Void, String> {

		String addlaborCompanyString;
		String addpersionnelName;
		ArrayList<String> name = new ArrayList<String>();
		
		 @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            mProgressDialog = new ProgressDialog(AddCompanyActivity.this);
				mProgressDialog.setMessage("Loading...");
				mProgressDialog.setIndeterminate(false);
				mProgressDialog.setCancelable(true);
				mProgressDialog.show();
	           
	        }
		
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
					
					JSONObject jsonaddCompany = new JSONObject();
					jsonaddCompany.put("ProjectID",singleton.getSelectedProjectID());
					jsonaddCompany.put("CompanyID",companyid);
					jsonaddCompany.put("GlossaryWord",companyNameText);
					jsonaddCompany.put("GlossaryCategoryID",3);
					addlaborCompanyString=jsonDataPost.addLaborCompany(jsonaddCompany);
				
					System.out.println("Inserted labor company name..........."+addlaborCompanyString);
					
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
			
			Intent gcmIntent = new Intent(AddCompanyActivity.this,AddLabor.class);
			finish();
			startActivity(gcmIntent);
			
		}
	}	
}
