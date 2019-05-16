package com.guodongbaohe.app.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.guodongbaohe.app.activity.PhoneLoginActivity;
import com.guodongbaohe.app.bean.BaseUserBean;
import com.guodongbaohe.app.bean.WchatLoginBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.EmjoyAndTeShuUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import cn.sharesdk.wechat.utils.WXAppExtendObject;
import cn.sharesdk.wechat.utils.WXMediaMessage;
import cn.sharesdk.wechat.utils.WechatHandlerActivity;

public class WXEntryActivity extends WechatHandlerActivity implements IWXAPIEventHandler {
    IWXAPI iwxapi;

    /**
     * 处理微信发出的向第三方应用请求app message
     * <p>
     * 在微信客户端中的聊天页面有“添加工具”，可以将本应用的图标添加到其中
     * 此后点击图标，下面的代码会被执行。Demo仅仅只是打开自己而已，但你可
     * 做点其他的事情，包括根本不打开任何页面
     */
    public void onGetMessageFromWXReq(WXMediaMessage msg) {
        if (msg != null) {
            Intent iLaunchMyself = getPackageManager().getLaunchIntentForPackage( getPackageName() );
            startActivity( iLaunchMyself );
        }
    }

    /**
     * 处理微信向第三方应用发起的消息
     * <p>
     * 此处用来接收从微信发送过来的消息，比方说本demo在wechatpage里面分享
     * 应用时可以不分享应用文件，而分享一段应用的自定义信息。接受方的微信
     * 客户端会通过这个方法，将这个信息发送回接收方手机上的本demo中，当作
     * 回调。
     * <p>
     * 本Demo只是将信息展示出来，但你可做点其他的事情，而不仅仅只是Toast
     */
    public void onShowMessageFromWXReq(WXMediaMessage msg) {
        if (msg != null && msg.mediaObject != null
                && (msg.mediaObject instanceof WXAppExtendObject)) {
            WXAppExtendObject obj = (WXAppExtendObject) msg.mediaObject;
        }
    }

