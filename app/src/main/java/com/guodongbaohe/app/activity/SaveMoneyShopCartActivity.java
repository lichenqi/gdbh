package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.ShopCartAdapter;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.AllNetBean;
import com.guodongbaohe.app.bean.GaoYongJinBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.port.DemoTradeCallback;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.StringCleanZeroUtil;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SaveMoneyShopCartActivity extends BaseActivity {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.tv_youhuiquan_zhang)
    TextView tv_youhuiquan_zhang;
    @BindView(R.id.tv_shengqian)
    TextView tv_shengqian;
    @BindView(R.id.tv_make_yuan)
    TextView tv_make_yuan;
    @BindView(R.id.re_keweinizhuan)
    RelativeLayout re_keweinizhuan;
    String shop_ids, member_role;
    private int coupon_num = 0;
    private double coupon_money = 0;
    private double make_money = 0;
    double app_v;
    private BigDecimal bg;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    Dialog loadingDialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogUtil.closeDialog(loadingDialog);
    }

    @Override
    public int getContainerView() {
        return R.layout.savemoneyshopcartactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("省钱购物车");
        loadingDialog = DialogUtil.createLoadingDialog(SaveMoneyShopCartActivity.this, "加载...");
        shop_ids = PreferUtils.getString(getApplicationContext(), "shop_ids");
        member_role = PreferUtils.getString(getApplicationContext(), "member_role");
        String tax_rate = PreferUtils.getString(getApplicationContext(), "tax_rate");
        app_v = 1 - Double.valueOf(tax_rate);
        alibcShowParams = new AlibcShowParams(OpenType.Native, true);
        getListData();
        initRecyclerview();
    }

    private void initRecyclerview() {
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
            re_keweinizhuan.setVisibility(View.VISIBLE);
        } else if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
            re_keweinizhuan.setVisibility(View.VISIBLE);
        } else if (Constant.VIP_USER_LEVEL.contains(member_role)) {
            re_keweinizhuan.setVisibility(View.VISIBLE);
        } else {
            re_keweinizhuan.setVisibility(View.GONE);
        }
    }

    /*查询数据*/
    private void getListData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        if (!TextUtils.isEmpty(shop_ids)) {
            map.put("goods_id", shop_ids);
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
                        Log.i("数据看看", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                AllNetBean bean = GsonUtil.GsonToBean(response.toString(), AllNetBean.class);
                                if (bean == null) return;
                                final List<AllNetBean.AllNetData> list = bean.getResult();
                                if (list.size() > 0) {
                                    for (AllNetBean.AllNetData listData : list) {
                                        listData.setMember_role(member_role);
                                        if (Double.valueOf(listData.getCoupon_surplus()) > 0) {
                                            /*优惠券张数*/
                                            coupon_num++;
                                            /*可为你省钱*/
                                            double d_price = Double.valueOf(listData.getAttr_prime()) - Double.valueOf(listData.getAttr_price());
                                            BigDecimal bg3 = new BigDecimal(d_price);
                                            double d_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                            coupon_money += d_money;
                                        }
                                        /*可为你赚钱*/
                                        double result = Double.valueOf(listData.getAttr_price()) * Double.valueOf(listData.getAttr_ratio());
                                        BigDecimal bg2 = new BigDecimal(result);
                                        double money = bg2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                        make_money += money;
                                    }
                                    ShopCartAdapter shopCartAdapter = new ShopCartAdapter(getApplicationContext(), list);
                                    recyclerview.setAdapter(shopCartAdapter);
                                    tv_youhuiquan_zhang.setText(coupon_num + "");
                                    tv_shengqian.setText(StringCleanZeroUtil.DoubleFormat(coupon_money));
                                    if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
                                        calculate(90);
                                    } else if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
                                        calculate(70);
                                    } else if (Constant.VIP_USER_LEVEL.contains(member_role)) {
                                        calculate(40);
                                    } else {
                                        tv_make_yuan.setText("0");
                                    }
                                    DialogUtil.closeDialog(loadingDialog);
                                    /*adapter点击事件*/
                                    shopCartAdapter.setonclicklistener(new OnItemClick() {
                                        @Override
                                        public void OnItemClickListener(View view, int position) {
                                            String goods_id = list.get(position).getGoods_id();
                                            String coupon_id = list.get(position).getCoupon_id();
                                            /*调用高佣金接口*/
                                            getGaoYongJinData(goods_id, coupon_id);
                                        }
                                    });
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
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

    /*跳转淘宝链接*/
    String coupon_url;

    private void getGaoYongJinData(String goods_id, String coupon_id) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        if (!TextUtils.isEmpty(goods_id)) {
            map.put("goods_id", goods_id);
        }
        if (!TextUtils.isEmpty(coupon_id)) {
            map.put("coupon_id", coupon_id);
        }
        map.put(Constant.SHOP_REFERER, "cart");
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
                                AlibcTrade.show(SaveMoneyShopCartActivity.this, page, alibcShowParams, null, exParams, new DemoTradeCallback());
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


    private void calculate(int num) {
        double result = make_money * num / 10000 * app_v;
        bg = new BigDecimal(result);
        double money = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        tv_make_yuan.setText(money + "");
    }

}
