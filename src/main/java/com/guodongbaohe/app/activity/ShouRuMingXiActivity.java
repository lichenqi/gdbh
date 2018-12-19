package com.guodongbaohe.app.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.fragment.MingXiFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ShouRuMingXiActivity extends BaseActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.radiogroup)
    RadioGroup radiogroup;
    @BindView(R.id.radio_one)
    RadioButton radio_one;
    @BindView(R.id.radio_two)
    RadioButton radio_two;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    private List<Fragment> fragments;
    Bundle bundle;

    @Override
    public int getContainerView() {
        return R.layout.shourumingxiactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("收入明细");
        fragments = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            MingXiFragment fragment = new MingXiFragment();
            bundle = new Bundle();
            bundle.putInt("position", i);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        viewpager.setAdapter(new MyOrderAdapter(getSupportFragmentManager()));
        viewpager.addOnPageChangeListener(this);
        radiogroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        getTabState(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.radio_one:
                viewpager.setCurrentItem(0);
                break;
            case R.id.radio_two:
                viewpager.setCurrentItem(1);
                break;
        }
    }

    private void getTabState(int index) {
        radio_one.setChecked(false);
        radio_two.setChecked(false);
        switch (index) {
            case 0:
                radio_one.setChecked(true);
                break;
            case 1:
                radio_two.setChecked(true);
                break;
        }
    }

    private class MyOrderAdapter extends FragmentPagerAdapter {

        public MyOrderAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
