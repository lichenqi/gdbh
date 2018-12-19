package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.bumptech.glide.Glide;
import com.guodongbaohe.app.MainActivity;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.PicsAdapter;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.GaoYongJinBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.port.DemoTradeCallback;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.NumUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AllNetSerachShopdetailActivity extends BaseActivity {
    ArrayList<String> imgesList;
    String short_title, pict_url, old_price, sale_price, sale_num, ratio, shopid, start_time, end_time, shop_url, shop_tile;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.recyclerview_pic)
    RecyclerView recyclerview_pic;
    /*券后价*/
    @BindView(R.id.tv_price)
    TextView tv_price;
    /*原价*/
    @BindView(R.id.tv_old_price)
    TextView tv_old_price;
    /*销量*/
    @BindView(R.id.tv_sale_num)
    TextView tv_sale_num;
    /*优惠券布局*/
    @BindView(R.id.re_coupon)
    RelativeLayout re_coupon;
    @BindView(R.id.tv_coupon)
    TextView tv_coupon;
    /*商品标题*/
    @BindView(R.id.goodname)
    TextView goodname;
    /*没有下级的布局*/
    @BindView(R.id.ll_youke)
    LinearLayout ll_common_buju;
    /*vip用户*/
    @BindView(R.id.ll_tishensix)
    LinearLayout ll_vip_buju;
    /*合伙人布局*/
    @BindView(R.id.ll_sjizcai)
    LinearLayout ll_hehuoren_buju;
    /*总裁布局*/
    @BindView(R.id.ll_youhuiquan_show)
    LinearLayout ll_zcai_buju;
    @BindView(R.id.coupon_money)
    TextView coupon_money;
    @BindView(R.id.tv_coupon_time)
    TextView tv_coupon_time;
    /*分享赚*/
    @BindView(R.id.tv_share_money)
    TextView tv_share_money;
    /*【购买返*/
    @BindView(R.id.tv_buy)
    TextView tv_buy;
    private String goods_gallery = "";
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native

    @Override
    public int getContainerView() {
        return R.layout.allnetserachshopdetailactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        shop_tile = intent.getStringExtra("shop_tile");
        short_title = intent.getStringExtra("short_title");
        pict_url = intent.getStringExtra("pict_url");
        old_price = intent.getStringExtra("old_price");
        sale_price = intent.getStringExtra("sale_price");
        sale_num = intent.getStringExtra("sale_num");
        ratio = intent.getStringExtra("ratio");
        shopid = intent.getStringExtra("shopid");
        imgesList = intent.getStringArrayListExtra("imgesList");
        start_time = intent.getStringExtra("start_time");
        end_time = intent.getStringExtra("end_time");
        shop_url = intent.getStringExtra("shop_url");
        tax_rate = PreferUtils.getString(getApplicationContext(), "tax_rate");
        son_count = PreferUtils.getString(getApplicationContext(), "son_count");
        member_role = PreferUtils.getString(getApplicationContext(), "member_role");
        app_v = 1 - Double.valueOf(tax_rate);
        setMiddleTitle(short_title);
        initView();
        initShopdetailRecycler();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < imgesList.size(); i++) {
            sb.append(imgesList.get(i)).append(",");
        }
        goods_gallery = sb.toString().substring(0, sb.toString().length() - 1);
        alibcShowParams = new AlibcShowParams(OpenType.Native, true);
    }

    @OnClick({R.id.ll_youke, R.id.ll_youhuiquan_show, R.id.ll_sjizcai, R.id.ll_tishensix, R.id.tv_share_money, R.id.tv_buy})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_youke:
                toMakeMoneyFragment();
                break;
            case R.id.ll_youhuiquan_show:/*总裁优惠券显示*/
                toTaoBao();
                break;
            case R.id.ll_sjizcai:/*合伙人显示*/
                toMakeMoneyFragment();
                break;
            case R.id.ll_tishensix:/*有下级显示*/
                toMakeMoneyFragment();
                break;
            case R.id.tv_share_money:/*分享赚*/
                if (PreferUtils.getBoolean(getApplicationContext(), "isLogin")) {
                    intent = new Intent(getApplicationContext(), CreationShareActivity.class);
                    intent.putExtra("goods_thumb", "");
                    intent.putExtra("goods_gallery", goods_gallery);
                    intent.putExtra("goods_name", short_title);
                    intent.putExtra("promo_slogan", shop_tile);
                    intent.putExtra("attr_price", sale_price);
                    intent.putExtra("attr_prime", old_price);
                    intent.putExtra("coupon_url", shop_url);
                    intent.putExtra("attr_site", "");
                    intent.putExtra("good_short", short_title);
                    intent.putExtra("attr_ratio", ratio);
                    intent.putExtra("goods_id", shopid);
                    startActivity(intent);
                } else {
                    intent = new Intent(getApplicationContext(), LoginAndRegisterActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.tv_buy:
                toTaoBao();
                break;
        }
    }

    private void toTaoBao() {
        long timelineStr = System.currentTimeMillis() / 1000;
        HashMap<String, String> map = new HashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        map.put("goods_id", shopid);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.GAOYONGIN + "?" + param)
                .addHeader("APPID", Constant.APPID)
                .addHeader("x-userid", PreferUtils.getString(getApplicationContext(), "member_id"))
                .addHeader("VERSION", VersionUtil.getVersionCode(getApplicationContext()))
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
                                String coupon_click_url = bean.getResult().getCoupon_click_url();
                                AlibcBasePage page;
                                if (TextUtils.isEmpty(coupon_click_url)) {
                                    page = new AlibcPage(shop_url);
                                } else {
                                    page = new AlibcPage(coupon_click_url);
                                }
                                HashMap<String, String> exParams = new HashMap<>();
                                exParams.put("isv_code", "appisvcode");
                                exParams.put("alibaba", "阿里巴巴");
                                AlibcTrade.show(AllNetSerachShopdetailActivity.this, page, alibcShowParams, null, exParams, new DemoTradeCallback());
                            } else {
                                ToastUtils.showToast(getApplicationContext(), Constant.NONET);
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

    String tax_rate, son_count, member_role;
    double app_v;

    private void initShopdetailRecycler() {
        recyclerview_pic.setHasFixedSize(true);
        recyclerview_pic.setNestedScrollingEnabled(false);
        recyclerview_pic.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        for (int i = 0; i < imgesList.size(); i++) {
            if (imgesList.get(i).substring(imgesList.get(i).length() - 3, imgesList.get(i).length()).equals("gif")) {
                imgesList.remove(i);
            }
        }
        PicsAdapter picsAdapter = new PicsAdapter(imgesList);
        recyclerview_pic.setAdapter(picsAdapter);
    }

    private void initView() {
        tv_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        Glide.with(getApplicationContext()).load(pict_url).placeholder(R.drawable.loading_img).into(iv);
        tv_price.setText(sale_price);
        tv_old_price.setText("¥" + old_price);
        tv_sale_num.setText("月销" + NumUtil.getNum(sale_num) + "件");
        goodname.setText(short_title);
        setCouponPriceView();
        if (member_role.equals("2")) {
            re_coupon.setVisibility(View.GONE);
            ll_common_buju.setVisibility(View.GONE);
            ll_vip_buju.setVisibility(View.GONE);
            ll_hehuoren_buju.setVisibility(View.GONE);
            ll_zcai_buju.setVisibility(View.VISIBLE);
            /*总裁比例*/
            setDataBiLi(90);
        } else if (member_role.equals("1")) {
            re_coupon.setVisibility(View.VISIBLE);

            ll_common_buju.setVisibility(View.GONE);
            ll_vip_buju.setVisibility(View.GONE);
            ll_hehuoren_buju.setVisibility(View.VISIBLE);
            ll_zcai_buju.setVisibility(View.GONE);
            setDataBiLi(82);
        } else {
            if (son_count.equals("0")) {
                re_coupon.setVisibility(View.VISIBLE);

                ll_common_buju.setVisibility(View.VISIBLE);
                ll_vip_buju.setVisibility(View.GONE);
                ll_hehuoren_buju.setVisibility(View.GONE);
                ll_zcai_buju.setVisibility(View.GONE);
                setDataBiLi(40);
            } else {
                re_coupon.setVisibility(View.VISIBLE);

                ll_common_buju.setVisibility(View.GONE);
                ll_vip_buju.setVisibility(View.VISIBLE);
                ll_hehuoren_buju.setVisibility(View.GONE);
                ll_zcai_buju.setVisibility(View.GONE);
                setDataBiLi(50);
            }
        }
    }

    private void setCouponPriceView() {
        double v = Double.valueOf(old_price) - Double.valueOf(sale_price);
        BigDecimal bg = new BigDecimal(v);
        double coupon_moeny = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        tv_coupon.setText(coupon_moeny + "元券");
        tv_coupon_time.setText("有效期:" + start_time + " 至 " + end_time);
        coupon_money.setText(coupon_moeny + "元优惠券");
    }

    private void setDataBiLi(int num) {
        double result = Double.valueOf(sale_price) * Double.valueOf(ratio) * app_v * num / 10000;
        BigDecimal bg3 = new BigDecimal(result);
        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        tv_share_money.setText("分享赚¥" + money);
        tv_buy.setText("购买返¥" + money);
    }

    Intent intent;

    private void toMakeMoneyFragment() {
        if (PreferUtils.getBoolean(getApplicationContext(), "isLogin")) {
            intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("shopdetail_upgrade", "shopdetail_upgrade");
            startActivity(intent);
            finish();
        } else {
            intent = new Intent(getApplicationContext(), LoginAndRegisterActivity.class);
            startActivity(intent);
        }
    }

}
