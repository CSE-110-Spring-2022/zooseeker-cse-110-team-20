package com.example.zookeeperteam20;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.ViewHolder>{
    private Map<String, ZooData.VertexInfo> exhibits = Collections.emptyMap();

    public void setExhibitData(Map<String, ZooData.VertexInfo> newExhibits) {
        this.exhibits.clear();
        this.exhibits = newExhibits;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.animal_name_search,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setExhibit(exhibits.get(position));
    }

    @Override
    public int getItemCount() {
        return exhibits.size();
    }

    /*@Override
    public long getItemId(int position) {
        return ZooDatas.get(position).id;
    }*/


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private ZooData.VertexInfo exhibit;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.Animal_name);
        }

        public ZooData.VertexInfo getExhibit() {
            return exhibit;
        }

        public void setExhibit(ZooData.VertexInfo exhibit) {
            this.exhibit = exhibit;
            this.textView.setText(exhibit.name);
        }
    }
}
