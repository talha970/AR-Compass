package com.augmented_reality.ui;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;


import com.augmented_reality.R;
import com.augmented_reality.activity.AugmentedReality;
import com.augmented_reality.camera.CameraModel;
import com.augmented_reality.common.Orientation.ORIENTATION;
import com.augmented_reality.data.ARData;
import com.augmented_reality.data.ScreenPosition;
import com.augmented_reality.ui.objects.PaintableBox;
import com.augmented_reality.ui.objects.PaintableCircle;
import com.augmented_reality.ui.objects.PaintableIcon;
import com.augmented_reality.ui.objects.PaintableLine;
import com.augmented_reality.ui.objects.PaintablePosition;
import com.augmented_reality.ui.objects.PaintableRadarPoints;
import com.augmented_reality.ui.objects.PaintableText;


/**
 * This class will visually represent a radar screen with a radar radius and
 * blips on the screen in their appropriate locations.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class Radar {

    public static float RADIUS = 100;
    private static Context context;
    private static final int LINE_COLOR = Color.argb(150, 0, 0, 220);
    private static final float PAD_X = 15;
    private static final float PAD_Y = 10;
    private static final int RADAR_COLOR = Color.argb(200, 249, 249, 249);
    private static final int BLACK_COLOR = Color.argb(200, 250, 250, 250);
    private static final int TEXT_COLOR = Color.rgb(0, 0, 0);
    private static final int TEXT_SIZE = 16;

    private static final StringBuilder DIR_TXT = new StringBuilder();
    private static final StringBuilder RADAR_TXT = new StringBuilder();
    private static final StringBuilder DIST_TXT = new StringBuilder();
    private static final StringBuilder DEC_TXT = new StringBuilder();

    private static ScreenPosition leftRadarLine = null;
    private static ScreenPosition rightRadarLine = null;
    private static PaintablePosition leftLineContainer = null;
    private static PaintablePosition rightLineContainer = null;
    private static PaintablePosition circleContainer = null;
    private static PaintablePosition innerContainer = null; //mine
    private static PaintablePosition innerinnerContainer = null; //mine
    private static PaintablePosition radarContainer = null; //mine
    private static PaintableRadarPoints radarPoints = null;
    private static PaintablePosition pointsContainer = null;
private Resources reso;
    private static PaintableText paintableText = null;
    private static PaintablePosition paintedContainer = null;

    public Radar(Resources res) {
        if (leftRadarLine == null)
            leftRadarLine = new ScreenPosition();
        if (rightRadarLine == null)
            rightRadarLine = new ScreenPosition();
       reso=res;
    }

    /**
     * Draw the radar on the given Canvas.
     * 
     * @param canvas
     *            Canvas to draw on.
     * @throws NullPointerException
     *             if Canvas is NULL.
     */
    public static void setContext(Context mcontext) {
        if (context == null)
            context = mcontext;
       
    }
    public void draw(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();

        // Adjust upside down to compensate for zoom-bar
        int ui_ud_pad = 80;
        if (AugmentedReality.ui_portrait) 
            ui_ud_pad = 55;

        ORIENTATION orient = ORIENTATION.LANDSCAPE;
        if (AugmentedReality.useRadarAutoOrientate) {
            orient = ARData.getDeviceOrientation();
            if (orient==ORIENTATION.LANDSCAPE_UPSIDE_DOWN) {
                canvas.save();
                canvas.translate(canvas.getWidth() - ui_ud_pad, canvas.getHeight());
                canvas.rotate(180);
            } else {
            	//RADIUS=100;
                // If landscape, do nothing
            }
        }

        // Update the radar graphics and text based upon the new pitch and bearing
        canvas.save();
        canvas.translate(0, 5);
        
        drawRadarCircle(canvas);
        //drawBox(canvas);
        drawRadarPoints(canvas);
        drawRadarLines(canvas);
        drawRadarText(canvas);
        canvas.restore();

        if (orient!=ORIENTATION.LANDSCAPE)
            canvas.restore();
    }
    public void drawBox(Canvas canvas) {
        if (canvas == null)
 throw new NullPointerException();
        ORIENTATION orient = ORIENTATION.LANDSCAPE;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int w = size.x;
        int h = size.y;
        float density = context.getResources().getDisplayMetrics().density;
        orient = ARData.getDeviceOrientation();
        int maxX = canvas.getWidth();
        int maxY = canvas.getHeight();
        PaintableBox box=new PaintableBox(canvas.getWidth(), canvas.getHeight(), BLACK_COLOR, BLACK_COLOR);
       
       
        PaintablePosition boxcontainer=new PaintablePosition(box, maxX/2, maxY+100, 0, 1);
        boxcontainer.paint(canvas);
        
        
    }
    
    private void drawRadarCircle(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int w = size.x;
        int h = size.y;
     /*   if (circleContainer == null) {
            PaintableCircle paintableCircle = new PaintableCircle(RADAR_COLOR, RADIUS, true);
            circleContainer = new PaintablePosition(paintableCircle, PAD_X + RADIUS, PAD_Y + RADIUS, 0, 1);
            PaintableCircle paintableinnerCircle = new PaintableCircle(BLACK_COLOR, RADIUS-10, false);//mine all of it
            innerContainer = new PaintablePosition(paintableinnerCircle, PAD_X + RADIUS, PAD_Y + RADIUS, 0, 1);
            PaintableCircle paintableinnerinnerCircle = new PaintableCircle(BLACK_COLOR, RADIUS-30, false);
            innerinnerContainer = new PaintablePosition(painta
            bleinnerinnerCircle, PAD_X + RADIUS, PAD_Y + RADIUS, 0, 1);
        }
        */
        ORIENTATION orient = ORIENTATION.LANDSCAPE;
        orient = ARData.getDeviceOrientation();
        int maxX = canvas.getWidth();
        int maxY = canvas.getHeight();
        Bitmap bitmap;
       
        	  bitmap = BitmapFactory.decodeResource(reso, R.drawable.radar);
        
     // DisplayMetrics metrics = new DisplayMetrics();  
      DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
     // context.getWindowManager().getDefaultDisplay().getMetrics(metrics); 
     
      //((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
     
      float density = context.getResources().getDisplayMetrics().density;
      
      float scale = metrics.densityDpi;
      int ht_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, context.getResources().getDisplayMetrics());
      int wt_px =(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 256, context.getResources().getDisplayMetrics());
    
        
        	PaintableIcon radarpic=    new PaintableIcon(bitmap,ht_px, (int)(wt_px));
        	radarpic.set(bitmap, 300, 256);
        	radarContainer = new PaintablePosition(radarpic, maxX-200,  maxY/2+100, 0, 1);
            radarContainer.paint(canvas);
       
      
        //circleContainer.paint(canvas);
        //innerContainer.paint(canvas);
        //innerinnerContainer.paint(canvas);
    }
    public int pxtodp(int pixel) {
    	final float scale = context.getResources().getDisplayMetrics().density;
    	int dip = (int) (pixel* scale + 0.5f);
    	return dip;
    }
    private void drawRadarPoints(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();
        float density = context.getResources().getDisplayMetrics().density;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int w = size.x;
        int h = size.y;
        ORIENTATION orient = ORIENTATION.LANDSCAPE;
        orient = ARData.getDeviceOrientation();
       // float density = context.getResources().getDisplayMetrics().density;
        int maxX = canvas.getWidth();
        int maxY = canvas.getHeight();
        if (radarPoints == null)
            radarPoints = new PaintableRadarPoints();

        if (pointsContainer == null)
            pointsContainer = new PaintablePosition(radarPoints, 0, 0, 0, 1);
        else
        	// pointsContainer.set(radarPoints, maxX-300, (maxY-295), 0, 1);
        pointsContainer.set(radarPoints, 0, 0, 0, 1);

        // Rotate the points to match the azimuth
       /* canvas.save();
        canvas.translate((PAD_X + radarPoints.getWidth() / 2), (PAD_X + radarPoints.getHeight() / 2));
        canvas.rotate(-ARData.getAzimuth());//////////TEE LOOK HERE!!
        canvas.scale(1, 1);
        canvas.translate(-(radarPoints.getWidth() / 2), -(radarPoints.getHeight() / 2));*/
        
     
        	if(density==2.0){
        canvas.save();
        canvas.translate(((maxX-300) + radarPoints.getWidth() / 2), (maxY/2 + radarPoints.getHeight() / 2));
        canvas.rotate(-ARData.getAzimuth());
        canvas.scale(1, 1);
        canvas.translate(-(radarPoints.getWidth() / 2), -(radarPoints.getHeight() / 2));
        pointsContainer.paint(canvas);
        canvas.restore();}
        	else if(density==1.5){
        		 canvas.save();
        	        canvas.translate(((maxX-300) + radarPoints.getWidth() / 2), (230 + radarPoints.getHeight() / 2));
        	        canvas.rotate(-ARData.getAzimuth());//////////TEE LOOK HERE!!
        	        canvas.scale(1, 1);
        	        canvas.translate(-(radarPoints.getWidth() / 2), -(radarPoints.getHeight() / 2));
        	        pointsContainer.paint(canvas);
        	        canvas.restore();}
        	else{
        		  canvas.save();
        	        canvas.translate(((maxX-300) + radarPoints.getWidth() / 2), (maxY/2 + radarPoints.getHeight() / 2));
        	        canvas.rotate(-ARData.getAzimuth());
        	        canvas.scale(1, 1);
        	        canvas.translate(-(radarPoints.getWidth() / 2), -(radarPoints.getHeight() / 2));
        	        pointsContainer.paint(canvas);
        	        canvas.restore();
        	}
        	
        }
    

    private void drawRadarLines(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();
        ORIENTATION orient = ORIENTATION.LANDSCAPE;
        orient = ARData.getDeviceOrientation();
        int maxX = canvas.getWidth();
        int maxY = canvas.getHeight();
        float density = context.getResources().getDisplayMetrics().density;
       
        
        	if(density==1.5){
        	 	RADIUS=95;
        	}
        	else if(density==2.0){
        		RADIUS=100;
        	}
        
        // Left line
        if (leftLineContainer == null) {
            leftRadarLine.set(0, -RADIUS);
            leftRadarLine.rotate(-CameraModel.DEFAULT_VIEW_ANGLE / 2);
            leftRadarLine.add(PAD_X + RADIUS, PAD_Y + RADIUS);

            float leftX = leftRadarLine.getX() - (PAD_X + RADIUS);
            float leftY = leftRadarLine.getY() - (PAD_Y + RADIUS);
            PaintableLine leftLine = new PaintableLine(LINE_COLOR, leftX, leftY);
            leftLineContainer = new PaintablePosition(leftLine, PAD_X + RADIUS, PAD_Y + RADIUS, 0, 1);
        }
    //    leftLineContainer.paint(canvas);

        // Right line
        if (rightLineContainer == null) {
            rightRadarLine.set(0, -RADIUS);
            rightRadarLine.rotate(CameraModel.DEFAULT_VIEW_ANGLE / 2);
            rightRadarLine.add(PAD_X + RADIUS, PAD_Y + RADIUS);

            float rightX = rightRadarLine.getX() - (PAD_X + RADIUS);
            float rightY = rightRadarLine.getY() - (PAD_Y + RADIUS);
            PaintableLine rightLine = new PaintableLine(LINE_COLOR, rightX, rightY);
            rightLineContainer = new PaintablePosition(rightLine, PAD_X + RADIUS, PAD_Y + RADIUS, 0, 1);
        }
      //  rightLineContainer.paint(canvas);
    }

    private void drawRadarText(Canvas canvas) {
        if (canvas == null)
            throw new NullPointerException();

        // Direction text
        int range = (int) (ARData.getAzimuth() / (360f / 16f));
        DIR_TXT.setLength(0);
        if (range == 15 || range == 0)
            DIR_TXT.append("N");
        else if (range == 1 || range == 2)
            DIR_TXT.append("NE");
        else if (range == 3 || range == 4)
            DIR_TXT.append("E");
        else if (range == 5 || range == 6)
            DIR_TXT.append("SE");
        else if (range == 7 || range == 8)
            DIR_TXT.append("S");
        else if (range == 9 || range == 10)
            DIR_TXT.append("SW");
        else if (range == 11 || range == 12)
            DIR_TXT.append("W");
        else if (range == 13 || range == 14)
            DIR_TXT.append("NW");
        float density = context.getResources().getDisplayMetrics().density;
        ORIENTATION orient = ORIENTATION.LANDSCAPE;
        orient = ARData.getDeviceOrientation();
        int maxX = canvas.getWidth();
        int maxY = canvas.getHeight();
        int azimuth = (int) ARData.getAzimuth();
        RADAR_TXT.setLength(0);
        RADAR_TXT.append(azimuth).append((char) 176).append(" ").append(DIR_TXT);
        // Azimuth text
       
        // Zoom text
       
        	if(density==2.0){
        		radarText(canvas, RADAR_TXT.toString(), maxX-200,  maxY/2-34, true);
        radarText(canvas, formatDist(ARData.getRadius() * 1000),  maxX-200, maxY/2+250, false);
        	}
        	else if(density==1.5){
        		radarText(canvas, RADAR_TXT.toString(), maxX-200,  maxY/2-10, true);
                radarText(canvas, formatDist(ARData.getRadius() * 1000),  maxX-200, maxY/2+200, false);	
        	}
        	else{
        		radarText(canvas, RADAR_TXT.toString(), maxX-200,  maxY/2-60, true);
                radarText(canvas, formatDist(ARData.getRadius() * 1000),  maxX-200, maxY/2+250, false);
        	}
        
        }

    private void radarText(Canvas canvas, String txt, float x, float y, boolean bg) {
        if (canvas == null || txt == null)
            throw new NullPointerException();

        if (paintableText == null)
            paintableText = new PaintableText(txt, TEXT_COLOR, TEXT_SIZE, bg);
        else
            paintableText.set(txt, TEXT_COLOR, TEXT_SIZE, bg);

        if (paintedContainer == null)
            paintedContainer = new PaintablePosition(paintableText, x, y, 0, 1);
        else
            paintedContainer.set(paintableText, x, y, 0, 1);

        paintedContainer.paint(canvas);
    }

    private static String formatDist(float meters) {
        DIST_TXT.setLength(0);
        if (meters < 1000)
            DIST_TXT.append((int) meters).append("m");
        else if (meters < 10000)
            DIST_TXT.append(formatDec(meters / 1000f, 1)).append("km");
        else
            DIST_TXT.append((int) (meters / 1000f)).append("km");
        return DIST_TXT.toString();
    }

    private static String formatDec(float val, int dec) {
        DEC_TXT.setLength(0);
        int factor = (int) Math.pow(10, dec);

        int front = (int) (val);
        int back = (int) Math.abs(val * (factor)) % factor;

        DEC_TXT.append(front).append(".").append(back);
        return DEC_TXT.toString();
    }
}
