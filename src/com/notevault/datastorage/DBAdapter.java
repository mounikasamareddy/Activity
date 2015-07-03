package com.notevault.datastorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.notevault.arraylistsupportclasses.ActivityDB;
import com.notevault.arraylistsupportclasses.ActivityNetworkDB;
import com.notevault.arraylistsupportclasses.ClassDb;
import com.notevault.arraylistsupportclasses.ECmpany;
import com.notevault.arraylistsupportclasses.EName;
import com.notevault.arraylistsupportclasses.EStatus;
import com.notevault.arraylistsupportclasses.EntityDB;
import com.notevault.arraylistsupportclasses.EntriesNetworkDB;
import com.notevault.arraylistsupportclasses.MNameDb;
import com.notevault.arraylistsupportclasses.NAmeDb;
import com.notevault.arraylistsupportclasses.ProjectDB;
import com.notevault.arraylistsupportclasses.TaskNetworkDB;
import com.notevault.arraylistsupportclasses.TasksDB;
import com.notevault.arraylistsupportclasses.TradeDb;
import com.notevault.arraysupportclasses.CalenderActivity;

import com.notevault.pojo.Singleton;

/**
 * Created by HP on 1/26/2015.
 */
public class DBAdapter {
	SQLiteDatabase SQLObj;
	DBHelper helperObj;
	Context context;
	Singleton singleton;
	private static DBAdapter _dbAdapter = null;

	public DBAdapter(Context c) {
		context = c;
		singleton = Singleton.getInstance();
	}

