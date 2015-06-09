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
import com.notevault.arraylistsupportclasses.EntityAlign;
import com.notevault.arraylistsupportclasses.EntityDB;
import com.notevault.arraylistsupportclasses.EntityData;
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
	ListView entriesListView;
	ServerUtilities jsonDataPost = new ServerUtilities();
	public ArrayList<String> dateList = new ArrayList<String>();
	public Set<String> dateSorted = new HashSet<String>();
	public static ArrayList<String> collectiveConcatenatedEntryList = new ArrayList<String>();
	public ArrayList<String> allEntriesID = new ArrayList<String>();
	public ArrayList<String> sortedListByDate = new ArrayList<String>();
	String values[];
	public String glue = "-~-";
	LinearLayout hintMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		System.out.println("On create called.");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.entered_activity);
		hintMessage = (LinearLayout) findViewById(R.id.hintMessage_layout);

		singleton = Singleton.getInstance();
		collectiveConcatenatedEntryList.clear();
		dbAdapter = DBAdapter.get_dbAdapter(this);
		if (singleton.isOnline()) {
			getEntries();
		} else {

			Log.d("offline", "---->");
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

							singleton.setOfflineEntry(false);
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
				roundTv.setText(Utilities.eAligndata.get(position).getTYPE());
				tv.setText(Utilities.eAligndata.get(position).getNAME());
				tv1.setText(Utilities.eAligndata.get(position).getTRD_COMP());
				tv2.setText(Utilities.eAligndata.get(position).getCLASSI_STAT());
				tv3.setText(Utilities.eAligndata.get(position).getHR_QTY() + "");
				if (Utilities.eAligndata.get(position).getTYPE().equals("L")) {
					roundTv.setBackgroundResource(R.drawable.circleyellow);
				} else if (Utilities.eAligndata.get(position).getTYPE().equals("E")) {
					roundTv.setBackgroundResource(R.drawable.circleblack);
				} else if (Utilities.eAligndata.get(position).getTYPE().equals("M")) {
					roundTv.setBackgroundResource(R.drawable.circleblue);
				}

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
		if (singleton.isReloadPage()) {
			System.out.println("Reloading the page.");

			EntriesListByTypeActivity.reload = 1;
			singleton.setReloadPage(false);
			this.onCreate(null);
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
									collectiveConcatenatedEntryList.add(type
											+ glue + name + glue + trade + glue
											+ classification + glue + hour
											+ glue + id + glue + dateCreated);
								}

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
								}
								// allEntriesID.addAll(mid);

							} else {
								System.out
										.println("No material entries found.");
							}
						}
						// int delResponse = dbAdapter.deleteEntries();
						int delrecords = dbAdapter.deleteEntries();
						Log.d("deleted", "--->" + delrecords);

						if (collectiveConcatenatedEntryList.size() > 0) {
							readEntriesFromDB();
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

		int Aid = singleton.getSelectedActivityID();
		Utilities.edata.clear();
		Utilities.eAligndata.clear();

		
		List<EntityDB> data = dbAdapter.getAllEntityRecords(Aid);
		
		Log.d("length", "--->" + data.size());
		for (EntityDB val : data) {
			EntityData details = new EntityData();
			details.setNAME(val.getNAME());
			details.setTRD_COMP(val.getTRD_COMP());
			details.setCLASSI_STAT(val.getCLASSI_STAT());
			details.setHR_QTY(val.getHR_QTY());
			details.setTYPE(val.getType());
			Utilities.edata.add(details);

		}

		dbAdapter.Close();
		for (int i = 0; i < Utilities.edata.size(); i++) {
			
			if (Utilities.edata.get(i).getTYPE().equals("L")) {
				EntityAlign align = new EntityAlign();
				align.setTYPE(Utilities.edata.get(i).getTYPE());
				align.setNAME(Utilities.edata.get(i).getNAME());
				align.setCLASSI_STAT(Utilities.edata.get(i).getCLASSI_STAT());
				align.setHR_QTY(Utilities.edata.get(i).getHR_QTY());
				align.setTRD_COMP(Utilities.edata.get(i).getTRD_COMP());
				Utilities.eAligndata.add(align);
			}

		}

		for (int i = 0; i < Utilities.edata.size(); i++) {

			if (Utilities.edata.get(i).getTYPE().equals("E")) {
				EntityAlign align = new EntityAlign();
				align.setTYPE(Utilities.edata.get(i).getTYPE());
				align.setNAME(Utilities.edata.get(i).getNAME());
				align.setCLASSI_STAT(Utilities.edata.get(i).getCLASSI_STAT());
				align.setHR_QTY(Utilities.edata.get(i).getHR_QTY());
				align.setTRD_COMP(Utilities.edata.get(i).getTRD_COMP());
				Utilities.eAligndata.add(align);
			}

		}
		for (int i = 0; i < Utilities.edata.size(); i++) {

			if (Utilities.edata.get(i).getTYPE().equals("M")) {
				EntityAlign align = new EntityAlign();
				align.setTYPE(Utilities.edata.get(i).getTYPE());
				align.setNAME(Utilities.edata.get(i).getNAME());
				align.setCLASSI_STAT(Utilities.edata.get(i).getCLASSI_STAT());
				align.setHR_QTY(Utilities.edata.get(i).getHR_QTY());
				align.setTRD_COMP(Utilities.edata.get(i).getTRD_COMP());
				Utilities.eAligndata.add(align);
			}

		}
		Log.d("ealigndata arraylength", "---->" + Utilities.eAligndata.size());

		for (int i = 0; i < Utilities.eAligndata.size(); i++) {
			Log.d("alighdata", "---->" + Utilities.eAligndata.get(i).getTYPE());
			
		}

		entriesListView = (ListView) findViewById(R.id.list);
		EntriesListAdapter entriesListAdapter = new EntriesListAdapter();
		entriesListView.setAdapter(entriesListAdapter);
		entriesListAdapter.notifyDataSetChanged();
		entriesListAdapter.notifyDataSetInvalidated();

	}
}