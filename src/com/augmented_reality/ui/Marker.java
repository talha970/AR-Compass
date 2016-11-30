package com.augmented_reality.ui;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.location.Location;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.augmented_reality.R;
import com.augmented_reality.activity.AugmentedReality;
import com.augmented_reality.camera.CameraModel;
import com.augmented_reality.common.Vector;
import com.augmented_reality.common.Orientation.ORIENTATION;
import com.augmented_reality.data.ARData;
import com.augmented_reality.data.PhysicalLocation;
import com.augmented_reality.ui.objects.PaintableBoxedText;
import com.augmented_reality.ui.objects.PaintableCircle;
import com.augmented_reality.ui.objects.PaintableGps;
import com.augmented_reality.ui.objects.PaintableIcon;
import com.augmented_reality.ui.objects.PaintableObject;
import com.augmented_reality.ui.objects.PaintablePoint;
import com.augmented_reality.ui.objects.PaintablePosition;


/**
 * This class will represent a physical location and will calculate it's
 * visibility and draw it's text and visual representation accordingly. This
 * should be extended if you want to change the way a Marker is viewed.
 * 
 * Note: This class assumes if two Markers have the same name, it is the same
 * object.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Marker  implements Comparable<Marker> {
	 private static PaintablePosition markerContainer = null;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("@#");
    private static final Vector locationVector = new Vector(0, 0, 0);
    private static Context context;
    private final Vector screenPositionVector = new Vector();
    private final Vector tmpVector = new Vector();
    private final Vector tmpLocationVector = new Vector();
    private final Vector locationXyzRelativeToCameraView = new Vector();
    private final float[] distanceArray = new float[1];
    private final float[] locationArray = new float[3];
public boolean g_flag=false;
    private final StringBuilder textStr = new StringBuilder();
    private final StringBuilder vicStr = new StringBuilder();
    private final StringBuilder distStr = new StringBuilder();

    private final Box box = new Box();
int maxX=0;
int maxY=0;
    private float initialY = 0.0f;

    private static CameraModel cam = null;

    // Container for the circle or icon symbol
    protected PaintableObject gpsSymbol = null;
    private PaintablePosition symbolContainer = null;

    // Container for text
    protected PaintableBoxedText textBox = null;
    protected PaintableBoxedText vicBox = null;
    protected PaintableBoxedText distBox = null;
    private PaintablePosition textContainer = null;
    private PaintablePosition vicContainer = null;
    private PaintablePosition distContainer = null;
    // Unique identifier of Marker
    private String name = null;
    private String vicinity=null;
    private String place=null;
    // Marker's physical location (Lat, Lon, Alt)
    private final PhysicalLocation physicalLocation = new PhysicalLocation();
    // Distance from camera to PhysicalLocation in meters
    private double distance = 0.0;
    // Is within the radar
    private boolean isOnRadar = false;
    // Is in the camera's view
    private boolean isInView = false;
    // Physical location's X, Y, Z relative to the camera's location
    private final Vector locationXyzRelativeToPhysicalLocation = new Vector();
    // Marker's default color
    private int color = Color.WHITE;
    // For tracking Markers which have no altitude
    private boolean noAltitude = false;

    // Used to show exact GPS position
    private static boolean debugGpsPosition = false;
    private PaintablePoint positionPoint = null;
    private PaintablePosition positionContainer = null;

    public Marker(String name, double latitude, double longitude, double altitude, int color,String vic,String p) {
        set(name, latitude, longitude, altitude, color,vic,p);
    }

    public Marker(Boolean flag) {
		// TODO Auto-generated constructor stub
	g_flag=flag;
    }

	/**
     * Set the objects parameters. This should be used instead of creating new
     * objects.
     * 
     * @param name
     *            String representing the Marker.
     * @param latitude
     *            Latitude of the Marker in decimal format (example 39.931269).
     * @param longitude
     *            Longitude of the Marker in decimal format (example -75.051261).
     * @param altitude
     *            Altitude of the Marker in meters (>0 is above sea level).
     * @param color
     *            Color of the Marker.
     */
    public void set(String name, double latitude, double longitude, double altitude, int color,String vic,String p) {
        if (name == null)
            throw new NullPointerException();

        this.name = name;
        this.vicinity=vic;
        this.place=p;
        this.physicalLocation.set(latitude, longitude, altitude);
        this.color = color;
        this.isOnRadar = false;
        this.isInView = false;
        this.locationXyzRelativeToPhysicalLocation.set(0, 0, 0);
        this.initialY = 0.0f;
        if (altitude == 0.0d)
            this.noAltitude = true;
        else
            this.noAltitude = false;
    }

    /**
     * Get the name of the Marker.
     * 
     * @return String representing the new of the Marker.
     */
    public static void setContext(Context mcontext) {
        if (context == null)
            context = mcontext;
       
    }
    public String getName() {
        return this.name;
    }
    public String getvic() {
        return this.vicinity;
    } public String getplace() {
        return this.place;
    }
    /**
     * Get the color of this Marker.
     * 
     * @return int representing the Color of this Marker.
     */
    public int getColor() {
        return this.color;
    }

    /**
     * Get the distance of this Marker from the current GPS position.
     * 
     * @return double representing the distance of this Marker from the current
     *         GPS position.
     */
    public double getDistance() {
        return this.distance;
    }

    /**
     * Get the initial Y coordinate of this Marker. Used to reset after
     * collision detection.
     * 
     * @return float representing the initial Y coordinate of this Marker.
     */
    public float getInitialY() {
        return this.initialY;
    }

    /**
     * Get the whether the Marker is inside the range (relative to slider on
     * view)
     * 
     * @return True if Marker is inside the range.
     */
    public boolean isOnRadar() {
        return this.isOnRadar;
    }

    /**
     * Get the whether the Marker is inside the camera's view
     * 
     * @return True if Marker is inside the camera's view.
     */
    public boolean isInView() {
        return this.isInView;
    }

    /**
     * Get the position of the Marker in XYZ.
     * 
     * @return Vector representing the position of the Marker.
     */
    public Vector getScreenPosition() {
        screenPositionVector.set(locationXyzRelativeToCameraView);
        return screenPositionVector;
    }

    /**
     * Get the the location of the Marker in XYZ.
     * 
     * @return Vector representing the location of the Marker.
     */
    public Vector getLocation() {
        return this.locationXyzRelativeToPhysicalLocation;
    }

    public float getHeight() {
        if (symbolContainer == null || textContainer == null)
            return 0f;
        return symbolContainer.getHeight() + textContainer.getHeight();
    }

    public float getWidth() {
        if (symbolContainer == null || textContainer == null)
            return 0f;
        float symbolWidth = symbolContainer.getWidth();
        float textWidth = textContainer.getWidth();
        return (textWidth > symbolWidth) ? textWidth : symbolWidth;
    }

    /**
     * Update the matrices and visibility of the Marker.
     * 
     * @param canvas
     *            Canvas to use in the CameraModel.
     * @param addX
     *            Adder to the X position.
     * @param addY
     *            Adder to the Y position.
     */
    public void update(Canvas canvas, float addX, float addY) {
        if (canvas == null)
            throw new NullPointerException();

        if (cam == null)
            cam = new CameraModel(canvas.getWidth(), canvas.getHeight(), true);
        cam.set(canvas.getWidth(), canvas.getHeight(), false);
        cam.setViewAngle(CameraModel.DEFAULT_VIEW_ANGLE);
        populateMatrices(cam, addX, addY);
        updateRadar();
        updateView();
    }

    private void populateMatrices(CameraModel cam, float addX, float addY) {
        if (cam == null)
            throw new NullPointerException();

        // Find the location given the rotation matrix
        tmpLocationVector.set(locationVector);
        tmpLocationVector.add(locationXyzRelativeToPhysicalLocation);
        tmpLocationVector.prod(ARData.getRotationMatrix());
        cam.projectPoint(tmpLocationVector, tmpVector, addX, addY);
        locationXyzRelativeToCameraView.set(tmpVector);
    }

    private void updateRadar() {
        isOnRadar = false;

        float range = ARData.getRadius() * 1000;
        float scale = range / Radar.RADIUS;
        locationXyzRelativeToPhysicalLocation.get(locationArray);
        float x = locationArray[0] / scale;
        float y = locationArray[2] / scale; // z==y Switched on purpose
        if ((x*x + y*y) < (Radar.RADIUS * Radar.RADIUS))
            isOnRadar = true;
    }

    private void updateView() {
        isInView = false;

        // If it's not on the radar, can't be in view
        if (!isOnRadar)
            return;

        locationXyzRelativeToCameraView.get(locationArray);
        float x = locationArray[0];
        float y = locationArray[1];
        float z = locationArray[2];

        // If it's not in the same side as our viewing angle (behind us)
        if (z >= -1f)
            return;

        // TODO: Revisit for a better approach, I assume it's a "square" axis aligned square.
        float max = Math.max(getWidth(), getHeight()) + 25; // overscan a bit
        float ulX = x - max / 2;
        float ulY = y - max / 2;
        float lrX = x + max / 2;
        float lrY = y + max / 2;
        if (lrX >= -1 && ulX <= cam.getWidth() && lrY >= -1 && ulY <= cam.getHeight())
            isInView = true;
    }

    /**
     * Calculate the relative position of this Marker from the given Location.
     * 
     * @param location
     *            Location to use in the relative position.
     * @throws NullPointerException
     *             if Location is NULL.
     */
    public void calcRelativePosition(Location location) {
        if (location == null)
            throw new NullPointerException();

        // Update the markers distance based on the new location.
        updateDistance(location);

        // noAltitude means that the elevation of the POI is not known
        // and should be set to the users GPS altitude
        if (noAltitude)
            physicalLocation.setAltitude(location.getAltitude());

        // Compute the relative position vector from user position to POI
        // location
        PhysicalLocation.convLocationToVector(location, 
                                              physicalLocation, 
                                              locationXyzRelativeToPhysicalLocation);
        initialY = locationXyzRelativeToPhysicalLocation.getY();
        updateRadar();
    }

    private void updateDistance(Location location) {
        if (location == null)
            throw new NullPointerException();

        Location.distanceBetween(physicalLocation.getLatitude(), 
                                 physicalLocation.getLongitude(), 
                                 location.getLatitude(),
                                 location.getLongitude(), 
                                 distanceArray);
        distance = distanceArray[0];
    }

    /**
     * Tell if the x/y position is on this marker (if the marker is visible)
     * 
     * @param x
     *            float x value.
     * @param y
     *            float y value.
     * @return True if Marker is visible and x/y is on the marker.
     */
    public int handleClick(float x, float y) {
        if (!isOnRadar || !isInView)
            return 0;

       int result = isPointOnMarker(x, y);
        Log.e("handleClick", "point (x="+x+" y="+y+") isPointOnMarker="+result);
        return result;
    }

    /**
     * Determines if the marker is on this Marker.
     * 
     * @param marker
     *            Marker to test for overlap.
     * @return True if the marker is on Marker.
     */
    public boolean isMarkerOnMarker(Marker marker) {
        return isMarkerOnMarker(marker, true);
    }

    /**
     * Determines if the marker is on this Marker.
     * 
     * @param marker
     *            Marker to test for overlap.
     * @param reflect
     *            if True the Marker will call it's self recursively with the
     *            opposite arguments.
     * @return True if the marker is on Marker.
     */
    private boolean isMarkerOnMarker(Marker marker, boolean reflect) {
        if (marker == null)
            return false;

        marker.getScreenPosition().get(locationArray);
        float x = locationArray[0];
        float y = locationArray[1];

        int middleOfMarker = isPointOnMarker(x, y);
        if (middleOfMarker==1)
            return true;

        boolean onGps = isPaintableOnMarker(marker.gpsSymbol);
        if (onGps)
            return true;

        boolean onText = isPaintableOnMarker(marker.textBox);
        if (onText)
            return true;

        // If reflect is True then reverse the arguments and see if this Marker
        // is on the other marker.
        return (reflect) ? marker.isMarkerOnMarker(this, false) : false;
    }

    private boolean isPaintableOnMarker(PaintableObject paintable) {
        if (paintable == null)
            return false;

        box.set(paintable);

        // UL
        float ulX = box.ulX;
        float ulY = box.ulY;
        // UR
        float urX = box.urX;
        float urY = box.urY;
        // LL
        float llX = box.llX;
        float llY = box.llY;
        // LR
        float lrX = box.lrX;
        float lrY = box.lrY;

       int upperLeftOfMarker = isPointOnMarker(ulX, ulY);   //my lame code
        if (upperLeftOfMarker==1)
            return true;

        int upperRightOfMarker = isPointOnMarker(urX, urY);
        if (upperRightOfMarker==1)
            return true;

       int lowerLeftOfMarker = isPointOnMarker(llX, llY);
        if (lowerLeftOfMarker==1)
            return true;

        int lowerRightOfMarker = isPointOnMarker(lrX, lrY);
        if (lowerRightOfMarker==1)
            return true;

        return false;
    }

    /**
     * Determines if the point is on this Marker.
     * 
     * @param xPoint
     *            X point.
     * @param yPoint
     *            Y point.
     * @return True if the point is on this Marker.
     */
    private int isPointOnMarker(float xPoint, float yPoint) {
    	int gps=0;
        box.set(gpsSymbol);
       if (box.isPointInBox(xPoint, yPoint)){
    	   gps=1;
       
       return 1;
       }
    	
        box.set(textBox);
         if (box.isPointInBox(xPoint, yPoint))
        {
        	if(gps!=1){
        		return 2;
        	}
        	
        	return 0;	
        }
        
        
           return 0;
   
    }

    /**
     * Draw this Marker on the Canvas
     * 
     * @param canvas
     *            Canvas to draw on.
     * @throws NullPointerException
     *             if the Canvas is NULL.
     */
    public void draw(Canvas canvas,boolean flag,int changed) {
        if (canvas == null)
            throw new NullPointerException();

        // If not visible then do nothing
        if (!isOnRadar || !isInView)
            return;
//g_flag=flag;
        // Draw the Icon and Text
        drawIcon(canvas);
if(flag){
        drawText(canvas);
        Log.e("drawing true","gl");
        
}
else if (flag==false){
	Log.e("drawing false","gl");
	if(changed==1){
		//drawText(canvas);
	}
	else{
		drawTextwithicon(canvas);
	}
}
     //Log.e("dialog check",Boolean.toString(flag));
// Draw the exact position
        if (debugGpsPosition)
            drawPosition(canvas);
    }
    private float findMax(float... vals) {
    	   float max = Float.MIN_VALUE;

    	   for (float d : vals) {
    	      if (d > max) max = d;
    	   }

    	   return max;
    	}
    private void drawTextwithicon(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();

        textStr.setLength(0);
        if (distance < 1000.0) {
            textStr.append(name).append(" (").append(DECIMAL_FORMAT.format(distance)).append("m)");
        } else {
            double d = distance / 1000.0;
            textStr.append(name).append(" (").append(DECIMAL_FORMAT.format(d)).append("km)");
        }
        float maxHeight = Math.round(canvas.getHeight() / 10f) + 1;

        if (textBox == null)
           // textBox = new PaintableBoxedText(textStr.toString(), Math.round(maxHeight / 2f) + 1, 300, 0, 300);
        textBox = new PaintableBoxedText(textStr.toString(),Math.round(maxHeight / 2f) + 1, 300, Color.rgb(255, 255, 255), Color.argb(150,255, 255, 255), Color.rgb(0, 0, 0), 0, 300);
        else
            textBox.set(textStr.toString(), Math.round(maxHeight / 2f) + 1, 300);

        getScreenPosition().get(locationArray);
        float x = locationArray[0];
        float y = locationArray[1];

        // Adjust the text to be below
        textBox.setCoordinates(0, textBox.getHeight()/2);

        float currentAngle = 0;
        if (AugmentedReality.useMarkerAutoRotate) {
            currentAngle = ARData.getDeviceOrientationAngle()+90;
            currentAngle = 360 - currentAngle;
        }
        if (textContainer == null)
            textContainer = new PaintablePosition(textBox, x, y, currentAngle, 1);
        else
            textContainer.set(textBox, x, y, currentAngle, 1);

        textContainer.paint(canvas);
    }
    protected void drawIcon(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();

        if (gpsSymbol == null)
            gpsSymbol = new PaintableGps(36, 8, true, getColor());

        getScreenPosition().get(locationArray);
        float x = locationArray[0];
        float y = locationArray[1];

        // Adjust the symbol to be above
        gpsSymbol.setCoordinates(0, -gpsSymbol.getHeight()/2);

        float currentAngle = 0;
        if (AugmentedReality.useMarkerAutoRotate) {
            currentAngle = ARData.getDeviceOrientationAngle()+90;
            currentAngle = 360 - currentAngle;
        }
        if (symbolContainer == null)
            symbolContainer = new PaintablePosition(gpsSymbol, x, y, currentAngle, 1);
        else
            symbolContainer.set(gpsSymbol, x, y, currentAngle, 1);

        symbolContainer.paint(canvas);
    }
