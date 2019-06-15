package com.guodongbaohe.app.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcDetailPage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.guodongbaohe.app.MainActivity;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.port.DemoTradeCallback;
import com.guodongbaohe.app.util.ClipContentUtil;
import com.guodongbaohe.app.util.CommonUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.WebViewUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdvertisementOfWebViewActivity extends BaseActivity {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.webview)
    WebView webview;
    String url;
    ImageView iv_back;
    ImageView iv_right;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    Intent intent;

    @Override
    public int getContainerView() {
        return R.layout.baseh5activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        ButterKnife.bind( this );
        iv_back = findViewById( R.id.iv_back );
        iv_right = findViewById( R.id.iv_right );
        setRightIVVisible();
        iv_right.setImageResource( R.mipmap.webview_reload );
        intent = getIntent();
        url = intent.getStringExtra( "url" );
        alibcShowParams = new AlibcShowParams( OpenType.Native, true );
        WebSettings settings = webview.getSettings();
        webview.setVerticalScrollBarEnabled( false );
        settings.setJavaScriptEnabled( true );
        settings.setSupportZoom( false );
        settings.setBuiltInZoomControls( true );
        webview.setWebViewClient( new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webview.loadUrl( url, WebViewUtil.getWebViewHead( getApplicationContext() ) );
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading( view, request );
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
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
                    intent = new Intent( getApplicationContext(), MainActivity.class );
                    startActivity( intent );
                    finish();
                }
            }
        } );
        webview.loadUrl( url, WebViewUtil.getWebViewHead( getApplicationContext() ) );
        webview.addJavascriptInterface( new DemoJavascriptInterface(), "daihao" );
        initRightListener();
    }

    /*webview刷新*/
    private void initRightListener() {
        iv_right.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webview != null) {
                    webview.reload();
                }
            }
        } );
    }

    public class DemoJavascriptInterface {

        /*地推物料交互到淘宝详情（不用转高拥接口）*/
        @JavascriptInterface
        public void shareIndex(String id) {
            Log.i( "商品id", id );
            if (PreferUtils.getBoolean( getApplicationContext(), "isLogin" )) {
                AlibcBasePage page = new AlibcDetailPage( id );
                HashMap<String, String> exParams = new HashMap<>();
                exParams.put( "isv_code", "appisvcode" );
                exParams.put( "alibaba", "阿里巴巴" );
                AlibcTrade.show( AdvertisementOfWebViewActivity.this, page, alibcShowParams, null, exParams, new DemoTradeCallback() );
                /*清空剪切板内容*/
                ClipboardManager cm = (ClipboardManager) getSystemService( Context.CLIPBOARD_SERVICE );
                if (cm.hasPrimaryClip()) {
                    cm.setPrimaryClip( ClipData.newPlainText( null, "" ) );
                }
            } else {
                Intent intent = new Intent( getApplicationContext(), LoginAndRegisterActivity.class );
                startActivity( intent );
            }
        }

        /*地推物料复制按钮交互*/
        @JavascriptInterface
        public void copy(String url) {
            Log.i( "看看地址", url + " 值 " );
            if (!TextUtils.isEmpty( url )) {
                ClipContentUtil.getInstance( getApplicationContext() ).putNewSearch( url );//保存记录到数据库
                ClipboardManager systemService = (ClipboardManager) getSystemService( Context.CLIPBOARD_SERVICE );
                systemService.setPrimaryClip( ClipData.newPlainText( "text", url ) );
            }
        }

        /*图片长按保存*/
        @JavascriptInterface
        public void preserve(String img) {
            saveImgToLocal( img );
        }

        /*淘宝会场*/
        @JavascriptInterface
        public void goShopping(String myUrl) {
            runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    if (PreferUtils.getBoolean( getApplicationContext(), "isLogin" )) {
                        AlibcBasePage page = new AlibcPage( myUrl );
                        HashMap<String, String> exParams = new HashMap<>();
                        exParams.put( "isv_code", "appisvcode" );
                        exParams.put( "alibaba", "阿里巴巴" );
                        AlibcTrade.show( AdvertisementOfWebViewActivity.this, page, alibcShowParams, null, exParams, new DemoTradeCallback() );
                    } else {
                        intent = new Intent( getApplicationContext(), LoginAndRegisterActivity.class );
                        startActivity( intent );
                    }
                }
            } );
        }
    }

    /*保存图片至相册*/
    private void saveImgToLocal(String img) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                URL imageurl = null;
                try {
                    imageurl = new URL( img );
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
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
            CommonUtil.saveBitmap2file( bitmap, getApplicationContext() );
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack();
            return true;
        } else {
            intent = new Intent( getApplicationContext(), MainActivity.class );
            startActivity( intent );
            finish();
        }
        return super.onKeyDown( keyCode, event );
    }

    @Override
    protected void onDestroy() {
        if (webview != null) {
            webview.loadDataWithBaseURL( null, "", "text/html", "utf-8", null );
            webview.clearHistory();
            ((ViewGroup) webview.getParent()).removeView( webview );
            webview.destroy();
            webview = null;
        }
        super.onDestroy();
    }

}
