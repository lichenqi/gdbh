package com.guodongbaohe.app.custom_view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.guodongbaohe.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestRefreshActivity extends AppCompatActivity {

    @BindView(R.id.refreshlayout)
    RefreshLayout refreshlayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.testrefreshactivity );
        ButterKnife.bind( this );
        refreshlayout.setOnRefresh( new RefreshLayout.OnRefresh() {
            @Override
            public void onDownPullRefresh() {

            }

            @Override
            public void onUpPullRefresh() {

            }
        } );
    }
}
