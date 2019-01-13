package com.esri.arcgisruntime.sample.displaydevicelocation;

import android.widget.Toast;

import java.util.List;
import java.lang.Math;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.loadable.LoadStatusChangedEvent;
import com.esri.arcgisruntime.loadable.LoadStatusChangedListener;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
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
  private LocationDisplay mLocationDisplay;
  private Player mPlayer;
  private boolean mattsmistake;

  public GameRunner(MapView mapView, String mapURL, LocationDisplay locD) {
    loadMap(mapURL);
    mMapView = mapView;
    mMapView.setMap(mMap);
    mLocationDisplay = locD;
    mattsmistake = false;
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

  public double getPlayerDiameter() {
    if(mPlayer == null) {
      return -1.0;
    }
    return mPlayer.getDiameter();
  }

  public void collide(MainActivity ma) {
    Location playerLoc = mMapView.getLocationDisplay().getLocation();
    Point playerPt = playerLoc.getPosition();
    double arbDiam = 51;
    //guessed what x and y
    if (playerPt != null) {
      if(!mattsmistake) {
        Point wgs84Point = (Point) GeometryEngine.project(playerPt, SpatialReferences.getWebMercator());
        mPlayer = new Player(wgs84Point.getX(), wgs84Point.getY(), arbDiam);
        mattsmistake = true;
      }
      int i = mItems.size()-1;
      Item currItem;
      while(i >= 0) {
        currItem = mItems.get(i);
        //check collision
        if ((Math.sqrt(Math.pow((mPlayer.getLat() - currItem.getLatitude()), 2) +
                      Math.pow((mPlayer.getLon() - currItem.getLongitude()), 2)) <
                               (mPlayer.getDiameter() + currItem.getDiameter()) / 2.5) &&
                mPlayer.getDiameter() > currItem.getDiameter()) {
          Toast.makeText(ma, "monch", Toast.LENGTH_LONG).show();
          ListenableFuture<Void> future = currItem.getFeature().getFeatureTable().deleteFeatureAsync(currItem.getFeature());
          try {
            future.get();
            mItems.remove(currItem);
            //make player bigger
            double itemArea = Math.PI * Math.pow(currItem.getDiameter() / 2, 2);
            double playerArea = Math.PI * Math.pow(mPlayer.getDiameter() / 2, 2);
            playerArea += itemArea;
            mPlayer.setDiameter(2 * Math.sqrt(playerArea / Math.PI));
            mPlayer.addItemsCollected(currItem);
            
            //possiblly needed change scale
            //double scaleVal = 2500 + (player.getDiameter() * 100);
            //ListenableFuture<Boolean> scaleFuture = mMapView.setViewpointScaleAsync(scaleVal);
            //scaleFuture.get();

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
