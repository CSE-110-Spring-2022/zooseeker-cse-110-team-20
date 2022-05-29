package com.example.zookeeperteam20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DirectionsActivity extends AppCompatActivity {
    public static final String EXTRA_LISTEN_TO_GPS = "listen_to_gps";

    boolean dir = false;
    int count = 0;
    public RecyclerView recyclerView;
    List<GraphPath<String, IdentifiedWeightedEdge>> route;
    ArrayList<Path> pathsBetweenExhibits = new ArrayList<Path>();
    Graph<String, IdentifiedWeightedEdge> g;
    DirectionsAdapter adapter = new DirectionsAdapter();
    Map<String, ZooData.VertexInfo> vInfo;
    Map<String, ZooData.EdgeInfo> eInfo;
    int whereToCount = 0;
    ArrayList<ExhibitItem> ordered = new ArrayList<ExhibitItem>();
    ArrayList<Path> nextPath = new ArrayList<Path>();
    ArrayList<Path> prevPath = new ArrayList<Path>();
    ArrayList<Path> currPath = new ArrayList<Path>();
    LatLng currentLocation = new LatLng(32.737986, -117.169499);

    boolean listenToGps = false;

    //    @SuppressLint("MissingPermission")


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final PermissionChecker permissionChecker = new PermissionChecker(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        // First check permission.


        // Extract ordered from PlanActivity
        ordered = (ArrayList<ExhibitItem>) getIntent().getSerializableExtra("Directions");
        Log.d("Directions UI", ordered.toString());

        //Load Graph, VetexInfo, and EdgeInfo
        g = ZooData.loadZooGraphJSON(this, "zoo_graph.json");
        vInfo = ZooData.loadVertexInfoJSON(this,"zoo_node_info.json");
        eInfo = ZooData.loadEdgeInfoJSON(this,"zoo_edge_info.json");
        //Shortest Route created
        ShortestDistance shortDist = new ShortestDistance(g,ordered);
        route = shortDist.getShortest();
        adapter = new DirectionsAdapter();
        //DirectionsAdapter adapter = new DirectionsAdapter();
        adapter.setHasStableIds(true);

        // Set title of activity (text at top)
        TextView wT = findViewById(R.id.whereTo);
        wT.setText("Directions to " + ordered.get(whereToCount).getExhibitName());


       recyclerView = findViewById(R.id.directions_list);
       recyclerView.setLayoutManager(new LinearLayoutManager(this));
       recyclerView.setAdapter(adapter);
       Path p;

       Log.d("currentRoute", route.toString());

       for(IdentifiedWeightedEdge e : route.get(0).getEdgeList()) {
           p = new Path(vInfo.get(g.getEdgeSource(e).toString()).name,
                   vInfo.get(g.getEdgeTarget(e).toString()).name
                   ,g.getEdgeWeight(e),
                   eInfo.get(e.getId()).street);
           pathsBetweenExhibits.add(p);
//           Log.d("newPath", p.toString());
       }




       //Filter amd swap directions if necessary
        if(whereToCount < ordered.size() ) {
            for (int i = pathsBetweenExhibits.size() - 1; i >= 0; i--) {
                if (i == pathsBetweenExhibits.size() - 1) {
                    if (pathsBetweenExhibits.get(i).getTarget().equals(ordered.get(whereToCount).getExhibitName()) != true) {
                        pathsBetweenExhibits.get(i).swap();
                    }
                } else {
                    if (pathsBetweenExhibits.get(i).getTarget().equals(pathsBetweenExhibits.get(i + 1).getSource()) != true) {
                        pathsBetweenExhibits.get(i).swap();
                    }
                }
            }
        }
        adapter.setRouteItems(pathsBetweenExhibits);

        /* Permissions Setup */
//        if (permissionChecker.ensurePermissions()) return;

        /* Line for GPS detection */
        var listenToGps = getIntent().getBooleanExtra(EXTRA_LISTEN_TO_GPS, true);
        if (!listenToGps) setupLocationListener();
    }

    public void onNextClicked(View view) {
        if(count < route.size() - 1) {
            nextPath = new ArrayList<Path>();

            Log.d("Click", route.get(count).toString()); //Used for debugging

            Path p;

            // Create Initial NextPath
            count++;
            for (IdentifiedWeightedEdge e : route.get(count).getEdgeList()) {
                p = new Path(vInfo.get(g.getEdgeSource(e).toString()).name,
                        vInfo.get(g.getEdgeTarget(e).toString()).name
                        , g.getEdgeWeight(e),
                        eInfo.get(e.getId()).street);
                nextPath.add(p);
            }

            //Filter and swap directions if necessary
            whereToCount++;
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

            if(!dir) {
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

    }

    public void onPreviousClicked(View view) {
        if(count ==  0) {
            Utilities.showAlert(this,"No previous directions available");
        }
        else {

            prevPath = new ArrayList<Path>();
            Path p;

            for(IdentifiedWeightedEdge e : route.get(count).getEdgeList()) {
                p = new Path(vInfo.get(g.getEdgeSource(e).toString()).name,
                        vInfo.get(g.getEdgeTarget(e).toString()).name,
                        g.getEdgeWeight(e),
                        eInfo.get(e.getId()).street);
                prevPath.add(p);
            }

            //Filter and swap directions if neccesary
            whereToCount--;
            Log.d("orderedPrev",ordered.toString());
            Log.d("WTC", String.valueOf(whereToCount));
            Log.d("l",prevPath.get(0).getTarget());
            Log.d("source",prevPath.get(0).getSource());
            Collections.reverse(prevPath);
            if(whereToCount < ordered.size() ) {
                for(int i = prevPath.size() - 1; i >= 0 ; i--) {
                    if (i == prevPath.size() - 1) {
                        if(prevPath.get(i).getTarget().equals(ordered.get(whereToCount).getExhibitName()) != true) {
                            prevPath.get(i).swap();
                            Log.d("l2",prevPath.get(0).getTarget());
                            Log.d("source2",prevPath.get(0).getSource());
                        }
                    } else {
                        if(prevPath.get(i).getTarget().equals(prevPath.get(i+1).getSource()) != true) {
                            prevPath.get(i).swap();
                        }
                    }
                }
            }

            //reverse every path in prevPath so directions make sense




            count--;



            adapter.setRouteItems(prevPath);
            TextView wT = findViewById(R.id.whereTo);
            if (whereToCount < ordered.size() ) {
                wT.setText("Directions to " + ordered.get(whereToCount).getExhibitName());
            }
            else {
                wT.setText("Directions to Exit");
            }

        }
    }

    public void onCancelDirectionsClicked(View view) {
        Intent intent = new Intent(this, Zoo_activity.class);
        startActivity(intent);
    }


    public void onSettingsClicked(View view) {
        currPath = new ArrayList<Path>();
        Path p;

        for(IdentifiedWeightedEdge e : route.get(count).getEdgeList()) {
            p = new Path(vInfo.get(g.getEdgeSource(e).toString()).name,
                    vInfo.get(g.getEdgeTarget(e).toString()).name,
                    g.getEdgeWeight(e),
                    eInfo.get(e.getId()).street);
            currPath.add(p);
        }
        if (!dir) {
            Log.d("b4Brief", pathsBetweenExhibits.toString());
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
        else{
            currPath.clear();
            for(IdentifiedWeightedEdge e : route.get(count).getEdgeList()) {
                p = new Path(vInfo.get(g.getEdgeSource(e).toString()).name,
                        vInfo.get(g.getEdgeTarget(e).toString()).name
                        ,g.getEdgeWeight(e),
                        eInfo.get(e.getId()).street);
                currPath.add(p);
            }


            //Filter amd swap directions if necessary
            if(whereToCount < ordered.size() ) {
                for (int i = currPath.size() - 1; i >= 0; i--) {
                    if (i == currPath.size() - 1) {
                        if (currPath.get(i).getTarget().equals(ordered.get(whereToCount).getExhibitName()) != true) {
                            currPath.get(i).swap();
                        }
                    } else {
                        if (currPath.get(i).getTarget().equals(currPath.get(i + 1).getSource()) != true) {
                            currPath.get(i).swap();
                        }
                    }
                }
            }
            adapter.setRouteItems(currPath);
            dir = false;
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
                }
            }
        };
        locationManager.requestLocationUpdates(provider, 0, 0f, locationListener);
    }


    public void onMockButtonClicked(View view) {
        var inputType = EditorInfo.TYPE_CLASS_NUMBER
                | EditorInfo.TYPE_NUMBER_FLAG_SIGNED
                | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL;

        final EditText latInput = new EditText(this);
        latInput.setInputType(inputType);
        latInput.setHint("Latitude");
        latInput.setText("32.737986");

        final EditText lngInput = new EditText(this);
        lngInput.setInputType(inputType);
        lngInput.setHint("Longitude");
        lngInput.setText("-117.169499");

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
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.cancel();
                });
        builder.show();
    }


}