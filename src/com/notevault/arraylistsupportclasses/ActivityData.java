package com.notevault.arraylistsupportclasses;

import java.util.Comparator;

public class ActivityData {

	String AName;
	int Hasdata;
	int AId;
	int AIdentity;
	int Tid;
	String TDate;

	

	public ActivityData(int aIdentity2, int aId2, String aName2, int hasdata2,
			int tid2) {
		this.AIdentity = aIdentity2;
		this.AId = aId2;
		this.AName = aName2;
		this.Hasdata = hasdata2;
		this.Tid = tid2;
	}

	public static class OrderByAName implements Comparator<ActivityData> {

		@Override
		 public int compare(ActivityData o1, ActivityData o2) {
            return o1.AName.compareTo(o2.AName);
        }
	}
	public String getTDate() {
		return TDate;
	}

	public void setTDate(String tDate) {
		TDate = tDate;
	}

	public int getTid() {
		return Tid;
	}

	public void setTid(int tid) {
		Tid = tid;
	}

	public int getAIdentity() {
		return AIdentity;
	}

	public void setAIdentity(int aIdentity) {
		AIdentity = aIdentity;
	}

	public int getAId() {
		return AId;
	}

	public void setAId(int aId) {
		AId = aId;
	}

	public String getAName() {
		return AName;
	}

	public void setAName(String aName) {
		AName = aName;
	}

	public int getHasdata() {
		return Hasdata;
	}

	public void setHasdata(int hasdata) {
		Hasdata = hasdata;
	}
	public int compareTo(ActivityData o) {
		 return this.AId > o.AId ? 1 : (this.AId < o.AId ? -1 : 0);
	}

	/*
	 * implementing toString method to print orderId of Order
	 */
	@Override
	public String toString() {
		return String.valueOf(AName);
	}
}
