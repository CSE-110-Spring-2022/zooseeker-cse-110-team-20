/*package com.example.zookeeperteam20;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class DirectionsAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder>{
    private List<""> "" = Collections.emptyList();

    public void setListItems() {
        this."".clear();
        this."" = new"";
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanAdapter.ViewHolder holder, int position) {
        holder.setitems("".get(position));
    }

    @Override
    public int getItemCount() {
        return "".size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private someItem item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.)
        }

        public getItem() {
            return ;
        }

        public void setItem() {}
        this.item = item;
        this.textView.setText(big string);
    }

}*/
