package com.notevault.arraylistsupportclasses;

import java.util.Comparator;



public  class ProjectData implements Comparable<ProjectData> {

	int PID;
	String PName;
	int hasData;
	int hasActivities;

	public static class OrderByPName implements Comparator<ProjectData> {

		@Override
		 public int compare(ProjectData o1, ProjectData o2) {
            return o1.PName.compareTo(o2.PName);
        }
	}
	

	/*
	 * Anohter implementation or Comparator interface to sort list of Order
	 * object based upon customer name.
	 */

	public ProjectData(int pid, String pName, int hasData, int hasActivities) {
		this.PID = pid;
		this.PName = pName;
		this.hasData = hasData;
		this.hasActivities = hasActivities;
		// TODO Auto-generated constructor stub
	}

	public int getPID() {
		return PID;
	}

	public void setPID(int pID) {
		PID = pID;
	}

	public String getPName() {
		return PName;
	}

	public void setPName(String pName) {
		PName = pName;
	}

	public int getHasData() {
		return hasData;
	}

	public void setHasData(int hasData) {
		this.hasData = hasData;
	}

	public int getHasActivities() {
		return hasActivities;
	}

	public void setHasActivities(int hasActivities) {
		this.hasActivities = hasActivities;
	}

	@Override
	public int compareTo(ProjectData o) {
		 return this.PID > o.PID ? 1 : (this.PID < o.PID ? -1 : 0);
	}

	/*
	 * implementing toString method to print orderId of Order
	 */
	@Override
	public String toString() {
		return String.valueOf(PName);
	}

}
