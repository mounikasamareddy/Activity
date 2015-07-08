package com.notevault.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.notevault.activities.ActivitiesListActivity.SwipeListAdapter;
import com.notevault.adapter.EntriesAdapter;
import com.notevault.adapter.TaskAdapter;
import com.notevault.arraylistsupportclasses.EntitiAlignDate;
import com.notevault.arraylistsupportclasses.EntityAlign;
import com.notevault.arraylistsupportclasses.EntityDB;
import com.notevault.arraylistsupportclasses.EntityData;
import com.notevault.arraylistsupportclasses.ProjectData;
import com.notevault.arraylistsupportclasses.TaskData;
import com.notevault.arraylistsupportclasses.TasksDB;
import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;
import com.notevault.support.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class EntriesListByDateActivity extends Activity {

	Singleton singleton;
	DBAdapter dbAdapter;
	ListView entriesListView,enteredlist;
	ServerUtilities jsonDataPost = new ServerUtilities();
	public ArrayList<String> dateList = new ArrayList<String>();
	public Set<String> dateSorted = new HashSet<String>();
	public static ArrayList<String> collectiveConcatenatedEntryList = new ArrayList<String>();
	public ArrayList<String> allEntriesID = new ArrayList<String>();
	public ArrayList<String> sortedListByDate = new ArrayList<String>();
	String values[];
	public String glue = "-~-";
	LinearLayout hintMessage;
	private EntriesAdapter mAdapter1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("On create called.");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.entered_activity);
		hintMessage = (LinearLayout) findViewById(R.id.hintMessage_layout);
		enteredlist=(ListView)findViewById(R.id.enteredlist2);
		singleton = Singleton.getInstance();
		collectiveConcatenatedEntryList.clear();
	
		dbAdapter = DBAdapter.get_dbAdapter(this);
		if (singleton.isOnline()) {
			
			getEntries();
			mAdapter1 = new EntriesAdapter(EntriesListByDateActivity.this);
			enteredlist.setAdapter(mAdapter1);
		} else {

			Log.d("entered offline", "---->");
			readEntriesFromDB();

		}

	}

	class EntriesListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (singleton.isOnline()) {
				if (values == null) {
					hintMessage.setVisibility(View.VISIBLE);
					return 0;
				} else if (values.length > 0) {
					hintMessage.setVisibility(LinearLayout.GONE);
				}
				return values.length;
			} else {
				return Utilities.edata.size();
			}
		}

		@Override
		public Object getItem(int position) {
			if (singleton.isOnline()) {
				return position;
			} else {
				return Utilities.adata.get(position);
			}

		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			LayoutInflater li = getLayoutInflater();
			convertView = li.inflate(R.layout.customlist2, null);
			TextView tv = (TextView) convertView.findViewById(R.id.textView1);
			TextView tv1 = (TextView) convertView.findViewById(R.id.textView2);
			TextView tv2 = (TextView) convertView.findViewById(R.id.textView3);
			TextView tv3 = (TextView) convertView.findViewById(R.id.textView4);

			TextView roundTv = (TextView) convertView.findViewById(R.id.tv);

			if (singleton.isOnline()) {

				if (values.length == 0) {
					Toast.makeText(getApplicationContext(),
							"No entries found.", Toast.LENGTH_LONG).show();
				} else {

					String entry = values[position];

					/*
					 * if(entry.endsWith(glue)) entry = entry + "null";
					 */
					final String val[] = entry.split(glue);
					/*
					 * if(val[6].equals("null")) val[6] = "";
					 */

					roundTv.setText(val[0]);
					tv.setText(val[1]);
					tv1.setText(val[2]);
					tv2.setText(val[3]);
					if (!val[4].equals("null") && val[4] != null)
						val[4] = Singleton.prettyFormat(val[4]);
					tv3.setText(val[4]);

					// ty, n, t, c, h, i, d -- labor
					// ty, n, c, s, q, i, d -- material/equipment
					if (val[0].equals("L")) {
						roundTv.setBackgroundResource(R.drawable.circleyellow);
					} else if (val[0].equals("E")) {
						roundTv.setBackgroundResource(R.drawable.circleblack);
					} else if (val[0].equals("M")) {
						roundTv.setBackgroundResource(R.drawable.circleblue);
					}

					convertView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							// singleton.setOfflineEntry(false);
							if (val[5].startsWith("OF")) {
								singleton.setOfflineEntry(true);
								val[5] = val[5].substring(2);
							}
							singleton.setCurrentSelectedEntryID(Integer
									.parseInt(val[5]));
							singleton.setNewEntryFlag(false);

							if (val[0].equals("L")) {
								singleton.setSelectedLaborName(val[1]);
								singleton.setSelectedLaborTrade(val[2]);
								singleton
										.setSelectedLaborClassification(val[3]);
								singleton.setSelectedLaborHours(val[4]);
								// singleton.setSelectedLaborDescription(val[6]);
								Intent intent = new Intent(
										EntriesListByDateActivity.this,
										AddLabor.class);
								startActivity(intent);
							} else if (val[0].equals("E")) {
								singleton.setSelectedEquipmentName(val[1]);
								singleton.setSelectedEquipmentCompany(val[2]);
								singleton.setSelectedEquipmentStatus(val[3]);
								singleton.setSelectedEquipmentQty(val[4]);
								// singleton.setSelectedEquipmentDescription(val[6]);
								Intent intent = new Intent(
										EntriesListByDateActivity.this,
										AddEquipment.class);
								startActivity(intent);
							} else if (val[0].equals("M")) {
								singleton.setSelectedMaterialName(val[1]);
								singleton.setSelectedMaterialCompany(val[2]);
								singleton.setSelectedMaterialStatus(val[3]);
								singleton.setSelectedMaterialQty(val[4]);
								// singleton.setSelectedMaterialDescription(val[6]);
								Intent intent = new Intent(
										EntriesListByDateActivity.this,
										AddMaterial.class);
								startActivity(intent);
							}
						}
					});
				}
			} else {

				Log.d("offline", "--->");
				roundTv.setText(Utilities.eAligndate.get(position).getTYPE());
				tv.setText(Utilities.eAligndate.get(position).getNAME());
				tv1.setText(Utilities.eAligndate.get(position).getTRD_COMP());
				tv2.setText(Utilities.eAligndate.get(position).getCLASSI_STAT());
				tv3.setText(Utilities.eAligndate.get(position).getHR_QTY() + "");
				if (Utilities.eAligndate.get(position).getTYPE().equals("L")) {
					roundTv.setBackgroundResource(R.drawable.circleyellow);
				} else if (Utilities.eAligndate.get(position).getTYPE()
						.equals("E")) {
					roundTv.setBackgroundResource(R.drawable.circleblack);
				} else if (Utilities.eAligndate.get(position).getTYPE()
						.equals("M")) {
					roundTv.setBackgroundResource(R.drawable.circleblue);
				}
				convertView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						singleton
								.setCurrentSelectedEntryID(Utilities.eAligndate
										.get(position).getID());
						singleton.setNewEntryFlag(false);
						singleton
								.setSelectedEntityIdentity(Utilities.eAligndate
										.get(position).getEIdentity());

						if (Utilities.eAligndate.get(position).getTYPE()
								.equals("L")) {
							singleton.setSelectedLaborName(Utilities.eAligndate
									.get(position).getNAME());
							singleton
									.setSelectedLaborTrade(Utilities.eAligndate
											.get(position).getTRD_COMP());
							singleton
									.setSelectedLaborClassification(Utilities.eAligndate
											.get(position).getCLASSI_STAT());

							singleton
									.setSelectedLaborHours(Utilities.eAligndate
											.get(position).getHR_QTY() + "");
							// singleton.setSelectedLaborDescription(val[6]);
							Intent intent = new Intent(
									EntriesListByDateActivity.this,
									AddLabor.class);
							startActivity(intent);
						} else if (Utilities.eAligndate.get(position).getTYPE()
								.equals("E")) {
							singleton
									.setSelectedEquipmentName(Utilities.eAligndate
											.get(position).getNAME());
							singleton
									.setSelectedEquipmentCompany(Utilities.eAligndate
											.get(position).getTRD_COMP());
							singleton
									.setSelectedEquipmentStatus(Utilities.eAligndate
											.get(position).getCLASSI_STAT());
							singleton
									.setSelectedEquipmentQty(Utilities.eAligndate
											.get(position).getHR_QTY() + "");
							// singleton.setSelectedEquipmentDescription(val[6]);
							Intent intent = new Intent(
									EntriesListByDateActivity.this,
									AddEquipment.class);
							startActivity(intent);
						} else if (Utilities.eAligndate.get(position).getTYPE()
								.equals("M")) {
							singleton
									.setSelectedMaterialName(Utilities.eAligndate
											.get(position).getNAME());
							singleton
									.setSelectedMaterialCompany(Utilities.eAligndate
											.get(position).getTRD_COMP());
							singleton
									.setSelectedMaterialStatus(Utilities.eAligndate
											.get(position).getCLASSI_STAT());
							singleton
									.setSelectedMaterialQty(Utilities.eAligndate
											.get(position).getHR_QTY() + "");
							// singleton.setSelectedMaterialDescription(val[6]);
							Intent intent = new Intent(
									EntriesListByDateActivity.this,
									AddMaterial.class);
							startActivity(intent);
						}
					}
				});
			}
			return convertView;
		}
	}

	public void getEntries() {
		collectiveConcatenatedEntryList.clear();
		allEntriesID.clear();
		GetLaborEntries laborData = new GetLaborEntries();
		if (singleton.isOnline()) {
			laborData.execute();
		}
	}

	/*
	 * @Override public void onBackPressed() { singleton.setReloadPage(true);
	 * super.onBackPressed(); }
	 */

	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("Entries By Date On resume called.");
		Log.d(" enteredonresume", "--->" + singleton.isReloadPage());
		singleton.setReloadPage(true);
		if (singleton.isOnline()) {
			if (singleton.isReloadPage()) {
				System.out.println("Reloading the page.");

				EntriesListByTypeActivity.reload = 1;
				singleton.setReloadPage(false);
				this.onCreate(null);
			}
		} else {

			if (singleton.isReloadPage()) {
				System.out.println("dffdgfOffline");
				readEntriesFromDB();

				singleton.setReloadPage(false);
			}

		}
	}

	private class GetLaborEntries extends AsyncTask<Void, Void, String> {

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
				readEntriesFromDB();
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(final String laborEntriesResponseJSONObj) {
			// ArrayList<String> lid = new ArrayList<String>();
			System.out
					.println("labor response: " + laborEntriesResponseJSONObj);
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
						collectiveConcatenatedEntryList.clear();
						if (jsonObj.getInt("Status") == 0
								|| jsonObj.getInt("Status") == 200) {
							System.out
									.println("*************  If Condition ******************");
							String entriesString = jsonObj
									.getString("Lentries");
							JSONArray jsonArray = new JSONArray(entriesString);
							if (jsonArray.length() > 0) {
								// Populating data into lists.
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject e = jsonArray.getJSONObject(i);
									String type = String.valueOf(e.getString(
											"Type").charAt(0));
									String name = e.getString("Nm").replace(
											"\\", "");
									String trade = e.getString("T").replace(
											"\\", "");
									String classification = e.getString("Cl")
											.replace("\\", "");
									double hour = e.getDouble("H");
									String id = String.valueOf(e.getInt("I"));
									String dateCreated = e.getString("D");
									// String desc = e.getString("N");
									// lid.add(id);
									dateList.add(dateCreated);
									// collectiveConcatenatedEntryList.add(type
									// + glue + name + glue + trade + glue +
									// classification + glue + hour + glue + id
									// + glue + desc);
//									dateCreated=dateCreated.replace("-","");
//									dateCreated=dateCreated.replace(" ", "");
//									dateCreated=dateCreated.replace(":", "");
									collectiveConcatenatedEntryList.add(type
											+ glue + name + glue + trade + glue
											+ classification + glue + hour
											+ glue + id + glue + dateCreated);
									
									
									Log.d("dateCreated","--->"+dateCreated);
								}
//								for(int i=0;i<collectiveConcatenatedEntryList.size();i++)
//								{
//									Log.d("collectiveConcatenatedEntryList","--->"+collectiveConcatenatedEntryList.get(i));
//								}
								System.out
										.println("Debugging SortedListByDate : ..... "
												+ collectiveConcatenatedEntryList);
								System.out
										.println("Debugging dateList : ..... "
												+ dateList);
								// allEntriesID.addAll(lid);

								entriesListView = (ListView) findViewById(R.id.list);
								EntriesListAdapter entriesListAdapter = new EntriesListAdapter();
								entriesListView.setAdapter(entriesListAdapter);
								entriesListAdapter.notifyDataSetChanged();
								entriesListAdapter.notifyDataSetInvalidated();
							} else {
								System.out.println("No labor entries found.");
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
								// Populating data into lists.
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject e = jsonArray.getJSONObject(i);
									String name = e.getString("Nm");
									String company = e.getString("Com");
									String status = e.getString("S");
									String type = String.valueOf(e.getString(
											"Type").charAt(0));
									String id = String.valueOf(e.getInt("I"));
									String dateCreated = e.getString("D");
									double qty = e.getDouble("Q");
									// String desc = e.getString("N");
									// eid.add(id);
									dateList.add(dateCreated);
								
									// collectiveConcatenatedEntryList.add(type
									// + glue + name + glue + company + glue +
									// status + glue + qty + glue + id + glue +
									// desc);
									collectiveConcatenatedEntryList.add(type
											+ glue + name + glue + company
											+ glue + status + glue + qty + glue
											+ id + glue + dateCreated);
									System.out.println("concatenated List: "
											+ collectiveConcatenatedEntryList);
								}
								// allEntriesID.addAll(eid);

								entriesListView = (ListView) findViewById(R.id.list);
								EntriesListAdapter entriesListAdapter = new EntriesListAdapter();
								entriesListView.setAdapter(entriesListAdapter);
								entriesListAdapter.notifyDataSetChanged();
								entriesListAdapter.notifyDataSetInvalidated();
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
								// Populating data into lists.
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject e = jsonArray.getJSONObject(i);
									String name = e.getString("Nm");
									String company = e.getString("Com");
									String status = e.getString("S");
									String type = String.valueOf(e.getString(
											"Type").charAt(0));
									String id = String.valueOf(e.getInt("I"));
									double qty = e.getDouble("Q");
									String dateCreated = e.getString("D");
									// String desc = e.getString("N");
									// mid.add(id);
									dateList.add(dateCreated);
									// collectiveConcatenatedEntryList.add(type
									// + glue + name + glue + company + glue +
									// status + glue + qty + glue + id + glue +
									// desc);
									
									collectiveConcatenatedEntryList.add(type
											+ glue + name + glue + company
											+ glue + status + glue + qty + glue
											+ id + glue + dateCreated);
									for(int j=0;j<collectiveConcatenatedEntryList.size();j++)
									{
										Log.d("collectives","--->"+collectiveConcatenatedEntryList.get(j));
									}
								}
								// allEntriesID.addAll(mid);

							} else {
								System.out
										.println("No material entries found.");
							}
						}

						int delrecords = dbAdapter.deleteEntries();
						Log.d("deleted", "--->" + delrecords);

						if (collectiveConcatenatedEntryList.size() > 0) {
							writeEntriesToDB();
						}
						processDataAndSetAdapter();

					} catch (JSONException e) {
						e.printStackTrace();
					}

				} else {
					System.out
							.println("An error occurred! Could not fetch material entries.");
				}
			}
		}

		private void writeEntriesToDB() {
			long insertResponse = 0;
			for (String value : collectiveConcatenatedEntryList) {
				if (value.endsWith(glue))
					value = value + "null";
				String[] entry = value.split(glue);
				// if (entry[6].equals("null"))
				// entry[6] = "";
				// insertResponse =
				// dbAdapter.insertEntry(entry[1],entry[2],entry[3],
				// Double.parseDouble(entry[4]),entry[6],entry[0],"N",entry[5]);
				insertResponse = dbAdapter.insertEntry(entry[1], entry[2],
						entry[3], entry[4], entry[0], "N", entry[5]);
				Log.d("insert", "--->" + insertResponse);
				System.out.println(value);
			}
			System.out.println("Entry insertion response: " + insertResponse);

		}
	}

	public void processDataAndSetAdapter() {

		// ArrayList<String> sortedList = new ArrayList<String>();
		if (!singleton.isOnline()) {
			allEntriesID.clear();
			for (String entry : collectiveConcatenatedEntryList) {
				String[] params = entry.split(glue);
				allEntriesID.add(params[5]);
			}
		}
		// System.out.println("Collective Concatenated Entry List: " +
		// collectiveConcatenatedEntryList);
		sortedListByDate.clear();
		dateSorted.clear();
		dateSorted.addAll(dateList);
		for (String date : dateSorted) {
			// System.out.println("date : -----"+date);
			for (String entryListDate : collectiveConcatenatedEntryList) {
				String currentListDate = entryListDate.substring(
						entryListDate.lastIndexOf(glue) + 3,
						entryListDate.length());
				// System.out.println("currentListDate : "+currentListDate);
				if (date.equals(currentListDate)) {
					// System.out.println("List to add : "+entryListDate);
					sortedListByDate.add(entryListDate);
				}
			}
		}
		/*
		 * Collections.sort(allEntriesID);
		 * System.out.println("AllEntriesID List: "+ allEntriesID);
		 * System.out.println("Sorted entryID's List: "+ allEntriesID);
		 * //sortedList.clear();
		 * 
		 * int i = 0; while (sortedList.size() <
		 * collectiveConcatenatedEntryList.size() &&
		 * collectiveConcatenatedEntryList.size() != 0) {
		 * 
		 * System.out.println("collectiveConcatenatedEntryList.size(): "+
		 * collectiveConcatenatedEntryList.size());
		 * System.out.println("sortedList.size(): "+sortedList.size());
		 * System.out.println("i = " + i);
		 * 
		 * for (String entry : collectiveConcatenatedEntryList) { String[]
		 * params = entry.split(glue);
		 * if(params[5].equals(allEntriesID.get(i)))//if
		 * (entry.contains(Integer.toString(allEntriesID.get(i))))
		 * sortedList.add(entry); } i++; if(i >
		 * collectiveConcatenatedEntryList.size()) { i = 0; } }
		 */
		System.out.println("Sorted Entries List final statement : "
				+ sortedListByDate);

		values = sortedListByDate.toArray(new String[sortedListByDate.size()]);
		System.out.println("Sorted Entries List2: " + values.length);
		entriesListView = (ListView) findViewById(R.id.list);
		EntriesListAdapter entriesListAdapter = new EntriesListAdapter();
		entriesListView.setAdapter(entriesListAdapter);
		entriesListAdapter.notifyDataSetChanged();
		entriesListAdapter.notifyDataSetInvalidated();
	}

	public void readEntriesFromDB() {
		mAdapter1 = new EntriesAdapter(this);
		int Aid = singleton.getSelectedActivityID();
		int Tid = singleton.getSelectedTaskID();
		Utilities.edata.clear();
		Utilities.eAligndate.clear();
		List<EntityDB> data = null;

		if (Tid == 0 && Aid == 0) {
			Log.d("both", "-->" + singleton.getSelectedTaskIdentityoffline());
			data = dbAdapter.getAllEntityRecords(singleton
					.getSelectedTaskIdentityoffline());
			Log.d("activity id=0",
					"--->" + singleton.getSelectedTaskIdentityoffline() + " "
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
				details.setDate(val.getDate());
				Utilities.edata.add(details);

			}
		}
		dbAdapter.Close();

		for (int i = 0; i < Utilities.edata.size(); i++) {

		
				EntitiAlignDate align = new EntitiAlignDate(Utilities.edata.get(i).getEIDentity(),Utilities.edata.get(i).getID(),Utilities.edata.get(i).getTYPE()
						,Utilities.edata.get(i).getNAME(),Utilities.edata.get(i).getCLASSI_STAT(),Utilities.edata.get(i).getHR_QTY(),Utilities.edata.get(i).getTRD_COMP(),
						Utilities.edata.get(i).getAction(),Integer.parseInt(Utilities.edata.get(i).getDate()));
				align.setEIdentity(Utilities.edata.get(i).getEIDentity());
				align.setID(Utilities.edata.get(i).getID());
				align.setTYPE(Utilities.edata.get(i).getTYPE());
				align.setNAME(Utilities.edata.get(i).getNAME());
				align.setCLASSI_STAT(Utilities.edata.get(i).getCLASSI_STAT());
				align.setHR_QTY(Utilities.edata.get(i).getHR_QTY());
				align.setTRD_COMP(Utilities.edata.get(i).getTRD_COMP());
				align.setAction(Utilities.edata.get(i).getAction());
				align.setDate(Integer.parseInt(Utilities.edata.get(i).getDate()));
				Utilities.eAligndate.add(align);
			

		}
		Collections.sort(Utilities.eAligndate, new EntitiAlignDate.OrderByEDate());
		Log.d("eAligndate arraylength", "---->" + Utilities.eAligndate.size());
		if (Utilities.eAligndate.size() > 0) {
			for (int i = 0; i < Utilities.eAligndate.size(); i++) {
				Log.d("alighdata", "---->"
						+ Utilities.eAligndate.get(i).getDate() + " "
						+ Utilities.eAligndate.get(i).getID() + " "
						+ Utilities.eAligndate.get(i).getTYPE() + " "
						+ Utilities.eAligndate.get(i).getEIdentity() + " "
						+ Utilities.eAligndate.get(i).getNAME() + " "
						+ Utilities.eAligndate.get(i).getTRD_COMP() + " "
						+ Utilities.eAligndate.get(i).getCLASSI_STAT() + " "
						+ Utilities.eAligndate.get(i).getHR_QTY() + " "
						+ Utilities.eAligndate.get(i).getTYPE() + " "
						+ Utilities.eAligndate.get(i).getAction());

			}
		} else {
			if (singleton.getselectedActivityIdentityoffline() != 0) {
				dbAdapter.updateActivity(singleton
						.getselectedActivityIdentityoffline());
			}
		}
		entriesListView = (ListView) findViewById(R.id.list);
		EntriesListAdapter entriesListAdapter = new EntriesListAdapter();
		entriesListView.setAdapter(entriesListAdapter);
		entriesListAdapter.notifyDataSetChanged();
		entriesListAdapter.notifyDataSetInvalidated();
		enteredlist.setAdapter(mAdapter1);
		singleton.setReloadPage(true);

		

	}
}