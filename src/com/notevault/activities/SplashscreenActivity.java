package com.notevault.activities;

import java.security.SecureRandom;

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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.notevault.arraylistsupportclasses.LoginData;
import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;
import com.notevault.support.Utilities;

public class SplashscreenActivity extends Activity{
    private static int SPLASH_TIME_OUT = 500;
    Singleton singleton;
    SharedPreferences sharedPreferences,settingshifttask,settingPreferences2;
    public static final String MyPREFERENCES = "MyPrefs" ;
    static BroadcastReceiver networkStateReceiver;
    Intent intent;
    public static Context splashScreenActivityContext;
    String username, password;
    ServerUtilities jsonDataPost = new ServerUtilities();
    DBAdapter dbAdapter;
    //Map<Integer, String> offlineDataFromDB = new HashMap<Integer, String>();
    JSONObject offlineEntryJSONObj;
    String type = "";
    String action = "";
    //private ProgressDialog mProgressDialog;
String welcomwName1=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        
        singleton = Singleton.getInstance();
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        dbAdapter = DBAdapter.get_dbAdapter(this);

        boolean connectivity = testNetwork(SplashscreenActivity.this);
        if (!connectivity) {
            // write your code
            System.out.println("You are now offline");
            Log.d("offline..con","--->");
            singleton.setOnline(false);
        } else {
            //write your code
            System.out.println("You are now online");

            singleton.setOnline(true);
        }

        splashScreenActivityContext = getApplicationContext();
        networkStateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                boolean connectivity = testNetwork(SplashscreenActivity.this);
                if (!connectivity) {
                    // write your code
                    System.out.println("You are now offline");
                    singleton.setOnline(false);
                } else {
                    //write your code
                    System.out.println("You are now online");
                    if(!singleton.isOnline())
                        syncDataWithServer();
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        try {
            registerReceiver(networkStateReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(sharedPreferences.contains("loggedOut")
                && sharedPreferences.getString("loggedOut", "").equalsIgnoreCase("false")){
            //intent = new Intent(SplashscreenActivity.this, LoginActivity.class);
            executeAutoLogin();
        }else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    intent = new Intent(SplashscreenActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }, SPLASH_TIME_OUT);
        }
    }

