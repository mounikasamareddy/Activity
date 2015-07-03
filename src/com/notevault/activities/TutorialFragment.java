package com.notevault.activities;
 
import java.util.List;
import java.util.Vector;

import com.notevault.activities.FirstFragment;
import com.notevault.activities.SecondFragment;
import com.notevault.support.PagerAdapter1;
import com.notevault.pojo.Singleton;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.widget.Toast;

public class TutorialFragment extends FragmentActivity{
   
	Singleton singleton;
	public PagerAdapter1 mPagerAdapter;
    private static long back_pressed;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.splashscreen1);

        //initialsie the pager
        this.initializePaging();
    }
 
    private void initializePaging() {
 
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, FirstFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, SecondFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, ThirdFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, TutorialFour.class.getName()));
        fragments.add(Fragment.instantiate(this, GettingStarted.class.getName()));
        this.mPagerAdapter  = new PagerAdapter1(super.getSupportFragmentManager(), fragments);
        ViewPager pager = (ViewPager)super.findViewById(R.id.viewpager);
        pager.setAdapter(this.mPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()){
            // Need to cancel the toast here
            toast.cancel();
            // code for exit
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else {
            // Ask user to press back button one more time to close app.
            toast=  Toast.makeText(getBaseContext(), "Press once again to exit.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
        back_pressed = System.currentTimeMillis();
    }

}