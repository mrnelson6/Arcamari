/* Copyright 2016 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.esri.arcgisruntime.sample.displaydevicelocation;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;
import android.content.pm.PackageManager;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;


public class MainActivity extends AppCompatActivity {
  private MapView mMapView;
  private LocationDisplay mLocationDisplay;
  private int requestCode = 2;
  String[] reqPermissions = new String[]{ Manifest.permission.ACCESS_FINE_LOCATION,
                                          Manifest.permission.ACCESS_COARSE_LOCATION };
  private boolean mIsBound = false;
  private MusicService mServ;
  private GameRunner mGame;
  private ServiceConnection Scon = new ServiceConnection() {
    public void onServiceConnected(ComponentName name, IBinder binder) {
      mServ = ((MusicService.ServiceBinder) binder).getService();
    }

    public void onServiceDisconnected(ComponentName name) {
      mServ = null;
    }
  };

  void doBindService() {
    bindService(new Intent(this, MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
    mIsBound = true;
  }

  void doUnbindService() {
    if (mIsBound) {
      unbindService(Scon);
      mIsBound = false;
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mMapView = findViewById(R.id.mapView);

    // add topographic basemap
    //ArcGISMap map = new ArcGISMap(Basemap.Type.TOPOGRAPHIC, 34.056295, -117.195800, 10);
    // create the service feature table
    //ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable("https://services.arcgis.com/V6ZHFr6zdgNZuVG0/ArcGIS/rest/services/Redlands_Trees_View/FeatureServer/0");
    // create the feature layer using the service feature table
    //FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
    // get the operational layers then add to operational layer to ArcGISMap
    //map.getOperationalLayers().add(featureLayer);

    mMapView.setOnTouchListener(new MapView.OnTouchListener() {
      @Override
      public boolean onMultiPointerTap(MotionEvent motionEvent) {
        return false;
      }

      @Override
      public boolean onDoubleTouchDrag(MotionEvent motionEvent) {
        return false;
      }

      @Override
      public boolean onUp(MotionEvent motionEvent) {
        return false;
      }

      @Override
      public boolean onRotate(MotionEvent motionEvent, double v) {
        return false;
      }

      @Override
      public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
      }

      @Override
      public boolean onDoubleTap(MotionEvent e) {
        return false;
      }

      @Override
      public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
      }

      @Override
      public boolean onDown(MotionEvent e) {
        return false;
      }

      @Override
      public void onShowPress(MotionEvent e) {

      }

      @Override
      public boolean onSingleTapUp(MotionEvent e) {
        return false;
      }

      @Override
      public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
      }

      @Override
      public void onLongPress(MotionEvent e) {

      }

      @Override
      public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
      }

      @Override
      public boolean onScale(ScaleGestureDetector detector) {
        return false;
      }

      @Override
      public boolean onScaleBegin(ScaleGestureDetector detector) {
        return false;
      }

      @Override
      public void onScaleEnd(ScaleGestureDetector detector) {

      }

      @Override
      public boolean onTouch(View v, MotionEvent event) {
        return false;
      }
    });

    // get the MapView's LocationDisplay
    mLocationDisplay = mMapView.getLocationDisplay();

    mLocationDisplay.setInitialZoomScale(2500);
    mLocationDisplay.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
      @Override
      public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
        mGame.collide(MainActivity.this);
       // Toast.makeText(MainActivity.this, "we got em", Toast.LENGTH_LONG).show();
      }
    });


    mLocationDisplay.addDataSourceStatusChangedListener(new LocationDisplay.DataSourceStatusChangedListener() {
      @Override
      public void onStatusChanged(LocationDisplay.DataSourceStatusChangedEvent dataSourceStatusChangedEvent) {
        // If LocationDisplay started OK, then continue.
        if (dataSourceStatusChangedEvent.isStarted())
          return;

        // No error is reported, then continue.
        if (dataSourceStatusChangedEvent.getError() == null)
          return;

        // If an error is found, handle the failure to start.
        // Check permissions to see if failure may be due to lack of permissions.
        boolean permissionCheck1 = ContextCompat.checkSelfPermission(MainActivity.this, reqPermissions[0]) ==
                                                                      PackageManager.PERMISSION_GRANTED;
        boolean permissionCheck2 = ContextCompat.checkSelfPermission(MainActivity.this, reqPermissions[1]) ==
                                                                      PackageManager.PERMISSION_GRANTED;

        if (!(permissionCheck1 && permissionCheck2)) {
          // If permissions are not already granted, request permission from the user.
          ActivityCompat.requestPermissions(MainActivity.this, reqPermissions, requestCode);
        } else {
          // Report other unknown failure types to the user - for example, location services may not
          // be enabled on the device.
          String message = String.format("Error in DataSourceStatusChangedListener: %s",
                                          dataSourceStatusChangedEvent.getSource()
                                                  .getLocationDataSource().getError().getMessage());
          Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }
      }
    });

    // Listen to changes in the status of the location data source.
    // Start Navigation Mode
    // This mode is best suited for in-vehicle navigation.
    mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
    if (!mLocationDisplay.isStarted()) {
      mLocationDisplay.startAsync();
    }

    // Start Katamari Damacy OST
    Intent music = new Intent(this, MusicService.class);
    startService(music);

    final String mapURL = "https://www.arcgis.com/home/webmap/viewer.html?webmap=ac2d655059fb402fa6bf2be64120eb49";
    mGame = new GameRunner(mMapView, mapURL);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    // If request is cancelled, the result arrays are empty.
    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      // Location permission was granted. This would have been triggered in response to failing to start the
      // LocationDisplay, so try starting this again.
      mLocationDisplay.startAsync();
    } else {
      // If permission was denied, show toast to inform user what was chosen. If LocationDisplay is started again,
      // request permission UX will be shown again, option should be shown to allow never showing the UX again.
      // Alternative would be to disable functionality so request is not shown again.
      Toast.makeText(MainActivity.this,
                     "LocationDisplayManager cannot run because location permission was denied",
                      Toast.LENGTH_SHORT).show();
      // Update UI to reflect that the location display did not actually start
    }
  }

  @Override
  protected void onPause() {
    mMapView.pause();
    super.onPause();
    // mServ.pauseMusic();
  }

  @Override
  protected void onResume() {
    super.onResume();
    mMapView.resume();
    // mServ.pauseMusic();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mMapView.dispose();
    mServ.onDestroy();
  }
}