//added distance text
    private void drawText(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int w = size.x;
        int h = size.y;
        float density = context.getResources().getDisplayMetrics().density;
        textStr.setLength(0);
        vicStr.setLength(0);
        distStr.setLength(0);
        ORIENTATION orient = ORIENTATION.LANDSCAPE;
        orient = ARData.getDeviceOrientation();
            //textStr.append(name);//.append("\r\n").append("").append(DECIMAL_FORMAT.format(distance)).append("m)");
        
          //  double d = distance / 1000.0;
            textStr.append("  ").append(name).append("  ");//.append("\r\n");//.append(DECIMAL_FORMAT.format(d)).append("km");
            vicStr.append(vicinity);
        if (distance < 1000.0) {
            distStr.append("").append(DECIMAL_FORMAT.format(distance)).append("m");
        } else {
            double d = distance / 1000.0;
            distStr.append("").append(DECIMAL_FORMAT.format(d)).append("km");
        }
        float maxHeight;
       
       
        	 maxHeight = Math.round(canvas.getHeight() / 10f)-5 ;
        
Log.w("name length",Integer.toString(name.length()));
Log.w("vic length",Integer.toString(vicinity.length()));
distStr.length();
float length=Math.max(name.length(),vicinity.length());
        if (textBox == null)
            {
        	textBox = new PaintableBoxedText(textStr.toString(), Math.round(maxHeight / 2f) + 1, w/3,1,0);
        	vicBox = new PaintableBoxedText(vicStr.toString(), Math.round(maxHeight / 2f) + 1, w/3,Color.rgb(0, 0, 0), Color.argb(255, 0, 0, 0), Color.rgb(240, 255, 255),0,0);
        distBox = new PaintableBoxedText(distStr.toString(), Math.round(maxHeight / 2f) + 1, 300,Color.rgb(0,0,0), Color.argb(255, 0, 0, 0), Color.rgb(255, 0, 0),0,0);
        }
        else{
        	
        	
            textBox.set(textStr.toString(), Math.round(maxHeight / 2f) + 1, w/2); //box width
            vicBox.set(vicStr.toString(), Math.round(h / 30f) + 1, 500);
            distBox.set(distStr.toString(), Math.round(maxHeight / 2f) + 1, 100);
        	
        	}
        getScreenPosition().get(locationArray);
        float x = locationArray[0];
        float y = locationArray[1];

        // Adjust the text to be below
        textBox.setCoordinates(0, textBox.getHeight()/2);
        vicBox.setCoordinates(0, h/4);
       distBox.setCoordinates(0, textBox.getHeight()-100);
       
       
       
       
      maxX = canvas.getWidth();
      
       maxY = canvas.getHeight();
        float currentAngle = 0;
        
        
        
        if (AugmentedReality.useMarkerAutoRotate) {
            currentAngle = ARData.getDeviceOrientationAngle()+90;
            currentAngle = 360 - currentAngle;
            orient = ARData.getDeviceOrientation();
        }
        if (textContainer == null)
            {textContainer = new PaintablePosition(textBox, maxX, maxY, 0, 1);
            distContainer = new PaintablePosition(vicBox, maxX, maxY, 0, 1);
            vicContainer = new PaintablePosition(distBox, maxX, maxY, 0, 1);
            }
       
        else{
        	
        	
        		textContainer = new PaintablePosition(textBox, (maxX/2)-150, (h/2)+50, 0, 1);
        		
        		distContainer = new PaintablePosition(distBox, (w/2-150), (h/2+120), 0, 1);
        		vicContainer = new PaintablePosition(vicBox, (maxX/2)-150, (h/2), 0, 1);
        	
        	}
        
     //  Bitmap bitmap = BitmapFactory.decodeResource( context.getResources(), R.drawable.rec1);
      

    
       textContainer.paint(canvas);
       vicContainer.paint(canvas);
        distContainer.paint(canvas);
        
       
            
            	if(density==2){
            	 Bitmap bitmap = BitmapFactory.decodeResource( context.getResources(), R.drawable.but);
                 PaintableIcon radarpic=    new PaintableIcon(bitmap, 529, 255);
                markerContainer = new PaintablePosition(radarpic, (maxX/2)+100, (maxY/2)+230, 0, 1);
               markerContainer.paint(canvas);}
            
    }
    public int pxtodp(int pixel) {
    	final float scale = context.getResources().getDisplayMetrics().density;
    	int dip = (int) (pixel* scale + 0.5f);
    	return dip;
    }
    private void drawPosition(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();

        if (positionPoint == null)
            positionPoint = new PaintablePoint(Color.MAGENTA, true);

        getScreenPosition().get(locationArray);
        float x = locationArray[0];
        float y = locationArray[1];

        float currentAngle = 0;
        if (AugmentedReality.useMarkerAutoRotate) {
            currentAngle = ARData.getDeviceOrientationAngle()+90;
            currentAngle = 360 - currentAngle;
        }
        if (positionContainer == null)
            positionContainer = new PaintablePosition(positionPoint, x, y, currentAngle, 1);
        else
            positionContainer.set(positionPoint, x, y, currentAngle, 1);

        positionContainer.paint(canvas);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Marker another) {
        if (another == null)
            throw new NullPointerException();

        return name.compareTo(another.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object marker) {
        if (marker == null || name == null)
            throw new NullPointerException();

        return name.equals(((Marker)marker).getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    private static final class Box {

        private final float[] points = new float[]{0,0};
        private final Matrix matrix = new Matrix();

        // UL
        private float ulX = 0;
        private float ulY = 0;
        // UR
        private float urX = 0;
        private float urY = 0;
        // LL
        private float llX = 0;
        private float llY = 0;
        // LR
        private float lrX = 0;
        private float lrY = 0;

        private void set(PaintableObject paintable) {
            if (paintable == null)
                return;

            float x = paintable.getX();
            float y = paintable.getY();
            if ((paintable instanceof PaintableGps) || (paintable instanceof PaintableCircle)) {
                // drawing circles is slightly different then anything else, need to handle differently
                x = -paintable.getWidth()/2;
                y = -paintable.getHeight();
            }
            matrix.set(paintable.matrix);

            // UL
            points[0] = x;
            points[1] = y;
            matrix.mapPoints(points);
            ulX = points[0];
            ulY = points[1];

            // UR
            points[0] = x+paintable.getWidth();
            points[1] = y;
            matrix.mapPoints(points);
            urX = points[0];
            urY = points[1];

            // LL
            points[0] = x;
            points[1] = y+paintable.getHeight();
            matrix.mapPoints(points);
            llX = points[0];
            llY = points[1];

            // LR
            points[0] = x+paintable.getWidth();
            points[1] = y+paintable.getHeight();
            matrix.mapPoints(points);
            lrX = points[0];
            lrY = points[1];
        }

        /*
         * Determines what side of a line a point lies.
         */
        private static final byte side(float Ax, float Ay, 
                                          float Bx, float By, 
                                          float x, float y) {
            float result = (Bx-Ax)*(y-Ay) - (By-Ay)*(x-Ax);
            if (result<0) {
                // below or right
                return -1;
            }
            if (result>0) {
                // above or left
                return 1;
            }
            // on the line
            return 0;
        }

        /*
         * Determines if point is between two lines
         */
        private static final boolean between(float aX, float aY, 
                                                float bX, float bY, 
                                                float cX, float cY, 
                                                float dX, float dY, 
                                                float x, float y) {
            byte first = side(aX, aY, bX, bY, x, y);
            byte second = side(cX, cY, dX, dY, x, y);
            if (first==(second*-1)) return true;
            return false;
        }

        private boolean isPointInBox(float xPoint, float yPoint) {
            // Is the point between the top and bottom lines
            boolean betweenTB = between(ulX, ulY, 
                                         urX, urY, 
                                         llX, llY, 
                                         lrX, lrY, 
                                         xPoint, yPoint);
            // Is the point between the left and right lines
            boolean betweenLR = between(ulX, ulY, 
                                         llX, llY, 
                                         urX, urY, 
                                         lrX, lrY, 
                                         xPoint, yPoint);
            if (betweenTB && betweenLR) return true;
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "ul=("+ulX+", "+ulY+") "+
                    "ur=("+urX+", "+urY+")\n"+
                    "ll=("+llX+", "+llY+") "+
                    "lr=("+lrX+", "+lrY+")";
        }
    }
}
