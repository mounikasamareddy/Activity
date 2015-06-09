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
import com.notevault.pojo.Singleton;
import com.notevault.support.Utilities;

public class TaskAdapter extends BaseAdapter{

	Context context;
	LayoutInflater infilator;
	Singleton singleton;
	public TaskAdapter(Context context) {
		this.context=context;
		infilator=LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Utilities.tdata.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return Utilities.tdata.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		UserHolder holder;
		singleton = Singleton.getInstance();
		if (convertView == null) {
			convertView= infilator.inflate(R.layout.customlist, null);
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

		holder.tv.setText(Utilities.tdata.get(position).getTName());
		if (singleton.isEnableTasks()) {
			if (Utilities.tdata.get(position).getHasData() == 1) {
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
				
				
				singleton.setSelectedTaskID(Utilities.tdata.get(position).getTID());

				singleton.setSelectedTaskName(Utilities.tdata.get(position).getTName());
				singleton.setselectedTaskIdentityoffline(Utilities.tdata.get(position).getTIdentity());
				
				

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
				
				
				Intent intent = new Intent(context,
						ActivitiesListActivity.class);
				singleton.setSelectedTaskName(Utilities.tdata.get(position).getTName());
				singleton.setSelectedTaskID(Utilities.tdata.get(position).getTID());
				System.err.println("TaskName: "
						+ singleton.getSelectedTaskName());
				System.err.println("TaskID: "
						+ singleton.getSelectedTaskID());
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
