package com.guodongbaohe.app.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.ExclusiveTutorBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.dialogfragment.BaseNiceDialog;
import com.guodongbaohe.app.dialogfragment.NiceDialog;
import com.guodongbaohe.app.dialogfragment.ViewConvertListener;
import com.guodongbaohe.app.dialogfragment.ViewHolder;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.ClipContentUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.Tools;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExclusiveTutorActivity extends BaseActivity {
    @BindView(R.id.tv_copy)
    TextView tv_copy;
    @BindView(R.id.re_parent)
    RelativeLayout re_parent;
    @BindView(R.id.tv_num)
    TextView tv_num;
    String member_role, member_id;
    String wechat;

    @Override
    public int getContainerView() {
        return R.layout.exclusivetutoractivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        ButterKnife.bind( this );
        setMiddleTitle( "专属导师" );
        member_role = PreferUtils.getString( getApplicationContext(), "member_role" );
        member_id = PreferUtils.getString( getApplicationContext(), "member_id" );
        getData();
    }

    private void getData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", member_id );
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String mapParam = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.EXCLUSIVETUTOR_API + "?" + mapParam )
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
                        Log.i( "微信号", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                ExclusiveTutorBean bean = GsonUtil.GsonToBean( response.toString(), ExclusiveTutorBean.class );
                                if (bean == null) return;
                                wechat = bean.getResult().getWechat();
                                tv_num.setText( wechat );
                                showDialog();
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

    @OnClick({R.id.tv_copy})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_copy:
                copyFunction( 1 );
                showDialog();
                break;
        }
    }

    NiceDialog niceDialog;

    private void showDialog() {
        copyFunction( 0 );
        niceDialog = NiceDialog.init();
        niceDialog.setLayoutId( R.layout.copy_wchat_dialog );
        niceDialog.setConvertListener( new ViewConvertListener() {
            @Override
            protected void convertView(ViewHolder holder, BaseNiceDialog dialog) {
                ImageView close = holder.getView( R.id.close );
                TextView add = holder.getView( R.id.add );
                close.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        niceDialog.dismiss();
                    }
                } );
                add.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!Tools.isAppAvilible( getApplicationContext(), "com.tencent.mm" )) {
                            ToastUtils.showToast( getApplicationContext(), "您还没有安装微信客户端,请先安转客户端" );
                            return;
                        }
                        Intent intent = new Intent( Intent.ACTION_MAIN );
                        ComponentName cmp = new ComponentName( "com.tencent.mm", "com.tencent.mm.ui.LauncherUI" );
                        intent.addCategory( Intent.CATEGORY_LAUNCHER );
                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                        intent.setComponent( cmp );
                        startActivity( intent );
                        niceDialog.dismiss();
                    }
                } );
            }
        } );
        niceDialog.setMargin( 150 );
        niceDialog.show( getSupportFragmentManager() );
        niceDialog.setCancelable( false );
        niceDialog.setOutCancel( false );
    }

    private void copyFunction(int mode) {
        ClipboardManager cm = (ClipboardManager) getSystemService( Context.CLIPBOARD_SERVICE );
        ClipData mClipData = ClipData.newPlainText( "Label", tv_num.getText().toString().trim() );
        cm.setPrimaryClip( mClipData );
        ClipContentUtil.getInstance( getApplicationContext() ).putNewSearch( tv_num.getText().toString().trim() );//保存记录到数据库
        if (mode == 1) {
            ToastUtils.showBackgroudCenterToast( getApplicationContext(), "复制成功" );
        }
    }

}
