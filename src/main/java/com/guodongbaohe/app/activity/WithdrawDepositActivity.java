package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.fragment.CouponMoneyFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WithdrawDepositActivity extends BaseActivity {
    private String[] titles = {"佣金提现", "津贴提现"};
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    private List<Fragment> fragments;
    private TabFragmentAdapter adapter;
    TextView tv_right_name;

    @Override
    public int getContainerView() {
        return R.layout.withdrawdepositactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("提现");
        setRightTitle("提现明细");
        setRightTVVisible();
        tv_right_name = findViewById(R.id.tv_right_name);
        fragments = new ArrayList<>();
        fragments.add(new CouponMoneyFragment());
        fragments.add(new CouponMoneyFragment());
        adapter = new TabFragmentAdapter(fragments, getSupportFragmentManager());
        viewpager.setAdapter(adapter);
        tablayout.setupWithViewPager(viewpager);
        tv_right_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TiXianRecordActivity.class);
                startActivity(intent);
            }
        });
    }

    public class TabFragmentAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public TabFragmentAdapter(List<Fragment> fragments, FragmentManager fm) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return titles == null ? 0 : titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
