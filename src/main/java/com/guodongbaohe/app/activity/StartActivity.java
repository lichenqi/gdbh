package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.guodongbaohe.app.MainActivity;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.bean.CommonBean;
import com.guodongbaohe.app.bean.NoticeBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.SpUtil;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StartActivity extends BigBaseActivity {
    Intent intent;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.re_parent)
    RelativeLayout re_parent;
    private TimeCount countdownTime = new TimeCount(4000, 1000);
    boolean isFirst;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advertisementactivity);
        ButterKnife.bind(this);
        /*获取头部分类标题*/
        getClassicHeadTitle();
        /*获取小提示数据*/
        getNoticeData();
        isFirst = PreferUtils.getBoolean(getApplicationContext(), "isFirst");
        countdownTime.start();
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainActivity();
            }
        });
        re_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainActivity();
            }
        });
    }

    private void toMainActivity() {
        if (!isFirst) {
            intent = new Intent(getApplicationContext(), GuideActivity.class);
            startActivity(intent);
            PreferUtils.putBoolean(getApplicationContext(), "isFirst", true);
        } else {
            intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
        finish();
    }

    private class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            time.setText("跳过" + millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            toMainActivity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countdownTime != null) {
            countdownTime.cancel();
        }
    }

    List<CommonBean.CommonResult> result_list;

    private void getClassicHeadTitle() {
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.GOODS_CATES)
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
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            Log.i("数据啊", response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                CommonBean bean = GsonUtil.GsonToBean(response.toString(), CommonBean.class);
                                if (bean == null) return;
                                result_list = bean.getResult();
                                SpUtil.putList(getApplicationContext(), "head_title_list", result_list);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        Toast.makeText(getApplicationContext(), Constant.NONET, Toast.LENGTH_LONG);
                    }
                });
    }

    private void getNoticeData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", "notice");
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.NOTICE + param)
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
                        Log.i("小提示数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                NoticeBean bean = GsonUtil.GsonToBean(response.toString(), NoticeBean.class);
                                if (bean == null) return;
                                String title = bean.getResult().getTitle();
                                PreferUtils.putString(getApplicationContext(), "notice_title", title);
                                PreferUtils.putString(getApplicationContext(), "notice_url", bean.getResult().getUrl());
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

}
