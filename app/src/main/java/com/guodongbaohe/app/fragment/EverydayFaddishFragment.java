package com.guodongbaohe.app.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.LoginAndRegisterActivity;
import com.guodongbaohe.app.activity.ShopDetailActivity;
import com.guodongbaohe.app.activity.VideoPlayActivity;
import com.guodongbaohe.app.adapter.EverydayfaddishAdapter;
import com.guodongbaohe.app.bean.EverydayHostGoodsBean;
import com.guodongbaohe.app.bean.GaoYongJinBean;
import com.guodongbaohe.app.bean.ShopBasicBean;
import com.guodongbaohe.app.bean.TemplateBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.dialogfragment.BaseNiceDialog;
import com.guodongbaohe.app.dialogfragment.NiceDialog;
import com.guodongbaohe.app.dialogfragment.ViewConvertListener;
import com.guodongbaohe.app.dialogfragment.ViewHolder;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.BitmapShareManager;
import com.guodongbaohe.app.util.ClipContentUtil;
import com.guodongbaohe.app.util.CommonUtil;
import com.guodongbaohe.app.util.DensityUtils;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.IconAndTextGroupUtil;
import com.guodongbaohe.app.util.NumUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.QRCodeUtil;
import com.guodongbaohe.app.util.ShareManager;
import com.guodongbaohe.app.util.StringCleanZeroUtil;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.Tools;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.util.XRecyclerViewUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/*每日爆款*/
public class EverydayFaddishFragment extends Fragment {
    private View view;
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    @BindView(R.id.to_top)
    ImageView to_top;
    List<EverydayHostGoodsBean.GoodsList> list = new ArrayList<>();
    private int pageNum = 1;
    EverydayfaddishAdapter adapter;
    int which_position;
    String goods_gallery;
    private List<String> list_share_imgs;
    IWXAPI iwxapi;
    /*单张图片分享字段*/
    private final int ONLYPHOTO = 1;
    /*多张图片分享字段*/
    private final int MOREPHOTO = 2;
    Context context;
    /*淘口令标识*/
    private String taokouling_sign = "{淘口令}";
    /*下单链接*/
    private String order_sign = "{下单链接}";
    ClipboardManager cm;
    String content_taobao_eight;
    List<String> goodIdList;
    TextView tv_content;
    int screen_width, screen_height;

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister( this );
        if (notice_dialog != null) {
            notice_dialog.dismiss();
        }
    }

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate( R.layout.everydayfaddishfragment, container, false );
            ButterKnife.bind( this, view );
            EventBus.getDefault().register( this );
            context = MyApplication.getInstance();
            iwxapi = WXAPIFactory.createWXAPI( context, Constant.WCHATAPPID, true );
            iwxapi.registerApp( Constant.WCHATAPPID );
            cm = (ClipboardManager) context.getSystemService( Context.CLIPBOARD_SERVICE );
            getData();
            getTemplateData();
            initNoticeDialog();
            getScreenSize();
            xrecycler.setHasFixedSize( true );
            xrecycler.setLayoutManager( new LinearLayoutManager( context ) );
            XRecyclerViewUtil.setView( xrecycler );
            adapter = new EverydayfaddishAdapter( list, getActivity() );
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
            /*分享点击*/
            adapter.onShareClickListener( new OnItemClick() {
                @Override
                public void OnItemClickListener(View view, int position) {
                    if (PreferUtils.getBoolean( context, "isLogin" )) {
                        which_position = position - 1;
                        goods_gallery = list.get( which_position ).getGoods_gallery();
                        String status = list.get( which_position ).getStatus();
                        String flag = list.get( which_position ).getFlag();
                        String content = list.get( which_position ).getContent();

                        ClipData mClipData = ClipData.newPlainText( "Label", content );
                        cm.setPrimaryClip( mClipData );
                        ClipContentUtil.getInstance( context ).putNewSearch( content );//保存记录到数据库
                        ToastUtils.showToast( context, "文案内容已复制成功" );

                        /*好货专场*/
                        if (flag.equals( "goods_sub" )) {
                            String goods_id_list = list.get( which_position ).getGoods_id_list();
                            String[] good_id_old = goods_id_list.replace( "||", "," ).split( "," );
                            goodIdList = new ArrayList<>();
                            for (int i = 0; i < good_id_old.length; i++) {
                                if (!good_id_old[i].equals( "0" )) {
                                    goodIdList.add( good_id_old[i] );
                                }
                            }
                            eachbitmapList = new ArrayList<>();
                            /*获取每个商品对应的一些信息*/
                            loadingDialog = DialogUtil.createLoadingDialog( getContext(), "加载中..." );
                            for (int i = 0; i < goodIdList.size(); i++) {
                                getEachShopData( goodIdList.get( i ) );
                            }

                        } else {
                            /*普通图片*/
                            if (TextUtils.isEmpty( status ) || status.equals( "0" )) {
                                ToastUtils.showToast( context, "该商品抢光呢!" );
                                return;
                            }
                            if (goods_gallery.contains( "||" )) {
                                /*多张图片用原生分享*/
                                String[] imgs = goods_gallery.replace( "||", "," ).split( "," );
                                list_share_imgs = new ArrayList<>();
                                for (int i = 0; i < imgs.length; i++) {
                                    list_share_imgs.add( imgs[i] );
                                }
                                morePicsShareDialog();
                            } else {
                                /*单张图片用sharesdk分享*/
                                showShare( list.get( which_position ).getGoods_id() );
                            }
                        }

                    } else {
                        startActivity( new Intent( context, LoginAndRegisterActivity.class ) );
                    }
                }
            } );
            /*复制评论点击*/
            adapter.setonFuZhiListener( new OnItemClick() {
                @Override
                public void OnItemClickListener(View view, int position) {
                    if (PreferUtils.getBoolean( context, "isLogin" )) {
                        content_taobao_eight = PreferUtils.getString( context, "taokouling_content" );
                        which_position = position - 1;
                        String status = list.get( which_position ).getStatus();
                        if (TextUtils.isEmpty( status ) || status.equals( "0" )) {
                            ToastUtils.showToast( context, "该商品抢光呢!" );
                            return;
                        }
                        getTaoKouLin( position - 1 );
                    } else {
                        startActivity( new Intent( context, LoginAndRegisterActivity.class ) );
                    }
                }
            } );
            /*用户评论内容复制点击*/
            adapter.setOnUserFuZhiClickListener( new OnItemClick() {
                @Override
                public void OnItemClickListener(View view, int position) {
                    String goods_comment = list.get( position - 1 ).getGoods_comment();
                    ClipData mClipData = ClipData.newPlainText( "Label", goods_comment );
                    cm.setPrimaryClip( mClipData );
                    ToastUtils.showCenterToast( context, "评论复制成功" );
                    ClipContentUtil.getInstance( context ).putNewSearch( goods_comment );//保存记录到数据库
                }
            } );
            /*独立的视频点击播放*/
            adapter.setOnVideoClickListener( new OnItemClick() {
                @Override
                public void OnItemClickListener(View view, int position) {
                    String video = list.get( position - 1 ).getVideo();
                    Intent intent = new Intent( context, VideoPlayActivity.class );
                    intent.putExtra( "url", video );
                    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                    startActivity( intent );
                }
            } );
            /*整个点击*/
            adapter.setAllItemClickListener( new OnItemClick() {
                @Override
                public void OnItemClickListener(View view, int position) {
                    int pos = position - 1;
                    String status = list.get( pos ).getStatus();
                    if (TextUtils.isEmpty( status ) || status.equals( "0" )) {
                        ToastUtils.showToast( context, "该商品抢光呢!" );
                        return;
                    }
                    getShopBasicData( pos );
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
                    if (i > 3000) {
                        to_top.setVisibility( View.VISIBLE );
                    } else {
                        to_top.setVisibility( View.GONE );
                    }
                }
            } );
            to_top.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    xrecycler.scrollToPosition( 0 );
                }
            } );
            adapter.setonLongClickListener( new EverydayfaddishAdapter.OnLongClick() {
                @Override
                public void OnLongClickListener(View view, int position) {
                    String content = list.get( position - 1 ).getContent();
                    String status = list.get( position - 1 ).getStatus();
                    if (!TextUtils.isEmpty( status ) && status.equals( "0" )) {
                        ToastUtils.showToast( context, "该商品抢光呢!" );
                        return;
                    }
                    ClipData mClipData = ClipData.newPlainText( "Label", content );
                    cm.setPrimaryClip( mClipData );
                    ToastUtils.showToast( context, "复制成功" );
                    ClipContentUtil.getInstance( context ).putNewSearch( content );//保存记录到数据库
                }
            } );
        }
        return view;
    }

    /*获取屏幕的宽高*/
    private void getScreenSize() {
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics( metric );
        screen_width = metric.widthPixels;
        screen_height = metric.heightPixels;
    }

    /*小提示弹框*/
    private void initNoticeDialog() {
        notice_dialog = new Dialog( getContext(), R.style.transparentFrameWindowStyle );
        notice_dialog.setContentView( R.layout.my_backgroud_toast );
        Window window = notice_dialog.getWindow();
        window.setGravity( Gravity.CENTER | Gravity.CENTER );
        tv_content = notice_dialog.findViewById( R.id.tv_content );
    }

    /*商品详情头部信息*/
    private void getShopBasicData(int pos) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( "goods_id", list.get( pos ).getGoods_id() );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.SHOP_HEAD_BASIC + "?" + param )
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
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                ShopBasicBean bean = GsonUtil.GsonToBean( response.toString(), ShopBasicBean.class );
                                if (bean == null) return;
                                ShopBasicBean.ShopBasicData result = bean.getResult();
                                Intent intent = new Intent( context, ShopDetailActivity.class );
                                intent.putExtra( "goods_id", result.getGoods_id() );
                                intent.putExtra( "cate_route", result.getCate_route() );/*类目名称*/
                                intent.putExtra( "cate_category", result.getCate_category() );/*类目id*/
                                intent.putExtra( "attr_price", result.getAttr_price() );
                                intent.putExtra( "attr_prime", result.getAttr_prime() );
                                intent.putExtra( "attr_ratio", result.getAttr_ratio() );
                                intent.putExtra( "sales_month", result.getSales_month() );
                                intent.putExtra( "goods_name", result.getGoods_name() );/*长标题*/
                                intent.putExtra( "goods_short", result.getGoods_short() );/*短标题*/
                                intent.putExtra( "seller_shop", result.getSeller_shop() );/*店铺姓名*/
                                intent.putExtra( "goods_thumb", result.getGoods_thumb() );/*单图*/
                                intent.putExtra( "goods_gallery", result.getGoods_gallery() );/*多图*/
                                intent.putExtra( "coupon_begin", result.getCoupon_begin() );/*开始时间*/
                                intent.putExtra( "coupon_final", result.getCoupon_final() );/*结束时间*/
                                intent.putExtra( "coupon_surplus", result.getCoupon_surplus() );/*是否有券*/
                                intent.putExtra( "coupon_explain", result.getGoods_slogan() );/*推荐理由*/
                                intent.putExtra( "attr_site", result.getAttr_site() );/*天猫或者淘宝*/
                                intent.putExtra( "coupon_total", result.getCoupon_total() );
                                intent.putExtra( "coupon_id", result.getCoupon_id() );
                                intent.putExtra( Constant.SHOP_REFERER, "circle" );/*商品来源*/
                                intent.putExtra( Constant.GAOYONGJIN_SOURCE, result.getSource() );/*高佣金来源*/
                                startActivity( intent );
                            } else {
                                String result = jsonObject.getString( "result" );
                                ToastUtils.showToast( context, result );
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

    /*生成淘口令接口*/
    private void getTaoKouLin(int i) {
        /*高佣金接口*/
        getGaoYongJinData( i );
    }

    Dialog loadingDialog;

    private void getGaoYongJinData(int pos) {
        loadingDialog = DialogUtil.createLoadingDialog( getContext(), "加载..." );
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", PreferUtils.getString( context, "member_id" ) );
        map.put( "goods_id", list.get( pos ).getGoods_id() );
        map.put( Constant.SHOP_REFERER, "circle" );
        if (!TextUtils.isEmpty( list.get( pos ).getSource() )) {
            map.put( Constant.GAOYONGJIN_SOURCE, list.get( pos ).getSource() );
        }
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.GAOYONGIN + "?" + param )
                .addHeader( "x-userid", PreferUtils.getString( context, "member_id" ) )
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
                            if (jsonObject.getInt( "status" ) >= 0) {
                                GaoYongJinBean bean = GsonUtil.GsonToBean( response.toString(), GaoYongJinBean.class );
                                if (bean == null) return;
                                String coupon_click_url = bean.getResult().getCoupon_click_url();
                                shenchengTaoKouLing( coupon_click_url );
                            } else {
                                ClipData mClipData = ClipData.newPlainText( "Label", list.get( which_position ).getContent() );
                                cm.setPrimaryClip( mClipData );
                                ClipContentUtil.getInstance( context ).putNewSearch( list.get( which_position ).getContent() );//保存记录到数据库
                                showWeiXinDialog();
                                DialogUtil.closeDialog( loadingDialog, getContext() );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( context, Constant.NONET );
                        DialogUtil.closeDialog( loadingDialog, getContext() );
                    }
                } );
    }

    String start_tkl, end_tkl, tkl_content;

    /*获取淘口令*/
    private void shenchengTaoKouLing(String coupon_click_url) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", PreferUtils.getString( context, "member_id" ) );
        map.put( "url", coupon_click_url );
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.GETTAOKOULING + "?" + param )
                .tag( this )
                .addHeader( "x-userid", PreferUtils.getString( context, "member_id" ) )
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
                        DialogUtil.closeDialog( loadingDialog, getContext() );
                        Log.i( "淘口令", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                String taokouling = jsonObject.getString( "result" );
                                if (!TextUtils.isEmpty( content_taobao_eight )) {
                                    tkl_content = content_taobao_eight.replace( taokouling_sign, taokouling ).replace( order_sign, "" ).trim();
                                } else {
                                    if (!TextUtils.isEmpty( taokouling_muban )) {
                                        tkl_content = taokouling_muban.replace( taokouling_sign, taokouling ).replace( order_sign, "" );
                                    } else {
                                        tkl_content = "复制这条评论信息" + taokouling + "，打开【手机Taobao】即可查看";
                                    }
                                }
                                ClipData mClipData = ClipData.newPlainText( "Label", tkl_content );
                                cm.setPrimaryClip( mClipData );
                                ClipContentUtil.getInstance( context ).putNewSearch( tkl_content );//保存记录到数据库
                                showWeiXinDialog();
                            } else {
                                ClipData mClipData = ClipData.newPlainText( "Label", list.get( which_position ).getContent() );
                                cm.setPrimaryClip( mClipData );
                                ClipContentUtil.getInstance( context ).putNewSearch( list.get( which_position ).getContent() );//保存记录到数据库
                                showWeiXinDialog();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        DialogUtil.closeDialog( loadingDialog, getContext() );
                        ToastUtils.showToast( context, Constant.NONET );
                    }
                } );
    }

    /*去微信弹框粘贴框*/
    Dialog dialog;

    private void showWeiXinDialog() {
        dialog = new Dialog( getContext(), R.style.transparentFrameWindowStyle );
        dialog.setContentView( R.layout.open_weixin );
        Window window = dialog.getWindow();
        window.setGravity( Gravity.CENTER | Gravity.CENTER );
        TextView sure = (TextView) dialog.findViewById( R.id.sure );
        TextView cancel = (TextView) dialog.findViewById( R.id.cancel );
        sure.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        } );
        cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (!iwxapi.isWXAppInstalled()) {
                    ToastUtils.showToast( context, "请先安装微信APP" );
                } else {
                    Intent intent = new Intent( Intent.ACTION_MAIN );
                    ComponentName cmp = new ComponentName( "com.tencent.mm", "com.tencent.mm.ui.LauncherUI" );
                    intent.addCategory( Intent.CATEGORY_LAUNCHER );
                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                    intent.setComponent( cmp );
                    startActivity( intent );
                }
            }
        } );
        dialog.show();
    }

    private void getData() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( "page", String.valueOf( pageNum ) );
        map.put( "type", "goods" );
        map.put( "limit", "5" );
        String param = ParamUtil.getMapParam( map );
        Log.i( "每日爆款地址", Constant.BASE_URL + Constant.EVERYDAY_NEW_API + "?" + param );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.EVERYDAY_NEW_API + "?" + param )
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
                        Log.i( "每日爆款", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                EverydayHostGoodsBean bean = GsonUtil.GsonToBean( response.toString(), EverydayHostGoodsBean.class );
                                if (bean == null) return;
                                List<EverydayHostGoodsBean.GoodsList> list_result = bean.getResult();
                                if (list_result.size() == 0) {
                                    xrecycler.setNoMore( true );
                                    xrecycler.refreshComplete();
                                    xrecycler.loadMoreComplete();
                                } else {
                                    boolean isLogin = PreferUtils.getBoolean( context, "isLogin" );
                                    String son_count = PreferUtils.getString( context, "son_count" );
                                    String member_role = PreferUtils.getString( context, "member_role" );
                                    for (EverydayHostGoodsBean.GoodsList listData : list_result) {
                                        listData.setLogin( isLogin );
                                        listData.setSon_count( son_count );
                                        listData.setMember_role( member_role );
                                    }
                                    if (pageNum == 1) {
                                        list.clear();
                                        list.addAll( list_result );
                                        adapter.notifyDataSetChanged();
                                        xrecycler.refreshComplete();
                                    } else {
                                        list.addAll( list_result );
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
                        ToastUtils.showToast( getActivity(), Constant.NONET );
                    }
                } );
    }

    View share_view;
    /*中间的大图*/
    ImageView p_iv;
    /*标题*/
    TextView p_title;
    /*销量*/
    TextView p_sale_num;
    /*原价*/
    TextView p_old_price;
    /*券类型 1*/
    TextView tv_coupon_type;
    /*多少元券*/
    TextView tv_coupon_money;
    /*优惠券类型*/
    TextView tv_jia_type;
    /*券后价*/
    TextView p_coupon_price;
    /*二维码图片*/
    ImageView iv_qr_code;

    /*单张图片生成二维码组图样式*/
    private void showShare(String shopId) {
        loadingDialog = DialogUtil.createLoadingDialog( getContext(), "加载中..." );
        share_view = LayoutInflater.from( context ).inflate( R.layout.creation_second_share_poster, null );
        p_iv = share_view.findViewById( R.id.p_iv );
        p_title = share_view.findViewById( R.id.p_title );
        p_sale_num = share_view.findViewById( R.id.p_sale_num );
        p_old_price = share_view.findViewById( R.id.p_old_price );
        tv_coupon_type = share_view.findViewById( R.id.tv_coupon_type );
        tv_coupon_money = share_view.findViewById( R.id.tv_coupon_money );
        tv_jia_type = share_view.findViewById( R.id.tv_jia_type );
        p_coupon_price = share_view.findViewById( R.id.p_coupon_price );
        iv_qr_code = share_view.findViewById( R.id.iv_qr_code );
        shareGetShopDetailData( shopId );
    }

    String goods_thumb, attr_price, attr_prime, goods_short, goods_name, goods_id;
    BigDecimal bg3;
    double v;

    private void shareGetShopDetailData(String shopId) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( "goods_id", shopId );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.SHOP_HEAD_BASIC + "?" + param )
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
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                ShopBasicBean bean = GsonUtil.GsonToBean( response.toString(), ShopBasicBean.class );
                                if (bean == null) return;
                                ShopBasicBean.ShopBasicData result = bean.getResult();
                                goods_thumb = result.getGoods_thumb();
                                attr_price = result.getAttr_price();
                                attr_prime = result.getAttr_prime();
                                goods_short = result.getGoods_short();
                                goods_name = result.getGoods_name();
                                goods_id = result.getGoods_id();
                                String coupon_surplus = result.getCoupon_surplus();
                                String sale_num = result.getSales_month();
                                String attr_site = result.getAttr_site();
                                v = Double.valueOf( attr_prime ) - Double.valueOf( attr_price );
                                if (Double.valueOf( coupon_surplus ) > 0) {
                                    tv_coupon_type.setText( "券" );
                                    tv_jia_type.setText( "券后价 ¥ " );
                                    double d_price = Double.valueOf( attr_prime ) - Double.valueOf( attr_price );
                                    bg3 = new BigDecimal( d_price );
                                    double d_money = bg3.setScale( 2, BigDecimal.ROUND_HALF_UP ).doubleValue();
                                    tv_coupon_money.setText( StringCleanZeroUtil.DoubleFormat( d_money ) + "元券" );
                                } else {
                                    if (v > 0) {
                                        tv_coupon_type.setText( "折" );
                                        tv_jia_type.setText( "折后价 ¥ " );
                                        double disaccount = Double.valueOf( attr_price ) / Double.valueOf( attr_prime ) * 10;
                                        bg3 = new BigDecimal( disaccount );
                                        double d_zhe = bg3.setScale( 1, BigDecimal.ROUND_HALF_UP ).doubleValue();
                                        tv_coupon_money.setText( d_zhe + "折" );
                                    } else {
                                        tv_coupon_type.setText( "特" );
                                        tv_jia_type.setText( "特惠价 ¥ " );
                                        tv_coupon_money.setText( "立即抢购" );
                                    }
                                }
                                p_sale_num.setText( "销量: " + NumUtil.getNum( sale_num ) );
                                IconAndTextGroupUtil.setTextView( context, p_title, goods_name, attr_site );
                                p_coupon_price.setText( StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                                p_old_price.setText( " ¥" + StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                                p_old_price.getPaint().setFlags( Paint.STRIKE_THRU_TEXT_FLAG ); //中间横线
                                p_old_price.getPaint().setAntiAlias( true );// 抗锯齿
                                /*转高勇接口*/
                                shareGaoYongJiekou();
                            } else {
                                DialogUtil.closeDialog( loadingDialog, getContext() );
                                String result = jsonObject.getString( "result" );
                                ToastUtils.showToast( context, result );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( context, Constant.NONET );
                        DialogUtil.closeDialog( loadingDialog, getContext() );
                    }
                } );
    }

    String coupon_url;

    private void shareGaoYongJiekou() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", PreferUtils.getString( context, "member_id" ) );
        map.put( "goods_id", list.get( which_position ).getGoods_id() );
        map.put( Constant.SHOP_REFERER, "circle" );
        if (!TextUtils.isEmpty( list.get( which_position ).getSource() )) {
            map.put( Constant.GAOYONGJIN_SOURCE, list.get( which_position ).getSource() );
        }
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.GAOYONGIN + "?" + param )
                .addHeader( "x-userid", PreferUtils.getString( context, "member_id" ) )
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
                        Log.i( "分享高勇", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                GaoYongJinBean bean = GsonUtil.GsonToBean( response.toString(), GaoYongJinBean.class );
                                if (bean == null) return;
                                String coupon_remain_count = bean.getResult().getCoupon_remain_count();
                                if (!TextUtils.isEmpty( coupon_remain_count ) && Double.valueOf( coupon_remain_count ) > 0) {
                                    coupon_url = bean.getResult().getCoupon_click_url();
                                } else {
                                    coupon_url = bean.getResult().getItem_url();
                                }
                                shareToukouLing( coupon_url );
                            } else {
                                DialogUtil.closeDialog( loadingDialog, getContext() );
                                String result = jsonObject.getString( "result" );
                                ToastUtils.showToast( context, result );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( context, Constant.NONET );
                        DialogUtil.closeDialog( loadingDialog, getContext() );
                    }
                } );
    }

    /*分享需要的淘口令接口*/
    private void shareToukouLing(String content) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", PreferUtils.getString( context, "member_id" ) );
        map.put( "url", content );
        map.put( "goods_id", goods_id );
        map.put( "attr_prime", attr_prime );
        map.put( "attr_price", attr_price );
        map.put( "text", goods_name );
        map.put( "logo", goods_thumb );
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.GETTAOKOULING + "?" + param )
                .tag( this )
                .addHeader( "x-userid", PreferUtils.getString( context, "member_id" ) )
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
                        DialogUtil.closeDialog( loadingDialog, getContext() );
                        Log.i( "淘口令", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                String taokouling = jsonObject.getString( "result" );
                                getQrcode( taokouling );
                            } else {
                                String result = jsonObject.getString( "result" );
                                DialogUtil.closeDialog( loadingDialog, getContext() );
                                ToastUtils.showToast( context, result );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        DialogUtil.closeDialog( loadingDialog, getContext() );
                        ToastUtils.showToast( context, Constant.NONET );
                    }
                } );
    }

    private void getQrcode(String taokl) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "type", "goods" );
        map.put( "words", taokl );
        map.put( "attr_prime", attr_prime );
        map.put( "attr_price", attr_price );
        map.put( "platform", "android" );
        map.put( "member_id", PreferUtils.getString( context, "member_id" ) );
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.ERWEIMAA + "?" + param )
                .tag( this )
                .addHeader( "x-userid", PreferUtils.getString( context, "member_id" ) )
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
                        DialogUtil.closeDialog( loadingDialog, getContext() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                String result = jsonObject.getString( "result" );
                                Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap( result, DensityUtils.dip2px( context, 100 ) );
                                iv_qr_code.setImageBitmap( mBitmap );
                                Glide.with( context ).load( goods_thumb ).asBitmap().into( target );
                            } else {
                                String result = jsonObject.getString( "result" );
                                ToastUtils.showToast( context, result );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( context, Constant.NONET );
                        DialogUtil.closeDialog( loadingDialog, getContext() );
                    }
                } );
    }

    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            p_iv.setImageBitmap( bitmap );
            viewSaveToImage( share_view );
        }
    };

    /*看不见的view转化成bitmap*/
    public Bitmap createBitmapOfNew(View v, int width, int height) {
        int viewWidth = DensityUtils.dip2px( context, 350 );
        //测量使得view指定大小
        int measuredWidth = View.MeasureSpec.makeMeasureSpec( viewWidth, View.MeasureSpec.EXACTLY );
        int measuredHeight = View.MeasureSpec.makeMeasureSpec( height, View.MeasureSpec.AT_MOST );
        v.measure( measuredWidth, measuredHeight );
        //调用layout方法布局后，可以得到view的尺寸大小
        v.layout( 0, 0, viewWidth, v.getMeasuredHeight() );
        Bitmap bmp = Bitmap.createBitmap( viewWidth, v.getHeight(), Bitmap.Config.ARGB_8888 );
        Canvas c = new Canvas( bmp );
        c.drawColor( Color.WHITE );
        v.draw( c );
        return bmp;
    }

    Bitmap hebingBitmap;

    private void viewSaveToImage(View view) {
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics( metric );
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        hebingBitmap = createBitmapOfNew( view, width, height );
        DialogUtil.closeDialog( loadingDialog, getContext() );
        /*先开存储权限*/
        if (ContextCompat.checkSelfPermission( context, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission( context, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            //没有存储权限
            ActivityCompat.requestPermissions( getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, ONLYPHOTO );
        } else {
            /*自定义九宫格样式*/
            customShareStyle();
        }
    }

    private void saveData() {
        HashMap<String, String> map = new HashMap<>();
        map.put( "gid", list.get( which_position ).getId() );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.SHARE_NUM + "?" + param )
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
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            int status = jsonObject.getInt( "status" );
                            if (status >= 0) {
                                xrecycler.refresh();
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

    /*普通多张图片用原生分享*/
    private void morePicsShareDialog() {
        if (ContextCompat.checkSelfPermission( context, Manifest.permission.READ_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission( context, Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED) {
            //没有存储权限
            ActivityCompat.requestPermissions( getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MOREPHOTO );
        } else {
            /*多图分享弹框*/
            duoTuShareDialog();
        }
    }

    /*多图分享弹框*/
    private void duoTuShareDialog() {
        NiceDialog.init().setLayoutId( R.layout.morepicssharedialog )
                .setConvertListener( new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        holder.setOnClickListener( R.id.tv_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        } );
                        RelativeLayout re_wchat_friend = holder.getView( R.id.re_wchat_friend );
                        RelativeLayout re_wchat_circle = holder.getView( R.id.re_wchat_circle );
                        RelativeLayout re_qq_friend = holder.getView( R.id.re_qq_friend );
                        RelativeLayout re_qq_space = holder.getView( R.id.re_qq_space );
                        re_wchat_friend.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*微信好友分享*/
                                dialog.dismiss();
                                sharePics( 0, "wchat" );
                            }
                        } );
                        re_wchat_circle.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*微信朋友圈分享*/
                                dialog.dismiss();
                                /*由于微信机制不让分享多图直接到微信朋友圈*/
                                WChatCircleDialog();
                                /*保存多张图片到朋友圈*/
                                saveMorePhotoToLocal();
                            }
                        } );
                        re_qq_friend.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*qq好友*/
                                dialog.dismiss();
                                sharePics( 0, "qq" );
                            }
                        } );
                        re_qq_space.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*保存图片*/
                                dialog.dismiss();
                                saveMorePhotoToLocal();
                            }
                        } );
                    }
                } )
                .setShowBottom( true )
                .setOutCancel( true )
                .setAnimStyle( R.style.EnterExitAnimation )
                .show( getFragmentManager() );
    }

    private void sharePics(int i, String type) {
        ShareManager shareManager = new ShareManager( getContext() );
        shareManager.setShareImage( hebingBitmap, i, list_share_imgs, "", type, 100 );
    }

    /*单张图片生成组图分享*/
    private void customShareStyle() {
        NiceDialog.init().setLayoutId( R.layout.custom_share_style )
                .setConvertListener( new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        holder.setOnClickListener( R.id.tv_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        } );
                        LinearLayout wcaht_friend = holder.getView( R.id.wcaht_friend );
                        LinearLayout wchat_circle = holder.getView( R.id.wchat_circle );
                        LinearLayout qq_friend = holder.getView( R.id.qq_friend );
                        LinearLayout save_img = holder.getView( R.id.save_img );
                        wcaht_friend.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                wchatFriendShare();
                            }
                        } );
                        wchat_circle.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                wchatFriendCircle();
                            }
                        } );
                        qq_friend.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                qqFriend();
                            }
                        } );
                        save_img.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                CommonUtil.saveBitmap2file( hebingBitmap, context );
                            }
                        } );
                    }
                } )
                .setShowBottom( true )
                .setOutCancel( true )
                .setAnimStyle( R.style.EnterExitAnimation )
                .show( getFragmentManager() );
    }

    /*微信好友分享*/
    private void wchatFriendShare() {
        Wechat.ShareParams sp = new Wechat.ShareParams();
        sp.setImageData( hebingBitmap );
        sp.setShareType( Platform.SHARE_IMAGE );
        Platform wechat = ShareSDK.getPlatform( Wechat.NAME );
        wechat.setPlatformActionListener( new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                /*保存在后台*/
                saveData();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
            }

            @Override
            public void onCancel(Platform platform, int i) {
            }
        } );
        wechat.share( sp );
    }

    /*微信朋友圈分享*/
    private void wchatFriendCircle() {
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        sp.setImageData( hebingBitmap );
        sp.setShareType( Platform.SHARE_IMAGE );
        Platform weChat = ShareSDK.getPlatform( WechatMoments.NAME );
        weChat.setPlatformActionListener( new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                /*保存在后台*/
                saveData();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
            }

            @Override
            public void onCancel(Platform platform, int i) {
            }
        } );
        weChat.share( sp );
    }

    /*qq好友*/
    private void qqFriend() {
        QQ.ShareParams sp = new QQ.ShareParams();
        sp.setImageData( hebingBitmap );
        sp.setShareType( Platform.SHARE_IMAGE );
        Platform qq = ShareSDK.getPlatform( QQ.NAME );
        qq.setPlatformActionListener( new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                /*保存在后台*/
                saveData();
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.i( "qq好友", i + " " );
            }

            @Override
            public void onCancel(Platform platform, int i) {
            }
        } );
        qq.share( sp );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MOREPHOTO:
                /*多张图片存储权限回调*/
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast( context, "分享多张图片需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置" );
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast( context, "分享多张图片需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置" );
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    duoTuShareDialog();
                }
                break;
            case ONLYPHOTO:
                /*单张图片分享回调*/
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast( context, "分享图片需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置" );
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast( context, "分享图片需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置" );
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    /*自定义九宫格样式*/
                    customShareStyle();
                }
                break;
        }
    }

    String taokouling_muban;

    /*获取模板数据*/
    private void getTemplateData() {
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.SHARE_MOBAN )
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
                        DialogUtil.closeDialog( loadingDialog, getContext() );
                        Log.i( "打印模板看看", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                TemplateBean bean = GsonUtil.GsonToBean( response.toString(), TemplateBean.class );
                                if (bean == null) return;
                                taokouling_muban = bean.getResult().getComment();
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

    /*保存图片*/
    private void saveMorePhotoToLocal() {
        /*网络路劲存储*/
        new Thread( new Runnable() {
            @Override
            public void run() {
                URL imageurl;
                try {
                    for (int i = 0; i < list_share_imgs.size(); i++) {
                        imageurl = new URL( list_share_imgs.get( i ) );
                        HttpURLConnection conn = (HttpURLConnection) imageurl.openConnection();
                        conn.setDoInput( true );
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream( is );
                        is.close();
                        Message msg = new Message();
                        // 把bm存入消息中,发送到主线程
                        msg.obj = bitmap;
                        handler.sendMessage( msg );
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } ).start();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            CommonUtil.saveBitmap2file( bitmap, context );
        }
    };

    /*多图分享到微信时弹框到微信app*/
    private void WChatCircleDialog() {
        dialog = new Dialog( getContext(), R.style.transparentFrameWindowStyle );
        dialog.setContentView( R.layout.wchatcircle_dialog_pics );
        Window window = dialog.getWindow();
        window.setGravity( Gravity.CENTER | Gravity.CENTER );
        TextView dismiss = (TextView) dialog.findViewById( R.id.dismiss );
        dismiss.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        } );
        TextView open_wx = (TextView) dialog.findViewById( R.id.open_wx );
        open_wx.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Tools.isAppAvilible( getContext(), "com.tencent.mm" )) {
                    ToastUtils.showToast( getContext(), "您还没有安装微信客户端,请先安转客户端" );
                    return;
                }
                Intent intent = new Intent( Intent.ACTION_MAIN );
                ComponentName cmp = new ComponentName( "com.tencent.mm", "com.tencent.mm.ui.LauncherUI" );
                intent.addCategory( Intent.CATEGORY_LAUNCHER );
                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                intent.setComponent( cmp );
                startActivity( intent );
                dialog.dismiss();
            }
        } );
        dialog.show();
    }

    private void getEachShopData(String shopId) {
        getEachIdData( shopId );
    }

    private void getEachIdData(String shopId) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( "goods_id", shopId );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.SHOP_HEAD_BASIC + "?" + param )
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
                        Log.i( "商品详情数据", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                ShopBasicBean bean = GsonUtil.GsonToBean( response.toString(), ShopBasicBean.class );
                                if (bean == null) return;
                                ShopBasicBean.ShopBasicData result = bean.getResult();
                                /*转高佣接口*/
                                getEachGaoYongData( result );
                            } else {
                                DialogUtil.closeDialog( loadingDialog, getContext() );
                                String result = jsonObject.getString( "result" );
                                ToastUtils.showToast( context, result );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( context, Constant.NONET );
                        DialogUtil.closeDialog( loadingDialog, getContext() );
                    }
                } );

    }

    /*高拥接口*/
    private void getEachGaoYongData(ShopBasicBean.ShopBasicData result) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", PreferUtils.getString( context, "member_id" ) );
        map.put( "goods_id", result.getGoods_id() );
        map.put( Constant.SHOP_REFERER, "circle" );
        if (!TextUtils.isEmpty( result.getSource() )) {
            map.put( Constant.GAOYONGJIN_SOURCE, result.getSource() );
        }
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.GAOYONGIN + "?" + param )
                .addHeader( "x-userid", PreferUtils.getString( context, "member_id" ) )
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
                        Log.i( "分享高勇", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                GaoYongJinBean bean = GsonUtil.GsonToBean( response.toString(), GaoYongJinBean.class );
                                if (bean == null) return;
                                String coupon_remain_count = bean.getResult().getCoupon_remain_count();
                                if (!TextUtils.isEmpty( coupon_remain_count ) && Double.valueOf( coupon_remain_count ) > 0) {
                                    coupon_url = bean.getResult().getCoupon_click_url();
                                } else {
                                    coupon_url = bean.getResult().getItem_url();
                                }
                                getEachTaoKouLingData( coupon_url, result );
                            } else {
                                DialogUtil.closeDialog( loadingDialog, getContext() );
                                String result = jsonObject.getString( "result" );
                                ToastUtils.showToast( context, result );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( context, Constant.NONET );
                        DialogUtil.closeDialog( loadingDialog, getContext() );
                    }
                } );
    }

    private void getEachTaoKouLingData(String coupon_url, ShopBasicBean.ShopBasicData result) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", PreferUtils.getString( context, "member_id" ) );
        map.put( "url", coupon_url );
        map.put( "goods_id", result.getGoods_id() );
        map.put( "attr_prime", result.getAttr_prime() );
        map.put( "attr_price", result.getAttr_price() );
        map.put( "text", result.getGoods_name() );
        map.put( "logo", result.getGoods_thumb() );
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.GETTAOKOULING + "?" + param )
                .tag( this )
                .addHeader( "x-userid", PreferUtils.getString( context, "member_id" ) )
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
                        Log.i( "淘口令", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                String taokouling = jsonObject.getString( "result" );
                                getEachQrcode( taokouling, result );
                            } else {
                                String result = jsonObject.getString( "result" );
                                DialogUtil.closeDialog( loadingDialog, getContext() );
                                ToastUtils.showToast( context, result );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        DialogUtil.closeDialog( loadingDialog, getContext() );
                        ToastUtils.showToast( context, Constant.NONET );
                    }
                } );
    }

    private void getEachQrcode(String taokouling, ShopBasicBean.ShopBasicData result) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "type", "goods" );
        map.put( "words", taokouling );
        map.put( "attr_prime", result.getAttr_prime() );
        map.put( "attr_price", result.getAttr_price() );
        map.put( "platform", "android" );
        map.put( "member_id", PreferUtils.getString( context, "member_id" ) );
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.ERWEIMAA + "?" + param )
                .tag( this )
                .addHeader( "x-userid", PreferUtils.getString( context, "member_id" ) )
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
                        Log.i( "淘口令结果啊", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                String result_qrcode = jsonObject.getString( "result" );
                                /*这里开始进行布局赋值*/
                                View each_share_view = LayoutInflater.from( context ).inflate( R.layout.creation_second_share_poster, null );
                                ImageView each_p_iv = each_share_view.findViewById( R.id.p_iv );
                                TextView p_title = each_share_view.findViewById( R.id.p_title );
                                TextView p_sale_num = each_share_view.findViewById( R.id.p_sale_num );
                                TextView p_old_price = each_share_view.findViewById( R.id.p_old_price );
                                TextView tv_coupon_type = each_share_view.findViewById( R.id.tv_coupon_type );
                                TextView tv_coupon_money = each_share_view.findViewById( R.id.tv_coupon_money );
                                TextView tv_jia_type = each_share_view.findViewById( R.id.tv_jia_type );
                                TextView p_coupon_price = each_share_view.findViewById( R.id.p_coupon_price );
                                ImageView iv_qr_code = each_share_view.findViewById( R.id.iv_qr_code );
                                v = Double.valueOf( result.getAttr_prime() ) - Double.valueOf( result.getAttr_price() );
                                if (Double.valueOf( result.getCoupon_surplus() ) > 0) {
                                    tv_coupon_type.setText( "券" );
                                    tv_jia_type.setText( "券后价 ¥ " );
                                    double d_price = Double.valueOf( result.getAttr_prime() ) - Double.valueOf( result.getAttr_price() );
                                    bg3 = new BigDecimal( d_price );
                                    double d_money = bg3.setScale( 2, BigDecimal.ROUND_HALF_UP ).doubleValue();
                                    tv_coupon_money.setText( StringCleanZeroUtil.DoubleFormat( d_money ) + "元券" );
                                } else {
                                    if (v > 0) {
                                        tv_coupon_type.setText( "折" );
                                        tv_jia_type.setText( "折后价 ¥ " );
                                        double disaccount = Double.valueOf( result.getAttr_price() ) / Double.valueOf( result.getAttr_prime() ) * 10;
                                        bg3 = new BigDecimal( disaccount );
                                        double d_zhe = bg3.setScale( 1, BigDecimal.ROUND_HALF_UP ).doubleValue();
                                        tv_coupon_money.setText( d_zhe + "折" );
                                    } else {
                                        tv_coupon_type.setText( "特" );
                                        tv_jia_type.setText( "特惠价 ¥ " );
                                        tv_coupon_money.setText( "立即抢购" );
                                    }
                                }
                                p_sale_num.setText( "销量: " + NumUtil.getNum( result.getSales_month() ) );
                                IconAndTextGroupUtil.setTextView( context, p_title, result.getGoods_name(), result.getAttr_site() );
                                p_coupon_price.setText( StringCleanZeroUtil.DoubleFormat( Double.valueOf( result.getAttr_price() ) ) );
                                p_old_price.setText( " ¥" + StringCleanZeroUtil.DoubleFormat( Double.valueOf( result.getAttr_prime() ) ) );
                                p_old_price.getPaint().setFlags( Paint.STRIKE_THRU_TEXT_FLAG ); //中间横线
                                p_old_price.getPaint().setAntiAlias( true );// 抗锯齿
                                Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap( result_qrcode, DensityUtils.dip2px( context, 100 ) );
                                iv_qr_code.setImageBitmap( mBitmap );
                                String goods_thumb = result.getGoods_thumb();
                                if (goods_thumb.contains( "alicdn" ) || goods_thumb.contains( "tbcdn" ) || goods_thumb.contains( "taobaocdn" )) {
                                    goods_thumb = goods_thumb + "_400x400.jpg";
                                }
                                Glide.with( context ).load( goods_thumb ).asBitmap().into( new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        each_p_iv.setImageBitmap( resource );
                                        viewSaveToImageOfEach( each_share_view );
                                    }
                                } );
                            } else {
                                String result = jsonObject.getString( "result" );
                                ToastUtils.showToast( context, result );
                                DialogUtil.closeDialog( loadingDialog, getContext() );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( context, Constant.NONET );
                        DialogUtil.closeDialog( loadingDialog, getContext() );
                    }
                } );
    }

    Bitmap each_hebingBitmap;
    List<Bitmap> eachbitmapList;
    Dialog notice_dialog;

    private void viewSaveToImageOfEach(View view) {
        each_hebingBitmap = createBitmapOfNew( view, screen_width, screen_height );
        eachbitmapList.add( each_hebingBitmap );
        DialogUtil.closeDialog( loadingDialog, getContext() );
        tv_content.setText( " 正在生成第" + eachbitmapList.size() + "张图片" );
        if (!notice_dialog.isShowing()) {
            notice_dialog.show();
        }
        if (goodIdList.size() == eachbitmapList.size()) {
            notice_dialog.dismiss();
            /*弹出分享窗口*/
            showEachDialogShow();
        }
    }

    private void showEachDialogShow() {
        NiceDialog.init().setLayoutId( R.layout.morepicssharedialog )
                .setConvertListener( new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        holder.setOnClickListener( R.id.tv_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        } );
                        RelativeLayout re_wchat_friend = holder.getView( R.id.re_wchat_friend );
                        RelativeLayout re_wchat_circle = holder.getView( R.id.re_wchat_circle );
                        RelativeLayout re_qq_friend = holder.getView( R.id.re_qq_friend );
                        RelativeLayout re_qq_space = holder.getView( R.id.re_qq_space );
                        re_wchat_friend.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*微信好友分享*/
                                dialog.dismiss();
                                sharePicsOfEach( 0, "wchat" );
                            }
                        } );
                        re_wchat_circle.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*微信朋友圈分享*/
                                dialog.dismiss();
                                /*由于微信机制不让分享多图直接到微信朋友圈*/
                                WChatCircleDialog();
                                /*保存多张图片到朋友圈*/
                                for (int i = 0; i < eachbitmapList.size(); i++) {
                                    CommonUtil.saveBitmap2file( eachbitmapList.get( i ), context );
                                }
                            }
                        } );
                        re_qq_friend.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*qq好友*/
                                dialog.dismiss();
                                sharePicsOfEach( 0, "qq" );
                            }
                        } );
                        re_qq_space.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*保存图片*/
                                dialog.dismiss();
                                for (int i = 0; i < eachbitmapList.size(); i++) {
                                    CommonUtil.saveBitmap2file( eachbitmapList.get( i ), context );
                                }
                                ToastUtils.showBackgroudCenterToast( context, "图片已保存至手机图库" );
                            }
                        } );
                    }
                } )
                .setShowBottom( true )
                .setOutCancel( true )
                .setAnimStyle( R.style.EnterExitAnimation )
                .show( getFragmentManager() );
    }

    private void sharePicsOfEach(int i, String type) {
        BitmapShareManager shareManager = new BitmapShareManager( getContext() );
        shareManager.setShareImage( eachbitmapList, i, type );
    }

}
