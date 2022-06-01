package com.example.zookeeperteam20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zookeeperteam20.location.PermissionChecker;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class DirectionsActivity extends AppCompatActivity {
    public static final String EXTRA_LISTEN_TO_GPS = "listen_to_gps";

    boolean dir = false;
    boolean reverse = false;
    boolean forward = false;
    int count = 0;
    public RecyclerView recyclerView;
    List<GraphPath<String, IdentifiedWeightedEdge>> route;
    GraphPath<String, IdentifiedWeightedEdge> rou;
    ArrayList<Path> pathsBetweenExhibits = new ArrayList<Path>();
    Graph<String, IdentifiedWeightedEdge> g;
    DirectionsAdapter adapter = new DirectionsAdapter();
    Map<String, ZooData.VertexInfo> vInfo;
    Map<String, ZooData.EdgeInfo> eInfo;
    int whereToCount = 0;
    ArrayList<ExhibitItem> ordered = new ArrayList<ExhibitItem>();
    ArrayList<ExhibitItem> ExhibitsList = new ArrayList<ExhibitItem>();
    ArrayList<Path> nextPath = new ArrayList<Path>();
    ArrayList<Path> prevPath = new ArrayList<Path>();
    ArrayList<Path> currPath = new ArrayList<Path>();
    String nearest;
    LatLng currentLocation = new LatLng(32.73561, -117.14936);
    ExhibitItem exitItem;

    ExhibitItem nearestInList;

    boolean listenToGps = true;

    //    @SuppressLint("MissingPermission")


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final PermissionChecker permissionChecker = new PermissionChecker(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        // First check permission.


        // Extract ordered from PlanActivity
        ordered = (ArrayList<ExhibitItem>) getIntent().getSerializableExtra("Directions");
        loadProfile();
        List<String> exitTags = Arrays.asList("enter", "leave", "start", "begin", "entrance", "exit");
        Log.d("Directions UI Ordered", ordered.toString());

        //Load Graph, VetexInfo, and EdgeInfo
        g = ZooData.loadZooGraphJSON(this, "zoo_graph.json");
        vInfo = ZooData.loadVertexInfoJSON(this,"zoo_node_info.json");
        eInfo = ZooData.loadEdgeInfoJSON(this,"zoo_edge_info.json");
        exitItem = new ExhibitItem("entrance_exit_gate","Entrance and Exit Gate", vInfo.get("entrance_exit_gate").kind,new Tags(exitTags));
        exitItem.setLat(vInfo.get("entrance_exit_gate").lat);
        exitItem.setLng(vInfo.get("entrance_exit_gate").lng);
        ordered.add(exitItem);


        //Refactoring : create ExhibitsList
        createExhibitsList();

        //Shortest Route created
        //ShortestDistance shortDist = new ShortestDistance(g,ordered);
        OffRouteDetection ord = new OffRouteDetection(currentLocation,ExhibitsList);
        Log.d("ExhibitList", ExhibitsList.toString());
        Log.d("ordNearest",ord.nearest().getId());
        if(ord.nearest().getParentId().equals("NULLNULLNULL")) {
            nearest = ord.nearest().getId();
        }
        else {
            nearest = ord.nearest().getParentId();
        }
        rou = DijkstraShortestPath.findPathBetween(g,nearest, ordered.get(count).getId());


        //Set up Adapter
        adapter = new DirectionsAdapter();
        adapter.setHasStableIds(true);

        // Set title of activity (text at top)
        TextView wT = findViewById(R.id.whereTo);
        if(whereToCount < ordered.size()){
            wT.setText("Directions to " + ordered.get(whereToCount).getExhibitName());
        }
        else{
            wT.setText("Directions to Exit");
        }

        recyclerView = findViewById(R.id.directions_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        Path p;

        //Log.d("currentRoute", route.toString());

        for(IdentifiedWeightedEdge e : rou.getEdgeList()) {
            p = new Path(vInfo.get(g.getEdgeSource(e).toString()).name,
                    vInfo.get(g.getEdgeTarget(e).toString()).name
                    ,g.getEdgeWeight(e),
                    eInfo.get(e.getId()).street);
            pathsBetweenExhibits.add(p);
//           Log.d("newPath", p.toString());
        }

        //Refactoring filter class - filters our edges to make sure directions make sense directionally
        Filter filter = new Filter(g,vInfo,eInfo);
        pathsBetweenExhibits = filter.filtAndSwap(whereToCount,ordered,pathsBetweenExhibits);
        adapter.setRouteItems(pathsBetweenExhibits);

        /* Permissions Setup */
        if (permissionChecker.ensurePermissions()) return;

        /* Line for GPS detection */
        var listenToGps = getIntent().getBooleanExtra(EXTRA_LISTEN_TO_GPS, true);
        if (!listenToGps) setupLocationListener();
        saveProfile();
        SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("lastActivity", getClass().getName());
        editor.commit();
    }

    public void onNextClicked(View view) {
        if(count < ordered.size() - 1) {
            reverse = false;
            forward = true;

            nextPath = new ArrayList<Path>();

            //Log.d("Click", route.get(count).toString()); //Used for debugging

            Path p;

            // Create Initial NextPath
            count++;
            rou = DijkstraShortestPath.findPathBetween(g,nearest, ordered.get(count).getId());
            for (IdentifiedWeightedEdge e : rou.getEdgeList()) {
                p = new Path(vInfo.get(g.getEdgeSource(e).toString()).name,
                        vInfo.get(g.getEdgeTarget(e).toString()).name
                        , g.getEdgeWeight(e),
                        eInfo.get(e.getId()).street);
                nextPath.add(p);
            }

            //Filter and swap directions if necessary
            whereToCount++;


            //Refactoring - filters nextPath so directions make sense
            Filter filter = new Filter(g,vInfo,eInfo);
            nextPath = filter.filtAndSwap(whereToCount,ordered,nextPath);


            // Once ordered list has been traversed we will set up check if we need to swap directions
            // on final directions to exit.


            if(dir) {
                Path bp;
                for (int i = 0; i < nextPath.size() - 1; i++) {
                    if (nextPath.get(i).getStreet().equals(nextPath.get(i + 1).getStreet())) {
                        bp = new Path(nextPath.get(i).getSource(),
                                nextPath.get(i + 1).getTarget(),
                                nextPath.get(i).getDistance() + nextPath.get(i + 1).getDistance(),
                                nextPath.get(i).getStreet());

                        nextPath.set(i, bp);
                        nextPath.remove(i + 1);
                        i--;
                    }
                }
                adapter.setRouteItems(nextPath);
            }
            else {
                adapter.setRouteItems(nextPath);
            }
            //Log.d("CheckNext", nextPath.toString()); //Used for debugging


            //Updates title of page with correct exhibit we are trying to get to
            TextView wT = findViewById(R.id.whereTo);
            if (whereToCount < ordered.size() ) {
                wT.setText("Directions to " + ordered.get(whereToCount).getExhibitName());
            }
            else {
                wT.setText("Directions to Exit");
            }
        }
        else {
            //Warning shows up once route is completed
            Utilities.showAlert(this, "Route is completed");
        }
        saveProfile();
    }

    public void onPreviousClicked(View view) {
        if(count ==  0) {
            Utilities.showAlert(this,"No previous directions available");
        }
        else {
            reverse = true;
            forward = false;
            prevPath = new ArrayList<Path>();
            Path p;
            count--;
            rou = DijkstraShortestPath.findPathBetween(g,nearest, ordered.get(count).getId());
            for(IdentifiedWeightedEdge e : rou.getEdgeList()) {
                p = new Path(vInfo.get(g.getEdgeSource(e).toString()).name,
                        vInfo.get(g.getEdgeTarget(e).toString()).name,
                        g.getEdgeWeight(e),
                        eInfo.get(e.getId()).street);
                prevPath.add(p);
            }

            //Filter and swap directions if neccesary
            whereToCount--;
            //Log.d("orderedPrev",ordered.toString());
            //Log.d("WTC", String.valueOf(whereToCount));
            //Log.d("l",prevPath.get(0).getTarget());
            //Log.d("source",prevPath.get(0).getSource());
            Filter filter = new Filter(g,vInfo,eInfo);
            prevPath = filter.filtAndSwap(whereToCount,ordered,prevPath);

            //reverse every path in prevPath so directions make sense

            //Set Top Text
            TextView wT = findViewById(R.id.whereTo);
            if (whereToCount < ordered.size() ) {
                wT.setText("Directions to " + ordered.get(whereToCount).getExhibitName());
            }
            else {
                wT.setText("Directions to Exit");
            }

            if(dir) {
                Path bp;
                for (int i = 0; i < prevPath.size() - 1; i++) {
                    if (prevPath.get(i).getStreet().equals(prevPath.get(i + 1).getStreet())) {
                        bp = new Path(prevPath.get(i).getSource(),
                                prevPath.get(i + 1).getTarget(),
                                prevPath.get(i).getDistance() + prevPath.get(i + 1).getDistance(),
                                prevPath.get(i).getStreet());

                        prevPath.set(i, bp);
                        prevPath.remove(i + 1);
                        i--;
                    }
                }
                adapter.setRouteItems(prevPath);
            }
            else {
                adapter.setRouteItems(prevPath);
            }


        }
        saveProfile();
    }

    public void onCancelDirectionsClicked(View view) {
        Intent intent = new Intent(this, Zoo_activity.class);
        startActivity(intent);
    }


    public void onSettingsClicked(View view) {
        Path p;
        if(!forward && !reverse){
            currPath = new ArrayList<Path>();
            for(IdentifiedWeightedEdge e : rou.getEdgeList()) {
                p = new Path(vInfo.get(g.getEdgeSource(e).toString()).name,
                        vInfo.get(g.getEdgeTarget(e).toString()).name,
                        g.getEdgeWeight(e),
                        eInfo.get(e.getId()).street);
                currPath.add(p);
            }
            //Filter amd swap directions if necessary
            Filter filter = new Filter(g,vInfo,eInfo);
            currPath = filter.filtAndSwap(whereToCount,ordered,currPath);

            //Check settings of Directions
            if (!dir) {
                Log.d("b4Brief", currPath.toString());
                //Brief Directions
                int k;
                Path bp;
                for (int i = 0; i < currPath.size() - 1; i++) {
                    if (currPath.get(i).getStreet().equals(currPath.get(i + 1).getStreet())) {
                        bp = new Path(currPath.get(i).getSource(),
                                currPath.get(i + 1).getTarget(),
                                currPath.get(i).getDistance() + currPath.get(i + 1).getDistance(),
                                currPath.get(i).getStreet());

                        currPath.set(i, bp);
                        currPath.remove(i + 1);
                        i--;
                    }

                }
                Log.d("after", currPath.toString());
                adapter.setRouteItems(currPath);
                Log.d("afterAd", currPath.toString());
                dir = true;

            }
            else {
                adapter.setRouteItems(currPath);
                dir = false;
            }



        }
        else if(forward && !reverse) {
            //Rebuild nextPath
            nextPath.clear();
            for (IdentifiedWeightedEdge e : rou.getEdgeList()) {
                p = new Path(vInfo.get(g.getEdgeSource(e).toString()).name,
                        vInfo.get(g.getEdgeTarget(e).toString()).name
                        , g.getEdgeWeight(e),
                        eInfo.get(e.getId()).street);
                nextPath.add(p);
            }
            //Swap directions if Necessary
            Filter filter = new Filter(g,vInfo,eInfo);
            filter.filtAndSwap(whereToCount,ordered,nextPath);

            //Check settings of Directions
            if (!dir) {
                Log.d("b4Brief", nextPath.toString());
                //Brief Directions
                int k;
                Path bp;
                for (int i = 0; i < nextPath.size() - 1; i++) {
                    if (nextPath.get(i).getStreet().equals(nextPath.get(i + 1).getStreet())) {
                        bp = new Path(nextPath.get(i).getSource(),
                                nextPath.get(i + 1).getTarget(),
                                nextPath.get(i).getDistance() + nextPath.get(i + 1).getDistance(),
                                nextPath.get(i).getStreet());

                        nextPath.set(i, bp);
                        nextPath.remove(i + 1);
                        i--;
                    }

                }
                Log.d("after", nextPath.toString());
                adapter.setRouteItems(nextPath);
                Log.d("afterAd", nextPath.toString());
                dir = true;

            }
            else {
                adapter.setRouteItems(nextPath);
                dir = false;
            }
        }
        else if(!forward && reverse) {
            //Rebuild prevPath
            prevPath.clear();
            for(IdentifiedWeightedEdge e : rou.getEdgeList()) {
                p = new Path(vInfo.get(g.getEdgeSource(e).toString()).name,
                        vInfo.get(g.getEdgeTarget(e).toString()).name,
                        g.getEdgeWeight(e),
                        eInfo.get(e.getId()).street);
                prevPath.add(p);
            }

            //Swap directions if necessary
            //Collections.reverse(prevPath);
            Filter filter = new Filter(g,vInfo,eInfo);
            prevPath = filter.filtAndSwap(whereToCount,ordered,prevPath);

            //Check settings of Directions
            if (!dir) {
                Log.d("b4Brief", prevPath.toString());
                //Brief Directions
                int k;
                Path bp;
                for (int i = 0; i < prevPath.size() - 1; i++) {
                    if (prevPath.get(i).getStreet().equals(prevPath.get(i + 1).getStreet())) {
                        bp = new Path(prevPath.get(i).getSource(),
                                prevPath.get(i + 1).getTarget(),
                                prevPath.get(i).getDistance() + prevPath.get(i + 1).getDistance(),
                                prevPath.get(i).getStreet());

                        prevPath.set(i, bp);
                        prevPath.remove(i + 1);
                        i--;
                    }

                }
                Log.d("after", prevPath.toString());
                adapter.setRouteItems(prevPath);
                Log.d("afterAd", prevPath.toString());
                dir = true;

            }
            else {
                adapter.setRouteItems(prevPath);
                dir = false;
            }
        }
    }


    @SuppressLint("MissingPermission")
    private void setupLocationListener() {
        new PermissionChecker(this).ensurePermissions();

        // Connect location listener to the model.
        var provider = LocationManager.GPS_PROVIDER;
        var locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        var locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
//                var coords = Pair.create(
//                        location.getLatitude(),
//                        location.getLongitude()
//                );
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                if ((currentLocation.latitude != lat) || (currentLocation.longitude != lng)) {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    Log.d("currentLocation", currentLocation.toString());



                    ChangedLocation();
                }
            }
        };
        locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
    }


    public void ChangedLocation() {
        // Check if closest
        List<ExhibitItem> unvisited = ordered.subList(whereToCount, ordered.size());

        // Use offRoute detection to output the closest exhibits to the current location
        OffRouteDetection ord = new OffRouteDetection(currentLocation,ExhibitsList);
        ExhibitItem nearestExhibitItem = ord.nearest();

        // Check if it has parents id, only records parents
        if(ord.nearest().getParentId().equals("NULLNULLNULL")) {
            nearest = ord.nearest().getId();
        }
        else {
            nearest = ord.nearest().getParentId();
        }

        // Find the shortest Dijkstra path to
        rou = DijkstraShortestPath.findPathBetween(g,nearest, ordered.get(count).getId());


        // if we are not at the exact exhibit as before
        if (nearest != ordered.get(whereToCount).getId()) {

            Boolean replan = false;
            // Check if the current location is at an exhibit later in plan
            Double closestDist = Double.POSITIVE_INFINITY;

            // This loops through the exhibits on the rest of the list
            // Check if we are closer to any exhibits on the rest of the list, if so, need replan
            for (ExhibitItem i : unvisited) {
                Log.d("i", i.getId());
                GraphPath currentPath = DijkstraShortestPath.findPathBetween(g, nearest, i.getId());
                Double currentDist = currentPath.getWeight();

                if (currentDist < closestDist) {
                    closestDist = currentDist;
                    nearestInList = i;
                }
            }


            // If we are closer to an exhibit later in plan instead of the current destination
            // Set off-route replan to true
            if (nearestInList.getId() != ordered.get(count).getId()) {
                replan = true;
            }

            Log.d("offRouteBoolean", replan.toString());

            // If we are off-route (closer to an exhibit later in plan)
            if (replan == true) {

                // First set up alarm asking if the user wants to be re-routed
                var inputType = EditorInfo.TYPE_CLASS_NUMBER
                        | EditorInfo.TYPE_NUMBER_FLAG_SIGNED
                        | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL;


                final LinearLayout layout = new LinearLayout(this);
                layout.setDividerPadding(8);
                layout.setOrientation(LinearLayout.VERTICAL);

                var builder = new AlertDialog.Builder(this)
                        .setTitle("You are off-route, Reroute?")
                        .setView(layout)
                        // If the user wants to replan
                        .setPositiveButton("Sure!", (dialog, which) -> {
                            //If the user wants to be re-directed

                            //First remove the entrance/exit gate because that would always be at the end
                            unvisited.remove(unvisited.size()-1);

                            // Replan the rest of the exhibits
                            ShortestDistanceGreedy shortestDistanceGreedy = new ShortestDistanceGreedy(g, unvisited, nearestInList);
                            List<GraphPath<String, IdentifiedWeightedEdge>> nextHalf = shortestDistanceGreedy.getShortest();

                            ArrayList<String> singlePath = new ArrayList<String>();
                            //Extract GraphPaths from route and create ArrayList of endVertex names
                            //Also sum up distance
                            for (GraphPath<String, IdentifiedWeightedEdge> path : nextHalf) {
                                singlePath.add(path.getEndVertex());
                            }


                            // output the new plan for the unvisited exhibits
                            ArrayList<ExhibitItem> orderedUnvisited = new ArrayList<ExhibitItem>();
                            for (int i = 0; i < singlePath.size(); i++) {
                                for (ExhibitItem exItem : unvisited) {
                                    if (exItem.getId().equals(singlePath.get(i)) || exItem.getParentId().equals(singlePath.get(i))) {
                                        orderedUnvisited.add(exItem);
                                    }
                                }
                            }

                            // in order to create the whole list, we need the visited exhibits plan
                            List<ExhibitItem> visited = ordered.subList(0, whereToCount);

                            ArrayList<ExhibitItem> totalList = new ArrayList<ExhibitItem>();

                            totalList.addAll(visited);
                            if (nearestExhibitItem.getParentId() == "NULLNULLNULL") {
                                totalList.add(nearestExhibitItem);
                            } else {
                                for (ExhibitItem i : ExhibitsList) {
                                    if (i.getId().equals(nearestExhibitItem.getParentId())) {
                                        totalList.add(i);
                                    }
                                }
//
                            }

                            // now assign the new list or plan to ordered
                            totalList.addAll(orderedUnvisited);
                            ordered = totalList;
                            ordered.add(exitItem);
                            Log.d("newOrdered", ordered.toString());


                            // We need to display whatever we have outputed next
                            nextPath = new ArrayList<Path>();

                            //Log.d("Click", route.get(count).toString()); //Used for debugging

                            Path p;

                            // Create Initial NextPath
                            count++;
                            rou = DijkstraShortestPath.findPathBetween(g,nearest, ordered.get(count).getId());
                            for (IdentifiedWeightedEdge e : rou.getEdgeList()) {
                                p = new Path(vInfo.get(g.getEdgeSource(e).toString()).name,
                                        vInfo.get(g.getEdgeTarget(e).toString()).name
                                        , g.getEdgeWeight(e),
                                        eInfo.get(e.getId()).street);
                                nextPath.add(p);
                            }

                            //Filter and swap directions if necessary
                            whereToCount++;
                            Filter filter = new Filter(g,vInfo,eInfo);
                            nextPath = filter.filtAndSwap(whereToCount,ordered,nextPath);

                            if(dir) {
                                Path bp;
                                for (int i = 0; i < nextPath.size() - 1; i++) {
                                    if (nextPath.get(i).getStreet().equals(nextPath.get(i + 1).getStreet())) {
                                        bp = new Path(nextPath.get(i).getSource(),
                                                nextPath.get(i + 1).getTarget(),
                                                nextPath.get(i).getDistance() + nextPath.get(i + 1).getDistance(),
                                                nextPath.get(i).getStreet());

                                        nextPath.set(i, bp);
                                        nextPath.remove(i + 1);
                                        i--;
                                    }
                                }
                                adapter.setRouteItems(nextPath);
                            }
                            else {
                                adapter.setRouteItems(nextPath);
                            }
                            //Log.d("CheckNext", nextPath.toString()); //Used for debugging


                            //Updates title of page with correct exhibit we are trying to get to
                            TextView wT = findViewById(R.id.whereTo);
                            if (whereToCount < ordered.size() ) {
                                wT.setText("Directions to " + ordered.get(whereToCount).getExhibitName());
                            }
                            else {
                                wT.setText("Directions to Exit");
                            }
                        })
                        .setNegativeButton("Mind your business :(", (dialog, which) -> {
                            dialog.cancel();
                        });
                builder.show();

                // If we only need to replan
            } else {
                //Show notification that our location changed
                Utilities.showAlert(this, "Current Location Changes!");

                Path p;

                Log.d("nearestNow", nearest);
                // Create Initial NextPath
                nextPath.clear();
                rou = DijkstraShortestPath.findPathBetween(g,nearest, ordered.get(count).getId());
                for (IdentifiedWeightedEdge e : rou.getEdgeList()) {
                    p = new Path(vInfo.get(g.getEdgeSource(e).toString()).name,
                            vInfo.get(g.getEdgeTarget(e).toString()).name
                            , g.getEdgeWeight(e),
                            eInfo.get(e.getId()).street);
                    nextPath.add(p);
                }

                //Filter and swap directions if necessary
                Filter filter = new Filter(g,vInfo,eInfo);
                nextPath = filter.filtAndSwap(whereToCount,ordered,nextPath);


                if(dir) {
                    Path bp;
                    for (int i = 0; i < nextPath.size() - 1; i++) {
                        if (nextPath.get(i).getStreet().equals(nextPath.get(i + 1).getStreet())) {
                            bp = new Path(nextPath.get(i).getSource(),
                                    nextPath.get(i + 1).getTarget(),
                                    nextPath.get(i).getDistance() + nextPath.get(i + 1).getDistance(),
                                    nextPath.get(i).getStreet());

                            nextPath.set(i, bp);
                            nextPath.remove(i + 1);
                            i--;
                        }
                    }
                    adapter.setRouteItems(nextPath);
                }
                else {
                    adapter.setRouteItems(nextPath);
                }
                //Log.d("CheckNext", nextPath.toString()); //Used for debugging


                //Updates title of page with correct exhibit we are trying to get to
                TextView wT = findViewById(R.id.whereTo);
                if (whereToCount < ordered.size() ) {
                    wT.setText("Directions to " + ordered.get(whereToCount).getExhibitName());
                }
                else {
                    wT.setText("Directions to Exit");
                }

            }
        }
        saveProfile();
    }


    public void onMockButtonClicked(View view) {
        var inputType = EditorInfo.TYPE_CLASS_NUMBER
                | EditorInfo.TYPE_NUMBER_FLAG_SIGNED
                | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL;

        final EditText latInput = new EditText(this);
        latInput.setInputType(inputType);
        latInput.setHint("Latitude");
        latInput.setText("32.73971798112842");

        final EditText lngInput = new EditText(this);
        lngInput.setInputType(inputType);
        lngInput.setHint("Longitude");
        lngInput.setText("-117.16644660080382");

        final LinearLayout layout = new LinearLayout(this);
        layout.setDividerPadding(8);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(latInput);
        layout.addView(lngInput);

        var builder = new AlertDialog.Builder(this)
                .setTitle("Inject a Mock Location")
                .setView(layout)
                .setPositiveButton("Submit", (dialog, which) -> {
                    var lat = Double.parseDouble(latInput.getText().toString());
                    var lng = Double.parseDouble(lngInput.getText().toString());
                    currentLocation = new LatLng(lat, lng);
                    Log.d("currentLocation", currentLocation.toString());

                    //See if we need to replan or not
                    ChangedLocation();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                });
        builder.show();
    }

    public void onSkipClicked(View view){
        if(count < ordered.size() - 1) {

            int nextE = whereToCount;
            ordered.remove(nextE);

            // Check if closest
            List<ExhibitItem> unvisited = ordered.subList(whereToCount, ordered.size());
            Log.d("unvisitedAtNewFunction", unvisited.toString());

            OffRouteDetection ord = new OffRouteDetection(currentLocation,ExhibitsList);
            Log.d("ordNearest",ord.nearest().getId());
            ExhibitItem nearestExhibitItem = ord.nearest();
            if(ord.nearest().getParentId().equals("NULLNULLNULL")) {
                nearest = ord.nearest().getId();
            }
            else {
                nearest = ord.nearest().getParentId();
            }
            rou = DijkstraShortestPath.findPathBetween(g,nearest, ordered.get(count).getId());

            nextPath = new ArrayList<Path>();

            Path p;
            // Create Initial NextPath

            rou = DijkstraShortestPath.findPathBetween(g,nearest, ordered.get(count).getId());
            for (IdentifiedWeightedEdge e : rou.getEdgeList()) {
                p = new Path(vInfo.get(g.getEdgeSource(e).toString()).name,
                        vInfo.get(g.getEdgeTarget(e).toString()).name
                        , g.getEdgeWeight(e),
                        eInfo.get(e.getId()).street);
                nextPath.add(p);
            }

            //Filter and swap directions if necessary
            Filter filter = new Filter(g,vInfo,eInfo);
            nextPath = filter.filtAndSwap(whereToCount,ordered,nextPath);

            if(dir) {
                Path bp;
                for (int i = 0; i < nextPath.size() - 1; i++) {
                    if (nextPath.get(i).getStreet().equals(nextPath.get(i + 1).getStreet())) {
                        bp = new Path(nextPath.get(i).getSource(),
                                nextPath.get(i + 1).getTarget(),
                                nextPath.get(i).getDistance() + nextPath.get(i + 1).getDistance(),
                                nextPath.get(i).getStreet());

                        nextPath.set(i, bp);
                        nextPath.remove(i + 1);
                        i--;
                    }
                }
                adapter.setRouteItems(nextPath);
            }
            else {
                adapter.setRouteItems(nextPath);
            }
            //Log.d("CheckNext", nextPath.toString()); //Used for debugging


            //Updates title of page with correct exhibit we are trying to get to
            TextView wT = findViewById(R.id.whereTo);
            if (whereToCount < ordered.size() ) {
                wT.setText("Directions to " + ordered.get(whereToCount).getExhibitName());
            }
            else {
                wT.setText("Directions to Exit");
            }
        }
        else if(count < ordered.size()-1){
            //Warning shows up once route is completed
            Utilities.showAlert(this, "No more exhibits, press next to go to exit");
        }
        else{
            Utilities.showAlert(this, "Route is complete.");
        }
        saveProfile();
    }
    public void loadProfile(){
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonOrdered = preferences.getString("Ordered", null);
        if(jsonOrdered != null){
            Type type2 = new TypeToken<List<ExhibitItem>>(){}.getType();
            ordered = gson.fromJson(jsonOrdered, type2);
            count = preferences.getInt("Count", 0);
            whereToCount = preferences.getInt("WCount", 0);
        }
    }
    public void saveProfile(){
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        //String json = gson.toJson(route);
        String jsonOrder = gson.toJson(ordered);
        int countData = count;
        int whereToCountData = whereToCount;
        //editor.putString("Route", json);
        editor.putString("Ordered", jsonOrder);
        editor.putInt("Count", countData);
        editor.putInt("WCount", whereToCountData);
        editor.commit();
    }

    public void createExhibitsList() {
        ExhibitItem e0;
        for (ZooData.VertexInfo node : vInfo.values()) {
            e0 = new ExhibitItem(node.id,node.name,node.kind,new Tags(node.tags));
            if (node.parent_id != null){
                e0.setParentId(node.parent_id);
                e0.setParentName(vInfo.get(node.parent_id).name);
                e0.setLat(vInfo.get(node.parent_id).lat);
                e0.setLng(vInfo.get(node.parent_id).lng);
                Log.d("currentLatNameWithParents", node.id);
                Log.d("currentLatWithParents", vInfo.get(node.parent_id).lat.toString());
            } else {
                e0.setLat(vInfo.get(node.id).lat);
                Log.d("currentLatName", node.name);
                Log.d("currentLat", vInfo.get(node.id).lat.toString());
                e0.setLng(vInfo.get(node.id).lng);
            }
            ExhibitsList.add(e0);
            Log.d("ZooData", node.name);
        }
    }


}