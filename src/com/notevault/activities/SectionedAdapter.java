package com.notevault.activities;

import java.util.ArrayList;
import java.util.TreeSet;

import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

class CustomAdapter extends BaseAdapter {

	Intent intent;
	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;

	private ArrayList<String> mData = new ArrayList<String>();
	private TreeSet<Integer> sectionHeader = new TreeSet<Integer>();

	private LayoutInflater mInflater;

	public CustomAdapter(Context context) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addItem(final String item) {
		mData.add(item);
		notifyDataSetChanged();
	}

	public void addSectionHeaderItem(final String item) {
		mData.add(item);
		sectionHeader.add(mData.size() - 1);
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public String getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int rowType = getItemViewType(position);

		if (convertView == null) {
			holder = new ViewHolder();
			switch (rowType) {
			case TYPE_ITEM:
				convertView = mInflater.inflate(R.layout.customlist2, null);
				TextView textView = (TextView) convertView.findViewById(R.id.textView1);
				//textView.setText(mData.get(position));
				TextView tv1=(TextView)convertView.findViewById(R.id.textView2);
				TextView tv2=(TextView)convertView.findViewById(R.id.textView3);
				TextView tv3=(TextView)convertView.findViewById(R.id.textView4);
				TextView roundTv=(TextView)convertView.findViewById(R.id.tv);
				String val[]=mData.get(position).split(",");
				textView.setText(val[0]);
				tv1.setText(val[1]);
				if (val[4].equals("L")) {
					tv2.setText(val[2]);
					tv3.setText(val[5]);
					roundTv.setText(val[4]);
					roundTv.setBackgroundResource(R.drawable.circleyellow);

				}else if (val[4].equals("E")) {
					tv3.setText(val[2]);
					roundTv.setText(val[4]);
					tv2.setText(val[5]);
					roundTv.setBackgroundResource(R.drawable.circleblack);

				}else if (val[4].equals("M")) {
					tv3.setText(val[2]);
					roundTv.setText(val[4]);
					tv2.setText(val[5]);
					roundTv.setBackgroundResource(R.drawable.circleblue);

				}
				break;
			case TYPE_SEPARATOR:
				convertView = mInflater.inflate(R.layout.header, null);
				holder.textView = (TextView) convertView.findViewById(R.id.header_txt);
				holder.textView.setText(mData.get(position));
				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
	
		return convertView;
	}

	public static class ViewHolder {
		public TextView textView,tv1,tv2,tv3,roundTv;
	}

} 