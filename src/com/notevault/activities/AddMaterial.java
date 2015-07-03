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

import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;

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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AddMaterial extends Activity {

	Singleton singleton;
	String values[] = { "Name", "Company", "Status" };
	ServerUtilities jsonDataPost = new ServerUtilities();
	TextView deleteTextView;
	ImageView addMaterialEntriesView;
	EditText qtyEditText, descEditText;
	TextView projectText, taskText, dateText, activityText;
	ListView materialView;
	TextView cancelTextView;
	private ProgressDialog mProgressDialog;
	String errorMsg = "";
	DBAdapter dbAdapter;
	String glue = "-~-";
	LinearLayout addImageLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addmaterial);

		singleton = Singleton.getInstance();
		dbAdapter = DBAdapter.get_dbAdapter(this);
		projectText = (TextView) findViewById(R.id.greeting);
		taskText = (TextView) findViewById(R.id.txt);
		activityText = (TextView) findViewById(R.id.activitytxt);
		dateText = (TextView) findViewById(R.id.text);

		qtyEditText = (EditText) findViewById(R.id.editText4);
		// descEditText = (EditText)findViewById(R.id.desc_editText6);
		deleteTextView = (TextView) findViewById(R.id.textView6_delete);

		projectText.setText(singleton.getSelectedProjectName());
		taskText.setText(singleton.getSelectedTaskName());
		activityText.setText(singleton.getSelectedActivityName());
		dateText.setText(singleton.getCurrentSelectedDateFormatted());

		materialView = (ListView) findViewById(R.id.listView1);
		Myadapter myad = new Myadapter();
		materialView.setAdapter(myad);
		qtyEditText.setText(singleton.getSelectedMaterialQty());
		// descEditText.setText(singleton.getSelectedMaterialDescription());
		if (singleton.isNewEntryFlag()) {
			System.out.println("New Material Entry");
		} else {
			TextView materialTextView = (TextView) findViewById(R.id.textdata);
			materialTextView.setText("Material");
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
						AddMaterial.this);
				alertDialog.setTitle("Delete Material");
				alertDialog.setMessage("Please confirm to delete.");

				alertDialog.setPositiveButton("Confirm",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								if (singleton.isOnline()) {

									mProgressDialog = new ProgressDialog(
											AddMaterial.this);
									mProgressDialog.setMessage("Loading...");
									mProgressDialog.setIndeterminate(false);
									mProgressDialog.show();

									DeleteMaterialTask deletelabor = new DeleteMaterialTask();
									deletelabor.execute();
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

										int updateEntry = dbAdapter.updateEntryOffline(singleton
												.getSelectedEntityIdentity(),"D");
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
		addMaterialEntriesView = (ImageView) findViewById(R.id.save);
		addImageLayout.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				singleton.setSelectedMaterialQty(qtyEditText.getText()
						.toString().trim());
				// singleton.setSelectedMaterialDescription(descEditText.getText().toString().trim());
				String n, c, s, q;
				errorMsg = "";
				n = singleton.getSelectedMaterialName();
				c = singleton.getSelectedMaterialCompany();
				s = singleton.getSelectedMaterialStatus();
				q = singleton.getSelectedMaterialQty();

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
						errorMsg += " are mandatory";
					} else {
						errorMsg += " is mandatory";
					}
				}
				AlertDialog alertDialog = new AlertDialog.Builder(
						AddMaterial.this).create();
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
									AddMaterial.this);
							mProgressDialog.setMessage("Loading...");
							mProgressDialog.setIndeterminate(false);
							mProgressDialog.show();
							AddMaterialEntries materialEntries = new AddMaterialEntries();
							materialEntries.execute();
						} else {

							Toast.makeText(getApplicationContext(), "Offline",
									Toast.LENGTH_LONG).show();
							readDBData();
							
						}

					} else {
						mProgressDialog = new ProgressDialog(AddMaterial.this);
						mProgressDialog.setMessage("Loading...");
						mProgressDialog.setIndeterminate(false);
						mProgressDialog.show();
						System.out.println("update code should execute");
						UpdateEntries updateMaterial = new UpdateEntries();
						updateMaterial.execute();
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
			TextView tv2 = (TextView) convertView.findViewById(R.id.textView2);
			final String val[] = values[position].split("~");
			tv.setText(val[0]);
			tv2.setVisibility(View.VISIBLE);

			if (val[0].equals("Name")) {
				tv2.setText(singleton.getSelectedMaterialName());
			} else if (val[0].equals("Company")) {
				tv2.setText(singleton.getSelectedMaterialCompany());
			} else if (val[0].equals("Status")) {
				tv2.setText(singleton.getSelectedMaterialStatus());
			}

			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					singleton.setSelectedMaterialQty(qtyEditText.getText()
							.toString().trim());
					// singleton.setSelectedMaterialDescription(descEditText.getText().toString().trim());
					if (val[0].equals("Name")) {
						Intent intent = new Intent(AddMaterial.this,
								MaterialNameList.class);
						overridePendingTransition(0, 0);
						startActivity(intent);
					} else if (val[0].equals("Company")) {
						Intent intent = new Intent(AddMaterial.this,
								MaterialCompanyList.class);
						overridePendingTransition(0, 0);
						startActivity(intent);
					} else if (val[0].equals("Status")) {
						Intent intent = new Intent(AddMaterial.this,
								MaterialStatusList.class);
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
		this.onCreate(null);
		System.err.println(singleton.getSelectedMaterialStatus());
	}

	private class AddMaterialEntries extends AsyncTask<Void, Void, String> {

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
					JSONObject jsonAddMaterialJSON = new JSONObject();
					jsonAddMaterialJSON.put("AccountID",
							singleton.getAccountId());
					jsonAddMaterialJSON.put("SubscriberID",
							singleton.getSubscriberId());
					jsonAddMaterialJSON.put("ProjectID",
							singleton.getSelectedProjectID());
					jsonAddMaterialJSON.put("ActivityId",
							singleton.getSelectedActivityID());
					jsonAddMaterialJSON.put("ProjectDay",
							singleton.getCurrentSelectedDate());
					jsonAddMaterialJSON.put("Name",
							singleton.getSelectedMaterialName());
					jsonAddMaterialJSON.put("Company",
							singleton.getSelectedMaterialCompany());
					jsonAddMaterialJSON.put("Status",
							singleton.getSelectedMaterialStatus());
					jsonAddMaterialJSON.put("Quantity",
							singleton.getSelectedMaterialQty());
					// jsonaddPersionnel.put("Notes",
					// singleton.getSelectedMaterialDescription());
					jsonAddMaterialJSON.put("TaskId",
							singleton.getSelectedMaterialDescription());
					jsonAddMaterialJSON.put("UserId", singleton.getUserId());
					return jsonDataPost.addMaterialEntry(jsonAddMaterialJSON);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String result) {
			long materialInsertResponse = 0;
			mProgressDialog.dismiss();
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;
				Toast.makeText(getApplicationContext(),
						"Sorry! Server could not be reached.",
						Toast.LENGTH_LONG).show();
				materialInsertResponse = dbAdapter.insertEntry(
						singleton.getSelectedMaterialName(),
						singleton.getSelectedMaterialCompany(),
						singleton.getSelectedMaterialStatus(),
						singleton.getSelectedMaterialQty(), "M", "I",
						dbAdapter.generateOfflineEntryID());
			} else {
				if (result != null) {

					JSONObject jObject = null;
					try {
						jObject = new JSONObject(result);
						String Status = jObject.getString("Status");
						int StatusCode = singleton.getHTTPResponseStatusCode();
						if (StatusCode == 200 || StatusCode == 0) {
							materialInsertResponse = dbAdapter.insertEntry(
									singleton.getSelectedMaterialName(),
									singleton.getSelectedMaterialCompany(),
									singleton.getSelectedMaterialStatus(),
									singleton.getSelectedMaterialQty(), "M",
									"N", jObject.getString("MID"));
							System.out
									.println("materialInsertResponse inside Add Labor Success: "
											+ materialInsertResponse);
							/*
							 * singleton.setReloadPage(true); Intent intent =
							 * new Intent(AddMaterial.this,
							 * EntriesListActivity.class);
							 * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							 * startActivity(intent); finish();
							 */
						} else {
							materialInsertResponse = dbAdapter.insertEntry(
									singleton.getSelectedMaterialName(),
									singleton.getSelectedMaterialCompany(),
									singleton.getSelectedMaterialStatus(),
									singleton.getSelectedMaterialQty(), "M",
									"I", dbAdapter.generateOfflineEntryID());
							System.out
									.println("materialInsertResponse inside Add Labor Failure: "
											+ materialInsertResponse);
							singleton.setReloadPage(true);
						}
					} catch (JSONException e) {
						e.printStackTrace();
						materialInsertResponse = dbAdapter.insertEntry(
								singleton.getSelectedMaterialName(),
								singleton.getSelectedMaterialCompany(),
								singleton.getSelectedMaterialStatus(),
								singleton.getSelectedMaterialQty(), "M", "I",
								dbAdapter.generateOfflineEntryID());
					}
				} else {
					System.out
							.println("An error occurred! Could not add entry.");
					materialInsertResponse = dbAdapter.insertEntry(
							singleton.getSelectedMaterialName(),
							singleton.getSelectedMaterialCompany(),
							singleton.getSelectedMaterialStatus(),
							singleton.getSelectedMaterialQty(), "M", "I",
							dbAdapter.generateOfflineEntryID());
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

					JSONObject jsonAddMaterialJSON = new JSONObject();
					jsonAddMaterialJSON.put("AccountID",
							singleton.getAccountId());
					jsonAddMaterialJSON.put("SubscriberID",
							singleton.getSubscriberId());
					jsonAddMaterialJSON.put("ProjectID",
							singleton.getSelectedProjectID());
					jsonAddMaterialJSON.put("ActivityId",
							singleton.getSelectedActivityID());
					jsonAddMaterialJSON.put("Name",
							singleton.getSelectedMaterialName());
					jsonAddMaterialJSON.put("Company",
							singleton.getSelectedMaterialCompany());
					jsonAddMaterialJSON.put("Status",
							singleton.getSelectedMaterialStatus());
					jsonAddMaterialJSON.put("Quantity",
							singleton.getSelectedMaterialQty());
					// jsonaddPersionnel.put("Notes",
					// singleton.getSelectedMaterialDescription());
					// jsonaddPersionnel.put("ProjectDay",
					// singleton.getCurrentSelectedDate());
					jsonAddMaterialJSON.put("ID",
							singleton.getCurrentSelectedEntryID());
					// jsonaddPersionnel.put("UserId", singleton.getUserId());
					System.out.println("equipment id.............."
							+ singleton.getCurrentSelectedEntryID());

					return jsonDataPost
							.updateMaterialEntry(jsonAddMaterialJSON);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String result) {
			long materialUpdateResponse = 0;
			mProgressDialog.dismiss();
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;
				Toast.makeText(getApplicationContext(),
						"Sorry! Server could not be reached.",
						Toast.LENGTH_LONG).show();
				if (singleton.isOfflineEntry())
					materialUpdateResponse = dbAdapter.updateEntry(
							singleton.getSelectedMaterialName(),
							singleton.getSelectedMaterialCompany(),
							singleton.getSelectedMaterialStatus(),
							singleton.getSelectedMaterialQty(),
							"M",
							"I",
							"OF"
									+ String.valueOf(singleton
											.getCurrentSelectedEntryID()));
				else
					materialUpdateResponse = dbAdapter.updateEntry(singleton
							.getSelectedMaterialName(), singleton
							.getSelectedMaterialCompany(), singleton
							.getSelectedMaterialStatus(), singleton
							.getSelectedMaterialQty(), "M", "U", String
							.valueOf(singleton.getCurrentSelectedEntryID()));
			} else {
				if (result != null) {
					System.out.println("Update material response: " + result);
					int StatusCode = singleton.getHTTPResponseStatusCode();
					if (StatusCode == 200 || StatusCode == 0) {
						materialUpdateResponse = dbAdapter.updateEntry(
								singleton.getSelectedMaterialName(), singleton
										.getSelectedMaterialCompany(),
								singleton.getSelectedMaterialStatus(),
								singleton.getSelectedMaterialQty(), "M", "N",
								String.valueOf(singleton
										.getCurrentSelectedEntryID()));
					} else {
						if (singleton.isOfflineEntry())
							materialUpdateResponse = dbAdapter
									.updateEntry(
											singleton.getSelectedMaterialName(),
											singleton
													.getSelectedMaterialCompany(),
											singleton
													.getSelectedMaterialStatus(),
											singleton.getSelectedMaterialQty(),
											"M",
											"I",
											"OF"
													+ String.valueOf(singleton
															.getCurrentSelectedEntryID()));
						else
							materialUpdateResponse = dbAdapter.updateEntry(
									singleton.getSelectedMaterialName(),
									singleton.getSelectedMaterialCompany(),
									singleton.getSelectedMaterialStatus(),
									singleton.getSelectedMaterialQty(), "M",
									"U", String.valueOf(singleton
											.getCurrentSelectedEntryID()));
					}
					/*
					 * Intent intent = new Intent(AddMaterial.this,
					 * EntriesListActivity.class);
					 * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					 * startActivity(intent); finish();
					 */
				} else {
					System.out
							.println("An error occurred! Could not update entry.");
					if (singleton.isOfflineEntry())
						materialUpdateResponse = dbAdapter.updateEntry(
								singleton.getSelectedMaterialName(),
								singleton.getSelectedMaterialCompany(),
								singleton.getSelectedMaterialStatus(),
								singleton.getSelectedMaterialQty(),
								"M",
								"I",
								"OF"
										+ String.valueOf(singleton
												.getCurrentSelectedEntryID()));
					else
						materialUpdateResponse = dbAdapter.updateEntry(
								singleton.getSelectedMaterialName(), singleton
										.getSelectedMaterialCompany(),
								singleton.getSelectedMaterialStatus(),
								singleton.getSelectedMaterialQty(), "M", "U",
								String.valueOf(singleton
										.getCurrentSelectedEntryID()));
				}
			}
			singleton.setReloadPage(true);
			onBackPressed();
		}
	}

	private class DeleteMaterialTask extends AsyncTask<Void, Void, String> {

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
					JSONObject jsonAddMaterial = new JSONObject();
					jsonAddMaterial.put("Id",
							singleton.getCurrentSelectedEntryID());
					return jsonDataPost.deleteMaterialEntry(jsonAddMaterial);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String result) {
			mProgressDialog.dismiss();
			long materialDeleteResponse = 0;
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;
				Toast.makeText(getApplicationContext(),
						"Sorry! Server could not be reached.",
						Toast.LENGTH_LONG).show();
				if (singleton.isOfflineEntry())
					materialDeleteResponse = dbAdapter.deleteEntryByID("OF"
							+ String.valueOf(singleton
									.getCurrentSelectedEntryID()));
				else
					materialDeleteResponse = dbAdapter.updateEntry(singleton
							.getSelectedMaterialName(), singleton
							.getSelectedMaterialCompany(), singleton
							.getSelectedMaterialStatus(), singleton
							.getSelectedMaterialQty(), "M", "D", String
							.valueOf(singleton.getCurrentSelectedEntryID()));
			} else {
				if (result != null) {
					System.out.println("Update material response: " + result);
					int StatusCode = singleton.getHTTPResponseStatusCode();
					if (StatusCode == 200 || StatusCode == 0) {

					} else {
						if (singleton.isOfflineEntry())
							materialDeleteResponse = dbAdapter
									.deleteEntryByID("OF"
											+ String.valueOf(singleton
													.getCurrentSelectedEntryID()));
						else
							materialDeleteResponse = dbAdapter.updateEntry(
									singleton.getSelectedMaterialName(),
									singleton.getSelectedMaterialCompany(),
									singleton.getSelectedMaterialStatus(),
									singleton.getSelectedMaterialQty(), "M",
									"D", String.valueOf(singleton
											.getCurrentSelectedEntryID()));
					}
					/*
					 * Intent intent = new Intent(AddMaterial.this,
					 * EntriesListActivity.class);
					 * intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					 * startActivity(intent); finish();
					 */
				} else {
					System.out
							.println("An error occurred! Could not delete entry.");
					if (singleton.isOfflineEntry())
						materialDeleteResponse = dbAdapter.deleteEntryByID("OF"
								+ String.valueOf(singleton
										.getCurrentSelectedEntryID()));
					else
						materialDeleteResponse = dbAdapter.updateEntry(
								singleton.getSelectedMaterialName(), singleton
										.getSelectedMaterialCompany(),
								singleton.getSelectedMaterialStatus(),
								singleton.getSelectedMaterialQty(), "M", "D",
								String.valueOf(singleton
										.getCurrentSelectedEntryID()));
				}
			}
			singleton.setReloadPage(true);
			onBackPressed();
		}
	}
	protected void readDBData() {
		if (singleton.getSelectedActivityID() == 0) {
			if (singleton.getSelectedTaskID() == 0) {
				long insertEntities = dbAdapter.insertEntryOffline(
						singleton.getSelectedMaterialName(),
						singleton
								.getSelectedMaterialCompany(),
						singleton
								.getSelectedMaterialStatus(),
						singleton.getSelectedMaterialQty(),
						"M",
						"N",
						"0",
						singleton
								.getSelectedTaskIdentityoffline(),
						singleton
								.getselectedActivityIdentityoffline(),
						"offline");
				Log.d("en_insert labour 0 id",
						"----->"
								+ insertEntities
								+ "  "
								+ singleton
										.getSelectedTaskIdentityoffline()
								+ "  "
								+ singleton
										.getSelectedActivityID());
				long updateEntity = dbAdapter.updateActivity(
						singleton
								.getSelectedTaskIdentityoffline(),
						singleton.getSelectedActivityID());
				Log.d("en_insert labour 0 id", "----->"
						+ insertEntities + " "
						+ updateEntity);
			} else {
				long insertEntities = dbAdapter.insertEntryOffline(
						singleton.getSelectedMaterialName(),
						singleton
								.getSelectedMaterialCompany(),
						singleton
								.getSelectedMaterialStatus(),
						singleton.getSelectedMaterialQty(),
						"M",
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
					singleton.getSelectedMaterialName(),
					singleton.getSelectedMaterialCompany(),
					singleton.getSelectedMaterialStatus(),
					singleton.getSelectedMaterialQty(),
					"M", "N", "0",
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