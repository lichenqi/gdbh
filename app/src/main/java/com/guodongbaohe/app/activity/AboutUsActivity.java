package com.guodongbaohe.app.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.util.WebViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/*1227*/
public class AboutUsActivity extends BaseActivity {
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.version)
    TextView version;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Override
    public int getContainerView() {
        return R.layout.aboutusactivity;
    }

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String about_us = PreferUtils.getString(getApplicationContext(), "about_us");
        ButterKnife.bind(this);
        setMiddleTitle("关于我们");
        String versionCode = VersionUtil.getVersionCode(getApplicationContext());
        version.setText("版本号: V" + versionCode);
        WebSettings settings = webview.getSettings();
        webview.setVerticalScrollBarEnabled(false);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url, WebViewUtil.getWebViewHead(getApplicationContext()));
                return true;
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });
        webview.loadUrl(about_us, WebViewUtil.getWebViewHead(getApplicationContext()));
    }
}
