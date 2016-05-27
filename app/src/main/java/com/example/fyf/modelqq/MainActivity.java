package com.example.fyf.modelqq;

import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyf.modelqq.bean.Info;
import com.example.fyf.modelqq.custom.CustomViewpager;
import com.example.fyf.modelqq.custom.RadarViewGroup;
import com.example.fyf.modelqq.utils.FixedSpeedScroller;
import com.example.fyf.modelqq.utils.ZoomOutPageTransformer;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,RadarViewGroup.IRadarClickListener {
    private CustomViewpager viewpager;
    private RelativeLayout ryContainer;
    private RadarViewGroup radarViewGroup;

    private int[] mImgs = {R.drawable.len, R.drawable.leo, R.drawable.lep,
            R.drawable.leq, R.drawable.ler, R.drawable.les, R.drawable.mln, R.drawable.mmz, R.drawable.mna,
            R.drawable.mnj, R.drawable.leo, R.drawable.leq, R.drawable.les, R.drawable.lep};
    private String[] mNames = { "肖大屌", "姚大屌", "翁大屌", "李大屌", "陈大屌", "付巨屌", "哆啦屌梦"};
    private int mPosition;
    private FixedSpeedScroller scroller;
    private SparseArray<Info> mDatas = new SparseArray<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        ryContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return viewpager.dispatchTouchEvent(event);
            }
        });
        ViewpagerAdapter adapter = new ViewpagerAdapter();
        viewpager.setAdapter(adapter);
        viewpager.setOffscreenPageLimit(mImgs.length);
        viewpager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.viewpager_margin));
        //设置切换动画
        viewpager.setPageTransformer(true,new ZoomOutPageTransformer());
        viewpager.addOnPageChangeListener(this);
        setViewPagerSpeed(250);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                radarViewGroup.setDatas(mDatas);
            }
        },1500);
        radarViewGroup.setiRadarClickListener(this);

    }

    private void initData() {
        for (int i = 0;i<mImgs.length;i++){
            Info info = new Info();
            info.setPortraitId(mImgs[i]);
            info.setAge((int)Math.random()*25+16+"岁");
            info.setName(mNames[(int) (Math.random()*mNames.length)]);
            info.setSex(i%3 ==0?false:true);
            info.setDistance(Math.round(Math.random()*10)*100/100);
            mDatas.put(i,info);

        }
    }

    private void initView() {
        viewpager = (CustomViewpager) findViewById(R.id.vp);
        ryContainer = (RelativeLayout) findViewById(R.id.ry_container);
        radarViewGroup = (RadarViewGroup) findViewById(R.id.radar);
    }

    /**
     * 设置viewpager切换速度
     * @param duration
     */
    private void setViewPagerSpeed(int duration){
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            scroller = new FixedSpeedScroller(MainActivity.this,new AccelerateInterpolator());
            field.set(viewpager,scroller);
            scroller.setmDuration(duration);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mPosition = position;


    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Tag","当前位置"+mPosition);
        Log.d("Tag","速度"+viewpager.getSpeed());
        radarViewGroup.setCurrentShowItem(position);

        if (viewpager.getSpeed()<-1800){
            viewpager.setCurrentItem(mPosition+2);
            Log.d("Tag","位置"+mPosition);
            viewpager.setSpeed(0);
        }else if (viewpager.getSpeed()>1800 && mPosition>0){
            viewpager.setCurrentItem(mPosition-1);
            Log.d("Tag","位置"+mPosition);
            viewpager.setSpeed(0);
        }


    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onRadarItemClick(int position) {
        viewpager.setCurrentItem(position);
    }

    class ViewpagerAdapter extends PagerAdapter{
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final Info info = mDatas.get(position);
            //设置一大堆演示用的数据，麻里麻烦~~
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.viewpager_layout, null);
            ImageView ivPortrait = (ImageView) view.findViewById(R.id.iv);
            ImageView ivSex = (ImageView) view.findViewById(R.id.iv_sex);
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            TextView tvDistance = (TextView) view.findViewById(R.id.tv_distance);
            tvName.setText(info.getName());
            tvDistance.setText(info.getDistance() + "km");
            ivPortrait.setImageResource(info.getPortraitId());
            if (info.getSex()) {
                ivSex.setImageResource(R.drawable.girl);
            } else {
                ivSex.setImageResource(R.drawable.boy);
            }
            ivPortrait.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "这是 " + info.getName() + " >.<", Toast.LENGTH_SHORT).show();
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return mImgs.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
