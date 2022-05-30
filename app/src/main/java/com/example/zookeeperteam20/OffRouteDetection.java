package com.example.zookeeperteam20;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.jgrapht.Graph;

import java.util.ArrayList;
import java.util.List;

public class OffRouteDetection {
    String STARTEND = "entrance_exit_gate";
    LatLng currentLocation;
    List<ExhibitItem> noRepeats;


    OffRouteDetection(LatLng start, List<ExhibitItem> noRepeats) {
        this.currentLocation = start;
        this.noRepeats = noRepeats;
    }

    ExhibitItem nearest() {

        // We obtain such list from the para noRepeats input obtained from UI
        Double minDist = Double.POSITIVE_INFINITY;;
        ExhibitItem closest = null;


        for (ExhibitItem item : noRepeats) {
            Double itemLat = item.getLat();
            Double itemLng = item.getLng();
            Log.d("itemLat", itemLat.toString());
            Double latDiff = currentLocation.latitude - itemLat;
            Double lngDiff = currentLocation.longitude - itemLng;
            Double curDist = Math.sqrt((latDiff*latDiff)+(lngDiff*lngDiff));

            if (curDist < minDist) {
                minDist = curDist;
                closest = item;
            }
        }

        return closest;
    }
}
