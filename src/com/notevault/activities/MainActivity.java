package com.notevault.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
    private static long back_pressed;
    Toast toast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen1);
        
        /** Getting a reference to the ViewPager defined the layout file */        
        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        
        /** Getting fragment manager */
        FragmentManager fm = getSupportFragmentManager();
        
        /** Instantiating FragmentPagerAdapter */
        MyFragmentPagerAdapter pagerAdapter = new MyFragmentPagerAdapter(fm);
        
        /** Setting the pagerAdapter to the pager object */
        pager.setAdapter(pagerAdapter);
        
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
            toast =  Toast.makeText(getBaseContext(), "Press once again to exit.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }
        back_pressed = System.currentTimeMillis();
    }
}
