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

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.TimeZone;
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
    TextView copy_lingpai; //复制
    @BindView(R.id.youxiaoqi)
    TextView youxiaoqi;
    Dialog loadingDialog;
    SimpleDateFormat format;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("果冻令牌");
        iv_right=(ImageView)findViewById(R.id.iv_right);
        setRightIVVisible();
        iv_right.setImageResource(R.mipmap.refish_h);
        //启动定时器
        timer.schedule(task, 0, 2*60*1000);
         format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                loadingDialog = DialogUtil.createLoadingDialog(GetTokenActivity.this, "...");
                getTokenData();
                ToastUtils.showToast(GetTokenActivity.this,"刷新成功！");
            }
        });
        copy_lingpai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(vc_centerLine.getVcText().toString())){

                    CopyToClipboard(GetTokenActivity.this, vc_centerLine.getVcText().toString());
                    copy_lingpai.setText("复制成功");
                }

            }
        });
        vc_centerLine.setEnabled(false);
    }
    public Date StrToData(String str){

        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    private Handler handler  = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what== 1){
                getTokenData();
            }
        }
    };


    private Timer timer = new Timer(true);

    //任务
    private TimerTask task = new TimerTask() {
        public void run() {
            Message msg = new Message();
            msg.what= 1;
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
    int exp_time;
    private void getTokenData(){
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
                        DialogUtil.closeDialog(loadingDialog);
                        Log.i("手机令牌数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                TokenBean get_token = GsonUtil.GsonToBean(response.toString(), TokenBean.class);
                                if (get_token==null)return;
                                TokenBean.ResultBean bean=get_token.getResult();
                                if (bean==null)return;
                                vc_centerLine.setVcText(bean.getCode());
                                exp_time=bean.getExp_time();
//                                String date=getDateToString(exp_time);
                                String SSSSSS=DateUtils.getTimeToString(exp_time*1000);
                                Log.i("时间格式1",SSSSSS);
//                                String exptime=dateToStrLong(date);
                                youxiaoqi.setText(SSSSSS);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        DialogUtil.closeDialog(loadingDialog);
                        ToastUtils.showToast(getContext(), Constant.NONET);
                    }
                });
    }
    /*时间戳转换成字符窜*/
    public static String getDateToString(long time) {
//        Date d = new Date(time);
//        SimpleDateFormat  mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        return mSimpleDateFormat.format(d);
        TimeZone tz = TimeZone.getTimeZone("ETC/GMT-8");
        TimeZone.setDefault(tz);
        SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        return df.format(Long.valueOf(time));
    }
    // 时间戳转字符串  1503991612952 ==> 2017-08-29 15:26:52  时间戳 是long 类型
    public static String timeToString(long s){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = sdf.parse(sdf.format(new Date(s)));
            //Date date = sdf.parse(sdf.format(new Long(s)));// 等价于
            return sdf.format(date);
        } catch (NumberFormatException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        return null;
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
