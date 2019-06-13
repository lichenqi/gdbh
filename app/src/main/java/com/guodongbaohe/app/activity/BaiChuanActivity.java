package com.guodongbaohe.app.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.adapter.login.AlibcLogin;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.BeiAnBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaiChuanActivity extends BaseActivity {
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    ImageView iv_back;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    AlibcBasePage page;
    String member_id, script;
    HashMap<String, String> exParams;
    String js, taobao_nick;

    @Override
    public int getContainerView() {
        return R.layout.baseh5activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        ButterKnife.bind( this );
        AlibcLogin instance = AlibcLogin.getInstance();
        taobao_nick = instance.getSession().nick;
        iv_back = (ImageView) findViewById( R.id.iv_back );
        member_id = PreferUtils.getString( getApplicationContext(), "member_id" );
        alibcShowParams = new AlibcShowParams( OpenType.H5, true );
        exParams = new HashMap<>();
        exParams.put( "isv_code", "appisvcode" );
        exParams.put( "alibaba", "阿里巴巴" );
        WebSettings settings = webview.getSettings();
        webview.setVerticalScrollBarEnabled( false );
        settings.setJavaScriptEnabled( true );
        settings.setSupportZoom( false );
        settings.setBuiltInZoomControls( true );
        webview.setWebViewClient( new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished( view, url );
                if (!TextUtils.isEmpty( js )) {
                    webview.loadUrl( "javascript:" + js );
                    Log.i( "聚到地址", js );
                }
            }

        } );
        webview.setWebChromeClient( new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility( View.GONE );
                } else {
                    progressBar.setVisibility( View.VISIBLE );
                    progressBar.setProgress( newProgress );
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                setMiddleTitle( title );
                super.onReceivedTitle( view, title );
            }
        } );
        iv_back.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webview.canGoBack()) {
                    webview.goBack();
                } else {
                    finish();
                }
            }
        } );
        /*检查是否需要备案*/
        checkIsNeedBeian();
        webview.addJavascriptInterface( new DemoJavascriptInterface(), "Guodong" );
    }

    public class DemoJavascriptInterface {

        @JavascriptInterface
        public void saved(String msg) {
            Log.i( "回调消息", msg + "  1  " );
            /*保存接口*/
            saveMsg( msg );
        }
    }

    private void saveMsg(String msg) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", member_id );
        map.put( "account_name", taobao_nick );
        map.put( "explain", msg );
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.SAVEBEIAN + "?" + param )
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
                        Log.i( "保存结果", response.toString() );
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        Log.i( "保存结果错误", error_msg );
                    }
                } );
    }

    String note;

    /*检查是否需要备案*/
    private void checkIsNeedBeian() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( Constant.TIMELINE, String.valueOf( timelineStr ) );
        map.put( Constant.PLATFORM, Constant.ANDROID );
        map.put( "member_id", member_id );
        String qianMingMapParam = ParamUtil.getQianMingMapParam( map );
        String token = EncryptUtil.encrypt( qianMingMapParam + Constant.NETKEY );
        map.put( Constant.TOKEN, token );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post()
                .url( Constant.BASE_URL + Constant.BEIANCHECK + "?" + param )
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
                        Log.i( "是否需要备案", response.toString() );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                            } else {
                                BeiAnBean beiAnBean = GsonUtil.GsonToBean( response.toString(), BeiAnBean.class );
                                if (beiAnBean.getSpecial().equals( "beian" )) {
                                    BeiAnBean.BeiAnData result = beiAnBean.getResult();
                                    note = result.getNote();
                                    String url = result.getUrl();
                                    script = result.getScript();
                                    /*js文件*/
                                    js = "var ns = document.createElement('script');";
                                    js += "ns.src = '" + script + "'; ns.onload = function(){ Beian.init( '" + note + "' ); };";
                                    js += "document.body.appendChild(ns);";
                                    webview.loadUrl( url );
                                }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlibcTradeSDK.destory();
    }
}
