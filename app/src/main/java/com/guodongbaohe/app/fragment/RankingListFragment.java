package com.guodongbaohe.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.JhsAdapter;
import com.guodongbaohe.app.bean.HomeListBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.JumpToShopDetailUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.util.XRecyclerViewUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RankingListFragment extends OldLazyLoadFragment {
    private View view;
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    @BindView(R.id.to_top)
    ImageView to_top;
    private int pageNum = 1;
    private List<HomeListBean.ListData> list = new ArrayList<>();
    JhsAdapter adapter;
    private int position;
    Context context;

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case Constant.LOGIN_OUT:
                /*用户退出*/
                xrecycler.refresh();
                break;
            case Constant.LOGINSUCCESS:
                /*登录成功*/
                xrecycler.refresh();
                break;
            case Constant.USER_LEVEL_UPGRADE:
                /*用户等级升级成功*/
                xrecycler.refresh();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister( this );
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register( this );
        if (getArguments() != null) {
            position = getArguments().getInt( "position" );
            context = MyApplication.getInstance();
        }
        super.onCreate( savedInstanceState );
    }

    @Override
    public void onLazyLoad() {
        getData();
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        if (view == null) {
            view = inflater.inflate( R.layout.secondclassicfragment, container, false );
            ButterKnife.bind( this, view );
            context = MyApplication.getInstance();
            to_top.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    xrecycler.scrollToPosition( 0 );
                }
            } );
        }
        return view;
    }

    @Override
    public void initEvent() {
        xrecycler.setHasFixedSize( true );
        xrecycler.setLayoutManager( new LinearLayoutManager( context ) );
        XRecyclerViewUtil.setView( xrecycler );
        adapter = new JhsAdapter( list, context );
        xrecycler.setAdapter( adapter );
        xrecycler.setLoadingListener( new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                getData();
            }

            @Override
            public void onLoadMore() {
                pageNum++;
                getData();
            }
        } );
        adapter.setonclicklistener( new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                int pos = position - 1;
                HomeListBean.ListData listData = list.get( pos );
                JumpToShopDetailUtil.start2ShopDetailOfListBean( context, listData );
            }
        } );
        xrecycler.addOnScrollListener( new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged( recyclerView, newState );
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled( recyclerView, dx, dy );
                int i = recyclerView.computeVerticalScrollOffset();
                if (i > 1200) {
                    to_top.setVisibility( View.VISIBLE );
                } else {
                    to_top.setVisibility( View.GONE );
                }
            }
        } );
    }

    private void getData() {
        HashMap<String, String> map = new HashMap<>();
        map.put( "page", String.valueOf( pageNum ) );
        switch (position) {
            case 0:
                map.put( "rank", "hour" );
                break;
            case 1:
                map.put( "rank", "day" );
                break;
            case 2:
                map.put( "rank", "attr_ratio" );
                break;
            case 3:
                map.put( "rank", "attr_index" );
                break;
        }
        map.put( "limit", "10" );
        final String param = ParamUtil.getMapParam( map );
        Log.i( "商品排行榜", Constant.BASE_URL + Constant.RANKINGLIST + "?" + param );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.RANKINGLIST + "?" + param )
                .tag( this )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( context, Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( context, Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( context ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( context, "" ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "跑行绑数据", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                HomeListBean bean = GsonUtil.GsonToBean( response.toString(), HomeListBean.class );
                                if (bean != null) {
                                    List<HomeListBean.ListData> result = bean.getResult();
                                    boolean isLogin = PreferUtils.getBoolean( context, "isLogin" );
                                    String son_count = PreferUtils.getString( context, "son_count" );
                                    String member_role = PreferUtils.getString( context, "member_role" );
                                    for (HomeListBean.ListData listData : result) {
                                        listData.setLogin( isLogin );
                                        listData.setSon_count( son_count );
                                        listData.setMember_role( member_role );
                                    }
                                    if (pageNum == 1) {
                                        list.clear();
                                        list.addAll( result );
                                        adapter.notifyDataSetChanged();
                                        xrecycler.refreshComplete();
                                    } else {
                                        list.addAll( result );
                                        adapter.notifyDataSetChanged();
                                        xrecycler.loadMoreComplete();
                                    }
                                }
                            } else {
                                xrecycler.refreshComplete();
                                xrecycler.loadMoreComplete();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        xrecycler.refreshComplete();
                        xrecycler.loadMoreComplete();
                        ToastUtils.showToast( context, Constant.NONET );
                    }
                } );
    }
}
