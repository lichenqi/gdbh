package com.guodongbaohe.app.lazy_base_fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.MadRushActivity;
import com.guodongbaohe.app.adapter.MadRushListAdapter;
import com.guodongbaohe.app.bean.MadRushListBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.JumpToShopDetailUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MadRushFragment extends LazyBaseFragment implements MadRushActivity.OnItem {
    @BindView(R.id.swiperefreshlayout)
    SwipeRefreshLayout swiperefreshlayout;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.to_top)
    ImageView to_top;
    private String name, id, member_role;
    Context context;
    private int pageNum = 1;
    List<MadRushListBean.MadListData> list = new ArrayList<>();
    MadRushListAdapter adapter;
    private String rank = "hour";
    /*上拉加载是否还有数据  1 代表还有数据  -1代表没有数据了；*/
    int data_is_exist = 1;

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister( this );
    }

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case "hour":
                rank = "hour";
                pageNum = 1;
                getListData();
                break;
            case "day":
                rank = "day";
                pageNum = 1;
                getListData();
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            Bundle arguments = getArguments();
            name = arguments.getString( "name" );
            id = arguments.getString( "id" );
        }
        super.onCreate( savedInstanceState );
    }

    @Override
    protected void initData() {
        ButterKnife.bind( this, getView() );
        EventBus.getDefault().register( this );
        context = MyApplication.getInstance();
        member_role = PreferUtils.getString( context, "member_role" );
        recyclerview.setHasFixedSize( true );
        recyclerview.setLayoutManager( new LinearLayoutManager( context ) );
        getListData();
        adapter = new MadRushListAdapter( context, list, member_role );
        recyclerview.setAdapter( adapter );
        recyclerview.addOnScrollListener( new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged( recyclerView, newState );
                boolean top = recyclerView.canScrollVertically( -1 );//值表示是否能向下滚动，false表示已经滚动到顶部
                boolean bottom = recyclerView.canScrollVertically( 1 );//值表示是否能向上滚动，false表示已经滚动到底部
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {/*滑动停止时*/
                    //到达底部之后如果footView的状态不是正在加载的状态,就将 他切换成正在加载的状态
                    new Handler().postDelayed( new Runnable() {
                        @Override
                        public void run() {
                            Log.i( "打印滑动状态", newState + "  " + top + "  " + bottom );
                            if (data_is_exist == 1) {
                                if (top) {
                                    pageNum++;
                                    getListData();
                                }
                            }
                            adapter.changeState( data_is_exist );
                        }
                    }, 1000 );
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled( recyclerView, dx, dy );
                int i = recyclerView.computeVerticalScrollOffset();
                if (dy != 0 && i > 3000) {
                    to_top.setVisibility( View.VISIBLE );
                } else {
                    to_top.setVisibility( View.GONE );
                }
            }
        } );
        swiperefreshlayout.setColorSchemeColors( 0xffffffff );
        swiperefreshlayout.setProgressBackgroundColorSchemeColor( 0xffff3831 );
        swiperefreshlayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed( new Runnable() {
                    @Override
                    public void run() {
                        pageNum = 1;
                        getListData();
                        swiperefreshlayout.setRefreshing( false );
                    }
                }, 1000 );
            }
        } );
        adapter.setOnClickListener( new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                MadRushListBean.MadListData madListData = list.get( position );
                JumpToShopDetailUtil.startToDetailOfMadRushList( context, madListData );
            }
        } );
    }

    /*获取列表数据*/
    private void getListData() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( "limit", "15" );
        map.put( "page", String.valueOf( pageNum ) );
        map.put( "rank", rank );
        map.put( "extra_id", id );
        String param = ParamUtil.getMapParam( map );
        Log.i( "疯抢榜列表参数", Constant.BASE_URL + Constant.MADRUSH_LIST_DATA + "?" + param );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.MADRUSH_LIST_DATA + "?" + param )
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
                        Log.i( "疯抢榜数据", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                MadRushListBean bean = GsonUtil.GsonToBean( response.toString(), MadRushListBean.class );
                                if (bean == null) return;
                                List<MadRushListBean.MadListData> listData = bean.getResult();
                                if (listData.size() > 0) {
                                    data_is_exist = 1;
                                    if (pageNum == 1) {
                                        list.clear();
                                        list.addAll( listData );
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        list.addAll( listData );
                                        adapter.notifyDataSetChanged();
                                    }
                                } else {
                                    data_is_exist = -1;
                                    adapter.changeState( data_is_exist );
                                }
                            } else {
                                data_is_exist = -1;
                                adapter.changeState( data_is_exist );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( context, Constant.NONET );
                        data_is_exist = -1;
                    }
                } );
    }

    @Override
    protected int getLayoutId() {
        return R.layout.madrushfragment;
    }

    @Override
    protected boolean setFragmentTarget() {
        return true;
    }

    @Override
    public void OnItemClick(String name) {
    }

    @OnClick({R.id.to_top})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.to_top:
                recyclerview.scrollToPosition( 0 );
                break;
        }
    }

}
