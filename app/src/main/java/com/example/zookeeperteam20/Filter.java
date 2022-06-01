package com.example.zookeeperteam20;

import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.Map;

public class Filter {
    Graph<String, IdentifiedWeightedEdge> g;
    Map<String, ZooData.VertexInfo> vInfo;
    Map<String, ZooData.EdgeInfo> eInfo;
    public Filter(Graph<String, IdentifiedWeightedEdge> g,Map<String, ZooData.VertexInfo> vInfo,Map<String, ZooData.EdgeInfo> eInfo){
        this.g = g;
        this.vInfo = vInfo;
        this.eInfo = eInfo;
    }

    public ArrayList<Path> filtAndSwap(int wtc, ArrayList<ExhibitItem> ordered, ArrayList<Path> path) {
        if(wtc < ordered.size() ) {
            for (int i = path.size() - 1; i >= 0; i--) {
                if (i == path.size() - 1) {
                    if (path.get(i).getTarget().equals(ordered.get(wtc).getExhibitName()) != true) {
                        path.get(i).swap();
                    }
                } else {
                    if (path.get(i).getTarget().equals(path.get(i + 1).getSource()) != true) {
                        path.get(i).swap();
                    }
                }
            }
        }
        else {
            for( int i = path.size() - 1; i >= 0; i--) {
                if(i == path.size() - 1) {
                    if (path.get(i).getTarget().equals("Entrance and Exit Gate") != true) {
                        path.get(i).swap();
                    }
                }
                else {
                    if(path.get(i).getTarget().equals(path.get(i + 1).getSource()) != true) {
                        path.get(i).swap();
                    }
                }
            }
        }
        return path;
    }



}
