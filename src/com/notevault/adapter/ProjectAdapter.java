package com.notevault.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.notevault.activities.ActivitiesListActivity;
import com.notevault.activities.R;
import com.notevault.activities.TasksListActivity;
import com.notevault.pojo.Singleton;
import com.notevault.support.Utilities;

public class ProjectAdapter extends BaseAdapter {

	Context context;
	LayoutInflater infilate;
	Singleton singleton;

	public ProjectAdapter(Context context) {
		infilate=LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {

		return Utilities.pdata.size();
	}

	@Override
	public Object getItem(int arg0) {

		return Utilities.pdata.get(arg0);
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
			convertView= infilate.inflate(R.layout.customlist, null);
			holder=new UserHolder();
			holder.tv = (TextView) convertView.findViewById(R.id.textView1);
		

			holder.orangeArrow = (ImageView) convertView
					.findViewById(R.id.name_imageView2);
			holder.greyArrow = (ImageView) convertView
					.findViewById(R.id.name_imageView1);

			convertView.setTag(holder);

		} else {
			holder = (UserHolder) convertView.getTag();

		}
		//Log.d("disable","---->"+Utilities.pdata.get(position).getHasActivities()+"  "+position+" "+Utilities.pdata.get(position).getPName());
		holder.tv.setText(Utilities.pdata.get(position).getPName());
		if (singleton.isEnableTasks()) {
			
			Log.d("position","--->"+position);
			if (Utilities.pdata.get(position).getHasData() == 1) {
				holder.orangeArrow.setVisibility(View.VISIBLE);
				holder.greyArrow.setVisibility(View.INVISIBLE);
			}
			else{
				holder.orangeArrow.setVisibility(View.INVISIBLE);
				holder.greyArrow.setVisibility(View.VISIBLE);
			}

		}
		else{
			
			//Log.d("disable","---->"+Utilities.pdata.get(position).getHasActivities()+"  "+position);
			if (Utilities.pdata.get(position).getHasData()==1) 
			{
			holder.orangeArrow.setVisibility(View.VISIBLE);
			holder.greyArrow.setVisibility(View.INVISIBLE);
			}
			else{
				holder.orangeArrow.setVisibility(View.INVISIBLE);
				holder.greyArrow.setVisibility(View.VISIBLE);
			}
		}
		holder.tv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				singleton.setSelectedProjectName(Utilities.pdata.get(position).getPName());
				
				Log.d("proect","--->"+Utilities.pdata.get(position).getPID()+"   "+position);
				singleton.setSelectedProjectID(Utilities.pdata.get(position).getPID());
				
				

				Date curDate = new Date();
				// System.out.println("Cur Date : ##################################### : "+
				// curDate);
				SimpleDateFormat format1 = new SimpleDateFormat(
						"dd-MM-yyyy");
				try {
					singleton.setCurrentSelectedDateFormatted(format1
							.parse(format1.format(curDate)).toString()
							.replace(" 00:00:00 GMT+05:30", ","));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				singleton.setCurrentSelectedDate(new SimpleDateFormat(
						"yyyyMMdd").format(curDate));
				Intent intent;
				if (singleton.isEnableTasks()) {
					intent = new Intent(context,
							TasksListActivity.class);
				} else {
					intent = new Intent(context,
							ActivitiesListActivity.class);

				}
				context.startActivity(intent);
			}
		});

		return convertView;
	}

	static class UserHolder {
		TextView tv;
		ImageView orangeArrow, greyArrow;
	}

}
