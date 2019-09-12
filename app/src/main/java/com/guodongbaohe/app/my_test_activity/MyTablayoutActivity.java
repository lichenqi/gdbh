package com.guodongbaohe.app.my_test_activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.custom_view.SlidingTabLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyTablayoutActivity extends BaseActivity {

    @BindView(R.id.slidingtablayout)
    SlidingTabLayout slidingtablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    private String[] mTitles = {"李晨奇", "丹丹", "居家百货", "美食", "后端", "设计", "工具资源", "运动户外"};
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    @Override
    public int getContainerView() {
        return R.layout.mytablayoutactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        ButterKnife.bind( this );
        for (int i = 0; i < mTitles.length; i++) {
            MyTablayoutFragment fragment = new MyTablayoutFragment();
            mFragments.add( fragment );
        }
        MyAdapter myAdapter = new MyAdapter( getSupportFragmentManager() );
        viewpager.setAdapter( myAdapter );
        slidingtablayout.setViewPager( viewpager );
    }

    private class MyAdapter extends FragmentStatePagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super( fm );
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments.get( i );
        }

        @Override
        public int getCount() {
            return mTitles == null ? 0 : mTitles.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
    }

}
