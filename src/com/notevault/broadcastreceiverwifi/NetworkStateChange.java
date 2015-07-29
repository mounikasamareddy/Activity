package com.notevault.broadcastreceiverwifi;

import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.notevault.activities.ActivitiesListActivity;
import com.notevault.activities.AddActivity;
import com.notevault.activities.ClassificationList;
import com.notevault.activities.EntriesListActivity;
import com.notevault.activities.EntriesListByDateActivity;
import com.notevault.activities.ProjectListActivity;
import com.notevault.activities.SettingActivity;
import com.notevault.activities.TasksListActivity;
import com.notevault.adapter.TaskAdapter;
import com.notevault.arraylistsupportclasses.ANetworkData;
import com.notevault.arraylistsupportclasses.ActivityNetworkDB;
import com.notevault.arraylistsupportclasses.EntriesNetworkDB;
import com.notevault.arraylistsupportclasses.LoginData;
import com.notevault.arraylistsupportclasses.TNetworkData;
import com.notevault.arraylistsupportclasses.TaskData;
import com.notevault.arraylistsupportclasses.TaskNetworkDB;
import com.notevault.arraylistsupportclasses.TasksDB;

import com.notevault.arraysupportclasses.UpdateEntriesProjo;
import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;
import com.notevault.support.Utilities;

public class NetworkStateChange extends BroadcastReceiver {
	DBAdapter dbadapter;
	NetworkInfo info;
	int i, j;
	int projectid;
	int Tidentity;
	String tName, newActivityName;
	
	Singleton singleton;
	List<ActivityNetworkDB> data2, data1;
	List<EntriesNetworkDB> entries1, entries2;
	List<UpdateEntriesProjo> delete1, delete2;
	ServerUtilities jsonDataPost = new ServerUtilities();
	String Adate;
	List<UpdateEntriesProjo> uEntries, uEntries1;
	int AAid, ActivityofflineId;
	Context context;
	List<TasksDB> data;
	int ID = 0, tasksize, activitysize, EPid, EAid, Etid, Eidentity1,
			Eidentity2, Eidentity3;
	String name, trade, clasification, hrs, entridate, hours;

	@Override
	public void onReceive(Context context, Intent intent) {
this.context=context;
		singleton = Singleton.getInstance();
		dbadapter = DBAdapter.get_dbAdapter(context);

		boolean mobileDataEnabled = false; // Assume disabled
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			Class cmClass = Class.forName(cm.getClass().getName());
			Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
			method.setAccessible(true); // Make the method callable
			// get the setting for "mobile data"
			mobileDataEnabled = (Boolean) method.invoke(cm);

			if (intent.getAction().equals(
					WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				info = intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (info.isConnected()) {
					
					Log.d("wifi Available ", "---->" + info.isConnected());
					singleton.setOnline(true);

					ReadWifiData();
				} else if (mobileDataEnabled) {

					Log.d("Netowk Available ", "---->" + mobileDataEnabled);

					ReadWifiData();
				} else {
					singleton.setOnline(false);
					
					Log.d(" not Netowk Available ", "---->");
				}

			}

		} catch (Exception e) {
			Toast.makeText(context, "Mobile NetWork Issue!", Toast.LENGTH_LONG)
					.show();
		}

	}

