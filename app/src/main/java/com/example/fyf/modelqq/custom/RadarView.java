package com.example.fyf.modelqq.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.example.fyf.modelqq.R;

/**
 * Created by FYF on 2016/5/20.
 */
public class RadarView extends View {
    private Paint mPaintLine;//画线
    private Paint mPaintCircle;//画圆
    private Paint mPaintScan;//画扫描

    private int mWidth,mHeight;//画整个图形的长度和宽度

    private Matrix matrix = new Matrix();//旋转需要的矩阵
    private int scaleAngle;//扫面旋转需要的角度
    private Shader scanShader;//扫面渲染shader;
    private Bitmap centerBitmap;//最中间的Icon

    //每个圆圈所占的比例
    private static float[] circleProportion = {1 / 13f, 2 / 13f, 3 / 13f, 4 / 13f, 5 / 13f, 6 / 13f};
    private int scanSpeed = 5;//旋转速度

    private int currentScanningCount;//当前扫描的次数;
    private int currentScanningItem;//当前扫面显示的item;
    private int maxScanItemCount;//最大扫描次数
    private boolean startScan = false;//只有设置了数据后才会开始扫描
    private IScanningListener iScanningListener;//扫描时监听回调接口;
    public void setiScanningListener(IScanningListener listener){
        this.iScanningListener = listener;
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            scaleAngle = (scaleAngle+scanSpeed)%360;
            matrix.postRotate(scanSpeed,mWidth/2,mHeight/2);
            invalidate();
            postDelayed(run,130);
            //开始扫描显示标识为true,且只扫描一圈
            if (startScan && currentScanningCount <=(360/scanSpeed)){
                    //正在扫描中
                    if (iScanningListener !=null && currentScanningCount%scanSpeed ==0 && currentScanningItem<maxScanItemCount){
                        iScanningListener.onScanning(currentScanningItem,scaleAngle);
                        currentScanningItem++;
                    }else if (iScanningListener != null&& currentScanningItem == maxScanItemCount){//扫描完成
                        iScanningListener.onScanSuccess();
                    }
                currentScanningCount++;
            }
        }
    };



    public RadarView(Context context) {
        this(context,null);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RadarView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        init();
        post(run);
    }

    private void init() {
        mPaintLine = new Paint();
        mPaintLine.setColor(getResources().getColor(R.color.line_color_blue));
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStrokeWidth(1);
        mPaintLine.setStyle(Paint.Style.STROKE);

        mPaintCircle = new Paint();
        mPaintCircle.setColor(Color.WHITE);
        mPaintCircle.setAntiAlias(true);

        mPaintScan = new Paint();
        mPaintScan.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureSize(widthMeasureSpec),measureSize(widthMeasureSpec));
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mWidth = mHeight = Math.min(mWidth,mHeight);
        centerBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.avatar);
        //设置扫描渲染的shader
        scanShader = new SweepGradient(mWidth/2,mHeight/2,
                new int[]{Color.TRANSPARENT,Color.parseColor("#84B5CA")},null);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int measureSize(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else {
            result = 300;
            if (specMode == MeasureSpec.AT_MOST){
                result = Math.min(result,specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawCircle(canvas);
        drawScan(canvas);
        drawcenterIcon(canvas);
    }

    /**
     * 绘制最中间的图标
     * @param canvas
     */
    private void drawcenterIcon(Canvas canvas) {
        canvas.drawBitmap(centerBitmap,null, new Rect((int) (mWidth / 2 - mWidth * circleProportion[0]), (int) (mHeight / 2 - mWidth * circleProportion[0]),
                (int) (mWidth / 2 + mWidth * circleProportion[0]), (int) (mHeight / 2 + mWidth * circleProportion[0])), mPaintCircle);

    }

    /**
     * 绘制扫描区域
     */
    private void drawScan(Canvas canvas) {
        canvas.save();
        mPaintScan.setShader(scanShader);
        canvas.concat(matrix);
        canvas.drawCircle(mWidth/2,mHeight/2,mWidth*circleProportion[4],mPaintScan);
        canvas.restore();


    }

    /**
     * 绘制圆
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(mWidth/2,mHeight/2,mWidth*circleProportion[1],mPaintLine);
        canvas.drawCircle(mWidth/2,mHeight/2,mWidth*circleProportion[2],mPaintLine);
        canvas.drawCircle(mWidth/2,mHeight/2,mWidth*circleProportion[3],mPaintLine);
        canvas.drawCircle(mWidth/2,mHeight/2,mWidth*circleProportion[4],mPaintLine);
        canvas.drawCircle(mWidth/2,mHeight/2,mWidth*circleProportion[5],mPaintLine);
    }


    public interface IScanningListener{
        //正在扫描的回调
        void onScanning(int position,float scanAngle);
        //扫面成功的回调
        void onScanSuccess();
    }
    public void startScan(){
        this.startScan = true;
    }
    public void setMaxScanItemCount(int maxScanItemCount){
        this.maxScanItemCount = maxScanItemCount;
    }

}

