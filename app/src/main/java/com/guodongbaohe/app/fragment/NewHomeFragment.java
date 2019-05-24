package com.guodongbaohe.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.ExclusiveTutorActivity;
import com.guodongbaohe.app.activity.LoginAndRegisterActivity;
import com.guodongbaohe.app.activity.SearchActivity;
import com.guodongbaohe.app.activity.ShouRuMingXiActivity;
import com.guodongbaohe.app.adapter.HomeChoiceAdapter;
import com.guodongbaohe.app.bean.CommonBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DensityUtils;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.SpUtil;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewHomeFragment extends Fragment {
    private View view;
    @BindView(R.id.re_space_line)
    RelativeLayout re_space_line;
    /*搜索布局*/
    @BindView(R.id.re_search_title)
    RelativeLayout re_search_title;
    @BindView(R.id.re_parent_title)
    RelativeLayout re_parent_title;
    /*搜索点击布局*/
    @BindView(R.id.re_search)
    RelativeLayout re_search;
    @BindView(R.id.re_tablayout_parent)
    RelativeLayout re_tablayout_parent;
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.re_choice)
    RelativeLayout re_choice;
    @BindView(R.id.iv_choice)
    ImageView iv_choice;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.iv_chat)
    ImageView iv_chat;
    @BindView(R.id.iv_liaotian)
    ImageView iv_liaotian;
    private List<Fragment> fragments;
    TabLayoutAdapter adapter;
    Bundle bundle;
    List<CommonBean.CommonResult> titleList;
    PopupWindow popupWindow;
    RecyclerView recyclerview;
    HomeChoiceAdapter homeChoiceAdapter;
    Context context;

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister( this );
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        EventBus.getDefault().register( this );
    }

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case "toFirst":
                viewpager.setCurrentItem( 0 );
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate( R.layout.newhomefragment, container, false );
            ButterKnife.bind( this, view );
            context = MyApplication.getInstance();
            initDataView();
            /*显示折叠搜索框*/
            initChoicePopupwindowView();
        }
        return view;
    }

    private void initDataView() {
        titleList = SpUtil.getList( context, "head_title_list" );
        if (titleList == null) {
            getClassicHeadTitle();
        } else {
            /*tablayout头部赋值操作*/
            setTabLayoutDataView();
        }
    }

    /*tablayout头部赋值操作*/
    private void setTabLayoutDataView() {
        fragments = new ArrayList<>();
        AllFragment allFragment = new AllFragment( re_tablayout_parent, re_search_title, re_parent_title, re_space_line );
        bundle = new Bundle();
        bundle.putString( "cate_id", titleList.get( 0 ).getCate_id() );
        allFragment.setArguments( bundle );
        fragments.add( allFragment );
        for (int i = 1; i < titleList.size(); i++) {
            NewOtherFragment otherFragment = new NewOtherFragment();
            bundle = new Bundle();
            bundle.putInt( "which_position", i );
            bundle.putString( "cate_id", titleList.get( i ).getCate_id() );
            bundle.putString( "label", titleList.get( i ).getLabel() );
            otherFragment.setArguments( bundle );
            fragments.add( otherFragment );
        }
        adapter = new TabLayoutAdapter( fragments, getChildFragmentManager() );
        viewpager.setAdapter( adapter );
        tablayout.setupWithViewPager( viewpager );
        viewpager.setCurrentItem( 0 );
        viewpager.setOffscreenPageLimit( 0 );
        viewpager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (CommonBean.CommonResult bean : titleList) {
                    bean.setChoose( false );
                }
                titleList.get( position ).setChoose( true );
                homeChoiceAdapter.notifyDataSetChanged();
                if (position == 0) {
                    EventBus.getDefault().post( "timeStart" );
                    String currentColor = PreferUtils.getString( context, "currentColor" );
                    if (!TextUtils.isEmpty( currentColor )) {
                        if (currentColor.length() == 7 && currentColor.substring( 0, 1 ).equals( "#" )) {
                            setColor( currentColor );
                        } else {
                            setColor( "#000000" );
                        }
                    } else {
                        setColor( "#000000" );
                    }
                } else {
                    EventBus.getDefault().post( "timeStop" );
                    String s = "#000000";
                    setColor( s );
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        } );
    }

    private class TabLayoutAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public TabLayoutAdapter(List<Fragment> fragments, FragmentManager fm) {
            super( fm );
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get( position );
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get( position ).getName();
        }

        @Override
        public int getCount() {
            return titleList == null ? 0 : titleList.size();
        }
    }

    /*tablayout的头部数据*/
    private void getClassicHeadTitle() {
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.GOODS_CATES )
                .tag( this )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( context, Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( context, Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( context ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( context, PreferUtils.getString( context, "member_id" ) ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            Log.i( "数据啊", response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                CommonBean bean = GsonUtil.GsonToBean( response.toString(), CommonBean.class );
                                titleList = bean.getResult();
                                SpUtil.putList( context, "head_title_list", titleList );
                                /*tablayout头部赋值操作*/
                                setTabLayoutDataView();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( context, Constant.NONET );
                    }
                } );
    }

    /*顶部搜索框和导航狂颜色赋值操作*/
    private void setColor(String color) {
        if (!TextUtils.isEmpty( color )) {
            int i = Color.parseColor( color );
            re_tablayout_parent.setBackgroundColor( i );
            re_search_title.setBackgroundColor( i );
            re_parent_title.setBackgroundColor( i );
            re_space_line.setBackgroundColor( i );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getActivity().getWindow();
                window.addFlags( WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS );
                /*状态栏*/
                window.setStatusBarColor( i );
            }
        }
    }

    /*显示折叠搜索框*/
    private void initChoicePopupwindowView() {
        View view = LayoutInflater.from( context ).inflate( R.layout.choice_popupwindow_view, null );
        RelativeLayout re_all_channel = (RelativeLayout) view.findViewById( R.id.re_all_channel );
        recyclerview = (RecyclerView) view.findViewById( R.id.recyclerview );
        popupWindow = new PopupWindow( view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true );
        recyclerview.setHasFixedSize( true );
        GridLayoutManager manager = new GridLayoutManager( context, 4 );
        recyclerview.setLayoutManager( manager );
        setChoiceData();
        popupWindow.setOnDismissListener( new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha( 1f );
            }
        } );
        re_all_channel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                backgroundAlpha( 1f );
            }
        } );
    }

    private void setChoiceData() {
        titleList = SpUtil.getList( context, "head_title_list" );
        if (titleList == null) {
            getChoiceDataTitle();
        } else {
            initRecyclerviewAdapter();
        }
    }

    /*折叠recyclerview赋值操作*/
    private void initRecyclerviewAdapter() {
        homeChoiceAdapter = new HomeChoiceAdapter( context, titleList );
        recyclerview.setAdapter( homeChoiceAdapter );
        titleList.get( 0 ).setChoose( true );
        homeChoiceAdapter.setOnClickListener( new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                for (CommonBean.CommonResult bean : titleList) {
                    bean.setChoose( false );
                }
                titleList.get( position ).setChoose( true );
                homeChoiceAdapter.notifyDataSetChanged();
                popupWindow.dismiss();
                backgroundAlpha( 1f );
                viewpager.setCurrentItem( position );
            }
        } );
    }

    /*获取折叠分类数据*/
    private void getChoiceDataTitle() {
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.GOODS_CATES )
                .tag( this )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( context, Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( context, Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( context ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( context, PreferUtils.getString( context, "member_id" ) ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            Log.i( "数据啊", response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                CommonBean bean = GsonUtil.GsonToBean( response.toString(), CommonBean.class );
                                titleList = bean.getResult();
                                SpUtil.putList( context, "head_title_list", titleList );
                                initRecyclerviewAdapter();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( context, Constant.NONET );
                    }
                } );
    }

    @OnClick({R.id.iv_chat, R.id.re_choice, R.id.re_search, R.id.iv_liaotian})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.iv_chat:
                if (PreferUtils.getBoolean( context, "isLogin" )) {
                    Intent intent = new Intent( context, ShouRuMingXiActivity.class );
                    intent.putExtra( "type", "0" );
                    intent.putExtra( Constant.TOMAINTYPE, "" );
                    startActivity( intent );
                } else {
                    startActivity( new Intent( context, LoginAndRegisterActivity.class ) );
                }
                break;
            case R.id.re_choice:/*点击折叠按钮*/
                popupWindow.showAsDropDown( re_search_title, 0, DensityUtils.dip2px( getContext(), 10 ) );
                backgroundAlpha( 0.5f );
                break;
            case R.id.re_search:
                startActivity( new Intent( context, SearchActivity.class ) );
                break;
            case R.id.iv_liaotian:
                if (PreferUtils.getBoolean( context, "isLogin" )) {
                    Intent intent = new Intent( context, ExclusiveTutorActivity.class );
                    startActivity( intent );
                } else {
                    startActivity( new Intent( context, LoginAndRegisterActivity.class ) );
                }
                break;
        }
    }

    /*设置添加屏幕的背景透明度*/
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getActivity().getWindow().setAttributes( lp );
    }

}

