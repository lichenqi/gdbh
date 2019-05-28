package com.guodongbaohe.app.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.HomeListBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CeShiRecyclerviewLoadMoreActivity extends BaseActivity {
    @BindView(R.id.swiperefreshlayout)
    SwipeRefreshLayout swiperefreshlayout;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    List<HomeListBean.ListData> listData;
    List<HomeListBean.ListData> list = new ArrayList<>();
    LoadMoreAdapter adapter;
    int pageNum = 1;
    /*上拉加载是否还有数据  1 代表还有数据  -1代表没有数据了*/
    int data_is_exist = 1;

    @Override
    public int getContainerView() {
        return R.layout.ceshirecyclerviewloadmoreactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        ButterKnife.bind( this );
        setMiddleTitle( "加载更多" );
        recyclerview.setHasFixedSize( true );
        recyclerview.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        getListData();
        initRecyclerData();
        swiperefreshlayout.setColorSchemeColors( Color.parseColor( "#EFA818" ) );
        swiperefreshlayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed( new Runnable() {
                    public void run() {
                        pageNum = 1;
                        data_is_exist = 1;
                        adapter.changeState( data_is_exist );
                        swiperefreshlayout.setRefreshing( false );
                    }
                }, 1500 );
            }
        } );
    }

    private void initRecyclerData() {
        adapter = new LoadMoreAdapter( list );
        recyclerview.setAdapter( adapter );
        recyclerview.addOnScrollListener( new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged( recyclerView, newState );
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //到达底部之后如果footView的状态不是正在加载的状态,就将 他切换成正在加载的状态
                    new Handler().postDelayed( new Runnable() {
                        @Override
                        public void run() {
                            pageNum++;
                            if (data_is_exist == 1) {
                                getListData();
                            }
                            adapter.changeState( data_is_exist );
                        }
                    }, 1500 );
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled( recyclerView, dx, dy );
            }
        } );
    }

    /*列表数据*/
    private void getListData() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( "limit", "12" );
        map.put( "supid", "154" );
        map.put( "page", String.valueOf( pageNum ) );
        String param = ParamUtil.getMapParam( map );
        Log.i( "列表数据参数", Constant.BASE_URL + Constant.SHOP_LIST + "?" + param );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.SHOP_LIST + "?" + param )
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
                        Log.i( "列表数据", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                HomeListBean bean = GsonUtil.GsonToBean( response.toString(), HomeListBean.class );
                                if (bean == null) return;
                                listData = bean.getResult();
                                if (listData.size() > 1) {
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
                                }
                            } else {
                                data_is_exist = -1;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        data_is_exist = -1;
                    }
                } );
    }


    public class LoadMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<HomeListBean.ListData> list;
        private int item_normal = 1;/*正常布局*/
        private int item_foot = -1;/*加载更多脚布局*/
        private int state;

        public void changeState(int state) {
            this.state = state;
            notifyDataSetChanged();
        }

        public LoadMoreAdapter(List<HomeListBean.ListData> list) {
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == item_normal) {
                view = LayoutInflater.from( getApplicationContext() ).inflate( R.layout.load_more_item, parent, false );
                return new LoadMoreHolder( view );
            } else if (viewType == item_foot) {
                view = LayoutInflater.from( getApplicationContext() ).inflate( R.layout.load_more_foot_view, parent, false );
                return new FootHolder( view );
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof LoadMoreHolder) {
                Glide.with( getApplicationContext() ).load( list.get( position ).getGoods_thumb() ).into( ((LoadMoreHolder) holder).iv );
            } else if (holder instanceof FootHolder) {
                switch (state) {
                    case 1:/*还有数据*/
                        ((FootHolder) holder).progressBar.setVisibility( View.VISIBLE );
                        ((FootHolder) holder).tv_foot.setVisibility( View.GONE );
                        break;
                    case -1:/*没有更多数据了*/
                        ((FootHolder) holder).progressBar.setVisibility( View.GONE );
                        ((FootHolder) holder).tv_foot.setVisibility( View.VISIBLE );
                        break;
                }
            }
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position + 1 == getItemCount()) {
                return item_foot;
            } else {
                return item_normal;
            }
        }
    }

    /*正常Holder*/
    public class LoadMoreHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        ImageView iv;

        public LoadMoreHolder(View itemView) {
            super( itemView );
            ButterKnife.bind( this, itemView );
        }
    }

    public class FootHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.progressBar)
        ProgressBar progressBar;
        @BindView(R.id.tv_foot)
        TextView tv_foot;

        public FootHolder(View itemView) {
            super( itemView );
            ButterKnife.bind( this, itemView );
        }
    }

}
