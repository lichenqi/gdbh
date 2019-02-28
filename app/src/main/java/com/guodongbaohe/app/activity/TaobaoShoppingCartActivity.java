package com.guodongbaohe.app.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeCallback;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.model.TradeResult;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcMyCartsPage;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.PreferUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaobaoShoppingCartActivity extends BaseActivity {
    @BindView(R.id.tv_youhui)
    TextView tv_youhui;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.notice)
    RelativeLayout notice;
    ImageView iv_back,iv_right;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    AlibcBasePage page;
    private String script = "https://static.mopland.com/js/tbcart.js";
    private String js;

    @Override
    public int getContainerView() {
        return R.layout.taobaoshoppingcartactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("淘宝购物车");
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_right=(ImageView)findViewById(R.id.iv_right);
        setRightIVVisible();
        iv_right.setImageResource(R.mipmap.refish_h);
        alibcShowParams = new AlibcShowParams(OpenType.H5, true);
        page = new AlibcMyCartsPage();
        HashMap<String, String> exParams = new HashMap<>();
        exParams.put("isv_code", "appisvcode");
        exParams.put("alibaba", "阿里巴巴");
        WebSettings settings = webview.getSettings();
        webview.setVerticalScrollBarEnabled(false);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(true);
        /*js文件*/
        js = "var ns = document.createElement('script');";
        js += "ns.src = '" + script + "'; ns.onload = function(){TbCart.init();};";
        js += "document.body.appendChild(ns);";
        webview.addJavascriptInterface(new DemoJavascriptInterface(), "Guodong");
        AlibcTrade.show(TaobaoShoppingCartActivity.this, webview, new MyWebViewClient(), new MyWebChromeClient(), page, alibcShowParams, null, exParams, new AlibcTradeCallback() {

            @Override
            public void onTradeSuccess(TradeResult tradeResult) {
                //打开电商组件，用户操作中成功信息回调。tradeResult：成功信息（结果类型：加购，支付；支付结果）
            }

            @Override
            public void onFailure(int code, String msg) {
                //打开电商组件，用户操作中错误信息回调。code：错误码；msg：错误信息
            }

        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webview.canGoBack()) {
                    webview.goBack();
                } else {
                    finish();
                }
            }
        });
        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webview.reload();
            }
        });
    }

    public class MyWebChromeClient extends WebChromeClient {

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
            super.onReceivedTitle(view, title);
        }
    }

    public class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!TextUtils.isEmpty(js)) {
                webview.loadUrl("javascript:" + js);
            }
        }

    }

    private String shop_ids = "";

    public class DemoJavascriptInterface {

        @JavascriptInterface
        public void coupon(String[] msg) {
            shop_ids = "";
            for (int i = 0; i < msg.length; i++) {
                shop_ids += msg[i] + ",";
            }
            shop_ids = shop_ids.substring(0, shop_ids.length() - 1);
            Log.i("订单数据", shop_ids + "李晨奇");
            if (!TextUtils.isEmpty(shop_ids)) {
                PreferUtils.putString(getApplicationContext(), "shop_ids", shop_ids);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(shop_ids)) {
                        //耗时操作
                        mHandler.sendEmptyMessage(0);
                    }
                }
            }).start();
            webview.reload();
        }
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    tv_youhui.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };

    Dialog loadingDialog;

    @OnClick({R.id.tv_youhui, R.id.notice})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_youhui:
                loadingDialog = DialogUtil.createLoadingDialog(TaobaoShoppingCartActivity.this, "正在查询");
                Intent intent = new Intent(getApplicationContext(), SaveMoneyShopCartActivity.class);
                startActivity(intent);
                break;
            case R.id.notice:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DialogUtil.closeDialog(loadingDialog);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlibcTradeSDK.destory();
        DialogUtil.closeDialog(loadingDialog);
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
