package com.guodongbaohe.app.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.lazy_base_fragment.MadRushFragment;
import com.guodongbaohe.app.myutil.MobilePhoneUtil;
import com.guodongbaohe.app.view.MyRadioButton;
import com.kepler.jd.login.KeplerApiManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MadRushActivity extends BigBaseActivity {

    @BindView(R.id.re_title)
    RelativeLayout reTitle;
    @BindView(R.id.radio_one)
    MyRadioButton radioOne;
    @BindView(R.id.radio_two)
    MyRadioButton radioTwo;
    @BindView(R.id.radiogroup)
    RadioGroup radiogroup;
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    private String[] titles = {"精选", "母婴", "美食", "女装", "美妆个护", "内衣", "居家百货", "鞋包装饰", "数码家电", "男装", "运动户外"};
    private List<Fragment> fragmentList;
    MyFragmnetAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.madrushactivity );
        ButterKnife.bind( this );
        int statusBarHeight = MobilePhoneUtil.getStatusBarHeight( getApplicationContext() );
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) reTitle.getLayoutParams();
        layoutParams.setMargins( 0, statusBarHeight, 0, 0 );
        reTitle.setLayoutParams( layoutParams );
        initViewPager();
        initJd();
    }

    private void initJd() {
        boolean keplerLogined = KeplerApiManager.getWebViewService().isKeplerLogined();
    }

    private void initViewPager() {
        fragmentList = new ArrayList<>();
        MadRushFragment fragment;
        Bundle bundle;
        for (int i = 0; i < titles.length; i++) {
            fragment = new MadRushFragment();
            bundle = new Bundle();
            bundle.putString( "title", titles[i] );
            fragment.setArguments( bundle );
            fragmentList.add( fragment );
        }
        adapter = new MyFragmnetAdapter( fragmentList, getSupportFragmentManager() );
        viewpager.setAdapter( adapter );
        tablayout.setupWithViewPager( viewpager );
        viewpager.setCurrentItem( 0 );
        viewpager.setOffscreenPageLimit( fragmentList.size() );
        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab tab = tablayout.getTabAt( i );//获得每一个tab
            tab.setCustomView( R.layout.tablayout_item_new );//给每一个tab设置view
            if (i == 0) {
                // 设置第一个tab的TextView是被选择的样式
                ImageView iv_location = tab.getCustomView().findViewById( R.id.iv_location );
                tab.getCustomView().findViewById( R.id.tab_text ).setSelected( true );//第一个tab被选中
                iv_location.setVisibility( View.VISIBLE );
            }
            TextView textView = tab.getCustomView().findViewById( R.id.tab_text );
            textView.setText( titles[i] );//设置tab上的文字
        }
        tablayout.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById( R.id.tab_text ).setSelected( true );
                tab.getCustomView().findViewById( R.id.iv_location ).setVisibility( View.VISIBLE );
                viewpager.setCurrentItem( tab.getPosition() );
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById( R.id.tab_text ).setSelected( false );
                tab.getCustomView().findViewById( R.id.iv_location ).setVisibility( View.GONE );
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        } );
    }

    private class MyFragmnetAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> fragmentList;

        public MyFragmnetAdapter(List<Fragment> fragmentList, FragmentManager fm) {
            super( fm );
            this.fragmentList = fragmentList;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentList.get( i );
        }

        @Override
        public int getCount() {
            return titles == null ? 0 : titles.length;
        }
    }

}
