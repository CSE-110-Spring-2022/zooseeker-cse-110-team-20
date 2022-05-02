package com.example.zookeeperteam20;

import android.util.Log;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.*;


public class ShortestDistance {
    Graph<String, IdentifiedWeightedEdge> g;
    String STARTEND = "entrance_exit_gate";
    ArrayList<ExhibitItem> noRepeats;
    ArrayList<String> testing;


    ShortestDistance(Graph<String, IdentifiedWeightedEdge> g, ArrayList<ExhibitItem> noRepeats) {
        this.g = g;
        this.noRepeats = noRepeats;
    }

    // Poly constructor for testing purposes
//    ShortestDistance(Graph<String, IdentifiedWeightedEdge> g, ArrayList<String> testing) {
//        this.g = g;
//        this.testing = testing;
//    }

    public List<GraphPath<String, IdentifiedWeightedEdge>> getShortest() {
        List<String> unvisited = new ArrayList<String>();

        for (ExhibitItem item : noRepeats) {
            String name = item.getId();
            unvisited.add(name);
        }
//        for (String item : testing) {
//            unvisited.add(item);
//        }

        String startNode = STARTEND;
        List<GraphPath<String, IdentifiedWeightedEdge>> totalPath = new ArrayList<GraphPath<String, IdentifiedWeightedEdge>>();

        Log.d("unvisitied", unvisited.toString());
        unvisited.remove(startNode);
        Log.d("GraphTag",g.toString());

        while (unvisited.size() > 1) {
            unvisited.remove(startNode);
            Log.d("current unvisited", unvisited.toString());

            double minDist = Double.POSITIVE_INFINITY;
            GraphPath<String, IdentifiedWeightedEdge> minPath = null;


            for (String endNode : unvisited) {
                Log.d("Check", startNode + endNode);
                GraphPath<String, IdentifiedWeightedEdge> path = DijkstraShortestPath.findPathBetween(g, startNode, endNode);
                if (minDist > path.getWeight()) {
                    minDist = path.getWeight();
                    minPath = path;
                }
            }
            totalPath.add(minPath);

            Log.d("minimum path", minPath.toString());
            startNode = minPath.getEndVertex();
        }

        // We have reached all destinatinos, exit the zoo!
        totalPath.add(DijkstraShortestPath.findPathBetween(g, startNode, STARTEND));

        return totalPath;
    }

}
