package com.guodongbaohe.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.CreateShareForChoosePicsAdapter;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.ChooseImagsNum;
import com.guodongbaohe.app.bean.TemplateBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.itemdecoration.PinLeiItemDecoration;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.ClipContentUtil;
import com.guodongbaohe.app.util.CommonUtil;
import com.guodongbaohe.app.util.DensityUtils;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.IconAndTextGroupUtil;
import com.guodongbaohe.app.util.NetPicsToBitmap;
import com.guodongbaohe.app.util.NetUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.QRCodeUtil;
import com.guodongbaohe.app.util.ShareManager;
import com.guodongbaohe.app.util.StringCleanZeroUtil;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.Tools;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.moments.WechatMoments;

public class CreateShare_New_Activity extends BaseActivity {
    /*规则说明*/
    @BindView(R.id.re_guize)
    RelativeLayout re_guize;
    /*奖金金额*/
    @BindView(R.id.tv_bonus)
    TextView tv_bonus;
    /*图片列表*/
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    /*生成海报按钮*/
    @BindView(R.id.tv_buide_poster)
    TextView tv_buide_poster;
    /*微信好友*/
    @BindView(R.id.re_wchat_friend)
    RelativeLayout re_wchat_friend;
    /*微信朋友圈*/
    @BindView(R.id.re_wchat_circle)
    RelativeLayout re_wchat_circle;
    /*qq好友*/
    @BindView(R.id.re_qq_friend)
    RelativeLayout re_qq_friend;
    /*保存图片*/
    @BindView(R.id.re_qq_space)
    RelativeLayout re_qq_space;
    /*编辑文案模板*/
    @BindView(R.id.edit_comment_template)
    TextView edit_comment_template;
    /*线一*/
    @BindView(R.id.tv_view_line_one)
    TextView tv_view_line_one;
    /*标题二*/
    @BindView(R.id.shop_title)
    TextView shop_title;
    /*在售价三*/
    @BindView(R.id.shop_original_price)
    TextView shop_original_price;
    /*券后限时秒杀四*/
    @BindView(R.id.shop_coupon_price)
    TextView shop_coupon_price;
    /*线五*/
    @BindView(R.id.tv_view_line_two)
    TextView tv_view_line_two;
    /*下单链接六*/
    @BindView(R.id.tv_order_addrress)
    TextView tv_order_addrress;
    /*线七*/
    @BindView(R.id.tv_view_line_three)
    TextView tv_view_line_three;
    /*淘口令八*/
    @BindView(R.id.taobao_ling)
    TextView taobao_ling;
    /*单独的淘口令信息*/
    @BindView(R.id.tv_tkl_content)
    TextView tv_tkl_content;
    /*复制评论按钮*/
    @BindView(R.id.tkl_copy_comment)
    TextView tkl_copy_comment;
    /*复制文案分享按钮*/
    @BindView(R.id.tv_copy_comment_shre)
    TextView tv_copy_comment_shre;
    /*纯评论框*/
    @BindView(R.id.re_plun)
    RelativeLayout re_plun;
    /*推荐理由*/
    @BindView(R.id.tuijian_liyou)
    TextView tuijian_liyou;
    /*推荐理由下面的线*/
    @BindView(R.id.tv_view_line_four)
    TextView tv_view_line_four;
    /*商品资源*/
    String goods_thumb, goods_gallery, goods_name, promo_slogan, attr_price, attr_prime,
            attr_site, good_short, attr_ratio, goods_id, share_taokouling, share_qrcode, coupon_surplus;
    Intent intent;
    private List<String> pics_list;
    /*组装图片数据集合*/
    List<ChooseImagsNum> chooseImagsNumList;
    BigDecimal bg3;
    String member_role, member_id;
    double app_v;
    double v;
    Dialog loadingDialog;
    CreateShareForChoosePicsAdapter adapter;
    /*判断是否生成过推广海报  0 没有 ； 1生成过*/
    private int buidePoster = 0;
    /*记住选中的位置*/
    LinkedHashMap<Integer, Integer> choose_poition;
    List<Integer> allPosition;
    String title_sign = "{标题}";
    String order_address_sign = "{下单链接}";
    String taokouling_sign = "{淘口令}";
    String tuijian_sign = "{推荐理由}";
    /*微信朋友圈单张图片*/
    private List<String> wchatCirclePicsList;
    private int ainteger;
    String circle_pic_url;

