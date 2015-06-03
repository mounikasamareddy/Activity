package com.notevault.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkAdapter extends BroadcastReceiver {

public static Context context;
public static String SETTINGS_NETWORK = "settings_network";

@Override
public void onReceive(Context arg0, Intent arg1) {
	try {
		NetworkInfo networkInfo = (NetworkInfo) arg1.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		
		boolean currentState = false;
		
		if ((networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected()) || (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE	&& networkInfo.isConnected())) {
			currentState = true;
		} else {
			currentState = false;
		}	
		
		if(currentState == false){
		}else{
			if(currentState == true){
				/*Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});*/
				//thread.start();
			}else{
				System.out.println("Not Available");
			}
		}
	} catch (Exception ex) {
		ex.printStackTrace();
	}
}
}
