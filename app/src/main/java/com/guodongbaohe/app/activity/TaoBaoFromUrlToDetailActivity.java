package com.guodongbaohe.app.activity;

import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BigBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaoBaoFromUrlToDetailActivity extends BigBaseActivity {
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.tv_back)
    TextView tv_back;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.iv_right)
    ImageView iv_right;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taobaofromurltodetailactivity);
        ButterKnife.bind(this);
        String coupon_url = getIntent().getStringExtra("coupon_url");
        WebSettings settings = webview.getSettings();
        webview.setVerticalScrollBarEnabled(false);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(true);
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
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
                tv_title.setText(title);
                super.onReceivedTitle(view, title);
            }
        });
        webview.loadUrl(coupon_url);
    }

    @OnClick({R.id.iv_back, R.id.tv_back, R.id.iv_right})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (webview.canGoBack()) {
                    webview.goBack();
                } else {
                    finish();
                }
                break;
            case R.id.tv_back:
                finish();
                break;
            case R.id.iv_right:
                if (webview != null) {
                    webview.reload();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (webview != null) {
            webview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webview.clearHistory();
            ((ViewGroup) webview.getParent()).removeView(webview);
            webview.destroy();
            webview = null;
        }
        super.onDestroy();
    }

}
