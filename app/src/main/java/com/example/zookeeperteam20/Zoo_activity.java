package com.example.zookeeperteam20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

public class Zoo_activity extends AppCompatActivity {

    public RecyclerView recyclerView;
    ArrayList<String> ExhibitsList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoo);

        String start = "entrance_exit_gate";
        String goal = "elephant_odyssey";

        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(this,"sample_zoo_graph.json");
        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, start, goal);

        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON(this,"sample_node_info.json");
        Map<String, ZooData.EdgeInfo> eInfo = ZooData.loadEdgeInfoJSON(this,"sample_edge_info.json");

        for( ZooData.VertexInfo node : vInfo.values()) {
            if(node.kind == ZooData.VertexInfo.Kind.EXHIBIT) {
                ExhibitsList.add(node.name);
                Log.d("ZooData", node.name);
            }
        }

        AnimalAdapter adapter = new AnimalAdapter();
        adapter.setHasStableIds(true);

        //recyclerView = findViewById(R.id.Exhibits);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(adapter);

       // adapter.setExhibitData(vInfo);
        /*for(IdentifiedWeightedEdge e :path.getEdgeList()) {
            Log.d("ZooData1",eInfo.get(e.getId()).street );
            Log.d("ZooData2",vInfo.get(g.getEdgeSource(e).toString()).name);
            Log.d("ZooData3",vInfo.get(g.getEdgeTarget(e).toString()).name);
        } */


    }
}