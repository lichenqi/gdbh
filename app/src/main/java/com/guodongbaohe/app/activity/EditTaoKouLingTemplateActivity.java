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

public class EditTaoKouLingTemplateActivity extends BaseActivity {
    @BindView(R.id.ed_input_content)
    EditText ed_input_content;
    @BindView(R.id.tv_huifu)
    TextView tv_huifu;
    @BindView(R.id.tv_save)
    TextView tv_save;
    /*判断是否点击保存按钮*/
    private boolean isSave = false;
    ImageView iv_back;
    Dialog loadingDialog;
    private String tkl_sign = "{淘口令}";

    @Override
    public int getContainerView() {
        return R.layout.edittaokoulingtemplateactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("编辑模板");
        iv_back = (ImageView) findViewById(R.id.iv_back);
        initBackListener();/*返回键点击*/
        initlocalDataView();/*初始化本地数据*/
    }

    @OnClick({R.id.tv_huifu, R.id.tv_save})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_huifu:
                getNewTemplateData(1);
                break;
            case R.id.tv_save:
                String content = ed_input_content.getText().toString();
                if (!content.contains(tkl_sign)) {
                    ToastUtils.showToast(getApplicationContext(), "缺少必填内容{淘口令}");
                    return;
                }
                loadingDialog = DialogUtil.createLoadingDialog(EditTaoKouLingTemplateActivity.this, "保存中...");
                PreferUtils.putString(getApplicationContext(), "taokouling_content", content);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DialogUtil.closeDialog(loadingDialog, EditTaoKouLingTemplateActivity.this);
                        ToastUtils.showToast(getApplicationContext(), "保存成功");
                    }
                }, 1000);
                isSave = true;
                break;
        }
    }

    /*初始化本地数据*/
    private void initlocalDataView() {
        String taokouling_content = PreferUtils.getString(getApplicationContext(), "taokouling_content");
        if (TextUtils.isEmpty(taokouling_content)) {
            /*代表一次都没有保存过*/
            /*执行网络请求*/
            getNewTemplateData(0);/*获取模板数据*/
        } else {
            ed_input_content.setText(taokouling_content);
            ed_input_content.setSelection(taokouling_content.length());
        }
    }

    /*模板数据*/
    private void getNewTemplateData(final int mode) {
        if (mode == 1) {
            loadingDialog = DialogUtil.createLoadingDialog(EditTaoKouLingTemplateActivity.this, "恢复中...");
        }
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.NEW_TAMPLATE_DATA)
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
                        DialogUtil.closeDialog(loadingDialog, EditTaoKouLingTemplateActivity.this);
                        Log.i("新模板数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                TemplateBean bean = GsonUtil.GsonToBean(response.toString(), TemplateBean.class);
                                if (bean == null) return;
                                String comment = bean.getResult().getComment();/*淘口令*/
                                ed_input_content.setText(comment);
                                ed_input_content.setSelection(comment.length());
                                PreferUtils.putString(getApplicationContext(), "taokouling_content", comment);
                                if (mode == 1) {
                                    ToastUtils.showToast(getApplicationContext(), "已恢复");
                                }
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
                        DialogUtil.closeDialog(loadingDialog, EditTaoKouLingTemplateActivity.this);
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogUtil.closeDialog(loadingDialog, EditTaoKouLingTemplateActivity.this);
    }

    private void initBackListener() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSave) {
                    setResult(300);
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
                setResult(300);
                finish();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