	private void ReadWifiData() {

		Utilities.ActivityNetworkData.clear();
		// read task Db here
		Log.d("readdata", "-->");

		data2 = dbadapter.getAllOfflineActivityRecords();
		Log.d("readdata", "-->" + data2.size());
		entries2 = dbadapter.getAllOfflineEntriesRecords();
		List<UpdateEntriesProjo> uEntries = dbadapter
				.getAllEntriesUpdateRecords("U");

		delete1 = dbadapter.getAllEntriesUpdateRecords("D");

		Cursor c = dbadapter.queryCredentialsLogin();
		Log.d("curfdsfdf", "--->" + c.getCount());
		
		Log.d("shift enabled","--->"+singleton.isEnableShiftTracking());
		if (c.getCount() != 0) {

			Log.d("curfdsfdf", "--->" + c.getCount());
			if (c.moveToFirst()) {

				singleton.setUserId(c.getInt(c.getColumnIndex("UserID")));
				singleton.setAccountId(c.getInt(c.getColumnIndex("AccountID")));
				singleton.setCompanyId(c.getInt(c.getColumnIndex("CompanyID")));
				singleton.setLNCID(c.getInt(c.getColumnIndex("LNPCID")));
				singleton.setLTCID(c.getInt(c.getColumnIndex("LTCID")));
				singleton.setLCCID(c.getInt(c.getColumnIndex("LCCID")));
				singleton.setENCID(c.getInt(c.getColumnIndex("ENCID")));
				singleton.setCCID(c.getInt(c.getColumnIndex("CCID")));
				singleton.setMNCID(c.getInt(c.getColumnIndex("MNCID")));
				singleton.setSubscriberId(c.getInt(c.getColumnIndex("SubID")));
				singleton.setCompanyName(c.getString(c
						.getColumnIndex("Company")));
				singleton.setUsername(c.getString(c
						.getColumnIndex("displayname")));
				
				Log.d("network details online",
						"--->" + singleton.getAccountId() + " "
								+ singleton.getSubscriberId());
			}

		}
		if (delete1.size() > 0) {
			new DeleteLaborTask().execute();

		} else if (uEntries.size() > 0) {

			Log.d("readdata", "-->" + uEntries.size());
			UpdateEntries updateLabor = new UpdateEntries();
			updateLabor.execute();
		}

		else if (data2.size() > 0) {
			Log.d("activity", "--->" + data2.size());
			ReadActivitydata();
		} else if (entries2.size() > 0) {

			Log.d("entries", "--->" + entries2.size());
			readEntriesdata();

		}

	}

	private void ReadActivitydata() {
		// read Activity information here

		new AddActivityPtoject().execute();

	}

	public class AddActivityPtoject extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
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

					data1 = dbadapter.getAllOfflineActivityRecords();

