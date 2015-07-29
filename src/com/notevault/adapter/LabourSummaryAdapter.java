package com.notevault.adapter;

import com.notevault.activities.AddEquipment;
import com.notevault.activities.AddLabor;
import com.notevault.activities.AddMaterial;
import com.notevault.activities.LaborSummary;
import com.notevault.activities.R;
import com.notevault.adapter.CustomAdapter.UserHolder;
import com.notevault.pojo.Singleton;
import com.notevault.support.Utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LabourSummaryAdapter extends BaseAdapter {

	Context context;
	LayoutInflater infilate;
	Singleton singleton;

	public LabourSummaryAdapter(Context context) {
		infilate = LayoutInflater.from(context);
		this.context = context;

	}

	@Override
	public int getCount() {

		return LaborSummary.labourname.size();
	}

	@Override
	public Object getItem(int arg0) {

		return LaborSummary.labourname.get(arg0);
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
			convertView = infilate.inflate(R.layout.customlist2, null);
			holder = new UserHolder();
			holder.layout = (RelativeLayout) convertView
					.findViewById(R.id.relativelay);
			holder.label1 = (TextView) convertView.findViewById(R.id.label1);
			holder.roundTv = (TextView) convertView.findViewById(R.id.tv);
			holder.textView = (TextView) convertView
					.findViewById(R.id.textView1);
			holder.tv1 = (TextView) convertView.findViewById(R.id.textView2);
			holder.tv2 = (TextView) convertView.findViewById(R.id.textView3);
			holder.tv3 = (TextView) convertView.findViewById(R.id.textView4);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.listbutton = (ImageView) convertView
					.findViewById(R.id.listbutton);
			convertView.setTag(holder);

		} else {
			holder = (UserHolder) convertView.getTag();

		}
		// Log.d("disable","---->"+Utilities.pdata.get(position).getHasActivities()+"  "+position+" "+Utilities.pdata.get(position).getPName());

		// Log.d("header","--->"+Utilities.groupdata.get(position).getHeadname()+" "+Utilities.groupdata.get(position).getType()+" "+Utilities.groupdata.get(position).getName()+" "+Utilities.groupdata.get(position).getTrade()+
		// " "+Utilities.groupdata.get(position).getClassification()+" "+Utilities.groupdata.get(position).getHrs());
		if (LaborSummary.labourname.get(position).equals("LABOR")) {

			holder.label1.setVisibility(View.VISIBLE);

			holder.label1.setText(LaborSummary.labourname.get(position));
			holder.roundTv.setVisibility(View.GONE);
			holder.img.setVisibility(View.GONE);
			holder.listbutton.setVisibility(View.GONE);
			holder.textView.setText("");
			holder.tv1.setText("");
			holder.tv2.setText("");
			holder.tv3.setText("");
			holder.layout.setBackgroundColor(Color.parseColor("#EBEBE9"));
		} else {
			holder.layout.setBackgroundColor(Color.WHITE);
			holder.label1.setVisibility(View.GONE);
			holder.roundTv.setVisibility(View.VISIBLE);
			holder.img.setVisibility(View.GONE);
			holder.listbutton.setVisibility(View.VISIBLE);
			holder.tv1.setVisibility(View.GONE);
			holder.tv2.setVisibility(View.GONE);

			holder.roundTv.setText("L");

			holder.textView.setText(LaborSummary.labourname.get(position));

			holder.tv3.setText(LaborSummary.hours.get(position)+"");

			holder.roundTv.setBackgroundResource(R.drawable.circleyellow);
			
		}
		return convertView;
	}

	static class UserHolder {
		TextView roundTv, textView, tv2, tv1, tv3, label1;
		ImageView img, listbutton;
		RelativeLayout layout;
	}
}