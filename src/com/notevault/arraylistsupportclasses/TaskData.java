package com.notevault.arraylistsupportclasses;

import java.util.Comparator;

public class TaskData implements Comparable<TaskData> {
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

	public static class OrderByTName implements Comparator<TaskData> {

		
			@Override
			 public int compare(TaskData o1, TaskData o2) {
	            return o1.TName.compareTo(o2.TName);
	        }
	}
	

	/*
	 * Anohter implementation or Comparator interface to sort list of Order
	 * object based upon customer name.
	 */

	public TaskData(int tid2, String tName2, int hasData2, int Tidentty) {
		this.TID = tid2;
		this.TName = tName2;
		this.hasData = hasData;
		this.TIdentity=Tidentty;
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
	@Override
	public int compareTo(TaskData o) {
		return this.TID > o.TID ? 1 : (this.TID < o.TID ? -1 : 0);
	}

	/*
	 * implementing toString method to print orderId of Order
	 */
	@Override
	public String toString() {
		return String.valueOf(TID);
	}
}
