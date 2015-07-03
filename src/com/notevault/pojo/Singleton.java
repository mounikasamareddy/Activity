package com.notevault.pojo;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;

public class Singleton {

    private static Singleton _singleton = null;
    private int companyId;
    private String companyName;
    private int accountId;
    private int userId;
    private int roleId;
    private int subscriberId;

    private int selectedProjectID;
    private String selectedProjectName;
    private int selectedTaskID;
    private String selectedTaskName;
    private int selectedActivityID;
    private String selectedActivityName;
    private String currentSelectedDate;				//(real date for api calls)
    private String currentSelectedDateFormatted;	//(formatted date for displaying in activity.)
    private int currentSelectedEntryID;
    private String currentSelectedEntryType;
    private String toDate;
    private int LNCID, LTCID, LCCID, ENCID, CCID, ESCID, MNCID, MSCID;

    private boolean loggedOut = true;
    private boolean online;
    private boolean reloadPage;
    private boolean EnableShiftTracking;
    private boolean EnableOvertimeTracking;
    private String ResentShiftItem;
private int selectedEntriesIdentityoffline;
    public int getSelectedEntriesIdentityoffline() {
	return selectedEntriesIdentityoffline;
}

public void setSelectedEntriesIdentityoffline(int selectedEntriesIdentityoffline) {
	this.selectedEntriesIdentityoffline = selectedEntriesIdentityoffline;
}

	public String getResentShiftItem() {
		return ResentShiftItem;
	}

	public void setResentShiftItem(String resentShiftItem) {
		ResentShiftItem = resentShiftItem;
	}

	public boolean isEnableOvertimeTracking() {
		return EnableOvertimeTracking;
	}

	public void setEnableOvertimeTracking(boolean enableOvertimeTracking) {
		EnableOvertimeTracking = enableOvertimeTracking;
	}

	public boolean isEnableShiftTracking() {
		return EnableShiftTracking;
	}

	public void setEnableShiftTracking(boolean enableShiftTracking) {
		EnableShiftTracking = enableShiftTracking;
	}

	private boolean newEntryFlag;
    int selectedTaskIdentityoffline;
    int selectedActivityIdentityoffline;

    public boolean isSyncingToServer() {
        return syncingToServer;
    }

    public void setSyncingToServer(boolean syncingToServer) {
        this.syncingToServer = syncingToServer;
    }

    private boolean syncingToServer;
    private  boolean offlineEntry;
    private boolean enableTasks;
    private String HTTPResponse;
    private int HTTPResponseStatusCode;

    private String selectedLaborName;
    private String selectedLaborTrade;
    private String selectedLaborClassification;
    private String selectedLaborHours;
    private String selectedLaborDescription;
    private String selectedEquipmentName;
    private String selectedEquipmentCompany;
    private String selectedEquipmentStatus;
    private String selectedEquipmentQty;
    private String selectedEquipmentDescription;
    private String selectedMaterialName;
    private String selectedMaterialCompany;
    private String selectedMaterialStatus;
    private String selectedMaterialQty;
    private String selectedMaterialDescription;
    private int selectedEntityIdentity;

    private HashMap<Integer, String> projectsList = new HashMap<Integer, String>();
    private HashMap<String, String> activitiesList = new HashMap<String, String>();
    private HashMap<Integer, String>taskList = new HashMap<Integer, String>();

    private Singleton(){
        this.setCompanyName(null);
        this.setAccountId(0);
        this.setUserId(0);
        this.setCompanyId(0);
    }

    public static Singleton getInstance() {
        if (_singleton == null) {
            _singleton = new Singleton();
        }
        return _singleton;
    }

    public boolean isOfflineEntry() {
        return offlineEntry;
    }

    public void setOfflineEntry(boolean offlineEntry) {
        this.offlineEntry = offlineEntry;
    }

    public boolean isNewEntryFlag() {
        return newEntryFlag;
    }

    public void setNewEntryFlag(boolean newEntryFlag) {
        this.newEntryFlag = newEntryFlag;
    }

    public int getHTTPResponseStatusCode() {
        return HTTPResponseStatusCode;
    }

    public void setHTTPResponseStatusCode(int hTTPResponseStatusCode) {
        HTTPResponseStatusCode = hTTPResponseStatusCode;
    }

    public String getHTTPResponse() {
        return HTTPResponse;
    }

    public void setHTTPResponse(String hTTPResponse) {
        HTTPResponse = hTTPResponse;
    }

    public String getSelectedLaborName() {
        return selectedLaborName;
    }

