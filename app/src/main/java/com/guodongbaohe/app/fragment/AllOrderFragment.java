package com.guodongbaohe.app.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.SearchResultActivity;
import com.guodongbaohe.app.adapter.OrderAdaptr;
import com.guodongbaohe.app.bean.OrderBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.ClipContentUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.util.XRecyclerViewUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllOrderFragment extends android.support.v4.app.Fragment {
    private View view;
    String member_id, member_role, son_count;
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    @BindView(R.id.nodata)
    ImageView nodata;
    private int pageNum = 1;
    private List<OrderBean.OrderData> list = new ArrayList<>();
    OrderAdaptr adaptr;
    int position;
    private String itemUrl = "https://item.taobao.com/item.htm?id=";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            position = getArguments().getInt( "position" );
        }
        super.onCreate( savedInstanceState );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate( R.layout.allorderfragment, container, false );
            ButterKnife.bind( this, view );
            member_id = PreferUtils.getString( getContext(), "member_id" );
            member_role = PreferUtils.getString( getContext(), "member_role" );
            son_count = PreferUtils.getString( getContext(), "son_count" );
            getData();
            initView();
        }
        return view;
    }

    private void initView() {
        xrecycler.setHasFixedSize( true );
        xrecycler.setLayoutManager( new LinearLayoutManager( getContext() ) );
        XRecyclerViewUtil.setView( xrecycler );
        adaptr = new OrderAdaptr( getContext(), list, member_role, son_count );
        xrecycler.setAdapter( adaptr );
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
        adaptr.setonfuzhiclicklistener( new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                ClipboardManager cm = (ClipboardManager) getContext().getSystemService( Context.CLIPBOARD_SERVICE );
                ClipData mClipData = ClipData.newPlainText( "Label", list.get( position - 1 ).getTrade_id() );
                cm.setPrimaryClip( mClipData );
                ClipContentUtil.getInstance( getContext() ).putNewSearch( list.get( position - 1 ).getTrade_id() );//保存记录到数据库
                ToastUtils.showToast( getContext(), "订单号复制成功" );
            }
        } );
        adaptr.setonshopdetailclicklistener( new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                /*跳转到商品搜索列表*/
                String num_iid = list.get( position - 1 ).getNum_iid();
                Intent intent = new Intent( getContext(), SearchResultActivity.class );
                intent.putExtra( "keyword", itemUrl + num_iid );
                intent.putExtra( "search_type", 1 );
                startActivityForResult( intent, 1 );
            }
        } );
    }

    private void getData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", member_id );
        map.put( "page", String.valueOf( pageNum ) );
        if (position == 1) {
            map.put( "tk_status", "12,14" );
        } else if (position == 2) {
            map.put( "tk_status", "3" );
        }
        final String param = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( param + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String mapParam = ParamUtil.getMapParam( map );
        Log.i( "订单", Constant.BASE_URL + Constant.MYORDERLIST + "?" + mapParam );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.MYORDERLIST + "?" + mapParam )
                .tag( this )
                .addHeader( "x-userid", member_id )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( getContext(), Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( getContext(), Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( getContext() ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( getContext(), PreferUtils.getString( getContext(), "member_id" ) ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "订单数据", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            int status = jsonObject.getInt( "status" );
                            if (status >= 0) {
                                OrderBean bean = GsonUtil.GsonToBean( response.toString(), OrderBean.class );
                                if (bean == null) return;
                                List<OrderBean.OrderData> result = bean.getResult();
                                if (pageNum == 1) {
                                    if (result.size() == 0) {
                                        nodata.setVisibility( View.VISIBLE );
                                    } else {
                                        nodata.setVisibility( View.GONE );
                                    }
                                    list.clear();
                                    list.addAll( result );
                                    xrecycler.refreshComplete();
                                    adaptr.notifyDataSetChanged();
                                } else {
                                    list.addAll( result );
                                    xrecycler.loadMoreComplete();
                                    adaptr.notifyDataSetChanged();
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
                        nodata.setVisibility( View.VISIBLE );
                        xrecycler.refreshComplete();
                        xrecycler.loadMoreComplete();
                        ToastUtils.showToast( getContext(), Constant.NONET );
                    }
                } );
    }

}
