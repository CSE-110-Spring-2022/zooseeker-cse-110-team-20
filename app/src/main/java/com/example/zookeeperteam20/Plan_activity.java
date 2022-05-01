package com.example.zookeeperteam20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Plan_activity extends AppCompatActivity {

    public RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        PlanAdapter adapter = new PlanAdapter();
        adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.planList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        ArrayList<ExhibitItem> selected = (ArrayList<ExhibitItem>) getIntent().getSerializableExtra("plan");
        adapter.setExhibitItems(selected);

        Log.d("oof",selected.toString());

    }

    public void onPlanClicked(View view) {
        Intent intent = new Intent(this,DirectionsActivity.class);
        //intent.putExtra("Directions", 0);
        startActivity(intent);
    }
}