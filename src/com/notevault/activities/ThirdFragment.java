package com.notevault.activities;
//import com.notevault.labor_by_notevault.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ThirdFragment extends Fragment {
	@Override
	   public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        if (container == null) {
	           
	            return null;
	        }
	        return (LinearLayout)inflater.inflate(R.layout.splashscreen4, container, false);
	        
	    }
	}