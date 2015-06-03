package com.notevault.activities;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.notevault.pojo.Singleton;

import java.util.ArrayList;
import java.util.TreeSet;

public class EntriesListByTypeActivity extends ListActivity  {

    Singleton singleton;
    String values[];
    ArrayList<String> entries = new ArrayList<String>();
    private CustomAdapter mAdapter;
    public static int reload = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.types_activity);
        //this.setContentView(entriesListView);
        singleton = Singleton.getInstance();
        entries = EntriesListByDateActivity.collectiveConcatenatedEntryList;
        //System.out.println("Entries from EntriesListByDate activity: ");
        System.out.println(entries.toString());
        setAdapter();
        System.out.println("type on create called.");
    }

    protected void onResume() {
        if(reload == 1){
            reload = 0;
            singleton.setReloadPage(false);
            this.onCreate(null);
        }


        super.onResume();
        System.out.println("Entries By Date On resume called.");
        if(singleton.isReloadPage()){
            System.out.println("Reloading the page.");
            //readEntriesFromDB();
            singleton.setReloadPage(false);
            this.onCreate(null);
        }
    }

    private void setAdapter() {
        boolean hasLaborEntries, hasEquipmentEntries, hasMaterialEntries, equipmentHeaderFlag, materialHeaderFlag;
        hasLaborEntries = hasEquipmentEntries = hasMaterialEntries = equipmentHeaderFlag = materialHeaderFlag = false;
        mAdapter = new CustomAdapter(EntriesListByTypeActivity.this);

        for(int i = 0; i < entries.size(); i++){
            if(entries.get(i).startsWith("L")){
                hasLaborEntries = true;
            }else if(entries.get(i).startsWith("E")){
                hasEquipmentEntries = true;
            }else if(entries.get(i).startsWith("M")){
                hasMaterialEntries = true;
            }
        }

        System.out.println("Size: "+entries.size());
        int i = 0;
        if(hasLaborEntries)
            mAdapter.addSectionHeaderItem("Labor");
        
        while(entries.size() != 0){
            if(hasLaborEntries){
                if(entries.get(i).startsWith("L")){
                    mAdapter.addItem(entries.get(i));
                    entries.remove(i);
                }else{
                    i++;
                }
                
                if(i == entries.size()) {
                        i = 0;
                        hasLaborEntries = false;
                }
                System.out.println("Entries List: " + entries);
                continue;
            }

            if(hasEquipmentEntries){
                if(!equipmentHeaderFlag) {
                    mAdapter.addSectionHeaderItem("Equipment");
                    equipmentHeaderFlag = true;
                }

                if(entries.get(i).startsWith("E")){
                    mAdapter.addItem(entries.get(i));
                    entries.remove(i);
                }else{
                    i++;
                }
                if(i == entries.size()) {
                    i = 0;
                    hasEquipmentEntries = false;
                }
                System.out.println("Entries List: " + entries);
                continue;
            }

            if(hasMaterialEntries){
                if(!materialHeaderFlag) {
                    mAdapter.addSectionHeaderItem("Material");
                    materialHeaderFlag = true;
                }

                if(entries.get(i).startsWith("M")){
                    mAdapter.addItem(entries.get(i));
                    entries.remove(i);
                }else{
                    i++;
                }
                if(i == entries.size()) {
                    i = 0;
                    hasMaterialEntries = false;
                }
                System.out.println("Entries List: " + entries);
                continue;
            }
        }
        setListAdapter(mAdapter);
    }

    public class CustomAdapter extends BaseAdapter {

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

        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            String glue = "-~-";

            if(convertView == null) {
                holder = new ViewHolder();

                if (mData.size() < 0) {
                    Toast.makeText(getApplicationContext(), "No entries found.", Toast.LENGTH_LONG).show();
                } else {
                    switch (getItemViewType(position)) {
                        case TYPE_ITEM:

                            convertView = mInflater.inflate(R.layout.customlist2, null);
                            TextView roundTv = (TextView) convertView.findViewById(R.id.tv);
                            TextView textView = (TextView) convertView.findViewById(R.id.textView1);
                            TextView tv1 = (TextView) convertView.findViewById(R.id.textView2);
                            TextView tv2 = (TextView) convertView.findViewById(R.id.textView3);
                            TextView tv3 = (TextView) convertView.findViewById(R.id.textView4);

                            String entry = mData.get(position);
                           /* if (entry.endsWith(glue))
                                entry = entry + "null";*/

                            final String val[] = entry.split(glue);
                           /* if (val[6].equals("null"))
                                val[6] = "";*/

                            roundTv.setText(val[0]);
                            textView.setText(val[1]);
                            tv1.setText(val[2]);
                            tv2.setText(val[3]);
                            if (!val[4].equals("null") && val[4] != null)
                                val[4] = Singleton.prettyFormat(val[4]);
                            tv3.setText(val[4]);

                            if (val[0].equals("L")) {
                                roundTv.setBackgroundResource(R.drawable.circleyellow);
                            } else if (val[0].equals("E")) {
                                roundTv.setBackgroundResource(R.drawable.circleblack);
                            } else if (val[0].equals("M")) {
                                roundTv.setBackgroundResource(R.drawable.circleblue);
                            }

                            convertView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    singleton.setCurrentSelectedEntryID(Integer.parseInt(val[5]));
                                    singleton.setNewEntryFlag(false);

                                    if (val[0].equals("L")) {
                                        singleton.setSelectedLaborName(val[1]);
                                        singleton.setSelectedLaborTrade(val[2]);
                                        singleton.setSelectedLaborClassification(val[3]);
                                        singleton.setSelectedLaborHours(val[4]);
                                        //singleton.setSelectedLaborDescription(val[6]);
                                        Intent intent = new Intent(EntriesListByTypeActivity.this, AddLabor.class);
                                        startActivity(intent);
                                    } else if (val[0].equals("E")) {
                                        singleton.setSelectedEquipmentName(val[1]);
                                        singleton.setSelectedEquipmentCompany(val[2]);
                                        singleton.setSelectedEquipmentStatus(val[3]);
                                        singleton.setSelectedEquipmentQty(val[4]);
                                        //singleton.setSelectedEquipmentDescription(val[6]);
                                        Intent intent = new Intent(EntriesListByTypeActivity.this, AddEquipment.class);
                                        startActivity(intent);
                                    } else if (val[0].equals("M")) {
                                        singleton.setSelectedMaterialName(val[1]);
                                        singleton.setSelectedMaterialCompany(val[2]);
                                        singleton.setSelectedMaterialStatus(val[3]);
                                        singleton.setSelectedMaterialQty(val[4]);
                                        //singleton.setSelectedMaterialDescription(val[6]);
                                        Intent intent = new Intent(EntriesListByTypeActivity.this, AddMaterial.class);
                                        startActivity(intent);
                                    }
                                }
                            });

                            break;
                        case TYPE_SEPARATOR:
                            convertView = mInflater.inflate(R.layout.header, null);
                            holder.text = (TextView) convertView.findViewById(R.id.header_txt);
                            holder.text.setText(mData.get(position));
                            //TextView header = (TextView) convertView.findViewById(R.id.header_txt);
                            //header.setText(mData.get(position));
                            break;
                    }
                    convertView.setTag(holder);
                }
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            return convertView;
        }
    }

    public static class ViewHolder{
        TextView text;
    }
}