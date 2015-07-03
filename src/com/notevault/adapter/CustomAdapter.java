package com.notevault.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.notevault.activities.AddEquipment;
import com.notevault.activities.AddLabor;
import com.notevault.activities.AddMaterial;
import com.notevault.activities.EntriesListByTypeActivity;
import com.notevault.activities.R;
import com.notevault.pojo.Singleton;
import com.notevault.support.Utilities;

public class CustomAdapter extends BaseAdapter{

	Context context;
	LayoutInflater infilate;
	Singleton singleton;

	public CustomAdapter(Context context) {
		infilate=LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {

		return Utilities.groupdata.size();
	}

	@Override
	public Object getItem(int arg0) {

		return Utilities.groupdata.get(arg0);
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
			convertView= infilate.inflate(R.layout.customlist2, null);
			holder=new UserHolder();
			
		holder.label1=(TextView) convertView
				.findViewById(R.id.label1);
			holder.roundTv = (TextView) convertView
					.findViewById(R.id.tv);
			holder.textView = (TextView) convertView
					.findViewById(R.id.textView1);
			holder.tv1 = (TextView) convertView
					.findViewById(R.id.textView2);
			holder.tv2 = (TextView) convertView
					.findViewById(R.id.textView3);
			holder.tv3 = (TextView) convertView
					.findViewById(R.id.textView4);
			holder.img = (ImageView) convertView
					.findViewById(R.id.img);
			holder.listbutton = (ImageView) convertView
					.findViewById(R.id.listbutton);
			convertView.setTag(holder);

		} else {
			holder = (UserHolder) convertView.getTag();

		}
		//Log.d("disable","---->"+Utilities.pdata.get(position).getHasActivities()+"  "+position+" "+Utilities.pdata.get(position).getPName());
		
		Log.d("header","--->"+Utilities.groupdata.get(position).getHeadname()+" "+Utilities.groupdata.get(position).getType()+" "+Utilities.groupdata.get(position).getName()+" "+Utilities.groupdata.get(position).getTrade()+
				" "+Utilities.groupdata.get(position).getClassification()+" "+Utilities.groupdata.get(position).getHrs());
		if(Utilities.groupdata.get(position).getHeadname().equals("Material")|| Utilities.groupdata.get(position).getHeadname().equals("Equipment")|| Utilities.groupdata.get(position).getHeadname().equals("Labor"))
		{
			Log.d("inside","-->"+Utilities.groupdata.get(position).getHeadname());
			holder.label1.setVisibility(View.VISIBLE);
			holder.label1.setText(Utilities.groupdata.get(position).getHeadname());
			holder.roundTv.setVisibility(View.GONE);
			holder.img.setVisibility(View.GONE);
			holder.listbutton.setVisibility(View.GONE);
			holder.textView.setText("");
			holder.tv1.setText("");
			holder.tv2.setText("");
			holder.tv3.setText("");
		}
		else{
			holder.label1.setVisibility(View.GONE);
			holder.roundTv.setVisibility(View.VISIBLE);
			holder.img.setVisibility(View.VISIBLE);
			holder.listbutton.setVisibility(View.VISIBLE);
		if(Utilities.groupdata.get(position).getType().equals("Labor"))
		{
		holder.roundTv.setText("L");
		
		}
		else if(Utilities.groupdata.get(position).getType().equals("Equipment"))
		{
			holder.roundTv.setText("E");
		}
		else{
			holder.roundTv.setText("M");
		}
		
		holder.textView.setText(Utilities.groupdata.get(position).getName());
		holder.tv1.setText(Utilities.groupdata.get(position).getTrade());
		holder.tv2.setText(Utilities.groupdata.get(position).getClassification());
		holder.tv3.setText(Utilities.groupdata.get(position).getHrs()+"");
		
		if (Utilities.groupdata.get(position).getType().equals("Labor")) {
			holder.roundTv.setBackgroundResource(R.drawable.circleyellow);
		} else if (Utilities.groupdata.get(position).getType().equals("Equipment")) {
			holder.roundTv.setBackgroundResource(R.drawable.circleblack);
		} else if (Utilities.groupdata.get(position).getType().equals("Material")) {
			holder.roundTv.setBackgroundResource(R.drawable.circleblue);
		}
		convertView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("disable","---->"+Utilities.groupdata.get(position).getEId());
				singleton.setCurrentSelectedEntryID(
						Utilities.groupdata.get(position).getEId());
				singleton.setNewEntryFlag(false);

				if (Utilities.groupdata.get(position).getType().equals("Labor")) {
					Log.d("labor","---->");
					singleton.setSelectedLaborName(Utilities.groupdata.get(position).getName());
					singleton.setSelectedLaborTrade(Utilities.groupdata.get(position).getTrade());
					singleton
							.setSelectedLaborClassification(Utilities.groupdata.get(position).getClassification());
					singleton.setSelectedLaborHours(Utilities.groupdata.get(position).getHrs()+"");
					// singleton.setSelectedLaborDescription(val[6]);
					Intent intent = new Intent(
							context,
							AddLabor.class);
					context.startActivity(intent);
				} else if (Utilities.groupdata.get(position).getType().equals("Equipment")) {
					Log.d("Equipment","---->");
					singleton.setSelectedEquipmentName(Utilities.groupdata.get(position).getName());
					singleton
							.setSelectedEquipmentCompany(Utilities.groupdata.get(position).getTrade());
					singleton
							.setSelectedEquipmentStatus(Utilities.groupdata.get(position).getClassification());
					singleton.setSelectedEquipmentQty(Utilities.groupdata.get(position).getHrs()+"");
					// singleton.setSelectedEquipmentDescription(val[6]);
					Intent intent = new Intent(
							context,
							AddEquipment.class);
					context.startActivity(intent);
				} else if (Utilities.groupdata.get(position).getType().equals("Material")) {
					singleton.setSelectedMaterialName(Utilities.groupdata.get(position).getName());
					singleton
							.setSelectedMaterialCompany(Utilities.groupdata.get(position).getTrade());
					singleton.setSelectedMaterialStatus(Utilities.groupdata.get(position).getClassification());
					singleton.setSelectedMaterialQty(Utilities.groupdata.get(position).getHrs()+"");
					// singleton.setSelectedMaterialDescription(val[6]);
					Intent intent = new Intent(
							context,
							AddMaterial.class);
					context.startActivity(intent);
				}
			}
				
			
		});
		
		}
		return convertView;
	}

	static class UserHolder {
		TextView roundTv,textView,tv2,tv1,tv3,label1;
		ImageView img,listbutton;
	}

}
