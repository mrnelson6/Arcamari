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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;
import android.content.pm.PackageManager;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;


public class MainActivity extends AppCompatActivity {
  private MapView mMapView;
  private LocationDisplay mLocationDisplay;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mMapView = findViewById(R.id.mapView);
    WebmapLoader webmapLoader = new WebmapLoader("https://learngis.maps.arcgis.com/home/webmap/viewer.html?webmap=3476f0e5637c481b89eafd18b6620c79");
    ArcGISMap map = webmapLoader.getMap();

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

    mMapView.setMap(map);

    // get the MapView's LocationDisplay
    mLocationDisplay = mMapView.getLocationDisplay();

    // Listen to changes in the status of the location data source.
    // Start Navigation Mode
    // This mode is best suited for in-vehicle navigation.
    mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION );
    if (!mLocationDisplay.isStarted()) {
      mLocationDisplay.startAsync();
    }

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
      Toast.makeText(MainActivity.this, "LocationDisplayManager cannot run because location permission was denied", Toast
              .LENGTH_SHORT).show();

      // Update UI to reflect that the location display did not actually start
    }
  }

  @Override
  protected void onPause(){
    mMapView.pause();
    super.onPause();
  }

  @Override
  protected void onResume(){
    super.onResume();
    mMapView.resume();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mMapView.dispose();
  }

}
