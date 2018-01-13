package com.endselect.bubbleup.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.endselect.bubbleup.R;


/**
 * Created by frank on 2018/1/12.
 */

public class CircleBadge extends View {

    float padding;
    double currentRatio;

    WaveDrawable wavy;

    public CircleBadge(Context context) {
        super(context);
        init();
    }

    public CircleBadge(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleBadge(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setCompletionRatioAnimated(double ratio) {
        if (wavy == null)
            return;

        int level = computeLevel(ratio);
        int currLevel = computeLevel(currentRatio);

        if (ratio <= currentRatio) {
            wavy.setWavyLevel(level);
        } else {
            wavy.setMaxWavyLevel(level);
            ObjectAnimator animator = ObjectAnimator.ofInt(wavy, "alpha", currLevel, level);
            animator.setDuration(3000);

            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animation.removeAllListeners();
                    wavy.setBubblesEnabled(false);
                }
            });
            animator.setInterpolator(new DecelerateInterpolator());
            animator.start();
            wavy.setBubblesEnabled(true);
        }
        currentRatio = ratio;
    }

    private int computeLevel(double ratio) {
        int level = (int) Math.round(ratio * getHeight());
        level = Math.min(getHeight(), Math.max(0, level));
        return level;
    }

    private void init() {
        setBackgroundResource(R.drawable.circle);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int height = bottom - top - getPaddingTop() - getPaddingBottom();
        padding = 0.13f * height;
        if (wavy == null) {
            wavy = new WaveDrawable(getContext(), getWidth(), getHeight());
            wavy.setCallback(this);
        } else {
            wavy.resetSize(getWidth(), getHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWater(canvas);
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || who == wavy;
    }

    private void drawWater(Canvas canvas) {
        canvas.save();
        Path circle = new Path();
        circle.addCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2, Path.Direction.CCW);
        circle.close();
        canvas.clipPath(circle);
        wavy.draw(canvas);
        canvas.restore();
    }
}
