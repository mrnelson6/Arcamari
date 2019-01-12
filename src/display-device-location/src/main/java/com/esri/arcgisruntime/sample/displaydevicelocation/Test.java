package com.esri.arcgisruntime.sample.displaydevicelocation;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.List;

public class Test {

//  public void test(Player player, MapView mMapView, ServiceFeatureTable serviceFeatureTable){
//    World mWorld = new World(mMapView.getMap(), serviceFeatureTable);
//    List<Item> items = mWorld.getItems();
//    int i = items.size() - 1;
//    Item currItem;
//    while(i >= 0) {
//      currItem = items.get(i);
//    //for (Item currItem : items) {
//      //check collision
//      if (Math.sqrt(Math.pow((player.getLat() - currItem.getLatitude()), 2) +
//          Math.pow((player.getLon() - currItem.getLongitude()), 2)) <
//          (player.getDiameter() + currItem.getDiameter())) {
//        ListenableFuture<Void> future = currItem.getFeature().getFeatureTable().deleteFeatureAsync(currItem.getFeature());
//        try {
//          future.get();
//          items.remove(currItem);
//        } catch (Exception e) {
//          System.out.println("I cannot begin to fathom how we got here!");
//          System.out.println(e.getMessage());
//        }
//      }
//      i--;
//    }
//
//    System.out.println(items);
//  }

}
