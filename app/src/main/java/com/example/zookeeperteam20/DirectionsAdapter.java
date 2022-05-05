package com.example.zookeeperteam20;

import android.text.Layout;
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
import java.util.Map;

public class DirectionsAdapter extends RecyclerView.Adapter<DirectionsAdapter.ViewHolder>{
    private ArrayList<Path> paths = new ArrayList<Path>();
    public void setRouteItems(ArrayList<Path> newPaths) {
        this.paths.clear();
        this.paths = newPaths;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public DirectionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.directions_list,parent,false);

        return new DirectionsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DirectionsAdapter.ViewHolder holder, int position) {
        holder.setP(paths.get(position),position);
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private Path p;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.directions_view);
        }

        public Path getP() {

            return p;
        }

        public void setP(Path p, int position) {
            this.p = p;
            String numAdd = (position+1) + ". " + p.toString();
            this.textView.setText(numAdd);
        }
    }
}
