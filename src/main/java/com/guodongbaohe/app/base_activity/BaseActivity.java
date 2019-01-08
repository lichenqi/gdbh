package com.guodongbaohe.app.base_activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.noober.background.BackgroundLibrary;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends BigBaseActivity {
    ImageView iv_back, iv_right;
    TextView tv_title, tv_right_name;
    FrameLayout fl_container;
    RelativeLayout rl_parent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        BackgroundLibrary.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baseactivity);
        rl_parent = (RelativeLayout) findViewById(R.id.rl_parent);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right_name = (TextView) findViewById(R.id.tv_right_name);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        fl_container = (FrameLayout) findViewById(R.id.fl_container);
        View view = getLayoutInflater().inflate(getContainerView(), null);
        fl_container.addView(view);
    }

    public abstract int getContainerView();

    public void setTitleBackgroudColor(int color) {
        rl_parent.setBackgroundColor(color);
    }

    public void setMiddleTitle(String name) {
        tv_title.setText(name);
    }

    public void setMiddleColor(int color) {
        tv_title.setTextColor(color);
    }

    public void setRightTitle(String name) {
        tv_right_name.setText(name);
    }

    public void setRightColor(int color) {
        tv_right_name.setTextColor(color);
    }

    public void onBack(View view) {
        finish();
    }

    public void setRightImageView(int images) {
        iv_right.setImageResource(images);
    }

    public void setLeftImageView(int images) {
        iv_back.setImageResource(images);
    }

    public void setRightIVVisible() {
        iv_right.setVisibility(View.VISIBLE);
    }

    public void setRightIVGone() {
        iv_right.setVisibility(View.GONE);
    }

    public void setRightTVVisible() {
        tv_right_name.setVisibility(View.VISIBLE);
    }

    public void setRightTVGone() {
        tv_right_name.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
