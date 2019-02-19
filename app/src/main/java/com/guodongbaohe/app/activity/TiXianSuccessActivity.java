package com.guodongbaohe.app.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;

import butterknife.ButterKnife;

public class TiXianSuccessActivity extends BaseActivity {
    @Override
    public int getContainerView() {
        return R.layout.tixiansuccessactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("");
    }
}
