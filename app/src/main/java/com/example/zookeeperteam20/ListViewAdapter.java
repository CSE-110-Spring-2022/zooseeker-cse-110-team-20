package com.example.zookeeperteam20;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables

    Context mContext;
    LayoutInflater inflater;
    private List<ExhibitItem> exhibitNamesList = null;
    private ArrayList<ExhibitItem> arraylist;

    public ListViewAdapter(Context context, List<ExhibitItem> exhibitNamesList) {
        mContext = context;
        this.exhibitNamesList = exhibitNamesList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<ExhibitItem>();
        this.arraylist.addAll(exhibitNamesList);
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return exhibitNamesList.size();
    }

    @Override
    public ExhibitItem getItem(int position) {
        return exhibitNamesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.activity_list_view_items, null);
            // Locate the TextViews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(exhibitNamesList.get(position).getExhibitName());
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        exhibitNamesList.clear();
        if (charText.length() == 0) {
            exhibitNamesList.addAll(arraylist);
        } else {
            for (ExhibitItem wp : arraylist) {
                if (wp.getExhibitName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    exhibitNamesList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}
