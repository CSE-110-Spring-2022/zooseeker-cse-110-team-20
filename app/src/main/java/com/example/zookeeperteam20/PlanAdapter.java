package com.example.zookeeperteam20;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {
    private List<ExhibitItem> exhibitItems = Collections.emptyList();
    private ArrayList<Double> dists = new ArrayList<Double>();
    public void setExhibitItems(List<ExhibitItem> newExhibitItems, ArrayList<Double> dists) {
        this.exhibitItems.clear();
        this.exhibitItems = newExhibitItems;
        this.dists = dists;
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
        holder.setExhibitItem(exhibitItems.get(position),position,dists);
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

        public void setExhibitItem(ExhibitItem exhibitItem, int position,ArrayList<Double> dists) {
            this.exhibitItem = exhibitItem;
            String numAdd = (position+1) + ". " + exhibitItem.getExhibitName()  + " - " + dists.get(position)
                    + " meters away";
            this.textView.setText(numAdd);
        }

    }

}
