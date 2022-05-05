package com.example.zookeeperteam20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Plan_activity extends AppCompatActivity {

    public RecyclerView recyclerView;
    List<GraphPath<String, IdentifiedWeightedEdge>> route;
    ArrayList<ExhibitItem> ordered = new ArrayList<ExhibitItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        PlanAdapter adapter = new PlanAdapter();
        adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.planList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(this, "sample_zoo_graph.json");
        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON(this,"sample_node_info.json");
        ArrayList<ExhibitItem> selected = (ArrayList<ExhibitItem>) getIntent().getSerializableExtra("plan");
        ShortestDistance shortDist = new ShortestDistance(g,selected);

        route = shortDist.getShortest();
        Log.d("route",route.toString());
        ExhibitItem ex;
        //selected.clear();
        ArrayList<String> test1 = new ArrayList<String>();
        for(GraphPath<String, IdentifiedWeightedEdge> path : route) {
            test1.add(path.getEndVertex());
//            for(IdentifiedWeightedEdge e : path.getEdgeList()) {
//                ZooData.VertexInfo vertexinfo = vInfo.get(g.getEdgeSource(e));
//                ex = new ExhibitItem(vertexinfo.id,vertexinfo.name,vertexinfo.kind,vertexinfo.tags);
//                selected.add(ex);
//                break;
//            }
        }
        Log.d("Test1 Array", test1.toString());

        for(int i = 0; i < test1.size(); i++) {
            for(ExhibitItem exItem: selected) {
                if (exItem.getId().equals(test1.get(i))) {
                    ordered.add(exItem);
                }
            }
        }
        adapter.setExhibitItems(ordered);

        Log.d("oof",selected.toString());

    }

    public void onPlanClicked(View view) {
       Intent intent = new Intent(this, DirectionsActivity.class);
       intent.putExtra("Directions", ordered);
       startActivity(intent);

    }
}