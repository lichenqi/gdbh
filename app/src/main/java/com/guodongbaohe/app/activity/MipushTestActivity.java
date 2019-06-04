package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.guodongbaohe.app.MainActivity;
import com.guodongbaohe.app.bean.ShopBasicBean;
import com.guodongbaohe.app.bean.YouMengPushBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.JumpToShopDetailUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

public class MipushTestActivity extends UmengNotifyClickActivity {


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate( bundle );
    }

    @Override
    public void onMessage(Intent intent) {
        super.onMessage( intent );  //此方法必须调用，否则无法统计打开数
        String body = intent.getStringExtra( AgooConstants.MESSAGE_BODY );
        Log.i( "离线消息友盟", body );
        if (TextUtils.isEmpty( body )) return;
        YouMengPushBean bean = GsonUtil.GsonToBean( body, YouMengPushBean.class );
        if (bean == null) return;
        String target = bean.getExtra().getTarget();
        if (target == null) return;
        switch (target) {
            case "money":/*佣金明细*/
                intent = new Intent( getApplicationContext(), ShouRuMingXiActivity.class );
                intent.putExtra( "type", "0" );
                intent.putExtra( Constant.TOMAINTYPE, Constant.TOMAINTYPE );
                startActivity( intent );
                finish();
                break;
            case "credit":/*团队奖金明细*/
                intent = new Intent( getApplicationContext(), ShouRuMingXiActivity.class );
                intent.putExtra( "type", "1" );
                intent.putExtra( Constant.TOMAINTYPE, Constant.TOMAINTYPE );
                startActivity( intent );
                finish();
                break;
            case "market":/*我的团队*/
                intent = new Intent( getApplicationContext(), MyTeamActivity.class );
                intent.putExtra( Constant.TOMAINTYPE, Constant.TOMAINTYPE );
                startActivity( intent );
                finish();
                break;
            case "order":/*我的订单*/
                intent = new Intent( getApplicationContext(), MyOrderActivity.class );
                intent.putExtra( Constant.TOMAINTYPE, Constant.TOMAINTYPE );
                startActivity( intent );
                finish();
                break;
            case "income":/*我的收入*/
                intent = new Intent( getApplicationContext(), MyIncomeingActivity.class );
                intent.putExtra( Constant.TOMAINTYPE, Constant.TOMAINTYPE );
                startActivity( intent );
                finish();
                break;
            case "withdraw":/*提现记录*/
                intent = new Intent( getApplicationContext(), TiXianRecordActivity.class );
                intent.putExtra( Constant.TOMAINTYPE, Constant.TOMAINTYPE );
                startActivity( intent );
                finish();
                break;
            case "goods":/*商品详细*/
                String content = bean.getExtra().getContent();
                if (!TextUtils.isEmpty( content )) {/*商品id*/
                    /*本地商品进商品详情*/
                    getShopBasicData( content );
                }
                break;
            case "share_friend":/*邀请好友*/
                intent = new Intent( getApplicationContext(), YaoQingFriendActivity.class );
                startActivity( intent );
                finish();
                break;
            default:/*其他到主界面*/
                intent = new Intent( getApplicationContext(), MainActivity.class );
                startActivity( intent );
                finish();
                break;
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
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                ShopBasicBean bean = GsonUtil.GsonToBean( response.toString(), ShopBasicBean.class );
                                if (bean == null) return;
                                ShopBasicBean.ShopBasicData result = bean.getResult();
                                JumpToShopDetailUtil.start2ActivityOfHeadBean( getApplicationContext(), result );
                                finish();
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