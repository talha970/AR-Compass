package com.augmented_reality.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.augmented_reality.R;
import com.augmented_reality.camera.CameraSurface;
import com.augmented_reality.data.ARData;
import com.augmented_reality.ui.Marker;
import com.augmented_reality.widget.VerticalSeekBar;
import com.augmented_reality.widget.VerticalTextView;

/**
 * This class extends the SensorsActivity and is designed tie the AugmentedView
 * and zoom bar together.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class AugmentedReality extends SensorsActivity implements OnTouchListener {
	
    private static final String TAG = "AugmentedReality";
    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
    private static final int ZOOMBAR_BACKGROUND_COLOR = Color.argb(10, 55, 55, 55);
    private static final String END_TEXT = FORMAT.format(AugmentedReality.MAX_ZOOM) + " km";
    private static final int END_TEXT_COLOR = Color.WHITE;
    Marker currentmarker=null;
    protected static WakeLock wakeLock = null;
    protected static CameraSurface camScreen = null;
    protected static VerticalSeekBar myZoomBar = null;
    protected static VerticalTextView endLabel = null;
    protected static LinearLayout zoomLayout = null;
    protected static AugmentedView augmentedView = null;

    public static final float MAX_ZOOM = 100; // in KM
    public static final float ONE_PERCENT = MAX_ZOOM / 100f;
    public static final float TEN_PERCENT = 10f * ONE_PERCENT;
    public static final float TWENTY_PERCENT = 2f * TEN_PERCENT;
    public static final float EIGHTY_PERCENTY = 4f * TWENTY_PERCENT;
public boolean flag=false;
    public static boolean ui_portrait = false;  // Defaulted to LANDSCAPE use  
    public static boolean showRadar = true;
    public static boolean showZoomBar = true;
    public static boolean useRadarAutoOrientate = false;
    public static boolean useMarkerAutoRotate = false;
    public static boolean useDataSmoothing = true;
    public static boolean useCollisionDetection = false; // defaulted OFF
    public static boolean collisionflag = false;
    public ImageButton imgbtn1;
    public ImageButton imgbtn2;
    public ImageButton imgbtn3;
    public ImageButton imgbtn4;
    public ImageButton imgbtn5;
    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        camScreen = new CameraSurface(this);
        setContentView(camScreen);
        
        LinearLayout menu=new LinearLayout(this);
        menu.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams mp=new LinearLayout.LayoutParams(100,LayoutParams.MATCH_PARENT);
        menu.setBackgroundDrawable(getResources().getDrawable(R.drawable.border));
        menu.setLayoutParams(mp);
        //linLayout.addView(menu);
        
        
        // Add these image buttons to Menu Bar
         imgbtn1=new ImageButton(this);
          imgbtn2=new ImageButton(this);
          imgbtn3=new ImageButton(this);
     imgbtn4=new ImageButton(this);
          imgbtn5=new ImageButton(this);
         
         imgbtn1.setBackgroundDrawable(getResources().getDrawable(R.drawable.airportm));
         imgbtn2.setBackgroundDrawable(getResources().getDrawable(R.drawable.bankm));
         imgbtn3.setBackgroundDrawable(getResources().getDrawable(R.drawable.restaurantm));
         imgbtn4.setBackgroundDrawable(getResources().getDrawable(R.drawable.mosquem));
         imgbtn5.setBackgroundDrawable(getResources().getDrawable(R.drawable.educationm));
         
        LinearLayout.LayoutParams milp=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
         milp.setMargins(0, 0, 0, 0);
         menu.addView(imgbtn1, milp);
         menu.addView(imgbtn2, milp);
         menu.addView(imgbtn3, milp);
         menu.addView(imgbtn4, milp);
         menu.addView(imgbtn5, milp);

        
        
        
        
        
        
        
        
        
              flag = getIntent().getExtras().getBoolean("iconflag");
              Log.e("aug real iconflag",Boolean.toString(flag));
        augmentedView = new AugmentedView(this,flag);
        augmentedView.setOnTouchListener(this);
        LayoutParams augLayout = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addContentView(augmentedView, augLayout);

        zoomLayout = new LinearLayout(this);
        zoomLayout.setVisibility((showZoomBar) ? LinearLayout.VISIBLE : LinearLayout.GONE);
        zoomLayout.setOrientation(LinearLayout.VERTICAL);
        zoomLayout.setPadding(5, 5, 5, 5);
        zoomLayout.setBackgroundColor(ZOOMBAR_BACKGROUND_COLOR);

        endLabel = new VerticalTextView(this);
        endLabel.setText(END_TEXT);
        endLabel.setTextColor(END_TEXT_COLOR);
        LinearLayout.LayoutParams zoomTextParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        zoomTextParams.gravity = Gravity.CENTER;
        zoomLayout.addView(endLabel, zoomTextParams);

        myZoomBar = new VerticalSeekBar(this);
        myZoomBar.setMax(100);
        myZoomBar.setProgress(50);
        myZoomBar.setOnSeekBarChangeListener(myZoomBarOnSeekBarChangeListener);
        LinearLayout.LayoutParams zoomBarParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
        zoomBarParams.gravity = Gravity.CENTER_HORIZONTAL;
        zoomLayout.addView(myZoomBar, zoomBarParams);

        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT, Gravity.RIGHT);
        addContentView(zoomLayout, frameLayoutParams);
        addContentView(menu,mp);
        updateDataOnZoom();

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();

        wakeLock.acquire();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();

        wakeLock.release();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSensorChanged(SensorEvent evt) {
        super.onSensorChanged(evt);

        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER || evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            augmentedView.postInvalidate();
        }
    }

    private OnSeekBarChangeListener myZoomBarOnSeekBarChangeListener = new OnSeekBarChangeListener() {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateDataOnZoom();
            camScreen.invalidate();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            // Ignore
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            updateDataOnZoom();
            camScreen.invalidate();
        }
    };

    private static float calcZoomLevel() {
        int myZoomLevel = myZoomBar.getProgress();
        float myout = 0;

        float percent = 0;
        if (myZoomLevel <= 25) {
            percent = myZoomLevel / 25f;
            myout = ONE_PERCENT * percent;
        } else if (myZoomLevel > 25 && myZoomLevel <= 50) {
            percent = (myZoomLevel - 25f) / 25f;
            myout = ONE_PERCENT + (TEN_PERCENT * percent);
        } else if (myZoomLevel > 50 && myZoomLevel <= 75) {
            percent = (myZoomLevel - 50f) / 25f;
            myout = TEN_PERCENT + (TWENTY_PERCENT * percent);
        } else {
            percent = (myZoomLevel - 75f) / 25f;
            myout = TWENTY_PERCENT + (EIGHTY_PERCENTY * percent);
        }

        return myout;
    }

    /**
     * Called when the zoom bar has changed.
     */
    protected void updateDataOnZoom() {
        float zoomLevel = calcZoomLevel();
        ARData.setRadius(zoomLevel);
        ARData.setZoomLevel(FORMAT.format(zoomLevel));
        ARData.setZoomProgress(myZoomBar.getProgress());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onTouch(View view, MotionEvent me) {
    	
        if (me.getAction() != MotionEvent.ACTION_DOWN) return false;

        // See if the motion event is on a Marker
        for (Marker marker : ARData.getMarkers()) {
            if (marker.handleClick(me.getX(), me.getY())==1) {
            	if(marker!=null){
            		augmentedView.setmarker(marker);
            		currentmarker=marker;
            		markerTouched(marker,1);
                }
            	return true;
            }
        }
        for (Marker m : ARData.getMarkers()) {
        if (m.handleClick(me.getX(), me.getY())==2) {
                	if(currentmarker!=null){
                		//augmentedView.setmarker(m);
                		markerTouched(currentmarker,2);
                    Log.e("marker",currentmarker.getName());
                	}
                return true;
            }
        }

        return super.onTouchEvent(me);
    }

    protected void markerTouched(Marker marker,int ans) {
        Log.v(TAG, "markerTouched() not implemented. marker=");//+marker.getName());
    }
}
