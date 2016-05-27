package com.example.fyf.modelqq.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;

import com.example.fyf.modelqq.R;
import com.example.fyf.modelqq.utils.DisplayUtils;

/**
 * Created by FYF on 2016/5/20.
 */
public class CircleView extends View {
    private Paint mPaint;
    private Bitmap mBitmap;
    private float radius = DisplayUtils.dp2px(getContext(),9);//半径;
    private float disx;//位置x;
    private float disY;//位置y;
    private float angle;//旋转的角度
    private float proportion;//根据远近的距离的不同计算得到的应该占的半径的比例

    public float getDisx() {
        return disx;
    }

    public void setDisx(float disx) {
        this.disx = disx;
    }

    public float getDisY() {
        return disY;
    }

    public void setDisY(float disY) {
        this.disY = disY;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getProportion() {
        return proportion;
    }

    public void setProporticon(float proportion) {
        this.proportion = proportion;
    }

    public CircleView(Context context) {
        this(context,null);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(getResources().getColor(R.color.bg_color_pink));
        mPaint.setAntiAlias(true);//抗锯齿
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureSize(widthMeasureSpec),measureSize(heightMeasureSpec));
    }
    private int measureSize(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = DisplayUtils.dp2px(getContext(),18);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(radius,radius,radius,mPaint);
        if (mBitmap!=null){
            canvas.drawBitmap(mBitmap,null,new Rect(0,0,2*(int)radius,2*(int)radius),mPaint);
        }
    }
    public void setPaintColor(int resId){
        mPaint.setColor(resId);
        invalidate();
    }
    public void serPortraitIcon(int resID){
        mBitmap = BitmapFactory.decodeResource(getResources(),resID);
        invalidate();
    }
    public void clearPortaitIcon(){
        mBitmap = null;
        invalidate();
    }
}