    public void setSelectedLaborName(String selectedLaborName) {
        this.selectedLaborName = selectedLaborName;
    }

    public String getSelectedLaborTrade() {
        return selectedLaborTrade;
    }

    public void setSelectedLaborTrade(String selectedLaborTrade) {
        this.selectedLaborTrade = selectedLaborTrade;
    }

    public String getSelectedLaborClassification() {
        return selectedLaborClassification;
    }

    public void setSelectedLaborClassification(String selectedLaborClassification) {
        this.selectedLaborClassification = selectedLaborClassification;
    }

    public String getSelectedLaborHours() {
        return selectedLaborHours;
    }

    public void setSelectedLaborHours(String selectedLaborHours) {
        this.selectedLaborHours = selectedLaborHours;
    }

    public String getSelectedLaborDescription() {
        return selectedLaborDescription;
    }

    public void setSelectedLaborDescription(String selectedLaborDescription) {
        this.selectedLaborDescription = selectedLaborDescription;
    }

    public String getSelectedEquipmentName() {
        return selectedEquipmentName;
    }

    public void setSelectedEquipmentName(String selectedEquipmentName) {
        this.selectedEquipmentName = selectedEquipmentName;
    }

    public String getSelectedEquipmentCompany() {
        return selectedEquipmentCompany;
    }

    public void setSelectedEquipmentCompany(String selectedEquipmentCompany) {
        this.selectedEquipmentCompany = selectedEquipmentCompany;
    }

    public String getSelectedEquipmentStatus() {
        return selectedEquipmentStatus;
    }

    public void setSelectedEquipmentStatus(String selectedEquipmentStatus) {
        this.selectedEquipmentStatus = selectedEquipmentStatus;
    }

    public String getSelectedEquipmentQty() {
        return selectedEquipmentQty;
    }

    public void setSelectedEquipmentQty(String selectedEquipmentQty) {
        this.selectedEquipmentQty = selectedEquipmentQty;
    }

    public String getSelectedEquipmentDescription() {
        return selectedEquipmentDescription;
    }

    public void setSelectedEquipmentDescription(String selectedEquipmentDescription) {
        this.selectedEquipmentDescription = selectedEquipmentDescription;
    }

    public String getSelectedMaterialName() {
        return selectedMaterialName;
    }

    public void setSelectedMaterialName(String selectedMaterialName) {
        this.selectedMaterialName = selectedMaterialName;
    }

    public String getSelectedMaterialCompany() {
        return selectedMaterialCompany;
    }

    public void setSelectedMaterialCompany(String selectedMaterialCompany) {
        this.selectedMaterialCompany = selectedMaterialCompany;
    }

    public String getSelectedMaterialStatus() {
        return selectedMaterialStatus;
    }

    public void setSelectedMaterialStatus(String selectedMaterialStatus) {
        this.selectedMaterialStatus = selectedMaterialStatus;
    }

    public String getSelectedMaterialQty() {
        return selectedMaterialQty;
    }

    public void setSelectedMaterialQty(String selectedMaterialQty) {
        this.selectedMaterialQty = selectedMaterialQty;
    }

    public String getSelectedMaterialDescription() {
        return selectedMaterialDescription;
    }

