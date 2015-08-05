package com.notevault.activities;

import java.security.SecureRandom;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.notevault.arraylistsupportclasses.EntityDB;
import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;

public class AddLabor extends Activity {

	Singleton singleton;
	String values[] = { "Name", "Trade", "Classification" };
	TextView deleteTextView;
	EditText hourEditText, descEditText;
	ImageView addLaborEntriesView;
	private ProgressDialog mProgressDialog;

	ServerUtilities jsonDataPost = new ServerUtilities();

	TextView projectText, taskText, dateText, activityText;
	ListView laborView;
	TextView tv2;
	TextView cancelTextView;
	String errorMsg = "";
	DBAdapter dbAdapter;
	String glue = "-~-";
	LinearLayout addImageLayout;
	String trade, classification;
	EditText timeAndHalf, doubleTime;
	List<EntityDB> data;
	SharedPreferences settingPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addlabor);
		singleton = Singleton.getInstance();
		System.err.println("onStart: " + singleton.getSelectedLaborName());
		dbAdapter = DBAdapter.get_dbAdapter(this);

		settingPreferences = getSharedPreferences(
				SettingActivity.EnableOVERTIMETRACKPREFERENCES,
				Context.MODE_PRIVATE);
		if (settingPreferences.contains(String.valueOf(singleton.getUserId()))) {
			if (settingPreferences.getString(
					String.valueOf(singleton.getUserId()), "")
					.equalsIgnoreCase("true")) {
				singleton.setEnableOvertimeTracking(true);
			}
		}

		Log.d("details",
				"--->" + singleton.getAccountId() + " "
						+ singleton.getSubscriberId());

		projectText = (TextView) findViewById(R.id.greeting);
		taskText = (TextView) findViewById(R.id.txt);
		dateText = (TextView) findViewById(R.id.text);
		activityText = (TextView) findViewById(R.id.textv);

		hourEditText = (EditText) findViewById(R.id.editText4);
		if (singleton.isEnableOvertimeTracking()) {

			timeAndHalf = (EditText) findViewById(R.id.timeandhalf);
			doubleTime = (EditText) findViewById(R.id.doubletime);

			timeAndHalf.setVisibility(View.VISIBLE);

			doubleTime.setVisibility(View.VISIBLE);

		}
		// descEditText = (EditText)findViewById(R.id.desc_textView6);
		deleteTextView = (TextView) findViewById(R.id.textView6_delete);

		projectText.setText(singleton.getSelectedProjectName());
		taskText.setText(singleton.getSelectedTaskName());
		activityText.setText(singleton.getSelectedActivityName());
		dateText.setText(singleton.getCurrentSelectedDateFormatted());

		laborView = (ListView) findViewById(R.id.listView1);
		Myadapter myad = new Myadapter();
		laborView.setAdapter(myad);
		Log.d("214739582609764", "--->" + singleton.getSelectedLaborHours());
		hourEditText.setText(singleton.getSelectedLaborHours());
		if (singleton.isEnableOvertimeTracking()) {
			doubleTime.setText(singleton.getSelectedLaborDoubleTime());
			timeAndHalf.setText(singleton.getSelectedLaborTimeAndHalf());
		}

		if (descEditText != null) {
			descEditText.setText(singleton.getSelectedLaborDescription());
		}

		if (singleton.isNewEntryFlag()) {
			System.out.println("Enter New Labor entries.");
		} else {
			TextView laborTextView = (TextView) findViewById(R.id.textdata);
			laborTextView.setText("Labor");
			deleteTextView.setVisibility(View.VISIBLE);
		}

		cancelTextView = (TextView) findViewById(R.id.cancel);
		cancelTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		deleteTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						AddLabor.this);
				alertDialog.setTitle("Delete Labor");
				alertDialog.setMessage("Please confirm to delete.");

				alertDialog.setPositiveButton("Confirm",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								if (singleton.isOnline()) {
									mProgressDialog = new ProgressDialog(
											AddLabor.this);
									mProgressDialog.setMessage("Loading...");
									mProgressDialog.setIndeterminate(false);
									mProgressDialog.show();

									DeleteLaborTask deleteLabor = new DeleteLaborTask();
									deleteLabor.execute();
								} else {
									Log.d("offline",
											"--->"
													+ singleton
															.getCurrentSelectedEntryID()
													+ " "
													+ singleton
															.getSelectedEntityIdentity());

									if (singleton.getCurrentSelectedEntryID() == 0) {

										int deleteEntity = dbAdapter
												.deleteEntryByIDOffline(singleton
														.getSelectedEntityIdentity());
										Log.d("delete Eid=0",
												"--->"
														+ deleteEntity
														+ " "
														+ singleton
																.getSelectedEntityIdentity());

									} else {

										int updateEntry = dbAdapter.updateEntryOffline(
												singleton
														.getSelectedEntityIdentity(),
												"D");
										Log.d("update Eid=0",
												"--->"
														+ updateEntry
														+ " "
														+ singleton
																.getSelectedEntityIdentity());

									}
									singleton.setReloadPage(true);
									onBackPressed();
								}

							}
						});
				// Setting Negative "cancel" Button
				alertDialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
				// Showing Alert Message
				alertDialog.show();
			}
		});

		hourEditText
				.setOnEditorActionListener(new EditText.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_DONE) {
							
								addImageLayout.performClick();
							
							return true;
						}
						return false;
					}

				});
		if (singleton.isEnableOvertimeTracking()) {
			timeAndHalf
			.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
			          
			        	addImageLayout.performClick();
			            return true;
			        }
			        return false;
			    }
			});
			doubleTime
					.setOnEditorActionListener(new EditText.OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView v, int actionId,
								KeyEvent event) {
							if (actionId == EditorInfo.IME_ACTION_DONE) {
								
								addImageLayout.performClick();
								return true;
							}
							return false;
						}

					});
		}

		addImageLayout = (LinearLayout) findViewById(R.id.image_layout);
		addLaborEntriesView = (ImageView) findViewById(R.id.save);
		addImageLayout.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				addImageLayout.setEnabled(false);
				singleton.setSelectedLaborHours(hourEditText.getText()
						.toString().trim());
				if (singleton.isEnableOvertimeTracking()) {
					singleton.setSelectedLaborTimeAndHalf(timeAndHalf.getText()
							.toString().trim());
					singleton.setSelectedLaborDoubleTime(doubleTime.getText()
							.toString().trim());
				}
				// singleton.setSelectedLaborDescription(descEditText.getText().toString().trim());
				String n, t, c, h;
				errorMsg = "";
				n = singleton.getSelectedLaborName();
				t = singleton.getSelectedLaborTrade();
				c = singleton.getSelectedLaborClassification();
				h = singleton.getSelectedLaborHours();

				// Entry fields validation.
				if (n.equalsIgnoreCase(""))
				{
					errorMsg = "Name";
				}
				if (t.equalsIgnoreCase("")) {
					if (!errorMsg.equalsIgnoreCase(""))
						errorMsg += ", ";
					errorMsg += "Trade";
				}
				if (c.equalsIgnoreCase("")) {
					if (!errorMsg.equalsIgnoreCase(""))
						errorMsg += ", ";
					errorMsg += "Classification";
				}
				if (h.equalsIgnoreCase("")) {
					if (!errorMsg.equalsIgnoreCase(""))
						errorMsg += ", ";
					
					errorMsg += "Hour";
				}
				if (singleton.isEnableOvertimeTracking()) {
				
					if (timeAndHalf.getText().toString().equalsIgnoreCase(""))
					{
						if(!errorMsg.equalsIgnoreCase(""))
						{
							errorMsg += ", ";
						}
						
						
						errorMsg += "TimeAndHalf";
					}

					if (doubleTime.getText().toString().equalsIgnoreCase("")) 
					{
						if(!errorMsg.equalsIgnoreCase(""))
						{
							errorMsg += ", ";
						}
						
						
						
						errorMsg += "DoubleTime";
					}
				}
				if (errorMsg.length() > 0) {
					if (errorMsg.contains(",")) {
						errorMsg += " are mandatory.";
					} else {
						errorMsg += " is mandatory.";
					}
				}
				System.err.println("setFlag: " + singleton.isNewEntryFlag());

				AlertDialog alertDialog = new AlertDialog.Builder(AddLabor.this)
						.create();
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								errorMsg = "";
								addImageLayout.setEnabled(true);
							}
						});

				if (errorMsg.equalsIgnoreCase("")) {

					if (singleton.isNewEntryFlag()) {
						if (singleton.isOnline()) {
							mProgressDialog = new ProgressDialog(AddLabor.this);
							mProgressDialog.setMessage("Loading...");
							mProgressDialog.setIndeterminate(false);
							mProgressDialog.show();
							System.out
									.println("Add entries code should execute");
							AddLaborEntries laborEntries = new AddLaborEntries();
							laborEntries.execute();
						} else {

							readDataFromDB();

						}
					} else {

						Log.d("update code should execute", "--->");
						if (singleton.isOnline()) {
							UpdateEntries updateLabor = new UpdateEntries();
							updateLabor.execute();
						} else {
							Log.d("update code should execute", "--->"
									+ singleton.getSelectedLaborName());

							if (singleton.getCurrentSelectedEntryID() == 0) {

								long upentry = dbAdapter.updateEntryOffline1(
										singleton.getSelectedLaborName(),
										singleton.getSelectedLaborTrade(),
										singleton
												.getSelectedLaborClassification(),
										singleton.getSelectedLaborHours(), "L",
										"N");
								Log.d("updateentity eId =0", "-->" + upentry);
							} else {

								long upentry = dbAdapter.updateEntry(singleton
										.getSelectedLaborName(), singleton
										.getSelectedLaborTrade(), singleton
										.getSelectedLaborClassification(),
										singleton.getSelectedLaborHours(), "L",
										"U",
										singleton.getCurrentSelectedEntryID()
												+ "");
								Log.d("updateentity eId =something", "-->"
										+ upentry);
							}
							singleton.setReloadPage(true);
							onBackPressed();
						}
					}
				} else {
					// display alert(errorMsg);
					alertDialog.setMessage(errorMsg);
					alertDialog.show();
					errorMsg = "";
				}

			}

		});
	}

	class Myadapter extends BaseAdapter {

		@Override
		public int getCount() {
			return values.length;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			LayoutInflater li = getLayoutInflater();
			convertView = li.inflate(R.layout.entrieslistview, null);
			TextView tv = (TextView) convertView.findViewById(R.id.textView1);
			tv2 = (TextView) convertView.findViewById(R.id.textView2);

			final String val[] = values[position].split("~");
			tv.setText(val[0]);
			//tv2.setVisibility(View.VISIBLE);
			
			
		
			if (val[0].equals("Name")) {
				Log.d("check","--->"+val[0]);
				if(singleton.getSelectedLaborName().equals(""))
				{
					tv2.setText("None");
				}
				else{
					tv2.setText(singleton.getSelectedLaborName());
				}
				

			} 
			if (val[0].equals("Trade")) {
				
				Log.d("inside trade",
						"--->" + singleton.getSelectedLaborTrade()
								+ "...");
				if (singleton.getSelectedLaborName() != null
						&& !singleton.getSelectedLaborName().isEmpty()) {

					Log.d("name not null",
							"--->" + singleton.getSelectedLaborTrade()
									+ "...");
					
					if (singleton.getSelectedLaborTrade() != null
							&& !singleton.getSelectedLaborTrade().isEmpty()) {
						
						Log.d("trade not null",
								"--->" + singleton.getSelectedLaborTrade()
										+ "...");
						
						tv2.setText(singleton.getSelectedLaborTrade()+"");
						
						
					} else {

						data = dbAdapter.getAllEntityRecordsByLName(singleton
								.getSelectedLaborName());
						Log.d("text", "--->" + data.size());
						if (data.size() > 0) {
							trade = "";
							classification = "";
							for (EntityDB val1 : data) {
								if (val1.getNAME().equals(
										singleton.getSelectedLaborName())) {
									trade = val1.getTRD_COMP();
									classification = val1.getCLASSI_STAT();

								}
								Log.d("dataname", "--->" + val1.getNAME() + " "
										+ trade + " " + classification);
							}
							singleton.setSelectedLaborTrade(trade);
							if(singleton.getSelectedLaborTrade().equals(""))
							{
								tv2.setText("None");
							}
							else{
								tv2.setText(trade);
							}
						} else {
							
								tv2.setText("None");
							
						}
					}
				} else {
					if(singleton.getSelectedLaborTrade().equals(""))
					{
						tv2.setText("None");
					}
					else
					{
					tv2.setText(singleton.getSelectedLaborTrade());
					}
				}

			}
			if (val[0].equals("Classification")) {
				
				
				if (singleton.getSelectedLaborName() != null
						&& !singleton.getSelectedLaborName().isEmpty()) {
					

					if (singleton.getSelectedLaborClassification() != null
							&& !singleton.getSelectedLaborClassification()
									.isEmpty()) {
						
						
						tv2.setText(singleton.getSelectedLaborClassification() + "");
						
						
						
					} else {

						singleton
								.setSelectedLaborClassification(classification);

						tv2.setText("None");

						
					}

				} else {
					if(singleton.getSelectedLaborClassification().equals(""))
					{
						tv2.setText("None");
					}
					else
					{
					tv2.setText(singleton.getSelectedLaborClassification() + "");
					}
				}
			}
			

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					System.err.println("Hours in EditText: "
							+ hourEditText.getText().toString().trim());
					singleton.setSelectedLaborHours(hourEditText.getText()
							.toString().trim());

					// singleton.setSelectedLaborDescription(descEditText.getText().toString().trim());

					if (val[0].equals("Name")) {
						Intent intent = new Intent(AddLabor.this,
								NameListActivity.class);
						overridePendingTransition(0, 0);
						startActivity(intent);
					} else if (val[0].equals("Trade")) {
						Intent intent = new Intent(AddLabor.this,
								TradeListActivity.class);
						overridePendingTransition(0, 0);
						startActivity(intent);
					} else if (val[0].equals("Classification")) {
						Intent intent = new Intent(AddLabor.this,
								ClassificationList.class);
						overridePendingTransition(0, 0);
						startActivity(intent);
					}
				}
			});
			return convertView;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		System.err.println("onResume: " + singleton.getSelectedLaborName());
		Log.d("onresume", "-->");
		System.err.println("Hours in onResume: "
				+ singleton.getSelectedLaborHours());
		laborView = (ListView) findViewById(R.id.listView1);
		laborView.invalidate();
		this.onCreate(null);
		Myadapter myad = new Myadapter();
		myad.notifyDataSetChanged();
	}

	private class AddLaborEntries extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			try {
				TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					@Override
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] arg0,
							String arg1) {
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
				HttpsURLConnection.setDefaultSSLSocketFactory(sc
						.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(hv);

				try {
					JSONObject jsonAddLabor = new JSONObject();
					jsonAddLabor.put("AccountID", singleton.getAccountId());
					jsonAddLabor.put("SubscriberID",
							singleton.getSubscriberId());
					jsonAddLabor.put("ProjectID",
							singleton.getSelectedProjectID());
					jsonAddLabor.put("ActivityId",
							singleton.getSelectedActivityID());
					jsonAddLabor.put("ProjectDay",
							singleton.getCurrentSelectedDate());
					jsonAddLabor.put("Name", singleton.getSelectedLaborName());
					jsonAddLabor
							.put("Trade", singleton.getSelectedLaborTrade());
					jsonAddLabor.put("Classification",
							singleton.getSelectedLaborClassification());
					jsonAddLabor
							.put("Hours", singleton.getSelectedLaborHours());
					if (singleton.isEnableOvertimeTracking()) {
						jsonAddLabor.put("TimeAndHalf", timeAndHalf.getText()
								.toString());
						jsonAddLabor.put("DoubleTime", doubleTime.getText()
								.toString());

					}

					jsonAddLabor.put("TaskId", singleton.getSelectedTaskID());
					jsonAddLabor.put("UserId", singleton.getUserId());
					Log.d("request", "--->" + jsonAddLabor);
					return jsonDataPost.addLaborEntry(jsonAddLabor);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				String ID = dbAdapter.generateOfflineEntryID();
				// long laborInsertResponse = dbAdapter.insertEntry(
				// singleton.getSelectedLaborName(),
				// singleton.getSelectedLaborTrade(),
				// singleton.getSelectedLaborClassification(),
				// singleton.getSelectedLaborHours(), "L", "I", ID);

				// Log.d("Labour data", "--->" + laborInsertResponse);
				EntriesListByDateActivity.collectiveConcatenatedEntryList
						.add("L" + glue + singleton.getSelectedLaborName()
								+ glue + singleton.getSelectedLaborTrade()
								+ glue
								+ singleton.getSelectedLaborClassification()
								+ glue + singleton.getSelectedLaborHours()
								+ glue + ID);

				singleton.setReloadPage(true);
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String result) {
			Log.d("result", "--->" + result);
			mProgressDialog.dismiss();
			long laborInsertResponse = 0;
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;
				Toast.makeText(getApplicationContext(),
						"Sorry! Server could not be reached.",
						Toast.LENGTH_LONG).show();
				// laborInsertResponse = dbAdapter.insertEntry(
				// singleton.getSelectedLaborName(),
				// singleton.getSelectedLaborTrade(),
				// singleton.getSelectedLaborClassification(),
				// singleton.getSelectedLaborHours(), "L", "I",
				// dbAdapter.generateOfflineEntryID());
			} else {
				if (result != null) {
					System.out.println("Add labor response: " + result);
					// boolean isJSONObject = false;

					try {

						int StatusCode = singleton.getHTTPResponseStatusCode();
						JSONObject jObject = new JSONObject(result);
						String Status = jObject.getString("Status");

						if (Status.equals("Success") || StatusCode == 200
								|| StatusCode == 0) {
							if (singleton.isEnableOvertimeTracking()) {

								laborInsertResponse = dbAdapter
										.insertEntry(
												singleton
														.getSelectedLaborName(),
												singleton
														.getSelectedLaborTrade(),
												singleton
														.getSelectedLaborClassification(),
												singleton
														.getSelectedLaborHours(),
												"L", "N", jObject
														.getString("LID"));
								Log.d("Labour", "---->" + laborInsertResponse);
							} else {
								laborInsertResponse = dbAdapter
										.insertEntry(
												singleton
														.getSelectedLaborName(),
												singleton
														.getSelectedLaborTrade(),
												singleton
														.getSelectedLaborClassification(),
												singleton
														.getSelectedLaborHours(),
												"L", "N", jObject
														.getString("LID"));
							}
							System.out
									.println("laborInsertResponse inside Add Labor Success: "
											+ laborInsertResponse);
							singleton.setReloadPage(true);
						} else {
							// laborInsertResponse = dbAdapter.insertEntry(
							// singleton.getSelectedLaborName(),
							// singleton.getSelectedLaborTrade(),
							// singleton.getSelectedLaborClassification(),
							// singleton.getSelectedLaborHours(), "L",
							// "I", dbAdapter.generateOfflineEntryID());
							// System.out
							// .println("laborInsertResponse inside Add Labor Failure: "
							// + laborInsertResponse);
							// singleton.setReloadPage(true);
						}
					} catch (JSONException e) {
						// laborInsertResponse = dbAdapter.insertEntry(
						// singleton.getSelectedLaborName(),
						// singleton.getSelectedLaborTrade(),
						// singleton.getSelectedLaborClassification(),
						// singleton.getSelectedLaborHours(), "L", "I",
						// dbAdapter.generateOfflineEntryID());
						// System.out
						// .println("laborInsertResponse inside Add Labor JSON Exception: "
						// + laborInsertResponse);
						e.printStackTrace();
					}
				} else {
					// laborInsertResponse = dbAdapter.insertEntry(
					// singleton.getSelectedLaborName(),
					// singleton.getSelectedLaborTrade(),
					// singleton.getSelectedLaborClassification(),
					// singleton.getSelectedLaborHours(), "L", "I",
					// dbAdapter.generateOfflineEntryID());
					System.out
							.println("An error occurred! Could not add entry.");
				}
			}
			singleton.setReloadPage(true);
			onBackPressed();
		}
	}

	private class UpdateEntries extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			try {
				TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					@Override
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] arg0,
							String arg1) {
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
				HttpsURLConnection.setDefaultSSLSocketFactory(sc
						.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(hv);

				try {
					JSONObject jsonAddLabor = new JSONObject();
					jsonAddLabor.put("AccountID", singleton.getAccountId());
					jsonAddLabor.put("SubscriberID",
							singleton.getSubscriberId());
					jsonAddLabor.put("ProjectID",
							singleton.getSelectedProjectID());
					jsonAddLabor.put("ActivityId",
							singleton.getSelectedActivityID());
					jsonAddLabor.put("ID",
							singleton.getCurrentSelectedEntryID());
					jsonAddLabor.put("Name", singleton.getSelectedLaborName());
					jsonAddLabor
							.put("Trade", singleton.getSelectedLaborTrade());
					jsonAddLabor.put("Classification",
							singleton.getSelectedLaborClassification());
					jsonAddLabor
							.put("Hours", singleton.getSelectedLaborHours());
					// jsonAddLabor.put("Notes",
					// singleton.getSelectedLaborDescription());
					// jsonAddLabor.put("UserId", singleton.getUserId());
					System.out.println("labor sent id :"
							+ singleton.getCurrentSelectedEntryID());
					Log.d("back responce", "--->" + jsonAddLabor);
					return jsonDataPost.updateLaborEntry(jsonAddLabor);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String result) {
			long laborUpdateResponse = 0;

			Log.d("labour update responce", "--->" + result);
			// mProgressDialog.dismiss();
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;
				Toast.makeText(getApplicationContext(),
						"Sorry! Server could not be reached.",
						Toast.LENGTH_LONG).show();

				Log.d("offline entry", "--->" + singleton.isOfflineEntry());
				// if (singleton.isOfflineEntry())
				// laborUpdateResponse = dbAdapter.updateEntry(
				// singleton.getSelectedLaborName(),
				// singleton.getSelectedLaborTrade(),
				// singleton.getSelectedLaborClassification(),
				// singleton.getSelectedLaborHours(),
				// "L",
				// "I",
				// "OF"
				// + String.valueOf(singleton
				// .getCurrentSelectedEntryID()));
				// else
				// laborUpdateResponse = dbAdapter.updateEntry(
				// singleton.getSelectedLaborName(),
				// singleton.getSelectedLaborTrade(),
				// singleton.getSelectedLaborClassification(),
				// singleton.getSelectedLaborHours(), "L", "U",
				// String.valueOf(singleton.getCurrentSelectedEntryID()));
			} else {
				if (result != null) {
					System.out.println("Update labor response: " + result);
					int StatusCode = singleton.getHTTPResponseStatusCode();
					if (StatusCode == 200 || StatusCode == 0) {
						laborUpdateResponse = dbAdapter
								.updateEntry(
										singleton.getSelectedLaborName(),
										singleton.getSelectedLaborTrade(),
										singleton
												.getSelectedLaborClassification(),
										singleton.getSelectedLaborHours(), "L",
										"N", String.valueOf(singleton
												.getCurrentSelectedEntryID()));
					} else {
						// if (singleton.isOfflineEntry())
						// laborUpdateResponse = dbAdapter
						// .updateEntry(
						// singleton.getSelectedLaborName(),
						// singleton.getSelectedLaborTrade(),
						// singleton
						// .getSelectedLaborClassification(),
						// singleton.getSelectedLaborHours(),
						// "L",
						// "I",
						// "OF"
						// + String.valueOf(singleton
						// .getCurrentSelectedEntryID()));
						// else
						laborUpdateResponse = dbAdapter
								.updateEntry(
										singleton.getSelectedLaborName(),
										singleton.getSelectedLaborTrade(),
										singleton
												.getSelectedLaborClassification(),
										singleton.getSelectedLaborHours(), "L",
										"U", String.valueOf(singleton
												.getCurrentSelectedEntryID()));
					}

				} else {
					System.out
							.println("An error occurred! Could not update entry.");
					// if (singleton.isOfflineEntry())
					// laborUpdateResponse = dbAdapter.updateEntry(
					// singleton.getSelectedLaborName(),
					// singleton.getSelectedLaborTrade(),
					// singleton.getSelectedLaborClassification(),
					// singleton.getSelectedLaborHours(),
					// "L",
					// "I",
					// "OF"
					// + String.valueOf(singleton
					// .getCurrentSelectedEntryID()));
					// else
					laborUpdateResponse = dbAdapter.updateEntry(singleton
							.getSelectedLaborName(), singleton
							.getSelectedLaborTrade(), singleton
							.getSelectedLaborClassification(), singleton
							.getSelectedLaborHours(), "L", "U", String
							.valueOf(singleton.getCurrentSelectedEntryID()));
				}
			}
			singleton.setReloadPage(true);
			onBackPressed();
		}
	}

	private class DeleteLaborTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			try {
				TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					@Override
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] arg0,
							String arg1) {
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
				HttpsURLConnection.setDefaultSSLSocketFactory(sc
						.getSocketFactory());
				HttpsURLConnection.setDefaultHostnameVerifier(hv);

				try {
					JSONObject jsonLabor = new JSONObject();
					jsonLabor.put("Id", singleton.getCurrentSelectedEntryID());
					return jsonDataPost.deleteLaborEntry(jsonLabor);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String result) {
			long laborDeleteResponse = 0;
		
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;
				Toast.makeText(getApplicationContext(),
						"Sorry! Server could not be reached.",
						Toast.LENGTH_LONG).show();
				readDataFromDB();

			} else {
				if (result != null) {
					Log.d("Delete labor response: ", "--->" + result);
					int StatusCode = singleton.getHTTPResponseStatusCode();
					if (StatusCode == 200 || StatusCode == 0) {
						laborDeleteResponse = dbAdapter
								.deleteEntryByID(String.valueOf(singleton
										.getCurrentSelectedEntryID()));
					} else {
						
					}
				} else {
					System.out
							.println("An error occurred! Could not delete entry.");
					
				}
			}
			singleton.setReloadPage(true);
			onBackPressed();
		}
	}

	private void readDataFromDB() {
		if (singleton.getSelectedActivityID() == 0) {

			if (singleton.isEnableOvertimeTracking()) {
				Log.d("shidt enabled",
						"--->" + singleton.isEnableOvertimeTracking());
				long insertEntities = dbAdapter.insertEntryOffline(

				singleton.getSelectedLaborName(),
						singleton.getSelectedLaborTrade(),
						singleton.getSelectedLaborClassification(),
						singleton.getSelectedLaborHours(), "L", "N", "0",
						singleton.getSelectedTaskID(),
						singleton.getselectedActivityIdentityoffline(),
						"offline",
						Integer.parseInt(timeAndHalf.getText().toString()),
						Integer.parseInt(doubleTime.getText().toString()));

			} else {

				long insertEntities = dbAdapter.insertEntryOffline(

				singleton.getSelectedLaborName(),
						singleton.getSelectedLaborTrade(),
						singleton.getSelectedLaborClassification(),
						singleton.getSelectedLaborHours(), "L", "N", "0",
						singleton.getSelectedTaskID(),
						singleton.getselectedActivityIdentityoffline(),
						"offline", 0, 0);

			}

			long updateEntity = dbAdapter.updateActivity(
					singleton.getSelectedTaskID(),
					singleton.getSelectedActivityID());
			Log.d("en_insert labour 0 id", "----->" + updateEntity);

		} else {
			if (singleton.isEnableOvertimeTracking()) {
				Log.d("shidt enabled",
						"--->" + singleton.isEnableOvertimeTracking());
				long insertEntities = dbAdapter.insertEntryOffline(
						singleton.getSelectedLaborName(),
						singleton.getSelectedLaborTrade(),
						singleton.getSelectedLaborClassification(),
						singleton.getSelectedLaborHours(), "L", "N", "0",
						singleton.getSelectedTaskID(),
						singleton.getSelectedActivityID(), "offline",
						Integer.parseInt(timeAndHalf.getText().toString()),
						Integer.parseInt(doubleTime.getText().toString()));

			} else {
				long insertEntities = dbAdapter.insertEntryOffline(
						singleton.getSelectedLaborName(),
						singleton.getSelectedLaborTrade(),
						singleton.getSelectedLaborClassification(),
						singleton.getSelectedLaborHours(), "L", "N", "0",
						singleton.getSelectedTaskID(),
						singleton.getSelectedActivityID(), "offline", 0, 0);
			}
			long updateEntity = dbAdapter.updateActivity(
					singleton.getSelectedTaskID(),
					singleton.getSelectedActivityID());
			Log.d("en_insert with offline ", "----->" + updateEntity);
		}
		singleton.setReloadPage(true);
		onBackPressed();

	}
}