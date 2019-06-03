package com.guodongbaohe.app.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseLazyLoadFragment extends Fragment {

    protected View rootView;
    private boolean isPrepared;
    protected boolean isVisible;
    private boolean isFirst = true;

    public BaseLazyLoadFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setHasOptionsMenu( true );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = initView( inflater, container );//让子类实现初始化视图
        isPrepared = true;
        layzLoad();
        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated( savedInstanceState );
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint( isVisibleToUser );
        if (getUserVisibleHint()) {
            isVisible = true;
            layzLoad();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /*dosometing*/
    private void onInvisible() {

    }

    protected void layzLoad() {
        if (!isPrepared || !isVisible || !isFirst) {
            return;
        }
        initData();
        isFirst = false;
    }

    protected abstract void initData();

    protected abstract View initView(LayoutInflater inflater, ViewGroup container);

}
