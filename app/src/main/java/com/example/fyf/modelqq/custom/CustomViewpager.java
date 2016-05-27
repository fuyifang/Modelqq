package com.example.fyf.modelqq.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by FYF on 2016/5/20.
 */
public class CustomViewpager extends ViewPager {
    private long downTime;
    private float LastX;
    private float mSpeed;


    public CustomViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomViewpager(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                LastX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                x = ev.getX();
                break;
            case MotionEvent.ACTION_UP:
                //计算得到从手指按下到离开的滑动速度
                mSpeed = (x-LastX)*1000/(System.currentTimeMillis()-downTime);

        }
        return super.dispatchTouchEvent(ev);
    }
    public float getSpeed(){
        return mSpeed;
    }
    public void setSpeed(float speed){
        this.mSpeed = speed;
    }
}
