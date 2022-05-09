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
    ArrayList<ExhibitItem> noRepeats = new ArrayList<ExhibitItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoo);

        // Variables set for
        //String start = "entrance_exit_gate";
        //String goal = "elephant_odyssey";

        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(this, "sample_zoo_graph.json");
        //GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, start, goal);

        Map<String, ZooData.VertexInfo> vInfo = ZooData.loadVertexInfoJSON(this, "sample_node_info.json");
        Map<String, ZooData.EdgeInfo> eInfo = ZooData.loadEdgeInfoJSON(this, "sample_edge_info.json");


        list = (ListView) findViewById(R.id.listview_Exhibits);

        ExhibitItem e0;
        for (ZooData.VertexInfo node : vInfo.values()) {
            if (node.kind == ZooData.VertexInfo.Kind.EXHIBIT) {
                e0 = new ExhibitItem(node.id,node.name,node.kind,node.tags);
                ExhibitsList.add(e0);
                Log.d("ZooData", node.name);
            }
        }

        //Set up Adapter to create drop down menu for search bar
        //Followed a tutorial on how to create such search bar into our code
        // Source : https://abhiandroid.com/ui/searchview#:~:text=%20SearchView%20Methods%20In%20Android%3A%20%201%20isIconfiedByDefault,hint%20text%20to%20display%20in%20the...%20More%20
        adapter = new ListViewAdapter(this, ExhibitsList);
        list.setAdapter(adapter);

        AnimalAdapter adapter = new AnimalAdapter();
        adapter.setHasStableIds(true);

        editSearch = (SearchView) findViewById(R.id.search);
        // sets drop down menu to not appear until we start typing
        list.setVisibility(ListView.INVISIBLE);
        editSearch.setOnQueryTextListener(this);

        //counter to count amount of times exhibits have been selected
        TextView counter = findViewById(R.id.number);

        //Detect Clicks on search results and update Plan list and count accordingly
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ExhibitItem item = (ExhibitItem) list.getItemAtPosition(position);
                if (!selected.contains(item)) {
                    count++;
                }
                selected.add(item);
                counter.setText(String.valueOf(count));
                //Log used to keep track of selected animals (ensures functionality working correctly)
                Log.d("oof", selected.toString());
            }
        });

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    //When text is change, the list will update according to text in search
    @Override
    public boolean onQueryTextChange(String newText) {
        list.setVisibility(ListView.VISIBLE);
        String text = newText;
        adapter.filter(text);
        return false;
    }

    //When Plan button is made, we filter selected list to have
    //no repeats in it in order to create a planned list
    public void onPlanClicked(View view) {
        Intent intent = new Intent(this,Plan_activity.class);
        for(ExhibitItem elem : selected) {
            if(!noRepeats.contains(elem)) {
                noRepeats.add(elem);
            }
        }
        intent.putExtra("plan",noRepeats);
        startActivity(intent);
    }
}