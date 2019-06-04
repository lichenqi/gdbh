package com.guodongbaohe.app.activity;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.GScAdapter;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.AllNetBean;
import com.guodongbaohe.app.bean.GCollectionBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.EncryptUtil;
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
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.guodongbaohe.app.common_constant.MyApplication.getContext;

public class GCollectionActivity extends BaseActivity implements View.OnClickListener, GScAdapter.OnItemClickListener {
    private static final int MYLIVE_MODE_CHECK = 0;
    private static final int MYLIVE_MODE_EDIT = 1;
    ImageView iv_back, nodata;
    LinearLayout rl_bottom, show_no;
    TextView tv_right_name;
    TextView tv_title;
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    private GScAdapter adapter;
    private int mEditMode = MYLIVE_MODE_CHECK;
    //全选
    CheckBox ckAll;
    //删除
    TextView tvSettlement;
    private int pageNum = 1;
    List<GCollectionBean.ResultBean> list = new ArrayList<>();
    private String itemUrl = "https://item.taobao.com/item.htm?id=";

    @Override
    public int getContainerView() {
        return R.layout.activity_gcollection;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister( this );
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        ButterKnife.bind( this );
        EventBus.getDefault().register( this );
        member_id = PreferUtils.getString( getApplicationContext(), "member_id" );
        tv_title = (TextView) findViewById( R.id.tv_title );
        iv_back = (ImageView) findViewById( R.id.iv_back );
        tv_right_name = (TextView) findViewById( R.id.tv_right_name );
        tv_title.setText( "我的收藏" );
        tv_right_name.setText( "编辑" );
        nodata = (ImageView) findViewById( R.id.nodata );
        ckAll = (CheckBox) findViewById( R.id.ck_all );
        tvSettlement = (TextView) findViewById( R.id.login );
        rl_bottom = (LinearLayout) findViewById( R.id.rl_bottom );
        show_no = (LinearLayout) findViewById( R.id.show_no );
        getListData();
        initXrecycler();
        initListener();
        ckAll.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAllMain();
            }
        } );
    }

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case Constant.COLLECT_CHANGE:
                pageNum = 1;
                getListData();
                break;
        }
    }

    private boolean isSelectAll = false;
    private int index = 0;

    /**
     * 全选和反选
     */
    private void selectAllMain() {
        if (adapter == null) return;
        if (!isSelectAll) {
            for (int i = 0, j = list.size(); i < j; i++) {
                list.get( i ).setIschoosed( true );
                id = list.get( i ).getGoods_id();
                list_id.add( id );
            }

            index = list.size();
            tvSettlement.setEnabled( true );
            isSelectAll = true;
        } else {
            for (int i = 0, j = list.size(); i < j; i++) {
                list.get( i ).setIschoosed( false );
                list_id.clear();
            }
            index = 0;
            isSelectAll = false;
        }
        adapter.notifyDataSetChanged();
    }

    Dialog dialog;

    /**
     * 删除逻辑
     */
    private void deleteVideo() {
        if (index == 0) {
            return;
        }
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new Dialog( GCollectionActivity.this, R.style.transparentFrameWindowStyle );
        dialog.setContentView( R.layout.pop_user );
        Window window = dialog.getWindow();
        window.setGravity( Gravity.CENTER | Gravity.CENTER );
        TextView msg = (TextView) dialog.findViewById( R.id.tv_msg );
        TextView sure = (TextView) dialog.findViewById( R.id.btn_sure );
        TextView cancle = (TextView) dialog.findViewById( R.id.btn_cancle );
        if (msg == null || cancle == null || sure == null) return;
        if (index == 1) {
            msg.setText( "删除后不可恢复，是否删除该条目？" );
        } else {
            msg.setText( "删除后不可恢复，是否删除这" + index + "个条目？" );
        }
        cancle.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        } );
        sure.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = list.size(), j = 0; i > j; i--) {
                    GCollectionBean.ResultBean myLive = list.get( i - 1 );
                    if (myLive.isIschoosed()) {
                        list.remove( myLive );
                        index--;
                    }
                }
                if (list.size() == 0) {
                    rl_bottom.setVisibility( View.GONE );
                }
                DeleteData();
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        } );
        dialog.show();
    }

    private void initListener() {
        adapter.setOnItemClickListener( this );
        tvSettlement.setOnClickListener( this );
        tv_right_name.setOnClickListener( this );
    }

    String member_id;

    /*列表数据*/
    private void getListData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", member_id );
        map.put( "page", String.valueOf( pageNum ) );
        map.put( "limit", "14" );
        String param = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( param + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String mapParam = ParamUtil.getMapParam( map );
        Log.i( "列表数据参数", Constant.BASE_URL + Constant.SHOUCANGFY + "?" + mapParam );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.SHOUCANGFY + "?" + mapParam )
                .tag( this )
                .addHeader( "x-userid", member_id )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( getContext(), Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( getContext(), Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( getContext() ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( getApplicationContext(), PreferUtils.getString( getApplicationContext(), "member_id" ) ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "列表数据", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                GCollectionBean bean = GsonUtil.GsonToBean( response.toString(), GCollectionBean.class );
                                if (bean == null) return;
                                List<GCollectionBean.ResultBean> result = bean.getResult();
                                if (pageNum == 1 && result.size() == 0) {
                                    nodata.setVisibility( View.VISIBLE );
                                    tv_right_name.setVisibility( View.GONE );
                                } else {
                                    nodata.setVisibility( View.GONE );
                                    tv_right_name.setVisibility( View.VISIBLE );
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
                        ToastUtils.showToast( getContext(), Constant.NONET );
                        xrecycler.refreshComplete();
                        xrecycler.loadMoreComplete();
                    }
                } );
    }

    private void initXrecycler() {
        xrecycler.setHasFixedSize( true );
        xrecycler.setLayoutManager( new LinearLayoutManager( getApplicationContext() ) );
        XRecyclerViewUtil.setView( xrecycler );
        adapter = new GScAdapter( getApplicationContext(), list );
        xrecycler.setAdapter( adapter );
        xrecycler.setLoadingListener( new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                getListData();
            }

            @Override
            public void onLoadMore() {
                pageNum++;
                getListData();
            }
        } );
    }

    StringBuilder str = new StringBuilder();
    String goods_id;

    /*列表数据*/
    private void DeleteData() {
        for (int i = 0; i < list_id.size(); i++) {
            if (i == list_id.size() - 1)//当循环到最后一个的时候 就不添加逗号,
            {
                str.append( list_id.get( i ) );
            } else {
                str.append( list_id.get( i ) );
                str.append( "," );
            }
        }
        goods_id = str.toString();
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", member_id );
        map.put( "goods_id", goods_id );
        String param = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( param + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String mapParam = ParamUtil.getMapParam( map );
        Log.i( "列表数据参数", Constant.BASE_URL + Constant.CANCEL_COLLECT_SHOP + "?" + mapParam );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.CANCEL_COLLECT_SHOP + "?" + mapParam )
                .tag( this )
                .addHeader( "x-userid", member_id )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( getContext(), Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( getContext(), Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( getContext() ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( getApplicationContext(), PreferUtils.getString( getApplicationContext(), "member_id" ) ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "列表数据", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                pageNum = 1;
                                getListData();
                                EventBus.getDefault().post( Constant.COLLECT_LIST_CHANGE );
                            } else {
                                String result = jsonObject.getString( "result" );
                                ToastUtils.showToast( getApplicationContext(), result );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( getContext(), Constant.NONET );
                    }
                } );
    }

    /*走搜索接口之后跳转到商品详情*/
    private void getShopBasicData(int pos) {
        GCollectionBean.ResultBean result = list.get( pos );
        String goods_id = result.getGoods_id();
        getShopDetail( itemUrl + goods_id );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_right_name:
                updataEditMode();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.login:
                deleteVideo();
                break;
        }

    }

    private void updataEditMode() {
        mEditMode = mEditMode == MYLIVE_MODE_CHECK ? MYLIVE_MODE_EDIT : MYLIVE_MODE_CHECK;
        if (mEditMode == MYLIVE_MODE_EDIT) {
            tv_right_name.setText( "取消" );
            xrecycler.setLoadingMoreEnabled( true );
            xrecycler.setPullRefreshEnabled( false );
            rl_bottom.setVisibility( View.VISIBLE );
            editorStatus = true;
            tvSettlement.setEnabled( true );
        } else {
            ckAll.setChecked( false );
            tv_right_name.setText( "编辑" );
            xrecycler.setPullRefreshEnabled( true );
            isSelectAll = true;
            selectAllMain();
            rl_bottom.setVisibility( View.GONE );
            editorStatus = false;
        }
        adapter.setEditMode( mEditMode );
    }

    private boolean editorStatus = false;
    String id;
    List<String> list_id = new ArrayList<>();

    @Override
    public void onItemClickListener(int pos, List<GCollectionBean.ResultBean> myLiveList) {
        GCollectionBean.ResultBean myLive = myLiveList.get( pos );
        if (editorStatus) {
            id = myLive.getGoods_id();
            boolean isSelect = myLive.isIschoosed();
            if (!isSelect) {
                index++;
                if (list_id.size() > 0) {
                    for (int i = 0; i < list_id.size(); i++) {
                        if (!list_id.get( i ).equals( id )) {
                            list_id.add( id );
                        }
                    }
                } else {
                    list_id.add( id );
                }
                myLive.setIschoosed( true );
            } else {
                myLive.setIschoosed( false );
                index--;
                for (int i = 0; i < list_id.size(); i++) {
                    if (list_id.get( i ).equals( id )) {
                        list_id.remove( i );
                    }
                    ckAll.setChecked( false );
                }
                isSelectAll = false;
            }
            adapter.notifyDataSetChanged();
        } else {
            getShopBasicData( pos );
        }
    }

    Dialog loadingDialog;

    private void getShopDetail(String keyword) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( "sort", "" );
        map.put( "coupon", "false" );
        map.put( "page", "1" );
        map.put( "q", keyword );
        map.put( "type", "1" );
        final String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.GOODS_NEW_SEARCH + "?" + param )
                .tag( this )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( getContext(), Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( getContext(), Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( getContext() ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( getContext(), "" ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "搜索数据", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            int aReturn = jsonObject.getInt( "status" );
                            if (aReturn >= 0) {
                                AllNetBean bean = GsonUtil.GsonToBean( response.toString(), AllNetBean.class );
                                if (bean == null) return;
                                List<AllNetBean.AllNetData> list = bean.getResult();
                                if (list.size() > 0) {
                                    AllNetBean.AllNetData allNetData = list.get( 0 );
                                    JumpToShopDetailUtil.startToDetailOfSearch( getApplicationContext(), allNetData );
                                } else {
                                    ToastUtils.showToast( getContext(), "该商品已下架" );
                                }
                            } else {
                                String result = jsonObject.getString( "result" );
                                ToastUtils.showToast( getApplicationContext(), result );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( getApplicationContext(), Constant.NONET );
                    }
                } );
    }

}
