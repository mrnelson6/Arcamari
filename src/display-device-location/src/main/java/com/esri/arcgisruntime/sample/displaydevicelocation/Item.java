package com.esri.arcgisruntime.sample.displaydevicelocation;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;

import java.util.Map;


public final class Item {

  private boolean mCollected = false;
  private double  mDiameter;
  private Feature mFeature;
  private double  mLatitude;
  private double  mLongitude;
  private String  mFeatureTableName;

  public Item(Feature feature) {
    FeatureTable featureTable = feature.getFeatureTable();
    mFeatureTableName = featureTable.getDisplayName();
    mFeature = feature;
    Geometry geometry = feature.getGeometry();

    if (geometry instanceof Point) {
      Point point = (Point) geometry;
      mLatitude = point.getX();
      mLongitude = point.getY();
    }

    mDiameter = calculateDiameter();
  }

  private double calculateDiameter() {
    Map<String, Object> attributes = mFeature.getAttributes();

    if (attributes.containsKey("diameter")) {
      Object diameterValue = attributes.get("diameter");

      if (diameterValue instanceof Double) {
        return ((Double) diameterValue).doubleValue();
      }
      else if (diameterValue instanceof String) {
        return Double.parseDouble((String)diameterValue);
      }
    }
    return 1.0;
  }

  public double getDiameter() {
    return mDiameter;
  }

  public Feature getFeature() { return mFeature; }

  public String getFeatureTableName() {
    return mFeatureTableName;
  }

  public double getLatitude() {
    return mLatitude;
  }

  public double getLongitude() {
    return mLongitude;
  }

  public boolean isCollected() {
    return mCollected;
  }

  public void setCollected(boolean collected) {
    mCollected = collected;
  }
}
