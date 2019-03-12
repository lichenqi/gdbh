package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.guodongbaohe.app.util.DensityUtils;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.IconAndTextGroupUtil;
import com.guodongbaohe.app.util.NetPicsToBitmap;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.QRCodeUtil;
import com.guodongbaohe.app.util.StringCleanZeroUtil;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        getTemplateData();/*获取模板数据*/
        initTemplateDataView();/*初始化本地数据*/
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
        /*线一显示*/
        if (TextUtils.isEmpty(content_line_one)) {
            tv_view_line_one.setText("----------");
        } else {
            tv_view_line_one.setText(content_line_one);
        }
        /*标题显示*/
        if (TextUtils.isEmpty(content_title_two)) {
            if (TextUtils.isEmpty(good_short)) {
                shop_title.setText("【" + goods_name + "】");
            } else {
                shop_title.setText("【" + good_short + "】");
            }
        } else {
            String start_tile = content_title_two.substring(0, content_title_two.indexOf("{"));
            String end_tile = content_title_two.substring(content_title_two.indexOf("}") + 1);
            if (TextUtils.isEmpty(good_short)) {
                shop_title.setText(start_tile + goods_name + end_tile);
            } else {
                shop_title.setText(start_tile + good_short + end_tile);
            }
        }
        /*原价显示*/
        if (TextUtils.isEmpty(content_sale_price_three)) {
            shop_original_price.setText("【在售】¥" + StringCleanZeroUtil.DoubleFormat(Double.valueOf(attr_prime)));
        } else {
            String start_sale_price = content_sale_price_three.substring(0, content_sale_price_three.indexOf("{"));
            shop_original_price.setText(start_sale_price + "¥" + StringCleanZeroUtil.DoubleFormat(Double.valueOf(attr_prime)));
        }
        /*券后价显示*/
        if (TextUtils.isEmpty(content_coupon_four)) {
            shop_coupon_price.setText("【券后限时秒杀】¥" + StringCleanZeroUtil.DoubleFormat(Double.valueOf(attr_price)));
        } else {
            String start_coupon_price = content_coupon_four.substring(0, content_coupon_four.indexOf("{"));
            shop_coupon_price.setText(start_coupon_price + "¥" + StringCleanZeroUtil.DoubleFormat(Double.valueOf(attr_price)));
        }
        /*线二显示*/
        if (TextUtils.isEmpty(content_line_five)) {
            tv_view_line_two.setText("----------");
        } else {
            tv_view_line_two.setText(content_line_five);
        }
        /*下单链接显示*/
        if (TextUtils.isEmpty(content_order_six)) {
            tv_order_addrress.setText(share_qrcode);
        } else {
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
            tv_view_line_three.setText("----------");
        } else {
            tv_view_line_three.setText(content_line_seven);
        }
        /*淘口令显示*/
        if (TextUtils.isEmpty(content_taobao_eight)) {
            taobao_ling.setText("复制此条评论，(" + share_taokouling + ")，➡[淘✔寳]即可把我带回家");
        } else {
            if (content_taobao_eight.contains(taokouling_sign)) {
                String start_order = content_taobao_eight.substring(0, content_taobao_eight.indexOf("{"));
                String end_order = content_taobao_eight.substring(content_taobao_eight.indexOf("}") + 1);
                taobao_ling.setText(start_order + share_taokouling + end_order);
            } else {
                taobao_ling.setText(content_taobao_eight);
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
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {

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

    @OnClick({R.id.tv_buide_poster, R.id.edit_comment_template})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_buide_poster:/*点击生成海报按钮*/
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
        }
    }

    String content_line_one, content_title_two, content_sale_price_three, content_coupon_four, content_line_five,
            content_order_six, content_line_seven, content_taobao_eight;

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
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(10000, View.MeasureSpec.AT_MOST);
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
        adapter.refreDataChange(1);
        buidePoster = 1;
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

}
