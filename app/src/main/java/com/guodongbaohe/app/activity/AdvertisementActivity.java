package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.MainActivity;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.util.NetUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdvertisementActivity extends BigBaseActivity {
    @BindView(R.id.re_parent)
    RelativeLayout re_parent;
    @BindView(R.id.iv_advertisement)
    ImageView iv_advertisement;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.tv_notice)
    TextView tv_notice;
    private TimeCount countdownTime = new TimeCount( 4000, 1000 );
    String advertise_img, advertise_url;
    Intent intent;
    boolean isFirstOfAdvertise;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.advertisementactivity );
        ButterKnife.bind( this );
        isFirstOfAdvertise = PreferUtils.getBoolean( getApplicationContext(), "isFirstOfAdvertise" );
        intent = getIntent();
        advertise_img = intent.getStringExtra( "advertise_img" );
        advertise_url = intent.getStringExtra( "advertise_url" );
        Glide.with( getApplicationContext() ).load( advertise_img ).into( iv_advertisement );
        countdownTime.start();
        time.getBackground().setAlpha( 200 );
        tv_notice.getBackground().setAlpha( 200 );
        time.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainActivity();
            }
        } );
        re_parent.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent( getApplicationContext(), AdvertisementOfWebViewActivity.class );
                intent.putExtra( "url", advertise_url );
                startActivity( intent );
                finish();
                if (countdownTime != null) {
                    countdownTime.cancel();
                }
            }
        } );
    }

    private void toMainActivity() {
        if (NetUtil.getNetWorkState( AdvertisementActivity.this ) < 0) {
            ToastUtils.showToast( getApplicationContext(), "您的网络异常，请联网重试" );
            return;
        }
        if (!isFirstOfAdvertise) {
            intent = new Intent( getApplicationContext(), GuideActivity.class );
            startActivity( intent );
            PreferUtils.putBoolean( getApplicationContext(), "isFirstOfAdvertise", true );
        } else {
            intent = new Intent( getApplicationContext(), MainActivity.class );
            startActivity( intent );
        }
        if (countdownTime != null) {
            countdownTime.cancel();
        }
        finish();
    }

    private class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super( millisInFuture, countDownInterval );
        }

        @Override
        public void onTick(long millisUntilFinished) {
            time.setText( "跳过" + millisUntilFinished / 1000 + "s" );
        }

        @Override
        public void onFinish() {
            toMainActivity();
        }
    }

    @Override
    public void onNetChange(int netMobile) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countdownTime != null) {
            countdownTime.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (countdownTime != null) {
            countdownTime.cancel();
        }
    }

}
