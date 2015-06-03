package com.notevault.datastorage;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.notevault.arraylistsupportclasses.ProjectDB;
import com.notevault.arraylistsupportclasses.ProjectData;
import com.notevault.arraylistsupportclasses.TasksDB;
import com.notevault.pojo.Singleton;
import com.notevault.support.Utilities;

/**
 * Created by HP on 1/26/2015.
 */
public class DBAdapter {
    SQLiteDatabase SQLObj;
    DBHelper helperObj;
    Context context;
    Singleton singleton;
    private static DBAdapter _dbAdapter = null;

    public DBAdapter(Context c){
        context = c;
        singleton = Singleton.getInstance();
    }

    public static DBAdapter get_dbAdapter(Context c){
        if(_dbAdapter == null)
            _dbAdapter = new DBAdapter(c);
        return _dbAdapter;
    }

    public DBAdapter opnToRead() {
        helperObj = DBHelper.getDbHelper(context);
        SQLObj = helperObj.getReadableDatabase();
        return this;
    }

    public DBAdapter opnToWrite() {
        helperObj = DBHelper.getDbHelper(context);
        SQLObj = helperObj.getWritableDatabase();
        return this;
    }

    public void Close() {
        SQLObj.close();
    }

    public long insertCredentials(String username, String password){
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("UserID", singleton.getUserId());
        contentValues.put("AccountID", singleton.getAccountId());
        contentValues.put("SubID", singleton.getSubscriberId());
        contentValues.put("CompanyID", singleton.getCompanyId());
        contentValues.put("Company", singleton.getCompanyName());
        contentValues.put("LNPCID", singleton.getLTCID());
        contentValues.put("LTCID", singleton.getLTCID());
        contentValues.put("LCCID", singleton.getLCCID());
        contentValues.put("ENCID", singleton.getENCID());
        contentValues.put("CCID", singleton.getCCID());
        contentValues.put("MNCID", singleton.getMNCID());
        opnToWrite();
        long val = SQLObj.insert("Login", null, contentValues);
        Close();
        return val;
    }

