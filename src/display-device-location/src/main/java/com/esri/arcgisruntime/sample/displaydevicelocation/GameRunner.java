package com.esri.arcgisruntime.sample.displaydevicelocation;

import java.util.List;
import java.lang.Math;
import java.util.ArrayList;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.location.LocationDataSource.Location;



public class GameRunner {
    public MapView mMapView;
    public GameRunner(MapView mMapView) {
        this.mMapView = mMapView;
    }

    public void mainLoop() {
        ArcGISMap map = mMapView.getMap();

        List<Item> items = new ArrayList();

        List<FeatureTable> ft = map.getTables();
        //iterate over features and check for collisions
        List<Feature> features = new ArrayList();
        for(FeatureTable currTable : ft) {
            QueryParameters qp = new QueryParameters();
            qp.setMaxFeatures(50);
            ListenableFuture<FeatureQueryResult> future = currTable.queryFeaturesAsync(qp);
            try {
                FeatureQueryResult currFeatures = future.get();
                for(Feature currFeature : currFeatures) {
                    features.add(currFeature);
                }
            } catch(Exception e) {
                System.out.println("what happened!");
            }
        }

        Location playerLoc = mMapView.getLocationDisplay().getLocation();
        Point playerPt = playerLoc.getPosition();
        double arbDiam = 10;
        Player player = new Player(playerPt.getX(), playerPt.getY(), arbDiam);
        for(Item currItem : items) {
            //check collision
            if(Math.sqrt(Math.pow((player.getLat() - currItem.getLatitude()),2) +
                    Math.pow((player.getLon() - currItem.getLongitude()),2)) <
                    (player.getDiameter() + currItem.getDiameter())) {
                ListenableFuture<Void> future = currItem.getFeature().getFeatureTable().deleteFeatureAsync(currItem.getFeature());
                try {
                    future.get();
                    items.remove(currItem);
                } catch(Exception e) {
                    System.out.println("I cannot begin to fathom how we got here!");
                    System.out.println(e.getMessage());
                }
            }
        }
        //wait one second possibly
        mainLoop();
    }
}
