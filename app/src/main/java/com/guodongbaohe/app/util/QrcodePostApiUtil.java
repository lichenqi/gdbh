package com.guodongbaohe.app.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.GaoYongJinBean;
import com.guodongbaohe.app.bean.ShopBasicBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

public class QrcodePostApiUtil {
    Activity activity;
    Context context;
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
    LinkedHashMap<String, String> map;
    double v;
    BigDecimal bg3;
    String goods_thumb, attr_price, attr_prime, goods_short, goods_name, goods_id, coupon_surplus, sale_num, attr_site, source;

    public void getShopBasicData(Activity activity, Context context, String shopId) {
        this.activity = activity;
        this.context = context;
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
        /*获取 商品信息*/
        map = new LinkedHashMap<>();
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
                                coupon_surplus = result.getCoupon_surplus();
                                sale_num = result.getSales_month();
                                attr_site = result.getAttr_site();
                                source = result.getSource();
                                v = Double.valueOf( attr_prime ) - Double.valueOf( attr_price );
                                if (Double.valueOf( coupon_surplus ) > 0) {
                                    tv_coupon_type.setText( "券" );
                                    tv_jia_type.setText( "券后价" );
                                    double d_price = Double.valueOf( attr_prime ) - Double.valueOf( attr_price );
                                    bg3 = new BigDecimal( d_price );
                                    double d_money = bg3.setScale( 2, BigDecimal.ROUND_HALF_UP ).doubleValue();
                                    tv_coupon_money.setText( "¥ " + StringCleanZeroUtil.DoubleFormat( d_money ) );
                                } else {
                                    if (v > 0) {
                                        tv_coupon_type.setText( "折" );
                                        tv_jia_type.setText( "折后价" );
                                        double disaccount = Double.valueOf( attr_price ) / Double.valueOf( attr_prime ) * 10;
                                        bg3 = new BigDecimal( disaccount );
                                        double d_zhe = bg3.setScale( 1, BigDecimal.ROUND_HALF_UP ).doubleValue();
                                        tv_coupon_money.setText( d_zhe + "折" );
                                    } else {
                                        tv_coupon_type.setText( "特" );
                                        tv_jia_type.setText( "特惠价" );
                                        tv_coupon_money.setText( "立即抢购" );
                                    }
                                }
                                p_sale_num.setText( "销量: " + NumUtil.getNum( sale_num ) );
                                IconAndTextGroupUtil.setTextView( context, p_title, goods_name, attr_site );
                                p_coupon_price.setText( " ¥" + StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_price ) ) );
                                p_old_price.setText( " ¥" + StringCleanZeroUtil.DoubleFormat( Double.valueOf( attr_prime ) ) );
                                p_old_price.getPaint().setFlags( Paint.STRIKE_THRU_TEXT_FLAG ); //中间横线
                                p_old_price.getPaint().setAntiAlias( true );// 抗锯齿
                                /*转高勇接口*/
                                shareGaoYongJiekou();
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

    String coupon_url;

    private void shareGaoYongJiekou() {
        long timelineStr = System.currentTimeMillis() / 1000;
        map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", PreferUtils.getString( context, "member_id" ) );
        map.put( "goods_id", goods_id );
        map.put( Constant.SHOP_REFERER, "circle" );
        if (!TextUtils.isEmpty( source )) {
            map.put( Constant.GAOYONGJIN_SOURCE, source );
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
                        Log.i( "淘口令", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                String taokouling = jsonObject.getString( "result" );
                                getQrcode( taokouling );
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

    Bitmap hebingBitmap;

    private void viewSaveToImage(View view) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics( metric );
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        hebingBitmap = createBitmapOfNew( view, width, height );
    }

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
}
