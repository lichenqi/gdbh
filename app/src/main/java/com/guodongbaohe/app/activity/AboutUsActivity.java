package com.guodongbaohe.app.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.ConfigurationBean;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.util.WebViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/*关于 我们*/
public class AboutUsActivity extends BaseActivity {
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.version)
    TextView version;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    ImageView iv_right;
    String url, getUrl;
    ConfigurationBean.PageBean list_data;

    @Override
    public int getContainerView() {
        return R.layout.aboutusactivity;
    }

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        ButterKnife.bind( this );
        getUrl = PreferUtils.getString( this, "http_list_data" );
        if (!TextUtils.isEmpty( getUrl )) {
            Gson gson = new Gson();
            list_data = gson.fromJson( getUrl, new TypeToken<ConfigurationBean.PageBean>() {
            }.getType() );
            url = list_data.getAbout().getUrl();
        }
        setMiddleTitle( "关于我们" );
        iv_right = (ImageView) findViewById( R.id.iv_right );
        setRightIVVisible();
        iv_right.setImageResource( R.mipmap.refish_h );
        String versionCode = VersionUtil.getAndroidNumVersion( getApplicationContext() );
        version.setText( "版本号: V" + versionCode );
        WebSettings settings = webview.getSettings();
        webview.setVerticalScrollBarEnabled( false );
        settings.setJavaScriptEnabled( true );
        settings.setSupportZoom( false );
        settings.setBuiltInZoomControls( true );
        webview.setWebViewClient( new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl( url, WebViewUtil.getWebViewHead( getApplicationContext() ) );
                return true;
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
        } );
        webview.loadUrl( url, WebViewUtil.getWebViewHead( getApplicationContext() ) );
        iv_right.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webview.reload();
            }
        } );
    }
}
