package com.example.zookeeperteam20;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {
    private List<ExhibitItem> exhibitItems = Collections.emptyList();

    public void setExhibitItems(List<ExhibitItem> newExhibitItems) {
        this.exhibitItems.clear();
        this.exhibitItems = newExhibitItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.plan_list,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setExhibitItem(exhibitItems.get(position),position);
    }

    @Override
    public int getItemCount() {
        return exhibitItems.size();
    }

    /*@Override
    public long getItemId(int position) {
        return exhibitItems.get(position).id;
    }*/

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private ExhibitItem exhibitItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.planView);
        }

        public ExhibitItem getExhibitItem() {
            return exhibitItem;
        }

        public void setExhibitItem(ExhibitItem exhibitItem, int position) {
            this.exhibitItem = exhibitItem;
            String numAdd = (position+1) + ". " + exhibitItem.getExhibitName();
            this.textView.setText(numAdd);
        }

    }

}
