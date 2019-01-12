package com.esri.arcgisruntime.sample.displaydevicelocation;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;

import java.util.ArrayList;
import java.util.List;

public final class World {
    private ArrayList<Item> mItems;
    private Basemap mBasemap;
    private String mDescription;
    private Integer mSecondsToComplete;

    World(ArcGISMap map) {
        List<FeatureTable> ft = map.getTables();
        // iterate over features and check for collisions
        for (FeatureTable currTable : ft) {
            QueryParameters qp = new QueryParameters();
            qp.setMaxFeatures(50);
            ListenableFuture<FeatureQueryResult> future = currTable.queryFeaturesAsync(qp);
            try {
                FeatureQueryResult currFeatures = future.get();
                for (Feature currFeature : currFeatures) {
                    Item item = new Item(currFeature);
                    mItems.add(item);
                }
            } catch (Exception e) {
                System.out.println("what happened!");
            }
        }
    }

    public ArrayList<Item> getItems() { return mItems; }

    public void setItems(ArrayList<Item> items) { this.mItems = items; }

    public Basemap getBasemap() { return mBasemap; }

    public void setBasemap(Basemap basemap) { this.mBasemap = basemap; }

    public String getDescription() { return mDescription; }

    public void setDescription(String description) { this.mDescription = description; }

    public Integer getSecondsToComplete() { return mSecondsToComplete; }

    public void setSecondsToComplete(Integer seconds) { this.mSecondsToComplete = seconds; }
}