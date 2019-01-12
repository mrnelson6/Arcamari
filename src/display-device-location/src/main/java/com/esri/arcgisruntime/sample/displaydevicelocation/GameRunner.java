package com.esri.arcgisruntime.sample.displaydevicelocation;

import java.util.List;
import java.lang.Math;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.location.LocationDataSource.Location;



public class GameRunner {
    public MapView mMapView;
    public World world;
    public GameRunner(MapView mMapView) {
        this.mMapView = mMapView;
        world = new World(mMapView.getMap());
    }

    public void mainLoop() {
        List<Item> items = world.getItems();
        Location playerLoc = mMapView.getLocationDisplay().getLocation();
        Point playerPt = playerLoc.getPosition();
        double arbDiam = 10;
        //guessed what x and y
        if (playerPt != null) {
            Player player = new Player(playerPt.getX(), playerPt.getY(), arbDiam);
            for (Item currItem : items) {
                //check collision
                if (Math.sqrt(Math.pow((player.getLat() - currItem.getLatitude()), 2) +
                        Math.pow((player.getLon() - currItem.getLongitude()), 2)) <
                        (player.getDiameter() + currItem.getDiameter())) {
                    ListenableFuture<Void> future = currItem.getFeature().getFeatureTable().deleteFeatureAsync(currItem.getFeature());
                    try {
                        future.get();
                        items.remove(currItem);
                    } catch (Exception e) {
                        System.out.println("I cannot begin to fathom how we got here!");
                        System.out.println(e.getMessage());
                    }
                }
            }
            //wait one second possibly
            mainLoop();
        }
    }
}
