package com.example.zookeeperteam20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DirectionsActivity extends AppCompatActivity {
    int count = 1;
    public RecyclerView recyclerView;
    List<GraphPath<String, IdentifiedWeightedEdge>> route;
    ArrayList<Path> pathsBetweenExhibits = new ArrayList<Path>();
    Graph<String, IdentifiedWeightedEdge> g;
    DirectionsAdapter adapter = new DirectionsAdapter();
    Map<String, ZooData.VertexInfo> vInfo;
    Map<String, ZooData.EdgeInfo> eInfo;
    int whereToCount = 0;
    ArrayList<ExhibitItem> ordered = new ArrayList<ExhibitItem>();
    ArrayList<Path> nextPath = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        // Extract ordered from PlanActivity
        ordered = (ArrayList<ExhibitItem>) getIntent().getSerializableExtra("Directions");
        Log.d("Directions UI", ordered.toString());

        //Load Graph, VetexInfo, and EdgeInfo
        g = ZooData.loadZooGraphJSON(this, "sample_zoo_graph.json");
        vInfo = ZooData.loadVertexInfoJSON(this,"sample_node_info.json");
        eInfo = ZooData.loadEdgeInfoJSON(this,"sample_edge_info.json");
        //Shortest Route created
        ShortestDistance shortDist = new ShortestDistance(g,ordered);
        route = shortDist.getShortest();
        adapter = new DirectionsAdapter();
        //DirectionsAdapter adapter = new DirectionsAdapter();
        adapter.setHasStableIds(true);

        // Set title of activity (text at top)
        TextView wT = findViewById(R.id.whereTo);
        wT.setText("Directions to " + ordered.get(whereToCount).getExhibitName());
        whereToCount++;

       recyclerView = findViewById(R.id.directions_list);
       recyclerView.setLayoutManager(new LinearLayoutManager(this));
       recyclerView.setAdapter(adapter);
       Path p;

           for(IdentifiedWeightedEdge e : route.get(0).getEdgeList()) {
               p = new Path(vInfo.get(g.getEdgeSource(e).toString()).name,
                       vInfo.get(g.getEdgeTarget(e).toString()).name
                       ,g.getEdgeWeight(e),
                       eInfo.get(e.getId()).street);
               pathsBetweenExhibits.add(p);
           }
        adapter.setRouteItems(pathsBetweenExhibits);

    }

    public void onNextClicked(View view) {
        if(count < route.size()) {
            ArrayList<Path> nextPath = new ArrayList<Path>();
            Log.d("Click", route.get(count).toString()); //Used for debugging

            Path p;

            // Create Initial NextPath
            for (IdentifiedWeightedEdge e : route.get(count).getEdgeList()) {
                p = new Path(vInfo.get(g.getEdgeSource(e).toString()).name,
                        vInfo.get(g.getEdgeTarget(e).toString()).name
                        , g.getEdgeWeight(e),
                        eInfo.get(e.getId()).street);
                nextPath.add(p);
            }

            //Filter and swap directions if necessary
            if(whereToCount < ordered.size() ) {
                for (int i = nextPath.size() - 1; i >= 0; i--) {
                    if (i == nextPath.size() - 1) {
                        if (nextPath.get(i).getTarget().equals(ordered.get(whereToCount).getExhibitName()) != true) {
                            nextPath.get(i).swap();
                        }
                    } else {
                        if (nextPath.get(i).getTarget().equals(nextPath.get(i + 1).getSource()) != true) {
                            nextPath.get(i).swap();
                        }
                    }
                }
            }
            // Once ordered list has been traversed we will set up check if we need to swap directions
            // on final directions to exit.
            else {
                for( int i = nextPath.size() - 1; i >= 0; i--) {
                    if(i == nextPath.size() - 1) {
                        if (nextPath.get(i).getTarget().equals("Entrance and Exit Gate") != true) {
                            nextPath.get(i).swap();
                        }
                    }
                    else {
                        if(nextPath.get(i).getTarget().equals(nextPath.get(i + 1).getSource()) != true) {
                            nextPath.get(i).swap();
                        }
                    }
                }
            }

            Log.d("CheckNext", nextPath.toString()); //Used for debugging
            adapter.setRouteItems(nextPath);
            count++;

            //Updates title of page with correct exhibit we are trying to get to
            TextView wT = findViewById(R.id.whereTo);
            if (whereToCount < ordered.size() ) {
                wT.setText("Directions to " + ordered.get(whereToCount).getExhibitName());
                whereToCount++;
            }
            else {
                wT.setText("Directions to Exit");
            }
        }
        else {
            //Warning shows up once route is completed
            Utilities.showAlert(this, "Route is completed");
        }

    }
}