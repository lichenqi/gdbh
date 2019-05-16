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
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UnBindWChatActivity extends BaseActivity {
    @BindView(R.id.cell_phone)
    TextView cell_phone;
    @BindView(R.id.yzm_code)
    EditText yzm_code;
    @BindView(R.id.get_code)
    TextView get_code;
    @BindView(R.id.submit_btn)
    TextView submit_btn;
    String phoneNum, member_id;
    Dialog loadingDialog;
    private TimeCount time = new TimeCount( 60000, 1000 );

    @Override
    public int getContainerView() {
        return R.layout.unbindwchatactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        ButterKnife.bind( this );
        setMiddleTitle( "解绑微信" );
        Intent intent = getIntent();
        member_id = PreferUtils.getString( getApplicationContext(), "member_id" );
        phoneNum = intent.getStringExtra( "phoneNum" );
        cell_phone.setText( phoneNum );
    }

    @OnClick({R.id.get_code, R.id.submit_btn})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.get_code:
                getCodeData();
                break;
            case R.id.submit_btn:
                String code = yzm_code.getText().toString().trim();
                if (TextUtils.isEmpty( code )) {
                    ToastUtils.showToast( UnBindWChatActivity.this, "请输入验证码" );
                    return;
                }
                unbindWchatData( code );
                break;
        }
    }

    /*验证手机号和验证码*/
    private void unbindWchatData(String code) {
        loadingDialog = DialogUtil.createLoadingDialog( UnBindWChatActivity.this, "正在提交..." );
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "old_phone", phoneNum );
        map.put( "words", code );
        map.put( "member_id", member_id );
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String mapParam = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.CHANGE_PHONE + "?" + mapParam )
                .tag( this )
                .addHeader( "x-userid", member_id )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( getApplicationContext(), Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( getApplicationContext(), Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( getApplicationContext() ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( getApplicationContext(), member_id ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        Log.i( "验证结果", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                /*解绑微信*/
                                unbindWchatApi();
                            } else {
                                String result = jsonObject.getString( "result" );
                                ToastUtils.showToast( getApplicationContext(), result );
                                DialogUtil.closeDialog( loadingDialog, UnBindWChatActivity.this );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        DialogUtil.closeDialog( loadingDialog, UnBindWChatActivity.this );
                        ToastUtils.showToast( getApplicationContext(), Constant.NONET );
                    }
                } );
    }

    private void unbindWchatApi() {
        long timelineStr = System.currentTimeMillis() / 1000;
                              LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", member_id );
                              String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
                              String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
                              String mapParam = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.UNBINDWCHAT + "?" + mapParam )
                .tag( this )
                .addHeader( "x-userid", member_id )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( getApplicationContext(), Constant.PESUDOUNIQUEID ) )
                                      .addHeader( "x-nettype", PreferUtils.getString( getApplicationContext(), Constant.NETWORKTYPE ) )
                                      .addHeader( "x-agent", VersionUtil.getVersionCode( getApplicationContext() ) )
                                      .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( getApplicationContext(), member_id ) )
                                      .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        DialogUtil.closeDialog( loadingDialog, UnBindWChatActivity.this );
                        Log.i( "解绑结果", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                String result = jsonObject.getString( "result" );
                                ToastUtils.showBackgroudCenterToast( getApplicationContext(), result );
                                PreferUtils.putString( getApplicationContext(), "openWchatId", "" );
                                finish();
                            } else {
                                String result = jsonObject.getString( "result" );
                                ToastUtils.showToast( getApplicationContext(), result );
                                DialogUtil.closeDialog( loadingDialog, UnBindWChatActivity.this );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        DialogUtil.closeDialog( loadingDialog, UnBindWChatActivity.this );
                        ToastUtils.showToast( getApplicationContext(), Constant.NONET );
                    }
                } );
    }

    /*获取验证码接口*/
    private void getCodeData() {
        loadingDialog = DialogUtil.createLoadingDialog( UnBindWChatActivity.this, "正在获取验证码..." );
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "phone", phoneNum );
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String mapParam = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.GETCODE + "?" + mapParam )
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
                        DialogUtil.closeDialog( loadingDialog, UnBindWChatActivity.this );
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
                        DialogUtil.closeDialog( loadingDialog, UnBindWChatActivity.this );
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
            get_code.setClickable( false );
            get_code.setText( millisUntilFinished / 1000 + "s" );
        }

        @Override
        public void onFinish() {
            get_code.setClickable( true );
            get_code.setText( "获取验证码" );
        }
    }

    @Override
    protected void onDestroy() {
        if (time != null) {
            time.cancel();
        }
        DialogUtil.closeDialog( loadingDialog, UnBindWChatActivity.this );
        super.onDestroy();
    }

}
