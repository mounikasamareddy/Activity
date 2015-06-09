package com.notevault.arraylistsupportclasses;

public class TasksDB {
	int TID;
	String TName;
	int hasData;
	int TIdentity;
	 
	
	
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
