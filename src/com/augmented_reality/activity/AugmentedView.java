package com.augmented_reality.activity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import com.augmented_reality.data.ARData;
import com.augmented_reality.ui.Marker;
import com.augmented_reality.ui.Radar;

/**
 * This class extends the View class and is designed draw the zoom bar, radar
 * circle, and markers on the View.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class AugmentedView extends View {
	int changed=0;
    private static final String TAG = "AugmentedView";
    private static final AtomicBoolean drawing = new AtomicBoolean(false);
    private  final Radar radar = new Radar(this.getResources());
    private static final float[] locationArray = new float[3];
    private static final float[] oldlocationArray = new float[3];
    private static final List<Marker> cache = new ArrayList<Marker>();
    private static final Set<Marker> updated = new HashSet<Marker>();
    public Boolean flagaug=true;
    public Canvas can=null;
public Marker mark=null;
    public AugmentedView(Context context,Boolean f) {
        super(context);
        Log.v(TAG, "portrait              = "+AugmentedReality.ui_portrait);
        Log.v(TAG, "useCollisionDetection = "+AugmentedReality.useCollisionDetection);
        Log.v(TAG, "useSmoothing          = "+AugmentedReality.useDataSmoothing);
        Log.v(TAG, "showRadar             = "+AugmentedReality.showRadar);
        Log.v(TAG, "showZoomBar           = "+AugmentedReality.showZoomBar);
    flagaug=f;
    
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (canvas == null) return;
can=canvas;
        if (drawing.compareAndSet(false, true)) {
        	//Log.v(TAG, "DIRTY flag found, re-populating the cache.");

            // Get all the markers
            List<Marker> collection = ARData.getMarkers();
            radar.drawBox(canvas);
            if (AugmentedReality.showRadar) 
            { radar.draw(canvas);}
            // Prune all the markers that are out of the radar's radius (speeds
            // up drawing and collision detection)
            cache.clear();
            for (Marker m : collection) {
                m.update(canvas, 0, 0);
                if ( m.isOnRadar() &&m.isInView()) cache.add(m);
            }
            collection = cache;
            
            if (AugmentedReality.useCollisionDetection) {
              //  adjustForCollisions(canvas, collection);//when icon is touched
            if(mark!=null){
            	
            	mark.draw(canvas,true,1);
                Log.e("augview",Integer.toString(changed));
            	
            }
            
            }
            
            // Draw AR markers in reverse order since the last drawn should be
            // the closest
            ListIterator<Marker> iter = collection.listIterator(collection.size());
            while (iter.hasPrevious()) {
                Marker marker = iter.previous();
                if(flagaug==true){
              marker.draw(canvas,false,1);
              }
                else{
                    marker.draw(canvas,false,0);
                }
             /* if(flagaug==true){
          		flagaug=false;
          		changed=1;
          		Log.e("augview","value changed");
          	}*/
            }

            // Radar circle and radar markers
            
            drawing.set(false);
        }
    }
    public Canvas getcanvas(){
    return can;	
    }
public void setmarker(Marker m){
	mark=m;
}
    public static void adjustForCollisions(Canvas canvas, List<Marker> collection) {
        updated.clear();

        // Update the AR markers for collisions
        for (int i=0; i<collection.size(); i++) {
            Marker marker1 = collection.get(i);
            if (!marker1.isInView()) {
                updated.add(marker1);
                continue;
            }
            if (updated.contains(marker1))
                continue;

            int collisions = 1;
            for (int j=i+1; j<collection.size(); j++) {
                Marker marker2 = collection.get(j);
                if (!marker2.isInView()) {
                    updated.add(marker2);
                    continue;
                }
                if (updated.contains(marker2))
                    continue;

                float width = marker1.getWidth();
                float height = marker1.getHeight();
                float max = Math.max(width, height);
                if(AugmentedReality.collisionflag==false){
              	  locationArray[1] = 	oldlocationArray[1] ;
              	  Log.w("teejay123","flagfalse");
              	  Log.w("locationArray[1]",String.valueOf(locationArray[1]) );
              }
                if (marker1.isMarkerOnMarker(marker2)) {
                    marker2.getLocation().get(locationArray);
                    float y = locationArray[1];
                	oldlocationArray[1]=locationArray[1];
                    float h = collisions * max;
                    if(AugmentedReality.collisionflag==false){
                    	 // locationArray[1] = 	oldlocationArray[1] ;
                    	  Log.w("teejay123","flagfalse");
                    	  Log.w("locationArray[1]",String.valueOf(locationArray[1]) );
                    }
                    else{
                    	oldlocationArray[1]=locationArray[1];
                    locationArray[1] = y + h;
                    Log.w("locationArray[1]",String.valueOf(locationArray[1]) );
                    Log.w("oldlocation",String.valueOf(	oldlocationArray[1]) );
                    }
                    marker2.getLocation().set(locationArray) ;
                    marker2.update(canvas, 0, 0);
                    collisions++;
                    updated.add(marker2);
                }
            }
            updated.add(marker1);
        }
    }
}
