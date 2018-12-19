package com.guodongbaohe.app.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.guodongbaohe.app.bean.ClipBean;
import com.guodongbaohe.app.bean.GaoYongJinBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.dialogfragment.BaseNiceDialog;
import com.guodongbaohe.app.dialogfragment.NiceDialog;
import com.guodongbaohe.app.dialogfragment.ViewConvertListener;
import com.guodongbaohe.app.dialogfragment.ViewHolder;
import com.guodongbaohe.app.itemdecoration.PinLeiItemDecoration;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.share.OnekeyShare;
import com.guodongbaohe.app.util.DensityUtils;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.EncryptUtil;
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
import com.guodongbaohe.app.util.VersionUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
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
    String member_role, son_count, goods_id, coupon_click_url;
    BigDecimal bg3;
    private boolean isShowTuijian = true;
    private boolean isShowFanli = true;
    private boolean isShowTaokouling = true;
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
        setMiddleTitle("创建分享");
        recyclerview.setHasFixedSize(true);
        recyclerview.setNestedScrollingEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerview.setLayoutManager(manager);
        initRecyclerview();
        initViewData();
    }

    private void getGaoYongJinData() {
        loadingDialog = DialogUtil.createLoadingDialog(CreationShareActivity.this, "加载...");
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("goods_id", goods_id);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.GAOYONGIN + "?" + param)
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
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                GaoYongJinBean bean = GsonUtil.GsonToBean(response.toString(), GaoYongJinBean.class);
                                if (bean == null) return;
                                coupon_click_url = bean.getResult().getCoupon_click_url();
                                getTaoKouLing();
                            } else {
                                setQrCodeDefaultView();
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

    private void getTaoKouLing() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("url", coupon_click_url);
        map.put("goods_id", goods_id);
        map.put("attr_prime", attr_prime);
        map.put("attr_price", attr_price);
        map.put("text", goods_name);
        map.put("logo", pics_list.get(0));
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.GETTAOKOULING + "?" + param)
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
                        Log.i("淘口令", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                taokouling = jsonObject.getString("result");
                                taobao_ling.setText("【淘口令】" + taokouling);
                                getQrcode(taokouling);
                            } else {
                                setQrCodeDefaultView();
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

    private void initViewData() {
        if (TextUtils.isEmpty(good_short)) {
            shop_title.setText(goods_name);
        } else {
            shop_title.setText(good_short);
        }
        shop_original_price.setText("【原价】¥" + StringCleanZeroUtil.DoubleFormat(Double.valueOf(attr_prime)));
        shop_coupon_price.setText("【券后价】¥" + StringCleanZeroUtil.DoubleFormat(Double.valueOf(attr_price)));
        if (member_role.equals("2")) {
            rebateData(90);
        } else if (member_role.equals("1")) {
            rebateData(82);
        } else {
            if (son_count.equals("0")) {
                rebateData(40);
            } else {
                rebateData(50);
            }
        }
        if (TextUtils.isEmpty(promo_slogan)) {
            tv_tuijian_view_line.setVisibility(View.GONE);
            recommend_result.setVisibility(View.GONE);
        } else {
            tv_tuijian_view_line.setVisibility(View.VISIBLE);
            recommend_result.setVisibility(View.VISIBLE);
            recommend_result.setText("【推荐理由】" + promo_slogan);
        }
    }

    private void rebateData(int num) {
        double ninengfan = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * 50 / 10000 * app_v;
        bg3 = new BigDecimal(ninengfan);
        double money_ninneng = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        rebate.setText("【下载果冻宝盒购买】你能返¥" + money_ninneng);
        double result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * num / 10000 * app_v;
        bg3 = new BigDecimal(result);
        double money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        share_money.setText("分享赚¥" + money + "元");
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
        TextView p_title = view.findViewById(R.id.p_title);
        TextView p_coupon_price = view.findViewById(R.id.p_coupon_price);
        TextView tv_coupon = view.findViewById(R.id.tv_coupon);
        p_iv = view.findViewById(R.id.p_iv);
        TextView p_one_price = view.findViewById(R.id.p_one_price);
        iv_qr_code = view.findViewById(R.id.iv_qr_code);
        IconAndTextGroupUtil.setTextView(getApplicationContext(), p_title, goods_name, attr_site);
        p_coupon_price.setText(StringCleanZeroUtil.DoubleFormat(Double.valueOf(attr_price)));
        double d_price = Double.valueOf(attr_prime) - Double.valueOf(attr_price);
        bg3 = new BigDecimal(d_price);
        double d_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        tv_coupon.setText(StringCleanZeroUtil.DoubleFormat(d_money) + "元券");
        p_one_price.setText("¥" + StringCleanZeroUtil.DoubleFormat(Double.valueOf(attr_price)));
        /*高佣金接口*/
        getGaoYongJinData();
    }

    List<Integer> allPosition;
    List<String> share_img_str;
    String copy_fanli, copy_taokouling, copy_tuijian_liyou;

    @OnClick({R.id.ll_change_rq_code, R.id.re_show_fanli, R.id.re_show_comment_result, R.id.re_show_tkl_result, R.id.share_money, R.id.copy, R.id.order_shuoming})
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
                intent.putStringArrayListExtra("pics", (ArrayList<String>) pics_list);
                startActivityForResult(intent, 100);
                break;
            case R.id.re_show_fanli:
                if (isShowFanli) {
                    iv_tao_show.setImageResource(R.mipmap.buxianshiyjin);
                    rebate.setVisibility(View.GONE);
                } else {
                    iv_tao_show.setImageResource(R.mipmap.xainshiyjin);
                    rebate.setVisibility(View.VISIBLE);
                }
                isShowFanli = !isShowFanli;
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
                if (chooseImagsNumList.size() == 0) {
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
                    int ainteger = 0;
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
                intent.putExtra("url", PreferUtils.getString(getApplicationContext(), "shareGoods"));
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
        if (isShowFanli) {
            copy_fanli = rebate.getText().toString().trim();
        }
        if (isShowTaokouling) {
            copy_taokouling = taobao_ling.getText().toString().trim();
            taobaoziti = tv_taobao.getText().toString().trim();
        }
        if (isShowTuijian) {
            copy_tuijian_liyou = recommend_result.getText().toString().trim();
        }
        copy_cotent = copy_title + "\n" + copy_yuajia + "\n" + copy_coupon_jia + "\n";
        if (isShowFanli) {
            copy_cotent = copy_cotent + copy_fanli + "\n";
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
        ClipBean clipBean = new ClipBean();
        clipBean.setTitle(copy_cotent);
        clipBean.save();
        if (type == 0) {
            ToastUtils.showToast(getApplicationContext(), "文案复制成功");
        }
    }

    /*分享类型*/
    private int share_type = 0;

    private void morePicsShareDialog() {
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
                                if (ContextCompat.checkSelfPermission(CreationShareActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                        || ContextCompat.checkSelfPermission(CreationShareActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    //没有存储权限
                                    ActivityCompat.requestPermissions(CreationShareActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2699);
                                    dialog.dismiss();
                                } else {
                                    dialog.dismiss();
                                    sharePics(0, promo_slogan, "wchat");
                                }
                            }
                        });
                        re_wchat_circle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*微信朋友圈分享*/
                                share_type = 1;
                                if (ContextCompat.checkSelfPermission(CreationShareActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                        || ContextCompat.checkSelfPermission(CreationShareActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    //没有存储权限
                                    ActivityCompat.requestPermissions(CreationShareActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2699);
                                    dialog.dismiss();
                                } else {
                                    dialog.dismiss();
                                    sharePics(1, promo_slogan, "wchat");
                                }
                            }
                        });
                        re_qq_friend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*qq好友*/
                                share_type = 2;
                                if (ContextCompat.checkSelfPermission(CreationShareActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                        || ContextCompat.checkSelfPermission(CreationShareActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    //没有存储权限
                                    ActivityCompat.requestPermissions(CreationShareActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2699);
                                    dialog.dismiss();
                                } else {
                                    dialog.dismiss();
                                    sharePics(0, promo_slogan, "qq");
                                }
                            }
                        });
                        re_qq_space.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*qq空间*/
                                share_type = 3;
                                if (ContextCompat.checkSelfPermission(CreationShareActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                        || ContextCompat.checkSelfPermission(CreationShareActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    //没有存储权限
                                    ActivityCompat.requestPermissions(CreationShareActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2699);
                                    dialog.dismiss();
                                } else {
                                    dialog.dismiss();
                                    sharePics(1, promo_slogan, "qq_zone");
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
                    switch (share_type) {
                        case 0:
                            sharePics(0, promo_slogan, "wchat");
                            break;
                        case 1:
                            sharePics(1, promo_slogan, "wchat");
                            break;
                        case 2:
                            sharePics(0, promo_slogan, "qq");
                            break;
                        case 3:
                            sharePics(1, promo_slogan, "qq_zone");
                            break;
                    }
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
            OnekeyShare oks = new OnekeyShare();
            oks.disableSSOWhenAuthorize();
            if (i == 0) {
                Bitmap bitmap = NetPicsToBitmap.convertStringToIcon(share_img_str.get(0));
                oks.setImageData(bitmap);
            } else {
                oks.setImageUrl(share_img_str.get(0));
            }
            oks.setCallback(new OneKeyShareCallback());
            oks.show(getApplicationContext());
        }
    }

    private class OneKeyShareCallback implements PlatformActionListener {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
        }

        @Override
        public void onCancel(Platform platform, int i) {
        }
    }

    /*生成二维码接口*/
    private void getQrcode(String taokl) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("type", "goods");
        map.put("words", taokl);
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
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                /*二维码结果*/
                                String result = jsonObject.getString("result");
                                Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap(result, DensityUtils.dip2px(getApplicationContext(), 100));
                                iv_qr_code.setImageBitmap(mBitmap);
                                Glide.with(getApplicationContext()).load(pics_list.get(0)).asBitmap().into(target);
                            } else {
                                setQrCodeDefaultView();
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

    private void setQrCodeDefaultView() {
        Bitmap mBitmap = QRCodeUtil.createQRCodeBitmap("赶紧去各大应用市场下载果冻宝盒APP", 500);
        iv_qr_code.setImageBitmap(mBitmap);
        Glide.with(getApplicationContext()).load(pics_list.get(0)).asBitmap().into(target);
    }

}
