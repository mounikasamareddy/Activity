package com.notevault.adapter;

import java.text.DecimalFormat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
		int k=0;
		if (convertView == null) {
			convertView= infilate.inflate(R.layout.entridetails, null);
			holder=new UserHolder();
			holder.hrs = (TextView) convertView.findViewById(R.id.hrs);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.matirial = (TextView) convertView.findViewById(R.id.matirial);
			holder.units = (TextView) convertView.findViewById(R.id.units);


			convertView.setTag(holder);

		} else {
			holder = (UserHolder) convertView.getTag();

		}
		//Log.d("disable","---->"+Utilities.pdata.get(position).getHasActivities()+"  "+position+" "+Utilities.pdata.get(position).getPName());
		double ratio;
		if(k==0)
		{
			holder.title.setVisibility(View.VISIBLE);
			k++;
		}
		else{
			holder.title.setVisibility(View.GONE);
		}
		holder.hrs.setText(EntriesListByTypeActivity.totalhours+"");
		holder.matirial.setText(EntriesListByTypeActivity.MaterialData.get(position)+"");
		if(EntriesListByTypeActivity.totalhours==0)
		{
			ratio=EntriesListByTypeActivity.MaterialData.get(position)/1;
		}else
		{
			ratio=EntriesListByTypeActivity.MaterialData.get(position)/EntriesListByTypeActivity.totalhours;
			
		
		}
		holder.units.setText(""+new DecimalFormat("00.000").format(ratio));
		return convertView;
	}

	static class UserHolder {
		TextView hrs,matirial,units,title;
		
	}

}
