package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.bean.BaseUserBean;
import com.guodongbaohe.app.bean.ConfigurationBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.EmjoyAndTeShuUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*新用户手机号码登录界面*/
public class FirstUserLoginActivity extends BigBaseActivity {
    String phone, invite_code;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.ed_yanzma)
    EditText ed_yanzma;
    @BindView(R.id.tv_get_code)
    TextView tv_get_code;
    @BindView(R.id.login)
    TextView login;
    @BindView(R.id.iv_xieyi)
    ImageView iv_xieyi;
    @BindView(R.id.ll_xieyi)
    LinearLayout ll_xieyi;
    private TimeCount time = new TimeCount( 60000, 1000 );
    int num;
    private boolean isXieyi = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.checkyanzmaactivity );
        ButterKnife.bind( this );
        num = (int) ((Math.random() * 9 + 1) * 10000000);
        Intent intent = getIntent();
        phone = intent.getStringExtra( "phone" );
        invite_code = intent.getStringExtra( "invite_code" );
    }

    String url, getUrl;
    ConfigurationBean.PageBean list_data;

    @OnClick({R.id.iv_back, R.id.tv_get_code, R.id.login, R.id.iv_xieyi, R.id.ll_xieyi})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_get_code:
                getCodeData();
                break;
            case R.id.login:
                String yanzma = ed_yanzma.getText().toString().trim();
                if (TextUtils.isEmpty( yanzma )) {
                    ToastUtils.showToast( getApplicationContext(), "请输入您获取的验证码" );
                    return;
                }
                if (isXieyi == false) {
                    ToastUtils.showToast( getApplicationContext(), "请先阅读用户协议并勾选" );
                    return;
                }
                LoginData( yanzma );
                break;
            case R.id.iv_xieyi:
                if (isXieyi) {
                    iv_xieyi.setImageResource( R.mipmap.buxianshiyjin );
                } else {
                    iv_xieyi.setImageResource( R.mipmap.xainshiyjin );
                }
                isXieyi = !isXieyi;
                break;
            case R.id.ll_xieyi:
                getUrl = PreferUtils.getString( this, "http_list_data" );
                if (!TextUtils.isEmpty( getUrl )) {
                    Gson gson = new Gson();
                    list_data = gson.fromJson( getUrl, new TypeToken<ConfigurationBean.PageBean>() {
                    }.getType() );
                    url = list_data.getProtocol().getUrl();
                }
                Intent intent = new Intent( getApplicationContext(), XinShouJiaoChengActivity.class );
                intent.putExtra( "url", PreferUtils.getString( getApplicationContext(), "agreement" ) );
                startActivity( intent );
                break;
        }
    }

    String result;

    private void LoginData(String yanzma) {
        loadingDialog = DialogUtil.createLoadingDialog( FirstUserLoginActivity.this, "正在登录" );
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "phone", phone );
        map.put( "words", yanzma );
        map.put( "invite_code", invite_code );
        String mapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( mapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String param = ParamUtil.getMapParam( map );
        Log.i( "登录请求地址", Constant.BASE_URL + Constant.FIRSTUSERLOGIN + "?" + param );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.FIRSTUSERLOGIN + "?" + param )
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
                        DialogUtil.closeDialog( loadingDialog, FirstUserLoginActivity.this );
                        Log.i( "登录返回值", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            int status = jsonObject.getInt( "status" );
                            if (status >= 0) {
                                /*登录成功*/
                                result = jsonObject.getString( "result" );/*用户id*/
                                getConfigurationData( result );
                            } else {
                                /*登录异常*/
                                result = jsonObject.getString( "result" );
                                if (TextUtils.isEmpty( result )) {
                                    ToastUtils.showToast( getApplicationContext(), Constant.NONET );
                                } else {
                                    ToastUtils.showToast( getApplicationContext(), result );
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( getApplicationContext(), Constant.NONET );
                        DialogUtil.closeDialog( loadingDialog, FirstUserLoginActivity.this );
                    }
                } );
    }

    Dialog loadingDialog;

    private void getCodeData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        loadingDialog = DialogUtil.createLoadingDialog( FirstUserLoginActivity.this, "正在获取验证码..." );
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( "phone", phone );
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.GETCODE + "?" + param )
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
                        Log.i( "验证码", response.toString() );
                        DialogUtil.closeDialog( loadingDialog, FirstUserLoginActivity.this );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            int aReturn = jsonObject.getInt( "status" );
                            if (aReturn >= 0) {
                                ToastUtils.showToast( getApplicationContext(), "短息验证码已发送至您的手机" );
                                time.start();
                            } else {
                                String result = jsonObject.getString( "result" );
                                if (TextUtils.isEmpty( result )) {
                                    ToastUtils.showToast( getApplicationContext(), Constant.NONET );
                                } else {
                                    ToastUtils.showToast( getApplicationContext(), result );
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        DialogUtil.closeDialog( loadingDialog, FirstUserLoginActivity.this );
                        ToastUtils.showToast( getApplicationContext(), Constant.NONET );
                    }
                } );
    }

    private class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super( millisInFuture, countDownInterval );
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tv_get_code.setClickable( false );
            tv_get_code.setText( "重新获取" + millisUntilFinished / 1000 + "s" );
            tv_get_code.setBackgroundResource( R.drawable.gray_invite_code );
            tv_get_code.setTextColor( 0xff939393 );
        }

        @Override
        public void onFinish() {
            tv_get_code.setClickable( true );
            tv_get_code.setText( "获取验证码" );
            tv_get_code.setBackgroundResource( R.drawable.yanzma );
            tv_get_code.setTextColor( 0xffffffff );
        }
    }


    @Override
    protected void onDestroy() {
        if (time != null) {
            time.cancel();
        }
        DialogUtil.closeDialog( loadingDialog, FirstUserLoginActivity.this );
        super.onDestroy();
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
                .addHeader( "x_userid", member_id )
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
                                PreferUtils.putString( getApplicationContext(), "openWchatId", result.getOpenid() );/*存储微信id*/
                                PreferUtils.putBoolean( getApplicationContext(), "isLogin", true );
                                ToastUtils.showToast( getApplicationContext(), "登录成功" );
                                EventBus.getDefault().post( Constant.LOGINSUCCESS );
                                setResult( 100 );
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
