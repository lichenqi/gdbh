package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.guodongbaohe.app.adapter.YaoQingFriendAdapter;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.ClipBean;
import com.guodongbaohe.app.bean.InviteAwardBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.itemdecoration.YingXiaoVipItem;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.share.OnekeyShare;
import com.guodongbaohe.app.util.DensityUtils;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.NetUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.QRCodeUtil;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

public class YaoQingFriendActivity extends BaseActivity {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.re_copy)
    RelativeLayout re_copy;
    Bitmap qrCodeBitmap;
    @BindView(R.id.re_share)
    RelativeLayout re_share;
    View share_view;
    int space;
    ImageView share_iv;
    private int currentPosition = 0;
    ImageView share_qrcode;

    @Override
    public int getContainerView() {
        return R.layout.yaoqingfriendactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("邀请好友");
        recyclerview.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerview.setLayoutManager(manager);
        space = DensityUtils.dip2px(getApplicationContext(), 15);
        share_view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.share_view_invite_code, null);
        share_iv = (ImageView) share_view.findViewById(R.id.iv);
        TextView share_code = (TextView) share_view.findViewById(R.id.code);
        share_qrcode = (ImageView) share_view.findViewById(R.id.qrcode);
        share_code.setText("邀请码:" + PreferUtils.getString(getApplicationContext(), "invite_code"));
        /*二维码内容*/
        getQrCodeData();
    }

    Dialog loadingDialog;

    private void getQrCodeData() {
        loadingDialog = DialogUtil.createLoadingDialog(YaoQingFriendActivity.this, "加载...");
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("platform", "android");
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.INVITEFRIENDHAIBAO + "?" + param)
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
                        Log.i("二维码", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                String qrcode_result = jsonObject.getString("result");
                                qrCodeBitmap = QRCodeUtil.createQRCodeBitmap(qrcode_result, DensityUtils.dip2px(getApplicationContext(), 120));
                                share_qrcode.setImageBitmap(qrCodeBitmap);
                                getData();
                            } else {
                                String result = jsonObject.getString("result");
                                ToastUtils.showToast(getApplicationContext(), result);
                                DialogUtil.closeDialog(loadingDialog);
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


    @OnClick({R.id.re_share, R.id.re_copy})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.re_share:
                if (NetUtil.getNetWorkState(YaoQingFriendActivity.this) < 0) {
                    ToastUtils.showToast(getApplicationContext(), "您的网络异常，请联网重试");
                    return;
                }
                if (result.size() > 0) {
                    loadingDialog = DialogUtil.createLoadingDialog(YaoQingFriendActivity.this, "正在加载...");
                    Glide.with(getApplicationContext()).load(result.get(currentPosition).getImage()).asBitmap().into(target);
                }
                break;
            case R.id.re_copy:
                ClipBean clipBean = new ClipBean();
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData mClipData = ClipData.newPlainText("Label", PreferUtils.getString(getApplicationContext(), "share_friends_title"));
                cm.setPrimaryClip(mClipData);
                ToastUtils.showToast(getApplicationContext(), "邀请链接复制成功");
                clipBean.setTitle(PreferUtils.getString(getApplicationContext(), "share_friends_title"));
                clipBean.save();
                break;
        }
    }

    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            share_iv.setImageBitmap(bitmap);
            getViewToPics(share_view);
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
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(DensityUtils.dip2px(getApplicationContext(), 600), View.MeasureSpec.AT_MOST);
        v.measure(measuredWidth, measuredHeight);
//        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        viewSaveToImage(v);
    }

    Bitmap hebingBitmap;
    OnekeyShare oks;

    private void viewSaveToImage(View view) {
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheBackgroundColor(Color.WHITE);
        // 把一个View转换成图片
        hebingBitmap = loadBitmapFromView(view);
        DialogUtil.closeDialog(loadingDialog);
        oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();
        oks.setImageData(hebingBitmap);
        oks.setCallback(new OneKeyShareCallback());
        oks.show(getApplicationContext());
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

    List<InviteAwardBean.InviteAwardData> result;
    YaoQingFriendAdapter adapter;

    private void getData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", "qrcode");
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.BANNER + "?" + param)
                .tag(this)
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
                        Log.i("邀请有奖", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                InviteAwardBean bean = GsonUtil.GsonToBean(response.toString(), InviteAwardBean.class);
                                if (bean == null) return;
                                result = bean.getResult();
                                recyclerview.addItemDecoration(new YingXiaoVipItem(space, result.size()));
                                adapter = new YaoQingFriendAdapter(getApplicationContext(), result, qrCodeBitmap);
                                recyclerview.setAdapter(adapter);
                                for (InviteAwardBean.InviteAwardData a : result) {
                                    a.setChoose(false);
                                }
                                result.get(0).setChoose(true);
                                currentPosition = 0;
                                adapter.onclicklistener(new OnItemClick() {
                                    @Override
                                    public void OnItemClickListener(View view, int position) {
                                        currentPosition = position;
                                        for (InviteAwardBean.InviteAwardData bean : result) {
                                            bean.setChoose(false);
                                        }
                                        result.get(position).setChoose(true);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
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

    private class OneKeyShareCallback implements PlatformActionListener {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            Log.i("分享完成", i + "");
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            Log.i("分享失败", i + "" + throwable);
        }

        @Override
        public void onCancel(Platform platform, int i) {
            Log.i("分享取消", i + "");
        }
    }

}