					Log.d("activity length", "--->" + data1.size());
					if (data1.size() == 0) {
						new EntiesLabourData().execute();
					} else {
						for (ActivityNetworkDB val : data1) {
							Log.d("shift enabled","--->"+singleton.isEnableShiftTracking());
							Log.d("Activity status offline",
									"--->" + val.getAIdentity() + " "
											+ val.getAId() + " "
											+ val.getADate() + " "
											+ val.getAName() + " "
											+ val.getTid() + " "
											+ val.getAstatus());

							//newActivityName = val.getAName().replace("@", "");
							singleton.setSelectedTaskID(val.getTid());
							singleton.setCurrentSelectedDate(val.getADate());
							Adate = val.getADate();
							AAid = val.getTid();
							ActivityofflineId = val.getAIdentity();
							String GMTdateTime = new SimpleDateFormat(
									"yyyy-MM-dd", Locale.ENGLISH)
									.format(new SimpleDateFormat("yyyyMMd")
											.parse(singleton
													.getCurrentSelectedDate()))
									+ " "
									+ new SimpleDateFormat("HH:mm:ss")
											.format(new Date());

							JSONObject jsonAddActivity = new JSONObject();

							jsonAddActivity.put("TaskId", AAid);
							jsonAddActivity.put("Name", val.getAName());
							jsonAddActivity
									.put("UserId", singleton.getUserId());
							jsonAddActivity.put("DateCreated", GMTdateTime);
							jsonAddActivity.put("ProjectDay", Adate);
							
							if (val.getShift()!=10) {
							jsonAddActivity.put("Shift", val.getShift());
							}
							Log.d("details", "------>" + jsonAddActivity);
							System.out.println("Request: jsonAddActivity: "
									+ jsonAddActivity);
							// System.out.println("GMT Date: %%%%%%%%%%%%%%%%%%%%%%%%%%%% : "+
							// GMTdateTime);
							return jsonDataPost
									.addActivityToTask(jsonAddActivity);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			System.out.println("Response add activity.................."
					+ result);
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;

			} else if (result != null) {

				int StatusCode = singleton.getHTTPResponseStatusCode();
				JSONObject json;
				try {
					json = new JSONObject(result);
					int Status = json.getInt("Status");
					if (Status == 0 || Status == 200) {

						int ActivityId = Integer.parseInt(json.getString("AI"));
						Log.d("details", "------>" + ActivityofflineId + "  "
								+ ActivityId + " " + newActivityName);
						singleton.setSelectedActivityID(ActivityId);
						long updateActivity = dbadapter
								.updateActivityActivityIdOnBC(
										ActivityofflineId, ActivityId,
										newActivityName);
						long updateEntries = dbadapter
								.updateEntityActivityIdOnBC(ActivityofflineId,
										ActivityId);
						Log.d("update BC", "--->" + updateActivity + " "
								+ updateEntries);

					} else if (Status == 201) {
						Log.d("An activity with this name already exists!",
								"--->");

					} else {
						Log.d("An error occurred while adding activity!",
								"--->");

					}

					new AddActivityPtoject().execute();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}

	}

	private void readEntriesdata() {

		// read Activity information herevcbv

		new EntiesLabourData().execute();

	}

	public class EntiesLabourData extends AsyncTask<Void, Void, String> {

		protected String doInBackground(Void... params) {

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
					entries1 = dbadapter.getAllOfflineEntriesRecords("L");
					Log.d("entries length", "--->" + entries1.size());
					if (entries1.size() == 0) {
						new EntriesEquietmentData().execute();
					} else {
						for (EntriesNetworkDB val : entries1) {
							singleton.setSelectedEntriesIdentityoffline(val
									.getEIdenty());
							name = val.getEname();
							trade = val.getTRD_C();
							clasification = val.getClassesI();
							entridate = val.getEdate();
							EPid = val.getPID();
							Etid = val.getTid();
							EAid = val.getAid();
							hours = val.getHR_QTY();

							JSONObject jsonAddLabor = new JSONObject();
							Log.d("data", "-->" + singleton.getAccountId()
									+ " " + singleton.getSubscriberId() + " "
									+ EPid + " " + EAid + " " + entridate + " "
									+ name + " " + trade + " " + clasification
									+ " " + hours+"..."+val.getDoubletime()+"..."+val.getTimeandhalf());

							jsonAddLabor.put("AccountID",
									singleton.getAccountId());
							jsonAddLabor.put("SubscriberID",
									singleton.getSubscriberId());
							jsonAddLabor.put("ProjectID", EPid);
							jsonAddLabor.put("ActivityId", EAid);
							jsonAddLabor.put("ProjectDay", entridate);
							jsonAddLabor.put("Name", name);
							jsonAddLabor.put("Trade", trade);
							jsonAddLabor.put("Classification", clasification);
							jsonAddLabor.put("Hours", hours);
							
							jsonAddLabor.put("TaskId", val.getTid());
							jsonAddLabor.put("UserId", singleton.getUserId());
							if(val.getDoubletime()>0)
							{
								jsonAddLabor
								.put("TimeAndHalf",val.getTimeandhalf() );
								jsonAddLabor.put("DoubleTime",val.getDoubletime());
							}
							Log.d("details", "-->" + jsonAddLabor);
							return jsonDataPost.addLaborEntry(jsonAddLabor);
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {

				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String result) {

			Log.d("result", "--->" + result);

			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;

			} else if (result != null) {

				int StatusCode = singleton.getHTTPResponseStatusCode();
				JSONObject jObject;
				try {
					jObject = new JSONObject(result);
					String Status = jObject.getString("Status");
					if (StatusCode == 200 || StatusCode == 0) {

						String Eid = jObject.getString("LID");
						Log.d("result",
								"--->"
										+ Eid
										+ " "
										+ singleton
												.getSelectedEntriesIdentityoffline()
										+ " " + Eid);
						if (Eid != null && !Eid.isEmpty()) {
							long entridata = dbadapter
									.updateEntriesEid(
											singleton
													.getSelectedEntriesIdentityoffline(),
											Eid);
							Log.d("entri update", "--->" + entridata);
						}
					}
					new EntiesLabourData().execute();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}

	public class EntriesEquietmentData extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

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
					entries1 = dbadapter.getAllOfflineEntriesRecords("E");
					Log.d("entries length", "--->" + entries1.size());
					if (entries1.size() == 0) {
						new EntriesMaterialData().execute();
					} else {
						for (EntriesNetworkDB val : entries1) {
							singleton.setSelectedEntriesIdentityoffline(val
									.getEIdenty());
							name = val.getEname();
							trade = val.getTRD_C();
							clasification = val.getClassesI();
							entridate = val.getEdate();
							EPid = val.getPID();
							Etid = val.getTid();
							EAid = val.getAid();
							hours = val.getHR_QTY();

							JSONObject jsonAddEquipmentReqJSON = new JSONObject();
							jsonAddEquipmentReqJSON.put("AccountID",
									singleton.getAccountId());
							jsonAddEquipmentReqJSON.put("SubscriberID",
									singleton.getSubscriberId());
							jsonAddEquipmentReqJSON.put("ProjectID", EPid);
							jsonAddEquipmentReqJSON.put("ActivityId", EAid);
							jsonAddEquipmentReqJSON
									.put("ProjectDay", entridate);
							jsonAddEquipmentReqJSON.put("Name", name);
							jsonAddEquipmentReqJSON.put("Owner", trade);
							jsonAddEquipmentReqJSON
									.put("Status", clasification);
							jsonAddEquipmentReqJSON.put("Quantity", hours);
							// jsonaddPersionnel.put("Notes",
							// singleton.getSelectedEquipmentDescription());
							jsonAddEquipmentReqJSON.put("TaskId", Etid);
							jsonAddEquipmentReqJSON.put("UserId",
									singleton.getUserId());
							Log.d("data", "--->" + jsonAddEquipmentReqJSON);
							return jsonDataPost
									.addEquipmentEntry(jsonAddEquipmentReqJSON);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;

		}

		protected void onPostExecute(final String result) {

			Log.d("result", "--->" + result);
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;

			} else if (result != null) {

				int StatusCode = singleton.getHTTPResponseStatusCode();
				JSONObject jObject;
				try {
					jObject = new JSONObject(result);
					String Status = jObject.getString("Status");
					if (StatusCode == 200 || StatusCode == 0) {

						String Eid = jObject.getString("EID");
						Log.d("result",
								"--->"
										+ Eid
										+ " "
										+ singleton
												.getSelectedEntriesIdentityoffline()
										+ " " + Eid);
						if (!Eid.equals(null)) {
							long entridata = dbadapter
									.updateEntriesEid(
											singleton
													.getSelectedEntriesIdentityoffline(),
											Eid);
							Log.d("entri update", "--->" + entridata);
						}
					}
					new EntriesEquietmentData().execute();

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}

	public class EntriesMaterialData extends AsyncTask<Void, Void, String> {

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
					entries1 = dbadapter.getAllOfflineEntriesRecords("M");
					Log.d("entries length", "--->" + entries1.size());
					if (entries1.size() == 0) {
						Log.d("over", "--->");
					} else {

						for (EntriesNetworkDB val : entries1) {
							singleton.setSelectedEntriesIdentityoffline(val
									.getEIdenty());
							name = val.getEname();
							trade = val.getTRD_C();
							clasification = val.getClassesI();
							entridate = val.getEdate();
							EPid = val.getPID();
							Etid = val.getTid();
							EAid = val.getAid();
							hours = val.getHR_QTY();

							JSONObject jsonAddMaterialJSON = new JSONObject();
							jsonAddMaterialJSON.put("AccountID",
									singleton.getAccountId());
							jsonAddMaterialJSON.put("SubscriberID",
									singleton.getSubscriberId());
							jsonAddMaterialJSON.put("ProjectID", EPid);
							jsonAddMaterialJSON.put("ActivityId", EAid);
							jsonAddMaterialJSON.put("ProjectDay", entridate);
							jsonAddMaterialJSON.put("Name", name);
							jsonAddMaterialJSON.put("Company", trade);
							jsonAddMaterialJSON.put("Status", clasification);
							jsonAddMaterialJSON.put("Quantity", hours);
							// jsonaddPersionnel.put("Notes",
							// singleton.getSelectedMaterialDescription());
							jsonAddMaterialJSON.put("TaskId", Etid);
							jsonAddMaterialJSON.put("UserId",
									singleton.getUserId());
							return jsonDataPost
									.addMaterialEntry(jsonAddMaterialJSON);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String result) {

			Log.d("result", "--->" + result);
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;

			} else if (result != null) {

				int StatusCode = singleton.getHTTPResponseStatusCode();
				JSONObject jObject;
				try {
					jObject = new JSONObject(result);
					String Status = jObject.getString("Status");
					if (StatusCode == 200 || StatusCode == 0) {

						String Eid = jObject.getString("MID");
						Log.d("result",
								"--->"
										+ Eid
										+ " "
										+ singleton
												.getSelectedEntriesIdentityoffline()
										+ " " + Eid);
						if (!Eid.equals(null)) {
							long entridata = dbadapter
									.updateEntriesEid(
											singleton
													.getSelectedEntriesIdentityoffline(),
											Eid);
							Log.d("entri update", "--->" + entridata);
						}
					}
					new EntriesMaterialData().execute();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

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

					List<UpdateEntriesProjo> uEntries1 = dbadapter
							.getAllEntriesUpdateRecords("U", "L");
					Log.d("no labour update entries", "--->" + uEntries1.size());
					if (uEntries1.size() == 0) {
						Log.d("no labour update entries", "--->");
						new UpdateEntriesEquipment().execute();
					} else {
						for (UpdateEntriesProjo val1 : uEntries1) {

							Log.d("labour update", "--->" + val1.getPID() + " "
									+ val1.getAid() + " " + val1.getEId() + " "
									+ val1.getEname() + " " + val1.getTRD_C()
									+ " " + val1.getClassesI() + " ");
							singleton.setCurrentSelectedEntryID(val1.getEId());
							JSONObject jsonAddLabor = new JSONObject();
							jsonAddLabor.put("AccountID",
									singleton.getAccountId());
							jsonAddLabor.put("SubscriberID",
									singleton.getSubscriberId());
							jsonAddLabor.put("ProjectID", val1.getPID());
							jsonAddLabor.put("ActivityId", val1.getAid());
							jsonAddLabor.put("ID", val1.getEId());
							jsonAddLabor.put("Name", val1.getEname());
							jsonAddLabor.put("Trade", val1.getTRD_C());
							jsonAddLabor.put("Classification",
									val1.getClassesI());
							jsonAddLabor.put("Hours", val1.getHR_QTY());
							// jsonAddLabor.put("Notes",
							// singleton.getSelectedLaborDescription());
							// jsonAddLabor.put("UserId",
							// singleton.getUserId());
							Log.d("labor sent id :", "--->" + jsonAddLabor);
							return jsonDataPost.updateLaborEntry(jsonAddLabor);

						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String result) {

			Log.d("result...", "--->" + result);
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;

			} else if (result != null) {

				int StatusCode = singleton.getHTTPResponseStatusCode();
				Log.d("statuscode", "-->" + StatusCode);
				JSONObject jObject;
				try {
					jObject = new JSONObject(result);
					String Status = jObject.getString("Status");
					if (StatusCode == 200 || StatusCode == 0) {

						Log.d("result", "--->"

						+ singleton.getCurrentSelectedEntryID());

						long entridata = dbadapter
								.updateEntriesAction1(singleton
										.getCurrentSelectedEntryID());
						Log.d("entri update", "--->" + entridata);

					}
					UpdateEntries updateLabor = new UpdateEntries();
					updateLabor.execute();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

	private class UpdateEntriesEquipment extends AsyncTask<Void, Void, String> {

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
					List<UpdateEntriesProjo> uEntries1 = dbadapter
							.getAllEntriesUpdateRecords("U", "E");
					Log.d("no labour update entries", "--->" + uEntries1.size());
					if (uEntries1.size() == 0) {
						Log.d("goto material", "--->");
						new UpdateEntriesMaterial().execute();
					} else {
						for (UpdateEntriesProjo val1 : uEntries1) {
							singleton.setCurrentSelectedEntryID(val1.getEId());

							Log.d("Equipment update",
									"--->" + val1.getPID() + " "
											+ val1.getAid() + " "
											+ val1.getEId() + " "
											+ val1.getEname() + " "
											+ val1.getTRD_C() + " "
											+ val1.getClassesI() + " ");
							JSONObject jsonaddPersionnel = new JSONObject();
							jsonaddPersionnel.put("AccountID",
									singleton.getAccountId());
							jsonaddPersionnel.put("SubscriberID",
									singleton.getSubscriberId());
							jsonaddPersionnel.put("ProjectID", val1.getPID());
							jsonaddPersionnel.put("ActivityId", val1.getAid());
							jsonaddPersionnel.put("Name", val1.getEname());
							jsonaddPersionnel.put("Owner", val1.getTRD_C());
							jsonaddPersionnel.put("Status", val1.getClassesI());
							jsonaddPersionnel.put("Quantity", val1.getHR_QTY());
							// jsonaddPersionnel.put("Notes",
							// singleton.getSelectedEquipmentDescription());
							jsonaddPersionnel.put("ID", val1.getEId());
							// jsonaddPersionnel.put("UserId",
							// singleton.getUserId());
							System.out.println("equipment id.............."
									+ singleton.getCurrentSelectedEntryID());
							return jsonDataPost
									.updateEquipmentEntry(jsonaddPersionnel);

						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String result) {

			Log.d("result...", "--->" + result);
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;

			} else if (result != null) {

				int StatusCode = singleton.getHTTPResponseStatusCode();
				Log.d("statuscode", "-->" + StatusCode);
				JSONObject jObject;
				try {
					jObject = new JSONObject(result);
					String Status = jObject.getString("Status");
					if (StatusCode == 200 || StatusCode == 0) {

						Log.d("result", "--->"

						+ singleton.getCurrentSelectedEntryID());

						long entridata = dbadapter
								.updateEntriesAction1(singleton
										.getCurrentSelectedEntryID());
						Log.d("entri update", "--->" + entridata);

					}

					new UpdateEntriesEquipment().execute();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

	private class UpdateEntriesMaterial extends AsyncTask<Void, Void, String> {

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
					List<UpdateEntriesProjo> uEntries1 = dbadapter
							.getAllEntriesUpdateRecords("U", "M");
					if (uEntries1.size() == 0) {
						Log.d("goto Activities", "--->");
						ReadActivitydata();
					} else {
						for (UpdateEntriesProjo val1 : uEntries1) {

							singleton.setCurrentSelectedEntryID(val1.getEId());
							Log.d("labour update", "--->" + val1.getPID() + " "
									+ val1.getAid() + " " + val1.getEId() + " "
									+ val1.getEname() + " " + val1.getTRD_C()
									+ " " + val1.getClassesI() + " ");
							JSONObject jsonAddMaterialJSON = new JSONObject();
							jsonAddMaterialJSON.put("AccountID",
									singleton.getAccountId());
							jsonAddMaterialJSON.put("SubscriberID",
									singleton.getSubscriberId());
							jsonAddMaterialJSON.put("ProjectID", val1.getPID());
							jsonAddMaterialJSON
									.put("ActivityId", val1.getAid());
							jsonAddMaterialJSON.put("Name", val1.getEname());
							jsonAddMaterialJSON.put("Company", val1.getTRD_C());
							jsonAddMaterialJSON.put("Status",
									val1.getClassesI());
							jsonAddMaterialJSON.put("Quantity",
									val1.getHR_QTY());
							// jsonaddPersionnel.put("Notes",
							// singleton.getSelectedMaterialDescription());
							// jsonaddPersionnel.put("ProjectDay",
							// singleton.getCurrentSelectedDate());
							jsonAddMaterialJSON.put("ID", val1.getEId());
							// jsonaddPersionnel.put("UserId",
							// singleton.getUserId());
							System.out.println("equipment id.............."
									+ singleton.getCurrentSelectedEntryID());

							return jsonDataPost
									.updateMaterialEntry(jsonAddMaterialJSON);

						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String result) {

			Log.d("result...", "--->" + result);
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;

			} else if (result != null) {

				int StatusCode = singleton.getHTTPResponseStatusCode();
				Log.d("statuscode", "-->" + StatusCode);
				JSONObject jObject;
				try {
					jObject = new JSONObject(result);
					String Status = jObject.getString("Status");
					if (StatusCode == 200 || StatusCode == 0) {

						Log.d("result", "--->"

						+ singleton.getCurrentSelectedEntryID());

						long entridata = dbadapter
								.updateEntriesAction1(singleton
										.getCurrentSelectedEntryID());
						Log.d("entri update", "--->" + entridata);

					}

					new UpdateEntriesMaterial().execute();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

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

					delete2 = dbadapter.getAllEntriesUpdateRecords("D");
					if (delete2.size() == 0) {
						UpdateEntries updateLabor = new UpdateEntries();
						updateLabor.execute();
					} else {
						for (UpdateEntriesProjo val1 : delete2) {
							singleton.setCurrentSelectedEntryID(val1.getEId());
							Log.d("delete",
									"--->" + val1.getEId() + " "
											+ val1.getEname());

							JSONObject jsonLabor = new JSONObject();
							jsonLabor.put("Id", val1.getEId());
							return jsonDataPost.deleteLaborEntry(jsonLabor);

						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String result) {

			Log.d("result...", "--->" + result);
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;

			} else if (result != null) {

				int StatusCode = singleton.getHTTPResponseStatusCode();
				Log.d("statuscode", "-->" + StatusCode);
				JSONObject jObject;
				try {
					jObject = new JSONObject(result);
					String Status = jObject.getString("Status");
					if (StatusCode == 200 || StatusCode == 0) {

						Log.d("result", "--->"

						+ singleton.getCurrentSelectedEntryID());

						long entridata = dbadapter
								.deleteEntryByIDNetwork(singleton
										.getCurrentSelectedEntryID() + "");
						Log.d("entri update", "--->" + entridata);

					}

					new DeleteLaborTask().execute();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

}
