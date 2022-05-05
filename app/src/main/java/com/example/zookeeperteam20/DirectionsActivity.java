package com.example.zookeeperteam20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DirectionsActivity extends AppCompatActivity {
    public RecyclerView recyclerView;
    List<GraphPath<String, IdentifiedWeightedEdge>> route;
    ArrayList<Path> pathsBetweenExhibits = new ArrayList<Path>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        // Extract ordered from PlanActivity
        ArrayList<ExhibitItem> ordered = (ArrayList<ExhibitItem>) getIntent().getSerializableExtra("Directions");
        Log.d("Directions UI", ordered.toString());

        //Load Graph, VetexInfo, and EdgeInfo
        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(this, "sample_zoo_graph.json");
        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON(this,"sample_node_info.json");
        Map<String, ZooData.EdgeInfo> eInfo = ZooData.loadEdgeInfoJSON(this,"sample_edge_info.json");
        //Shortest Route created
        ShortestDistance shortDist = new ShortestDistance(g,ordered);
        route = shortDist.getShortest();

        DirectionsAdapter adapter = new DirectionsAdapter();
        adapter.setHasStableIds(true);

       recyclerView = findViewById(R.id.directions_list);
       recyclerView.setLayoutManager(new LinearLayoutManager(this));
       recyclerView.setAdapter(adapter);
        Path p;
       for(GraphPath<String, IdentifiedWeightedEdge> path : route) {
           for(IdentifiedWeightedEdge e : path.getEdgeList()) {
               p = new Path(vInfo.get(g.getEdgeSource(e).toString()).name,
                       vInfo.get(g.getEdgeTarget(e).toString()).name
                       ,g.getEdgeWeight(e),
                       eInfo.get(e.getId()).street);
               pathsBetweenExhibits.add(p);
           }
           break;
       }
        adapter.setRouteItems(pathsBetweenExhibits);
    }
}