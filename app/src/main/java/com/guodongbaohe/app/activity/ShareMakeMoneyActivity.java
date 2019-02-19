package com.guodongbaohe.app.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.GalleryPicsAdapter;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.dialogfragment.BaseNiceDialog;
import com.guodongbaohe.app.dialogfragment.NiceDialog;
import com.guodongbaohe.app.dialogfragment.ViewConvertListener;
import com.guodongbaohe.app.dialogfragment.ViewHolder;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.share.OnekeyShare;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ShareManager;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

public class ShareMakeMoneyActivity extends BaseActivity {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    String goods_thumb, goods_gallery, goods_name, promo_slogan, attr_price, attr_prime, member_id, coupon_url, taokouling, content;
    private List<String> pics_list;
    @BindView(R.id.describe)
    TextView describe;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.sale_price)
    TextView sale_price;
    @BindView(R.id.catch_price)
    TextView catch_price;
    @BindView(R.id.coupon_money)
    TextView coupon_money;
    @BindView(R.id.tv_taokouling)
    TextView tv_taokouling;
    @BindView(R.id.re_show_tao)
    RelativeLayout re_show_tao;
    private boolean isShowTao = false;
    @BindView(R.id.iv_tao_show)
    ImageView iv_tao_show;
    @BindView(R.id.tv_fuzhitaokoul)
    TextView tv_fuzhitaokoul;
    @BindView(R.id.tv_share)
    TextView tv_share;
    @BindView(R.id.tv_taobao)
    TextView tv_taobao;
    /*分享类型*/
    private int share_type = 0;

    @Override
    public int getContainerView() {
        return R.layout.sharemakemoneyactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("创建分享");
        Intent intent = getIntent();
        goods_thumb = intent.getStringExtra("goods_thumb");
        goods_gallery = intent.getStringExtra("goods_gallery");
        goods_name = intent.getStringExtra("goods_name");
        promo_slogan = intent.getStringExtra("promo_slogan");
        attr_price = intent.getStringExtra("attr_price");
        attr_prime = intent.getStringExtra("attr_prime");
        coupon_url = intent.getStringExtra("coupon_url");
        member_id = PreferUtils.getString(getApplicationContext(), "member_id");
        initView();
        getTaoKouLing();
    }

    private void initView() {
        describe.setText(promo_slogan);
        title.setText(goods_name);
        sale_price.setText("【在售价】¥" + attr_prime);
        catch_price.setText("【抢购价】¥" + attr_price);
        pics_list = new ArrayList<>();
        if (TextUtils.isEmpty(goods_gallery)) {
            pics_list.add(goods_thumb);
        } else {
            String[] split = goods_gallery.split(",");
            for (int i = 0; i < split.length; i++) {
                pics_list.add(split[i]);
            }
        }
        recyclerview.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerview.setLayoutManager(manager);
        GalleryPicsAdapter adapter = new GalleryPicsAdapter(getApplicationContext(), pics_list);
        recyclerview.setAdapter(adapter);
        adapter.setonclicklistener(new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                Intent intent = new Intent(getApplicationContext(), TransparencyPicsActivity.class);
                intent.putExtra("position", position);
                intent.putStringArrayListExtra("pics", (ArrayList<String>) pics_list);
                startActivity(intent);
                overridePendingTransition(R.anim.ap2, R.anim.ap1);
            }
        });
    }

    @OnClick({R.id.re_show_tao, R.id.tv_fuzhitaokoul, R.id.tv_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.re_show_tao:
                if (!isShowTao) {
                    iv_tao_show.setImageResource(R.mipmap.xainshiyjin);
                    coupon_money.setVisibility(View.VISIBLE);
                } else {
                    iv_tao_show.setImageResource(R.mipmap.buxianshiyjin);
                    coupon_money.setVisibility(View.GONE);
                }
                isShowTao = !isShowTao;
                break;
            case R.id.tv_fuzhitaokoul:
                content = "";
                content = title.getText().toString().trim() + "\n"
                        + sale_price.getText().toString().trim() + "\n"
                        + catch_price.getText().toString().trim() + "\n"
                        + coupon_money.getText().toString().trim() + "\n"
                        + tv_taokouling.getText().toString().trim() + "\n"
                        + tv_taobao.getText().toString().trim();
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm.hasPrimaryClip()) {
                    cm.getPrimaryClip().getItemAt(0).getText();
                }
                ClipData mClipData = ClipData.newPlainText("Label", content);
                cm.setPrimaryClip(mClipData);
                ToastUtils.showToast(getApplicationContext(), "复制成功");
                break;
            case R.id.tv_share:
                if (pics_list.size() == 1) {
                    /*这里分单张图片分享 用sharessdk实现*/
                    shareSDkDialog();
                } else {
                    /*多张图片分享用原生实现*/
                    morePicsShareDialog();
                }
                break;
        }
    }

    private void shareSDkDialog() {
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();
        oks.setText(goods_name);
        oks.setText(promo_slogan);
        oks.setImageUrl(pics_list.get(0));
        oks.setSite(getString(R.string.app_name));
        oks.setCallback(new OneKeyShareCallback());
        oks.show(getApplicationContext());
    }

    /*多张图片分享*/
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
                                if (ContextCompat.checkSelfPermission(ShareMakeMoneyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                        || ContextCompat.checkSelfPermission(ShareMakeMoneyActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    //没有存储权限
                                    ActivityCompat.requestPermissions(ShareMakeMoneyActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2699);
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
                                if (ContextCompat.checkSelfPermission(ShareMakeMoneyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                        || ContextCompat.checkSelfPermission(ShareMakeMoneyActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    //没有存储权限
                                    ActivityCompat.requestPermissions(ShareMakeMoneyActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2699);
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
                                if (ContextCompat.checkSelfPermission(ShareMakeMoneyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                        || ContextCompat.checkSelfPermission(ShareMakeMoneyActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    //没有存储权限
                                    ActivityCompat.requestPermissions(ShareMakeMoneyActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2699);
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
                                if (ContextCompat.checkSelfPermission(ShareMakeMoneyActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                                        || ContextCompat.checkSelfPermission(ShareMakeMoneyActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    //没有存储权限
                                    ActivityCompat.requestPermissions(ShareMakeMoneyActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2699);
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
//        ShareManager shareManager = new ShareManager(ShareMakeMoneyActivity.this);
//        shareManager.setShareImage(i, pics_list, content, type);
    }

    private void getTaoKouLing() {
        long timelineStr = System.currentTimeMillis() / 1000;
        HashMap<String, String> map = new HashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("url", coupon_url);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.GETTAOKOULING + "?" + param)
                .tag(this)
                .addHeader("APPID", Constant.APPID)
                .addHeader("APPKEY", Constant.APPKEY)
                .addHeader("x-userid", member_id)
                .addHeader("VERSION", VersionUtil.getVersionCode(getApplicationContext()))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getString("status").equals("0")) {
                                taokouling = jsonObject.getString("result");
                                tv_taokouling.setText("复制这条信息" + taokouling);
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

    private class OneKeyShareCallback implements PlatformActionListener {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            Log.i("分享完成", i + "");
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            Log.i("分享失败", i + "");
        }

        @Override
        public void onCancel(Platform platform, int i) {
            Log.i("分享取消", i + "");
        }
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

        }
    }

}
