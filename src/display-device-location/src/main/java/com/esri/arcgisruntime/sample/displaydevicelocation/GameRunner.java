package com.esri.arcgisruntime.sample.displaydevicelocation;

import java.util.List;
import java.util.ArrayList;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;


public class GameRunner {
    public MapView mMapView;
    public GameRunner(MapView mMapView) {
        this.mMapView = mMapView;
    }

    public void mainLoop() {
        ArcGISMap map = mMapView.getMap();
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
        //wait one second possibly
        mainLoop();
    }
}
