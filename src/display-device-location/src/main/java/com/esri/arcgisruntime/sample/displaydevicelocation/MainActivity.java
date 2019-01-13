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
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.content.pm.PackageManager;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
  private MapView mMapView;
  private boolean mComplete;
  private LocationDisplay mLocationDisplay;
  private View.OnTouchListener onTouchList;
  private PictureMarkerSymbol mKatamariPictureSymbol;
  private PictureMarkerSymbol mKatamariPictureSymbol2;
  private PictureMarkerSymbol mKatamariPictureSymbol3;
  private PictureMarkerSymbol mKatamariPictureSymbol4;
  private float lastDiam = 50;
  private int requestCode = 2;
  String[] reqPermissions = new String[]{ Manifest.permission.ACCESS_FINE_LOCATION,
                                          Manifest.permission.ACCESS_COARSE_LOCATION };
  private boolean mIsBound = false;
  private MusicService mServ;
  private GameRunner mGame;
  private Button mButton;
  private PopupWindow mPopupWindow;
  private RelativeLayout mRelativeLayout;
  private Context mContext;
  private Activity mActivity;

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

  public void initCountdownTimer(long duration) {
    final TextView timerTextView = (TextView) findViewById(R.id.timerTextView);
    timerTextView.setTextColor(Color.BLACK);
    timerTextView.setBackgroundColor(Color.WHITE);

    CountDownTimer timer = new CountDownTimer(duration, 1000) {
      @Override
      public void onTick(final long millSecondsLeftToFinish) {
        timerTextView.setText("Time left: "+String.format("%02d:%02d",
                              TimeUnit.MILLISECONDS.toMinutes(millSecondsLeftToFinish),
                              TimeUnit.MILLISECONDS.toSeconds(millSecondsLeftToFinish) -
                              TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millSecondsLeftToFinish))));
    }

      @Override
      public void onFinish() {

        mComplete = true;
        timerTextView.setText("Game Over");

        if(mGame.getmPlayer()!=null ){
            mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.OFF);
            mMapView.setOnTouchListener(onTouchList);
          List<Item> itemsCollected = mGame.getmPlayer().getItemsCollected();
          List<Item> allItems = mGame.getmItems();
          graphics(allItems, itemsCollected);
        }
      }
    };
    timer.start();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mComplete = false;
    setContentView(R.layout.activity_main);
    mContext = getApplicationContext();
    mActivity = MainActivity.this;
    mRelativeLayout = (RelativeLayout) findViewById(R.id.rl);
    mButton = (Button) findViewById(R.id.btn);
    mMapView = findViewById(R.id.mapView);

    mButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        // Initialize a new instance of LayoutInflater service
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        View customView = inflater.inflate(R.layout.custom_layout,null);

        // Initialize a new instance of popup window
        mPopupWindow = new PopupWindow(
            customView,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            true
        );

        mPopupWindow.setElevation(20);

        // Finally, show the popup window at the center location of root relative layout
        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);
      }
    });

    mKatamariPictureSymbol = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.trans_katamari));
    mKatamariPictureSymbol2 = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.trans_katamari2));
    mKatamariPictureSymbol3 = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.trans_katamari3));
    mKatamariPictureSymbol4 = new PictureMarkerSymbol((BitmapDrawable) getResources().getDrawable(R.drawable.trans_katamari4));

    onTouchList = mMapView.getOnTouchListener();

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

        //Create a picture marker symbol from a file on disk;
        PictureMarkerSymbol symbolToSet;
        int num = new Random().nextInt(4);
        if(num==0)
            symbolToSet = mKatamariPictureSymbol;
        else if(num==1)
              symbolToSet = mKatamariPictureSymbol2;
        else if(num==2)
             symbolToSet = mKatamariPictureSymbol3;
        else if(num==3)
             symbolToSet = mKatamariPictureSymbol4;
        else
            symbolToSet = mKatamariPictureSymbol;
        float diam;
        if(mGame != null) {
          diam = (float) mGame.getPlayerDiameter() * 3;
          if(diam > lastDiam){
            lastDiam = diam;
          }
        }

        //for(PictureMarkerSymbol sym : new ArrayList<PictureMarkerSymbol>(mKatamariPictureSymbol, mKatamariPictureSymbol2, mKatamariPictureSymbol3, mKatamariPictureSymbol4)

        mKatamariPictureSymbol.setHeight(lastDiam);
        mKatamariPictureSymbol.setWidth(lastDiam);
        mKatamariPictureSymbol2.setHeight(lastDiam);
        mKatamariPictureSymbol2.setWidth(lastDiam);
        mKatamariPictureSymbol3.setHeight(lastDiam);
        mKatamariPictureSymbol3.setWidth(lastDiam);
        mKatamariPictureSymbol4.setHeight(lastDiam);
        mKatamariPictureSymbol4.setWidth(lastDiam);
        if(mComplete) {
          mLocationDisplay.setShowLocation(false);
          mKatamariPictureSymbol.setOpacity(0);
          mKatamariPictureSymbol2.setOpacity(0);
          mKatamariPictureSymbol3.setOpacity(0);
          mKatamariPictureSymbol4.setOpacity(0);
        }

        mLocationDisplay.setShowAccuracy(false);
        mLocationDisplay.setShowPingAnimation(false);
        mLocationDisplay.setHeadingSymbol(symbolToSet);
        mLocationDisplay.setAcquiringSymbol(symbolToSet);
        mLocationDisplay.setDefaultSymbol(symbolToSet);
        mLocationDisplay.setPingAnimationSymbol(symbolToSet);
        mLocationDisplay.setCourseSymbol(symbolToSet);
        //mLocationDisplay.setAccuracySymbol(pinBlankOrangeSymbol);
        //mLocationDisplay.setAccuracySymbol(pinBlankOrangeSymbol);
          //mLocationDisplay.setAccuracySymbol(pinBlankOrangeSymbol);
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
      mLocationDisplay.setDefaultSymbol(new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 10));
      mLocationDisplay.startAsync();
    }

    // Start Katamari Damacy OST
    Intent music = new Intent(this, MusicService.class);
    startService(music);

    final String mapURL = "https://www.arcgis.com/home/webmap/viewer.html?webmap=44f99fb7e03f4c5a8f01bcf467cd71e6";
    mGame = new GameRunner(mMapView, mapURL, mLocationDisplay);
    initCountdownTimer(60000);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    // If request is cancelled, the result arrays are empty.
    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      // Location permission was granted. This would have been triggered in response to failing to start the
      // LocationDisplay, so try starting this again.
      mLocationDisplay.setDefaultSymbol(new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 10));
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
  }

  public void graphics(List<Item> allItems, List<Item> itemsCollected){
    LayerList operationalLayers = mMapView.getMap().getOperationalLayers();
    for (Layer layer : operationalLayers) {
      if (layer instanceof FeatureLayer) {
        FeatureLayer featureLayer = (FeatureLayer) layer;
        FeatureTable featureTable = featureLayer.getFeatureTable();
        if (!(featureTable instanceof ServiceFeatureTable)) {
          continue;
        }
        ServiceFeatureTable serviceFeatureTable = (ServiceFeatureTable) featureTable;
        ListenableFuture<Void> future = serviceFeatureTable.undoLocalEditsAsync();
        try {
          future.get();
        } catch(Exception e) {
        }
      }
    }
    for(Item currItem : allItems) {
      currItem.getFeature().getFeatureTable().getFeatureLayer().setFeatureVisible(currItem.getFeature(), false);
    }
    for(Item currItem : itemsCollected) {
      currItem.getFeature().getFeatureTable().getFeatureLayer().setFeatureVisible(currItem.getFeature(), true);
    }

  }
}
