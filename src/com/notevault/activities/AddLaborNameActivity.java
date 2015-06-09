package com.notevault.activities;

import java.security.SecureRandom;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.notevault.datastorage.DBAdapter;
import com.notevault.pojo.Singleton;
import com.notevault.support.ServerUtilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class AddLaborNameActivity extends Activity{

    Singleton singleton;
    ServerUtilities jsonDataPost = new ServerUtilities();
    String newLaborNameText;
    EditText lobor_editText;
    private RadioGroup radioGroup, genderGroup, minorityGroup, localGroup;
    private RadioButton yes, no, maleRadioButton,femaleRadioButton,minority_yesButton,minority_noButton,local_yesButton,local_noButton;
    LinearLayout linearLayout;
    TextView cancel;
    Spinner tradeSpinner, classificationSpinner;
    String defaultClassification, defaultTrade;
    String genderString, minorityString, localString;
    char selectGender;
    int selectMinority, selectLocal;
    DBAdapter DbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addpersonnel);

        singleton = Singleton.getInstance();
        DbAdapter=DbAdapter.get_dbAdapter(this);
        yes = (RadioButton)findViewById(R.id.compliance_yesradio);
        no = (RadioButton)findViewById(R.id.compliance_noradio);

        maleRadioButton = (RadioButton)findViewById(R.id.radio0);
        femaleRadioButton = (RadioButton)findViewById(R.id.radio1);

        minority_yesButton = (RadioButton)findViewById(R.id.radio2);
        minority_noButton = (RadioButton)findViewById(R.id.radio3);

        local_yesButton = (RadioButton)findViewById(R.id.radio4);
        local_noButton = (RadioButton)findViewById(R.id.radio5);

        tradeSpinner = (Spinner)findViewById(R.id.trade_spinner);
        classificationSpinner = (Spinner)findViewById(R.id.classific_spinner);

        TextView textView =(TextView)findViewById(R.id.textdata);
        textView.setText("Add Name");
        lobor_editText = (EditText)findViewById(R.id.editText1);
        lobor_editText.setHint("Name");

        LaborTrade laborTrade=new LaborTrade();
        laborTrade.execute();

        LinearLayout addImageLayout = (LinearLayout)findViewById(R.id.image_layout);
        addImageLayout.setOnClickListener(new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
            	if(singleton.isOnline()) {
                newLaborNameText = lobor_editText.getText().toString().trim();
                if(newLaborNameText.equals("")){
                    AlertDialog alertDialog = new AlertDialog.Builder(AddLaborNameActivity.this).create();
                    alertDialog.setMessage("Please enter labor name");
                    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.show();
                }else{
                    AddPersonnelTask personnelTask = new AddPersonnelTask();
                    personnelTask.execute();
                }
            	}else{
    				Toast.makeText(getApplicationContext(), "UR in  offline!", Toast.LENGTH_LONG).show();
    				readDbData();
    			}
            }

			
        });

        cancel = (TextView)findViewById(R.id.cancel);
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup3);

        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                linearLayout = (LinearLayout)findViewById(R.id.linearlayout2);
                if(checkedId == R.id.compliance_yesradio) {
                    linearLayout.setVisibility(View.VISIBLE);
                } else if(checkedId == R.id.compliance_noradio) {
                    linearLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        genderGroup = (RadioGroup)findViewById(R.id.radioGroup1);
        genderGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                maleRadioButton = (RadioButton) findViewById(checkedId);
                femaleRadioButton = (RadioButton) findViewById(checkedId);
                genderString = femaleRadioButton.getText().toString();
                genderString = maleRadioButton.getText().toString();
                if(genderString.equals("Male")){
                    selectGender = genderString.charAt(0);
                    System.out.println("selected gender...:" + selectGender);
                }else{
                    selectGender = genderString.charAt(0);
                    System.out.println("selected gender...:" + selectGender);
                }
            }
        });

        minorityGroup = (RadioGroup)findViewById(R.id.radioGroup2);
        minorityGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                minority_yesButton = (RadioButton) findViewById(checkedId);
                minority_noButton = (RadioButton) findViewById(checkedId);
                minorityString = minority_noButton.getText().toString();
                minorityString = minority_yesButton.getText().toString();
                System.out.println(minorityString);
                if(minorityString.equals("Yes")){
                    selectMinority = 1;
                    System.out.println("selected minority...:"+selectMinority);
                }else{
                    selectMinority = 0;
                    System.out.println("selected minority...:"+selectMinority);
                }
            }
        });

        localGroup = (RadioGroup)findViewById(R.id.radioGroup4);
        localGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                local_yesButton = (RadioButton) findViewById(checkedId);
                local_noButton = (RadioButton) findViewById(checkedId);
                localString = local_yesButton.getText().toString();
                localString = local_noButton.getText().toString();
                System.out.println(localString);
                if(localString.equals("Yes")){
                    selectLocal = 1;
                    System.out.println("selected local...:"+selectLocal);
                }else{
                    selectLocal = 0;
                    System.out.println("selected local...:"+selectLocal);
                }
            }
        });

        tradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
                defaultTrade = item.toString();
                System.out.println("trade........"+defaultTrade);

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        classificationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item2 = parent.getItemAtPosition(pos);
                defaultClassification = item2.toString();
                System.out.println("classification........"+defaultClassification);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private class AddPersonnelTask extends AsyncTask<Void, Void, String> {
        //ArrayList<String> name = new ArrayList<String>();
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
                        return false;
                    }


                };
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(hv);

                try {
                    JSONObject jsonAddPersonnel = new JSONObject();
                    jsonAddPersonnel.put("ProjectID", singleton.getSelectedProjectID());
                    jsonAddPersonnel.put("CompanyID", singleton.getCompanyId());
                    jsonAddPersonnel.put("GlossaryWord", newLaborNameText);
                    jsonAddPersonnel.put("GlossaryCategoryID", singleton.getLNCID());
                    jsonAddPersonnel.put("Gender", selectGender);
                    jsonAddPersonnel.put("Minority", selectMinority);
                    jsonAddPersonnel.put("IsLocal", selectLocal);
                    jsonAddPersonnel.put("DefaultTrade", defaultTrade);
                    jsonAddPersonnel.put("DefaultClassification", defaultClassification);
                    jsonAddPersonnel.put("AccountId", singleton.getAccountId());
                    return jsonDataPost.addLaborPersonnel(jsonAddPersonnel);
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
            Intent intent = new Intent(AddLaborNameActivity.this, NameListActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private class LaborTrade extends AsyncTask<Void, Void, String> {
        ArrayList<String> trade = new ArrayList<String>();
        ArrayList<String> classification = new ArrayList<String>();

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
            }catch (Exception e) {
                e.printStackTrace();
            }

            try {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("ProjectID", singleton.getSelectedProjectID());
                jsonObj.put("CompanyID", singleton.getCompanyId());
                jsonObj.put("GlossaryCategoryID", singleton.getLTCID());

                return jsonDataPost.getLaborTrade(jsonObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(final String result) {
            try {
                JSONArray jsonResponse = new JSONArray(result);
                for(int i=0; i < jsonResponse.length(); i++) {
                    JSONObject e = jsonResponse.getJSONObject(i);
                    trade.add(e.getString("W"));
                }

                JSONObject jsonObj = new JSONObject();
                jsonObj.put("GlossaryCategoryID", singleton.getLCCID());
                String classificationResponse = jsonDataPost.getLaborClassification(jsonObj);
                JSONArray jsonResponse3 = new JSONArray(classificationResponse);
                for(int i=0; i < jsonResponse3.length(); i++) {
                    JSONObject e = jsonResponse3.getJSONObject(i);
                    classification.add(e.getString("W"));
                }

                setLaborTradeAdapter(trade);
                setLaborNameAdapter(classification);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void setLaborTradeAdapter(ArrayList<String> trade2) {
            ArrayAdapter<String> tradeListadp = new ArrayAdapter<String>(AddLaborNameActivity.this,android.R.layout.simple_list_item_1,trade2);
            tradeListadp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            tradeSpinner.setAdapter(tradeListadp);

        }
        private void setLaborNameAdapter(ArrayList<String> lbclassific2) {
            ArrayAdapter<String> tradeListadp = new ArrayAdapter<String>(AddLaborNameActivity.this,android.R.layout.simple_list_item_1,lbclassific2);
            tradeListadp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            classificationSpinner.setAdapter(tradeListadp);
        }
    }
    private void readDbData() {
    	
    	
    	long laborrecords=DbAdapter.insertGlossaryoffline(singleton.getLNCID(),newLaborNameText);
    	Log.d("labor","--->"+laborrecords);
    	
		
		
	}
}
