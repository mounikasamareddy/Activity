package com.notevault.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter{
	
	final int PAGE_COUNT = 5;

	/** Constructor of the class */
	public MyFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	/** This method will be invoked when a page is requested to create */
	 @Override
	    public Fragment getItem(int i) {

	        Fragment frag = null;

	        switch (i) {
	        case 0:
	            frag = new FirstFragment();
	            break;
	        case 1:
	            frag = new SecondFragment();
	            break;
	        case 2:
	            frag = new ThirdFragment();
	            break;
	        case 3:
	            frag = new FourthFragment();
	            break;
	        case 4:
	            frag = new GettingStarted();
	            break;

	        }
	        return frag;
	    }

	/** Returns the number of pages */
	@Override
	public int getCount() {		
		return PAGE_COUNT;
	}
	

}
