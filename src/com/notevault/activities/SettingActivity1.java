package com.notevault.activities;

import com.notevault.pojo.Singleton;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class SettingActivity1 extends Fragment{

	 String values[] = {"About","Tutorial","Logout"};
	    private static ProgressDialog mProgressDialog;
	    ImageView backImageView;
	    static boolean navigatorFlag = false;
	    Singleton singleton;
	    SharedPreferences sharedpreferences, settingPreferences,settingshifttask,settingovertimetrack;
	    public static final String MyPREFERENCES = "MyPrefs" ;
	    public static final String EnableTaskPREFERENCES = "EnableTasks" ;
	    public static final String EnableSHIFTPREFERENCES = "EnableShift" ;
	    public static final String EnableOVERTIMETRACKPREFERENCES = "EnableovertimeShift" ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.setting_activity, container,false);
        singleton = Singleton.getInstance();
        settingPreferences = getActivity().getSharedPreferences(EnableTaskPREFERENCES, Context.MODE_PRIVATE);
       
        settingshifttask =getActivity().getSharedPreferences(EnableSHIFTPREFERENCES, Context.MODE_PRIVATE);
        settingovertimetrack = getActivity().getSharedPreferences(EnableOVERTIMETRACKPREFERENCES, Context.MODE_PRIVATE);
        
        Switch enableTasksSwitch = (Switch)v.findViewById(R.id.switch1);
        Switch enableShiftTrackingSwitch = (Switch)v.findViewById(R.id.switch2);
        Switch enableOvertimeTrackingSwitch = (Switch)v.findViewById(R.id.switch3);
        
        
        if(settingPreferences.contains(String.valueOf(singleton.getUserId()))){
            if(settingPreferences.getString(String.valueOf(singleton.getUserId()), "").equalsIgnoreCase("true")){
                enableTasksSwitch.setChecked(true);
              
              
            }
        }
        if(settingshifttask.contains(String.valueOf(singleton.getUserId()))){
            if(settingshifttask.getString(String.valueOf(singleton.getUserId()), "").equalsIgnoreCase("true")){
                
               enableShiftTrackingSwitch.setChecked(true);
              
            }
        }
                  
        if(settingovertimetrack.contains(String.valueOf(singleton.getUserId()))){
            if(settingovertimetrack.getString(String.valueOf(singleton.getUserId()), "").equalsIgnoreCase("true")){
                
            	 enableOvertimeTrackingSwitch.setChecked(true);
              
            }
        }
        enableTasksSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                singleton.setReloadPage(true);
                Editor taskEditor = settingPreferences.edit();
                if (isChecked) {
                    // The toggle is enabled
                    taskEditor.putString(String.valueOf(singleton.getUserId()), "true");
                    singleton.setEnableTasks(true);
                    Log.d("setenable","--->"+true);
                } else {
                    // The toggle is disabled
                    taskEditor.putString(String.valueOf(singleton.getUserId()), "false");
                    singleton.setEnableTasks(false);
                    Log.d("setenable","--->"+false);
                }
                taskEditor.apply();
            }
        });
        
        enableShiftTrackingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                singleton.setReloadPage(true);
                Editor taskEditor = settingshifttask.edit();
                if (isChecked) {
                    // The toggle is enabled
                    taskEditor.putString(String.valueOf(singleton.getUserId()), "true");
                    singleton.setEnableShiftTracking(true);
                    Log.d("shift","--->"+true);
                } else {
                    // The toggle is disabled
                    taskEditor.putString(String.valueOf(singleton.getUserId()), "false");
                    singleton.setEnableShiftTracking(false);
                    Log.d("shift","--->"+false);
                }
                taskEditor.apply();
            }
        });
        
        enableOvertimeTrackingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                singleton.setReloadPage(true);
                Editor taskEditor = settingovertimetrack.edit();
                if (isChecked) {
                    // The toggle is enabled
                    taskEditor.putString(String.valueOf(singleton.getUserId()), "true");
                    singleton.setEnableOvertimeTracking(true);
                    Log.d("track","--->"+true);
                } else {
                    // The toggle is disabled
                    taskEditor.putString(String.valueOf(singleton.getUserId()), "false");
                    singleton.setEnableOvertimeTracking(false);
                    Log.d("track","--->"+false);
                }
                taskEditor.apply();
            }
        });

        ListView settingListView = (ListView)v.findViewById(R.id.listView1);
        ArrayAdapter<String> ad = new ArrayAdapter<String>(getActivity(), R.layout.settingtextview, values);
        settingListView.setAdapter(ad);
        settingListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
            	Log.d("test","--->"+arg0.getItemAtPosition(arg2));
            	
                if(arg2 == 0){
                    Intent aboutIntent = new Intent(getActivity(), AboutActivity.class);
                    startActivity(aboutIntent);

                }else if(arg2 == 1){
                    SettingActivity.navigatorFlag = true;
                    Intent sentIntent = new Intent(getActivity(), TutorialFragment.class);
                    startActivity(sentIntent);
                }
                else if (arg2 == 2) {
                    sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    Editor editor = sharedpreferences.edit();
                    
                    editor.putString("loggedOut", "true");
                    editor.apply();
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("Confirm Logout");
                    alertDialog.setMessage("Are you sure you want to logout?");
                    //alertDialog.setIcon(R.drawable.logout);
                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,int which) {
                            dialog.dismiss();
                            mProgressDialog = new ProgressDialog(getActivity());
                            mProgressDialog.setMessage("Loading...");
                            mProgressDialog.setIndeterminate(false);
                            mProgressDialog.show();
                            mProgressDialog.dismiss();
                            Intent logoutIntent = new Intent(getActivity(), GettingStartedActivity.class);
                            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(logoutIntent);
                        }

                    });
                    // Setting Negative "cancel" Button
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.show();
                }
            }

			private SharedPreferences getSharedPreferences(
					String mypreferences, int modePrivate) {
				// TODO Auto-generated method stub
				return null;
			}
        });

        backImageView = (ImageView)v.findViewById(R.id.imageView1);
        backImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
            	 Intent intent= new Intent(getActivity(),ProjectListActivity.class);
            	 getActivity().startActivity(intent);
            }
        });
        return v;

       
    }

	

	
}
