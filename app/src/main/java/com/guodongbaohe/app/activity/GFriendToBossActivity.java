package com.guodongbaohe.app.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
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
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.alipay.sdk.app.PayTask;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.alipay.PayResult;
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
import com.guodongbaohe.app.util.WebViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GFriendToBossActivity extends BaseActivity {
    ImageView iv_back;
    String url;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.webview)
    WebView webview;

    @Override
    public int getContainerView() {
        return R.layout.baseh5activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        url = "http://app.mopland.com/help/president";
        iv_back = (ImageView) findViewById(R.id.iv_back);
        member_id = PreferUtils.getString(getApplicationContext(), "member_id");
        Intent intent = getIntent();
        son_count = intent.getStringExtra("son_count");
        WebSettings settings = webview.getSettings();
        webview.setVerticalScrollBarEnabled(false);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(true);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url, WebViewUtil.getWebViewHead(getApplicationContext()));
                Log.i("网页地址", url);
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
        webview.loadUrl(url, WebViewUtil.getWebViewHead(getApplicationContext()));
        webview.addJavascriptInterface(new DemoJavascriptInterface(), "daihao");

    }

    public class DemoJavascriptInterface {

        @JavascriptInterface
        public void pay() {
//            Log.i("商品id", i+"");
            payData();
        }
    }

    Dialog loadingDialog;
    JSONObject jsonObject;
    String member_id, son_count, sn;

    private void payData() {
        loadingDialog = DialogUtil.createLoadingDialog(GFriendToBossActivity.this, "加载中...");
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("method", "alipay");
        map.put("is_new", "ture");
        String param = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(param + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String mapParam = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.PAY_DATA + "?" + mapParam)
                .tag(this)
                .addHeader("x-userid", member_id)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getApplicationContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getApplicationContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getApplicationContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getApplicationContext(), PreferUtils.getString(getApplicationContext(), "member_id")))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("支付", response.toString());
                        try {
                            jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getString("result").equals("升级成功")) {
                                DialogUtil.closeDialog(loadingDialog);
                                Intent intent = new Intent(getApplicationContext(), PaySuccessActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                JSONObject result = jsonObject.getJSONObject("result");
                                String amount = result.getString("trades[amount]");
                                String appid = result.getString("trades[appid]");
                                sn = result.getString("trades[sn]");
                                String username = result.getString("trades[username]");
                                String userid = result.getString("trades[userid]");
                                getOrderInfo(amount, appid, sn, userid, username);
                            } else {
                                String result = jsonObject.getString("result");
                                DialogUtil.closeDialog(loadingDialog);
                                ToastUtils.showToast(getApplicationContext(), result);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        DialogUtil.closeDialog(loadingDialog);
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

    String resultOrderInfo;

    private void getOrderInfo(String amount, String appid, String sn, String userid, String username) {
        HashMap<String, String> map = new HashMap<>();
        map.put("trades[amount]", amount);
        map.put("trades[appid]", appid);
        map.put("trades[sn]", sn);
        map.put("trades[method]", "alipay");
        map.put("trades[userid]", userid);
        map.put("trades[username]", username);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        Log.i("参数", qianMingMapParam);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.PAY_ORDER_NO + qianMingMapParam)
                .tag(this)
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        DialogUtil.closeDialog(loadingDialog);
                        Log.i("订单信息", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            resultOrderInfo = jsonObject.getString("result");
                            Runnable payRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    PayTask alipay = new PayTask(GFriendToBossActivity.this);
                                    Map<String, String> result = alipay.payV2(resultOrderInfo, true);
                                    Message msg = new Message();
                                    msg.what = 1;
                                    msg.obj = result;
                                    alipayHandler.sendMessage(msg);
                                }
                            };
                            Thread payThread = new Thread(payRunnable);
                            payThread.start();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        DialogUtil.closeDialog(loadingDialog);
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

    @SuppressLint("HandlerLeak")
    private Handler alipayHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();

                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        /*校验支付成功*/
                        checkSNData();
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        ToastUtils.showToast(getApplicationContext(), "支付成功");
                        Intent intent = new Intent(getApplicationContext(), PaySuccessActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtils.showToast(getApplicationContext(), "支付失败");
                    }
                    break;
                }
            }
        }
    };

    private void checkSNData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("sn", sn);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + "payment/validate?" + param)
                .tag(this)
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("教研", response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
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
