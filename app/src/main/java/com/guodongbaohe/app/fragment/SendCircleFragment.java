package com.guodongbaohe.app.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.common_constant.MyApplication;
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
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = MyApplication.getInstance();
        /*提示用户打开储存权限*/
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //没有存储权限
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

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

}