	public static DBAdapter get_dbAdapter(Context c) {
		if (_dbAdapter == null)
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

	public long insertCredentials(String username, String password) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("username", username);
		contentValues.put("password", password);
		contentValues.put("UserID", singleton.getUserId());
		contentValues.put("AccountID", singleton.getAccountId());
		contentValues.put("SubID", singleton.getSubscriberId());
		contentValues.put("CompanyID", singleton.getCompanyId());
		contentValues.put("Company", singleton.getCompanyName());
		contentValues.put("LNPCID", singleton.getLNCID());
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

	public long insertProject(int PID, String PName, int hasData,
			int hasActivities) {
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

	public long insertTaskOffline(int TID, String TName, int ProjectID,
			int hasData, String status) {
		String offTname = TName + "@";
		ContentValues contentValues = new ContentValues();
		contentValues.put("TID", TID);
		contentValues.put("TName", offTname);
		contentValues.put("ProjectID", ProjectID);
		contentValues.put("ProjectDay", singleton.getCurrentSelectedDate());
		contentValues.put("hasData", hasData);
		contentValues.put("status", status);
		opnToWrite();
		long val = SQLObj.insert("Tasks", null, contentValues);
		Close();
		return val;

	}

	public long insertActivity(String ActID, String ActName, int TaskID,
			String ProjectDay, int hasData, int UserID) {
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

	public long inserActivityoffline(int ActID, String ActName, int TaskID,
			int hasData, String status) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("ActID", ActID);
		contentValues.put("ActName", ActName + "@");
		contentValues.put("TaskID", TaskID);
		contentValues.put("TaskName", singleton.getSelectedTaskName());
		contentValues.put("PID", singleton.getSelectedProjectID());
		contentValues.put("ProjectDay", singleton.getCurrentSelectedDate());
		contentValues.put("hasData", hasData);
		contentValues.put("UserID", singleton.getUserId());
		contentValues.put("status", status);
		opnToWrite();
		Log.d("db", "--->" + status);
		long val = SQLObj.insert("Activities", null, contentValues);
		Close();
		return val;

	}

	public long insertEntry(String NAME, String TRD_COMP, String CLASSI_STAT,
			String HR_QTY, String TYPE, String ACTION, String ID) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("NAME", NAME);
		contentValues.put("TRD_COMP", TRD_COMP);
		contentValues.put("CLASSI_STAT", CLASSI_STAT);
		contentValues.put("HR_QTY", Double.parseDouble(HR_QTY));
		// contentValues.put("DESC", DESC);
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

	public long insertEntryOffline(String NAME, String TRD_COMP,
			String CLASSI_STAT, String HR_QTY, String TYPE, String ACTION,
			String ID, int Tid, int Aid, String status) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("NAME", NAME);
		contentValues.put("TRD_COMP", TRD_COMP);
		contentValues.put("CLASSI_STAT", CLASSI_STAT);
		contentValues.put("HR_QTY", Double.parseDouble(HR_QTY));
		// contentValues.put("DESC", DESC);
		contentValues.put("TYPE", TYPE);
		contentValues.put("ACTION", ACTION);
		contentValues.put("ID", ID);
		contentValues.put("PID", singleton.getSelectedProjectID());
		contentValues.put("TID", Tid);
		contentValues.put("AID", Aid);
		contentValues.put("UserID", singleton.getUserId());
		contentValues.put("AccountID", singleton.getAccountId());
		contentValues.put("SubID", singleton.getSubscriberId());
		contentValues.put("DATE", singleton.getCurrentSelectedDate());
		contentValues.put("status", status);
		opnToWrite();
		long val = SQLObj.insert("Entries", null, contentValues);
		Close();
		return val;

	}

	public long insertGlossary(int GCID, String GName) {
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

	public long insertGlossaryoffline(int GCID, String GName) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("GCID", GCID);
		contentValues.put("GName", GName);
		contentValues.put("PID", singleton.getSelectedProjectID());
		contentValues.put("CompanyID", singleton.getCompanyId());
		contentValues.put("status", "offline");
		opnToWrite();
		long val = SQLObj.insert("Glossary", null, contentValues);
		Close();
		return val;
	}

	/*
	 * public Cursor queryName() { String[] cols = { helperObj.KEY_ID,
	 * helperObj.FNAME, helperObj.LNAME }; opnToWrite(); Cursor c =
	 * SQLObj.query(TABLE_NAME, cols, null, null, null, null, null); return c; }
	 * 
	 * public Cursor queryAll(int nameId) { String[] cols = { helperObj.KEY_ID,
	 * helperObj.FNAME, helperObj.LNAME }; opnToWrite(); Cursor c =
	 * SQLObj.query(TABLE_NAME, cols, KEY_ID + "=" + nameId, null, null, null,
	 * null);
	 * 
	 * return c;
	 * 
	 * }
	 */

	public Cursor queryCredentials(String username, String password) {
		String[] cols = { "UserID", "AccountID", "SubID", "CompanyID",
				"Company", "LNPCID", "LTCID", "LCCID", "ENCID", "CCID", "MNCID" };
		opnToWrite();
		Cursor c = SQLObj
				.query("Login", cols, "username = '" + username
						+ "' and password = '" + password + "'", null, null,
						null, null);
		return c;
	}

	public Cursor queryProjects() {
		String[] cols = { "PID", "PName", "hasData", "hasActivities" };
		opnToWrite();
		Cursor c = SQLObj.query("Projects", cols,
				"CompanyID = " + singleton.getCompanyId() + " and UserID = "
						+ singleton.getUserId() + " and AccountID = "
						+ singleton.getAccountId() + " and SubID = "
						+ singleton.getSubscriberId(), null, null, null,
				"PName");
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
		Cursor c = SQLObj.query("Tasks", cols, "ProjectID = " + ProjectID
				+ " and ProjectDay = " + ProjectDay, null, null, null, "TName");
		return c;
	}

	public List<TasksDB> getAllTaskRecords(int pid) {

		// Select All Query
		opnToWrite();

		List<TasksDB> dataList = new ArrayList<TasksDB>();
		dataList.clear();
		String selectQuery = "SELECT  * FROM Tasks where ProjectId=" + pid;

		Cursor cursor = SQLObj.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				TasksDB data = new TasksDB();
				data.setTIdentity(cursor.getInt(0));
				data.setTID(cursor.getInt(1));
				data.setTName(cursor.getString(2));
				data.setHasData(cursor.getInt(5));
				data.setStatus(cursor.getString(6));
				dataList.add(data);

				// Adding contact to list

			} while (cursor.moveToNext());
		}

