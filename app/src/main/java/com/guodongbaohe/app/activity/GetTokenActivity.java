package com.guodongbaohe.app.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.TokenBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.ClipContentUtil;
import com.guodongbaohe.app.util.DateUtils;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.view.VerifyCodeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.guodongbaohe.app.common_constant.MyApplication.getContext;

public class GetTokenActivity extends BaseActivity {
    ImageView iv_right;
    @BindView(R.id.vc_centerLine)
    VerifyCodeView vc_centerLine;
    @BindView(R.id.copy_lingpai)
    TextView copy_lingpai; //复制令牌
    @BindView(R.id.copy_guanwang)
    TextView copy_guanwang; //复制官网
    @BindView(R.id.guanwang_address)
    TextView guanwang_address;
    @BindView(R.id.youxiaoqi)
    TextView youxiaoqi;
    SimpleDateFormat format;
    @BindView(R.id.shuoming)
    TextView shuoming;
    @BindView(R.id.is_boss)
    LinearLayout is_boss;
    private Timer timer = new Timer(true);
    Dialog dialog;
    int flag = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("果冻令牌");
        iv_right = (ImageView) findViewById(R.id.iv_right);
        setRightIVVisible();
        iv_right.setImageResource(R.mipmap.refish_h);
        String user_code = getIntent().getStringExtra("user_code");
        if (TextUtils.equals(user_code, "总裁")) {
            is_boss.setVisibility(View.VISIBLE);
        }
        shuoming.setText(PreferUtils.getString(GetTokenActivity.this, "app_token_desc"));
        //启动定时器
        timer.schedule(task, 0, 2 * 60 * 1000);
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = DialogUtil.createLoadingDialog(GetTokenActivity.this, "加载...");
                getTokenData();
                if (flag == 1) {
                    ToastUtils.showToast(GetTokenActivity.this, "刷新成功！");
                }

            }
        });

        copy_lingpai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(vc_centerLine.getVcText().toString())) {
                    CopyToClipboard(GetTokenActivity.this, vc_centerLine.getVcText().toString());
                    ToastUtils.showToast(GetTokenActivity.this, "复制成功");
                    copy_lingpai.setText("复制成功");
                }

            }
        });
        copy_guanwang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CopyToClipboard(GetTokenActivity.this, guanwang_address.getText().toString());
                ToastUtils.showToast(GetTokenActivity.this, "复制成功");
            }
        });
        vc_centerLine.setEnabled(false);
        vc_centerLine.setFocusable(false);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                iv_right.performClick();
            }
        }
    };

    //任务
    private TimerTask task = new TimerTask() {
        public void run() {
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
        }
    };

    @Override
    public int getContainerView() {
        return R.layout.get_token;
    }

    @Override
    protected void onResume() {
        getTokenData();
        super.onResume();
    }

    String exp_time;

    private void getTokenData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getContext(), "member_id"));
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.GET_TOKEN + "?" + param)
                .tag(this)
                .addHeader("x-userid", PreferUtils.getString(getContext(), "member_id"))
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getContext(), PreferUtils.getString(getContext(), "member_id")))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("手机令牌数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                TokenBean get_token = GsonUtil.GsonToBean(response.toString(), TokenBean.class);
                                if (get_token == null) return;
                                TokenBean.ResultBean bean = get_token.getResult();
                                if (bean == null) return;
                                vc_centerLine.setVcText(bean.getCode());
                                exp_time = bean.getExp_time();
                                String SSSSSS = DateUtils.getTimeToString(Long.valueOf(exp_time) * 1000);
                                youxiaoqi.setText(SSSSSS);
                                flag = 1;
                                DialogUtil.closeDialog(dialog, GetTokenActivity.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast(getContext(), Constant.NONET);
                        DialogUtil.closeDialog(dialog, GetTokenActivity.this);
                    }
                });
    }

    public void CopyToClipboard(Context context, String text) {
        ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clip.setText(text); // 复制
        if (!TextUtils.isEmpty(text)) {
            ClipContentUtil.getInstance(getApplicationContext()).putNewSearch(text);//保存记录到数据库
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            if (task != null) {
                task.cancel();  //将原任务从队列中移除
            }
        }
        DialogUtil.closeDialog(dialog, GetTokenActivity.this);
    }
}
