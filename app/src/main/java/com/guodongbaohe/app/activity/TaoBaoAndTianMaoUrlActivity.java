package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeCallback;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.model.TradeResult;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.bean.AllNetBean;
import com.guodongbaohe.app.bean.GaoYongJinBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.NetUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.util.WebViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*这个类专门对淘宝天猫地址加载  比如天猫国际  天猫超市   不要改动了*/
public class TaoBaoAndTianMaoUrlActivity extends BigBaseActivity {
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.tv_close)
    TextView tv_close;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.iv_right)
    ImageView iv_right;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.webview)
    WebView webview;
    @BindView(R.id.tv_notice)
    TextView tv_notice;
    @BindView(R.id.ll_yijian_view)
    LinearLayout ll_yijian_view;
    @BindView(R.id.yijianchaxuan)
    TextView yijianchaxuan;
    @BindView(R.id.ll_action)
    RelativeLayout ll_action;
    @BindView(R.id.re_share)
    RelativeLayout re_share;
    @BindView(R.id.tv_share)
    TextView tv_share;
    @BindView(R.id.re_buy)
    RelativeLayout re_buy;
    @BindView(R.id.tv_buy)
    TextView tv_buy;
    String url, title;
    String shop_id;
    Dialog loadingDialog;
    String member_role, tax_rate;
    double app_v;
    Intent intent;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taobaoandtianmaourlactivity);
        ButterKnife.bind(this);
        alibcShowParams = new AlibcShowParams(OpenType.Native, true);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        tv_title.setText(title);
        member_role = PreferUtils.getString(getApplicationContext(), "member_role");
        tax_rate = PreferUtils.getString(getApplicationContext(), "tax_rate");
        app_v = 1 - Double.valueOf(tax_rate);
        WebSettings settings = webview.getSettings();
        webview.setVerticalScrollBarEnabled(false);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(true);
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("淘宝地址", url);
                if (url.contains("https://detail.m.tmall.com/item.htm?")
                        || url.contains("https://detail.m.taobao.com/item.htm?")
                        || url.contains("https://detail.m.tmall.hk/item.htm?")
                        || url.contains("https://detail.m.taobao.hk/item.htm?")) {
                    tv_notice.setVisibility(View.VISIBLE);
                    ll_yijian_view.setVisibility(View.VISIBLE);
                    //将String类型的地址转变为URI类型
                    Uri uri = Uri.parse(url);
                    /*获取商品id*/
                    shop_id = uri.getQueryParameter("id");
                } else {
                    tv_notice.setVisibility(View.GONE);
                    ll_yijian_view.setVisibility(View.GONE);
                    ll_action.setVisibility(View.GONE);
                }
                super.onPageFinished(view, url);
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
                super.onReceivedTitle(view, title);
            }

        });
        webview.loadUrl(url, WebViewUtil.getWebViewHead(getApplicationContext()));
    }

    @OnClick({R.id.iv_back, R.id.tv_close, R.id.iv_right, R.id.yijianchaxuan, R.id.re_share, R.id.re_buy})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (webview.canGoBack()) {
                    webview.goBack();
                } else {
                    finish();
                }
                break;
            case R.id.tv_close:
                finish();
                break;
            case R.id.iv_right:
                if (webview != null) {
                    webview.reload();
                }
                break;
            case R.id.yijianchaxuan:/*一键查询优惠券*/
                /*获取商品头部信息*/
                getShopBasicData();
                break;
            case R.id.re_share:
                if (Constant.COMMON_USER_LEVEL.contains(member_role)) {
                    /*普通用户去升级vip*/
                    intent = new Intent(getApplicationContext(), CommonUserToVIPActivity.class);
                    startActivity(intent);
                } else {
                    /*会员去分享*/
                    /*先调用高佣金接口*/
                    shareGaoYongJinData();
                }
                break;
            case R.id.re_buy:/*购买*/
                if (NetUtil.getNetWorkState(TaoBaoAndTianMaoUrlActivity.this) < 0) {
                    ToastUtils.showToast(getApplicationContext(), "您的网络异常，请联网重试");
                    return;
                }
                /*调用高佣金接口*/
                getGaoYongJinData();
                break;
        }
    }

    /*跳转淘宝链接*/
    String coupon_url;

    /*分享调用高拥接口*/
    private void shareGaoYongJinData() {
        loadingDialog = DialogUtil.createLoadingDialog(TaoBaoAndTianMaoUrlActivity.this, "加载中...");
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        if (!TextUtils.isEmpty(shop_id)) {
            map.put("goods_id", shop_id);
        }
        if (!TextUtils.isEmpty(allNetData.getCoupon_id())) {
            map.put("coupon_id", allNetData.getCoupon_id());
        }
        map.put(Constant.SHOP_REFERER, "activity");
        if (!TextUtils.isEmpty(allNetData.getSource())) {
            map.put(Constant.GAOYONGJIN_SOURCE, allNetData.getSource());
        }
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.GAOYONGIN + "?" + param)
                .addHeader("x-userid", PreferUtils.getString(getApplicationContext(), "member_id"))
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
                                shareGetTaoKouLing(coupon_url);
                            } else {
                                DialogUtil.closeDialog(loadingDialog);
                                String result = jsonObject.getString("result");
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

    /*获取分享淘口令*/
    private void shareGetTaoKouLing(String coupon_click_url) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        map.put("url", coupon_click_url);
        map.put("goods_id", allNetData.getGoods_id());
        map.put("attr_prime", allNetData.getAttr_prime());
        map.put("attr_price", allNetData.getAttr_price());
        map.put("text", allNetData.getGoods_name());
        map.put("logo", allNetData.getGoods_thumb());
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.GETTAOKOULING + "?" + param)
                .tag(this)
                .addHeader("x-userid", PreferUtils.getString(getApplicationContext(), "member_id"))
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
                        Log.i("淘口令", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                String share_taokouling = jsonObject.getString("result");
                                shareGetQrcode(share_taokouling);
                            } else {
                                DialogUtil.closeDialog(loadingDialog);
                                String result = jsonObject.getString("result");
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

    /*获取分享二维码*/
    private void shareGetQrcode(final String share_taokouling) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("type", "goods");
        map.put("words", share_taokouling);
        map.put("attr_prime", allNetData.getAttr_prime());
        map.put("attr_price", allNetData.getAttr_price());
        map.put("platform", "android");
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.ERWEIMAA + "?" + param)
                .tag(this)
                .addHeader("x-userid", PreferUtils.getString(getApplicationContext(), "member_id"))
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
                        DialogUtil.closeDialog(loadingDialog);
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                /*二维码结果*/
                                String meresult = jsonObject.getString("result");
                                intent = new Intent(getApplicationContext(), CreationShareActivity.class);
                                intent.putExtra("goods_thumb", allNetData.getGoods_thumb());
                                intent.putExtra("goods_gallery", allNetData.getGoods_gallery());
                                intent.putExtra("goods_name", allNetData.getGoods_name());
                                intent.putExtra("promo_slogan", allNetData.getCoupon_explain());
                                intent.putExtra("attr_price", allNetData.getAttr_price());
                                intent.putExtra("attr_prime", allNetData.getAttr_prime());
                                intent.putExtra("attr_site", allNetData.getAttr_site());
                                intent.putExtra("good_short", allNetData.getGoods_short());
                                intent.putExtra("attr_ratio", allNetData.getAttr_ratio());
                                intent.putExtra("goods_id", allNetData.getGoods_id());
                                intent.putExtra("share_taokouling", share_taokouling);
                                intent.putExtra("share_qrcode", meresult);
                                intent.putExtra("coupon_surplus", allNetData.getCoupon_surplus());
                                startActivity(intent);
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
                        DialogUtil.closeDialog(loadingDialog);
                    }
                });
    }


    List<AllNetBean.AllNetData> list_taobao;
    AllNetBean.AllNetData allNetData;

    /*商品详情头部信息*/
    private void getShopBasicData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        if (!TextUtils.isEmpty(shop_id)) {
            map.put("goods_id", shop_id);
        }
        String param = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(param + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String mapParam = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.SHOPPING_CART_LIST_DATA + "?" + mapParam)
                .tag(this)
                .addHeader("x-userid", PreferUtils.getString(getApplicationContext(), "member_id"))
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
                        DialogUtil.closeDialog(loadingDialog);
                        Log.i("天猫数据看看", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                AllNetBean bean = GsonUtil.GsonToBean(response.toString(), AllNetBean.class);
                                if (bean == null) return;
                                list_taobao = bean.getResult();
                                if (list_taobao.size() > 0) {
                                    allNetData = list_taobao.get(0);
                                    ll_yijian_view.setVisibility(View.GONE);
                                    ll_action.setVisibility(View.VISIBLE);
                                    /*用户角色显示*/
                                    initUserDataView();
                                } else {
                                    ToastUtils.showToast(getApplicationContext(), "该商品暂无优惠券");
                                    ll_yijian_view.setVisibility(View.VISIBLE);
                                    ll_action.setVisibility(View.GONE);
                                }
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
                        DialogUtil.closeDialog(loadingDialog);
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

    /*用户角色显示*/
    private void initUserDataView() {
        if (Constant.COMMON_USER_LEVEL.contains(member_role)) {
            /*普通用户去升级vip*/
            tv_share.setText("升级VIP");
            tv_buy.setText("领券购买");
        } else {
            /*会员*/
            tv_buy.setText("返佣购买");
            if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
                /*总裁比例*/
                setDataBiLi(90);
            } else if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
                /*合伙人比例*/
                setDataBiLi(70);
            } else if (Constant.VIP_USER_LEVEL.contains(member_role)) {
                /*vip比例*/
                setDataBiLi(40);
            }
        }
    }

    private void setDataBiLi(int num) {
        String attr_price = allNetData.getAttr_price();
        String attr_ratio = allNetData.getAttr_ratio();
        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * app_v * num / 10000;
        BigDecimal bg3 = new BigDecimal(result);
        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        tv_share.setText("分享赚¥" + money);
    }

    /*点击购买调用高佣金接口*/
    private void getGaoYongJinData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        if (!TextUtils.isEmpty(shop_id)) {
            map.put("goods_id", shop_id);
        }
        if (!TextUtils.isEmpty(allNetData.getCoupon_id())) {
            map.put("coupon_id", allNetData.getCoupon_id());
        }
        map.put(Constant.SHOP_REFERER, "activity");
        if (!TextUtils.isEmpty(allNetData.getSource())) {
            map.put(Constant.GAOYONGJIN_SOURCE, allNetData.getSource());
        }
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.GAOYONGIN + "?" + param)
                .addHeader("x-userid", PreferUtils.getString(getApplicationContext(), "member_id"))
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
                                AlibcBasePage page;
                                page = new AlibcPage(coupon_url);
                                HashMap<String, String> exParams = new HashMap<>();
                                exParams.put("isv_code", "appisvcode");
                                exParams.put("alibaba", "阿里巴巴");
                                AlibcTrade.show(TaoBaoAndTianMaoUrlActivity.this, page, alibcShowParams, null, exParams, new AlibcTradeCallback() {
                                    @Override
                                    public void onTradeSuccess(TradeResult tradeResult) {
                                        /*阿里百川进淘宝成功*/
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        /*阿里百川进淘宝失败*/
                                        intent = new Intent(getApplicationContext(), TaoBaoFromUrlToDetailActivity.class);
                                        intent.putExtra("coupon_url", coupon_url);
                                        startActivity(intent);
                                    }
                                });
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
