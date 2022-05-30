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
    ArrayList<ExhibitItem> selected = new ArrayList<>();
    Map<String, ZooData.EdgeInfo> eInfo;
    ArrayList<Double> dists = new ArrayList<Double>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        //Adapter set up
        PlanAdapter adapter = new PlanAdapter();
        adapter.setHasStableIds(true);

        //RecyclerView set up
        recyclerView = findViewById(R.id.planList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        //Info from Jsons loaded in
        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(this, "zoo_graph.json");
        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON(this,"zoo_node_info.json");
        eInfo = ZooData.loadEdgeInfoJSON(this,"zoo_edge_info.json");

        //selected loaded from zooActivity
        selected = (ArrayList<ExhibitItem>) getIntent().getSerializableExtra("plan");
        ExhibitItem parent;
        ArrayList<ExhibitItem> noGroupRepeats = new ArrayList<ExhibitItem>();
        for(ExhibitItem elem : selected){
            if(elem.getParentId().equals("NULLNULLNULL")) {
                noGroupRepeats.add(elem);
            }
            else {
                parent = new ExhibitItem(vInfo.get(elem.getParentId()).id,
                        vInfo.get(elem.getParentId()).name,
                        vInfo.get(elem.getParentId()).kind,
                        new Tags(vInfo.get(elem.getParentId()).tags));
                Log.d("parentsInPlan", parent.toString());
                parent.setLat( vInfo.get(elem.getParentId()).lat);
                parent.setLng( vInfo.get(elem.getParentId()).lng);
                if(noGroupRepeats.size() == 0) {
                    noGroupRepeats.add(parent);
                }
                else {
                    boolean check = true;
                    for (ExhibitItem elemG : noGroupRepeats) {
                        if (elemG.getId().equals(parent.getId())) {
                            check = false;
                        }
                    }
                    if(check) {
                        noGroupRepeats.add(parent);
                    }

                }
            }
        }
        //Sets up and calculate shortest Distance route
        ShortestDistance shortDist = new ShortestDistance(g,noGroupRepeats);
        Log.d("noGroupRepeats",noGroupRepeats.toString());
        route = shortDist.getShortest();
        Log.d("route",route.toString()); //Used for debugging


        ExhibitItem ex;
        ArrayList<String> singlePath = new ArrayList<String>();
        double dist = 0.0;
        //Extract GraphPaths from route and create ArrayList of endVertex names
        //Also sum up distance
        for(GraphPath<String, IdentifiedWeightedEdge> path : route) {
            singlePath.add(path.getEndVertex());
            for(IdentifiedWeightedEdge e : path.getEdgeList()) {
                dist += g.getEdgeWeight(e);
            }
            dists.add(dist);
        }

        Log.d("Dist", dists.toString());
        Log.d("singlePath Array", singlePath.toString()); //Used for debugging

        //We use this for loop to create new arrayList that has all exhibits of
        // singlePath, meaning that no intersections will appear in plan
        for(int i = 0; i < singlePath.size(); i++) {
            for(ExhibitItem exItem: noGroupRepeats) {
                if (exItem.getId().equals(singlePath.get(i)) || exItem.getParentId().equals(singlePath.get(i))) {
                    ordered.add(exItem);
                }
            }
        }
        //adapter set
        Log.d("ordered",ordered.toString());
        adapter.setExhibitItems(ordered,dists);
        //Log.d("oof",selected.toString()); //used for debugging

    }

    //When Directions is clicked, we will forward data and go to new Activity
    public void onPlanClicked(View view) {
        if(ordered.size() == 0) {
            Utilities.showAlert(this, "Plan is empty");
        }
        else {
            Intent intent = new Intent(this, DirectionsActivity.class);
            intent.putExtra("Directions", ordered);
            startActivity(intent);
        }

    }

    public void onPlanClearClicked(View view) {
        Intent intent = new Intent(this, Zoo_activity.class);
        startActivity(intent);
    }
}