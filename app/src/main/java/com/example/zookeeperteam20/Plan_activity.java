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

        PlanAdapter adapter = new PlanAdapter();
        adapter.setHasStableIds(true);

        recyclerView = findViewById(R.id.planList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(this, "sample_zoo_graph.json");
        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON(this,"sample_node_info.json");
        eInfo = ZooData.loadEdgeInfoJSON(this,"sample_edge_info.json");
        selected = (ArrayList<ExhibitItem>) getIntent().getSerializableExtra("plan");

        ShortestDistance shortDist = new ShortestDistance(g,selected);

        route = shortDist.getShortest();
        Log.d("route",route.toString());
        ExhibitItem ex;
        //selected.clear();
        ArrayList<String> test1 = new ArrayList<String>();
        double dist = 0.0;
        for(GraphPath<String, IdentifiedWeightedEdge> path : route) {
            test1.add(path.getEndVertex());
            for(IdentifiedWeightedEdge e : path.getEdgeList()) {
                dist += g.getEdgeWeight(e);
            }
            dists.add(dist);

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

        /*
        double totDistance = 0;
        for(int i = 0; i < ordered.size(); i++){
            totDistance = 0;
            for (IdentifiedWeightedEdge e : route.get(i).getEdgeList()) {
                totDistance += g.getEdgeWeight(e);
                if(route.get(i).getEdgeList().size()-1 == route.get(i).getEdgeList().indexOf(e)){
                    ordered.get(i).setLocation(eInfo.get(e.getId()).street);
                }
            }
            ordered.get(i).setDistance(totDistance);
        }
        adapter.setExhibitItems(ordered);

        Log.d("oof",ordered.toString());
        */
       
        adapter.setExhibitItems(ordered,dists);
        
        Log.d("oof",selected.toString());

    }

    public void onPlanClicked(View view) {
       Intent intent = new Intent(this, DirectionsActivity.class);
       intent.putExtra("Directions", ordered);
       startActivity(intent);

    }
}