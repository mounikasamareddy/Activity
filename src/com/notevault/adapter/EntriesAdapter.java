package com.notevault.adapter;

import java.text.DecimalFormat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.notevault.activities.EntriesListByTypeActivity;
import com.notevault.activities.R;
import com.notevault.pojo.Singleton;

public class EntriesAdapter extends BaseAdapter{

	Context context;
	LayoutInflater infilate;
	Singleton singleton;
	
	public EntriesAdapter(Context context) {
		infilate=LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {

		return EntriesListByTypeActivity.MaterialData.size();
	}

	@Override
	public Object getItem(int arg0) {

		return EntriesListByTypeActivity.MaterialData.get(arg0);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@SuppressWarnings("null")
	@Override
	
	public View getView(final int position, View convertView, ViewGroup arg2) {

		UserHolder holder;
		singleton = Singleton.getInstance();
		
		if (convertView == null) {
			convertView = infilate.inflate(R.layout.entridetails, null);
			holder = new UserHolder();
			holder.hrs = (TextView) convertView.findViewById(R.id.hrs);
			holder.title = (LinearLayout) convertView.findViewById(R.id.title);
			holder.matirial = (TextView) convertView.findViewById(R.id.matirial);
			holder.units = (TextView) convertView.findViewById(R.id.units);
			convertView.setTag(holder);

		} else {
			holder = (UserHolder) convertView.getTag();

		}
		//Log.d("disable","---->"+Utilities.pdata.get(position).getHasActivities()+"  "+position+" "+Utilities.pdata.get(position).getPName());
		
		if(position==0)
		{
			holder.title.setVisibility(View.VISIBLE);
			Log.d("k val ","--->"+position);
			
			
		}
		else{
			Log.d("k val","--->"+position);
			holder.title.setVisibility(View.GONE);
		}
		holder.hrs.setText(EntriesListByTypeActivity.totalhours+"");
		holder.matirial.setText(EntriesListByTypeActivity.MaterialData.get(position)+"");
		
		double ratio = 0;
		if(EntriesListByTypeActivity.totalhours == 0){
			ratio = (double)EntriesListByTypeActivity.MaterialData.get(position)/1;
		}else{
			ratio = (double)EntriesListByTypeActivity.MaterialData.get(position)/(double)EntriesListByTypeActivity.totalhours;			
		}
		holder.units.setText(""+new DecimalFormat("0.0").format(ratio));
		return convertView;
	}

	static class UserHolder {
		TextView hrs, matirial, units;
		LinearLayout title;
	}

}
