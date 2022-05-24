package com.example.zookeeperteam20;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExhibitAdapter extends RecyclerView.Adapter<ExhibitAdapter.ViewHolder> {
    private List<ExhibitItem> exhibitItems = Collections.emptyList();

    public void setExhibitItems(List<ExhibitItem> newExhibitItems){
        this.exhibitItems.clear();
        this.exhibitItems = newExhibitItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.zoo_layout,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setExhibitItem(exhibitItems.get(position));
    }

    @Override
    public int getItemCount() {
        return exhibitItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView textView;
        public ExhibitItem exhibitItem;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            this.textView = itemView.findViewById(R.id.exhibit_list);
        }

        public ExhibitItem getExhibitItem(){
            return exhibitItem;
        }

        public void setExhibitItem(ExhibitItem exhibitItem){
            this.exhibitItem = exhibitItem;
            this.textView.setText(exhibitItem.getExhibitName());
        }
    }

}
