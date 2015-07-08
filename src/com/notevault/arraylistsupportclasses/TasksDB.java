package com.notevault.arraylistsupportclasses;

public class TasksDB {
	int TID;
	String TName;
	int hasData;
	int TIdentity;
	 String Status;
	String Pid;
	
	
	public String getPid() {
		return Pid;
	}
	public void setPid(String pid) {
		Pid = pid;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public int getTIdentity() {
		return TIdentity;
	}
	public void setTIdentity(int tIdentity) {
		TIdentity = tIdentity;
	}
	public int getTID() {
		return TID;
	}
	public void setTID(int tID) {
		TID = tID;
	}
	public String getTName() {
		return TName;
	}
	public void setTName(String tName) {
		TName = tName;
	}
	public int getHasData() {
		return hasData;
	}
	public void setHasData(int hasData) {
		this.hasData = hasData;
	}
	
}
