package com.example.zookeeperteam20;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.w3c.dom.Text;

public class Zoo_activity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ListView list;
    ListViewAdapter adapter;
    SearchView editSearch;
    ArrayList<ExhibitItem> ExhibitsList = new ArrayList<ExhibitItem>();
    int count;
    ArrayList<ExhibitItem> selected = new ArrayList<ExhibitItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoo);

        String start = "entrance_exit_gate";
        String goal = "elephant_odyssey";

        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(this, "sample_zoo_graph.json");
        GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, start, goal);

        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON(this, "sample_node_info.json");
        Map<String, ZooData.EdgeInfo> eInfo = ZooData.loadEdgeInfoJSON(this, "sample_edge_info.json");


        list = (ListView) findViewById(R.id.listview_Exhibits);
        //generate ExhibitItem List
        ExhibitItem e0;
        for (ZooData.VertexInfo node : vInfo.values()) {
            if (node.kind == ZooData.VertexInfo.Kind.EXHIBIT) {
                e0 = new ExhibitItem(node.name,node.kind,node.tags);
                ExhibitsList.add(e0);
                Log.d("ZooData", node.name);
            }
        }

        adapter = new ListViewAdapter(this, ExhibitsList);
        list.setAdapter(adapter);

        AnimalAdapter adapter = new AnimalAdapter();
        adapter.setHasStableIds(true);

        editSearch = (SearchView) findViewById(R.id.search);
        list.setVisibility(ListView.INVISIBLE);
        editSearch.setOnQueryTextListener(this);

        TextView counter = findViewById(R.id.count);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ExhibitItem item = (ExhibitItem) list.getItemAtPosition(position);
                selected.add(item);
                count++;
                counter.setText(String.valueOf(count));
                Log.d("oof", selected.toString());
            }
        });





        //for(listview itm : list.Items)

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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        list.setVisibility(ListView.VISIBLE);
        String text = newText;
        adapter.filter(text);
        return false;
    }

    public void onPlanClicked(View view) {
        Intent intent = new Intent(this,Plan_activity.class);
        ArrayList<ExhibitItem> noRepeats = new ArrayList<ExhibitItem>();
        for(ExhibitItem elem : selected) {
            if(!noRepeats.contains(elem)) {
                noRepeats.add(elem);
            }
        }
        intent.putExtra("plan",noRepeats);
        startActivity(intent);
    }
}