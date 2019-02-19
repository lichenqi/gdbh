package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.guodongbaohe.app.bean.YouMengPushBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.util.GsonUtil;
import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;

public class MipushTestActivity extends UmengNotifyClickActivity {


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);  //此方法必须调用，否则无法统计打开数
        String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        Log.i("离线消息友盟", body);
        if (TextUtils.isEmpty(body)) return;
        YouMengPushBean bean = GsonUtil.GsonToBean(body, YouMengPushBean.class);
        if (bean == null) return;
        String target = bean.getExtra().getTarget();
        if (target == null) return;
        switch (target) {
            case "money":/*佣金明细*/
                intent = new Intent(getApplicationContext(), ShouRuMingXiActivity.class);
                intent.putExtra("type", "0");
                intent.putExtra(Constant.TOMAINTYPE, Constant.TOMAINTYPE);
                startActivity(intent);
                finish();
                break;
            case "credit":/*团队奖金明细*/
                intent = new Intent(getApplicationContext(), ShouRuMingXiActivity.class);
                intent.putExtra("type", "1");
                intent.putExtra(Constant.TOMAINTYPE, Constant.TOMAINTYPE);
                startActivity(intent);
                finish();
                break;
            case "market":/*我的团队*/
                intent = new Intent(getApplicationContext(), MyTeamActivity.class);
                intent.putExtra(Constant.TOMAINTYPE, Constant.TOMAINTYPE);
                startActivity(intent);
                finish();
                break;
            case "order":/*我的订单*/
                intent = new Intent(getApplicationContext(), MyOrderActivity.class);
                intent.putExtra(Constant.TOMAINTYPE, Constant.TOMAINTYPE);
                startActivity(intent);
                finish();
                break;
            case "income":/*我的收入*/
                intent = new Intent(getApplicationContext(), MyIncomeingActivity.class);
                intent.putExtra(Constant.TOMAINTYPE, Constant.TOMAINTYPE);
                startActivity(intent);
                finish();
                break;
            case "withdraw":/*提现记录*/
                intent = new Intent(getApplicationContext(), TiXianRecordActivity.class);
                intent.putExtra(Constant.TOMAINTYPE, Constant.TOMAINTYPE);
                startActivity(intent);
                finish();
                break;
        }
    }
}