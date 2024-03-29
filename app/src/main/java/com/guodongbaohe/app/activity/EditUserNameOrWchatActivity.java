package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.view.FivteenContainsEmjoyEditText;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditUserNameOrWchatActivity extends BaseActivity {
    String member_id, content;
    @BindView(R.id.ed_name)
    FivteenContainsEmjoyEditText ed_name;
    @BindView(R.id.sure)
    TextView sure;

    @Override
    public int getContainerView() {
        return R.layout.editusernameorwchatactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("修改用户名");
        content = getIntent().getStringExtra("content");
        member_id = PreferUtils.getString(getApplicationContext(), "member_id");
        if (TextUtils.isEmpty(content)) {
            ed_name.setHint("请输入用户名");
        } else {
            ed_name.setText(content);
            ed_name.setSelection(content.length());
        }
    }

    String name;

    @OnClick({R.id.sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sure:
                name = ed_name.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.showToast(getApplicationContext(), "请输入用户名");
                    return;
                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("member_name", name);
                    saveData(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    Dialog loadingDialog;

    private void saveData(final String struct) {
        loadingDialog = DialogUtil.createLoadingDialog(EditUserNameOrWchatActivity.this, "正在修改");
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("struct", struct);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.EDITPERSONALDATA + "?" + param)
                .tag(this)
                .addHeader("x-userid", member_id)
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
                        DialogUtil.closeDialog(loadingDialog, EditUserNameOrWchatActivity.this);
                        Log.i("编辑结果", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                PreferUtils.putString(getApplicationContext(), "userName", name);/*存储用户名*/
                                EventBus.getDefault().post("headimgChange");
                                Intent intent = getIntent();
                                intent.putExtra("name", name);
                                setResult(1, intent);
                                finish();
                            } else {
                                String result = jsonObject.getString("result");
                                if (TextUtils.isEmpty(result)) {
                                    ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                                } else {
                                    ToastUtils.showToast(getApplicationContext(), result);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        DialogUtil.closeDialog(loadingDialog, EditUserNameOrWchatActivity.this);
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

    //完美解决输入框中不能输入的非法字符

    InputFilter inputFilter = new InputFilter() {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\u4E00-\\u9FA5_,.?!:;…~_\\-\"\"/@*+'()<>{}/[/]()<>{}\\[\\]=%&$|\\/♀♂#¥£¢€\"^` ，。？！：；……～“”、“（）”、（——）‘’＠‘·’＆＊＃《》￥《〈〉》〈＄〉［］￡［］｛｝｛｝￠【】【】％〖〗〖〗／〔〕〔〕＼『』『』＾「」「」｜﹁﹂｀．]");

        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            Matcher matcher = pattern.matcher(charSequence);
            if (!matcher.find()) {
                return null;
            } else {
                ToastUtils.showToast(getApplicationContext(), "暂不支持表情或者特殊字符修改");
                return "";
            }

        }
    };

    @Override
    protected void onDestroy() {
        DialogUtil.closeDialog(loadingDialog, EditUserNameOrWchatActivity.this);
        super.onDestroy();
    }
}
