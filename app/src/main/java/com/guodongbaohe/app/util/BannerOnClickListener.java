package com.guodongbaohe.app.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.guodongbaohe.app.activity.BaseH5Activity;
import com.guodongbaohe.app.activity.LoginAndRegisterActivity;
import com.guodongbaohe.app.activity.TaoBaoAndTianMaoUrlActivity;
import com.guodongbaohe.app.activity.TaobaoTianMaoHolidayOfActivity;
import com.guodongbaohe.app.activity.XinShouJiaoChengActivity;
import com.guodongbaohe.app.bean.BannerDataBean;
import com.guodongbaohe.app.bean.ShopBasicBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.gridview.ZeroPointsGoodsActivity;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.youth.banner.listener.OnBannerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;

public class BannerOnClickListener implements OnBannerListener {
    private Context context;
    private List<BannerDataBean.BannerList> banner_result;
    Intent intent;

    public BannerOnClickListener(Context context, List<BannerDataBean.BannerList> banner_result) {
        this.context = context;
        this.banner_result = banner_result;
    }

    @Override
    public void OnBannerClick(int position) {
        if (PreferUtils.getBoolean( context, "isLogin" )) {
            String url = banner_result.get( position ).getUrl();/*跳转地址*/
            String type = banner_result.get( position ).getType();/*跳转类型*/
            String extend = banner_result.get( position ).getExtend();/*轮播图标题*/
            if (!TextUtils.isEmpty( type )) {
                switch (type) {
                    case "normal":
                        /*普通链接地址*/
                        intent = new Intent( context, BaseH5Activity.class );
                        intent.putExtra( "url", url );
                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                        context.startActivity( intent );
                        break;
                    case "tmall":
                        /*淘宝天猫会场地址*/
                        intent = new Intent( context, TaoBaoAndTianMaoUrlActivity.class );
                        intent.putExtra( "url", url );
                        intent.putExtra( "title", extend );
                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                        context.startActivity( intent );
                        break;
                    case "local_goods":
                        /*本地商品进商品详情*/
                        getShopBasicData( url );
                        break;
                    case "xinshou":
                        /*新手教程主题*/
                        intent = new Intent( context, XinShouJiaoChengActivity.class );
                        intent.putExtra( "url", url );
                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                        context.startActivity( intent );
                        break;
                    case "app_theme":
                        /*app主题*/
                        intent = new Intent( context, BaseH5Activity.class );
                        intent.putExtra( "url", url );
                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                        context.startActivity( intent );
                        break;
                    case "taobao_no_coupon":/*淘宝天猫不需要一键查询*/
                        intent = new Intent( context, TaobaoTianMaoHolidayOfActivity.class );
                        intent.putExtra( "url", url );
                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                        context.startActivity( intent );
                        break;
                    case "ldms":/*0点秒杀*/
                        intent = new Intent( context, ZeroPointsGoodsActivity.class );
                        intent.putExtra( "goods_type", "ldms" );
                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                        context.startActivity( intent );
                        break;
                    case "gysp":/*高佣金商品*/
                        intent = new Intent( context, ZeroPointsGoodsActivity.class );
                        intent.putExtra( "goods_type", "gysp" );
                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                        context.startActivity( intent );
                        break;
                }
            }
        } else {
            intent = new Intent( context, LoginAndRegisterActivity.class );
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            context.startActivity( intent );
        }
    }

    /*商品详情头部信息*/
    private void getShopBasicData(String shopId) {
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
                                JumpToShopDetailUtil.start2ActivityOfHeadBean( context, result );
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
}