    public void setSelectedMaterialDescription(String selectedMaterialDescription) {
        this.selectedMaterialDescription = selectedMaterialDescription;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(int subscriberId) {
        this.subscriberId = subscriberId;
    }

    public int getLNCID() {
        return LNCID;
    }

    public void setLNCID(int lNCID) {
        LNCID = lNCID;
    }

    public int getLTCID() {
        return LTCID;
    }

    public void setLTCID(int lTCID) {
        LTCID = lTCID;
    }

    public int getLCCID() {
        return LCCID;
    }

    public void setLCCID(int lCCID) {
        LCCID = lCCID;
    }

    public int getENCID() {
        return ENCID;
    }

    public void setENCID(int eNCID) {
        ENCID = eNCID;
    }

    public int getCCID() {
        return CCID;
    }

    public void setCCID(int cCID) {
        CCID = cCID;
    }

    public int getESCID() {
        return ESCID;
    }

    public void setESCID(int eSCID) {
        ESCID = eSCID;
    }

    public int getMNCID() {
        return MNCID;
    }

    public void setMNCID(int mNCID) {
        MNCID = mNCID;
    }

    public int getMSCID() {
        return MSCID;
    }

    public void setMSCID(int mSCID) {
        MSCID = mSCID;
    }

    public HashMap<Integer, String> getProjectsList() {
        return projectsList;
    }

    public void setProjectsList(HashMap<Integer, String> projectsList) {
        this.projectsList = projectsList;
    }

    public HashMap<String, String> getActivitiesList() {
        return activitiesList;
    }

    public void setActivitiesList(HashMap<String, String> activitiesList) {
        this.activitiesList = activitiesList;
    }

    public HashMap<Integer, String> getTaskList() {
        return taskList;
    }

    public void setTaskList(HashMap<Integer, String> taskList) {
        this.taskList = taskList;
    }

    public int getSelectedProjectID() {
        return selectedProjectID;
    }

    public void setSelectedProjectID(int selectedProjectID) {
        this.selectedProjectID = selectedProjectID;
    }

    public String getSelectedProjectName() {
        return selectedProjectName;
    }

    public void setSelectedProjectName(String selectedProjectName) {
        this.selectedProjectName = selectedProjectName;
    }

    public int getSelectedTaskID() {
        return selectedTaskID;
    }

    public void setSelectedTaskID(int selectedTaskID) {
        this.selectedTaskID = selectedTaskID;
    }

    public String getSelectedTaskName() {
        return selectedTaskName;
    }

    public void setSelectedTaskName(String selectedTaskName) {
        this.selectedTaskName = selectedTaskName;
    }

    public int getSelectedActivityID() {
        return selectedActivityID;
    }

    public void setSelectedActivityID(int selectedActivityID) {
        this.selectedActivityID = selectedActivityID;
    }

    public String getSelectedActivityName() {
        return selectedActivityName;
    }

    public void setSelectedActivityName(String selectedActivityName) {
        this.selectedActivityName = selectedActivityName;
    }

    public String getCurrentSelectedDate() {
        return currentSelectedDate;
    }

    public void setCurrentSelectedDate(String currentSelectedDate) {
        this.currentSelectedDate = currentSelectedDate;
    }

    public String getCurrentSelectedDateFormatted() {
        return currentSelectedDateFormatted;
    }

    public void setCurrentSelectedDateFormatted(String currentSelectedDateFormatted) {
        this.currentSelectedDateFormatted = currentSelectedDateFormatted;
    }

    public String getCurrentSelectedEntryType() {
        return currentSelectedEntryType;
    }

    public void setCurrentSelectedEntryType(String currentSelectedEntryType) {
        this.currentSelectedEntryType = currentSelectedEntryType;
    }

    public int getCurrentSelectedEntryID() {
        return currentSelectedEntryID;
    }

    public void setCurrentSelectedEntryID(int currentSelectedEntryID) {
        this.currentSelectedEntryID = currentSelectedEntryID;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public int getAccountId() {
        return accountId;
    }
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getCompanyId() {
        return companyId;
    }
    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    //Check if passed String is a valid number.
    public static boolean isNUmeric(String s) {
        try{
            Double.parseDouble(s);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
    
    public boolean isReloadPage() {
		return reloadPage;
	}

	public void setReloadPage(boolean reloadPage) {
		this.reloadPage = reloadPage;
	}

    public boolean isLoggedOut() {
        return loggedOut;
    }

    public void setLoggedOut(boolean loggedOut) {
        this.loggedOut = loggedOut;
    }

    public boolean isEnableTasks() {
        return enableTasks;
    }

    public void setEnableTasks(boolean enableTasks) {
        this.enableTasks = enableTasks;
    }

    //Format number to strip unnecessary '.' & '0' at the end
    public static String prettyFormat(String s){
        DecimalFormat df = new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340);
        return df.format(Double.parseDouble(s)).toString();
    }
    
    public static String toTitleCase(String words){
    	String tCasedWord = "";
    	for(String word : words.split(" ")){
    		tCasedWord += (tCasedWord.equals("")?"":" ") + Character.toUpperCase(word.charAt(0))+word.substring(1, word.length());
    	}
    	return tCasedWord;
    }

	

	public int getSelectedTaskIdentityoffline() {
		return selectedTaskIdentityoffline;
	}



	public void setselectedTaskIdentityoffline(int tIdentity) {
		this.selectedTaskIdentityoffline = tIdentity;
		
	}

	public void setselectedActivityIdentityoffline(int aIdentity) {
		
		this.selectedActivityIdentityoffline = aIdentity;
	}

	public int getselectedActivityIdentityoffline() {
		
		return selectedActivityIdentityoffline;
	}

	public void setSelectedEntityIdentity(int eIdentity) {
		this.selectedEntityIdentity=eIdentity;
		
	}
public int getSelectedEntityIdentity() {
		
		return selectedEntityIdentity;
	}


	
}
