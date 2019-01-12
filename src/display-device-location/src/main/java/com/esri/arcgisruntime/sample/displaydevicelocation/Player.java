package com.esri.arcgisruntime.sample.displaydevicelocation;

import java.util.ArrayList;
import java.util.List;


public class Player {
  private double mLat;
  private double mLon;
  private double mDiameter;
  private List<Item> mItemsCollected;

  Player(double mLat, double mLon, double diameter) {
    this.mLat = mLat;
    this.mLon = mLon;
    this.mDiameter = diameter;
    this.mItemsCollected = new ArrayList<>();
  }

  public double getLat() {
    return mLat;
  }

  public void setLat(double mLat) {
    this.mLat = mLat;
  }

  public double getLon() {
    return mLon;
  }

  public void setLon(double mLon) {
    this.mLon = mLon;
  }

  public double getDiameter() {
    return mDiameter;
  }

  public void setDiameter(double mDiameter) {
    this.mDiameter = mDiameter;
  }

  public List<Item> getItemsCollected() {
    return mItemsCollected;
  }

  public void setItemsCollected(List<Item> itemsCollected) {
    this.mItemsCollected = itemsCollected;
  }
}