		// return contact list
		return dataList;
	}

	public List<TasksDB> getAllTaskexRecords() {

		// Select All Query
		opnToWrite();
		List<TasksDB> dataList = new ArrayList<TasksDB>();
		dataList.clear();
		String selectQuery = "SELECT  * FROM Tasks ";

		Cursor cursor = SQLObj.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				TasksDB data = new TasksDB();
				data.setTIdentity(cursor.getInt(0));
				data.setTID(cursor.getInt(1));
				data.setTName(cursor.getString(2));
				data.setHasData(cursor.getInt(5));
				data.setStatus(cursor.getString(6));
				dataList.add(data);

				// Adding contact to list

			} while (cursor.moveToNext());
		}

		// return contact list
		return dataList;
	}

	public List<TaskNetworkDB> getAllOfflineTaskRecords() {

		// Select All Query
		opnToWrite();
		List<TaskNetworkDB> dataList = new ArrayList<TaskNetworkDB>();

		String selectQuery = "SELECT  * FROM Tasks where status='offline'";

		Cursor cursor = SQLObj.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				TaskNetworkDB data = new TaskNetworkDB();
				data.setTIdentity(cursor.getInt(0));
				data.setTID(cursor.getInt(1));
				data.setTName(cursor.getString(2));
				data.setProjectId(cursor.getInt(3));
				data.setProjectDate(cursor.getString(4));
				data.setHasData(cursor.getInt(5));
				data.setStatus(cursor.getString(6));
				dataList.add(data);

				// Adding contact to list

			} while (cursor.moveToNext());
		}

		// return contact list
		return dataList;
	}

	public Cursor queryActivities(int TaskID, String ProjectDay, int UserID) {
		String[] cols = { "ActID", "ActName", "hasData" };
		opnToWrite();
		Cursor c = SQLObj.query("Activities", cols, "TaskID = " + TaskID
				+ " and ProjectDay = '" + ProjectDay + "' and UserID = "
				+ UserID, null, null, null, "ActName");
		return c;
	}

	public List<ActivityDB> getAllActivityRecords(int tid, String ProjectDay) {
		// Select All Query
		opnToWrite();
		List<ActivityDB> dataList = new ArrayList<ActivityDB>();
		dataList.clear();
		String selectQuery = "SELECT  * FROM Activities where TaskID=" + tid
				+ " and ProjectDay='" + ProjectDay + "' and hasData=1";

		Cursor cursor = SQLObj.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				ActivityDB data = new ActivityDB();
				data.setAIdentity(cursor.getInt(0));
				data.setAId(cursor.getInt(1));
				data.setAName(cursor.getString(2));
				data.setTid(cursor.getInt(3));
				data.setTDate(cursor.getString(6));
				data.setHasdata(cursor.getInt(7));

				dataList.add(data);

				// Adding contact to list

			} while (cursor.moveToNext());
		}

		// return contact list
		return dataList;

	}
	public List<ActivityDB> getAllActivityRecords() {
		// Select All Query
		opnToWrite();
		List<ActivityDB> dataList = new ArrayList<ActivityDB>();
		dataList.clear();
		String selectQuery = "SELECT  * FROM Activities";

		Cursor cursor = SQLObj.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				ActivityDB data = new ActivityDB();
				data.setAIdentity(cursor.getInt(0));
				data.setAId(cursor.getInt(1));
				data.setAName(cursor.getString(2));
				data.setTid(cursor.getInt(3));
				data.setTDate(cursor.getString(6));
				data.setHasdata(cursor.getInt(7));

				dataList.add(data);

				// Adding contact to list

			} while (cursor.moveToNext());
		}

		// return contact list
		return dataList;

	}
	public List<CalenderActivity> getAllActivityRecordsForCalender(int tid) {
		// Select All Query
		opnToWrite();
		List<CalenderActivity> dataList = new ArrayList<CalenderActivity>();
		String selectQuery = "SELECT  * FROM Activities where TaskID=" + tid;

		Cursor cursor = SQLObj.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		dataList.clear();
		if (cursor.moveToFirst()) {
			do {
				CalenderActivity data = new CalenderActivity();

				data.setAIdentity(cursor.getInt(0));
				data.setAId(cursor.getInt(1));
				data.setAName(cursor.getString(2));
				data.setTid(cursor.getInt(3));

				data.setHasdata(cursor.getInt(7));
				data.setDate(cursor.getString(6));

				dataList.add(data);

				// Adding contact to list

			} while (cursor.moveToNext());
		}

		// return contact list
		return dataList;

	}

	public List<ActivityNetworkDB> getAllOfflineActivityRecords() {
		opnToWrite();
		List<ActivityNetworkDB> dataList = new ArrayList<ActivityNetworkDB>();
		String selectQuery = "SELECT  * FROM Activities where status IN('offline')";
		dataList.clear();
		Cursor cursor = SQLObj.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				ActivityNetworkDB data = new ActivityNetworkDB();
				data.setAIdentity(cursor.getInt(0));
				data.setAId(cursor.getInt(1));
				data.setAName(cursor.getString(2));
				data.setTid(cursor.getInt(3));
				data.setADate(cursor.getString(6));
				data.setHasdata(cursor.getInt(7));
				data.setAstatus(cursor.getString(9));
				dataList.add(data);

				// Adding contact to list

			} while (cursor.moveToNext());
		}

		return dataList;
	}

	public List<EntriesNetworkDB> getAllOfflineEntriesRecords(String type) {
		opnToWrite();
		List<EntriesNetworkDB> dataList = new ArrayList<EntriesNetworkDB>();
		String selectQuery = "SELECT  * FROM Entries where status IN('offline')and TYPE='"
				+ type + "'";

		Cursor cursor = SQLObj.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				EntriesNetworkDB data = new EntriesNetworkDB();
				data.setEIdenty(cursor.getInt(0));
				data.setEname(cursor.getString(1));
				data.setTRD_C(cursor.getString(2));
				data.setClassesI(cursor.getString(3));
				data.setHR_QTY(cursor.getString(4));
				data.setType(cursor.getString(5));
				data.setAction(cursor.getString(6));
				data.setEId(cursor.getInt(7));
				data.setPID(cursor.getInt(8));
				data.setTid(cursor.getInt(9));
				data.setAid(cursor.getInt(12));
				data.setEdate(cursor.getString(14));
				data.setStatus(cursor.getString(15));
				dataList.add(data);

				// Adding contact to list

			} while (cursor.moveToNext());
		}

		return dataList;
	}

	public List<EntriesNetworkDB> getAllOfflineEntriesRecords() {
		opnToWrite();
		List<EntriesNetworkDB> dataList = new ArrayList<EntriesNetworkDB>();
		String selectQuery = "SELECT  * FROM Entries where status IN('offline')";

		Cursor cursor = SQLObj.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				EntriesNetworkDB data = new EntriesNetworkDB();
				data.setEIdenty(cursor.getInt(0));
				data.setEname(cursor.getString(1));
				data.setTRD_C(cursor.getString(2));
				data.setClassesI(cursor.getString(3));
				data.setHR_QTY(cursor.getString(4));
				data.setType(cursor.getString(5));
				data.setAction(cursor.getString(6));
				data.setEId(cursor.getInt(7));
				data.setPID(cursor.getInt(8));
				data.setTid(cursor.getInt(9));
				data.setAid(cursor.getInt(12));
				data.setEdate(cursor.getString(14));
				data.setStatus(cursor.getString(15));
				dataList.add(data);

				// Adding contact to list

			} while (cursor.moveToNext());
		}

		return dataList;
	}

	public Cursor queryMyTaskActivities(int TaskID, String ProjectDay,
			int UserID) {
		String[] cols = { "ActID", "ActName", "hasData" };
		opnToWrite();
		Cursor c = SQLObj.query("Activities", cols,
				"TaskName = 'My Task' and ProjectDay = '" + ProjectDay
						+ "' and UserID = " + UserID + " and PID = "
						+ singleton.getSelectedProjectID(), null, null, null,
				"ActName");
		return c;
	}

	public Cursor queryEntries() {
		String[] cols = { "TYPE", "NAME", "TRD_COMP", "CLASSI_STAT", "HR_QTY",
				"ID", "DATE" };
		opnToWrite();
		Cursor c = SQLObj.query("Entries", cols,
				"ACTION != 'D' and PID = " + singleton.getSelectedProjectID()
						+ " and SubID = " + singleton.getSubscriberId()
						+ " and AID = " + singleton.getSelectedActivityID()
						+ " and AccountID = " + singleton.getAccountId()
						+ " and DATE = '" + singleton.getCurrentSelectedDate()
						+ "'", null, null, null, "NAME");
		return c;
	}

	public List<EntityDB> getAllEntityRecords(int aid) {
		opnToWrite();
		List<EntityDB> dataList = new ArrayList<EntityDB>();
		String selectQuery = "SELECT  * FROM Entries where ACTION IN('N','U') and AID="
				+ aid;

		Cursor cursor = SQLObj.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				EntityDB data = new EntityDB();
				data.setEIdentity(cursor.getInt(0));
				data.setNAME(cursor.getString(1));
				data.setTRD_COMP(cursor.getString(2));
				data.setCLASSI_STAT(cursor.getString(3));
				data.setHR_QTY(cursor.getShort(4));
				data.setType(cursor.getString(5));
				data.setAction(cursor.getString(6));
				data.setID(cursor.getInt(7));

				dataList.add(data);

				// Adding contact to list

			} while (cursor.moveToNext());
		}

		// return contact list
		return dataList;
	}

	public List<EntityDB> getAllEntityRecordsByLName(String Lname) {
		opnToWrite();
		List<EntityDB> dataList = new ArrayList<EntityDB>();
		String selectQuery = "SELECT  * FROM Entries where ACTION IN('N','U') and NAME='"
				+ Lname + "'";

		Cursor cursor = SQLObj.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				EntityDB data = new EntityDB();
				data.setEIdentity(cursor.getInt(0));
				data.setNAME(cursor.getString(1));
				data.setTRD_COMP(cursor.getString(2));
				data.setCLASSI_STAT(cursor.getString(3));
				data.setHR_QTY(cursor.getShort(4));
				data.setType(cursor.getString(5));
				data.setAction(cursor.getString(6));
				data.setID(cursor.getInt(7));

				dataList.add(data);

				// Adding contact to list

			} while (cursor.moveToNext());
		}

		// return contact list
		return dataList;
	}

	public Cursor queryOfflineEntries() {
		String[] cols = { "TYPE", "NAME", "TRD_COMP", "CLASSI_STAT", "HR_QTY",
				"ID", "ACTION", "PID", "TID", "AID", "DATE", "UserID",
				"AccountID", "SubID" };
		opnToWrite();
		Cursor c = SQLObj.query("Entries", cols, "ACTION != 'N'", null, null,
				null, null);// and PID =
							// " + singleton.getSelectedProjectID() + " and
							// SubID = " + singleton.getSubscriberId() + " and
							// AID = " + singleton.getSelectedActivityID() + "
							// and AccountID = " + singleton.getAccountId() + "
							// and DATE = '" +
							// singleton.getCurrentSelectedDate() + "'"
		return c;
	}

	public List<NAmeDb> getAllnNameRecords(int i) {
		opnToWrite();
		List<NAmeDb> dataList = new ArrayList<NAmeDb>();
		String selectQuery = "SELECT  * FROM Glossary where GCID=" + i;

		Cursor cursor = SQLObj.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				NAmeDb data = new NAmeDb();
				data.setGCID(cursor.getInt(1));
				data.setGname(cursor.getString(2));

				dataList.add(data);

				// Adding contact to list

			} while (cursor.moveToNext());
		}

		// return contact list
		return dataList;
	}

	public List<TradeDb> getAllnTradeRecords(int ltcid) {
		opnToWrite();
		List<TradeDb> dataList = new ArrayList<TradeDb>();
		String selectQuery = "SELECT  * FROM Glossary where GCID=" + ltcid;

		Cursor cursor = SQLObj.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				TradeDb data = new TradeDb();

				data.setTname(cursor.getString(2));

				dataList.add(data);

				// Adding contact to list

			} while (cursor.moveToNext());
		}

		// return contact list
		return dataList;
	}

	public List<ClassDb> getAllnClassRecords(int lccid) {
		opnToWrite();
		List<ClassDb> dataList = new ArrayList<ClassDb>();
		String selectQuery = "SELECT  * FROM Glossary where GCID=" + lccid;

		Cursor cursor = SQLObj.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				ClassDb data = new ClassDb();

				data.setCName(cursor.getString(2));

				dataList.add(data);

				// Adding contact to list

			} while (cursor.moveToNext());
		}

		// return contact list
		return dataList;
	}

	public List<EName> getAllEnameRecords(int Encid) {
		opnToWrite();
		List<EName> dataList = new ArrayList<EName>();
		String selectQuery = "SELECT  * FROM Glossary where GCID=" + Encid;

		Cursor cursor = SQLObj.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				EName data = new EName();

				data.setEName(cursor.getString(2));

				dataList.add(data);

				// Adding contact to list

			} while (cursor.moveToNext());
		}

		// return contact list
		return dataList;
	}

	public List<ECmpany> getAllCCompanyRecords(int ccid) {
		opnToWrite();
		List<ECmpany> dataList = new ArrayList<ECmpany>();
		String selectQuery = "SELECT  * FROM Glossary where GCID=" + ccid;

		Cursor cursor = SQLObj.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				ECmpany data = new ECmpany();

				data.setECmpany(cursor.getString(2));

				dataList.add(data);

				// Adding contact to list

			} while (cursor.moveToNext());
		}

		// return contact list
		return dataList;
	}

	public List<EStatus> getAllCStatusRecords(int Escid) {
		opnToWrite();
		List<EStatus> dataList = new ArrayList<EStatus>();
		String selectQuery = "SELECT  * FROM Glossary where GCID=" + Escid;

		Cursor cursor = SQLObj.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				EStatus data = new EStatus();

				data.setEstatus(cursor.getString(2));

				dataList.add(data);

				// Adding contact to list

			} while (cursor.moveToNext());
		}

		// return contact list
		return dataList;
	}

	public List<MNameDb> getAllMNameRecords(int mncid) {
		opnToWrite();
		List<MNameDb> dataList = new ArrayList<MNameDb>();
		String selectQuery = "SELECT  * FROM Glossary where GCID=" + mncid;

		Cursor cursor = SQLObj.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				MNameDb data = new MNameDb();

				data.setMName(cursor.getString(2));

				dataList.add(data);

				// Adding contact to list

			} while (cursor.moveToNext());
		}

		// return contact list
		return dataList;
	}

	public String generateOfflineEntryID() {
		opnToWrite();
		String newId = "";
		Cursor c = SQLObj.query("Entries", null, null, null, null, null, null);
		if (c != null) {
			newId = "OF" + c.getCount();
		}
		Close();
		return newId;
	}

	public long updateProject(int PID, int hasData, int hasActivities) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("hasData", hasData);
		contentValues.put("hasActivities", hasActivities);
		opnToWrite();
		long val = SQLObj.update("Projects", contentValues, "PID = " + PID
				+ " and CompanyID = " + singleton.getCompanyId()
				+ " and UserID = " + singleton.getUserId()
				+ " and AccountID = " + singleton.getAccountId()
				+ " and SubID = " + singleton.getSubscriberId(), null);
		Close();
		return val;
	}

	public long updateTask(int PID, int TID, String ProjectDay) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("hasData", 1);
		opnToWrite();
		long val = SQLObj.update("Tasks", contentValues, "ProjectID = " + PID
				+ " and ProjectDay = '" + ProjectDay + "' and TID = " + TID,
				null);
		Close();
		return val;
	}

	public long updateActivity(int Aidentity) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("hasData", 0);
		opnToWrite();
		long val = SQLObj.update("Activities", contentValues, "AIdentity = "
				+ Aidentity, null);
		Close();
		return val;
	}

	public long updateTaskOnBC(int TIdentity, int TID, String Tname) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("TID", TID);
		contentValues.put("TName", Tname);
		contentValues.put("status", "");
		opnToWrite();

		long val = SQLObj.update("Tasks", contentValues, "TIdentity = "
				+ TIdentity, null);
		Close();
		return val;
	}

	public long updateActivityTaskIdOnBC(int TIdentity, int TID) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("TaskID", TID);
		contentValues.put("status", "offline");
		opnToWrite();
		long val = SQLObj.update("Activities", contentValues, "TaskID = "
				+ TIdentity + " and status='offline'", null);
		Close();
		return val;
	}

	public long updateEntityTaskIdOnBC(int TIdentity, int TID) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("TID", TID);
		contentValues.put("status", "offline");
		opnToWrite();
		long val = SQLObj.update("Entries", contentValues, "TID = " + TIdentity
				+ " and status='offline'", null);
		Close();
		return val;
	}

	public long updateActivityActivityIdOnBC(int AIdentity, int AID,
			String Aname) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("ActID", AID);
		contentValues.put("ActNAme", Aname);
		contentValues.put("status", "");
		opnToWrite();
		long val = SQLObj.update("Activities", contentValues, "AIdentity = "
				+ AIdentity, null);
		Close();
		return val;
	}

	public long updateEntityActivityIdOnBC(int AIdentity, int AID) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("AID", AID);
		contentValues.put("status", "offline");
		opnToWrite();
		long val = SQLObj.update("Entries", contentValues, "AID = " + AIdentity
				+ " and status='offline'", null);
		Close();
		return val;
	}

	public long updateEntriesEid(int EIdentity, String EID) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("ID", EID);
		contentValues.put("status", "");
		opnToWrite();
		long val = SQLObj.update("Entries", contentValues, "EIdentity = "
				+ EIdentity + " and status='offline'", null);
		Close();
		return val;
	}

	public long updateActivity(int TaskID, int ActID) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("hasData", 1);
		opnToWrite();
		long val = SQLObj.update(
				"Activities",
				contentValues,
				"TaskID = " + TaskID + " and ProjectDay = '"
						+ singleton.getCurrentSelectedDate()
						+ "' and  ActID = " + ActID, null);
		Close();
		return val;
	}

	public long updateEntry(String name, String trade_company,
			String classification_status, String hr_qty, String type,
			String action, String ID) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("EIdentity", singleton.getSelectedEntityIdentity());
		contentValues.put("NAME", name);
		contentValues.put("TRD_COMP", trade_company);
		contentValues.put("CLASSI_STAT", classification_status);
		contentValues.put("HR_QTY", hr_qty);
		contentValues.put("ACTION", action);
		contentValues.put("TYPE", type);

		opnToWrite();
		long val = SQLObj.update("Entries", contentValues,
				"PID = " + singleton.getSelectedProjectID() + " and SubID = "
						+ singleton.getSubscriberId() + " and AID = "
						+ singleton.getSelectedActivityID() + " and DATE = '"
						+ singleton.getCurrentSelectedDate() + "' and ID = '"
						+ ID + "'", null);
		Close();
		return val;
	}

	public int updateEntryOffline(int Eidentity, String action) {

		ContentValues contentValues = new ContentValues();

		contentValues.put("ACTION", action);

		opnToWrite();
		int val = SQLObj.update("Entries", contentValues, "EIdentity= "
				+ Eidentity, null);
		Close();
		return val;
	}

	public int deleteCredentials(String username, String password) {
		opnToWrite();
		int val = SQLObj.delete("Login", "username = '" + username
				+ "' and password = '" + password + "'", null);
		Close();
		return val;
	}

	public int deleteProjects() {
		opnToWrite();
		int val = SQLObj.delete("Projects", "UserID = " + singleton.getUserId()
				+ " and AccountID = " + singleton.getAccountId()
				+ " and SubID = " + singleton.getSubscriberId()
				+ " and CompanyID = " + singleton.getCompanyId(), null);
		Close();
		return val;
	}

	public int deleteTasks(int PID) {
		opnToWrite();
		int val = SQLObj.delete("Tasks", "ProjectID = " + PID
				+ " and ProjectDay = '" + singleton.getCurrentSelectedDate()
				+ "'", null);
		Close();
		return val;
	}

	public int deleteActivities(int TaskID, String ProjectDay) {
		opnToWrite();
		int val = SQLObj.delete("Activities", "TaskID = " + TaskID
				+ " and ProjectDay = '" + ProjectDay + "' and UserID = "
				+ singleton.getUserId(), null);
		Close();
		return val;
	}

	public int deleteMyTaskActivities(int TaskID, String ProjectDay) {
		opnToWrite();
		int val = SQLObj.delete("Activities",
				"TaskID = " + TaskID + " and ProjectDay = '" + ProjectDay
						+ "' and UserID = " + singleton.getUserId()
						+ " and PID = " + singleton.getSelectedProjectID(),
				null);
		Close();
		return val;
	}

	public int deleteEntries() {
		opnToWrite();

		int val = SQLObj.delete("Entries",
				" AID = " + singleton.getSelectedActivityID(), null);
		Close();
		return val;
	}

	public int deleteEntryByID(String ID) {
		opnToWrite();
		int val = SQLObj.delete(
				"Entries",
				"ID = '" + ID + "' and PID = "
						+ singleton.getSelectedProjectID() + " and TID = "
						+ singleton.getSelectedTaskID() + " and AID = "
						+ singleton.getSelectedActivityID() + " and DATE = '"
						+ singleton.getCurrentSelectedDate() + "'", null);
		Close();
		return val;
	}

	public int deleteEntryByIDOffline(int ID) {
		opnToWrite();
		int val = SQLObj.delete("Entries", "EIdentity = " + ID, null);
		Close();
		return val;
	}

	public int deleteGlossary(int GCID) {
		opnToWrite();
		int val = SQLObj.delete("Glossary",
				"PID = " + singleton.getSelectedProjectID()
						+ " and CompanyID = " + singleton.getCompanyId()
						+ " and GCID = " + GCID, null);
		Close();
		return val;
	}

}