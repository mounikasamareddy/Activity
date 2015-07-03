package com.notevault.activities;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;


import com.notevault.pojo.Singleton;

public class EntriesListActivity extends TabActivity implements OnTabChangeListener{

	Singleton singleton;
	TabHost tabHost;
	TextView tv ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitydetails);
		Bundle b = getIntent().getExtras();
	    int index = b.getInt("index");
	    Log.d("data","--->"+index);
	   

			
			
		
		singleton = Singleton.getInstance();
		TextView activityName = (TextView)findViewById(R.id.activity_txt);
		activityName.setText(singleton.getSelectedActivityName());

		TextView projectName = (TextView)findViewById(R.id.projectname_text);
		projectName.setText(singleton.getSelectedProjectName());

		TextView taskName = (TextView)findViewById(R.id.task_text);
        ImageView breadcrumb_separator = (ImageView)findViewById(R.id.breadcrumb_separator);
        if(singleton.isEnableTasks()){
            taskName.setText(singleton.getSelectedTaskName());
            breadcrumb_separator.setVisibility(View.VISIBLE);
            taskName.setVisibility(View.VISIBLE);
        }
		taskName.setText(singleton.getSelectedTaskName());

		TextView dateTextView = (TextView)findViewById(R.id.datetxt);
		dateTextView.setText(singleton.getCurrentSelectedDateFormatted());

		LinearLayout backLayout = (LinearLayout)findViewById(R.id.back_layout);
		backLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		 if(index==1){
		    	singleton.setNewEntryFlag(true);
					showActionSheet();
		    }
		LinearLayout addImageLayout = (LinearLayout)findViewById(R.id.image_layout);
		addImageLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
					singleton.setNewEntryFlag(true);
					showActionSheet();
				
				
			}
		});
		 
		// Get TabHost Refference
		tabHost = getTabHost();

		// Set TabChangeListener called when tab changed
		tabHost.setOnTabChangedListener(this);

		
		

		/************* TAB1 ************/
		// Create  Intents to launch an Activity for the tab (to be reused)
		Intent intentApple = new Intent().setClass(this, EntriesListByTypeActivity.class);
		TabSpec tabSpecApple = tabHost
		  .newTabSpec("first")
		  .setIndicator("Grouped")
		  .setContent(intentApple);
		
 

		

		/************* TAB2 ************/
		

		Intent intentAndroid = new Intent().setClass(this, EntriesListByDateActivity.class);
		TabSpec tabSpecAndroid = tabHost
		  .newTabSpec("second")
		  .setIndicator("Entered")
		  .setContent(intentAndroid);
		
		tabHost.addTab(tabSpecAndroid);
		tabHost.addTab(tabSpecApple);

		for(int i=1; i<tabHost.getTabWidget().getChildCount(); i++) 
		{
			TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
			tv.setTextColor(Color.parseColor("#FFFFFF"));
			tv.setTextSize(15);
		}
		// Set Tab1 as Default tab and change image  
		tabHost.getTabWidget().getChildTabViewAt(0).setBackgroundColor(Color.WHITE);
		tabHost.getTabWidget().getChildTabViewAt(1).setBackgroundDrawable(null);
		tabHost.setCurrentTab(1);
	}

    @Override
    public void onBackPressed() {
        singleton.setReloadPage(true);
        super.onBackPressed();
    }
	
	@Override
	public void onTabChanged(String arg0) {
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.GRAY); // unselected
			tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
			tv.setTextColor(Color.parseColor("#FFFFFF"));
			tv.setTextSize(15);
		}
		tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.WHITE); // selected
		tv = (TextView) tabHost.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
		tv.setTextColor(Color.parseColor("#666666"));
		tv.setTextSize(15);
	}

	public void showActionSheet() {
		final Dialog myDialog = new Dialog(EntriesListActivity.this);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		myDialog.setContentView(R.layout.actionsheet);
		LinearLayout laborLayout = (LinearLayout)myDialog.findViewById(R.id.labor_linear);
		laborLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				singleton.setSelectedLaborName("");
				singleton.setSelectedLaborTrade("");
				singleton.setSelectedLaborClassification("");
				singleton.setSelectedLaborHours("");
				singleton.setSelectedLaborDescription("");
				Intent intent = new Intent(EntriesListActivity.this, AddLabor.class);
				startActivity(intent);
				myDialog.dismiss();
			}
		});

		LinearLayout equipmentLayout = (LinearLayout)myDialog.findViewById(R.id.equipment_linear);
		equipmentLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				singleton.setSelectedEquipmentName("");
				singleton.setSelectedEquipmentCompany("");
				singleton.setSelectedEquipmentStatus("");
				singleton.setSelectedEquipmentQty("");
				singleton.setSelectedEquipmentDescription("");
				Intent intent = new Intent(EntriesListActivity.this, AddEquipment.class);
				startActivity(intent);
				myDialog.dismiss();
			}
		});

		LinearLayout materialLayout = (LinearLayout)myDialog.findViewById(R.id.material_linear);
		materialLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				singleton.setSelectedMaterialName("");
				singleton.setSelectedMaterialCompany("");
				singleton.setSelectedMaterialStatus("");
				singleton.setSelectedMaterialQty("");
				singleton.setSelectedMaterialDescription("");
				Intent intent = new Intent(EntriesListActivity.this, AddMaterial.class);
				startActivity(intent);
				myDialog.dismiss();
			}
		});

		TextView tvCancel = (TextView) myDialog.findViewById(R.id.cancel_textView4);
		tvCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog.dismiss();
			}
		});

		myDialog.getWindow().getAttributes().windowAnimations = R.anim.popup_show;
		myDialog.show();
		myDialog.getWindow().setGravity(Gravity.BOTTOM);
	}
}