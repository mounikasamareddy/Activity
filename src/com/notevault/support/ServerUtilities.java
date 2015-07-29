package com.notevault.support;

import android.content.Context;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.notevault.activities.LoginActivity;
import com.notevault.pojo.Singleton;

import java.net.UnknownHostException;

public class ServerUtilities {
    String JSONResponseString;
    Context context;
    private static HttpClient httpclient = new DefaultHttpClient();
    Singleton singleton = Singleton.getInstance();
    public static boolean unknownHostException = false;
    public static String apiDomainURI = "https://dev.notevault.com/mobile/collector.php?req=";
   
    public String authenticate(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String apiURL = apiDomainURI + "authenticate";
        try {
            HttpPost httppost = new HttpPost(apiURL);
            httppost.setHeader("Content-type", "application/json");
            System.out.println("Login Credentials: " + jsonObject);
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("Login Response code: "+singleton.getHTTPResponseStatusCode());
            JSONResponseString =   EntityUtils.toString(response.getEntity());
            System.out.println("Login Response message: "+JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String signUp(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String apiURL = apiDomainURI + "signUp";
        try {
            HttpPost httppost = new HttpPost(apiURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("SignUp Response code: "+singleton.getHTTPResponseStatusCode());
            JSONResponseString =   EntityUtils.toString(response.getEntity());
            System.out.println("SignUp Response message: "+JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String getAllProjectTasks (JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String getLaborNameURL = apiDomainURI + "getAllProjectTasks";
        try {
            HttpPost httppost = new HttpPost(getLaborNameURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("task Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(task) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String getMyTaskActivities (JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String getLaborNameURL = apiDomainURI + "getMytaskActivities";
        try {
            HttpPost httppost = new HttpPost(getLaborNameURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("MyTask Activities Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(MyTask Activities) json string: " + JSONResponseString);
        }catch (UnknownHostException e) {
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String addTaskToProject(JSONObject jsonObject)  {
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String apiURL = apiDomainURI + "addTaskToProject";
        try {
            HttpPost httppost = new HttpPost(apiURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes( "UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("addTaskToProject Request response code: " +  singleton.getHTTPResponseStatusCode());
            JSONResponseString =   EntityUtils.toString(response.getEntity());
            System.out.println("Response(addTaskToProject) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String getActivitiesByTask (JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        //String getLaborNameURL = apiDomainURI + "getActivitiesByTask";
        String getLaborNameURL = apiDomainURI + "getActivitiesByTaskIdAndProjectDay";
        try {
            HttpPost httppost = new HttpPost(getLaborNameURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("get TaskActivity Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(Activity) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String addActivityToTask(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String addActivityToTaskURL = apiDomainURI + "addActivityToTask";
        try {
            HttpPost httppost = new HttpPost(addActivityToTaskURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("add TaskActivity Request response code: " +  singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(Activity) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }
    public String addActivityShiftToTask(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String addActivityToTaskURL = apiDomainURI + "addActivityToTask";
        try {
            HttpPost httppost = new HttpPost(addActivityToTaskURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("add TaskActivity Request response code: " +  singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(Activity) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }
	/*public String projectName(JSONObject jsonObject){

		String resString;
		String url2 ="https://dev.notevault.com/mobileapp/collector.php?req=getprojectNames";
		try {			
			HttpPost httppost = new HttpPost(url2); 
			httppost.setHeader("Content-type", "application/json");  
			httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
			HttpResponse response = httpclient.execute(httppost); 
			int responseCode = response.getStatusLine().getStatusCode();
			System.out.println("Server response code................"+responseCode);
			resString =   EntityUtils.toString(response.getEntity());
			System.out.println("response string is =============="+resString);
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return resString;  
	}*/

    //Accepts a JSON Object calls getTradeNamesAPI & returns response as JSON String
    public String getLaborPersonnel(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String getLaborNameURL = apiDomainURI + "getLaborPersonnel";
        try {
            HttpPost httppost = new HttpPost(getLaborNameURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("getlaborNames Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(laborname) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }
    //Accepts a JSON Object calls getTradeNamesAPI & returns response as JSON String
    public String getLaborTrade(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String getTradeNameURL = apiDomainURI + "getLaborTrade";
        try {
            HttpPost httppost = new HttpPost(getTradeNameURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("getTradeNames Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(labortrade) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    //Accepts a JSON Object calls getLaborClassificationAPI & returns response as JSON String
    public String getLaborClassification(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String getLaborClassificationURL = apiDomainURI + "getLaborClassification";
        try {
            HttpPost httppost = new HttpPost(getLaborClassificationURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("getTradeNames Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(laborclassification) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    //Accepts a JSON Object calls getEquipmentNameAPI & returns response as JSON String
    public String getEquipmentName(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String getEquipmentNameURL = apiDomainURI + "getEquipmentName";
        try {
            HttpPost httppost = new HttpPost(getEquipmentNameURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("getEquipmentName Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(equipmentname) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    //Accepts a JSON Object calls getEquipmentCompanyAPI & returns response as JSON String
    public String getCompanyName(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String getEquipmentCompanyURL = apiDomainURI + "getCompanyNames";
        try {
            HttpPost httppost = new HttpPost(getEquipmentCompanyURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("getEquipmentCompany Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(equipmentcompany) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }
    //Accepts a JSON Object calls getEquipmentStatusAPI & returns response as JSON String
    public String getEquipmentStatus(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String getEquipmentStatusURL = apiDomainURI + "getEquipmentStatus";
        try {
            HttpPost httppost = new HttpPost(getEquipmentStatusURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            System.out.println("getEquipmentStatus Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(equipmentcompany) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    //Accepts a JSON Object calls getEquipmentNameAPI & returns response as JSON String
    public String getMaterialName(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String getMaterialNameURL = apiDomainURI + "getMaterialName";
        try {
            HttpPost httppost = new HttpPost(getMaterialNameURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("getMaterialName Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(getMaterialName) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }
    //Accepts a JSON Object calls getEquipmentCompanyAPI & returns response as JSON String
    public String getMaterialCompany(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String getMaterialCompanyURL = apiDomainURI + "getMaterialCompany";
        try {
            HttpPost httppost = new HttpPost(getMaterialCompanyURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("getEquipmentCompany Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(equipmentcompany) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }
    //Accepts a JSON Object calls getEquipmentStatusAPI & returns response as JSON String
    public String getMaterialStatus(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String getMaterialStatusURL = apiDomainURI + "getMaterialStatus";
        try {
            HttpPost httppost = new HttpPost(getMaterialStatusURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("getEquipmentCompany Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(getMaterialStatus) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String addLaborPersonnel(JSONObject jsonObject)  {
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String addPersionnelURL = apiDomainURI + "addLaborPersonnel";
        try {
            HttpPost httppost = new HttpPost(addPersionnelURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes( "UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("addLaborPersonnel Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString =   EntityUtils.toString(response.getEntity());
            System.out.println("Response(addLaborPersonnel) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;

    }

    public String addLaborCompany(JSONObject jsonObject)  {
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String addLaborCompanyURL = apiDomainURI + "addLaborCompanyName";
        try {
            HttpPost httppost = new HttpPost(addLaborCompanyURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes( "UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("addLaborCompany Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString =   EntityUtils.toString(response.getEntity());
            System.out.println("Response(addLaborCompany) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;

    }

    public String addLaborTrade(JSONObject jsonObject)  {
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String addLaborTradeURL = apiDomainURI + "addLaborTrade";
        try {
            HttpPost httppost = new HttpPost(addLaborTradeURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes( "UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("addLaborTrade Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString =   EntityUtils.toString(response.getEntity());
            System.out.println("Response(addLaborTrade) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String addLaborClassification(JSONObject jsonObject)  {
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String addLaborClassificationURL = apiDomainURI + "addLaborClassification";
        try {
            HttpPost httppost = new HttpPost(addLaborClassificationURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes( "UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("addLaborClassification Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString =   EntityUtils.toString(response.getEntity());
            System.out.println("Response(addLaborClassification) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;

    }

    public String addEquipmentName(JSONObject jsonObject)  {
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String addEquipmentNameURL = apiDomainURI + "addEquipmentName";
        try {
            HttpPost httppost = new HttpPost(addEquipmentNameURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes( "UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("addEquipmentName Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString =   EntityUtils.toString(response.getEntity());
            System.out.println("Response(addEquipmentName) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String addCompany(JSONObject jsonObject)  {
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String addEquipmentCompanyURL = apiDomainURI + "addCompany";
        try {
            HttpPost httppost = new HttpPost(addEquipmentCompanyURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes( "UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("addCompany Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString =   EntityUtils.toString(response.getEntity());
            System.out.println("Response(addCompany) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String addMaterialName(JSONObject jsonObject)  {
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String addMaterialNameURL = apiDomainURI + "addMaterialName";
        try {
            HttpPost httppost = new HttpPost(addMaterialNameURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes( "UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("addMaterialName Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString =   EntityUtils.toString(response.getEntity());
            System.out.println("Response(addMaterialName) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String  addMaterialCompany(JSONObject jsonObject)  {
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String addMaterialCompanyURL = apiDomainURI + "addCompany";
        try {
            HttpPost httppost = new HttpPost(addMaterialCompanyURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes( "UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("addMaterialCompany Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString =   EntityUtils.toString(response.getEntity());
            System.out.println("Response(addMaterialCompany) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String  addLaborEntry(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String addLaborEntriesURL = apiDomainURI + "addLaborEntry";
        try {
            HttpPost httppost = new HttpPost(addLaborEntriesURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes( "UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("addLaborEntry Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(addLaborEntry) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String  addEquipmentEntry(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String addEquipmentEntriesURL = apiDomainURI + "addEquipmentEntry";
        try {
            HttpPost httppost = new HttpPost(addEquipmentEntriesURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes( "UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("addEquipmentEntry Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(addEquipmentEntry) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String  addMaterialEntry(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String addMaterialEntriesURL = apiDomainURI + "addMaterialEntry";
        try {
            HttpPost httppost = new HttpPost(addMaterialEntriesURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes( "UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("addMaterialEntry Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(addMaterialEntry) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String getLaborEntries(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String getLaborEntriesURL = apiDomainURI + "getLaborEntries";
        try {
            HttpPost httppost = new HttpPost(getLaborEntriesURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("getLaborEntries Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(getLaborEntries) string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String getEquipmentEntries(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String getEquipmentEntriesURL = apiDomainURI + "getEquipmentEntries";
        try {
            HttpPost httppost = new HttpPost(getEquipmentEntriesURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("getEquipmentEntries Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(getEquipmentEntries) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String getMaterialEntries(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String getMaterialEntriesURL = apiDomainURI + "getMaterialEntries";
        try {
            HttpPost httppost = new HttpPost(getMaterialEntriesURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("getMaterialEntries Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(getMaterialEntries) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String deleteLaborEntry(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String deleteLaborEntryURL = apiDomainURI + "deleteLaborEntry";
        try {
            HttpPost httppost = new HttpPost(deleteLaborEntryURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("deleteLaborEntry Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(deleteLaborEntry) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String deleteEquipmentEntry(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String deleteEquipmentEntryURL = apiDomainURI + "deleteEquipmentEntry";
        try {
            HttpPost httppost = new HttpPost(deleteEquipmentEntryURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("deleteEquipmentEntry Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(deleteEquipmentEntry) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String deleteMaterialEntry(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String deleteMaterialEntryURL = apiDomainURI + "deleteMaterialEntry";
        try {
            HttpPost httppost = new HttpPost(deleteMaterialEntryURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("deleteMaterialEntry Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(deleteMaterialEntry) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String updateLaborEntry(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String updateLaboURL = apiDomainURI + "updateLaborEntry";
        try {
            HttpPost httppost = new HttpPost(updateLaboURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("update labor Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(update labor data) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String updateEquipmentEntry(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String updateEquipmentURL = apiDomainURI + "updateEquipmentEntry";
        try {
            HttpPost httppost = new HttpPost(updateEquipmentURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("update equipment Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(update equipment data) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String updateMaterialEntry(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String updateMaterialURL = apiDomainURI + "updateMaterialEntry";
        try {
            HttpPost httppost = new HttpPost(updateMaterialURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("update material Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(update material data) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String copyActivityToDay(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String getCopyofLaborEntriesURL = apiDomainURI + "copyActivity";//ToDay
        try {
            HttpPost httppost = new HttpPost(getCopyofLaborEntriesURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("copyActivityToDay Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(copyActivityToDay) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }

    public String getLaborSummary(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String getCopyofLaborEntriesURL = apiDomainURI + "getLaborSummary";//ToDay
        try {
            HttpPost httppost = new HttpPost(getCopyofLaborEntriesURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("getLaborSummary Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(getLaborSummary) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }
    public String getActivityCount(JSONObject jsonObject){
        singleton.setHTTPResponseStatusCode(0);
        JSONResponseString = "";
        String getCopyofLaborEntriesURL = apiDomainURI + "getActivityCount";//ToDay
        try {
            HttpPost httppost = new HttpPost(getCopyofLaborEntriesURL);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
            HttpResponse response = httpclient.execute(httppost);
            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
            System.out.println("getActivityCount Request response code: " + singleton.getHTTPResponseStatusCode());
            JSONResponseString = EntityUtils.toString(response.getEntity());
            System.out.println("Response(getActivityCount) json string: " + JSONResponseString);
        }catch (UnknownHostException e){
            unknownHostException = true;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return JSONResponseString;
    }
	public String passwordRecovery(JSONObject jsonObject) {
		 	singleton.setHTTPResponseStatusCode(0);
	        JSONResponseString = "";
	        String recoverPasswordURL = apiDomainURI + "passwordRecovery";
	        try {
	            HttpPost httppost = new HttpPost(recoverPasswordURL);
	            httppost.setHeader("Content-type", "application/json");
	            httppost.setEntity(new ByteArrayEntity(jsonObject.toString().getBytes("UTF8")));
	            HttpResponse response = httpclient.execute(httppost);
	            singleton.setHTTPResponseStatusCode(response.getStatusLine().getStatusCode());
	            System.out.println("recover password Request response code: " + singleton.getHTTPResponseStatusCode());
	            JSONResponseString = EntityUtils.toString(response.getEntity());
	            System.out.println("Response(recover password) json string: " + JSONResponseString);
	        }catch (UnknownHostException e){
	            unknownHostException = true;
	        }catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	        return JSONResponseString;
	}
}