    public long insertProject(int PID, String PName, int hasData, int hasActivities) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("PID", PID);
        contentValues.put("PName", PName);
        contentValues.put("CompanyID", singleton.getCompanyId());
        contentValues.put("UserID", singleton.getUserId());
        contentValues.put("AccountID", singleton.getAccountId());
        contentValues.put("SubID", singleton.getSubscriberId());
        contentValues.put("hasData", hasData);
        contentValues.put("hasActivities", hasActivities);
        opnToWrite();
        long val = SQLObj.insert("Projects", null, contentValues);
        Close();
        return val;
    }

    public long insertTask(int TID, String TName, int ProjectID, int hasData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("TID", TID);
        contentValues.put("TName", TName);
        contentValues.put("ProjectID", ProjectID);
        contentValues.put("ProjectDay", singleton.getCurrentSelectedDate());
        contentValues.put("hasData", hasData);
        opnToWrite();
        long val = SQLObj.insert("Tasks", null, contentValues);
        Close();
        return val;
    }

    public long insertActivity(String ActID, String ActName, int TaskID, String ProjectDay, int hasData, int UserID) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("ActID", ActID);
        contentValues.put("ActName", ActName);
        contentValues.put("TaskID", TaskID);
        contentValues.put("TaskName", singleton.getSelectedTaskName());
        contentValues.put("PID", singleton.getSelectedProjectID());
        contentValues.put("ProjectDay", ProjectDay);
        contentValues.put("hasData", hasData);
        contentValues.put("UserID", UserID);
        opnToWrite();
        long val = SQLObj.insert("Activities", null, contentValues);
        Close();
        return val;
    }

    public long insertEntry(String NAME, String TRD_COMP, String CLASSI_STAT, String HR_QTY, String TYPE, String ACTION, String ID) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", NAME);
        contentValues.put("TRD_COMP", TRD_COMP);
        contentValues.put("CLASSI_STAT", CLASSI_STAT);
        contentValues.put("HR_QTY", Double.parseDouble(HR_QTY));
        //contentValues.put("DESC", DESC);
        contentValues.put("TYPE", TYPE);
        contentValues.put("ACTION", ACTION);
        contentValues.put("ID", ID);
        contentValues.put("PID", singleton.getSelectedProjectID());
        contentValues.put("TID", singleton.getSelectedTaskID());
        contentValues.put("AID", singleton.getSelectedActivityID());
        contentValues.put("UserID", singleton.getUserId());
        contentValues.put("AccountID", singleton.getAccountId());
        contentValues.put("SubID", singleton.getSubscriberId());
        contentValues.put("DATE", singleton.getCurrentSelectedDate());
        opnToWrite();
        long val = SQLObj.insert("Entries", null, contentValues);
        Close();
        return val;
    }

    public long insertGlossary(int GCID, String GName){
        ContentValues contentValues = new ContentValues();
        contentValues.put("GCID", GCID);
        contentValues.put("GName", GName);
        contentValues.put("PID", singleton.getSelectedProjectID());
        contentValues.put("CompanyID", singleton.getCompanyId());
        opnToWrite();
        long val = SQLObj.insert("Glossary", null, contentValues);
        Close();
        return val;
    }
   /* public Cursor queryName() {
        String[] cols = { helperObj.KEY_ID, helperObj.FNAME, helperObj.LNAME };
        opnToWrite();
        Cursor c = SQLObj.query(TABLE_NAME, cols, null, null, null, null, null);
        return c;
    }

    public Cursor queryAll(int nameId) {
        String[] cols = { helperObj.KEY_ID, helperObj.FNAME, helperObj.LNAME };
        opnToWrite();
        Cursor c = SQLObj.query(TABLE_NAME, cols, KEY_ID + "=" + nameId, null, null, null, null);

        return c;

    }*/

    public Cursor queryCredentials(String username, String password){
        String[] cols = {"UserID", "AccountID", "SubID", "CompanyID", "Company", "LNPCID", "LTCID", "LCCID", "ENCID", "CCID", "MNCID"};
        opnToWrite();
        Cursor c = SQLObj.query("Login", cols, "username = '" + username + "' and password = '" + password + "'", null, null, null, null);
        return c;
    }

    public Cursor queryProjects(){
        String[] cols = {"PID", "PName", "hasData", "hasActivities"};
        opnToWrite();
        Cursor c = SQLObj.query("Projects", cols, "CompanyID = " + singleton.getCompanyId() + " and UserID = " + singleton.getUserId() + " and AccountID = " + singleton.getAccountId() + " and SubID = " + singleton.getSubscriberId(), null, null, null, "PName");
        return c;
    }
    
    public List<ProjectDB> getAllProjectRecords() {
       
        // Select All Query
    	  opnToWrite();
    	  List<ProjectDB> dataList = new ArrayList<ProjectDB>();
        String selectQuery = "SELECT  * FROM Projects";

       
        Cursor cursor = SQLObj.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	ProjectDB data = new ProjectDB();
                data.setPID(cursor.getInt(0));
                data.setPName(cursor.getString(1));
                data.setHasData(cursor.getInt(6));
                data.setHasActivities(cursor.getInt(7));
                dataList.add(data);
               
                // Adding contact to list
                
            } while (cursor.moveToNext());
        }
        
        
        
        // return contact list
        return dataList;
    }

    public Cursor queryTasks(int ProjectID, String ProjectDay) {
        String[] cols = { "TID", "TName", "hasData" };
        opnToWrite();
        Cursor c = SQLObj.query("Tasks", cols, "ProjectID = " + ProjectID + " and ProjectDay = " + ProjectDay, null, null, null, "TName");
        return c;
    }

    
    public List<TasksDB> getAllTaskRecords(int pid, String date1) {
        
        // Select All Query
    	  opnToWrite();
    	  List<TasksDB> dataList = new ArrayList<TasksDB>();
        String selectQuery = "SELECT  * FROM Tasks where ProjectId="+pid;

       
        Cursor cursor = SQLObj.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
            	TasksDB data = new TasksDB();
                data.setTID(cursor.getInt(0));
                data.setTName(cursor.getString(1));
                data.setHasData(cursor.getInt(4));
                
                dataList.add(data);
               
                // Adding contact to list
                
            } while (cursor.moveToNext());
        }
        
        
        
        // return contact list
        return dataList;
    }

    public Cursor queryActivities(int TaskID, String ProjectDay, int UserID) {
        String[] cols = { "ActID", "ActName", "hasData"};
        opnToWrite();
        Cursor c = SQLObj.query("Activities", cols, "TaskID = " + TaskID + " and ProjectDay = '" + ProjectDay + "' and UserID = " + UserID, null, null, null, "ActName");
        return c;
    }

    public Cursor queryMyTaskActivities(int TaskID, String ProjectDay, int UserID){
        String[] cols = { "ActID", "ActName", "hasData"};
        opnToWrite();
        Cursor c = SQLObj.query("Activities", cols, "TaskName = 'My Task' and ProjectDay = '" + ProjectDay + "' and UserID = " + UserID + " and PID = " + singleton.getSelectedProjectID(), null, null, null, "ActName");
        return c;
    }

    public Cursor queryEntries() {
        String[] cols = { "TYPE", "NAME", "TRD_COMP", "CLASSI_STAT", "HR_QTY",  "ID", "DATE"};
        opnToWrite();
        Cursor c = SQLObj.query("Entries", cols, "ACTION != 'D' and PID = " + singleton.getSelectedProjectID() + " and SubID = " + singleton.getSubscriberId() + " and AID = " + singleton.getSelectedActivityID() + " and AccountID = " + singleton.getAccountId() + " and DATE = '" + singleton.getCurrentSelectedDate() + "'", null, null, null, "NAME");
        return c;
    }

    public Cursor queryOfflineEntries() {
        String[] cols = { "TYPE", "NAME", "TRD_COMP", "CLASSI_STAT", "HR_QTY",  "ID", "ACTION", "PID", "TID", "AID", "DATE", "UserID", "AccountID", "SubID"};
        opnToWrite();
        Cursor c = SQLObj.query("Entries", cols, "ACTION != 'N'" , null, null, null, null);//and PID = " + singleton.getSelectedProjectID() + " and SubID = " + singleton.getSubscriberId() + " and AID = " + singleton.getSelectedActivityID() + " and AccountID = " + singleton.getAccountId() + " and DATE = '" + singleton.getCurrentSelectedDate() + "'"
        return c;
    }

    public Cursor queryGlossary(int GCID){
        String[] cols = {"GName"};
        opnToWrite();
        Cursor c = SQLObj.query("Glossary", cols, "PID = " + singleton.getSelectedProjectID() + " and CompanyID = " + singleton.getCompanyId() + " and GCID = " + GCID, null, null, null, "GName");
        return c;
    }

    public String generateOfflineEntryID(){
        opnToWrite();
        String newId = "";
        Cursor c = SQLObj.query("Entries", null, null, null, null, null, null);
        if (c != null ) {
            newId = "OF"+c.getCount();
        }
        Close();
        return newId;
    }

    public long updateProject(int PID, int hasData, int hasActivities) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("hasData", hasData);
        contentValues.put("hasActivities", hasActivities);
        opnToWrite();
        long val = SQLObj.update("Projects", contentValues, "PID = " + PID + " and CompanyID = " + singleton.getCompanyId() + " and UserID = " + singleton.getUserId() + " and AccountID = " + singleton.getAccountId() + " and SubID = " + singleton.getSubscriberId(), null);
        Close();
        return val;
    }

    public long updateTask(int PID, int TID, String ProjectDay) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("hasData", 1);
        opnToWrite();
        long val = SQLObj.update("Tasks", contentValues, "ProjectID = " + PID + " and ProjectDay = '" + ProjectDay + "' and TID = " + TID, null);
        Close();
        return val;
    }

    public long updateActivity(int TaskID, String ProjectDay, int ActID) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("hasData", 1);
        opnToWrite();
        long val = SQLObj.update("Activities", contentValues, "TaskID = " + TaskID + " and ProjectDay = '" + ProjectDay + "' and UserID = " + singleton.getUserId() + "and ActID = " + ActID, null);
        Close();
        return val;
    }

    public long updateEntry(String name, String trade_company, String classification_status, String hr_qty, String type, String action, String ID ) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("TRD_COMP", trade_company);
        contentValues.put("CLASSI_STAT", classification_status);
        contentValues.put("HR_QTY", Double.parseDouble(hr_qty));
        contentValues.put("ACTION", action);
        contentValues.put("TYPE", type);

        opnToWrite();
        long val = SQLObj.update("Entries", contentValues, "PID = " + singleton.getSelectedProjectID() + " and SubID = " + singleton.getSubscriberId() + " and AID = " + singleton.getSelectedActivityID() + " and DATE = '" + singleton.getCurrentSelectedDate() + "' and ID = '" + ID + "'", null);
        Close();
        return val;
    }

    public int deleteCredentials(String username, String password){
        opnToWrite();
        int val = SQLObj.delete("Login", "username = '" + username + "' and password = '" + password + "'", null);
        Close();
        return val;
    }

    public int deleteProjects() {
        opnToWrite();
        int val = SQLObj.delete("Projects", "UserID = " + singleton.getUserId() + " and AccountID = " + singleton.getAccountId() + " and SubID = " + singleton.getSubscriberId() + " and CompanyID = " + singleton.getCompanyId(), null);
        Close();
        return val;
    }

    public int deleteTasks(int PID) {
        opnToWrite();
        int val = SQLObj.delete("Tasks", "ProjectID = " + PID + " and ProjectDay = '" + singleton.getCurrentSelectedDate() + "'", null);
        Close();
        return val;
    }

    public int deleteActivities(int TaskID, String ProjectDay) {
        opnToWrite();
        int val = SQLObj.delete("Activities", "TaskID = " + TaskID + " and ProjectDay = '" + ProjectDay + "' and UserID = " + singleton.getUserId(), null);
        Close();
        return val;
    }

    public int deleteMyTaskActivities(int TaskID, String ProjectDay) {
        opnToWrite();
        int val = SQLObj.delete("Activities", "TaskID = " + TaskID + " and ProjectDay = '" + ProjectDay + "' and UserID = " + singleton.getUserId() + " and PID = " + singleton.getSelectedProjectID(), null);
        Close();
        return val;
    }

    public int deleteEntries(){
        opnToWrite();
        int val = SQLObj.delete("Entries", "PID = " + singleton.getSelectedProjectID() + " and SubID = " + singleton.getSubscriberId() + " and AID = "+ singleton.getSelectedActivityID() + " and AccountID = " + singleton.getAccountId() + " and DATE = '" + singleton.getCurrentSelectedDate() + "' and TYPE = 'N'", null);
        Close();
        return val;
    }

    public int deleteEntryByID( String ID) {
        opnToWrite();
        int val = SQLObj.delete("Entries", "ID = '" + ID + "' and PID = " + singleton.getSelectedProjectID() + " and TID = " + singleton.getSelectedTaskID() + " and AID = "+ singleton.getSelectedActivityID() + " and DATE = '" + singleton.getCurrentSelectedDate() + "'", null);
        Close();
        return val;
    }

    public int deleteGlossary(int GCID){
        opnToWrite();
        int val = SQLObj.delete("Glossary", "PID = " + singleton.getSelectedProjectID() + " and CompanyID = " + singleton.getCompanyId() + " and GCID = " + GCID, null);
        Close();
        return val;
    }
}