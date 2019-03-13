package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.TemplateBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditCommentTemplateActivity extends BaseActivity {
    @BindView(R.id.et_line_one)
    EditText et_line_one;
    @BindView(R.id.et_title_two)
    EditText et_title_two;
    @BindView(R.id.et_sale_price_three)
    EditText et_sale_price_three;
    @BindView(R.id.et_coupon_price_four)
    EditText et_coupon_price_four;
    @BindView(R.id.et_line_five)
    EditText et_line_five;
    @BindView(R.id.et_order_six)
    EditText et_order_six;
    @BindView(R.id.et_line_seven)
    EditText et_line_seven;
    @BindView(R.id.et_taobao_eight)
    EditText et_taobao_eight;
    /*恢复按钮*/
    @BindView(R.id.tv_huifu)
    TextView tv_huifu;
    /*保存按钮*/
    @BindView(R.id.tv_save)
    TextView tv_save;
    /*判断是否点击保存按钮*/
    private boolean isSave = false;
    ImageView iv_back;
    String content_line_one, content_title_two, content_sale_price_three, content_coupon_four, content_line_five,
            content_order_six, content_line_seven, content_taobao_eight;
    Dialog loadingDialog;
    String title_sign = "{标题}";
    String shop_old_price_sign = "{商品原价}";
    String shop_coupon_price_sign = "{券后价}";

    @Override
    public int getContainerView() {
        return R.layout.editcommenttemplateactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        setMiddleTitle("编辑模板");
        initBackListener();/*返回键点击*/
        initlocalDataView();/*初始化本地数据*/
    }

    /*初始化本地数据*/
    private void initlocalDataView() {
        content_line_one = PreferUtils.getString(getApplicationContext(), "content_line_one");
        content_title_two = PreferUtils.getString(getApplicationContext(), "content_title_two");
        content_sale_price_three = PreferUtils.getString(getApplicationContext(), "content_sale_price_three");
        content_coupon_four = PreferUtils.getString(getApplicationContext(), "content_coupon_four");
        content_line_five = PreferUtils.getString(getApplicationContext(), "content_line_five");
        content_order_six = PreferUtils.getString(getApplicationContext(), "content_order_six");
        content_line_seven = PreferUtils.getString(getApplicationContext(), "content_line_seven");
        content_taobao_eight = PreferUtils.getString(getApplicationContext(), "content_taobao_eight");
        if (TextUtils.isEmpty(content_line_one)) {
            /*代表一次都没有保存过*/
            /*执行网络请求*/
            getTemplateData();/*获取模板数据*/
        } else {
            et_line_one.setText(content_line_one);
            et_title_two.setText(content_title_two);
            et_sale_price_three.setText(content_sale_price_three);
            et_coupon_price_four.setText(content_coupon_four);
            et_line_five.setText(content_line_five);
            et_order_six.setText(content_order_six);
            et_line_seven.setText(content_line_seven);
            et_taobao_eight.setText(content_taobao_eight);
        }
    }

    @OnClick({R.id.tv_huifu, R.id.tv_save})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_huifu:
                break;
            case R.id.tv_save:
                String content_line_one = et_line_one.getText().toString().trim();
                String content_title_two = et_title_two.getText().toString().trim();
                String content_sale_price_three = et_sale_price_three.getText().toString().trim();
                String content_coupon_four = et_coupon_price_four.getText().toString().trim();
                String content_line_five = et_line_five.getText().toString().trim();
                String content_order_six = et_order_six.getText().toString().trim();
                String content_line_seven = et_line_seven.getText().toString().trim();
                String content_taobao_eight = et_taobao_eight.getText().toString().trim();
                if (!content_title_two.contains(title_sign)) {
                    ToastUtils.showToast(getApplicationContext(), "缺少必填内容{标题}");
                    return;
                }
                if (!content_sale_price_three.contains(shop_old_price_sign)) {
                    ToastUtils.showToast(getApplicationContext(), "缺少必填内容{商品原价}");
                    return;
                }
                if (!content_coupon_four.contains(shop_coupon_price_sign)) {
                    ToastUtils.showToast(getApplicationContext(), "缺少必填内容{券后价}");
                    return;
                }
                loadingDialog = DialogUtil.createLoadingDialog(EditCommentTemplateActivity.this, "保存中...");
                PreferUtils.putString(getApplicationContext(), "content_line_one", content_line_one);
                PreferUtils.putString(getApplicationContext(), "content_title_two", content_title_two);
                PreferUtils.putString(getApplicationContext(), "content_sale_price_three", content_sale_price_three);
                PreferUtils.putString(getApplicationContext(), "content_coupon_four", content_coupon_four);
                PreferUtils.putString(getApplicationContext(), "content_line_five", content_line_five);
                PreferUtils.putString(getApplicationContext(), "content_order_six", content_order_six);
                PreferUtils.putString(getApplicationContext(), "content_line_seven", content_line_seven);
                PreferUtils.putString(getApplicationContext(), "content_taobao_eight", content_taobao_eight);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogUtil.closeDialog(loadingDialog);
                        ToastUtils.showToast(getApplicationContext(), "保存成功");
                    }
                }, 1100);
                isSave = true;
                break;
        }
    }

    /*获取模板数据*/
    private void getTemplateData() {
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.SHARE_MOBAN)
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
                        Log.i("打印模板看看", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                TemplateBean bean = GsonUtil.GsonToBean(response.toString(), TemplateBean.class);
                                if (bean == null) return;
                                String comment = bean.getResult().getComment();
                                et_line_one.setText("----------");
                                et_title_two.setText("【{标题}】");
                                et_sale_price_three.setText("【在售】{商品原价}");
                                et_coupon_price_four.setText("【券后限时秒杀】{券后价}");
                                et_line_five.setText("----------");
                                et_order_six.setText("{下单链接}");
                                et_line_seven.setText("----------");
                                et_taobao_eight.setText(comment);
                            } else {
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
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogUtil.closeDialog(loadingDialog);
    }

    private void initBackListener() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSave) {
                    setResult(100);
                    finish();
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (isSave) {
                setResult(100);
                finish();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
