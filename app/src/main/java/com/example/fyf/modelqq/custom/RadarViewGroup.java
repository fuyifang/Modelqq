package com.example.fyf.modelqq.custom;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.example.fyf.modelqq.R;
import com.example.fyf.modelqq.bean.Info;

/**
 * Created by FYF on 2016/5/20.
 */
public class RadarViewGroup extends ViewGroup implements RadarView.IScanningListener {
    private int mWidth,mHeight;//viewgroup的宽高
    private SparseArray<Float> scanAnglelist = new SparseArray<>();//记录展示item所在扫描位置角度
    private SparseArray<Info> mDatas;
    private int dateLength;//数据源长度
    private int minItemPosition;//最小距离的item所在的数据源的位置
    private CircleView currentShowChild;//当前展示的Item;
    private CircleView minShowChild;//最小距离的item;
    private IRadarClickListener iRadarClickListener;//雷达图中点击监听CircleView小圆点回调接口

    public void setiRadarClickListener(IRadarClickListener iRadarClickListener) {
        this.iRadarClickListener = iRadarClickListener;
    }

    public RadarViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RadarViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RadarViewGroup(Context context) {
        this(context,null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureSize(widthMeasureSpec),measureSize(heightMeasureSpec));
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mWidth = mHeight= Math.min(mWidth,mHeight);
        //测量每个children
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        for (int i = 0;i<getChildCount();i++){
            View child = getChildAt(i);
            if (child.getId() == R.id.id_scan_circle){
                //为雷达扫描图设置需要的属性
                ((RadarView) child).setiScanningListener(this);
                //数据没有添加前扫描图在扫描，但是不会为circleview布局
                if (mDatas!=null && mDatas.size()>0){
                    ((RadarView)child).setMaxScanItemCount(mDatas.size());
                    ((RadarView) child).startScan();
                }
                continue;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        //首先放置雷达扫描图
        View view = findViewById(R.id.id_scan_circle);
        if (view != null){
            view.layout(0,0,view.getMeasuredWidth(),view.getMeasuredHeight());
        }
        for (int i = 0;i<childCount;i++){
            final int j = i;
            final View child = getChildAt(i);
            if (child.getId() == R.id.id_scan_circle){
                //跳过不是circleview
                continue;
            }
            //设置CircleView小圆点的坐标信息
            //坐标 = 旋转角度*半径*根据远近距离的不同计算得到的应该占的半径比例
            ((CircleView) child).setDisx((float) Math.cos(Math.toRadians(scanAnglelist.get(i - 1) - 5))
                    * ((CircleView) child).getProportion() * mWidth / 2);
            ((CircleView) child).setDisY((float) Math.sin(Math.toRadians(scanAnglelist.get(i - 1) - 5))
                    * ((CircleView) child).getProportion() * mWidth / 2);
            //如果扫描角度记录spareArray中的对应的item的值为0
            //说明还没有扫描到该item,跳过对改item的layout
            //scanAnglelist设置数据时全部设置的value = 0
            if (scanAnglelist.get(i-1) == 0){
                continue;
            }
            //放置Circle小圆点
            child.layout((int) ((CircleView) child).getDisx() + mWidth / 2, (int) ((CircleView) child).getDisY() + mHeight / 2,
                    (int) ((CircleView) child).getDisx() + child.getMeasuredWidth() + mWidth / 2,
                    (int) ((CircleView) child).getDisY() + child.getMeasuredHeight() + mHeight / 2);
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetAnim(currentShowChild);
                    currentShowChild = (CircleView) child;
                    //因为雷达图是childAt(0),所以这里需要作-1才是正确的Circle
                    startAnim(currentShowChild,j-1);
                    if (iRadarClickListener != null){
                        iRadarClickListener.onRadarItemClick(j-1);

                    }

                }
            });

        }

    }

    private void startAnim(CircleView currentShowChild, int position) {
        if (currentShowChild != null) {
            currentShowChild.serPortraitIcon(mDatas.get(position).getPortraitId());
            ObjectAnimator.ofFloat(currentShowChild, "scaleX", 2f).setDuration(300).start();
            ObjectAnimator.ofFloat(currentShowChild, "scaleY", 2f).setDuration(300).start();
        }
    }

    private void resetAnim(CircleView currentShowChild) {
        if (currentShowChild != null) {
            currentShowChild.clearPortaitIcon();
            ObjectAnimator.ofFloat(currentShowChild, "scaleX", 1f).setDuration(300).start();
            ObjectAnimator.ofFloat(currentShowChild, "scaleY", 1f).setDuration(300).start();
        }
    }
    public void setDatas(SparseArray<Info> datas){
        this.mDatas =datas;
        dateLength = mDatas.size();
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        //找到距离的最大值，最小值对应的minitemPosition
        for (int j = 0;j<dateLength;j++){
            Info item = mDatas.get(j);
            if (item.getDistance()<min){
                min = item.getDistance();
                minItemPosition = j;
            }if (item.getDistance()>max){
                max = item.getDistance();
            }
            scanAnglelist.put(j,0f);
        }
        //根据数据源信息动态添加CircleView
        for (int i = 0;i<dateLength;i++){
            CircleView circleview = new CircleView(getContext());
            if (mDatas.get(i).getSex()){
                circleview.setPaintColor(getResources().getColor(R.color.bg_color_pink));
            }else {
                circleview.setPaintColor(getResources().getColor(R.color.bg_color_blue));
            }   //根据远近距离的不同计算得到的应该占的半径比例 0.312-0.832
            circleview.setProporticon((mDatas.get(i).getDistance() / max + 0.6f) * 0.52f);
            if (minItemPosition == i) {
                minShowChild = circleview;
            }
            addView(circleview);


        }
    }
    private int measureSize(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 300;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;

    }

    @Override
    public void onScanning(int position, float scanAngle) {
        if (scanAngle == 0) {
            scanAnglelist.put(position, 1f);
        } else {
            scanAnglelist.put(position, scanAngle);
        }
        requestLayout();
    }

    @Override
    public void onScanSuccess() {
        Log.i("Tag","完成回调");
        resetAnim(currentShowChild);
        currentShowChild = minShowChild;
        startAnim(currentShowChild, minItemPosition);

    }
    /**
     * 雷达图中点击监听CircleView小圆点回调接口
     */
    public interface IRadarClickListener {
        void onRadarItemClick(int position);
    }
    /**
     * 根据position，放大指定的CircleView小圆点
     *
     * @param position
     */
    public void setCurrentShowItem(int position) {
        CircleView child = (CircleView) getChildAt(position + 1);
        resetAnim(currentShowChild);
        currentShowChild = child;
        startAnim(currentShowChild, position);
    }
}
