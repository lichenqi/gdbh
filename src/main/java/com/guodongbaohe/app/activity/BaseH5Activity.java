package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
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
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.GaoYongJinBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.port.DemoTradeCallback;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseH5Activity extends BaseActivity {
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.webview)
    WebView webview;
    String url;
    ImageView iv_back;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native

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
        alibcShowParams = new AlibcShowParams(OpenType.Native, true);
        WebSettings settings = webview.getSettings();
        webview.setVerticalScrollBarEnabled(false);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
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
                } else {
                    finish();
                }
            }
        });
        webview.loadUrl(url);
        webview.addJavascriptInterface(new DemoJavascriptInterface(), "daihao");
    }

    public class DemoJavascriptInterface {

        @JavascriptInterface
        public void shareIndex(String id) {
            Log.i("商品id", id);
            if (PreferUtils.getBoolean(getApplicationContext(), "isLogin")) {
//                /*调用高佣金接口*/
//                getGaoYongJinData(id);
                AlibcBasePage page = new AlibcDetailPage(id);
                HashMap<String, String> exParams = new HashMap<>();
                exParams.put("isv_code", "appisvcode");
                exParams.put("alibaba", "阿里巴巴");
                AlibcTrade.show(BaseH5Activity.this, page, alibcShowParams, null, exParams, new DemoTradeCallback());
            } else {
                Intent intent = new Intent(getApplicationContext(), LoginAndRegisterActivity.class);
                startActivity(intent);
            }
        }
    }

    /*跳转淘宝链接*/
    String coupon_url;

    private void getGaoYongJinData(String goods_id) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        map.put("goods_id", goods_id);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        Log.i("高佣金返回值参数", Constant.BASE_URL + Constant.GAOYONGIN + "?" + param);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.GAOYONGIN + "?" + param)
                .addHeader("x-userid", PreferUtils.getString(getApplicationContext(), "member_id"))
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getApplicationContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getApplicationContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getApplicationContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-token", ParamUtil.GroupMap(getApplicationContext(), PreferUtils.getString(getApplicationContext(), "member_id")))
                .addHeader("x-userid", PreferUtils.getString(getApplicationContext(), "member_id"))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("高佣金返回值", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                GaoYongJinBean bean = GsonUtil.GsonToBean(response.toString(), GaoYongJinBean.class);
                                if (bean == null) return;
                                String coupon_remain_count = bean.getResult().getCoupon_remain_count();
                                if (!TextUtils.isEmpty(coupon_remain_count) && Double.valueOf(coupon_remain_count) > 0) {
                                    coupon_url = bean.getResult().getCoupon_click_url();
                                } else {
                                    coupon_url = bean.getResult().getItem_url();
                                }
                                AlibcBasePage page = new AlibcPage(coupon_url);
                                HashMap<String, String> exParams = new HashMap<>();
                                exParams.put("isv_code", "appisvcode");
                                exParams.put("alibaba", "阿里巴巴");
                                AlibcTrade.show(BaseH5Activity.this, page, alibcShowParams, null, exParams, new DemoTradeCallback());
                            } else {
                                String result = jsonObject.getString("result");
                                ToastUtils.showToast(getApplicationContext(), result);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
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
