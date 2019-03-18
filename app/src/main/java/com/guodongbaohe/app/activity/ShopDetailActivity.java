package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.alibaba.baichuan.android.trade.callback.AlibcTradeCallback;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.model.TradeResult;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.guodongbaohe.app.MainActivity;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.JPAdapter;
import com.guodongbaohe.app.adapter.PicsAdapter;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.bean.BaseUserBean;
import com.guodongbaohe.app.bean.GaoYongJinBean;
import com.guodongbaohe.app.bean.PhotoAndTextBean;
import com.guodongbaohe.app.bean.RouteBean;
import com.guodongbaohe.app.bean.ShopIsCollectBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.ClipContentUtil;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

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
    /*多少元券布局*/
    @BindView(R.id.tv_classic_type)
    TextView tv_classic_type;
    Intent intent;
    double app_v;
    private AlibcShowParams alibcShowParams;//页面打开方式，默认，H5，Native
    boolean isLogin;
    String son_count, member_role, tax_rate;
    String goods_id, cate_route, attr_price, attr_prime, attr_ratio, sales_month, goods_name, goods_short, seller_shop,
            goods_thumb, goods_gallery, coupon_begin, coupon_final, coupon_surplus, coupon_explain, cate_category,
            attr_site, coupon_total, coupon_id, referer, source;
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
    /*收藏相关*/
    @BindView(R.id.re_collect)
    RelativeLayout re_collect;
    @BindView(R.id.iv_collect)
    ImageView iv_collect;
    @BindView(R.id.tv_collect)
    TextView tv_collect;
    @BindView(R.id.collect_list)
    TextView collect_list;
    /*最底部布局*/
    @BindView(R.id.ll_most_bottom)
    LinearLayout ll_most_bottom;
    @BindView(R.id.tv_lijiyaoqing)
    TextView tv_lijiyaoqing;
    @BindView(R.id.re_viewpager_parent)
    RelativeLayout re_viewpager_parent;
    /*开关字段*/
    private String is_pop_window, upgrade_vip_invite, money_upgrade_switch, is_show_money_vip, is_pop_window_vip;
    private boolean isShopCollect = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopdetailactivity);
        ButterKnife.bind(this);
        EventBus.getDefault().post(this);
        loadingDialog = DialogUtil.createLoadingDialog(ShopDetailActivity.this, "加载...");
        this.widthPixels = getResources().getDisplayMetrics().widthPixels;
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
        coupon_total = intent.getStringExtra("coupon_total");
        coupon_id = intent.getStringExtra("coupon_id");
        referer = intent.getStringExtra(Constant.SHOP_REFERER);
        source = intent.getStringExtra(Constant.GAOYONGJIN_SOURCE);
        String short_title = PreferUtils.getString(getApplicationContext(), "short_title");
        is_pop_window = PreferUtils.getString(getApplicationContext(), "is_pop_window");
        is_show_money_vip = PreferUtils.getString(getApplicationContext(), "is_show_money_vip");
        is_pop_window_vip = PreferUtils.getString(getApplicationContext(), "is_pop_window_vip");
        /*存储普通用户到vip需要的邀请人数*/
        upgrade_vip_invite = PreferUtils.getString(getApplicationContext(), "upgrade_vip_invite");
        money_upgrade_switch = PreferUtils.getString(getApplicationContext(), "money_upgrade_switch");
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

    private void initCollectShop() {
        if (PreferUtils.getBoolean(getApplicationContext(), "isLogin")) {
            /*判断商品是否收藏*/
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
                    .url(Constant.BASE_URL + Constant.SHOP_IS_COLLECT + "?" + param)
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
                            Log.i("商品收藏数据", response.toString());
                            try {
                                JSONObject jsonObject = new JSONObject(response.toString());
                                if (jsonObject.getInt("status") >= 0) {
                                    ShopIsCollectBean bean = GsonUtil.GsonToBean(response.toString(), ShopIsCollectBean.class);
                                    if (bean == null) return;
                                    String count = bean.getResult().getCount();
                                    if (count.equals("0")) {
                                        /*未收藏*/
                                        iv_collect.setImageResource(R.mipmap.wei_collect);
                                        tv_collect.setText("收藏");
                                        isShopCollect = false;
                                    } else if (count.equals("1")) {
                                        /*已收藏*/
                                        iv_collect.setImageResource(R.mipmap.yi_collected);
                                        tv_collect.setText("已收藏");
                                        isShopCollect = true;
                                    } else {
                                        /*未收藏*/
                                        iv_collect.setImageResource(R.mipmap.wei_collect);
                                        tv_collect.setText("收藏");
                                        isShopCollect = false;
                                    }
                                } else {
                                    /*未收藏*/
                                    iv_collect.setImageResource(R.mipmap.wei_collect);
                                    tv_collect.setText("收藏");
                                    isShopCollect = false;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, String error_msg) {
                            /*未收藏*/
                            iv_collect.setImageResource(R.mipmap.wei_collect);
                            tv_collect.setText("收藏");
                            isShopCollect = false;
                        }
                    });
        } else {
            iv_collect.setImageResource(R.mipmap.wei_collect);
            tv_collect.setText("收藏");
        }
    }

    private void initEditView() {
        goodname.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", goods_name);
                cm.setPrimaryClip(mClipData);
                ToastUtils.showToast(getApplicationContext(), "标题复制成功");
                ClipContentUtil.getInstance(getApplicationContext()).putNewSearch(goods_name);//保存记录到数据库
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
        if (TextUtils.isEmpty(goods_short)) {
            setVerticalCenterIconSpan(goods_name);
        } else {
            setVerticalCenterIconSpan(goods_short);
        }
        StringCleanZeroUtil.StringFormat(attr_price, tv_price);
        StringCleanZeroUtil.StringFormatWithYuan(attr_prime, tv_old_price);
        tv_sale_num.setText("月销" + NumUtil.getNum(sales_month));
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
        /*判断该商品是否收藏*/
        initCollectShop();
        /*获取下级数量接口*/
        if (PreferUtils.getBoolean(getApplicationContext(), "isLogin")) {
            getUserData();
        }
    }

    Dialog dialog;

    private void getClipContent() {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        boolean b = cm.hasPrimaryClip();
        if (b) {
            ClipData data = cm.getPrimaryClip();
            if (data == null) return;
            ClipData.Item item = data.getItemAt(0);
            final String content = item.coerceToText(getApplicationContext()).toString().trim().replace("\r\n\r\n", "\r\n");
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
    }

    private void showClipDialog(final String content) {
        PreferUtils.putString(getApplicationContext(), "clip_content", content);
        List<String> clip_list = ClipContentUtil.getInstance(getApplicationContext()).queryHistorySearchList();
        if (clip_list == null) return;
        for (int i = 0; i < clip_list.size(); i++) {
            if (clip_list.get(i).equals(content)) {
                return;
            }
        }
        guoDuTanKuang(content);
    }

    /*过渡弹框*/
    private void guoDuTanKuang(final String content) {
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

        if (Double.valueOf(coupon_surplus) > 0) {
            /*有券显示*/
            if (TextUtils.isEmpty(coupon_begin) || TextUtils.isEmpty(coupon_final)) {
                return;
            }
            String start_time = coupon_begin.substring(0, 4) + "." + coupon_begin.substring(4, 6) + "." + coupon_begin.substring(6, 8);
            String end_time = coupon_final.substring(0, 4) + "." + coupon_final.substring(4, 6) + "." + coupon_final.substring(6, 8);
            coupon_money.setText(StringCleanZeroUtil.DoubleFormat(youhuiquan) + "元优惠券");
            tv_coupon_time.setText("有效期:" + start_time + "-" + end_time);
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

        if (isLogin) {
            if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
                /*总裁比例*/
                setDataBiLi(90);
            } else if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
                /*合伙人比例*/
                setDataBiLi(70);
            } else if (Constant.VIP_USER_LEVEL.contains(member_role)) {
                /*vip比例*/
                setDataBiLi(40);
            } else {
                /*普通用户角色*/
                tv_share_money.setText("升级VIP");
                tv_buy.setText("领券购买");
            }
        } else {
            /*游客*/
            tv_share_money.setText("升级VIP");
            tv_buy.setText("领券购买");
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
                                        intent.putExtra("coupon_total", result.get(pos).getCoupon_total());
                                        intent.putExtra("coupon_id", result.get(pos).getCoupon_id());
                                        intent.putExtra(Constant.SHOP_REFERER, "search");/*商品来源*/
                                        intent.putExtra(Constant.GAOYONGJIN_SOURCE, result.get(pos).getSource());/*高佣金来源*/
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

    @OnClick({R.id.iv_back, R.id.tv_buy, R.id.tv_share_money, R.id.tv_tuijian, R.id.tv_baobei, R.id.re_yao_zhuanqian,
            R.id.ll_youhuiquan_show, R.id.to_home, R.id.tv_xiangqing, R.id.iv_yuanxing_back,
            R.id.to_top, R.id.re_collect, R.id.collect_list, R.id.ll_most_bottom})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_buy:
                /*购买返按钮*/
                toTaoBaoCouponActivity();
                break;
            case R.id.tv_share_money:
                /*分享赚按钮*/
                if (NetUtil.getNetWorkState(ShopDetailActivity.this) < 0) {
                    ToastUtils.showToast(getApplicationContext(), "您的网络异常，请联网重试");
                    return;
                }
                if (PreferUtils.getBoolean(getApplicationContext(), "isLogin")) {
                    if (Constant.COMMON_USER_LEVEL.contains(member_role)) {
                        /*普通用户到升级vip界面*/
                        intent = new Intent(getApplicationContext(), CommonUserToVIPActivity.class);
                        startActivity(intent);
                    } else {
                        /*先调用高佣金接口*/
                        shareGaoYongJinData();
                    }
                } else {
                    intent = new Intent(getApplicationContext(), LoginAndRegisterActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.ll_youhuiquan_show:
                /*优惠券布局按钮*/
                toTaoBaoCouponActivity();
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
            case R.id.re_collect:
                if (PreferUtils.getBoolean(getApplicationContext(), "isLogin")) {
                    if (isShopCollect == false) {
                        addCollectData();
                    } else {
                        cancelCollectData();
                    }
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginAndRegisterActivity.class));
                }
                break;
            case R.id.collect_list:
                if (PreferUtils.getBoolean(getApplicationContext(), "isLogin")) {
                    intent = new Intent(getApplicationContext(), GCollectionActivity.class);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginAndRegisterActivity.class));
                }
                break;
            case R.id.ll_most_bottom:
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
        if (!TextUtils.isEmpty(goods_id)) {
            map.put("goods_id", goods_id);
        }
        if (!TextUtils.isEmpty(coupon_id)) {
            map.put("coupon_id", coupon_id);
        }
        if (!TextUtils.isEmpty(referer)) {
            switch (referer) {
                case "search":
                    map.put(Constant.SHOP_REFERER, "search");
                    break;
                case "local":
                    map.put(Constant.SHOP_REFERER, "local");
                    break;
                case "circle":
                    map.put(Constant.SHOP_REFERER, "circle");
                    break;
                case "favorite":
                    map.put(Constant.SHOP_REFERER, "favorite");
                    break;
            }
        }
        if (!TextUtils.isEmpty(source)) {
            map.put(Constant.GAOYONGJIN_SOURCE, source);
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
                                AlibcTrade.show(ShopDetailActivity.this, page, alibcShowParams, null, exParams, new AlibcTradeCallback() {
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
            final ImageView iv = (ImageView) view.findViewById(R.id.iv);
            Glide.with(getApplicationContext()).load(bannerList.get(position % bannerList.size())).asBitmap().placeholder(R.drawable.loading_img)
                    .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            //原始图片宽高
                            int imageWidth = resource.getWidth();
                            int imageHeight = resource.getHeight();
                            Log.i("图片高度", imageHeight + "");
                            //按比例收缩图片
                            float ratio = (float) ((imageWidth * 1.0) / (widthPixels * 1.0));
                            int height = (int) (imageHeight * 1.0 / ratio);
                            ViewGroup.LayoutParams params = iv.getLayoutParams();
                            params.width = widthPixels;
                            params.height = height;
                            iv.setImageBitmap(resource);
                        }
                    });
            container.addView(view);
            return view;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlibcTradeSDK.destory();
        EventBus.getDefault().unregister(this);
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /*分享调用高拥接口*/
    private void shareGaoYongJinData() {
        loadingDialog = DialogUtil.createLoadingDialog(ShopDetailActivity.this, "加载中...");
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
        if (!TextUtils.isEmpty(referer)) {
            switch (referer) {
                case "search":
                    map.put(Constant.SHOP_REFERER, "search");
                    break;
                case "local":
                    map.put(Constant.SHOP_REFERER, "local");
                    break;
                case "circle":
                    map.put(Constant.SHOP_REFERER, "circle");
                    break;
                case "favorite":
                    map.put(Constant.SHOP_REFERER, "favorite");
                    break;
            }
        }
        if (!TextUtils.isEmpty(source)) {
            map.put(Constant.GAOYONGJIN_SOURCE, source);
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
        map.put("goods_id", goods_id);
        map.put("attr_prime", attr_prime);
        map.put("attr_price", attr_price);
        map.put("text", goods_name);
        map.put("logo", bannerList.get(0));
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
        map.put("attr_prime", attr_prime);
        map.put("attr_price", attr_price);
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
                                String result = jsonObject.getString("result");
                                intent = new Intent(getApplicationContext(), CreateShare_New_Activity.class);
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
                                intent.putExtra("share_taokouling", share_taokouling);
                                intent.putExtra("share_qrcode", result);
                                intent.putExtra("coupon_surplus", coupon_surplus);
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

    /*添加收藏*/
    private void addCollectData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        if (!TextUtils.isEmpty(goods_id)) {
            map.put("goods_id", goods_id);
        }
        if (!TextUtils.isEmpty(goods_name)) {
            map.put("goods_name", goods_name);
        }
        if (!TextUtils.isEmpty(goods_short)) {
            map.put("goods_short", goods_short);
        }
        if (!TextUtils.isEmpty(goods_thumb)) {
            map.put("goods_thumb", goods_thumb);
        }
        if (!TextUtils.isEmpty(attr_site)) {
            map.put("attr_site", attr_site);
        }
        if (!TextUtils.isEmpty(attr_prime)) {
            map.put("attr_prime", attr_prime);
        }
        if (!TextUtils.isEmpty(attr_price)) {
            map.put("attr_price", attr_price);
        }
        if (!TextUtils.isEmpty(attr_ratio)) {
            map.put("attr_ratio", attr_ratio);
        }
        if (!TextUtils.isEmpty(sales_month)) {
            map.put("sales_month", sales_month);
        }
        if (!TextUtils.isEmpty(seller_shop)) {
            map.put("seller_shop", seller_shop);
        }
        if (!TextUtils.isEmpty(coupon_total)) {
            map.put("coupon_total", coupon_total);
        }
        if (!TextUtils.isEmpty(coupon_surplus)) {
            map.put("coupon_surplus", coupon_surplus);
        }
        if (!TextUtils.isEmpty(coupon_id)) {
            map.put("coupon_id", coupon_id);
        }
        if (!TextUtils.isEmpty(cate_route)) {
            map.put("cate_route", cate_route);
        }
        if (!TextUtils.isEmpty(goods_gallery)) {
            map.put("goods_gallery", goods_gallery);
        }
        if (!TextUtils.isEmpty(coupon_begin)) {
            map.put("coupon_begin", coupon_begin);
        }
        if (!TextUtils.isEmpty(cate_category)) {
            map.put("cate_category", cate_category);
        }
        if (!TextUtils.isEmpty(coupon_final)) {
            map.put("coupon_final", coupon_final);
        }
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.ADD_COLLECT + "?" + param)
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
                        Log.i("添加收藏", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                ToastUtils.showToast(getApplicationContext(), "收藏成功");
                                iv_collect.setImageResource(R.mipmap.yi_collected);
                                tv_collect.setText("已收藏");
                                isShopCollect = true;
                                EventBus.getDefault().post(Constant.COLLECT_CHANGE);
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

    /*取消收藏*/
    private void cancelCollectData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        if (!TextUtils.isEmpty(goods_id)) {
            map.put("goods_id", goods_id);
        }
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.CANCEL_COLLECT_SHOP + "?" + param)
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
                        Log.i("取消收藏", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                ToastUtils.showToast(getApplicationContext(), "取消收藏成功");
                                isShopCollect = false;
                                iv_collect.setImageResource(R.mipmap.wei_collect);
                                tv_collect.setText("收藏");
                                EventBus.getDefault().post(Constant.COLLECT_CHANGE);
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

    /*分享赚普通用户到Vip弹窗*/
    private void shareMakeMoneyDialog() {
        dialog = new Dialog(ShopDetailActivity.this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(R.layout.share_shop_make_money);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView tv_num_invite = (TextView) dialog.findViewById(R.id.tv_num_invite);
        final LinearLayout ll_cancel = (LinearLayout) dialog.findViewById(R.id.ll_cancel);
        TextView up_leave = (TextView) dialog.findViewById(R.id.up_leave);
        ll_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_num_invite.setText("邀请" + upgrade_vip_invite + "位好友注册");
        up_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                intent = new Intent(getApplicationContext(), YaoQingFriendActivity.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    /*购买返普通到vip弹框*/
    BigDecimal bg3;

    private void ShowPtongToVipDialog() {
        dialog = new Dialog(ShopDetailActivity.this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(R.layout.common_to_vip);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView money_num = (TextView) dialog.findViewById(R.id.money_num);
        TextView tv_vip_fan = (TextView) dialog.findViewById(R.id.tv_vip_fan);
        TextView to_upgrade = (TextView) dialog.findViewById(R.id.to_upgrade);
        TextView up_buy = (TextView) dialog.findViewById(R.id.up_buy);
        LinearLayout ll_cancel = (LinearLayout) dialog.findViewById(R.id.ll_cancel);
        if (Double.valueOf(coupon_surplus) > 0) {
            up_buy.setText("领券购买");
        } else {
            up_buy.setText("立即购买");
        }
        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * app_v * 40 / 10000;
        bg3 = new BigDecimal(result);
        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        money_num.setText(money + "");
        if (is_show_money_vip.equals("yes")) {
            double sj_result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 50 / 10000 * app_v;
            bg3 = new BigDecimal(sj_result);
            double sj_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            tv_vip_fan.setText("升级VIP返" + sj_money + "元");
        } else {
            double sj_result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 82 / 10000 * app_v;
            bg3 = new BigDecimal(sj_result);
            double sj_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            tv_vip_fan.setText("升级合伙人返" + sj_money + "元");
        }
        ll_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /*立即升级*/
        to_upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                intent = new Intent(getApplicationContext(), GVipToFriendActivity.class);
                startActivity(intent);
            }
        });
        up_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                /*清空剪切板内容*/
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm.hasPrimaryClip()) {
                    cm.setPrimaryClip(ClipData.newPlainText(null, ""));
                }
                /*调用高佣金接口*/
                getGaoYongJinData();
            }
        });
        dialog.show();
    }

    /*点击购买返时  Vip到合伙人*/
    private void VipToHeHourenBuyDialog() {
        dialog = new Dialog(ShopDetailActivity.this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(R.layout.common_to_vip);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView money_num = (TextView) dialog.findViewById(R.id.money_num);
        TextView tv_vip_fan = (TextView) dialog.findViewById(R.id.tv_vip_fan);
        TextView to_upgrade = (TextView) dialog.findViewById(R.id.to_upgrade);
        TextView up_buy = (TextView) dialog.findViewById(R.id.up_buy);
        LinearLayout ll_cancel = (LinearLayout) dialog.findViewById(R.id.ll_cancel);
        if (Double.valueOf(coupon_surplus) > 0) {
            up_buy.setText("领券购买");
        } else {
            up_buy.setText("立即购买");
        }
        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * app_v * 50 / 10000;
        bg3 = new BigDecimal(result);
        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        money_num.setText(money + "");
        double sj_result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 82 / 10000 * app_v;
        bg3 = new BigDecimal(sj_result);
        double sj_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        tv_vip_fan.setText("升级合伙人返" + sj_money + "元");
        ll_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /*立即升级*/
        to_upgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                intent = new Intent(getApplicationContext(), GVipToFriendActivity.class);
                startActivity(intent);
            }
        });
        up_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                /*清空剪切板内容*/
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm.hasPrimaryClip()) {
                    cm.setPrimaryClip(ClipData.newPlainText(null, ""));
                }
                /*调用高佣金接口*/
                getGaoYongJinData();
            }
        });
        dialog.show();
    }

    private void getUserData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        map.put("field", Constant.USER_DATA_PARA);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.USER_BASIC_INFO + "?" + param)
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
                        Log.i("用户信息数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                BaseUserBean userDataBean = GsonUtil.GsonToBean(response.toString(), BaseUserBean.class);
                                if (userDataBean != null) {
                                    BaseUserBean.BaseUserData result = userDataBean.getResult();
                                    member_role = result.getMember_role();/*用户等级*/
                                    son_count = result.getFans();/*下级数量*/
                                    PreferUtils.putString(getApplicationContext(), "member_role", member_role);
                                    PreferUtils.putString(getApplicationContext(), "son_count", son_count);
                                }
                            } else {
                                ToastUtils.showToast(getApplicationContext(), jsonObject.getString("result"));
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

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case Constant.USER_LEVEL_UPGRADE:
                /*用户升级成功调用用户信息接口*/
                getUserData();
                break;
            case Constant.COLLECT_LIST_CHANGE:
                /*列表收藏改变*/
                initCollectShop();
                break;

        }
    }

    /*优惠券立即领取按钮和购买返按钮*/
    private void toTaoBaoCouponActivity() {
        if (NetUtil.getNetWorkState(ShopDetailActivity.this) < 0) {
            ToastUtils.showToast(getApplicationContext(), "您的网络异常，请联网重试");
            return;
        }
        if (PreferUtils.getBoolean(getApplicationContext(), "isLogin")) {
            if (is_pop_window.equals("yes") && Constant.COMMON_USER_LEVEL.contains(member_role)) {
                /*普通用户*/
                ShowPtongToVipDialog();
            } else if (is_pop_window_vip.equals("yes") && Constant.VIP_USER_LEVEL.contains(member_role)) {
                /*Vip会员*/
                VipToHeHourenBuyDialog();
            } else {
                /*清空剪切板内容*/
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm.hasPrimaryClip()) {
                    cm.setPrimaryClip(ClipData.newPlainText(null, ""));
                }
                /*调用高佣金接口*/
                getGaoYongJinData();
            }
        } else {
            intent = new Intent(getApplicationContext(), LoginAndRegisterActivity.class);
            startActivity(intent);
        }
    }

}
