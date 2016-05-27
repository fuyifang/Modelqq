package com.example.fyf.modelqq.utils;

import android.content.Context;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by FYF on 2016/5/20.
 */
public class FixedSpeedScroller extends Scroller{
    private int mDuration = 1000;

    public FixedSpeedScroller(Context context, AccelerateInterpolator interpolator) {
        super(context, interpolator);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy,mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
    public void setmDuration(int duration){
        this.mDuration = duration;
    }
}
