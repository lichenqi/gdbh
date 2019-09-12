package com.guodongbaohe.app.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.bean.MadRushBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.lazy_base_fragment.MadRushFragment;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.myutil.MobilePhoneUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.view.MyRadioButton;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MadRushActivity extends BigBaseActivity {
    @BindView(R.id.iv_back)
    ImageView iv_back;
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
    private List<Fragment> fragmentList;
    MyFragmnetAdapter adapter;
    List<MadRushBean.MadRushData> result;
    MadRushFragment fragment;
    Bundle bundle;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.madrushactivity );
        ButterKnife.bind( this );
        int statusBarHeight = MobilePhoneUtil.getStatusBarHeight( getApplicationContext() );
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) reTitle.getLayoutParams();
        layoutParams.setMargins( 0, statusBarHeight, 0, 0 );
        reTitle.setLayoutParams( layoutParams );
        getTitleData();
        initRadioGroupListener();
    }

    public interface OnItem {
        void OnItemClick(String name);
    }

    private OnItem onItem;

    // 绑定接口
    @Override
    public void onAttachFragment(Fragment fragment) {
        try {
            onItem = (OnItem) fragment;
        } catch (Exception e) {
        }
        super.onAttachFragment( fragment );
    }

    private void initRadioGroupListener() {
        radiogroup.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_one:
                        onItem.OnItemClick( "hour" );
                        EventBus.getDefault().post( "hour" );
                        break;
                    case R.id.radio_two:
                        onItem.OnItemClick( "day" );
                        EventBus.getDefault().post( "day" );
                        break;
                }
            }
        } );
    }

    /*标题数据*/
    private void getTitleData() {
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.MADRUSH_TITLE_DATA )
                .tag( this )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( getApplicationContext(), Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( getApplicationContext(), Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( getApplicationContext() ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( getApplicationContext(), "" ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "标题数据", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                MadRushBean bean = GsonUtil.GsonToBean( response.toString(), MadRushBean.class );
                                if (bean == null) return;
                                result = bean.getResult();
                                if (result.size() == 0) return;
                                initViewPager();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {

                    }
                } );
    }

    private void initViewPager() {
        fragmentList = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            fragment = new MadRushFragment();
            bundle = new Bundle();
            bundle.putString( "name", result.get( i ).getCate_name() );
            bundle.putString( "id", result.get( i ).getExtra_id() );
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
            textView.setText( result.get( i ).getCate_name() );//设置tab上的文字
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
            return result.get( position ).getCate_name();
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentList.get( i );
        }

        @Override
        public int getCount() {
            return result == null ? 0 : result.size();
        }
    }

    @OnClick({R.id.iv_back})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

}
