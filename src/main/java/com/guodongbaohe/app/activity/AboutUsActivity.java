package com.guodongbaohe.app.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.util.VersionUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.version)
    TextView version;

    @Override
    public int getContainerView() {
        return R.layout.aboutusactivity;
    }

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("关于我们");
        String versionCode = VersionUtil.getVersionCode(getApplicationContext());
        version.setText("版本号: V" + versionCode);
    }
}
