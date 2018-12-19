package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WChatYaoQingMaActivity extends BaseActivity {
    @BindView(R.id.et_invited_code)
    EditText et_invited_code;
    @BindView(R.id.tv_sure)
    TextView tv_sure;
    String wchatinfo;

    @Override
    public int getContainerView() {
        return R.layout.invitationcodeactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("邀请码");
        Intent intent = getIntent();
        wchatinfo = intent.getStringExtra("wchatinfo");
        et_invited_code();
    }

    private void et_invited_code() {
        et_invited_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 3) {
                    tv_sure.setBackgroundResource(R.drawable.red_bg);
                    tv_sure.setTextColor(0xffffffff);
                } else {
                    tv_sure.setBackgroundResource(R.drawable.gray_invite_code);
                    tv_sure.setTextColor(0xff585858);
                }
            }
        });
    }

    @OnClick({R.id.tv_sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sure:
                String code = et_invited_code.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    ToastUtils.showToast(getApplicationContext(), "邀请码不能为空");
                    return;
                }
                verificationCodeData(code);
                break;
        }
    }

    Dialog loadingDialog;

    private void verificationCodeData(final String code) {
        long timelineStr = System.currentTimeMillis() / 1000;
        loadingDialog = DialogUtil.createLoadingDialog(WChatYaoQingMaActivity.this, "验证中...");
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("account", code);
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        String mapParam = ParamUtil.getMapParam(map);
        String token = EncryptUtil.encrypt(mapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.CHECKINVITEDCODE + "?" + param)
                .tag(this)
                .addHeader("APPID", Constant.APPID)
                .addHeader("APPKEY", Constant.APPKEY)
                .enqueue(new JsonResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("邀请码", response.toString());
                        DialogUtil.closeDialog(loadingDialog);
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            String aReturn = jsonObject.getString("status");
                            String result = jsonObject.getString("result");
                            if (aReturn.equals("0")) {/*验证通过*/
                                Intent intent = new Intent(getApplicationContext(), PhoneLoginActivity.class);
                                intent.putExtra("yaoqingma", code);
                                intent.putExtra("wchatinfo", wchatinfo);
                                startActivityForResult(intent, 101);
                            } else {
                                ToastUtils.showToast(getApplicationContext(), result);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == 101) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
