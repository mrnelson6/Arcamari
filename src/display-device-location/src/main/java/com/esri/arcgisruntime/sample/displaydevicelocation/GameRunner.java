package com.esri.arcgisruntime.sample.displaydevicelocation;

import java.util.List;
import java.lang.Math;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.loadable.LoadStatusChangedEvent;
import com.esri.arcgisruntime.loadable.LoadStatusChangedListener;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.location.LocationDataSource.Location;

public class GameRunner {
  private LoadStatus   mLoadStatus = LoadStatus.NOT_LOADED;
  private ArcGISMap    mMap;
  private MapView      mMapView;
  private World        mWorld;
  private List<Item>   mItems;

  public GameRunner(MapView mapView, String mapURL) {
    loadMap(mapURL);
    mMapView = mapView;
    mMapView.setMap(mMap);
  }

  private void loadMap(String mapURL) {
    mMap = new ArcGISMap(mapURL);
    mMap.addLoadStatusChangedListener(new LoadStatusChangedListener() {
      @Override
      public void loadStatusChanged(LoadStatusChangedEvent loadStatusChangedEvent) {
        mLoadStatus = loadStatusChangedEvent.getNewLoadStatus();
        if (mLoadStatus == LoadStatus.LOADED) {
          mWorld = new World(mMap);
          mItems = mWorld.getItems();
        }
      }
    });
    mMap.loadAsync();
  }

  public void mainLoop() {
    Location playerLoc = mMapView.getLocationDisplay().getLocation();
    Point playerPt = playerLoc.getPosition();
    double arbDiam = 10;
    //guessed what x and y
    if (playerPt != null) {
      Player player = new Player(playerPt.getX(), playerPt.getY(), arbDiam);
      int i = mItems.size()-1;
      Item currItem;
      while(i >= 0) {
        currItem = mItems.get(i);
        //check collision
        if (Math.sqrt(Math.pow((player.getLat() - currItem.getLatitude()), 2) +
                      Math.pow((player.getLon() - currItem.getLongitude()), 2)) <
                               (player.getDiameter() + currItem.getDiameter())) {
          ListenableFuture<Void> future = currItem.getFeature().getFeatureTable().deleteFeatureAsync(currItem.getFeature());

          try {
            future.get();
            mItems.remove(currItem);
          } catch (Exception e) {
            System.out.println("I cannot begin to fathom how we got here!");
            System.out.println(e.getMessage());
          }
        }
        i--;
      }
    }
  }
}
