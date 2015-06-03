package com.notevault.datastorage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by HP on 1/26/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    public SQLiteDatabase DB;
    public static String DBName = "ActivitiesData";
    public static final int version = '1';
    public static Context currentContext;
    public final String PROJECTS_TABLE_SCRIPT = "CREATE TABLE IF NOT EXISTS Projects "   +
            "(  PID     INT(10),    PName       VARCHAR(20), " +
            "   CompanyID   INT(10),    UserID	INT(10),    AccountID	INT(10),    SubID   INT(10), " +
            "   hasData INT(1), hasActivities INT(1));";
    public final String TASKS_TABLE_SCRIPT = "CREATE TABLE IF NOT EXISTS Tasks "   +
            "(  TID         INT(10),    TName   VARCHAR(20)," +
            "   ProjectID   INT(10),    ProjectDay  VARCHAR(8),        hasData INT(1),status VARCHAR(8));";
    public final String ACTIVITIES_TABLE_SCRIPT = "CREATE TABLE IF NOT EXISTS Activities "  +
            "(  ActID   VARCHAR(10),    ActName     VARCHAR(20), " +
            "   TaskID  INT(10),    TaskName    VARCHAR(20), " +
            "   PID     INT(10),    ProjectDay  VARCHAR(8),     hasData	INT(1),  UserID INT(10));";
    public final String ENTRIES_TABLE_SCRIPT = "CREATE TABLE IF NOT EXISTS Entries "  +
            "(  NAME    VARCHAR(20),    TRD_COMP    VARCHAR(20),    CLASSI_STAT VARCHAR(20)," +
            "   HR_QTY  DOUBLE(10),     TYPE        VARCHAR(1), " +
            "   ACTION  VARCHAR(1),     ID          VARCHAR(10),    PID         INT(10),        TID         INT(10), " +
            "   UserID  INT(10),        SubID       INT(10),        AID         INT(10),        AccountID   INT(10), " +
            "   DATE        VARCHAR(8));";
    public final String LOGIN_TABLE_SCRIPT = "CREATE TABLE IF NOT EXISTS Login " +
            "(  username    VARCHAR(20),    password    VARCHAR(20), " +
            "   UserID      INT(10),        AccountID   INT(10),        SubID   INT(10), " +
            "   CompanyID   INT(10),        Company     VARCHAR(20), " +
            "   LNPCID      INT(10),        LTCID       INT(10),        LCCID   INT(10), " +
            "   ENCID       INT(10),        CCID        INT(10),        MNCID   INT(10));";

    public final String GLOSSARY_TABLE_SCRIPT = "CREATE TABLE IF NOT EXISTS Glossary " +
            "(  GCID         INT(10),        GName       VARCHAR(20),    PID    INT(10),    CompanyID   INT(10));";

    public static DBHelper dbHelper = null;

    public DBHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DBName, factory, version);
        currentContext = context;
        createDatabase();
    }

    public static DBHelper getDbHelper(Context c){
        if (dbHelper == null)
            dbHelper = new DBHelper(c, null);
        return dbHelper;
    }

    private void createDatabase() {
        DB = currentContext.openOrCreateDatabase(DBName, 0, null);
        DB.execSQL(PROJECTS_TABLE_SCRIPT);
        DB.execSQL(TASKS_TABLE_SCRIPT);
        DB.execSQL(ACTIVITIES_TABLE_SCRIPT);
        DB.execSQL(ENTRIES_TABLE_SCRIPT);
        DB.execSQL(LOGIN_TABLE_SCRIPT);
        DB.execSQL(GLOSSARY_TABLE_SCRIPT);
       //DB.execSQL("INSERT INTO Projects Values (1111, 'HOWDI!', 999, 7777777, 55555, 333, 1);");
    }

    private void dropDatabase(){
        DB.execSQL("DROP TABLE " + PROJECTS_TABLE_SCRIPT);
        DB.execSQL("DROP TABLE " + TASKS_TABLE_SCRIPT);
        DB.execSQL("DROP TABLE " + ACTIVITIES_TABLE_SCRIPT);
        DB.execSQL("DROP TABLE " + ENTRIES_TABLE_SCRIPT);
        DB.execSQL("DROP TABLE " + LOGIN_TABLE_SCRIPT);
        DB.execSQL("DROP TABLE " + GLOSSARY_TABLE_SCRIPT);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        dropDatabase();
        createDatabase();
    }
}
