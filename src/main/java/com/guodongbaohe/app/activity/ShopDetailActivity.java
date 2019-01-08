package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.adapter.login.AlibcLogin;
import com.alibaba.baichuan.android.trade.callback.AlibcLoginCallback;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.bumptech.glide.Glide;
import com.guodongbaohe.app.MainActivity;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.JPAdapter;
import com.guodongbaohe.app.adapter.PicsAdapter;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.bean.ClipBean;
import com.guodongbaohe.app.bean.GaoYongJinBean;
import com.guodongbaohe.app.bean.PhotoAndTextBean;
import com.guodongbaohe.app.bean.RouteBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.port.DemoTradeCallback;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.NetUtil;
import com.guodongbaohe.app.util.NumUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.StringCleanZeroUtil;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.view.CenterAlignImageSpan;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShopDetailActivity extends BigBaseActivity {
    @BindView(R.id.re_yao_zhuanqian)
    RelativeLayout re_yao_zhuanqian;
    @BindView(R.id.tv_yao_zhuanqian)
    TextView tv_yao_zhuanqian;
    /*商品标题*/
    @BindView(R.id.goodname)
    TextView goodname;
    /*商品售价*/
    @BindView(R.id.tv_price)
    TextView tv_price;
    /*商品原价*/
    @BindView(R.id.tv_old_price)
    TextView tv_old_price;
    /*销量*/
    @BindView(R.id.tv_sale_num)
    TextView tv_sale_num;
    /*优惠券值*/
    @BindView(R.id.coupon_money)
    TextView coupon_money;
    /*优惠券使用时间*/
    @BindView(R.id.tv_coupon_time)
    TextView tv_coupon_time;
    /*立即领取按钮*/
    @BindView(R.id.tv_get_coupon)
    TextView tv_get_coupon;
    /*图文详情*/
    @BindView(R.id.re_detail_show)
    RelativeLayout re_detail_show;
    /*旗舰店名字*/
    @BindView(R.id.qijiandian)
    TextView qijiandian;
    /*图文详情列表*/
    @BindView(R.id.recyclerview_pic)
    RecyclerView recyclerview_pic;
    /*精品列表*/
    @BindView(R.id.jp_recycler)
    RecyclerView jp_recycler;
    /*布局标题*/
    @BindView(R.id.nestedscrollview)
    NestedScrollView nestedscrollview;
    /*分享赚按钮*/
    @BindView(R.id.tv_share_money)
    TextView tv_share_money;
    /*购买返按钮*/
    @BindView(R.id.tv_buy)
    TextView tv_buy;
    /*回到首页按钮*/
    @BindView(R.id.to_home)
    RelativeLayout to_home;
    /*总裁优惠券布局*/
    @BindView(R.id.ll_youhuiquan_show)
    LinearLayout ll_youhuiquan_show;
    /*合伙人布局*/
    @BindView(R.id.ll_sjizcai)
    LinearLayout ll_sjizcai;
    /*有下级布局*/
    @BindView(R.id.ll_tishensix)
    LinearLayout ll_tishensix;
    /*没有下级和游客布局*/
    @BindView(R.id.ll_youke)
    LinearLayout ll_youke;
    /*多少元券布局*/
    @BindView(R.id.re_coupon)
    RelativeLayout re_coupon;
    @BindView(R.id.tv_coupon)
    TextView tv_coupon;
    @BindView(R.id.tv_classic_type)
    TextView tv_classic_type;
    Intent intent;
    double app_v;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    boolean isLogin;
    String son_count, member_role, tax_rate;
    String goods_id, cate_route, attr_price, attr_prime, attr_ratio, sales_month, goods_name, goods_short, seller_shop,
            goods_thumb, goods_gallery, coupon_begin, coupon_final, coupon_surplus, coupon_explain, cate_category, attr_site;
    /*标题头部布局*/
    @BindView(R.id.iv_yuanxing_back)
    ImageView iv_yuanxing_back;
    @BindView(R.id.re_title_parent)
    RelativeLayout re_title_parent;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.tv_baobei)
    TextView tv_baobei;
    @BindView(R.id.tv_xiangqing)
    TextView tv_xiangqing;
    @BindView(R.id.tv_tuijian)
    TextView tv_tuijian;
    /*头部信息view*/
    @BindView(R.id.ll_head_view)
    LinearLayout ll_head_view;
    int ll_head_view_height, re_detail_show_height, tv_jingpingtuijian_height;
    /*精品推荐*/
    @BindView(R.id.tv_jingpingtuijian)
    TextView tv_jingpingtuijian;
    int widthPixels;
    /*首页banner*/
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.llpoint)
    LinearLayout llpoint;
    @BindView(R.id.to_top)
    ImageView to_top;
    Dialog loadingDialog;
    @BindView(R.id.tv_youxiaji)
    TextView tv_youxiaji;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopdetailactivity);
        ButterKnife.bind(this);
        loadingDialog = DialogUtil.createLoadingDialog(ShopDetailActivity.this, "加载...");
        widthPixels = getResources().getDisplayMetrics().widthPixels;
        Intent intent = getIntent();
        goods_id = intent.getStringExtra("goods_id");
        cate_route = intent.getStringExtra("cate_route");
        cate_category = intent.getStringExtra("cate_category");
        attr_price = intent.getStringExtra("attr_price");
        attr_prime = intent.getStringExtra("attr_prime");
        attr_ratio = intent.getStringExtra("attr_ratio");
        sales_month = intent.getStringExtra("sales_month");
        goods_name = intent.getStringExtra("goods_name");
        goods_short = intent.getStringExtra("goods_short");
        seller_shop = intent.getStringExtra("seller_shop");
        goods_thumb = intent.getStringExtra("goods_thumb");
        goods_gallery = intent.getStringExtra("goods_gallery");
        coupon_begin = intent.getStringExtra("coupon_begin");
        coupon_final = intent.getStringExtra("coupon_final");
        coupon_surplus = intent.getStringExtra("coupon_surplus");
        coupon_explain = intent.getStringExtra("coupon_explain");
        attr_site = intent.getStringExtra("attr_site");
        String short_title = PreferUtils.getString(getApplicationContext(), "short_title");
        tv_old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        initPicRecycler();
        initScrollView();
        alibcShowParams = new AlibcShowParams(OpenType.Native, true);
        /*初始化商品信息*/
        initGoodHeadView();
        /*图文详情接口*/
        getPhotoTextData();
        /*精品推荐*/
        if (Double.valueOf(cate_category) > 0) {
            getRouteData();
        }
        /*获取view的高度*/
        getViewHight();
        /*bannerView*/
        initBannerView();
        /*复制标题操作*/
        initEditView();
        if (!TextUtils.isEmpty(short_title)) {
            tv_yao_zhuanqian.setText(short_title);
        }
    }

    private void initEditView() {
        goodname.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipBean clipBean = new ClipBean();
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", goods_name);
                cm.setPrimaryClip(mClipData);
                ToastUtils.showToast(getApplicationContext(), "标题复制成功");
                clipBean.setTitle(goods_name);
                clipBean.save();
                return false;
            }
        });
    }

    private List<String> bannerList = new ArrayList<>();
    private ImageView[] indicators;

    private void initBannerView() {
        if (TextUtils.isEmpty(goods_gallery)) {
            bannerList.add(goods_thumb);
        } else {
            String[] split = goods_gallery.split(",");
            for (int i = 0; i < split.length; i++) {
                bannerList.add(split[i]);
            }
        }
        indicators = new ImageView[bannerList.size()];
        for (int i = 0; i < bannerList.size(); i++) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_cycle_viewpager_indicator, null);
            ImageView iv = (ImageView) view.findViewById(R.id.image_indicator);
            indicators[i] = iv;
            llpoint.addView(view);
        }
        setIndicator(0);
        viewpager.setAdapter(new GuideAdapter());
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void getViewHight() {
        ViewTreeObserver viewTreeObserver = ll_head_view.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ll_head_view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ll_head_view_height = ll_head_view.getHeight();
            }
        });
        ViewTreeObserver re_detail_show_observe = re_detail_show.getViewTreeObserver();
        re_detail_show_observe.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                re_detail_show.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                re_detail_show_height = re_detail_show.getHeight();
            }
        });
        ViewTreeObserver tv_jingpingtuijian_observe = tv_jingpingtuijian.getViewTreeObserver();
        tv_jingpingtuijian_observe.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tv_jingpingtuijian.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                tv_jingpingtuijian_height = tv_jingpingtuijian.getHeight();
            }
        });
    }

    private void initGoodHeadView() {
        setVerticalCenterIconSpan(goods_name);
        StringCleanZeroUtil.StringFormat(attr_price, tv_price);
        StringCleanZeroUtil.StringFormatWithYuan(attr_prime, tv_old_price);
        tv_sale_num.setText("月销" + NumUtil.getNum(sales_month) + "件");
        setSellShopName(seller_shop);
    }

    private void setSellShopName(CharSequence charSequence) {
        String text = "[icon] " + charSequence;
        SpannableString spannable = new SpannableString(text);
        Drawable drawable;
        if (attr_site.equals("tmall")) {
            drawable = this.getResources().getDrawable(R.drawable.tianmao_site);
        } else {
            drawable = this.getResources().getDrawable(R.drawable.taobao_site);
        }
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        //图片居中
        CenterAlignImageSpan imageSpan = new CenterAlignImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        spannable.setSpan(imageSpan, 0, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        qijiandian.setText(spannable);
    }

    private void setVerticalCenterIconSpan(CharSequence charSequence) {
        String text = "[icon] " + charSequence;
        SpannableString spannable = new SpannableString(text);
        Drawable drawable;
        if (attr_site.equals("tmall")) {
            drawable = this.getResources().getDrawable(R.drawable.tianmao_site);
        } else {
            drawable = this.getResources().getDrawable(R.drawable.taobao_site);
        }
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        //图片居中
        CenterAlignImageSpan imageSpan = new CenterAlignImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        spannable.setSpan(imageSpan, 0, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        goodname.setText(spannable);
    }

    @Override
    protected void onResume() {
        tax_rate = PreferUtils.getString(getApplicationContext(), "tax_rate");
        isLogin = PreferUtils.getBoolean(getApplicationContext(), "isLogin");
        son_count = PreferUtils.getString(getApplicationContext(), "son_count");
        member_role = PreferUtils.getString(getApplicationContext(), "member_role");
        app_v = 1 - Double.valueOf(tax_rate);
        /*优惠券显示view*/
        initCouponView();
        super.onResume();
        /*获取剪切板内容*/
        getClipContent();
    }

    Dialog dialog;

    private void getClipContent() {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData data = cm.getPrimaryClip();
        if (data == null) return;
        ClipData.Item item = data.getItemAt(0);
        final String content = item.coerceToText(getApplicationContext()).toString();
        if (TextUtils.isEmpty(content)) return;
        boolean isFirstClip = PreferUtils.getBoolean(getApplicationContext(), "isFirstClip");
        if (!isFirstClip) {
            showClipDialog(content);
        } else {
            String clip_content = PreferUtils.getString(getApplicationContext(), "clip_content");
            if (clip_content.equals(content)) return;
            showClipDialog(content);
        }
        PreferUtils.putBoolean(getApplicationContext(), "isFirstClip", true);
    }

    private void showClipDialog(final String content) {
        PreferUtils.putString(getApplicationContext(), "clip_content", content);
        List<ClipBean> all = LitePal.findAll(ClipBean.class);
        if (all == null) return;
        for (ClipBean bean : all) {
            if (bean.getTitle().equals(content)) {
                return;
            }
        }
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new Dialog(ShopDetailActivity.this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(R.layout.clip_search_dialog);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView sure = (TextView) dialog.findViewById(R.id.sure);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        TextView title = (TextView) dialog.findViewById(R.id.content);
        title.setText(content);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra("keyword", content);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    BigDecimal bg;

    private void initCouponView() {
        double v = Double.valueOf(attr_prime) - Double.valueOf(attr_price);
        bg = new BigDecimal(v);
        double youhuiquan = bg.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (Double.valueOf(coupon_surplus) > 0) {
            tv_classic_type.setText("券后");
        } else {
            if (v > 0) {
                tv_classic_type.setText("折后");
            } else {
                tv_classic_type.setText("特惠价");
            }
        }
        if (isLogin) {
            tv_coupon.setText(StringCleanZeroUtil.DoubleFormat(youhuiquan) + "元券");
            if (member_role.equals("2")) {
                /*总裁级别*/
                /*优惠券显示*/
                re_coupon.setVisibility(View.GONE);
                ll_youke.setVisibility(View.GONE);
                ll_tishensix.setVisibility(View.GONE);
                ll_sjizcai.setVisibility(View.GONE);
                ll_youhuiquan_show.setVisibility(View.VISIBLE);
                if (Double.valueOf(coupon_surplus) > 0) {/*有券显示*/
                    if (TextUtils.isEmpty(coupon_begin) || TextUtils.isEmpty(coupon_final)) {
                        return;
                    }
                    String start_time = coupon_begin.substring(0, 4) + "." + coupon_begin.substring(4, 6) + "." + coupon_begin.substring(6, 8);
                    String end_time = coupon_final.substring(0, 4) + "." + coupon_final.substring(4, 6) + "." + coupon_final.substring(6, 8);
                    coupon_money.setText(StringCleanZeroUtil.DoubleFormat(youhuiquan) + "元优惠券");
                    tv_coupon_time.setText("有效期：" + start_time + "-" + end_time);
                    tv_get_coupon.setText("立即领取");
                } else {
                    /*折扣显示*/
                    if (v > 0) {
                        bg = new BigDecimal(Double.valueOf(attr_price) / Double.valueOf(attr_prime) * 10);
                        double zhekou = bg.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                        coupon_money.setText(zhekou + "折");
                        tv_coupon_time.setText("使用期限:商品优惠时间解释权归店铺所有");
                        tv_get_coupon.setText("立即抢购");
                    } else {
                        coupon_money.setText("特惠价" + attr_price + "元");
                        tv_coupon_time.setText("使用期限:商品优惠时间解释权归店铺所有");
                        tv_get_coupon.setText("立即抢购");
                    }
                }
            } else if (member_role.equals("1")) {
                /*合伙人升总裁*/
                if (Double.valueOf(coupon_surplus) > 0) {
                    re_coupon.setVisibility(View.VISIBLE);
                } else {
                    re_coupon.setVisibility(View.GONE);
                }
                ll_sjizcai.setVisibility(View.VISIBLE);
                ll_youke.setVisibility(View.GONE);
                ll_tishensix.setVisibility(View.GONE);
                ll_youhuiquan_show.setVisibility(View.GONE);
            } else {
                if (son_count.equals("0")) {
                    /*没有下级*/
                    if (Double.valueOf(coupon_surplus) > 0) {
                        re_coupon.setVisibility(View.VISIBLE);
                    } else {
                        re_coupon.setVisibility(View.GONE);
                    }
                    ll_youke.setVisibility(View.VISIBLE);
                    ll_tishensix.setVisibility(View.GONE);
                    ll_sjizcai.setVisibility(View.GONE);
                    ll_youhuiquan_show.setVisibility(View.GONE);
                } else {
                    /*有下级*/
                    if (Double.valueOf(coupon_surplus) > 0) {
                        re_coupon.setVisibility(View.VISIBLE);
                    } else {
                        re_coupon.setVisibility(View.GONE);
                    }
                    ll_tishensix.setVisibility(View.VISIBLE);
                    ll_youke.setVisibility(View.GONE);
                    ll_sjizcai.setVisibility(View.GONE);
                    ll_youhuiquan_show.setVisibility(View.GONE);
                    double sj_result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 82 / 10000 * app_v;
                    BigDecimal bg3 = new BigDecimal(sj_result);
                    double sj_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    tv_youxiaji.setText("成为合伙人赚¥" + sj_money);
                }
            }
        } else {
            tv_coupon.setText(StringCleanZeroUtil.DoubleFormat(youhuiquan) + "元券");
            if (Double.valueOf(coupon_surplus) > 0) {
                re_coupon.setVisibility(View.VISIBLE);
            } else {
                re_coupon.setVisibility(View.GONE);
            }
            ll_youke.setVisibility(View.VISIBLE);
            ll_tishensix.setVisibility(View.GONE);
            ll_sjizcai.setVisibility(View.GONE);
            ll_youhuiquan_show.setVisibility(View.GONE);
        }

        if (isLogin) {
            if (member_role.equals("2")) {
                /*总裁比例*/
                setDataBiLi(90);
            } else if (member_role.equals("1")) {
                /*合伙人比例*/
                setDataBiLi(82);
            } else {
                if (son_count.equals("0")) {
                    /*没有下级比例*/
                    setDataBiLi(40);
                } else {
                    /*有下级比例*/
                    setDataBiLi(50);
                }
            }
        } else {
            /*没有下级比例*/
            setDataBiLi(40);
        }
    }

    private void getPhotoTextData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("goods_id", goods_id);
        String param = ParamUtil.getMapParam(map);
        Log.i("图文详情", Constant.BASE_URL + "goods/detail" + "?" + param);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + "goods/detail" + "?" + param)
                .tag(this)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getApplicationContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getApplicationContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getApplicationContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getApplicationContext(), ""))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("图文详情然后", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                PhotoAndTextBean photoAndTextBean = GsonUtil.GsonToBean(response.toString(), PhotoAndTextBean.class);
                                if (photoAndTextBean == null) return;
                                PhotoAndTextBean.DetailObj detail = photoAndTextBean.getResult();
                                if (detail.getDetail().size() > 0) {
                                    list_detail = detail.getDetail();
                                    for (int i = 0; i < list_detail.size(); i++) {
                                        if (list_detail.get(i).substring(list_detail.get(i).length() - 3, list_detail.get(i).length()).equals("gif")) {
                                            list_detail.remove(i);
                                        }
                                    }
                                    PicsAdapter picsAdapter = new PicsAdapter(list_detail);
                                    recyclerview_pic.setAdapter(picsAdapter);
                                    DialogUtil.closeDialog(loadingDialog);
                                } else {
                                    DialogUtil.closeDialog(loadingDialog);
                                }
                            } else {
                                DialogUtil.closeDialog(loadingDialog);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        DialogUtil.closeDialog(loadingDialog);
                    }
                });
    }

    private void getRouteData() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("goods_id", goods_id);
        map.put("route", cate_route);
        map.put("category", cate_category);
        String param = ParamUtil.getMapParam(map);
        Log.i("相关商品", Constant.BASE_URL + "goods/related" + "?" + param);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + "goods/related" + "?" + param)
                .tag(this)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getApplicationContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getApplicationContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getApplicationContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getApplicationContext(), ""))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("相关商品", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                RouteBean routeBean = GsonUtil.GsonToBean(response.toString(), RouteBean.class);
                                if (routeBean == null) return;
                                final List<RouteBean.RouteData> result = routeBean.getResult();
                                for (RouteBean.RouteData bean : result) {
                                    bean.setLogin(isLogin);
                                    bean.setSon_count(son_count);
                                    bean.setMember_role(member_role);
                                }
                                JPAdapter jpAdapter = new JPAdapter(getApplicationContext(), result);
                                jp_recycler.setAdapter(jpAdapter);
                                jpAdapter.setonclicklistener(new OnItemClick() {
                                    @Override
                                    public void OnItemClickListener(View view, int pos) {
                                        intent = new Intent(getApplicationContext(), ShopDetailActivity.class);
                                        intent.putExtra("goods_id", result.get(pos).getGoods_id());
                                        intent.putExtra("cate_route", result.get(pos).getCate_route());/*类目名称*/
                                        intent.putExtra("cate_category", result.get(pos).getCate_category());/*类目id*/
                                        intent.putExtra("attr_price", result.get(pos).getAttr_price());
                                        intent.putExtra("attr_prime", result.get(pos).getAttr_prime());
                                        intent.putExtra("attr_ratio", result.get(pos).getAttr_ratio());
                                        intent.putExtra("sales_month", result.get(pos).getSales_month());
                                        intent.putExtra("goods_name", result.get(pos).getGoods_name());/*长标题*/
                                        intent.putExtra("goods_short", result.get(pos).getGoods_short());/*短标题*/
                                        intent.putExtra("seller_shop", result.get(pos).getSeller_shop());/*店铺姓名*/
                                        intent.putExtra("goods_thumb", result.get(pos).getGoods_thumb());/*单图*/
                                        intent.putExtra("goods_gallery", result.get(pos).getGoods_gallery());/*多图*/
                                        intent.putExtra("coupon_begin", result.get(pos).getCoupon_begin());/*开始时间*/
                                        intent.putExtra("coupon_final", result.get(pos).getCoupon_final());/*结束时间*/
                                        intent.putExtra("coupon_surplus", result.get(pos).getCoupon_surplus());/*是否有券*/
                                        intent.putExtra("coupon_explain", result.get(pos).getGoods_slogan());/*推荐理由*/
                                        intent.putExtra("attr_site", result.get(pos).getAttr_site());
                                        startActivity(intent);
                                    }
                                });
                                /*精品推荐end*/
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

    int parentHeight;

    private void initScrollView() {
        ViewTreeObserver viewTreeObserver = re_title_parent.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                re_title_parent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                parentHeight = re_title_parent.getHeight();
                nestedscrollview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        if (scrollY <= 0) {
                            //透明
                            re_title_parent.setBackgroundColor(Color.argb((int) 0, 227, 29, 26));
                            iv_yuanxing_back.getBackground().setAlpha(180);
                            iv_yuanxing_back.setVisibility(View.VISIBLE);
                            re_title_parent.setVisibility(View.GONE);
                        } else if (scrollY > 0 && scrollY <= parentHeight) {
                            float scale = (float) scrollY / parentHeight;
                            float alpha = (255 * scale);
                            // 只是layout背景透明
                            re_title_parent.setBackgroundColor(Color.argb((int) alpha, 26, 26, 26));
                            re_title_parent.setVisibility(View.VISIBLE);
                            iv_yuanxing_back.setVisibility(View.GONE);
                        } else {
                            iv_yuanxing_back.setVisibility(View.GONE);
                            re_title_parent.setVisibility(View.VISIBLE);
                            re_title_parent.setBackgroundColor(Color.argb((int) 255, 26, 26, 26));
                        }
                        if (scrollY > 1300) {
                            to_top.setVisibility(View.VISIBLE);
                        } else {
                            to_top.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    @OnClick({R.id.iv_back, R.id.tv_buy, R.id.tv_share_money, R.id.ll_youke, R.id.tv_tuijian, R.id.tv_baobei, R.id.re_yao_zhuanqian,
            R.id.ll_youhuiquan_show, R.id.ll_sjizcai, R.id.ll_tishensix, R.id.to_home, R.id.tv_xiangqing, R.id.iv_yuanxing_back, R.id.to_top})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_buy:/*购买返*/
                if (NetUtil.getNetWorkState(ShopDetailActivity.this) < 0) {
                    ToastUtils.showToast(getApplicationContext(), "您的网络异常，请联网重试");
                    return;
                }
                if (PreferUtils.getBoolean(getApplicationContext(), "isLogin")) {
                    /*判断淘宝是否授权*/
                    AlibcLogin alibcLogin = AlibcLogin.getInstance();
                    alibcLogin.showLogin(ShopDetailActivity.this, new AlibcLoginCallback() {

                        @Override
                        public void onSuccess() {
                            /*调用高佣金接口*/
                            getGaoYongJinData();
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            ToastUtils.showToast(ShopDetailActivity.this, "淘宝授权失败");
                        }
                    });
                } else {
                    intent = new Intent(getApplicationContext(), LoginAndRegisterActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.tv_share_money:/*分享赚*/
                if (NetUtil.getNetWorkState(ShopDetailActivity.this) < 0) {
                    ToastUtils.showToast(getApplicationContext(), "您的网络异常，请联网重试");
                    return;
                }
                if (PreferUtils.getBoolean(getApplicationContext(), "isLogin")) {
                    intent = new Intent(getApplicationContext(), CreationShareActivity.class);
                    intent.putExtra("goods_thumb", goods_thumb);
                    intent.putExtra("goods_gallery", goods_gallery);
                    intent.putExtra("goods_name", goods_name);
                    intent.putExtra("promo_slogan", coupon_explain);
                    intent.putExtra("attr_price", attr_price);
                    intent.putExtra("attr_prime", attr_prime);
                    intent.putExtra("attr_site", attr_site);
                    intent.putExtra("good_short", goods_short);
                    intent.putExtra("attr_ratio", attr_ratio);
                    intent.putExtra("goods_id", goods_id);
                    startActivity(intent);
                } else {
                    intent = new Intent(getApplicationContext(), LoginAndRegisterActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.ll_youke:/*游客或者没有下级*/
                toMakeMoneyFragment();
                break;
            case R.id.ll_youhuiquan_show:/*总裁优惠券显示*/
                if (PreferUtils.getBoolean(getApplicationContext(), "isLogin")) {
                    /*调用高佣金接口*/
                    getGaoYongJinData();
                } else {
                    intent = new Intent(getApplicationContext(), LoginAndRegisterActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.ll_sjizcai:/*合伙人显示*/
                toMakeMoneyFragment();
                break;
            case R.id.ll_tishensix:/*有下级显示*/
                toMakeMoneyFragment();
                break;
            case R.id.to_home:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("loginout", "loginout");
                startActivity(intent);
                finish();
                break;
            case R.id.tv_xiangqing:
                tv_baobei.setTextColor(0xffffffff);
                tv_xiangqing.setTextColor(0xfff6c15b);
                tv_tuijian.setTextColor(0xffffffff);
                nestedscrollview.scrollTo(0, ll_head_view.getBottom() - re_detail_show_height);
                break;
            case R.id.tv_tuijian:
                tv_baobei.setTextColor(0xffffffff);
                tv_xiangqing.setTextColor(0xffffffff);
                tv_tuijian.setTextColor(0xfff6c15b);
                nestedscrollview.scrollTo(0, recyclerview_pic.getBottom() - tv_jingpingtuijian_height);
                break;
            case R.id.tv_baobei:
                tv_baobei.setTextColor(0xfff6c15b);
                tv_xiangqing.setTextColor(0xffffffff);
                tv_tuijian.setTextColor(0xffffffff);
                nestedscrollview.scrollTo(0, ll_head_view.getTop());
                break;
            case R.id.iv_yuanxing_back:
                finish();
                break;
            case R.id.re_yao_zhuanqian:
                if (PreferUtils.getBoolean(getApplicationContext(), "isLogin")) {
                    intent = new Intent(getApplicationContext(), YaoQingFriendActivity.class);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginAndRegisterActivity.class));
                }
                break;
            case R.id.to_top:
                nestedscrollview.scrollTo(0, 0);
                break;
        }
    }

    /*跳转淘宝链接*/
    String coupon_url;

    private void getGaoYongJinData() {
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
                                AlibcTrade.show(ShopDetailActivity.this, page, alibcShowParams, null, exParams, new DemoTradeCallback());
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

    private void initPicRecycler() {
        recyclerview_pic.setHasFixedSize(true);
        recyclerview_pic.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerview_pic.setNestedScrollingEnabled(false);
        jp_recycler.setHasFixedSize(true);
        jp_recycler.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        jp_recycler.setNestedScrollingEnabled(false);
    }

    List<String> list_detail;

    private void setDataBiLi(int num) {
        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * app_v * num / 10000;
        BigDecimal bg3 = new BigDecimal(result);
        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        tv_share_money.setText("分享赚¥" + money);
        tv_buy.setText("购买返¥" + money);
    }

    /*设置指示器*/
    private void setIndicator(int selectedPosition) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(R.drawable.indicator_unselected1);
        }
        indicators[selectedPosition % indicators.length].setBackgroundResource(R.drawable.indicator_selected1);
    }

    private class GuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return bannerList == null ? 0 : Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.guide_item, container, false);
            ImageView iv = (ImageView) view.findViewById(R.id.iv);
            Glide.with(getApplicationContext()).load(bannerList.get(position % bannerList.size())).into(iv);
            container.addView(view);
            return view;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlibcTradeSDK.destory();
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
