package com.guodongbaohe.app.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.ShopDynamicDiaplayAdapter;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.view.DivergeViewSecond;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*商品免单动态展示*/
public class ShopDynamicDisplayActivity extends BigBaseActivity {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.re_yidong)
    RelativeLayout re_yidong;
    @BindView(R.id.tv_buied)
    TextView tv_buied;
    @BindView(R.id.divergeviewsecond)
    DivergeViewSecond divergeviewsecond;
    @BindView(R.id.iv_love_heart)
    ImageView iv_love_heart;
    TimerTask task;
    private boolean isLoop = true;
    private int listSize = 1;
    ShopDynamicDiaplayAdapter adapter;
    Timer timer;
    int width, screen_x, heightPixels;
    private List<Bitmap> mList = new ArrayList<Bitmap>();
    private int mIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopdynamicdisplayactivity);
        ButterKnife.bind(this);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screen_x = displayMetrics.widthPixels;
        heightPixels = displayMetrics.heightPixels;
        initLoveImages();
        initTransAnimation();
        initBottomAnimation();
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new ShopDynamicDiaplayAdapter(getApplicationContext(), listSize);
        recyclerview.setAdapter(adapter);
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if (isLoop) {
                    handler.sendEmptyMessage(0);
                }
            }
        };
        if (isLoop) {
            timer.schedule(task, 2000, 4000);
        }
    }

    private void initLoveImages() {
        mList = new ArrayList<>();
        Bitmap bitmap = ((BitmapDrawable) ResourcesCompat.getDrawable(getResources(), R.mipmap.yi_collected, null)).getBitmap();
        for (int i = 0; i < 100; i++) {
            mList.add(bitmap);
        }
        divergeviewsecond.post(new Runnable() {
            @Override
            public void run() {
                divergeviewsecond.setEndPoint(new PointF(divergeviewsecond.getMeasuredWidth() / 2, 0));
                divergeviewsecond.setDivergeViewProvider(new Provider());
            }
        });
    }

    class Provider implements DivergeViewSecond.DivergeViewProvider {

        @Override
        public Bitmap getBitmap(Object obj) {
            return mList == null ? null : mList.get((int) obj);
        }
    }

    @OnClick({R.id.iv_love_heart})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.iv_love_heart:
                if (mIndex == 8) {
                    mIndex = 0;
                }
                divergeviewsecond.startDiverges(mIndex);
                mIndex++;
                break;
        }
    }

    private void initTransAnimation() {
        ViewTreeObserver observer = re_yidong.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                re_yidong.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                width = re_yidong.getMeasuredWidth();
                final TranslateAnimation translateAnimation = new TranslateAnimation(screen_x, -width, 0, 0);
                translateAnimation.setDuration(20000);
                re_yidong.setAnimation(translateAnimation);
                translateAnimation.setFillEnabled(true);
                translateAnimation.setFillAfter(true);
                translateAnimation.startNow();
                translateAnimation.setAnimationListener(
                        new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {//开始时
                                re_yidong.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {//结束时
                                re_yidong.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        re_yidong.setVisibility(View.GONE);
                                        re_yidong.startAnimation(translateAnimation);
                                    }
                                });
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
            }
        });
    }

    private void initBottomAnimation() {
        final AnimationSet animationSet = new AnimationSet(false);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, heightPixels, -10000);
        Animation alphaAnimation = new AlphaAnimation(0, 1.0f);
        animationSet.addAnimation(translateAnimation);
//        animationSet.addAnimation(alphaAnimation);
        animationSet.setFillAfter(true);
        animationSet.setDuration(10000);
        tv_buied.startAnimation(animationSet);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                tv_buied.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                tv_buied.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_buied.startAnimation(animationSet);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (listSize == 20) {
                timer.cancel();
            } else {
                listSize++;
                adapter.addLitSize(listSize);
                recyclerview.scrollToPosition(listSize - 1);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (task != null) {
            task.cancel();
        }
        if (timer != null) {
            timer.cancel();
        }
        if (mList != null) {
            mList.clear();
            mList = null;
        }
    }
}
