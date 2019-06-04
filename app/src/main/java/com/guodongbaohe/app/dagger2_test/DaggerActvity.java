package com.guodongbaohe.app.dagger2_test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DaggerActvity extends BaseActivity {

    @BindView(R.id.tv_content)
    TextView tv_content;
    @Inject
    Student student;

    @Override
    public int getContainerView() {
        return R.layout.dagger_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        ButterKnife.bind( this );
        setMiddleTitle( "我是dagger" );
        DaggerMainComponent.builder().build().inject( this );
        tv_content.setText( student.showMessage() );
    }
}
