package com.esri.arcgisruntime.sample.displaydevicelocation;

import android.util.Log;

import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.loadable.LoadStatusChangedEvent;
import com.esri.arcgisruntime.loadable.LoadStatusChangedListener;

public final class WebmapLoader {

  private LoadStatus mLoadStatus = LoadStatus.NOT_LOADED;
  private ArcGISMap mMap;

  public WebmapLoader(String URL) {
    mMap = new ArcGISMap(URL);
    loadMap();
  }

  public ArcGISMap getMap()
  {
    return mMap;
  }

  private void loadMap() {

    mMap.addLoadStatusChangedListener(new LoadStatusChangedListener() {
      @Override
      public void loadStatusChanged(LoadStatusChangedEvent loadStatusChangedEvent) {
        mLoadStatus = loadStatusChangedEvent.getNewLoadStatus();
      }
    });

    mMap.loadAsync();
  }
}
