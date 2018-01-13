package com.endselect.bubbleup.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;

import com.endselect.bubbleup.R;

/**
 * Created by frank on 2018/1/12.
 */

public class WaveDrawable extends Drawable {

    private static final int COUNT = 21;

    private Context context;
    private int width, height;
    private int backgroundColor, bubbleColor;
    private Paint paint, bubblePaint;

    private float[] u1 = new float[COUNT];
    private boolean bubblesEnabled;
    private int maxWavyLevel;

    public WaveDrawable(Context context, int width, int height) {
        this.context = context;
        this.width = width;
        this.height = height;

        this.backgroundColor = context.getResources().getColor(R.color.secondary_background);
        paint = new Paint();
        paint.setColor(backgroundColor);
        paint.setAntiAlias(true);

        this.bubbleColor = context.getResources().getColor(R.color.bubble_color);
        bubblePaint = new Paint();
        bubblePaint.setColor(bubbleColor);
        bubblePaint.setAntiAlias(true);
        bubblePaint.setStyle(Paint.Style.STROKE);
        bubblePaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                context.getResources().getDisplayMetrics()));

        setBounds(0, 0, width, height);
    }

    @Override
    public int getIntrinsicWidth() {
        return this.width;
    }

    @Override
    public int getIntrinsicHeight() {
        return this.height;
    }

    public void setBubblesEnabled(boolean bubblesEnabled) {
        this.bubblesEnabled = bubblesEnabled;
    }

    public void setWavyLevel(int level) {
        for (int i = 0; i < COUNT; i++) {
            u1[i] = level;
            invalidateSelf();
        }
    }

    public void setMaxWavyLevel(int maxWavyLevel) {
        this.maxWavyLevel = maxWavyLevel;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int w = width / (COUNT - 2);
        if (bubblesEnabled) {
            drawBubbles(canvas, w);
        }
    }

    private void drawBubbles(Canvas canvas, int columnWidth) {
        int Range = 200;
        int MinRadius = 2;
        int FadeStartLevel = 50;

        float level = u1[0];
        float maxRadius = ((columnWidth >> 1) * 8) / 10;
        float maxHeight = height >> 2;

        canvas.save();
        canvas.clipRect(0, height - level, width, height);
        int alphaDecrement = (int) ((1 - Math.min(FadeStartLevel, maxWavyLevel - level) / (
                (float) FadeStartLevel)) * 255);
        for (int i = 0; i < COUNT; i++) {
            float dispersion = ((float) Math.sin(i * ((4 * Math.PI) / COUNT)) + 1) * Range;
            float ratio = ((level + dispersion) % Range) / Range;
            int alpha = Math.round(255 - ratio * 200);
            alpha = Math.max(0, alpha - alphaDecrement);
            bubblePaint.setColor(blendColor(backgroundColor, bubbleColor, alpha));
            float cx = i * columnWidth + columnWidth / 2;
            float cy = height - level + (1 - ratio) * (maxHeight - maxRadius);
            float radius = MinRadius + ratio * (maxRadius - MinRadius);
            canvas.drawCircle(cx, cy, radius, bubblePaint);
        }
        canvas.restore();
    }

    private int blendColor(int bg, int fg, int alpha) {
        float fAlpha = alpha / 255f;
        int r = (int) (Color.red(bg) * (1 - fAlpha) + Color.red(fg) * fAlpha);
        int g = (int) (Color.green(bg) * (1 - fAlpha) + Color.green(fg) * fAlpha);
        int b = (int) (Color.blue(bg) * (1 - fAlpha) + Color.blue(fg) * fAlpha);
        return Color.rgb(r, g, b);
    }

    @Override
    public void setAlpha(int alpha) {
        setWavyLevel(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    private float computeIdealSpeed() {
        int radDip = Math.round(((float) (width / 2)) / context.getResources().getDisplayMetrics
                ().density);
        return radDip * 1.36f;
    }

    public void resetSize(int width, int height) {
        int oldHeight = this.height;
        this.width = width;
        this.height = height;

        setBounds(0, 0, width, height);

        if (oldHeight != height) {
            for (int i = 0; i < COUNT; i++) {
                u1[i] = (u1[i] / oldHeight) * height;
            }
        }
    }
}
