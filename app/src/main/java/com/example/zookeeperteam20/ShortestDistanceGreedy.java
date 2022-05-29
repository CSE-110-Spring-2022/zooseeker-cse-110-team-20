package com.example.zookeeperteam20;

import android.util.Log;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.List;


public class ShortestDistanceGreedy {
    // Initial Parameters for constructing graphs
    Graph<String, IdentifiedWeightedEdge> g;
    String STARTEND = "entrance_exit_gate";
    ArrayList<ExhibitItem> noRepeats;
    ExhibitItem start;


    ShortestDistanceGreedy(Graph<String, IdentifiedWeightedEdge> g,
                           ArrayList<ExhibitItem> noRepeats,
                           ExhibitItem start) {
        // takes a Graph object and noRepeats for construction.
        this.g = g;
        this.noRepeats = noRepeats;
        this.start = start;
    }

    // This method will output the shortest routes to visit all selected exhibits in noRepeats.
    public List<GraphPath<String, IdentifiedWeightedEdge>> getShortest() {
        // ArrayList that contains all unvisited exhibitions that we intend to visit
        ArrayList<String> unvisited = new ArrayList<String>();

        // We obtain such list from the para noRepeats input obtained from UI
        String name = "";
        for (ExhibitItem item : noRepeats) {
            if(!item.getParentId().equals("NULLNULLNULL")) {
                 name = item.getParentId();
            }
            else {
                 name = item.getId();
            }
            unvisited.add(name);
        }

        // Start from the input start
        String startName = "";
        if(!start.getParentId().equals("NULLNULLNULL")) {
            startName = start.getParentId();
        }
        else {
            startName = start.getId();
        }
        String startNode = startName;

        // Initiate the array of GraphPath, where each entry represent a path to the next
        // closest exhibits.
        List<GraphPath<String, IdentifiedWeightedEdge>> totalPath = new ArrayList<GraphPath<String, IdentifiedWeightedEdge>>();

        // Remove the entrance gate node from the unvisited list because it is already visited
        unvisited.remove(startNode);

        if (unvisited.size() == 1) {
            Log.d("unvisited",unvisited.toString());
            Log.d("g",g.toString());
            totalPath.add(DijkstraShortestPath.findPathBetween(g, STARTEND, unvisited.get(0)));
            startNode = unvisited.get(0);
        } else {

            // If thee are still unvisited exhibits remaining, we will find the path to the closest
            // exhibits in the unvisited list, and so-on until we visit all desired exhibits.
            while (unvisited.size() > 1) {
                // We have visited our current starting exhibits
                unvisited.remove(startNode);

                // Initialize a minimal distance param
                double minDist = Double.POSITIVE_INFINITY;

                // minimal path would be in minPath
                GraphPath<String, IdentifiedWeightedEdge> minPath = null;

                // Find the closest next exhibit to the current location, then return the path and dist.
                for (String endNode : unvisited) {
                    Log.d("Check", startNode + endNode);
                    GraphPath<String, IdentifiedWeightedEdge> path =
                            DijkstraShortestPath.findPathBetween(g, startNode, endNode);
                    if (minDist > path.getWeight()) {
                        minDist = path.getWeight();
                        minPath = path;
                    }
                }

                // Add the path to the closest exhibit to the total path list.
                totalPath.add(minPath);

                // Move current location to the next one.
                startNode = minPath.getEndVertex();
            }
        }

        // We have reached all destinations, exit the zoo!
        totalPath.add(DijkstraShortestPath.findPathBetween(g, startNode, STARTEND));

        return totalPath;
    }

}
