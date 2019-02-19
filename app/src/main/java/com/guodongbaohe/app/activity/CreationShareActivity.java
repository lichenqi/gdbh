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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.ImagesBitmapAdapter;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.ChooseImagsNum;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.dialogfragment.BaseNiceDialog;
import com.guodongbaohe.app.dialogfragment.NiceDialog;
import com.guodongbaohe.app.dialogfragment.ViewConvertListener;
import com.guodongbaohe.app.dialogfragment.ViewHolder;
import com.guodongbaohe.app.itemdecoration.PinLeiItemDecoration;
import com.guodongbaohe.app.util.ClipContentUtil;
import com.guodongbaohe.app.util.CommonUtil;
import com.guodongbaohe.app.util.DensityUtils;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.IconAndTextGroupUtil;
import com.guodongbaohe.app.util.NetPicsToBitmap;
import com.guodongbaohe.app.util.NetUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.QRCodeUtil;
import com.guodongbaohe.app.util.ShareManager;
import com.guodongbaohe.app.util.StringCleanZeroUtil;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.Tools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class CreationShareActivity extends BaseActivity {
    @BindView(R.id.order_shuoming)
    RelativeLayout order_shuoming;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    /*二维码选择按钮*/
    @BindView(R.id.ll_change_rq_code)
    LinearLayout ll_change_rq_code;
    String goods_thumb, goods_gallery, goods_name, promo_slogan, attr_price, attr_prime, member_id, coupon_url, taokouling, content, attr_site, good_short, attr_ratio;
    private List<String> pics_list;
    ImagesBitmapAdapter adapter;
    private List<String> all_list;
    /*商品标题*/
    @BindView(R.id.shop_title)
    TextView shop_title;
    /*原价*/
    @BindView(R.id.shop_original_price)
    TextView shop_original_price;
    /*券后价*/
    @BindView(R.id.shop_coupon_price)
    TextView shop_coupon_price;
    /*你能返*/
    @BindView(R.id.rebate)
    TextView rebate;
    /*推荐理由*/
    @BindView(R.id.recommend_result)
    TextView recommend_result;
    /*淘口令*/
    @BindView(R.id.taobao_ling)
    TextView taobao_ling;
    /*返利佣金*/
    @BindView(R.id.re_show_fanli)
    RelativeLayout re_show_fanli;
    @BindView(R.id.iv_tao_show)
    ImageView iv_tao_show;
    /*推荐理由显示*/
    @BindView(R.id.re_show_comment_result)
    RelativeLayout re_show_comment_result;
    @BindView(R.id.iv_result_show)
    ImageView iv_result_show;
    /*淘口令显示*/
    @BindView(R.id.re_show_tkl_result)
    RelativeLayout re_show_tkl_result;
    @BindView(R.id.iv_tkl_show)
    ImageView iv_tkl_show;
    /*分享赚*/
    @BindView(R.id.share_money)
    TextView share_money;
    String member_role, son_count, goods_id, share_taokouling, share_qrcode, coupon_surplus;
    BigDecimal bg3;
    private boolean isShowTuijian = true;
    private boolean isShowFanli = true;
    private boolean isShowTaokouling = true;
    private boolean isShowXiaDanDizi = true;
    @BindView(R.id.copy)
    TextView copy;
    double app_v;
    Dialog loadingDialog;
    @BindView(R.id.tv_taobao)
    TextView tv_taobao;
    @BindView(R.id.taobao_view_line)
    TextView taobao_view_line;
    @BindView(R.id.tv_tuijian_view_line)
    TextView tv_tuijian_view_line;
    double v;
    @BindView(R.id.tv_xaidandizi_view_line)
    TextView tv_xaidandizi_view_line;
    @BindView(R.id.tv_xaidandizi)
    TextView tv_xaidandizi;
    @BindView(R.id.re_goumaidizi)
    RelativeLayout re_goumaidizi;
    @BindView(R.id.iv_dizi_show)
    ImageView iv_dizi_show;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int getContainerView() {
        return R.layout.creationshareactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        member_role = PreferUtils.getString(getApplicationContext(), "member_role");
        son_count = PreferUtils.getString(getApplicationContext(), "son_count");
        member_id = PreferUtils.getString(getApplicationContext(), "member_id");
        String tax_rate = PreferUtils.getString(getApplicationContext(), "tax_rate");
        app_v = 1 - Double.valueOf(tax_rate);
        Intent intent = getIntent();
        goods_thumb = intent.getStringExtra("goods_thumb");
        goods_gallery = intent.getStringExtra("goods_gallery");
        goods_name = intent.getStringExtra("goods_name");
        promo_slogan = intent.getStringExtra("promo_slogan");
        attr_price = intent.getStringExtra("attr_price");
        attr_prime = intent.getStringExtra("attr_prime");
        coupon_url = intent.getStringExtra("coupon_url");
        attr_site = intent.getStringExtra("attr_site");
        good_short = intent.getStringExtra("good_short");
        attr_ratio = intent.getStringExtra("attr_ratio");
        goods_id = intent.getStringExtra("goods_id");
        share_taokouling = intent.getStringExtra("share_taokouling");
        share_qrcode = intent.getStringExtra("share_qrcode");
        coupon_surplus = intent.getStringExtra("coupon_surplus");
        v = Double.valueOf(attr_prime) - Double.valueOf(attr_price);
        setMiddleTitle("创建分享");
        recyclerview.setHasFixedSize(true);
        recyclerview.setNestedScrollingEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerview.setLayoutManager(manager);
        initRecyclerview();
        initViewData();
    }

    private void initViewData() {
        if (TextUtils.isEmpty(good_short)) {
            shop_title.setText(goods_name);
        } else {
            shop_title.setText(good_short);
        }

        shop_original_price.setText("【原价】¥" + StringCleanZeroUtil.DoubleFormat(Double.valueOf(attr_prime)));
        if (Double.valueOf(coupon_surplus) > 0) {
            shop_coupon_price.setText("【券后价】¥" + StringCleanZeroUtil.DoubleFormat(Double.valueOf(attr_price)));
        } else {
            if (v > 0) {
                shop_coupon_price.setText("【折后价】¥" + StringCleanZeroUtil.DoubleFormat(Double.valueOf(attr_price)));
            } else {
                shop_coupon_price.setText("【特惠价】¥" + StringCleanZeroUtil.DoubleFormat(Double.valueOf(attr_price)));
            }
        }

        if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
            /*总裁用户*/
            rebateData(90);
        } else if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
            /*合伙人用户*/
            rebateData(70);
        } else if (Constant.VIP_USER_LEVEL.contains(member_role)) {
            /*vip用户*/
            rebateData(40);
        } else {
            rebate.setText("【下载果冻宝盒购买】你能返¥0");
            share_money.setText("立即分享");
        }

        if (TextUtils.isEmpty(promo_slogan)) {
            tv_tuijian_view_line.setVisibility(View.GONE);
            recommend_result.setVisibility(View.GONE);
        } else {
            tv_tuijian_view_line.setVisibility(View.VISIBLE);
            recommend_result.setVisibility(View.VISIBLE);
            recommend_result.setText("【推荐理由】" + promo_slogan);
        }
        tv_xaidandizi.setText("【下单地址】" + share_qrcode);
    }

    private void rebateData(int num) {
        double ninengfan = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * num / 10000 * app_v;
        bg3 = new BigDecimal(ninengfan);
        double money_ninneng = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        rebate.setText("【下载果冻宝盒购买】你能返¥" + money_ninneng);
        share_money.setText("分享赚¥" + money_ninneng + "元");
    }

    ImageView p_iv;
    View view;
    ImageView iv_qr_code;

    private void initRecyclerview() {
        pics_list = new ArrayList<>();
        if (TextUtils.isEmpty(goods_gallery)) {
            pics_list.add(goods_thumb);
        } else {
            String[] split = goods_gallery.split(",");
            for (int i = 0; i < split.length; i++) {
                pics_list.add(split[i]);
            }
        }
        view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.creation_erweima_pic, null);
        TextView p_title = (TextView) view.findViewById(R.id.p_title);
        TextView p_coupon_price = (TextView) view.findViewById(R.id.p_coupon_price);
        TextView tv_coupon = (TextView) view.findViewById(R.id.tv_coupon);
        p_iv = (ImageView) view.findViewById(R.id.p_iv);
        TextView p_one_price = (TextView) view.findViewById(R.id.p_one_price);
        iv_qr_code = (ImageView) view.findViewById(R.id.iv_qr_code);
        TextView tv_jia_type = (TextView) view.findViewById(R.id.tv_jia_type);
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
        taobao_ling.setText("【淘口令】" + share_taokouling);
        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(share_qrcode, DensityUtils.dip2px(getApplicationContext(), 100));
        iv_qr_code.setImageBitmap(mBitmap);
        Glide.with(getApplicationContext()).load(pics_list.get(0)).asBitmap().into(target);
    }

    List<Integer> allPosition;
    List<String> share_img_str;
    String copy_fanli, copy_taokouling, copy_tuijian_liyou, xiadandizi;
    private int ainteger;

    @OnClick({R.id.ll_change_rq_code, R.id.re_show_fanli, R.id.re_show_comment_result, R.id.re_show_tkl_result,
            R.id.share_money, R.id.copy, R.id.order_shuoming, R.id.re_goumaidizi})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_change_rq_code:
                Intent intent = new Intent(getApplicationContext(), ChangeQRCodePicActivity.class);
                intent.putExtra("goods_id", goods_id);
                intent.putExtra("title", goods_name);
                intent.putExtra("attr_site", attr_site);
                intent.putExtra("attr_price", attr_price);
                intent.putExtra("attr_prime", attr_prime);
                intent.putExtra("position", click_position);
                intent.putExtra("coupon_surplus", coupon_surplus);
                intent.putStringArrayListExtra("pics", (ArrayList<String>) pics_list);
                intent.putExtra("share_qrcode", share_qrcode);
                startActivityForResult(intent, 100);
                break;
            case R.id.re_show_fanli:
                if (isShowFanli) {
                    iv_tao_show.setImageResource(R.mipmap.xainshiyjin);
                    rebate.setVisibility(View.VISIBLE);
                } else {
                    iv_tao_show.setImageResource(R.mipmap.buxianshiyjin);
                    rebate.setVisibility(View.GONE);
                }
                isShowFanli = !isShowFanli;
                break;
            case R.id.re_goumaidizi:
                if (isShowXiaDanDizi) {
                    iv_dizi_show.setImageResource(R.mipmap.buxianshiyjin);
                    tv_xaidandizi.setVisibility(View.GONE);
                    tv_xaidandizi_view_line.setVisibility(View.GONE);
                } else {
                    iv_dizi_show.setImageResource(R.mipmap.xainshiyjin);
                    tv_xaidandizi.setVisibility(View.VISIBLE);
                    tv_xaidandizi_view_line.setVisibility(View.VISIBLE);
                }
                isShowXiaDanDizi = !isShowXiaDanDizi;
                break;
            case R.id.re_show_comment_result:
                if (TextUtils.isEmpty(promo_slogan)) {
                    ToastUtils.showToast(getApplicationContext(), "暂无推荐理由");
                    return;
                }
                if (isShowTuijian) {
                    iv_result_show.setImageResource(R.mipmap.buxianshiyjin);
                    recommend_result.setVisibility(View.GONE);
                    tv_tuijian_view_line.setVisibility(View.GONE);
                } else {
                    iv_result_show.setImageResource(R.mipmap.xainshiyjin);
                    recommend_result.setVisibility(View.VISIBLE);
                    tv_tuijian_view_line.setVisibility(View.VISIBLE);
                }
                isShowTuijian = !isShowTuijian;
                break;
            case R.id.re_show_tkl_result:
                if (isShowTaokouling) {
                    iv_tkl_show.setImageResource(R.mipmap.buxianshiyjin);
                    taobao_ling.setVisibility(View.GONE);
                    tv_taobao.setVisibility(View.GONE);
                    taobao_view_line.setVisibility(View.GONE);
                } else {
                    iv_tkl_show.setImageResource(R.mipmap.xainshiyjin);
                    taobao_ling.setVisibility(View.VISIBLE);
                    tv_taobao.setVisibility(View.VISIBLE);
                    taobao_view_line.setVisibility(View.VISIBLE);
                }
                isShowTaokouling = !isShowTaokouling;
                break;
            case R.id.share_money:
                if (NetUtil.getNetWorkState(CreationShareActivity.this) < 0) {
                    ToastUtils.showToast(getApplicationContext(), "您的网络异常，请联网重试");
                    return;
                }
                if (chooseImagsNumList == null) {
                    ToastUtils.showToast(getApplicationContext(), "请稍等，图片正在加载...");
                    return;
                }
                allPosition = new ArrayList<>();
                if (choose_poition.size() == 0) {
                    ToastUtils.showToast(getApplicationContext(), "至少勾选一张图片才能分享");
                    return;
                }
                Collection<Integer> values = choose_poition.values();// 得到全部的value
                Iterator<Integer> iter = values.iterator();
                while (iter.hasNext()) {
                    Integer str = iter.next();
                    allPosition.add(str);
                }
                if (allPosition.size() == 1) {
                    /*sharesdk分享*/
                    share_img_str = new ArrayList<>();
                    ainteger = 0;
                    for (int i = 0; i < allPosition.size(); i++) {
                        ainteger = allPosition.get(i);
                        String url = chooseImagsNumList.get(ainteger).getUrl();
                        share_img_str.add(url);
                    }
                    showShare(ainteger);
                } else {
                    share_img_str = new ArrayList<>();
                    Collections.sort(allPosition);
                    for (int i = 0; i < allPosition.size(); i++) {
                        Integer integer = allPosition.get(i);
                        String url = chooseImagsNumList.get(integer).getUrl();
                        share_img_str.add(url);
                    }
                    /*多张图片分享用原生实现*/
                    morePicsShareDialog();
                }
                getCopyContent(0);
                break;
            case R.id.copy:
                getCopyContent(0);
                break;
            case R.id.order_shuoming:
                intent = new Intent(getApplicationContext(), ShareDetailActivity.class);
                intent.putExtra("url", PreferUtils.getString(getApplicationContext(), "share_goods"));
                startActivity(intent);
                break;
        }
    }

    String taobaoziti;

    /*复制内容*/
    private void getCopyContent(int type) {
        /*全部复制分享文案（复制看得见的）*/
        String copy_cotent = "";
        String copy_title = shop_title.getText().toString().trim();
        String copy_yuajia = shop_original_price.getText().toString().trim();
        String copy_coupon_jia = shop_coupon_price.getText().toString().trim();
        if (!isShowFanli) {
            copy_fanli = rebate.getText().toString().trim();
        }
        if (isShowXiaDanDizi) {
            xiadandizi = tv_xaidandizi.getText().toString().trim();
        }
        if (isShowTaokouling) {
            copy_taokouling = taobao_ling.getText().toString().trim();
            taobaoziti = tv_taobao.getText().toString().trim();
        }
        if (isShowTuijian) {
            copy_tuijian_liyou = recommend_result.getText().toString().trim();
        }
        copy_cotent = copy_title + "\n" + copy_yuajia + "\n" + copy_coupon_jia + "\n";
        if (!isShowFanli) {
            copy_cotent = copy_cotent + copy_fanli + "\n";
        }
        if (isShowXiaDanDizi) {
            copy_cotent = copy_cotent + xiadandizi + "\n";
        }
        if (isShowTuijian) {
            copy_cotent = copy_cotent + copy_tuijian_liyou + "\n";
        }
        if (isShowTaokouling) {
            copy_cotent = copy_cotent + copy_taokouling + "\n" + taobaoziti;
        }
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm.hasPrimaryClip()) {
            cm.getPrimaryClip().getItemAt(0).getText();
        }
        ClipData mClipData = ClipData.newPlainText("Label", copy_cotent);
        cm.setPrimaryClip(mClipData);
        ClipContentUtil.getInstance(getApplicationContext()).putNewSearch(copy_cotent);//保存记录到数据库
        if (type == 0) {
            ToastUtils.showToast(getApplicationContext(), "文案复制成功");
        }
    }

    /*分享类型*/
    private int share_type = 0;

    private void morePicsShareDialog() {
        if (ContextCompat.checkSelfPermission(CreationShareActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(CreationShareActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //没有存储权限
            ActivityCompat.requestPermissions(CreationShareActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2699);
        } else {
            showDuoZhangTuPianDialog();
        }
    }

    /*多张图片弹窗显示*/
    private void showDuoZhangTuPianDialog() {
        NiceDialog.init().setLayoutId(R.layout.morepicssharedialog)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        holder.setOnClickListener(R.id.tv_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        RelativeLayout re_wchat_friend = holder.getView(R.id.re_wchat_friend);
                        RelativeLayout re_wchat_circle = holder.getView(R.id.re_wchat_circle);
                        RelativeLayout re_qq_friend = holder.getView(R.id.re_qq_friend);
                        RelativeLayout re_qq_space = holder.getView(R.id.re_qq_space);
                        re_wchat_friend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*微信好友分享*/
                                share_type = 0;
                                dialog.dismiss();
                                sharePics(0, promo_slogan, "wchat");
                            }
                        });
                        re_wchat_circle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*微信朋友圈分享*/
                                share_type = 1;
                                dialog.dismiss();
//                                sharePics(1, promo_slogan, "wchat");
                                /*由于微信机制不让分享多图直接到微信朋友圈*/
                                WChatCircleDialog();
                                /*包含第一张图*/
                                boolean firstClick = false;
                                for (Map.Entry<Integer, Integer> bean : choose_poition.entrySet()) {
                                    if (bean.getKey() == 0) {
                                        firstClick = true;
                                    }
                                }
                                if (firstClick) {
                                    Bitmap bitmap = NetPicsToBitmap.convertStringToIcon(share_img_str.get(0));
                                    CommonUtil.saveBitmap2file(bitmap, getApplicationContext());
                                    saveMorePhotoToLocal(1);
                                } else {
                                    saveMorePhotoToLocal(0);
                                }
                            }
                        });
                        re_qq_friend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*qq好友*/
                                share_type = 2;
                                dialog.dismiss();
                                sharePics(0, promo_slogan, "qq");
                            }
                        });
                        re_qq_space.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*保存图片*/
                                dialog.dismiss();
                                //TODO
                                /*包含第一张图*/
                                boolean firstClick = false;
                                for (Map.Entry<Integer, Integer> bean : choose_poition.entrySet()) {
                                    if (bean.getKey() == 0) {
                                        firstClick = true;
                                    }
                                }
                                if (firstClick) {
                                    Bitmap bitmap = NetPicsToBitmap.convertStringToIcon(share_img_str.get(0));
                                    CommonUtil.saveBitmap2file(bitmap, getApplicationContext());
                                    saveMorePhotoToLocal(1);
                                } else {
                                    saveMorePhotoToLocal(0);
                                }
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .setOutCancel(true)
                .setAnimStyle(R.style.EnterExitAnimation)
                .show(getSupportFragmentManager());
    }

    /*批量下载图片*/
    private void saveMorePhotoToLocal(final int mode) {
        /*网络路劲存储*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL imageurl;
                try {
                    for (int i = mode; i < share_img_str.size(); i++) {
                        imageurl = new URL(share_img_str.get(i));
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

    private void sharePics(int i, String content, String type) {
        int hava_one = 1;
        ShareManager shareManager = new ShareManager(CreationShareActivity.this);
        for (int k = 0; k < allPosition.size(); k++) {
            if (allPosition.get(k) == 0) {
                hava_one = 0;
            }
        }
        if (hava_one == 0) {
            shareManager.setShareImage(hebingBitmap, i, share_img_str, content, type, hava_one);
        } else {
            shareManager.setShareImage(hebingBitmap, i, share_img_str, content, type, hava_one);
        }
    }

    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            p_iv.setImageBitmap(bitmap);
            getViewToPics(view);
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
    String one_bitmap_s;
    List<ChooseImagsNum> chooseImagsNumList;
    LinkedHashMap<Integer, Integer> choose_poition;

    private void viewSaveToImage(View view) {
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheBackgroundColor(Color.WHITE);
        // 把一个View转换成图片
        hebingBitmap = loadBitmapFromView(view);
        one_bitmap_s = NetPicsToBitmap.convertIconToString(hebingBitmap);
        PreferUtils.putString(getApplicationContext(), "one_bitmap_s", one_bitmap_s);
        all_list = new ArrayList<>();
        all_list.add(one_bitmap_s);
        all_list.addAll(pics_list);
        chooseImagsNumList = new ArrayList<>();
        for (int i = 0; i < all_list.size(); i++) {
            ChooseImagsNum bean = new ChooseImagsNum();
            bean.setUrl(all_list.get(i));
            chooseImagsNumList.add(bean);
        }
        recyclerview.addItemDecoration(new PinLeiItemDecoration(DensityUtils.dip2px(getApplicationContext(), 10), all_list));
        adapter = new ImagesBitmapAdapter(getApplicationContext(), chooseImagsNumList);
        recyclerview.setAdapter(adapter);
        for (ChooseImagsNum bean : chooseImagsNumList) {
            bean.setChecked(false);
        }
        chooseImagsNumList.get(0).setChecked(true);
        adapter.notifyDataSetChanged();
        adapter.setonclicklistener(new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                Intent intent = new Intent(getApplicationContext(), TransparencyPicsActivity.class);
                intent.putExtra("position", position);
                intent.putStringArrayListExtra("pics_list", (ArrayList<String>) pics_list);
                startActivity(intent);
                overridePendingTransition(R.anim.ap2, R.anim.ap1);
            }
        });
        choose_poition = new LinkedHashMap<>();
        choose_poition.put(0, 0);
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
        DialogUtil.closeDialog(loadingDialog);
    }

    int click_position;
    int firstClick = -1;

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case "changeBitmap_s":
                String bitmap_s = PreferUtils.getString(getApplicationContext(), "bitmap_s");
                click_position = PreferUtils.getInt(getApplicationContext(), "click_position");
                PreferUtils.putString(getApplicationContext(), "one_bitmap_s", bitmap_s);
                chooseImagsNumList.get(0).setUrl(bitmap_s);
                adapter.notifyDataSetChanged();
                break;
        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 2699:
                /*存储权限回调*/
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getApplicationContext(), "分享多张图片需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getApplicationContext(), "分享多张图片需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    showDuoZhangTuPianDialog();
                }
                break;
            case 26999:
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getApplicationContext(), "分享单张图片需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getApplicationContext(), "分享单张图片需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    customShareStyle(ainteger);
                }
                break;
        }
    }

    private void showShare(int i) {
        /*先开存储权限*/
        if (ContextCompat.checkSelfPermission(CreationShareActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(CreationShareActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //没有存储权限
            ActivityCompat.requestPermissions(CreationShareActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 26999);
        } else {
            /*自定义九宫格样式*/
            customShareStyle(i);
        }
    }

    private void customShareStyle(final int mode) {
        NiceDialog.init().setLayoutId(R.layout.custom_share_style)
                .setConvertListener(new ViewConvertListener() {
                    @Override
                    public void convertView(ViewHolder holder, final BaseNiceDialog dialog) {
                        holder.setOnClickListener(R.id.tv_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        LinearLayout wcaht_friend = holder.getView(R.id.wcaht_friend);
                        LinearLayout wchat_circle = holder.getView(R.id.wchat_circle);
                        LinearLayout qq_friend = holder.getView(R.id.qq_friend);
                        LinearLayout save_img = holder.getView(R.id.save_img);
                        wcaht_friend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                wchatFriendShare(mode);
                            }
                        });
                        wchat_circle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                wchatFriendCircle(mode);
                            }
                        });
                        qq_friend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                qqFriend(mode);
                            }
                        });
                        save_img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                if (mode == 0) {
                                    Bitmap bitmap = NetPicsToBitmap.convertStringToIcon(share_img_str.get(0));
                                    CommonUtil.saveBitmap2file(bitmap, getApplicationContext());
                                } else {
                                    /*网络路劲存储*/
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            URL imageurl = null;
                                            try {
                                                imageurl = new URL(share_img_str.get(0));
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            }
                                            try {
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
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                }
                            }
                        });
                    }
                })
                .setShowBottom(true)
                .setOutCancel(true)
                .setAnimStyle(R.style.EnterExitAnimation)
                .show(getSupportFragmentManager());
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            CommonUtil.saveBitmap2file(bitmap, getApplicationContext());
        }
    };

    /*微信好友分享*/
    private void wchatFriendShare(int mode) {
        Wechat.ShareParams sp = new Wechat.ShareParams();
        if (mode == 0) {
            Bitmap bitmap = NetPicsToBitmap.convertStringToIcon(share_img_str.get(0));
            sp.setImageData(bitmap);
        } else {
            sp.setImageUrl(share_img_str.get(0));
        }
        sp.setShareType(Platform.SHARE_IMAGE);
        Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
        wechat.setPlatformActionListener(new PlatformActionListener() {
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
        wechat.share(sp);
    }

    /*微信朋友圈分享*/
    private void wchatFriendCircle(int mode) {
        WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
        if (mode == 0) {
            Bitmap bitmap = NetPicsToBitmap.convertStringToIcon(share_img_str.get(0));
            sp.setImageData(bitmap);
        } else {
            sp.setImageUrl(share_img_str.get(0));
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

    /*qq好友*/
    private void qqFriend(int mode) {
        QQ.ShareParams sp = new QQ.ShareParams();
        if (mode == 0) {
            Bitmap bitmap = NetPicsToBitmap.convertStringToIcon(share_img_str.get(0));
            sp.setImageData(bitmap);
        } else {
            sp.setImageUrl(share_img_str.get(0));
        }
        sp.setShareType(Platform.SHARE_IMAGE);
        Platform qq = ShareSDK.getPlatform(QQ.NAME);
        qq.setPlatformActionListener(new PlatformActionListener() {
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
        qq.share(sp);
    }

    Dialog dialog;

    /*多图分享到微信时弹框到微信app*/
    private void WChatCircleDialog() {
        dialog = new Dialog(CreationShareActivity.this, R.style.transparentFrameWindowStyle);
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

}