    int num;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        //接收到分享以及登录的intent传递handleIntent方法，处理结果
        iwxapi = WXAPIFactory.createWXAPI( this, Constant.WCHATAPPID, false );
        iwxapi.handleIntent( getIntent(), this );
        num = (int) ((Math.random() * 9 + 1) * 10000000);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        //登录回调
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                String code = ((SendAuth.Resp) baseResp).code;
                //获取用户信息
                int type = baseResp.getType();
                Log.i( "微信返回值", code + "  " + type );
                wchatLoginData( code );
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED://用户拒绝授权
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL://用户取消
                finish();
                break;
            default:
                finish();
                break;
        }
    }

    private void wchatLoginData(String code) {
        final LinkedHashMap<String, String> map = new LinkedHashMap<>();
        long timelineStr = System.currentTimeMillis() / 1000;
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "code", code );
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        final String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.WCHATLOGIN + "?" + param )
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
                        Log.i( "后台返回值微信", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            int status = jsonObject.getInt( "status" );
                            if (status >= 0) {
                                /*微信登录*/
                                String member_id = jsonObject.getString( "result" );
                                getConfigurationData( member_id );
                            } else {
                                String special = jsonObject.getString( "special" );
                                if (!TextUtils.isEmpty( special ) && special.equals( "wechat" )) {
                                    WchatLoginBean wchatLoginBean = GsonUtil.GsonToBean( response.toString(), WchatLoginBean.class );
                                    if (wchatLoginBean != null) {
                                        WchatLoginBean.WchatLoginResult result = wchatLoginBean.getResult();
                                        String avatar = result.getAvatar();/*微信头像*/
                                        String member_name = result.getMember_name();/*微信昵称*/
                                        String city = result.getCity();
                                        String openid = result.getOpenid();
                                        String unionid = result.getUnionid();
                                        String province = result.getProvince();
                                        String gender = result.getGender();
                                        Intent intent = new Intent( getApplicationContext(), PhoneLoginActivity.class );
                                        intent.putExtra( "avatar", avatar );
                                        intent.putExtra( "member_name", member_name );
                                        intent.putExtra( "city", city );
                                        intent.putExtra( "openid", openid );
                                        intent.putExtra( "unionid", unionid );
                                        intent.putExtra( "province", province );
                                        intent.putExtra( "gender", gender );
                                        startActivity( intent );
                                        finish();
                                    }
                                } else {
                                    ToastUtils.showToast( getApplicationContext(), Constant.NONET );
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( getApplicationContext(), Constant.NONET );
                        finish();
                    }
                } );
    }

    /*获取用户信息*/
    private void getConfigurationData(String member_id) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", member_id );
        map.put( "field", Constant.USER_DATA_PARA );
        String param = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( param + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String mapParam = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.USER_BASIC_INFO + "?" + mapParam )
                .tag( this )
                .addHeader( "x-userid", member_id )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( getApplicationContext(), Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( getApplicationContext(), Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( getApplicationContext() ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( getApplicationContext(), member_id ) )
                .addHeader( "x-userid", member_id )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "用户信息", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            int status = jsonObject.getInt( "status" );
                            if (status >= 0) {
                                BaseUserBean bean = GsonUtil.GsonToBean( response.toString(), BaseUserBean.class );
                                if (bean == null) return;
                                BaseUserBean.BaseUserData result = bean.getResult();
                                String avatar = result.getAvatar();/*用户头像*/
                                String fans = result.getFans();/*用户下级数量*/
                                String gender = result.getGender();/*用户性别*/
                                String invite_code = result.getInvite_code();/*邀请码*/
                                String user_id = result.getMember_id();/*用户id*/
                                String member_name = result.getMember_name();/*用户名*/
                                String member_role = result.getMember_role();/*用户角色*/
                                String phone = result.getPhone();/*用户电话号码（账号）*/
                                String wechat = result.getWechat();/*用户微信号*/
                                String pwechat = result.getPwechat();/*用户父微信号（邀请人微信号）*/
                                String balance = result.getBalance();/*可提现余额*/
                                String credits = result.getCredits();/*盒子余额*/
                                String openid = result.getOpenid();/*微信id*/
                                PreferUtils.putString( getApplicationContext(), "userImg", avatar );/*存储头像*/
                                PreferUtils.putString( getApplicationContext(), "member_id", user_id );/*存储用户id*/
                                PreferUtils.putString( getApplicationContext(), "sex", gender );/*存储性别*/
                                PreferUtils.putString( getApplicationContext(), "phoneNum", phone );/*存储电话号码*/
                                if (EmjoyAndTeShuUtil.containsEmoji( member_name )) {
                                    PreferUtils.putString( getApplicationContext(), "userName", "果冻" + num );/*存储用户名*/
                                } else {
                                    PreferUtils.putString( getApplicationContext(), "userName", member_name );/*存储用户名*/
                                }
                                PreferUtils.putString( getApplicationContext(), "wchatname", wechat );/*存储微信号*/
                                PreferUtils.putString( getApplicationContext(), "invite_code", invite_code );/*存储邀请码*/
                                PreferUtils.putString( getApplicationContext(), "member_role", member_role );/*存储用户等级*/
                                PreferUtils.putString( getApplicationContext(), "son_count", fans );/*存储下级个数*/
                                PreferUtils.putString( getApplicationContext(), "pwechat", pwechat );/*存储父微信号*/
                                PreferUtils.putString( getApplicationContext(), "balance", balance );/*存储可提现余额*/
                                PreferUtils.putString( getApplicationContext(), "credits", credits );/*存储盒子余额*/
                                PreferUtils.putString( getApplicationContext(), "openWchatId", openid );/*存储微信id*/
                                PreferUtils.putBoolean( getApplicationContext(), "isLogin", true );
                                ToastUtils.showToast( getApplicationContext(), "登录成功" );
                                EventBus.getDefault().post( "loginSuccess" );
                                Intent intent = new Intent();
                                intent.setAction( "wchatloginfinish" );
                                sendBroadcast( intent );
                                finish();
                                PushAgent mPushAgent = PushAgent.getInstance( getApplicationContext() );
                                mPushAgent.addAlias( user_id, Constant.YOUMENGPUSH, new UTrack.ICallBack() {
                                    @Override
                                    public void onMessage(boolean isSuccess, String message) {
                                        Log.i( "推送别名测试", message );
                                    }
                                } );
                            } else {
                                ToastUtils.showToast( getApplicationContext(), Constant.NONET );
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( getApplicationContext(), Constant.NONET );
                        finish();
                    }
                } );
    }
}
