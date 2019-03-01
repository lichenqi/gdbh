package com.guodongbaohe.app.common_constant;

import android.app.AppOpsManager;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeInitCallback;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.MyIncomeingActivity;
import com.guodongbaohe.app.activity.MyOrderActivity;
import com.guodongbaohe.app.activity.MyTeamActivity;
import com.guodongbaohe.app.activity.ShouRuMingXiActivity;
import com.guodongbaohe.app.activity.TiXianRecordActivity;
import com.guodongbaohe.app.cliputil.ClipboardUtil;
import com.guodongbaohe.app.myokhttputils.MyOkHttp;
import com.guodongbaohe.app.service.CrashHandler;
import com.guodongbaohe.app.service.NotificationPermissions;
import com.guodongbaohe.app.util.AppUtils;
import com.guodongbaohe.app.util.NetUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.mob.MobSDK;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.mezu.MeizuRegister;
import org.android.agoo.xiaomi.MiPushRegistar;
import org.litepal.LitePal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class MyApplication extends MultiDexApplication {
    private static Context context;
    private static MyApplication mInstance;
    private MyOkHttp mMyOkHttp;
    private DownloadMgr mDownloadMgr;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        mInstance = this;
        //持久化存储cookie
        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext()));
        //log拦截器
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        //自定义OkHttp
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .cookieJar(cookieJar)       //设置开启cookie
                .addInterceptor(logging)            //设置开启log
                .build();
        mMyOkHttp = new MyOkHttp(okHttpClient);
        mDownloadMgr = (DownloadMgr) new DownloadMgr.Builder()
                .myOkHttp(mMyOkHttp)
                .maxDownloadIngNum(5)       //设置最大同时下载数量（不设置默认5）
                .saveProgressBytes(50 * 1204)   //设置每50kb触发一次saveProgress保存进度 （不能在onProgress每次都保存 过于频繁） 不设置默认50kb
                .build();
        mDownloadMgr.resumeTasks();     //恢复本地所有未完成的任务

        /*sharesdk分享*/
        MobSDK.init(this);
        /*阿里百川*/
        AlibcTradeSDK.asyncInit(this, new AlibcTradeInitCallback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int i, String s) {
            }
        });
        String pesudoUniqueID = AppUtils.getPesudoUniqueID();
        PreferUtils.putString(context, Constant.PESUDOUNIQUEID, pesudoUniqueID);
        String netClassic = NetUtil.getNetClassic(context);
        PreferUtils.putString(context, Constant.NETWORKTYPE, netClassic);
        /*初始化剪切板*/
        ClipboardUtil.init(this);
        /*数据库初始化*/
        LitePal.initialize(this);
        /*友盟统计*/
        UMConfigure.init(this, Constant.UMENGAPPKEY, null, 0, null);
        /*启动错误日志*/
        CrashHandler catchHandler = CrashHandler.getInstance();
        catchHandler.init(getApplicationContext());
        /*友盟推送*/
        UMConfigure.init(this, "5c075c04b465f599ba000466", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "fd42e71e3be4345bbf609834bfb2c586");
        /*小米通道*/
        MiPushRegistar.register(this, "2882303761517919253", "5751791967253");
        /*华为通道*/
        HuaWeiRegister.register(this);
        HuaWeiRegister.register(getInstance());
        /*魅族*/
        MeizuRegister.register(context, "117937", "a6ad3e6fdb0a49cab79872d8921a9d85");
        //获取消息推送代理示例
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.i("推送测试", "注册成功：deviceToken：-------->  " + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("推送测试", "注册失败：-------->  " + "s:" + s + ",s1:" + s1);
            }
        });
        UmengMessageHandler messageHandler = new UmengMessageHandler() {
            /**
             * 自定义通知栏样式的回调方法
             */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                switch (msg.builder_id) {
                    case 1:
                        Notification.Builder builder = new Notification.Builder(context);
                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(),
                                R.layout.notification_view);
                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                        myNotificationView.setImageViewResource(R.id.notification_small_icon,
                                getSmallIconId(context, msg));
                        builder.setContent(myNotificationView)
                                .setSmallIcon(getSmallIconId(context, msg))
                                .setTicker(msg.ticker)
                                .setAutoCancel(true);
                        return builder.getNotification();
                    default:
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(messageHandler);
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void launchApp(Context context, UMessage msg) {
                Intent intent;
                Map<String, String> extra = msg.extra;
                String target = extra.get("target");
                if (!TextUtils.isEmpty(target)) {
                    switch (target) {
                        case "money":/*佣金明细*/
                            intent = new Intent(context, ShouRuMingXiActivity.class);
                            intent.putExtra("type", "0");
                            intent.putExtra(Constant.TOMAINTYPE, "");
                            startActivity(intent);
                            break;
                        case "credit":/*团队奖金明细*/
                            intent = new Intent(context, ShouRuMingXiActivity.class);
                            intent.putExtra("type", "1");
                            intent.putExtra(Constant.TOMAINTYPE, "");
                            startActivity(intent);
                            break;
                        case "market":/*我的团队*/
                            intent = new Intent(context, MyTeamActivity.class);
                            intent.putExtra(Constant.TOMAINTYPE, "");
                            startActivity(intent);
                            break;
                        case "order":/*我的订单*/
                            intent = new Intent(context, MyOrderActivity.class);
                            intent.putExtra(Constant.TOMAINTYPE, "");
                            startActivity(intent);
                            break;
                        case "income":/*我的收入*/
                            intent = new Intent(context, MyIncomeingActivity.class);
                            intent.putExtra(Constant.TOMAINTYPE, "");
                            startActivity(intent);
                            break;
                        case "withdraw":/*提现记录*/
                            intent = new Intent(getApplicationContext(), TiXianRecordActivity.class);
                            startActivity(intent);
                            break;
                    }
                }
                super.launchApp(context, msg);
            }

            @Override
            public void openUrl(Context context, UMessage msg) {
                super.openUrl(context, msg);
            }

            @Override
            public void openActivity(Context context, UMessage msg) {
                super.openActivity(context, msg);
            }

            @Override
            public void dealWithCustomAction(final Context context, final UMessage msg) {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // 对于自定义消息，PushSDK默认只统计送达。若开发者需要统计点击和忽略，则需手动调用统计方法。
                        boolean isClickOrDismissed = true;
                        if (isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(msg);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(msg);
                        }
                        Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                    }
                });
            }

        };
//        mPushAgent.setNotificationClickHandler(notificationClickHandler);
//        boolean is_start= NotificationPermissions.isNotificationEnable(context);
////        boolean is_start=isNotificationEnabled();
//        PreferUtils.putBoolean(context,"is_start",is_start);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public MyOkHttp getMyOkHttp() {
        return mMyOkHttp;
    }

    public DownloadMgr getDownloadMgr() {
        return mDownloadMgr;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
