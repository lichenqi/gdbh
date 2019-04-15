package com.guodongbaohe.app.activity;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.WchatBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.ClipContentUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GGfwChatActivity extends BaseActivity {
    @BindView(R.id.tv_gf_wechat)
    TextView tv_gf_wechat;
    @BindView(R.id.cope_weixin)
    Button cope_weixin;

    @Override
    public int getContainerView() {
        return R.layout.gf_wchat;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("客服服务");
        getDataWchat();
    }

    public void getDataWchat() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        String param = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(param + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String mapParam = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.GF_WCHAT + "?" + mapParam)
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
                        Log.i("账号信息", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                WchatBean bean = GsonUtil.GsonToBean(response.toString(), WchatBean.class);
                                if (!TextUtils.isEmpty(bean.getResult().getWechat())) {
                                    tv_gf_wechat.setText(bean.getResult().getWechat());
                                } else {
                                    tv_gf_wechat.setText("暂无官方微信");
                                }

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

    @OnClick({R.id.cope_weixin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cope_weixin:
                if (!tv_gf_wechat.getText().equals("暂无官方微信")) {
                    CopyToClipboard(this, tv_gf_wechat.getText().toString());
                    ToastUtils.showBackgroudCenterToast(getApplicationContext(), "复制成功");
                }
                break;
        }
    }

    public void CopyToClipboard(Context context, String text) {
        ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        //clip.getText(); // 粘贴
        clip.setText(text); // 复制
        if (!TextUtils.isEmpty(text)) {
            ClipContentUtil.getInstance(getApplicationContext()).putNewSearch(text);//保存记录到数据库
        }
    }
}
