package com.guodongbaohe.app.fragment;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.view.MyRadioButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SendCircleFragment extends Fragment implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
    private View view;
    @BindView(R.id.radiogroup)
    RadioGroup radiogroup;
    @BindView(R.id.radio_one)
    MyRadioButton radio_one;
    @BindView(R.id.radio_two)
    RadioButton radio_two;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    private List<Fragment> fragments;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.sendcirclefragment, container, false);
            ButterKnife.bind(this, view);
            fragments = new ArrayList<>();
            fragments.add(new EverydayFaddishFragment());
            fragments.add(new PublicityMaterialFragment());
            viewpager.setAdapter(new MyAdapter(getChildFragmentManager()));
            viewpager.addOnPageChangeListener(this);
            radiogroup.setOnCheckedChangeListener(this);
        }
        return view;
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

    private void getTabState(int index) {
        radio_one.setChecked(false);
        radio_two.setChecked(false);
        switch (index) {
            case 0:
                radio_one.setChecked(true);
                radio_one.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                radio_two.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                break;
            case 1:
                radio_two.setChecked(true);
                radio_two.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                radio_one.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                break;
        }
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

    private class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments == null ? 0 : fragments.size();
        }
    }

    private void initView() {
        Drawable drawable_news = getResources().getDrawable(R.drawable.hexian);
        drawable_news.setBounds(0, 0, 100, 5);//第一0是距左右边距离，第二0是距上下边距离，第三长度,第四宽度
        radio_one.setCompoundDrawables(null, null, null, drawable_news);
    }

}