    @Override
    public int getContainerView() {
        return R.layout.createshare_new_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("创建分享");
        member_role = PreferUtils.getString(getApplicationContext(), "member_role");
        member_id = PreferUtils.getString(getApplicationContext(), "member_id");
        String tax_rate = PreferUtils.getString(getApplicationContext(), "tax_rate");
        app_v = 1 - Double.valueOf(tax_rate);
        intent = getIntent();
        goods_thumb = intent.getStringExtra("goods_thumb");
        goods_gallery = intent.getStringExtra("goods_gallery");
        goods_name = intent.getStringExtra("goods_name");
        promo_slogan = intent.getStringExtra("promo_slogan");
        attr_price = intent.getStringExtra("attr_price");
        attr_prime = intent.getStringExtra("attr_prime");
        attr_site = intent.getStringExtra("attr_site");
        good_short = intent.getStringExtra("good_short");
        attr_ratio = intent.getStringExtra("attr_ratio");
        goods_id = intent.getStringExtra("goods_id");
        share_taokouling = intent.getStringExtra("share_taokouling");
        share_qrcode = intent.getStringExtra("share_qrcode");
        coupon_surplus = intent.getStringExtra("coupon_surplus");
        v = Double.valueOf(attr_prime) - Double.valueOf(attr_price);
        initRecyclerView();/*图片初始化*/
        initPosterLayoutView();/*二维码海报布局*/
        initTemplateDataView();/*初始化本地数据*/
        initMoneyData();/*初始化金额数据*/
    }

