package com.guodongbaohe.app.lazy_base_fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.guodongbaohe.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MadRushFragment extends LazyBaseFragment {

    @BindView(R.id.tv)
    TextView tv;
    private String title;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            Bundle arguments = getArguments();
            title = arguments.getString( "title" );
        }
        super.onCreate( savedInstanceState );
    }

    @Override
    protected void initData() {
        ButterKnife.bind( this, getView() );
        tv.setText( title );
    }

    @Override
    protected int getLayoutId() {
        return R.layout.madrushfragment;
    }

    @Override
    protected boolean setFragmentTarget() {
        return true;
    }

}
