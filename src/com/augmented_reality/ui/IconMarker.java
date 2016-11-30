package com.augmented_reality.ui;

import com.augmented_reality.ui.objects.PaintableIcon;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * This class extends Marker and draws an icon instead of a circle for it's
 * visual representation.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class IconMarker extends Marker {

    private Bitmap bitmap = null;

    public IconMarker(String name, double latitude, double longitude, double altitude, int color, Bitmap bitmap,String vic,String p) {
        super(name, latitude, longitude, altitude, color,vic,p);
        this.bitmap = bitmap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawIcon(Canvas canvas) {
        if (canvas == null || bitmap == null) throw new NullPointerException();

        // gpsSymbol is defined in Marker
        if (gpsSymbol == null) gpsSymbol = new PaintableIcon(bitmap, 64, 64);
        super.drawIcon(canvas);
    }
}