    public void syncDataWithServer(){
    	
    	Log.d("offline","-->");
//        Cursor c;
//        String name, trade_comp, classification_status, ID, Date;
//        double hr_qty = 0;
//        int ActivityID, AccountID, ProjectID, SubID, TaskID, UserID;
//        c = dbAdapter.queryOfflineEntries();
//        if (c != null ) {
//            if  (c.moveToFirst()) {
//                //int entriesArrayLength = c.getCount();
//                UpdateEntries updateEntries;
//                //mProgressDialog = new ProgressDialog(SplashscreenActivity.this);
//                //mProgressDialog.setMessage("Loading...");
//                //mProgressDialog.setIndeterminate(false);
//                //mProgressDialog.show();
//                singleton.setSyncingToServer(true);
//                do {
//                    System.out.println("#######  SYNC IN PROGRESS.  #######");
//                    name = c.getString(c.getColumnIndex("NAME"));
//                    trade_comp = c.getString(c.getColumnIndex("TRD_COMP"));
//                    classification_status = c.getString(c.getColumnIndex("CLASSI_STAT"));
//                    action = c.getString(c.getColumnIndex("ACTION"));
//                    type = c.getString(c.getColumnIndex("TYPE"));
//                    ID = c.getString(c.getColumnIndex("ID"));
//                    Date = c.getString(c.getColumnIndex("DATE"));
//
//                    hr_qty = c.getDouble(c.getColumnIndex("HR_QTY"));
//                    ProjectID = c.getInt(c.getColumnIndex("PID"));
//                    TaskID = c.getInt(c.getColumnIndex("TID"));
//                    ActivityID = c.getInt(c.getColumnIndex("AID"));
//                    UserID = c.getInt(c.getColumnIndex("UserID"));
//                    AccountID = c.getInt(c.getColumnIndex("AccountID"));
//                    SubID = c.getInt(c.getColumnIndex("SubID"));
//                    offlineEntryJSONObj = new JSONObject();
//
//                    try {
//                        if(action.equals("I")){
//                            offlineEntryJSONObj.put("AccountID", AccountID);
//                            offlineEntryJSONObj.put("SubscriberID", SubID);
//                            offlineEntryJSONObj.put("ProjectID", ProjectID);
//                            offlineEntryJSONObj.put("ActivityId", ActivityID);
//                            offlineEntryJSONObj.put("ProjectDay", Date);
//                            offlineEntryJSONObj.put("Name", name);
//                            offlineEntryJSONObj.put("Trade", trade_comp);
//                            offlineEntryJSONObj.put("Classification", classification_status);
//                            offlineEntryJSONObj.put("Hours", hr_qty);
//                            offlineEntryJSONObj.put("TaskId", TaskID);
//                            offlineEntryJSONObj.put("UserId", UserID);
//                        }else if(action.equals("U")){
//                            offlineEntryJSONObj.put("AccountID", AccountID);
//                            offlineEntryJSONObj.put("SubscriberID", SubID);
//                            offlineEntryJSONObj.put("ProjectID", ProjectID);
//                            offlineEntryJSONObj.put("ActivityId", ActivityID);
//                            offlineEntryJSONObj.put("ID", ID);
//                            offlineEntryJSONObj.put("Name", name);
//                            offlineEntryJSONObj.put("Trade", trade_comp);
//                            offlineEntryJSONObj.put("Classification", classification_status);
//                            offlineEntryJSONObj.put("Hours", hr_qty);
//                        }else{
//                            offlineEntryJSONObj.put("Id", ID);
//                        }
//
//                        updateEntries = new UpdateEntries();
//                        updateEntries.execute();
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }while (c.moveToNext());
//                //mProgressDialog.dismiss();
//                singleton.setSyncingToServer(false);
//            }else{
//                //System.out.println("No Tasks found in DB for selected Project.");
//            }
//        }
//        singleton.setOnline(true);
  }

    private class UpdateEntries extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... arg0) {

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

                // Call the respective entry api here.
                // Add, Update or Delete.
                if(type.equals("L")){
                    System.out.println("Type: L");
                    if(action.equals("I")){
                        jsonDataPost.addLaborEntry(offlineEntryJSONObj);
                    }else if(action.equals("U")){
                        jsonDataPost.updateLaborEntry(offlineEntryJSONObj);
                    }else{
                        jsonDataPost.deleteLaborEntry(offlineEntryJSONObj);
                    }
                }else if(type.equals("E")){
                    System.out.println("Type: E");
                    if(action.equals("I")){
                        jsonDataPost.addEquipmentEntry(offlineEntryJSONObj);
                    }else if(action.equals("U")){
                        jsonDataPost.updateEquipmentEntry(offlineEntryJSONObj);
                    }else{
                        jsonDataPost.deleteEquipmentEntry(offlineEntryJSONObj);
                    }
                }else{
                    System.out.println("Type: M");
                    if(action.equals("I")){
                        jsonDataPost.addMaterialEntry(offlineEntryJSONObj);
                    }else if(action.equals("U")){
                        jsonDataPost.updateMaterialEntry(offlineEntryJSONObj);
                    }else{
                        jsonDataPost.deleteMaterialEntry(offlineEntryJSONObj);
                    }
                }
                return jsonDataPost.updateLaborEntry(offlineEntryJSONObj);

            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(final String result) {
            System.out.println("Update offline entry response: " + result);
            long offlineEntryUpdateResponse = 0;
            if (ServerUtilities.unknownHostException)
                ServerUtilities.unknownHostException = false;
            if(result != null) {
                System.out.println("Delete labor response: " + result);
                int StatusCode = singleton.getHTTPResponseStatusCode();
                if(StatusCode == 200 || StatusCode == 0){
                    try {
                        JSONObject jObject = new JSONObject(result);
                        String Status = jObject.getString("Status");
                        String ID = null;
                        if (type.equals("L"))
                            ID = jObject.getString("LID");
                        if (type.equals("E"))
                            ID = jObject.getString("EID");
                        if (type.equals("M"))
                            ID = jObject.getString("MID");
                        offlineEntryUpdateResponse = dbAdapter.deleteEntryByID("OF" + String.valueOf(singleton.getCurrentSelectedEntryID()));
                        //Handle response here.
                        if(action.equals("I")){
                            //Get ID from JSON response and update
                            dbAdapter.updateEntry(offlineEntryJSONObj.getString("Name"), offlineEntryJSONObj.getString("Trade"), offlineEntryJSONObj.getString("Classification"), offlineEntryJSONObj.getString("Hours"), type, "N", ID);
                        }else if(action.equals("U")){
                            dbAdapter.updateEntry(offlineEntryJSONObj.getString("Name"), offlineEntryJSONObj.getString("Trade"), offlineEntryJSONObj.getString("Classification"), offlineEntryJSONObj.getString("Hours"), type, "N", offlineEntryJSONObj.getString("ID"));
                        }else{
                            dbAdapter.deleteEntryByID(offlineEntryJSONObj.getString("Id"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public boolean testNetwork(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

/*        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if(networkInfo != null)
        System.err.println(networkInfo.getType());
        System.err.println( ConnectivityManager.TYPE_WIFI);*/

        //PendingIntent sentPI = PendingIntent.getBroadcast(MainActivity.this, 0, new Intent(ConnectivityManager.CONNECTIVITY_ACTION), 0);

        if (
                (connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null
                        && connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable()
                        && connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()
                )
                        ||
                        (connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null
                                && connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isAvailable()
                                && connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()
                        )
                ) {
            return true;
        } else {
            return false;
        }
    }

    public void executeAutoLogin(){
        if(sharedPreferences.contains("username"))
            username = sharedPreferences.getString("username", "");
        if(sharedPreferences.contains("password"))
            password = sharedPreferences.getString("password", "");

        boolean rememberMe = false;
        if(sharedPreferences.contains("rememberMe") && sharedPreferences.getString("rememberMe", "").equalsIgnoreCase("true")){
            rememberMe = true;
        }

        if(rememberMe && !username.equals("") && !password.equals("")) {
            System.out.println("singleton.isLoggedOut(): " + singleton.isLoggedOut());
            System.out.println("executeAutoLogin triggered.");

            if(singleton.isOnline()){
                LoginTask sign = new LoginTask();
                sign.execute();
            }else{
                Toast.makeText(getApplicationContext(),
                        "You seem to be offline! Cannot login.", Toast.LENGTH_SHORT).show();
                
                Cursor c = dbAdapter.queryCredentialsLogin();
				if (c.getCount() != 0) {

					Log.d("curfdsfdf", "--->" + c.getCount());

					if (c.moveToFirst()) {
						singleton.setUserId(c.getInt(c
								.getColumnIndex("UserID")));
						singleton.setAccountId(c.getInt(c
								.getColumnIndex("AccountID")));
						singleton.setCompanyId(c.getInt(c
								.getColumnIndex("CompanyID")));
						singleton.setLNCID(c.getInt(c
								.getColumnIndex("LNPCID")));
						singleton.setLTCID(c.getInt(c
								.getColumnIndex("LTCID")));
						singleton.setLCCID(c.getInt(c
								.getColumnIndex("LCCID")));
						singleton.setENCID(c.getInt(c
								.getColumnIndex("ENCID")));
						singleton.setCCID(c.getInt(c
								.getColumnIndex("CCID")));
						singleton.setMNCID(c.getInt(c
								.getColumnIndex("MNCID")));
						singleton.setSubscriberId(c.getInt(c
								.getColumnIndex("SubID")));
						singleton.setCompanyName(c.getString(c
								.getColumnIndex("Company")));
						welcomwName1=c.getString(c.getColumnIndex("displayname"));
						singleton.setUsername(welcomwName1);
						
					}

					startActivity(new Intent(getApplicationContext(),
							ProjectListActivity.class));
//                intent = new Intent(SplashscreenActivity.this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);

				}  
            }

        }else{
            intent = new Intent(SplashscreenActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private class LoginTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... arg0) {

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
                    JSONObject json = new JSONObject();
                    json.put("UserName", username);
                    json.put("Password", password);
                    return jsonDataPost.authenticate(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(final String response) {
            //System.out.println();
            if (ServerUtilities.unknownHostException) {
                ServerUtilities.unknownHostException = false;

                Toast.makeText(getApplicationContext(), "Sorry! Server could not be reached.", Toast.LENGTH_LONG).show();

            } else {
                if (response == null)
                    Toast.makeText(getApplicationContext(), "Could not authenticate! Please try again later.", Toast.LENGTH_SHORT).show();
                else {
                    try {
                        JSONObject obj = new JSONObject(response);
                        String statusMessage = obj.getJSONObject("id").get("StatusMessage").toString();

                        /**
                         * Check response status code.
                         */
                        if ((obj.getJSONObject("id").getInt("Status") == 0)
                                || (obj.getJSONObject("id").getInt("Status") == 200)) {

                            /**
                             * Get Enable Tasks switch preferences.
                             */
                            SharedPreferences settingPreferences = getSharedPreferences(SettingActivity.EnableTaskPREFERENCES, Context.MODE_PRIVATE);

                            if(settingPreferences.contains(String.valueOf(singleton.getUserId()))
                                    && settingPreferences.getString(String.valueOf(singleton.getUserId()), "").equalsIgnoreCase("true"))
                            {
                            	
                                singleton.setEnableTasks(true);
                                Log.d("enabled","--->"+singleton.isEnableTasks());
                            }else{
                                singleton.setEnableTasks(false);
                                Log.d("enabled","--->"+singleton.isEnableTasks());
                            }
                            
                         
                            settingshifttask = getSharedPreferences(SettingActivity.EnableSHIFTPREFERENCES, Context.MODE_PRIVATE);

                            if(settingshifttask.contains(String.valueOf(singleton.getUserId()))
                                    && settingshifttask.getString(String.valueOf(singleton.getUserId()), "").equalsIgnoreCase("true"))
                            {
                                singleton.setEnableShiftTracking(true);
                                Log.d("enabled","--->"+singleton.isEnableShiftTracking());
                            }else{
                            	singleton.setEnableShiftTracking(false);
                                Log.d("shiftenabled","--->"+singleton.isEnableShiftTracking());
                            }
                            
                            
                          settingPreferences2 = getSharedPreferences(SettingActivity.EnableOVERTIMETRACKPREFERENCES, Context.MODE_PRIVATE);

                            if(settingPreferences2.contains(String.valueOf(singleton.getUserId()))
                                    && settingPreferences2.getString(String.valueOf(singleton.getUserId()), "").equalsIgnoreCase("true"))
                            {
                                singleton.setEnableOvertimeTracking(true);
                            }else{
                            	singleton.setEnableOvertimeTracking(false);
                            }
                            /**
                             * Update Singleton with server response.
                             */
                            singleton.setAccountId(obj.getJSONObject("id").getInt("AccountID"));
                            singleton.setUserId(obj.getJSONObject("id").getInt("UserID"));
                            singleton.setCompanyId(obj.getJSONObject("id").getInt("CompanyID"));
                            singleton.setLNCID(obj.getJSONObject("id").getInt("LNPCID"));
                            singleton.setLTCID(obj.getJSONObject("id").getInt("LTCID"));
                            singleton.setLCCID(obj.getJSONObject("id").getInt("LCCID"));
                            singleton.setENCID(obj.getJSONObject("id").getInt("ENCID"));
                            singleton.setCCID(obj.getJSONObject("id").getInt("CCID"));
                            singleton.setMNCID(obj.getJSONObject("id").getInt("MNCID"));
                            singleton.setSubscriberId(obj.getJSONObject("id").getInt("SubID"));
                            singleton.setCompanyName(obj.getJSONObject("id").getString("Company"));
                           
                            welcomwName1=obj.getJSONObject("id").getString("DisplayName");
    						singleton.setUsername(welcomwName1);
                         
                            JSONArray projects = obj.getJSONArray("p");
                            singleton.getProjectsList().clear();
                            LoginActivity.projectsListStatus.clear();
                            for (int i = 0; i < projects.length(); i++) {
                                JSONObject curProject = projects.getJSONObject(i);
                                singleton.getProjectsList().put(Integer.valueOf(curProject.getString("PI")), curProject.getString("PN"));
                                LoginActivity.projectsListStatus.put(Integer.valueOf(curProject.getString("PI")), curProject.getString("F"));
                                LoginActivity.projectsListActivityStatus.put(Integer.valueOf(curProject.getString("PI")), curProject.getString("MF"));
                            }
                            int delResponse = dbAdapter.deleteProjects();
                            System.out.println("Projects deletion response: " + delResponse);
                            if (singleton.getProjectsList().size() > 0) {
                                writeProjectsToDb();

                            }

                            System.out.println("Projects List map: " + singleton.getProjectsList());
                            startActivity(new Intent(getApplicationContext(), ProjectListActivity.class));
                        } else{
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public void writeProjectsToDb(){
        String[] values = new String[singleton.getProjectsList().size()];
        long insertResponse = 0;
        int i = 0;
        for (Integer key : singleton.getProjectsList().keySet()) {
            values[i] = singleton.getProjectsList().get(key);
            insertResponse = dbAdapter.insertProject(key, values[i++], LoginActivity.projectsListStatus.get(key).equals("T")?1:0,LoginActivity.projectsListActivityStatus.get(key).equals("T")?1:0);
        }
        System.out.println("Projects insertion response: " + insertResponse);
    }

    public  void readProjectsFromDb(){
        Cursor c = dbAdapter.queryProjects();
        if (c != null ) {
            if  (c.moveToFirst()) {
                do {
                    String projectName = c.getString(c.getColumnIndex("PName"));
                    int projectID = c.getInt(c.getColumnIndex("PID"));
                    String hasData = c.getInt(c.getColumnIndex("hasData"))==1?"T":"F";
                    String hasActivities = c.getInt(c.getColumnIndex("hasActivities"))==1?"T":"F";
                    singleton.getProjectsList().put(projectID, projectName);
                    LoginActivity.projectsListStatus.put(projectID, hasData);
                    LoginActivity.projectsListActivityStatus.put(projectID, hasActivities);
                    System.out.println("Name: " + projectName + ", ID: " + projectID + " hasData: " + hasData + " hasActivities: " + hasActivities);
                }while (c.moveToNext());
            }else{
                System.out.println("No Projects found in DB for this user account.");
            }
            startActivity(new Intent(getApplicationContext(), ProjectListActivity.class));
        }
        dbAdapter.Close();
    }

/*    @Override
    protected void onStop() {
        //if(networkStateReceiver != null)
        //MainActivity.this.unregisterReceiver(networkStateReceiver);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        SplashscreenActivity.this.unregisterReceiver(networkStateReceiver);
        super.onPause();
    }*/

/*@Override
protected void onStop() {
	unregisterReceiver(networkStateReceiver);
	super.onStop();
}*/

}
