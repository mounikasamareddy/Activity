package com.notevault.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class AddLabor extends Activity{

    Singleton singleton;
    String values[] = {"Name", "Trade", "Classification"};
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlabor);
        singleton = Singleton.getInstance();
        System.err.println("onStart: " + singleton.getSelectedLaborName());
        dbAdapter = DBAdapter.get_dbAdapter(this);

        projectText = (TextView)findViewById(R.id.greeting);
        taskText = (TextView)findViewById(R.id.txt);
        dateText = (TextView)findViewById(R.id.text);
        activityText = (TextView)findViewById(R.id.textv);

        hourEditText = (EditText)findViewById(R.id.editText4);
        //descEditText = (EditText)findViewById(R.id.desc_textView6);
        deleteTextView = (TextView)findViewById(R.id.textView6_delete);

        projectText.setText(singleton.getSelectedProjectName());
        taskText.setText(singleton.getSelectedTaskName());
        activityText.setText(singleton.getSelectedActivityName());
        dateText.setText(singleton.getCurrentSelectedDateFormatted());

        laborView = (ListView)findViewById(R.id.listView1);
        Myadapter myad = new Myadapter();
        laborView.setAdapter(myad);
        hourEditText.setText(singleton.getSelectedLaborHours());
        if (descEditText != null) {
            descEditText.setText(singleton.getSelectedLaborDescription());
        }

        if(singleton.isNewEntryFlag()){
            System.out.println("Enter New Labor entries.");
        }else{
            TextView laborTextView = (TextView)findViewById(R.id.textdata);
            laborTextView.setText("Labor");
            deleteTextView.setVisibility(View.VISIBLE);
        }

        cancelTextView = (TextView)findViewById(R.id.cancel);
        cancelTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        deleteTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddLabor.this);
                alertDialog.setTitle("Delete Labor");
                alertDialog.setMessage("Please confirm to delete.");

                alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                        mProgressDialog = new ProgressDialog(AddLabor.this);
                        mProgressDialog.setMessage("Loading...");
                        mProgressDialog.setIndeterminate(false);
                        mProgressDialog.show();

                        DeleteLaborTask deleteLabor = new DeleteLaborTask();
                        deleteLabor.execute();
                    }
                });
                // Setting Negative "cancel" Button
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                // Showing Alert Message
                alertDialog.show();
            }
        });
        
        hourEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                	addImageLayout.performClick();
                    return true;
                }
                return false;
            }

        });

        addImageLayout = (LinearLayout)findViewById(R.id.image_layout);
        addLaborEntriesView = (ImageView)findViewById(R.id.save);
        addImageLayout.setOnClickListener(new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {

                singleton.setSelectedLaborHours(hourEditText.getText().toString().trim());
                //singleton.setSelectedLaborDescription(descEditText.getText().toString().trim());
                String n, t, c, h;
                errorMsg = "";
                n = singleton.getSelectedLaborName();
                t = singleton.getSelectedLaborTrade();
                c = singleton.getSelectedLaborClassification();
                h = singleton.getSelectedLaborHours();

                // Entry fields validation.
                if(n.equalsIgnoreCase(""))
                    errorMsg = "Name";
                if(t.equalsIgnoreCase("")){
                    if(!errorMsg.equalsIgnoreCase(""))
                        errorMsg += ", ";
                    errorMsg += "Trade";
                }
                if(c.equalsIgnoreCase("")){
                    if(!errorMsg.equalsIgnoreCase(""))
                        errorMsg += ", ";
                    errorMsg += "Classification";
                }
                if(h.equalsIgnoreCase("")){
                    if(!errorMsg.equalsIgnoreCase(""))
                        errorMsg += ", ";
                    errorMsg += "Hour";
                }
                if(errorMsg.length() > 0){
                    if(errorMsg.contains(",")){
                        errorMsg += " are mandatory.";
                    }else{
                        errorMsg += " is mandatory.";
                    }
                }
                System.err.println("setFlag: "+ singleton.isNewEntryFlag());

                AlertDialog alertDialog = new AlertDialog.Builder(AddLabor.this).create();
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        errorMsg = "";
                    }
                });

                if(errorMsg.equalsIgnoreCase("")){
                    mProgressDialog = new ProgressDialog(AddLabor.this);
                    mProgressDialog.setMessage("Loading...");
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.show();
                    if (singleton.isNewEntryFlag()) {
                        System.out.println("Add entries code should execute");
                        AddLaborEntries laborEntries = new AddLaborEntries();
                        laborEntries.execute();
                    }else{
                        System.out.println("update code should execute");
                        UpdateEntries updateLabor = new UpdateEntries();
                        updateLabor.execute();
                    }
                }else{
                    //display alert(errorMsg);
                    alertDialog.setMessage(errorMsg);
                    alertDialog.show();
                    errorMsg = "";
                }

            }
        });
    }

    class Myadapter extends BaseAdapter{

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
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater li = getLayoutInflater();
            convertView = li.inflate(R.layout.customlist, null);
            TextView tv=(TextView)convertView.findViewById(R.id.textView1);
            tv2 = (TextView)convertView.findViewById(R.id.textView2);

            final String val[] = values[position].split("~");
            tv.setText(val[0]);
            tv2.setVisibility(View.VISIBLE);

            if (val[0].equals("Name")) {
                tv2.setText(singleton.getSelectedLaborName());
            } else if (val[0].equals("Trade")) {
                tv2.setText(singleton.getSelectedLaborTrade());
            }else if (val[0].equals("Classification")) {
                tv2.setText(singleton.getSelectedLaborClassification());
            }

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    System.err.println("Hours in EditText: "+ hourEditText.getText().toString().trim());
                    singleton.setSelectedLaborHours(hourEditText.getText().toString().trim());
                    //singleton.setSelectedLaborDescription(descEditText.getText().toString().trim());

                    if (val[0].equals("Name")) {
                        Intent intent = new Intent(AddLabor.this, NameListActivity.class);
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                    } else if (val[0].equals("Trade")) {
                        Intent intent = new Intent(AddLabor.this, TradeListActivity.class);
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                    } else if (val[0].equals("Classification")){
                        Intent intent = new Intent(AddLabor.this, ClassificationList.class);
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
        System.err.println("onResume: "+singleton.getSelectedLaborName());
        System.err.println("Hours in onResume: "+singleton.getSelectedLaborHours());
        laborView = (ListView)findViewById(R.id.listView1);
        laborView.invalidate();
        this.onCreate(null);
        Myadapter myad = new Myadapter();
        myad.notifyDataSetChanged();
    }

    private class AddLaborEntries extends AsyncTask<Void, Void, String> {

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
                    JSONObject jsonAddLabor = new JSONObject();
                    jsonAddLabor.put("AccountID", singleton.getAccountId());
                    jsonAddLabor.put("SubscriberID", singleton.getSubscriberId());
                    jsonAddLabor.put("ProjectID", singleton.getSelectedProjectID());
                    jsonAddLabor.put("ActivityId", singleton.getSelectedActivityID());
                    jsonAddLabor.put("ProjectDay", singleton.getCurrentSelectedDate());
                    jsonAddLabor.put("Name", singleton.getSelectedLaborName());
                    jsonAddLabor.put("Trade", singleton.getSelectedLaborTrade());
                    jsonAddLabor.put("Classification", singleton.getSelectedLaborClassification());
                    jsonAddLabor.put("Hours", singleton.getSelectedLaborHours());
                    //jsonAddLabor.put("Notes", singleton.getSelectedLaborDescription());
                    jsonAddLabor.put("TaskId", singleton.getSelectedTaskID());
                    jsonAddLabor.put("UserId", singleton.getUserId());
                    return jsonDataPost.addLaborEntry(jsonAddLabor);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }catch (Exception e) {
                String ID = dbAdapter.generateOfflineEntryID();
                long laborInsertResponse = dbAdapter.insertEntry(singleton.getSelectedLaborName(),  singleton.getSelectedLaborTrade(), singleton.getSelectedLaborClassification(), singleton.getSelectedLaborHours(), "L", "I", ID);
                EntriesListByDateActivity.collectiveConcatenatedEntryList.add("L" + glue + singleton.getSelectedLaborName() + glue + singleton.getSelectedLaborTrade() + glue + singleton.getSelectedLaborClassification() + glue + singleton.getSelectedLaborHours() + glue + ID);
                System.out.println("laborInsertResponse inside Add Labor JSON Exception: "+ laborInsertResponse);
                singleton.setReloadPage(true);
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(final String result) {
            mProgressDialog.dismiss();
            long laborInsertResponse = 0;
            if (ServerUtilities.unknownHostException) {
                ServerUtilities.unknownHostException = false;
                Toast.makeText(getApplicationContext(), "Sorry! Server could not be reached.", Toast.LENGTH_LONG).show();
                laborInsertResponse = dbAdapter.insertEntry(singleton.getSelectedLaborName(), singleton.getSelectedLaborTrade(), singleton.getSelectedLaborClassification(), singleton.getSelectedLaborHours(), "L", "I", dbAdapter.generateOfflineEntryID());
            } else {
                if (result != null){
                    System.out.println("Add labor response: " + result);
                    //boolean isJSONObject = false;

                    try {
                        int StatusCode = singleton.getHTTPResponseStatusCode();
                        JSONObject jObject = new JSONObject(result);
                        String Status = jObject.getString("Status");

                        if (Status.equals("Success") || StatusCode == 200 || StatusCode == 0) {
                            laborInsertResponse = dbAdapter.insertEntry(singleton.getSelectedLaborName(), singleton.getSelectedLaborTrade(), singleton.getSelectedLaborClassification(), singleton.getSelectedLaborHours(), "L", "N", jObject.getString("LID"));
                            System.out.println("laborInsertResponse inside Add Labor Success: " + laborInsertResponse);
                            singleton.setReloadPage(true);
                        } else {
                            laborInsertResponse = dbAdapter.insertEntry(singleton.getSelectedLaborName(), singleton.getSelectedLaborTrade(), singleton.getSelectedLaborClassification(), singleton.getSelectedLaborHours(), "L", "I", dbAdapter.generateOfflineEntryID());
                            System.out.println("laborInsertResponse inside Add Labor Failure: " + laborInsertResponse);
                            singleton.setReloadPage(true);
                        }
                    } catch (JSONException e) {
                        laborInsertResponse = dbAdapter.insertEntry(singleton.getSelectedLaborName(), singleton.getSelectedLaborTrade(), singleton.getSelectedLaborClassification(), singleton.getSelectedLaborHours(), "L", "I", dbAdapter.generateOfflineEntryID());
                        System.out.println("laborInsertResponse inside Add Labor JSON Exception: " + laborInsertResponse);
                        e.printStackTrace();
                    }
                }else{
                    laborInsertResponse = dbAdapter.insertEntry(singleton.getSelectedLaborName(), singleton.getSelectedLaborTrade(), singleton.getSelectedLaborClassification(), singleton.getSelectedLaborHours(), "L", "I", dbAdapter.generateOfflineEntryID());
                    System.out.println("An error occurred! Could not add entry.");
                }
            }
            singleton.setReloadPage(true);
            onBackPressed();
        }
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

                try {
                    JSONObject jsonAddLabor = new JSONObject();
                    jsonAddLabor.put("AccountID", singleton.getAccountId());
                    jsonAddLabor.put("SubscriberID", singleton.getSubscriberId());
                    jsonAddLabor.put("ProjectID", singleton.getSelectedProjectID());
                    jsonAddLabor.put("ActivityId", singleton.getSelectedActivityID());
                    jsonAddLabor.put("ID", singleton.getCurrentSelectedEntryID());
                    jsonAddLabor.put("Name", singleton.getSelectedLaborName());
                    jsonAddLabor.put("Trade", singleton.getSelectedLaborTrade());
                    jsonAddLabor.put("Classification", singleton.getSelectedLaborClassification());
                    jsonAddLabor.put("Hours", singleton.getSelectedLaborHours());
                    //jsonAddLabor.put("Notes", singleton.getSelectedLaborDescription());
                    //jsonAddLabor.put("UserId", singleton.getUserId());
                    System.out.println("labor sent id :" + singleton.getCurrentSelectedEntryID());
                    return jsonDataPost.updateLaborEntry(jsonAddLabor);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(final String result) {
            long laborUpdateResponse = 0;
            mProgressDialog.dismiss();
            if (ServerUtilities.unknownHostException) {
                ServerUtilities.unknownHostException = false;
                Toast.makeText(getApplicationContext(), "Sorry! Server could not be reached.", Toast.LENGTH_LONG).show();
                if(singleton.isOfflineEntry())
                    laborUpdateResponse = dbAdapter.updateEntry(singleton.getSelectedLaborName(), singleton.getSelectedLaborTrade(), singleton.getSelectedLaborClassification(), singleton.getSelectedLaborHours(), "L", "I", "OF"+String.valueOf(singleton.getCurrentSelectedEntryID()));
                else
                    laborUpdateResponse = dbAdapter.updateEntry(singleton.getSelectedLaborName(), singleton.getSelectedLaborTrade(), singleton.getSelectedLaborClassification(), singleton.getSelectedLaborHours(), "L", "U", String.valueOf(singleton.getCurrentSelectedEntryID()));
            } else {
                if(result != null) {
                    System.out.println("Update labor response: " + result);
                    int StatusCode = singleton.getHTTPResponseStatusCode();
                    if(StatusCode == 200 || StatusCode == 0){
                        laborUpdateResponse = dbAdapter.updateEntry(singleton.getSelectedLaborName(), singleton.getSelectedLaborTrade(), singleton.getSelectedLaborClassification(), singleton.getSelectedLaborHours(), "L", "N", String.valueOf(singleton.getCurrentSelectedEntryID()));
                    }else{
                        if(singleton.isOfflineEntry())
                            laborUpdateResponse = dbAdapter.updateEntry(singleton.getSelectedLaborName(), singleton.getSelectedLaborTrade(), singleton.getSelectedLaborClassification(), singleton.getSelectedLaborHours(), "L", "I", "OF"+String.valueOf(singleton.getCurrentSelectedEntryID()));
                        else
                            laborUpdateResponse = dbAdapter.updateEntry(singleton.getSelectedLaborName(), singleton.getSelectedLaborTrade(), singleton.getSelectedLaborClassification(), singleton.getSelectedLaborHours(), "L", "U", String.valueOf(singleton.getCurrentSelectedEntryID()));
                    }

                }else{
                    System.out.println("An error occurred! Could not update entry.");
                    if(singleton.isOfflineEntry())
                        laborUpdateResponse = dbAdapter.updateEntry(singleton.getSelectedLaborName(), singleton.getSelectedLaborTrade(), singleton.getSelectedLaborClassification(), singleton.getSelectedLaborHours(), "L", "I", "OF"+String.valueOf(singleton.getCurrentSelectedEntryID()));
                    else
                        laborUpdateResponse = dbAdapter.updateEntry(singleton.getSelectedLaborName(), singleton.getSelectedLaborTrade(), singleton.getSelectedLaborClassification(), singleton.getSelectedLaborHours(), "L", "U", String.valueOf(singleton.getCurrentSelectedEntryID()));
                }
            }
            singleton.setReloadPage(true);
            onBackPressed();
        }
    }

    private class DeleteLaborTask extends AsyncTask<Void, Void, String> {

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
                    JSONObject jsonLabor = new JSONObject();
                    jsonLabor.put("Id", singleton.getCurrentSelectedEntryID());
                    return jsonDataPost.deleteLaborEntry(jsonLabor);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(final String result) {
            long laborDeleteResponse = 0;
            mProgressDialog.dismiss();
            if (ServerUtilities.unknownHostException) {
                ServerUtilities.unknownHostException = false;
                Toast.makeText(getApplicationContext(), "Sorry! Server could not be reached.", Toast.LENGTH_LONG).show();
                if(singleton.isOfflineEntry())
                    laborDeleteResponse = dbAdapter.deleteEntryByID("OF" + String.valueOf(singleton.getCurrentSelectedEntryID()));
                else
                    laborDeleteResponse = dbAdapter.updateEntry(singleton.getSelectedLaborName(), singleton.getSelectedLaborTrade(), singleton.getSelectedLaborClassification(), singleton.getSelectedLaborHours(), "L", "D", String.valueOf(singleton.getCurrentSelectedEntryID()));
            } else {
                if(result != null) {
                    System.out.println("Delete labor response: " + result);
                    int StatusCode = singleton.getHTTPResponseStatusCode();
                    if(StatusCode == 200 || StatusCode == 0){
                        laborDeleteResponse = dbAdapter.deleteEntryByID("OF" + String.valueOf(singleton.getCurrentSelectedEntryID()));
                    }else {
                        if (singleton.isOfflineEntry())
                            laborDeleteResponse = dbAdapter.deleteEntryByID("OF" + String.valueOf(singleton.getCurrentSelectedEntryID()));
                        else
                            laborDeleteResponse = dbAdapter.updateEntry(singleton.getSelectedLaborName(), singleton.getSelectedLaborTrade(), singleton.getSelectedLaborClassification(), singleton.getSelectedLaborHours(), "L", "D", String.valueOf(singleton.getCurrentSelectedEntryID()));
                    }
                }else{
                    System.out.println("An error occurred! Could not delete entry.");
                    if (singleton.isOfflineEntry())
                        laborDeleteResponse = dbAdapter.deleteEntryByID("OF" + String.valueOf(singleton.getCurrentSelectedEntryID()));
                    else
                        laborDeleteResponse = dbAdapter.updateEntry(singleton.getSelectedLaborName(), singleton.getSelectedLaborTrade(), singleton.getSelectedLaborClassification(), singleton.getSelectedLaborHours(), "L", "D", String.valueOf(singleton.getCurrentSelectedEntryID()));
                }
            }
            singleton.setReloadPage(true);
            onBackPressed();
        }
    }
}