    private void initMoneyData() {
        if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
            /*总裁用户*/
            rebateData(90);
        } else if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
            /*合伙人用户*/
            rebateData(70);
        } else if (Constant.VIP_USER_LEVEL.contains(member_role)) {
            /*vip用户*/
            rebateData(40);
        }
    }

    /*用户级别显示你能返佣金数*/
    private void rebateData(int num) {
        double ninengfan = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * num / 10000 * app_v;
        bg3 = new BigDecimal(ninengfan);
        double money_ninneng = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        tv_bonus.setText("你的奖励预计为: ¥" + money_ninneng);
    }

    private void initTemplateDataView() {
        content_line_one = PreferUtils.getString(getApplicationContext(), "content_line_one");
        content_title_two = PreferUtils.getString(getApplicationContext(), "content_title_two");
        content_sale_price_three = PreferUtils.getString(getApplicationContext(), "content_sale_price_three");
        content_coupon_four = PreferUtils.getString(getApplicationContext(), "content_coupon_four");
        content_line_five = PreferUtils.getString(getApplicationContext(), "content_line_five");
        content_order_six = PreferUtils.getString(getApplicationContext(), "content_order_six");
        content_line_seven = PreferUtils.getString(getApplicationContext(), "content_line_seven");
        content_taobao_eight = PreferUtils.getString(getApplicationContext(), "content_taobao_eight");
        content_tuijian_nine = PreferUtils.getString(getApplicationContext(), "content_tuijian_nine");
        content_line_ten = PreferUtils.getString(getApplicationContext(), "content_line_ten");
        if (TextUtils.isEmpty(content_title_two)) {
            /*调用接口数据*/
            getTemplateData();
        } else {
            /*线一显示*/
            if (TextUtils.isEmpty(content_line_one)) {
                tv_view_line_one.setVisibility(View.GONE);
            } else {
                tv_view_line_one.setVisibility(View.VISIBLE);
                tv_view_line_one.setText(content_line_one);
            }
            /*标题显示*/
            String start_tile = content_title_two.substring(0, content_title_two.indexOf("{"));
            String end_tile = content_title_two.substring(content_title_two.indexOf("}") + 1);
            if (TextUtils.isEmpty(good_short)) {
                shop_title.setText(start_tile + goods_name + end_tile);
            } else {
                shop_title.setText(start_tile + good_short + end_tile);
            }
            /*原价显示*/
            String start_sale_price = content_sale_price_three.substring(0, content_sale_price_three.indexOf("{"));
            String end_sale_price = content_sale_price_three.substring(content_sale_price_three.indexOf("}") + 1);
            shop_original_price.setText(start_sale_price + StringCleanZeroUtil.DoubleFormat(Double.valueOf(attr_prime)) + end_sale_price);
            /*券后价显示*/
            String start_coupon_price = content_coupon_four.substring(0, content_coupon_four.indexOf("{"));
            String end_coupon_price = content_coupon_four.substring(content_coupon_four.indexOf("}") + 1);
            shop_coupon_price.setText(start_coupon_price + StringCleanZeroUtil.DoubleFormat(Double.valueOf(attr_price)) + end_coupon_price);
            /*线二显示*/
            if (TextUtils.isEmpty(content_line_five)) {
                tv_view_line_two.setVisibility(View.GONE);
            } else {
                tv_view_line_two.setVisibility(View.VISIBLE);
                tv_view_line_two.setText(content_line_five);
            }
            /*下单链接显示*/
            if (TextUtils.isEmpty(content_order_six)) {
                tv_order_addrress.setVisibility(View.GONE);
            } else {
                tv_order_addrress.setVisibility(View.VISIBLE);
                if (content_order_six.contains(order_address_sign)) {
                    String start_order = content_order_six.substring(0, content_order_six.indexOf("{"));
                    String end_order = content_order_six.substring(content_order_six.indexOf("}") + 1);
                    tv_order_addrress.setText(start_order + share_qrcode + end_order);
                } else {
                    tv_order_addrress.setText(content_order_six);
                }
            }
            /*线三显示*/
            if (TextUtils.isEmpty(content_line_seven)) {
                tv_view_line_three.setVisibility(View.GONE);
            } else {
                tv_view_line_three.setVisibility(View.VISIBLE);
                tv_view_line_three.setText(content_line_seven);
            }
            /*推荐理由显示*/
            if (TextUtils.isEmpty(content_tuijian_nine)) {
                tuijian_liyou.setVisibility(View.GONE);
            } else {
                tuijian_liyou.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(promo_slogan)) {
                    tuijian_liyou.setVisibility(View.GONE);
                } else {
                    tuijian_liyou.setVisibility(View.VISIBLE);
                    if (content_tuijian_nine.contains(tuijian_sign)) {
                        String start_tuijian = content_tuijian_nine.substring(0, content_tuijian_nine.indexOf("{"));
                        String end_tuijian = content_tuijian_nine.substring(content_tuijian_nine.indexOf("}") + 1);
                        tuijian_liyou.setText(start_tuijian + promo_slogan + end_tuijian);
                    } else {
                        tuijian_liyou.setText(content_tuijian_nine);
                    }
                }
            }
            /*推荐理由下面的线条*/
            if (TextUtils.isEmpty(content_line_ten)) {
                tv_view_line_four.setVisibility(View.GONE);
            } else {
                tv_view_line_four.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(promo_slogan)) {
                    tv_view_line_four.setVisibility(View.GONE);
                } else {
                    tv_view_line_four.setVisibility(View.VISIBLE);
                    tv_view_line_four.setText(content_line_ten);
                }
            }
            /*淘口令显示  和  单独的淘口令显示 */
            if (TextUtils.isEmpty(content_taobao_eight)) {
                taobao_ling.setVisibility(View.GONE);
                re_plun.setVisibility(View.GONE);
            } else {
                taobao_ling.setVisibility(View.VISIBLE);
                re_plun.setVisibility(View.VISIBLE);
                if (content_taobao_eight.contains(taokouling_sign)) {
                    String start_order = content_taobao_eight.substring(0, content_taobao_eight.indexOf("{"));
                    String end_order = content_taobao_eight.substring(content_taobao_eight.indexOf("}") + 1);
                    taobao_ling.setText(start_order + share_taokouling + end_order);
                    tv_tkl_content.setText(start_order + share_taokouling + end_order);
                } else {
                    taobao_ling.setText(content_taobao_eight);
                    tv_tkl_content.setText(content_taobao_eight);
                }
            }
        }
    }

    /*获取模板数据*/
    private void getTemplateData() {
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.SHARE_MOBAN)
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
                        Log.i("打印模板看看", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                TemplateBean bean = GsonUtil.GsonToBean(response.toString(), TemplateBean.class);
                                if (bean == null) return;
                                String comment = bean.getResult().getComment();
                                String content = bean.getResult().getContent();
                                String[] lines = content.split("\n");
                                if (lines.length == 0) return;
                                String start_tkl = comment.substring(0, comment.indexOf("{"));
                                String end_tkl = comment.substring(comment.indexOf("}") + 1);
                                tv_view_line_one.setVisibility(View.VISIBLE);
                                tv_view_line_two.setVisibility(View.VISIBLE);
                                tv_order_addrress.setVisibility(View.VISIBLE);
                                tv_view_line_three.setVisibility(View.VISIBLE);
                                tuijian_liyou.setVisibility(View.VISIBLE);
                                tv_view_line_four.setVisibility(View.VISIBLE);
                                taobao_ling.setVisibility(View.VISIBLE);
                                re_plun.setVisibility(View.VISIBLE);
                                tv_view_line_one.setText(lines[1]);
                                String start_title = lines[0].substring(0, lines[0].indexOf("{"));
                                String end_title = lines[0].substring(lines[0].indexOf("}") + 1);
                                if (TextUtils.isEmpty(good_short)) {
                                    shop_title.setText(start_title + goods_name + end_title);
                                } else {
                                    shop_title.setText(start_title + good_short + end_title);
                                }
                                String start_sale_price = lines[2].substring(0, lines[2].indexOf("{"));
                                String end_sale_price = lines[2].substring(lines[2].indexOf("}") + 1);
                                shop_original_price.setText(start_sale_price + StringCleanZeroUtil.DoubleFormat(Double.valueOf(attr_prime)) + end_sale_price);
                                String start_coupon_price = lines[3].substring(0, lines[3].indexOf("{"));
                                String end_coupon_price = lines[3].substring(lines[3].indexOf("}") + 1);
                                shop_coupon_price.setText(start_coupon_price + StringCleanZeroUtil.DoubleFormat(Double.valueOf(attr_price)) + end_coupon_price);
                                tv_view_line_two.setText(lines[1]);
                                tv_order_addrress.setText(share_qrcode);
                                tv_view_line_three.setText(lines[1]);
                                if (TextUtils.isEmpty(promo_slogan)) {
                                    tuijian_liyou.setVisibility(View.GONE);
                                } else {
                                    tuijian_liyou.setVisibility(View.VISIBLE);
                                    String start_liyou = lines[5].substring(0, lines[5].indexOf("{"));
                                    String end_liyou = lines[5].substring(lines[5].indexOf("}") + 1);
                                    tuijian_liyou.setText(start_liyou + promo_slogan + end_liyou);
                                }
                                tv_view_line_four.setText(lines[1]);
                                taobao_ling.setText(start_tkl + share_taokouling + end_tkl);
                                tv_tkl_content.setText(start_tkl + share_taokouling + end_tkl);
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

    /*图片布局显示*/
    private void initRecyclerView() {
        recyclerview.setHasFixedSize(true);
        recyclerview.setNestedScrollingEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerview.setLayoutManager(manager);
        pics_list = new ArrayList<>();
        chooseImagsNumList = new ArrayList<>();
        if (TextUtils.isEmpty(goods_gallery)) {
            pics_list.add(goods_thumb);
        } else {
            String[] split = goods_gallery.split(",");
            for (int i = 0; i < split.length; i++) {
                pics_list.add(split[i]);
            }
        }
        for (int i = 0; i < pics_list.size(); i++) {
            ChooseImagsNum bean = new ChooseImagsNum();
            bean.setUrl(pics_list.get(i));
            chooseImagsNumList.add(bean);
        }
        recyclerview.addItemDecoration(new PinLeiItemDecoration(DensityUtils.dip2px(getApplicationContext(), 10), pics_list));
        adapter = new CreateShareForChoosePicsAdapter(getApplicationContext(), chooseImagsNumList);
        recyclerview.setAdapter(adapter);
        adapter.setonclicklistener(new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                intent = new Intent(getApplicationContext(), PosterPhotoViewPagerActivity.class);
                intent.putExtra("current", position);
                intent.putExtra("mode", buidePoster);
                intent.putStringArrayListExtra("pics_list", (ArrayList<String>) pics_list);
                startActivity(intent);
                overridePendingTransition(R.anim.ap2, R.anim.ap1);
            }
        });
        choose_poition = new LinkedHashMap<>();
        chooseImagsNumList.get(0).setChecked(true);
        choose_poition.put(0, 0);
        adapter.notifyDataSetChanged();
        adapter.setOnCheckedListener(new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                chooseImagsNumList.get(position).setChecked(!chooseImagsNumList.get(position).isChecked());
                if (chooseImagsNumList.get(position).isChecked()) {
                    choose_poition.put(position, position);
                } else {
                    choose_poition.remove(position);
                }
                adapter.notifyItemChanged(position);
            }
        });
    }

    View qrcode_poster_view;
    /*标题*/
    TextView p_title;
    /*优惠券类型*/
    TextView tv_jia_type;
    /*优惠券价格*/
    TextView p_coupon_price;
    /*优惠券显示*/
    TextView tv_coupon;
    /*中间的大图*/
    ImageView p_iv;
    /*大图价格显示*/
    TextView p_one_price;
    /*二维码图片*/
    ImageView iv_qr_code;

    /*二维码海报布局*/
    private void initPosterLayoutView() {
        qrcode_poster_view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.creation_erweima_pic, null);
        p_title = (TextView) qrcode_poster_view.findViewById(R.id.p_title);
        tv_jia_type = (TextView) qrcode_poster_view.findViewById(R.id.tv_jia_type);
        p_coupon_price = (TextView) qrcode_poster_view.findViewById(R.id.p_coupon_price);
        tv_coupon = (TextView) qrcode_poster_view.findViewById(R.id.tv_coupon);
        p_iv = (ImageView) qrcode_poster_view.findViewById(R.id.p_iv);
        p_one_price = (TextView) qrcode_poster_view.findViewById(R.id.p_one_price);
        iv_qr_code = (ImageView) qrcode_poster_view.findViewById(R.id.iv_qr_code);
        if (Double.valueOf(coupon_surplus) > 0) {
            tv_jia_type.setText("券后价¥");
            double d_price = Double.valueOf(attr_prime) - Double.valueOf(attr_price);
            bg3 = new BigDecimal(d_price);
            double d_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            tv_coupon.setText(StringCleanZeroUtil.DoubleFormat(d_money) + "元券");
        } else {
            if (v > 0) {
                tv_jia_type.setText("折后价¥");
                double disaccount = Double.valueOf(attr_price) / Double.valueOf(attr_prime) * 10;
                bg3 = new BigDecimal(disaccount);
                double d_zhe = bg3.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                tv_coupon.setText(d_zhe + "折");
            } else {
                tv_jia_type.setText("特惠价¥");
                tv_coupon.setText("立即抢购");
            }
        }
        IconAndTextGroupUtil.setTextView(getApplicationContext(), p_title, goods_name, attr_site);
        p_coupon_price.setText(StringCleanZeroUtil.DoubleFormat(Double.valueOf(attr_price)));
        p_one_price.setText("¥" + StringCleanZeroUtil.DoubleFormat(Double.valueOf(attr_price)));
        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(share_qrcode, DensityUtils.dip2px(getApplicationContext(), 100));
        iv_qr_code.setImageBitmap(mBitmap);
    }

    @OnClick({R.id.tv_buide_poster, R.id.edit_comment_template, R.id.tkl_copy_comment, R.id.tv_copy_comment_shre
            , R.id.re_wchat_friend, R.id.re_wchat_circle, R.id.re_qq_friend, R.id.re_qq_space, R.id.re_guize})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_buide_poster:/*点击生成海报按钮*/
                if (NetUtil.getNetWorkState(CreateShare_New_Activity.this) < 0) {
                    ToastUtils.showToast(getApplicationContext(), "您的网络异常，请联网重试");
                    return;
                }
                if (buidePoster == 1) {
                    ToastUtils.showToast(getApplicationContext(), "已经生成过推广海报");
                } else {
                    allPosition = new ArrayList<>();
                    Collection<Integer> values = choose_poition.values();// 得到全部的value
                    Iterator<Integer> iter = values.iterator();
                    while (iter.hasNext()) {
                        Integer str = iter.next();
                        allPosition.add(str);
                    }
                    Collections.sort(allPosition);
                    loadingDialog = DialogUtil.createLoadingDialog(CreateShare_New_Activity.this, "生成中...");
                    if (allPosition.size() == 0) {
                        Glide.with(getApplicationContext()).load(pics_list.get(0)).asBitmap().into(target);
                    } else {
                        Glide.with(getApplicationContext()).load(pics_list.get(allPosition.get(0))).asBitmap().into(target);
                    }
                }
                break;
            case R.id.edit_comment_template:/*编辑文案模板按钮*/
                intent = new Intent(getApplicationContext(), EditCommentTemplateActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.tkl_copy_comment:/*复制评论按钮*/
                String pl_content = tv_tkl_content.getText().toString().trim();
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm.hasPrimaryClip()) {
                    cm.getPrimaryClip().getItemAt(0).getText();
                }
                ClipData mClipData = ClipData.newPlainText("Label", pl_content);
                cm.setPrimaryClip(mClipData);
                ClipContentUtil.getInstance(getApplicationContext()).putNewSearch(tv_tkl_content.getText().toString().trim());//保存记录到数据库
                ToastUtils.showToast(getApplicationContext(), "评论复制成功");
                break;
            case R.id.tv_copy_comment_shre:/*复制文案按钮*/
                copyWenAnFunction();
                break;
            case R.id.re_wchat_friend:/*微信好友*/
                if (NetUtil.getNetWorkState(CreateShare_New_Activity.this) < 0) {
                    ToastUtils.showToast(getApplicationContext(), "您的网络异常，请联网重试");
                    return;
                }
                if (choose_poition.size() == 0) {
                    ToastUtils.showToast(getApplicationContext(), "至少勾选一张图片才能分享");
                    return;
                }
                if (ContextCompat.checkSelfPermission(CreateShare_New_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(CreateShare_New_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //没有存储权限
                    ActivityCompat.requestPermissions(CreateShare_New_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    if (buidePoster == 1) {
                        /*生成过海报*/
                        wChatFriendPicShare(0);
                    } else {
                        /*没有生成过海报*/
                        wChatFriendPicShare(1);
                    }
                }
                break;
            case R.id.re_wchat_circle:/*朋友圈分享*/
                if (NetUtil.getNetWorkState(CreateShare_New_Activity.this) < 0) {
                    ToastUtils.showToast(getApplicationContext(), "您的网络异常，请联网重试");
                    return;
                }
                if (choose_poition.size() == 0) {
                    ToastUtils.showToast(getApplicationContext(), "至少勾选一张图片才能分享");
                    return;
                }
                if (ContextCompat.checkSelfPermission(CreateShare_New_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(CreateShare_New_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //没有存储权限
                    ActivityCompat.requestPermissions(CreateShare_New_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    /*朋友圈分享*/
                    circleCommonFunction();
                }
                break;
            case R.id.re_qq_space:/*批量下载*/
                if (NetUtil.getNetWorkState(CreateShare_New_Activity.this) < 0) {
                    ToastUtils.showToast(getApplicationContext(), "您的网络异常，请联网重试");
                    return;
                }
                if (choose_poition.size() == 0) {
                    ToastUtils.showToast(getApplicationContext(), "至少勾选一张图片才能保存");
                    return;
                }
                if (ContextCompat.checkSelfPermission(CreateShare_New_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(CreateShare_New_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //没有存储权限
                    ActivityCompat.requestPermissions(CreateShare_New_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
                } else {
                    savePicsToLocal();
                }
                break;
            case R.id.re_guize:/*规则*/
//                intent = new Intent(getApplicationContext(), ShareDetailActivity.class);
//                intent.putExtra("url", PreferUtils.getString(getApplicationContext(), "share_goods"));
//                startActivity(intent);
                break;
            case R.id.re_qq_friend:/*qq好友分享*/
                if (NetUtil.getNetWorkState(CreateShare_New_Activity.this) < 0) {
                    ToastUtils.showToast(getApplicationContext(), "您的网络异常，请联网重试");
                    return;
                }
                if (choose_poition.size() == 0) {
                    ToastUtils.showToast(getApplicationContext(), "至少勾选一张图片才能分享");
                    return;
                }
                if (ContextCompat.checkSelfPermission(CreateShare_New_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(CreateShare_New_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //没有存储权限
                    ActivityCompat.requestPermissions(CreateShare_New_Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
                } else {
                    /*qq好友分享*/
                    qqFriendShareCommon();
                }
                break;
        }
    }

    /*qq好友分享*/
    private void qqFriendShareCommon() {
        if (buidePoster == 1) {
            /*qq好友生成过海报*/
            qqFriendHavePoster(0);
        } else {
            /*qq好友没有生成过海报*/
            qqFriendHavePoster(1);
        }
    }

    /*qq好友*/
    private void qqFriendHavePoster(int mode) {
        ShareManager shareManager = new ShareManager(CreateShare_New_Activity.this);
        allPosition = new ArrayList<>();
        Collection<Integer> values = choose_poition.values();// 得到全部的value
        Iterator<Integer> iter = values.iterator();
        while (iter.hasNext()) {
            Integer str = iter.next();
            allPosition.add(str);
        }
        Collections.sort(allPosition);
        share_imgs = new ArrayList<>();
        for (int i = 0; i < allPosition.size(); i++) {
            share_imgs.add(chooseImagsNumList.get(allPosition.get(i)).getUrl());
        }
        shareManager.setShareImage(hebingBitmap, 0, share_imgs, "", "qq", mode);
    }

    /*朋友圈分享公共方法*/
    private void circleCommonFunction() {
        allPosition = new ArrayList<>();
        Collection<Integer> values = choose_poition.values();// 得到全部的value
        Iterator<Integer> iter = values.iterator();
        while (iter.hasNext()) {
            Integer str = iter.next();
            allPosition.add(str);
        }
        Collections.sort(allPosition);
        if (allPosition.size() == 1) {
            /*单张图片分享用sharesdk*/
            wchatCirclePicsList = new ArrayList<>();
            ainteger = 0;
            for (int i = 0; i < allPosition.size(); i++) {
                ainteger = allPosition.get(i);
                circle_pic_url = chooseImagsNumList.get(ainteger).getUrl();
            }
            wchatFriendCircle(ainteger, circle_pic_url);
        } else {
            /*微信朋友圈多张图片分享*/
            wchatCirlcePicsShare();
        }
    }

    /*直接保存图片*/
    private void savePicsToLocal() {
        if (buidePoster == 1) {
            /*生成过海报图片 去批量下载*/
            havePosterPicsSaveLocal();
        } else {
            /*没有生成过海报图片 去批量下载*/
            netPicsSaveLocal();
        }
    }

    /*微信朋友圈多张图片分享*/
    private void wchatCirlcePicsShare() {
        /*由于微信机制不让分享多图直接到微信朋友圈*/
        WChatCircleDialog();
        if (buidePoster == 1) {
            /*生成过海报图片 去批量下载*/
            havePosterPicsSaveLocal();
        } else {
            /*没有生成过海报图片 去批量下载*/
            netPicsSaveLocal();
        }
    }

    /*生成过海报图片 去批量下载*/
    private void havePosterPicsSaveLocal() {
        allPosition = new ArrayList<>();
        Collection<Integer> values = choose_poition.values();// 得到全部的value
        Iterator<Integer> iter = values.iterator();
        while (iter.hasNext()) {
            Integer str = iter.next();
            allPosition.add(str);
        }
        Collections.sort(allPosition);
        share_imgs = new ArrayList<>();
        for (int i = 0; i < allPosition.size(); i++) {
            if (allPosition.get(i) == 0) {
                Bitmap bitmap = NetPicsToBitmap.convertStringToIcon(chooseImagsNumList.get(0).getUrl());
                CommonUtil.saveBitmap2file(bitmap, getApplicationContext());
            }
            share_imgs.add(chooseImagsNumList.get(allPosition.get(i)).getUrl());
        }
        saveMorePhotoToLocal(1, share_imgs);
    }

    /*没有生成过海报图片 去批量下载*/
    private void netPicsSaveLocal() {
        allPosition = new ArrayList<>();
        Collection<Integer> values = choose_poition.values();// 得到全部的value
        Iterator<Integer> iter = values.iterator();
        while (iter.hasNext()) {
            Integer str = iter.next();
            allPosition.add(str);
        }
        Collections.sort(allPosition);
        share_imgs = new ArrayList<>();
        for (int i = 0; i < allPosition.size(); i++) {
            share_imgs.add(chooseImagsNumList.get(allPosition.get(i)).getUrl());
        }
        saveMorePhotoToLocal(0, share_imgs);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            CommonUtil.saveBitmap2file(bitmap, getApplicationContext());
        }
    };

    /*微信朋友圈单图分享*/
    private void wchatFriendCircle(int mode, String url) {
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        if (buidePoster == 1) {
            if (mode == 0) {
                Bitmap bitmap = NetPicsToBitmap.convertStringToIcon(url);
                sp.setImageData(bitmap);
            } else {
                sp.setImageUrl(url);
            }
        } else {
            sp.setImageUrl(url);
        }
        sp.setShareType(Platform.SHARE_IMAGE);
        Platform weChat = ShareSDK.getPlatform(WechatMoments.NAME);
        weChat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
            }

            @Override
            public void onCancel(Platform platform, int i) {
            }
        });
        weChat.share(sp);
    }


    List<String> share_imgs;

    /*微信好友分享*/
    private void wChatFriendPicShare(int mode) {
        ShareManager shareManager = new ShareManager(CreateShare_New_Activity.this);
        allPosition = new ArrayList<>();
        Collection<Integer> values = choose_poition.values();// 得到全部的value
        Iterator<Integer> iter = values.iterator();
        while (iter.hasNext()) {
            Integer str = iter.next();
            allPosition.add(str);
        }
        Collections.sort(allPosition);
        share_imgs = new ArrayList<>();
        for (int i = 0; i < allPosition.size(); i++) {
            share_imgs.add(chooseImagsNumList.get(allPosition.get(i)).getUrl());
        }
        shareManager.setShareImage(hebingBitmap, 0, share_imgs, "", "wchat", mode);
    }

    /*复制文案方法*/
    private void copyWenAnFunction() {
        /*全部复制分享文案（复制看得见的）*/
        String copy_cotent = "";
        String one = tv_view_line_one.getText().toString().trim();
        String two = shop_title.getText().toString().trim();
        String three = shop_original_price.getText().toString().trim();
        String four = shop_coupon_price.getText().toString().trim();
        String five = tv_view_line_two.getText().toString().trim();
        String six = tv_order_addrress.getText().toString().trim();
        String seven = tv_view_line_three.getText().toString().trim();
        String nine = tuijian_liyou.getText().toString().trim();
        String ten = tv_view_line_four.getText().toString().trim();
        String eight = taobao_ling.getText().toString().trim();
        if (tv_view_line_one.getVisibility() == View.VISIBLE) {
            copy_cotent = copy_cotent + one;
        }
        copy_cotent = copy_cotent + "\n" + two + "\n" + three + "\n" + four;
        if (tv_view_line_two.getVisibility() == View.VISIBLE) {
            copy_cotent = copy_cotent + "\n" + five;
        }
        if (tv_order_addrress.getVisibility() == View.VISIBLE) {
            copy_cotent = copy_cotent + "\n" + six;
        }
        if (tv_view_line_three.getVisibility() == View.VISIBLE) {
            copy_cotent = copy_cotent + "\n" + seven;
        }
        if (tuijian_liyou.getVisibility() == View.VISIBLE) {
            copy_cotent = copy_cotent + "\n" + nine;
        }
        if (tv_view_line_four.getVisibility() == View.VISIBLE) {
            copy_cotent = copy_cotent + "\n" + ten;
        }
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm.hasPrimaryClip()) {
            cm.getPrimaryClip().getItemAt(0).getText();
        }
        ClipData mClipData = ClipData.newPlainText("Label", copy_cotent);
        cm.setPrimaryClip(mClipData);
        ClipContentUtil.getInstance(getApplicationContext()).putNewSearch(copy_cotent);//保存记录到数据库
        ToastUtils.showToast(getApplicationContext(), "文案复制成功");
    }

    String content_line_one, content_title_two, content_sale_price_three, content_coupon_four, content_line_five,
            content_order_six, content_line_seven, content_taobao_eight, content_tuijian_nine, content_line_ten;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 100) {
            initTemplateDataView();
        }
    }

    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            p_iv.setImageBitmap(bitmap);
            getViewToPics(qrcode_poster_view);
        }
    };

    private void getViewToPics(View view) {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        layoutView(view, width, height);
    }

    private void layoutView(View v, int width, int height) {
        v.layout(0, 0, width, height);
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(12000, View.MeasureSpec.AT_MOST);
        v.measure(measuredWidth, measuredHeight);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        viewSaveToImage(v);
    }

    Bitmap hebingBitmap;

    private void viewSaveToImage(View view) {
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheBackgroundColor(Color.WHITE);
        // 把一个View转换成图片
        hebingBitmap = loadBitmapFromView(view);
        String one_buide_pic = NetPicsToBitmap.convertIconToString(hebingBitmap);
        PreferUtils.putString(getApplicationContext(), "one_buide_pic", one_buide_pic);
        ChooseImagsNum bean = new ChooseImagsNum();
        bean.setUrl(one_buide_pic);
        bean.setChecked(true);
        chooseImagsNumList.add(0, bean);
        for (ChooseImagsNum list : chooseImagsNumList) {
            list.setChecked(false);
        }
        chooseImagsNumList.get(0).setChecked(true);
        adapter.refreDataChange(1);
        buidePoster = 1;
        choose_poition.clear();
        choose_poition.put(0, 0);
        DialogUtil.closeDialog(loadingDialog);
    }

    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogUtil.closeDialog(loadingDialog);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                /*存储权限回调*/
                /*微信好友分享回调*/
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getApplicationContext(), "分享多张图片需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getApplicationContext(), "分享多张图片需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (buidePoster == 1) {
                        /*生成过海报*/
                        wChatFriendPicShare(0);
                    } else {
                        /*没有生成过海报*/
                        wChatFriendPicShare(1);
                    }
                }
                break;
            case 2:
                circleCommonFunction();
                break;
            case 3:
                qqFriendShareCommon();
                break;
            case 4:
                savePicsToLocal();
                break;
        }
    }

    Dialog dialog;

    /*多图分享到微信时弹框到微信app*/
    private void WChatCircleDialog() {
        dialog = new Dialog(CreateShare_New_Activity.this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(R.layout.wchatcircle_dialog_pics);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView dismiss = (TextView) dialog.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView open_wx = (TextView) dialog.findViewById(R.id.open_wx);
        open_wx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Tools.isAppAvilible(getApplicationContext(), "com.tencent.mm")) {
                    ToastUtils.showToast(getApplicationContext(), "您还没有安装微信客户端,请先安转客户端");
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_MAIN);
                ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cmp);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /*创建下载线程方法*/
    /*批量下载图片*/
    private void saveMorePhotoToLocal(final int mode, final List<String> share_imgs) {
        /*网络路劲存储*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL imageurl;
                try {
                    for (int i = mode; i < share_imgs.size(); i++) {
                        imageurl = new URL(share_imgs.get(i));
                        HttpURLConnection conn = (HttpURLConnection) imageurl.openConnection();
                        conn.setDoInput(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        is.close();
                        Message msg = new Message();
                        // 把bm存入消息中,发送到主线程
                        msg.obj = bitmap;
                        handler.sendMessage(msg);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
