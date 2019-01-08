package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.VersionUtil;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareDetailActivity extends BaseActivity {
    String url;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.webview)
    WebView webview;
    ImageView iv_back;

    @Override
    public int getContainerView() {
        return R.layout.baseh5activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");


        WebSettings settings = webview.getSettings();
        webview.setVerticalScrollBarEnabled(false);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(true);
        settings.setAllowContentAccess(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
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

            @Override
            public void onReceivedTitle(WebView view, String title) {
                setMiddleTitle(title);
                super.onReceivedTitle(view, title);
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webview.canGoBack()) {
                    webview.goBack();
                }
                 else {
                    finish();
                }
            }
        });
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("x-userid", PreferUtils.getString(getApplicationContext(), "member_id"));
        map.put("x-appid", Constant.APPID);
        map.put("x-devid", PreferUtils.getString(getApplicationContext(), Constant.PESUDOUNIQUEID));
        map.put("x-nettype", PreferUtils.getString(getApplicationContext(), Constant.NETWORKTYPE));
        map.put("x-agent", VersionUtil.getVersionCode(getApplicationContext()));
        map.put("x-platform", Constant.ANDROID);
        map.put("x-devtype", Constant.IMEI);
        map.put("x-token", ParamUtil.GroupMap(getApplicationContext(), PreferUtils.getString(getApplicationContext(), "member_id")));
        webview.loadUrl(url, map);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview.removeAllViews();
        webview.destroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
