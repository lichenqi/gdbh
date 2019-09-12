package com.guodongbaohe.app.my_test_activity;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.lazy_base_fragment.LazyBaseFragment;

public class MyTablayoutFragment extends LazyBaseFragment {

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.mytablayoutfragment;
    }

    @Override
    protected boolean setFragmentTarget() {
        return true;
    }
}
