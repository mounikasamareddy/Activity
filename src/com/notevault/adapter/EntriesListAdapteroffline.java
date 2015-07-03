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
import com.notevault.activities.R;
import com.notevault.adapter.CustomAdapter.UserHolder;
import com.notevault.pojo.Singleton;
import com.notevault.support.Utilities;

public class EntriesListAdapteroffline extends BaseAdapter{
	Context context;
	LayoutInflater infilate;
	Singleton singleton;

	public EntriesListAdapteroffline(Context context) {
		infilate=LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {

		return Utilities.eAligndata.size();
	}

	@Override
	public Object getItem(int arg0) {

		return Utilities.eAligndata.get(arg0);
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
		
		Log.d("header data","--->"+Utilities.eAligndata.get(position).getEIdentity()+" "+Utilities.eAligndata.get(position).getHeader()+" "+Utilities.eAligndata.get(position).getTYPE()+" "+Utilities.eAligndata.get(position).getNAME()+" "+Utilities.eAligndata.get(position).getTRD_COMP()+
				" "+Utilities.eAligndata.get(position).getCLASSI_STAT()+" "+Utilities.eAligndata.get(position).getHR_QTY());
		if(Utilities.eAligndata.get(position).getHeader().equals("Material")|| Utilities.eAligndata.get(position).getHeader().equals("Equipment")|| Utilities.eAligndata.get(position).getHeader().equals("Labor"))
		{
			Log.d("inside","-->"+Utilities.eAligndata.get(position).getHeader());
			holder.label1.setVisibility(View.VISIBLE);
			holder.label1.setText(Utilities.eAligndata.get(position).getHeader());
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
		if(Utilities.eAligndata.get(position).getTYPE().equals("L"))
		{
		holder.roundTv.setText("L");
		
		}
		else if(Utilities.eAligndata.get(position).getTYPE().equals("E"))
		{
			holder.roundTv.setText("E");
		}
		else{
			holder.roundTv.setText("M");
		}
		
		holder.textView.setText(Utilities.eAligndata.get(position).getNAME());
		holder.tv1.setText(Utilities.eAligndata.get(position).getTRD_COMP());
		holder.tv2.setText(Utilities.eAligndata.get(position).getCLASSI_STAT());
		holder.tv3.setText(Utilities.eAligndata.get(position).getHR_QTY()+"");
		
		if (Utilities.eAligndata.get(position).getTYPE().equals("L")) {
			holder.roundTv.setBackgroundResource(R.drawable.circleyellow);
		} else if (Utilities.eAligndata.get(position).getTYPE().equals("E")) {
			holder.roundTv.setBackgroundResource(R.drawable.circleblack);
		} else if (Utilities.eAligndata.get(position).getTYPE().equals("M")) {
			holder.roundTv.setBackgroundResource(R.drawable.circleblue);
		}
		convertView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d("disable","---->"+Utilities.eAligndata.get(position).getEIdentity());
				singleton.setCurrentSelectedEntryID(
						Utilities.eAligndata.get(position).getID());
				singleton.setSelectedEntityIdentity(Utilities.eAligndata.get(position).getEIdentity());
				singleton.setNewEntryFlag(false);

				if (Utilities.eAligndata.get(position).getTYPE().equals("L")) {
					singleton.setSelectedLaborName(Utilities.eAligndata.get(position).getNAME());
					singleton.setSelectedLaborTrade(Utilities.eAligndata.get(position).getTRD_COMP());
					singleton
							.setSelectedLaborClassification(Utilities.eAligndata.get(position).getCLASSI_STAT());
					singleton.setSelectedLaborHours(Utilities.eAligndata.get(position).getHR_QTY()+"");
					// singleton.setSelectedLaborDescription(val[6]);
					Intent intent = new Intent(
							context,
							AddLabor.class);
					context.startActivity(intent);
				} else if (Utilities.eAligndata.get(position).getTYPE().equals("E")) {
					singleton.setSelectedEquipmentName(Utilities.eAligndata.get(position).getNAME());
					singleton
							.setSelectedEquipmentCompany(Utilities.eAligndata.get(position).getTRD_COMP());
					singleton
							.setSelectedEquipmentStatus(Utilities.eAligndata.get(position).getCLASSI_STAT());
					singleton.setSelectedEquipmentQty(Utilities.eAligndata.get(position).getHR_QTY()+"");
					// singleton.setSelectedEquipmentDescription(val[6]);
					Intent intent = new Intent(
							context,
							AddEquipment.class);
					context.startActivity(intent);
				} else if (Utilities.eAligndata.get(position).getTYPE().equals("M")) {
					singleton.setSelectedMaterialName(Utilities.eAligndata.get(position).getNAME());
					singleton
							.setSelectedMaterialCompany(Utilities.eAligndata.get(position).getTRD_COMP());
					singleton.setSelectedMaterialStatus(Utilities.eAligndata.get(position).getCLASSI_STAT());
					singleton.setSelectedMaterialQty(Utilities.eAligndata.get(position).getHR_QTY()+"");
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
