package com.notevault.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class TutorialFour extends Fragment {//implements OnGestureListener
	
	private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureScanner;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
               Bundle savedInstanceState) {
   // Inflate the layout containing a title and body text.
   ViewGroup rootView = (ViewGroup) inflater
                   .inflate(R.layout.splashscreen5, container, false);
   LinearLayout layout = (LinearLayout)rootView.findViewById(R.id.layout);// get your root  layout
   layout.setOnTouchListener(new OnTouchListener() {

           @Override
           public boolean onTouch(View v, MotionEvent event) {
               Log.v(null, "TOUCH EVENT"); // handle your fragment number here
               Intent intent;
               if (SettingActivity.navigatorFlag == false){
            	  // intent = new Intent(getActivity(), GettingStarted.class);
                  // startActivity(intent);
               }else if (SettingActivity.navigatorFlag == true) {
           		intent = new Intent(getActivity(), SettingActivity.class);
           		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   startActivity(intent);
                   getActivity().finish();
             
               }
               return false;
           }
       });
         return rootView;
       }
    
}