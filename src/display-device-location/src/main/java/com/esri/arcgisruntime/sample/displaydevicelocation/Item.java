package com.esri.arcgisruntime.sample.displaydevicelocation;

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.Point;

import java.util.Map;


public final class Item {

  private boolean mCollected = false;
  private double  mDiameter;
  private Feature mFeature;
  private double  mLatitude;
  private double  mLongitude;
  private String mTableName;

  public Item(Feature feature, String tableName) {
    mFeature = feature;
    mTableName = tableName;
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
  public String getTableName() { return mTableName; }

  public double getDiameter() {
    return mDiameter;
  }

  public Feature getFeature() { return mFeature; }

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
