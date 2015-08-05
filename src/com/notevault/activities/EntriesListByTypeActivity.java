package com.notevault.activities;

import java.security.SecureRandom;
import java.util.ArrayList;
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

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.notevault.adapter.CustomAdapter;
import com.notevault.adapter.EntriesAdapter;
import com.notevault.adapter.EntriesListAdapteroffline;
import com.notevault.arraylistsupportclasses.EntityAlign;
import com.notevault.arraylistsupportclasses.EntityDB;
import com.notevault.arraylistsupportclasses.EntityData;
import com.notevault.arraylistsupportclasses.GroupData;
import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;
import com.notevault.support.Utilities;

public class EntriesListByTypeActivity extends Activity {

	Singleton singleton;
	String values[];
	ArrayList<String> entries = new ArrayList<String>();
	private ProgressDialog mdialog;
	private CustomAdapter mAdapter;
	private EntriesAdapter mAdapter1;
	public static int reload = 0;
	ServerUtilities jsonDataPost = new ServerUtilities();
	public String glue = "-~-";
	ListView ls, ls2;
	DBAdapter dbAdapter;

	public static ArrayList<Integer> MaterialData = new ArrayList<Integer>();
	public static int totalhours, materialsPlace1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.types_activity);

		dbAdapter = DBAdapter.get_dbAdapter(this);
		singleton = Singleton.getInstance();
		ls = (ListView) findViewById(R.id.groupedlist);
		ls2 = (ListView) findViewById(R.id.groupedlist2);

		if (singleton.isOnline()) {
			
			Utilities.groupdata.clear();
			entries.clear();
			new GetLaborEntries().execute();

		} else {

			Log.d("offline", "---->");

			readEntriesFromDB();

		}

	}

	protected void onResume() {
		super.onResume();
		entries.clear();
		Log.d("grouped onresume", "-->" + singleton.isReloadPage());
		if (reload == 1) {
			reload = 0;
			singleton.setReloadPage(false);
			this.onCreate(null);
		}

		System.out.println("Entries By Date On resume called.");
		if (singleton.isOnline()) {
			if (singleton.isReloadPage()) {
				System.out.println("Reloading the page.");

				singleton.setReloadPage(false);
				this.onCreate(null);
			}
		} else {
			readEntriesFromDB();
			singleton.setReloadPage(false);
			this.onCreate(null);
		}
	}

	private void setAdapter() {
		Log.d("adapter", "--->");
		MaterialData.clear();
		mAdapter = new CustomAdapter(EntriesListByTypeActivity.this);
		mAdapter1 = new EntriesAdapter(EntriesListByTypeActivity.this);
		Log.d("entries", "-->" + Utilities.groupdata.size());

		totalhours = 0;
		materialsPlace1 = 0;
		for (int i = 0; i < Utilities.groupdata.size(); i++) {
			Log.d("adapter entries", "--->"
					+ Utilities.groupdata.get(i).getType() + " "
					+ Utilities.groupdata.get(i).getName() + " "
					+ Utilities.groupdata.get(i).getTrade() + " "
					+ Utilities.groupdata.get(i).getClassification() + " "
					+ Utilities.groupdata.get(i).getHrs());
			if (Utilities.groupdata.get(i).getType().equals("Labor")) {

				totalhours = (int) (totalhours + Utilities.groupdata.get(i)
						.getHrs());
			}
			if (Utilities.groupdata.get(i).getType().equals("Material")) {
				MaterialData.add((int) Utilities.groupdata.get(i).getHrs());
			}

		}
		Log.d("material qty", "--->" + MaterialData.size());
		for (int i = 0; i < MaterialData.size(); i++) {
			Log.d("material qty", "--->" + MaterialData.get(i));
		}
		mAdapter.notifyDataSetChanged();
		ls.setAdapter(mAdapter);
		ls2.setAdapter(mAdapter1);

		// totalHrs.setText(""+totalhours);
		// materialsPlace.setText(""+materialsPlace1);
		// if(totalhours==0){
		// totalhours=1;
		// }
		// double hoursPerUnit1=materialsPlace1/totalhours;
		// hoursPerUnit.setText(""+new
		// DecimalFormat("##.000").format(hoursPerUnit1));
	}

	public static class ViewHolder {
		TextView text;
	}

	public class GetLaborEntries extends AsyncTask<Void, Void, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mhandler.sendEmptyMessage(0);
		}
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
					JSONObject laborEntriesReqJSONObj = new JSONObject();
					laborEntriesReqJSONObj.put("ActivityId",
							singleton.getSelectedActivityID());
					laborEntriesReqJSONObj.put("ProjectDay",
							singleton.getCurrentSelectedDate());
					laborEntriesReqJSONObj.put("TaskId",
							singleton.getSelectedTaskID());
					laborEntriesReqJSONObj.put("UserId", singleton.getUserId());
					// System.out.println("laborEntriesReqJSONObj: "+laborEntriesReqJSONObj);
					// Labor Entries
					return jsonDataPost.getLaborEntries(laborEntriesReqJSONObj);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				// readEntriesFromDB();
				e.printStackTrace();
			}
			return null;

		}

		protected void onPostExecute(final String laborEntriesResponseJSONObj) {
			// ArrayList<String> lid = new ArrayList<String>();
			Log.d("labor response: ", "--->" + laborEntriesResponseJSONObj);
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;
				Toast.makeText(getApplicationContext(),
						"Sorry! Server could not be reached.",
						Toast.LENGTH_LONG).show();
			} else {
				if (laborEntriesResponseJSONObj != null) {
					try {
						JSONObject jsonObj = new JSONObject(
								laborEntriesResponseJSONObj);
						
						if (jsonObj.getInt("Status") == 0
								|| jsonObj.getInt("Status") == 200) {
							System.out
									.println("*************  If Condition ******************");
							String entriesString = jsonObj
									.getString("Lentries");
							JSONArray jsonArray = new JSONArray(entriesString);
							Log.d("length", "--->" + jsonArray.length());
							if (jsonArray.length() > 0) {

								GroupData data1 = new GroupData();
								String str = "null";
								Log.d("p val", "--->");
								data1.setHeadname("Labor");
								data1.setType(str);
								data1.setName(str);
								data1.setTrade(str);
								data1.setClassification(str);
								data1.setHrs(0);
								data1.setTimeandhalf(0);
								data1.setDoubletime(0);
								data1.setEId(0);
								Utilities.groupdata.add(data1);

								Log.d("json len", "-->" + jsonArray.length());
								// Populating data into lists.
								for (int i = 0; i < jsonArray.length(); i++) {
									GroupData data = new GroupData();
									JSONObject e = jsonArray.getJSONObject(i);
									String type = String.valueOf(e.getString(
											"Type").charAt(0));
									String name = e.getString("Nm").replace(
											"\\", "");
									String trade = e.getString("T").replace(
											"\\", "");
									String classification = e.getString("Cl")
											.replace("\\", "");
									
//									double timeandhours=0;
//									double doubletime=0;
//									if(e.getString("TH").equals(""))
//									{
//										 timeandhours=0;
//										 doubletime=0;
//									}else{
//										 timeandhours = e.getDouble("TH");
//
//										doubletime = e.getDouble("DT");
//									}
									double hour = e.getDouble("H");
									String id = String.valueOf(e.getInt("I"));
									String dateCreated = e.getString("D");

									entries.add(type + glue + name + glue
											+ trade + glue + classification
											+ glue + hour + glue + id + glue
											+ dateCreated 
											);

									Log.d("check", "--->" + type + glue + name
											+ glue + trade + glue
											+ classification + glue + hour
											+ glue + id + glue + dateCreated);
									data.setHeadname("empty");
									data.setType("Labor");
									data.setName(name);
									data.setTrade(trade);
									data.setClassification(classification);
									data.setHrs(hour);
									
									data.setEId(Integer.parseInt(id));
									Utilities.groupdata.add(data);
								}

								// System.out.println("Debugging SortedListByDate : ..... "+
								// entries.size());

								// allEntriesID.addAll(lid);

							} else {
								// System.out.println("No labor entries found.");
							}
						}

						GetEquipmentEntries equipmentEntries = new GetEquipmentEntries();
						equipmentEntries.execute();
					} catch (JSONException e) {
						e.printStackTrace();
					}

				} else {
					System.out
							.println("An error occurred! Could not fetch labor entries.");
				}

			}
		}
	}
	Handler mhandler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				mdialog = ProgressDialog
						.show(EntriesListByTypeActivity.this, "", "Loading...!");
				removeMessages(0);
				break;
			
			case 1:
				if (mdialog.isShowing()) 
				{
					mdialog.cancel();
				}
				removeMessages(2);
				break;

			}
		}

	};
	private class GetEquipmentEntries extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... voids) {
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
					JSONObject equipmentEntriesReqJSONObj = new JSONObject();
					equipmentEntriesReqJSONObj.put("ActivityId",
							singleton.getSelectedActivityID());
					equipmentEntriesReqJSONObj.put("ProjectDay",
							singleton.getCurrentSelectedDate());
					equipmentEntriesReqJSONObj.put("AccountId",
							singleton.getAccountId());
					equipmentEntriesReqJSONObj.put("TaskId",
							singleton.getSelectedTaskID());
					equipmentEntriesReqJSONObj.put("UserId",
							singleton.getUserId());
					// Equipment Entries
					System.out.println("Get Equipment Entries called.");
					return jsonDataPost
							.getEquipmentEntries(equipmentEntriesReqJSONObj);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(
				final String equipmentEntriesResponseJSONObj) {
			// ArrayList<String> eid = new ArrayList<String>();
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;
				Toast.makeText(getApplicationContext(),
						"Sorry! Server could not be reached.",
						Toast.LENGTH_LONG).show();
			} else {
				if (equipmentEntriesResponseJSONObj != null) {
					try {
						JSONObject jsonObj = new JSONObject(
								equipmentEntriesResponseJSONObj);
						String statusMessage = jsonObj.getString("Status");
						if (!statusMessage.equalsIgnoreCase("201")) {
							String entriesString = jsonObj
									.getString("Eentries");
							JSONArray jsonArray = new JSONArray(entriesString);

							if (jsonArray.length() > 0) {

								GroupData data1 = new GroupData();
								String str = "null";
								Log.d("p val", "--->");
								data1.setHeadname("Equipment");
								data1.setType(str);
								data1.setName(str);
								data1.setTrade(str);
								data1.setClassification(str);
								data1.setHrs(0);
								data1.setEId(0);
								data1.setTimeandhalf(0);
								data1.setDoubletime(0);
								Utilities.groupdata.add(data1);
								// Populating data into lists.
								for (int i = 0; i < jsonArray.length(); i++) {
									GroupData data = new GroupData();
									JSONObject e = jsonArray.getJSONObject(i);
									String name = e.getString("Nm").replace(
											"\\", "");
									String company = e.getString("Com")
											.replace("\\", "");
									String status = e.getString("S").replace(
											"\\", "");
									String type = String.valueOf(e.getString(
											"Type").charAt(0));
									String id = String.valueOf(e.getInt("I"));
									String dateCreated = e.getString("D");
									double qty = e.getDouble("Q");
									// String desc = e.getString("N");
									// eid.add(id);

									// collectiveConcatenatedEntryList.add(type
									// + glue + name + glue + company + glue +
									// status + glue + qty + glue + id + glue +
									// desc);
									entries.add(type + glue + name + glue
											+ company + glue + status + glue
											+ qty + glue + id + glue
											+ dateCreated + glue + 0 + glue + 0);

									data.setHeadname("empty");
									data.setType("Equipment");
									data.setName(name);
									data.setTrade(company);
									data.setClassification(status);
									data.setHrs(qty);
									data.setEId(Integer.parseInt(id));
									data.setTimeandhalf(0);
									data.setDoubletime(0);
									Utilities.groupdata.add(data);
								}

								// allEntriesID.addAll(eid);

							} else {
								System.out
										.println("No equipment entries found.");
							}
						}

						GetMaterialEntries getMaterialEntries = new GetMaterialEntries();
						getMaterialEntries.execute();

					} catch (JSONException e) {
						e.printStackTrace();
					}

				} else {
					System.out
							.println("An error occurred! Could not fetch equipment entries.");
				}
			}
		}
	}

	private class GetMaterialEntries extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... voids) {
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
					JSONObject materialEntriesReqJSONObj = new JSONObject();
					materialEntriesReqJSONObj.put("ActivityId",
							singleton.getSelectedActivityID());
					materialEntriesReqJSONObj.put("ProjectDay",
							singleton.getCurrentSelectedDate());
					materialEntriesReqJSONObj.put("AccountId",
							singleton.getAccountId());
					materialEntriesReqJSONObj.put("TaskId",
							singleton.getSelectedTaskID());
					materialEntriesReqJSONObj.put("UserId",
							singleton.getUserId());
					// Material Entries
					return jsonDataPost
							.getMaterialEntries(materialEntriesReqJSONObj);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String materialEntriesResponseJSONObj) {
			// ArrayList<String> mid = new ArrayList<String>();
			if (ServerUtilities.unknownHostException) {
				ServerUtilities.unknownHostException = false;
				Toast.makeText(getApplicationContext(),
						"Sorry! Server could not be reached.",
						Toast.LENGTH_LONG).show();
			} else {
				if (materialEntriesResponseJSONObj != null) {
					try {
						JSONObject jsonObj = new JSONObject(
								materialEntriesResponseJSONObj);
						String statusMessage = jsonObj.getString("Status");
						if (!statusMessage.equalsIgnoreCase("201")) {
							String entriesString = jsonObj
									.getString("Mentries");
							JSONArray jsonArray = new JSONArray(entriesString);

							if (entriesString.length() > 0) {

								GroupData data1 = new GroupData();
								String str = "null";
								Log.d("p val", "--->");
								data1.setHeadname("Material");
								data1.setType(str);
								data1.setName(str);
								data1.setTrade(str);
								data1.setClassification(str);
								data1.setHrs(0);
								data1.setEId(0);
								data1.setTimeandhalf(0);
								data1.setDoubletime(0);
								Utilities.groupdata.add(data1);
								// Populating data into lists.
								for (int i = 0; i < jsonArray.length(); i++) {
									GroupData data = new GroupData();
									JSONObject e = jsonArray.getJSONObject(i);
									String name = e.getString("Nm").replace(
											"\\", "");
									String company = e.getString("Com")
											.replace("\\", "");
									String status = e.getString("S").replace(
											"\\", "");
									String type = String.valueOf(e.getString(
											"Type").charAt(0));
									String id = String.valueOf(e.getInt("I"));
									double qty = e.getDouble("Q");
									String dateCreated = e.getString("D");

									entries.add(type + glue + name + glue
											+ company + glue + status + glue
											+ qty + glue + id + glue
											+ dateCreated + glue + 0 + glue + 0);
									data.setHeadname("empty");
									data.setType("Material");
									data.setName(name);
									data.setTrade(company);
									data.setClassification(status);
									data.setHrs(qty);
									data.setEId(Integer.parseInt(id));
									data1.setTimeandhalf(0);
									data1.setDoubletime(0);
									Utilities.groupdata.add(data);

								}
								for (int i = 0; i < Utilities.groupdata.size(); i++) {
									Log.d("matirial entries", "--->"
											+ Utilities.groupdata.get(i)
													.getHeadname()
											+ " "
											+ Utilities.groupdata.get(i)
													.getType()
											+ " "
											+ Utilities.groupdata.get(i)
													.getName()
											+ " "
											+ Utilities.groupdata.get(i)
													.getTrade()
											+ " "
											+ Utilities.groupdata.get(i)
													.getClassification()
											+ " "
											+ Utilities.groupdata.get(i)
													.getHrs()
											+ " "
											+ Utilities.groupdata.get(i)
													.getEId());

								}

								// allEntriesID.addAll(mid);

							} else {
								System.out
										.println("No material entries found.");
							}
						}

						int delrecords = dbAdapter.deleteEntries();
						Log.d("deleted", "--->" + delrecords);

						if (entries.size() > 0) {
							writeEntriesToDB();
						}
						setAdapter();

					} catch (JSONException e) {
						e.printStackTrace();
					}

				} else {
					System.out
							.println("An error occurred! Could not fetch material entries.");
				}
			}
			mhandler.sendEmptyMessage(1);
		}
		
		private void writeEntriesToDB() {
			long insertResponse = 0;
			for (String value : entries) {
				if (value.endsWith(glue))
					value = value + "null";
				String[] entry = value.split(glue);
				// if (entry[6].equals("null"))
				// entry[6] = "";
				// insertResponse =
				// dbAdapter.insertEntry(entry[1],entry[2],entry[3],
				// Double.parseDouble(entry[4]),entry[6],entry[0],"N",entry[5]);
				// Log.d("@%$#^%^&^(*","--->"+entry[7]+" "+entry[8]);
				
				insertResponse = dbAdapter.insertEntry(entry[1], entry[2],
						entry[3], entry[4], entry[0], "N", entry[5]
						);
				Log.d("insert", "--->" + insertResponse);
				System.out.println(value);
			}
			System.out.println("Entry insertion response: " + insertResponse);

		}
	}

	public void readEntriesFromDB() {

		int Aid = singleton.getSelectedActivityID();
		int Tid = singleton.getSelectedTaskID();
		Utilities.edata.clear();
		Utilities.eAligndata.clear();
		List<EntityDB> data = null;
		MaterialData.clear();
		mAdapter1 = new EntriesAdapter(EntriesListByTypeActivity.this);
		if (Tid == 0 && Aid == 0) {
			Log.d("both", "-->" + singleton.getselectedActivityIdentityoffline());
			data = dbAdapter.getAllEntityRecords(singleton
					.getselectedActivityIdentityoffline());
			Log.d("activity id=0",
					"--->" + singleton.getselectedActivityIdentityoffline() + " "
							+ data.size());
		} else if (Aid == 0) {
			Log.d("activity id=0",
					"--->" + singleton.getselectedActivityIdentityoffline());
			if (singleton.getselectedActivityIdentityoffline() != 0) {
				data = dbAdapter.getAllEntityRecords(singleton
						.getselectedActivityIdentityoffline());
				Log.d("activity id=0",
						"--->" + singleton.getselectedActivityIdentityoffline()
								+ " " + data.size());
			}
		} else {
			data = dbAdapter.getAllEntityRecords(Aid);
			Log.d("activity id not 0", "--->" + Aid + " " + data.size());
		}

		if (data.size() > 0) {
			for (EntityDB val : data) {
				EntityData details = new EntityData();
				details.setEIDentity(val.getEIdentity());
				details.setID(val.getID());
				details.setNAME(val.getNAME());
				details.setTRD_COMP(val.getTRD_COMP());
				details.setCLASSI_STAT(val.getCLASSI_STAT());
				details.setHR_QTY(val.getHR_QTY());
				details.setTYPE(val.getType());
				details.setAction(val.getAction());
				details.setTimeandHalf(val.getTimeandHalf());
				details.setDoubleTime(val.getDoubleTime());
				Utilities.edata.add(details);

			}
		}
		dbAdapter.Close();
		if (Utilities.edata.size() > 0) {

		}
		int p = 0;
		for (int i = 0; i < Utilities.edata.size(); i++) {
			if (Utilities.edata.get(i).getTYPE().equals("L")) {
				if (p == 0) {
					EntityAlign align1 = new EntityAlign();
					String str = "null";
					align1.setHeader("Labor");
					align1.setEIdentity(0);
					align1.setID(0);
					align1.setTYPE(str);
					align1.setNAME(str);
					align1.setCLASSI_STAT(str);
					align1.setHR_QTY(str);
					align1.setTRD_COMP(str);
					align1.setAction(str);
					align1.setTimeandHalf(str);
					align1.setDoubleTime(str);
					Utilities.eAligndata.add(align1);

					p++;
				}
			}
			if (Utilities.edata.get(i).getTYPE().equals("L")) {
				EntityAlign align = new EntityAlign();
				align.setHeader("Empty");
				align.setEIdentity(Utilities.edata.get(i).getEIDentity());
				align.setID(Utilities.edata.get(i).getID());
				align.setTYPE(Utilities.edata.get(i).getTYPE());
				align.setNAME(Utilities.edata.get(i).getNAME());
				align.setCLASSI_STAT(Utilities.edata.get(i).getCLASSI_STAT());
				align.setHR_QTY(Utilities.edata.get(i).getHR_QTY());
				align.setTRD_COMP(Utilities.edata.get(i).getTRD_COMP());
				align.setAction(Utilities.edata.get(i).getAction());
				align.setTimeandHalf(Utilities.edata.get(i).getTimeandHalf());
				align.setDoubleTime(Utilities.edata.get(i).getDoubleTime());
				Utilities.eAligndata.add(align);
			}

		}
		int q = 0;
		for (int i = 0; i < Utilities.edata.size(); i++) {
			if (q == 0) {
				if (Utilities.edata.get(i).getTYPE().equals("E")) {
					EntityAlign align1 = new EntityAlign();
					String str = "null";
					align1.setHeader("Equipment");
					align1.setEIdentity(0);
					align1.setID(0);
					align1.setTYPE(str);
					align1.setNAME(str);
					align1.setCLASSI_STAT(str);
					align1.setHR_QTY(str);
					align1.setTRD_COMP(str);
					align1.setAction(str);
					align1.setTimeandHalf(str);
					align1.setDoubleTime(str);
					Utilities.eAligndata.add(align1);
					q++;
				}
			}
			if (Utilities.edata.get(i).getTYPE().equals("E")) {
				EntityAlign align = new EntityAlign();
				align.setHeader("Empty");
				align.setEIdentity(Utilities.edata.get(i).getEIDentity());
				align.setID(Utilities.edata.get(i).getID());
				align.setTYPE(Utilities.edata.get(i).getTYPE());
				align.setNAME(Utilities.edata.get(i).getNAME());
				align.setCLASSI_STAT(Utilities.edata.get(i).getCLASSI_STAT());
				align.setHR_QTY(Utilities.edata.get(i).getHR_QTY());
				align.setTRD_COMP(Utilities.edata.get(i).getTRD_COMP());
				align.setAction(Utilities.edata.get(i).getAction());
				align.setTimeandHalf(Utilities.edata.get(i).getTimeandHalf());
				align.setDoubleTime(Utilities.edata.get(i).getDoubleTime());
				Utilities.eAligndata.add(align);
			}

		}

		int r = 0;
		for (int i = 0; i < Utilities.edata.size(); i++) {
			if (r == 0) {
				if (Utilities.edata.get(i).getTYPE().equals("M")) {
					EntityAlign align1 = new EntityAlign();
					String str = "null";
					align1.setHeader("Material");
					align1.setEIdentity(0);
					align1.setID(0);
					align1.setTYPE(str);
					align1.setNAME(str);
					align1.setCLASSI_STAT(str);
					align1.setHR_QTY(str);
					align1.setTRD_COMP(str);
					align1.setAction(str);
					align1.setTimeandHalf(str);
					align1.setDoubleTime(str);
					Utilities.eAligndata.add(align1);
					r++;
				}
			}
			if (Utilities.edata.get(i).getTYPE().equals("M")) {
				EntityAlign align = new EntityAlign();
				align.setHeader("Empty");
				align.setEIdentity(Utilities.edata.get(i).getEIDentity());
				align.setID(Utilities.edata.get(i).getID());
				align.setTYPE(Utilities.edata.get(i).getTYPE());
				align.setNAME(Utilities.edata.get(i).getNAME());
				align.setCLASSI_STAT(Utilities.edata.get(i).getCLASSI_STAT());
				align.setHR_QTY(Utilities.edata.get(i).getHR_QTY());
				align.setTRD_COMP(Utilities.edata.get(i).getTRD_COMP());
				align.setAction(Utilities.edata.get(i).getAction());
				align.setTimeandHalf(Utilities.edata.get(i).getTimeandHalf());
				align.setDoubleTime(Utilities.edata.get(i).getDoubleTime());
				Utilities.eAligndata.add(align);
			}

		}
		Log.d("ealigndata arraylength", "---->" + Utilities.eAligndata.size());
		totalhours = 0;

		if (Utilities.eAligndata.size() > 0) {
			for (int i = 0; i < Utilities.eAligndata.size(); i++) {
				Log.d("alighdata", "---->"
						+ Utilities.eAligndata.get(i).getID() + " "
						+ Utilities.eAligndata.get(i).getTYPE() + " "
						+ Utilities.eAligndata.get(i).getEIdentity() + " "
						+ Utilities.eAligndata.get(i).getNAME() + " "
						+ Utilities.eAligndata.get(i).getTRD_COMP() + " "
						+ Utilities.eAligndata.get(i).getCLASSI_STAT() + " "
						+ Utilities.eAligndata.get(i).getHR_QTY() + " "
						+ Utilities.eAligndata.get(i).getTYPE() + " "
						+ Utilities.eAligndata.get(i).getAction()+ " "
						+Utilities.eAligndata.get(i).getTimeandHalf()+ " "
						+Utilities.eAligndata.get(i).getDoubleTime());

				if (Utilities.eAligndata.get(i).getTYPE().equals("L")) {

					totalhours = (int) (totalhours + Double
							.parseDouble(Utilities.eAligndata.get(i)
									.getHR_QTY()));
				}
				if (Utilities.eAligndata.get(i).getTYPE().equals("M")) {
					Log.d("material qty offline", "--->"
							+ Utilities.eAligndata.get(i).getHR_QTY());
					MaterialData.add((int) Double
							.parseDouble(Utilities.eAligndata.get(i)
									.getHR_QTY()));
				}

			}
		} else {
			if (singleton.getselectedActivityIdentityoffline() != 0) {
//				dbAdapter.updateActivity(singleton
//						.getselectedActivityIdentityoffline());
			}
		}

		EntriesListAdapteroffline entriesListAdapter = new EntriesListAdapteroffline(
				this);
		ls.setAdapter(entriesListAdapter);

		ls2.setAdapter(mAdapter1);

	}
}