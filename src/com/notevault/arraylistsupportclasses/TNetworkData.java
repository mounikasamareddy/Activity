package com.notevault.arraylistsupportclasses;

public class TNetworkData {
	int TID;
	String TName;
	int hasData;
	int TIdentity;
	String status;
	int ProjectId;
	String ProjectDate;

	public int getProjectId() {
		return ProjectId;
	}

	public void setProjectId(int projectId) {
		ProjectId = projectId;
	}

	public String getProjectDate() {
		return ProjectDate;
	}

	public void setProjectDate(String projectDate) {
		ProjectDate = projectDate;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
