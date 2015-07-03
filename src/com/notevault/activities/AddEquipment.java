package com.notevault.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class AddEquipment extends Activity {

	Singleton singleton;
	ServerUtilities jsonDataPost = new ServerUtilities();
	String values[] = { "Name", "Company", "Status" };
	TextView deleteTextView;
	ImageView addEquipmentEntriesView;
	EditText qtyEditText, descEditText;
	TextView projectText, taskText, dateText, activityText, tv2;
	ListView equipmentView;
	TextView cancelTextView;
	ProgressDialog mProgressDialog;
	String errorMsg = "";
	DBAdapter dbAdapter;
	String glue = "-~-";
	LinearLayout addImageLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.addequipment);

		singleton = Singleton.getInstance();
		dbAdapter = DBAdapter.get_dbAdapter(this);
		projectText = (TextView) findViewById(R.id.greeting);
		taskText = (TextView) findViewById(R.id.txt);
		dateText = (TextView) findViewById(R.id.text);
		activityText = (TextView) findViewById(R.id.activitytxt);

		projectText.setText(singleton.getSelectedProjectName());
		taskText.setText(singleton.getSelectedTaskName());
		activityText.setText(singleton.getSelectedActivityName());
		dateText.setText(singleton.getCurrentSelectedDateFormatted());

		qtyEditText = (EditText) findViewById(R.id.editText4);
		// descEditText = (EditText)findViewById(R.id.desc_editText9);
		deleteTextView = (TextView) findViewById(R.id.textView6_delete);

		qtyEditText.setText(singleton.getSelectedEquipmentQty());
		if (descEditText != null) {
			// descEditText.setText(singleton.getSelectedEquipmentDescription());
		}

		if (singleton.isNewEntryFlag()) {
			System.out.println("New Equipment Entry");
		} else {
			TextView laborTextView = (TextView) findViewById(R.id.textdata);
			laborTextView.setText("Equipment");
			deleteTextView.setVisibility(View.VISIBLE);
		}

		cancelTextView = (TextView) findViewById(R.id.cancel);
		cancelTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		equipmentView = (ListView) findViewById(R.id.listView1);
		Myadapter myad = new Myadapter();
		equipmentView.setAdapter(myad);

		deleteTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						AddEquipment.this);
				alertDialog.setTitle("Delete Equipment");
				alertDialog.setMessage("Please confirm to delete.");

				alertDialog.setPositiveButton("Confirm",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								if(singleton.isOnline())
								{
								mProgressDialog = new ProgressDialog(
										AddEquipment.this);
								mProgressDialog.setMessage("Loading...");
								mProgressDialog.setIndeterminate(false);
								mProgressDialog.show();

								DeleteEquipmentTask deleteEquipment = new DeleteEquipmentTask();
								deleteEquipment.execute();
								}
								else{
									
									
										Log.d("offline","--->"+singleton.getCurrentSelectedEntryID()+" "+singleton.getSelectedEntityIdentity());
										if(singleton.getCurrentSelectedEntryID()==0)
										{
											
											
											int deleteEntity= dbAdapter.deleteEntryByIDOffline(singleton.getSelectedEntityIdentity());
											Log.d("delete Eid=0","--->"+deleteEntity+" "+singleton.getSelectedEntityIdentity());
											
											
										}
										else{
											
											int updateEntry=dbAdapter.updateEntryOffline(singleton.getSelectedEntityIdentity(),"D");
											Log.d("update Eid=0","--->"+updateEntry+" "+singleton.getSelectedEntityIdentity());
											
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

		qtyEditText
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

		addImageLayout = (LinearLayout) findViewById(R.id.image_layout);
		addEquipmentEntriesView = (ImageView) findViewById(R.id.save);
		addImageLayout.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				singleton.setSelectedEquipmentQty(qtyEditText.getText()
						.toString().trim());
				// singleton.setSelectedEquipmentDescription(descEditText.getText().toString().trim());
				String n, c, s, q;
				errorMsg = "";
				n = singleton.getSelectedEquipmentName();
				c = singleton.getSelectedEquipmentCompany();
				s = singleton.getSelectedEquipmentStatus();
				q = singleton.getSelectedEquipmentQty();

				if (n.equalsIgnoreCase(""))
					errorMsg = "Name";
				if (c.equalsIgnoreCase("")) {
					if (!errorMsg.equalsIgnoreCase(""))
						errorMsg += ", ";
					errorMsg += "Company";
				}
				if (s.equalsIgnoreCase("")) {
					if (!errorMsg.equalsIgnoreCase(""))
						errorMsg += ", ";
					errorMsg += "Status";
				}
				if (q.equalsIgnoreCase("")) {
					if (!errorMsg.equalsIgnoreCase(""))
						errorMsg += ", ";
					errorMsg += "Quantity";
				}
				if (errorMsg.length() > 0) {
					if (errorMsg.contains(",")) {
						errorMsg += " are mandatory.";
					} else {
						errorMsg += " is mandatory.";
					}
				}
				AlertDialog alertDialog = new AlertDialog.Builder(
						AddEquipment.this).create();
				alertDialog.setButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								errorMsg = "";
							}
						});

				if (errorMsg.equalsIgnoreCase("")) {
					if (singleton.isNewEntryFlag()) {
						if (singleton.isOnline()) {
							mProgressDialog = new ProgressDialog(
									AddEquipment.this);
							mProgressDialog.setMessage("Loading...");
							mProgressDialog.setIndeterminate(false);
							mProgressDialog.show();
							AddEquipmentEntries equipmentEntries = new AddEquipmentEntries();
							equipmentEntries.execute();
						} else {

							Toast.makeText(getApplicationContext(), "Offline",
									Toast.LENGTH_LONG).show();
							readDbData();
							
						}
					} else {
						mProgressDialog = new ProgressDialog(AddEquipment.this);
						mProgressDialog.setMessage("Loading...");
						mProgressDialog.setIndeterminate(false);
						mProgressDialog.show();
						System.out.println("update code should execute");
						UpdateEntries updateEquipment = new UpdateEntries();
						updateEquipment.execute();
					}
				} else {
					// display alert(errorMsg);
					alertDialog.setMessage(errorMsg);
					alertDialog.show();
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
			convertView = li.inflate(R.layout.customlist, null);
			TextView tv = (TextView) convertView.findViewById(R.id.textView1);
			tv2 = (TextView) convertView.findViewById(R.id.textView2);
			final String val[] = values[position].split("~");
			tv.setText(val[0]);
			tv2.setVisibility(View.VISIBLE);

			if (val[0].equals("Name")) {
				tv2.setText(singleton.getSelectedEquipmentName());
			} else if (val[0].equals("Company")) {
				tv2.setText(singleton.getSelectedEquipmentCompany());
			} else if (val[0].equals("Status")) {
				tv2.setText(singleton.getSelectedEquipmentStatus());
			}

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					singleton.setSelectedEquipmentQty(qtyEditText.getText()
							.toString().trim());
					// singleton.setSelectedEquipmentDescription(descEditText.getText().toString().trim());

					if (val[0].equals("Name")) {
						Intent intent = new Intent(AddEquipment.this,
								EquipmentNameListActivity.class);
						overridePendingTransition(0, 0);
						startActivity(intent);
					} else if (val[0].equals("Company")) {
						Intent intent = new Intent(AddEquipment.this,
								EquipmentCompanyList.class);
						overridePendingTransition(0, 0);
						startActivity(intent);
					} else if (val[0].equals("Status")) {
						Intent intent = new Intent(AddEquipment.this,
								EquipmentStatusList.class);
						overridePendingTransition(0, 0);
						startActivity(intent);
					}
				}
			});
			return convertView;
		}
	}

	private class AddEquipmentEntries extends AsyncTask<Void, Void, String> {

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
					JSONObject jsonAddEquipmentReqJSON = new JSONObject();
					jsonAddEquipmentReqJSON.put("AccountID",
							singleton.getAccountId());
					jsonAddEquipmentReqJSON.put("SubscriberID",
							singleton.getSubscriberId());
					jsonAddEquipmentReqJSON.put("ProjectID",
							singleton.getSelectedProjectID());
					jsonAddEquipmentReqJSON.put("ActivityId",
							singleton.getSelectedActivityID());
					jsonAddEquipmentReqJSON.put("ProjectDay",
							singleton.getCurrentSelectedDate());
					jsonAddEquipmentReqJSON.put("Name",
							singleton.getSelectedEquipmentName());
					jsonAddEquipmentReqJSON.put("Owner",
							singleton.getSelectedEquipmentCompany());
					jsonAddEquipmentReqJSON.put("Status",
							singleton.getSelectedEquipmentStatus());
					jsonAddEquipmentReqJSON.put("Quantity",
							singleton.getSelectedEquipmentQty());
					// jsonaddPersionnel.put("Notes",
					// singleton.getSelectedEquipmentDescription());
					jsonAddEquipmentReqJSON.put("TaskId",
							singleton.getSelectedTaskID());
					jsonAddEquipmentReqJSON
							.put("UserId", singleton.getUserId());
					return jsonDataPost
							.addEquipmentEntry(jsonAddEquipmentReqJSON);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				String ID = dbAdapter.generateOfflineEntryID();
				long equipmentInsertResponse = dbAdapter.insertEntry(
						singleton.getSelectedEquipmentName(),
						singleton.getSelectedEquipmentCompany(),
						singleton.getSelectedEquipmentStatus(),
						singleton.getSelectedEquipmentQty(), "E", "I", ID);
				EntriesListByDateActivity.collectiveConcatenatedEntryList
						.add("E" + glue + singleton.getSelectedEquipmentName()
								+ glue
								+ singleton.getSelectedEquipmentCompany()
								+ glue + singleton.getSelectedEquipmentStatus()
								+ glue + singleton.getSelectedEquipmentQty()
								+ glue + ID);
				System.out
						.println("equipmentInsertResponse inside Add Labor JSON Exception: "
								+ equipmentInsertResponse);
				singleton.setReloadPage(true);
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String result) {
			mProgressDialog.dismiss();
			long equipmentInsertResponse = 0;
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;
				Toast.makeText(getApplicationContext(),
						"Sorry! Server could not be reached.",
						Toast.LENGTH_LONG).show();
				equipmentInsertResponse = dbAdapter.insertEntry(
						singleton.getSelectedEquipmentName(),
						singleton.getSelectedEquipmentCompany(),
						singleton.getSelectedEquipmentStatus(),
						singleton.getSelectedEquipmentQty(), "E", "I",
						dbAdapter.generateOfflineEntryID());
				System.out
						.println("Add Equipment Entry unknown host exception.");
			} else {
				if (result != null) {
					JSONObject jObject = null;
					try {
						jObject = new JSONObject(result);
						String Status = jObject.getString("Status");
						int StatusCode = singleton.getHTTPResponseStatusCode();
						System.out
								.println("Add Equipment Entry response not null.");
						if (StatusCode == 200 || StatusCode == 0) {
							System.out
									.println("Add Equipment Entry status code = 200.");
							equipmentInsertResponse = dbAdapter.insertEntry(
									singleton.getSelectedEquipmentName(),
									singleton.getSelectedEquipmentCompany(),
									singleton.getSelectedEquipmentStatus(),
									singleton.getSelectedEquipmentQty(), "E",
									"N", jObject.getString("EID"));
							System.out
									.println("laborInsertResponse inside Add Labor Success: "
											+ equipmentInsertResponse);
							// singleton.setReloadPage(true);
							/*
							 * Intent intent = new Intent(AddEquipment.this,
							 * EntriesListActivity.class);
							 * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							 * startActivity(intent); finish();
							 */
						} else {
							System.out
									.println("Add Equipment Entry status code != 200.");
							equipmentInsertResponse = dbAdapter.insertEntry(
									singleton.getSelectedEquipmentName(),
									singleton.getSelectedEquipmentCompany(),
									singleton.getSelectedEquipmentStatus(),
									singleton.getSelectedEquipmentQty(), "E",
									"I", dbAdapter.generateOfflineEntryID());
							System.out
									.println("laborInsertResponse inside Add Labor Failure: "
											+ equipmentInsertResponse);
							singleton.setReloadPage(true);
						}
					} catch (JSONException e) {
						System.out
								.println("Add Equipment Entry response raised JSON exception.");
						equipmentInsertResponse = dbAdapter.insertEntry(
								singleton.getSelectedEquipmentName(),
								singleton.getSelectedEquipmentCompany(),
								singleton.getSelectedEquipmentStatus(),
								singleton.getSelectedEquipmentQty(), "E", "I",
								dbAdapter.generateOfflineEntryID());
						e.printStackTrace();
					}
				} else {
					System.out.println("Add Equipment Entry response is null.");
					equipmentInsertResponse = dbAdapter.insertEntry(
							singleton.getSelectedEquipmentName(),
							singleton.getSelectedEquipmentCompany(),
							singleton.getSelectedEquipmentStatus(),
							singleton.getSelectedEquipmentQty(), "E", "I",
							dbAdapter.generateOfflineEntryID());
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
					JSONObject jsonaddPersionnel = new JSONObject();
					jsonaddPersionnel
							.put("AccountID", singleton.getAccountId());
					jsonaddPersionnel.put("SubscriberID",
							singleton.getSubscriberId());
					jsonaddPersionnel.put("ProjectID",
							singleton.getSelectedProjectID());
					jsonaddPersionnel.put("ActivityId",
							singleton.getSelectedActivityID());
					jsonaddPersionnel.put("Name",
							singleton.getSelectedEquipmentName());
					jsonaddPersionnel.put("Owner",
							singleton.getSelectedEquipmentCompany());
					jsonaddPersionnel.put("Status",
							singleton.getSelectedEquipmentStatus());
					jsonaddPersionnel.put("Quantity",
							singleton.getSelectedEquipmentQty());
					// jsonaddPersionnel.put("Notes",
					// singleton.getSelectedEquipmentDescription());
					jsonaddPersionnel.put("ID",
							singleton.getCurrentSelectedEntryID());
					// jsonaddPersionnel.put("UserId", singleton.getUserId());
					System.out.println("equipment id.............."
							+ singleton.getCurrentSelectedEntryID());
					return jsonDataPost.updateEquipmentEntry(jsonaddPersionnel);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String result) {
			long equipmentUpdateResponse = 0;
			mProgressDialog.dismiss();
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;
				Toast.makeText(getApplicationContext(),
						"Sorry! Server could not be reached.",
						Toast.LENGTH_LONG).show();
				if (singleton.isOfflineEntry())
					equipmentUpdateResponse = dbAdapter.updateEntry(
							singleton.getSelectedEquipmentName(),
							singleton.getSelectedEquipmentCompany(),
							singleton.getSelectedEquipmentStatus(),
							singleton.getSelectedEquipmentQty(),
							"E",
							"I",
							"OF"
									+ String.valueOf(singleton
											.getCurrentSelectedEntryID()));
				else
					equipmentUpdateResponse = dbAdapter.updateEntry(singleton
							.getSelectedEquipmentName(), singleton
							.getSelectedEquipmentCompany(), singleton
							.getSelectedEquipmentStatus(), singleton
							.getSelectedEquipmentQty(), "E", "U", String
							.valueOf(singleton.getCurrentSelectedEntryID()));
			} else {
				if (result != null) {

					System.out.println("Update labor response: " + result);
					int StatusCode = singleton.getHTTPResponseStatusCode();
					if (StatusCode == 200 || StatusCode == 0) {
						equipmentUpdateResponse = dbAdapter.updateEntry(
								singleton.getSelectedEquipmentName(), singleton
										.getSelectedEquipmentCompany(),
								singleton.getSelectedEquipmentStatus(),
								singleton.getSelectedEquipmentQty(), "E", "N",
								String.valueOf(singleton
										.getCurrentSelectedEntryID()));
					} else {
						if (singleton.isOfflineEntry())
							equipmentUpdateResponse = dbAdapter
									.updateEntry(
											singleton
													.getSelectedEquipmentName(),
											singleton
													.getSelectedEquipmentCompany(),
											singleton
													.getSelectedEquipmentStatus(),
											singleton.getSelectedEquipmentQty(),
											"E",
											"I",
											"OF"
													+ String.valueOf(singleton
															.getCurrentSelectedEntryID()));
						else
							equipmentUpdateResponse = dbAdapter.updateEntry(
									singleton.getSelectedEquipmentName(),
									singleton.getSelectedEquipmentCompany(),
									singleton.getSelectedEquipmentStatus(),
									singleton.getSelectedEquipmentQty(), "E",
									"U", String.valueOf(singleton
											.getCurrentSelectedEntryID()));
					}

					/*
					 * Intent intent = new Intent(AddEquipment.this,
					 * EntriesListActivity.class);
					 * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					 * startActivity(intent); finish();
					 */
				} else {
					System.out
							.println("An error occurred! Could not update entry.");
					if (singleton.isOfflineEntry())
						equipmentUpdateResponse = dbAdapter.updateEntry(
								singleton.getSelectedEquipmentName(),
								singleton.getSelectedEquipmentCompany(),
								singleton.getSelectedEquipmentStatus(),
								singleton.getSelectedEquipmentQty(),
								"E",
								"I",
								"OF"
										+ String.valueOf(singleton
												.getCurrentSelectedEntryID()));
					else
						equipmentUpdateResponse = dbAdapter.updateEntry(
								singleton.getSelectedEquipmentName(), singleton
										.getSelectedEquipmentCompany(),
								singleton.getSelectedEquipmentStatus(),
								singleton.getSelectedEquipmentQty(), "E", "U",
								String.valueOf(singleton
										.getCurrentSelectedEntryID()));
				}
			}
			singleton.setReloadPage(true);
			onBackPressed();
		}
	}

	private class DeleteEquipmentTask extends AsyncTask<Void, Void, String> {

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
					JSONObject jsonEquipment = new JSONObject();
					jsonEquipment.put("Id",
							singleton.getCurrentSelectedEntryID());
					return jsonDataPost.deleteEquipmentEntry(jsonEquipment);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String result) {
			long equipmentDeleteResponse = 0;
			mProgressDialog.dismiss();
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;
				Toast.makeText(getApplicationContext(),
						"Sorry! Server could not be reached.",
						Toast.LENGTH_LONG).show();
				Toast.makeText(getApplicationContext(),
						"Sorry! Server could not be reached.",
						Toast.LENGTH_LONG).show();
				if (singleton.isOfflineEntry())
					equipmentDeleteResponse = dbAdapter.deleteEntryByID("OF"
							+ String.valueOf(singleton
									.getCurrentSelectedEntryID()));
				else
					equipmentDeleteResponse = dbAdapter.updateEntry(singleton
							.getSelectedEquipmentName(), singleton
							.getSelectedEquipmentCompany(), singleton
							.getSelectedEquipmentStatus(), singleton
							.getSelectedEquipmentQty(), "E", "D", String
							.valueOf(singleton.getCurrentSelectedEntryID()));
			} else {
				if (result != null) {

					System.out.println("Delete labor response: " + result);
					int StatusCode = singleton.getHTTPResponseStatusCode();
					if (StatusCode == 200 || StatusCode == 0) {
						equipmentDeleteResponse = dbAdapter
								.deleteEntryByID("OF"
										+ String.valueOf(singleton
												.getCurrentSelectedEntryID()));
					} else {
						if (singleton.isOfflineEntry())
							equipmentDeleteResponse = dbAdapter
									.deleteEntryByID("OF"
											+ String.valueOf(singleton
													.getCurrentSelectedEntryID()));
						else
							equipmentDeleteResponse = dbAdapter.updateEntry(
									singleton.getSelectedEquipmentName(),
									singleton.getSelectedEquipmentCompany(),
									singleton.getSelectedEquipmentStatus(),
									singleton.getSelectedEquipmentQty(), "E",
									"D", String.valueOf(singleton
											.getCurrentSelectedEntryID()));
					}
					/*
					 * Intent intent = new Intent(AddEquipment.this,
					 * EntriesListActivity.class);
					 * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					 * startActivity(intent); finish();
					 */
				} else {
					System.out
							.println("An error occurred! Could not delete entry.");
					if (singleton.isOfflineEntry())
						equipmentDeleteResponse = dbAdapter
								.deleteEntryByID("OF"
										+ String.valueOf(singleton
												.getCurrentSelectedEntryID()));
					else
						equipmentDeleteResponse = dbAdapter.updateEntry(
								singleton.getSelectedEquipmentName(), singleton
										.getSelectedEquipmentCompany(),
								singleton.getSelectedEquipmentStatus(),
								singleton.getSelectedEquipmentQty(), "E", "D",
								String.valueOf(singleton
										.getCurrentSelectedEntryID()));
				}
			}
			singleton.setReloadPage(true);
			onBackPressed();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.onCreate(null);
	}
	protected void readDbData() {
		if (singleton.getSelectedActivityID() == 0) {
			if (singleton.getSelectedTaskID() == 0) {
				long insertEntities = dbAdapter.insertEntryOffline(
						singleton
								.getSelectedEquipmentName(),
						singleton
								.getSelectedEquipmentCompany(),
						singleton
								.getSelectedEquipmentStatus(),
						singleton.getSelectedEquipmentQty(),
						"E",
						"N",
						"0",
						singleton
								.getSelectedTaskIdentityoffline(),
						singleton
								.getselectedActivityIdentityoffline(),
						"offline");
				Log.d("en_insert labour 0 id", "----->"
						+ insertEntities);
				long updateEntity = dbAdapter.updateActivity(
						singleton
								.getSelectedTaskIdentityoffline(),
						singleton.getSelectedActivityID());
				Log.d("en_insert labour 0 id", "----->"
						+ insertEntities + " "
						+ updateEntity);
			} else {
				long insertEntities = dbAdapter.insertEntryOffline(
						singleton.getSelectedEquipmentName(),
						singleton.getSelectedEquipmentCompany(),
						singleton
								.getSelectedEquipmentStatus(),
						singleton.getSelectedEquipmentQty(),
						"E",
						"N",
						"0",
						singleton.getSelectedTaskID(),
						singleton
								.getselectedActivityIdentityoffline(),
						"offline");
				long updateEntity = dbAdapter.updateActivity(
						singleton.getSelectedTaskID(),
						singleton.getSelectedActivityID());
				Log.d("en_insert labour 0 id", "----->"
						+ insertEntities + " "
						+ updateEntity);
			}

		} else {
			long insertEntities = dbAdapter.insertEntryOffline(
					singleton.getSelectedEquipmentName(),
					singleton.getSelectedEquipmentCompany(),
					singleton
							.getSelectedEquipmentStatus(),
					singleton.getSelectedEquipmentQty(), "E",
					"N", "0",
					singleton.getSelectedTaskID(),
					singleton.getSelectedActivityID(),
					"offline");
			long updateEntity = dbAdapter.updateActivity(
					singleton.getSelectedTaskID(),
					singleton.getSelectedActivityID());
			Log.d("en_insert with offline ", "----->"
					+ insertEntities + " " + updateEntity);

		}
		singleton.setReloadPage(true);
		onBackPressed();

		
	}
}