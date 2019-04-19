package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.MainActivity;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.fragment.FirstMarkFragment;
import com.guodongbaohe.app.fragment.PotentialMarketFragment;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DialogUtil;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyTeamActivity extends BigBaseActivity {
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    private List<Fragment> fragments;
    private String[] titles = {"潜在用户", "一线团队", "二线团队"};
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.re_search)
    RelativeLayout re_search;
    @BindView(R.id.invite_people)
    TextView invite_people;
    @BindView(R.id.invite_code)
    TextView invite_code;
    Dialog dialog;
    String wchat_content;
    String stringExtra;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myteamactivity);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        stringExtra = intent.getStringExtra(Constant.TOMAINTYPE);
        /*我的团队总数量*/
        getData();
        String ainvite_code = PreferUtils.getString(getApplicationContext(), "invite_code");
        String wchatname = PreferUtils.getString(getApplicationContext(), "wchatname");
        invite_code.setText("我的邀请码: " + ainvite_code);
        fragments = new ArrayList<>();
        fragments.add(new PotentialMarketFragment());
        for (int i = 0; i < 2; i++) {
            FirstMarkFragment markFragment = new FirstMarkFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("which_poition", i);
            markFragment.setArguments(bundle);
            fragments.add(markFragment);
        }
        MarkAdapter adapter = new MarkAdapter(fragments, getSupportFragmentManager());
        viewpager.setAdapter(adapter);
        tablayout.setupWithViewPager(viewpager);
        viewpager.setCurrentItem(1);

        /*是否弹出微信号填写*/
        if (TextUtils.isEmpty(wchatname)) {
            showWChatDialog();
        }
    }

    private void showWChatDialog() {
        dialog = new Dialog(MyTeamActivity.this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(R.layout.showwchatdialog);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        final EditText input_wchat = (EditText) dialog.findViewById(R.id.input_wchat);
        TextView save = (TextView) dialog.findViewById(R.id.save);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wchat_content = input_wchat.getText().toString().trim();
                if (TextUtils.isEmpty(wchat_content)) {
                    ToastUtils.showToast(getApplicationContext(), "请先输入正确的微信号");
                    return;
                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("wechat", wchat_content);
                    saveData(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }

    private void getData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        map.put("page", String.valueOf(1));
        map.put("type", "");
        map.put("filter", "");
        map.put("sort", "DESC");
        map.put("field", "member_id");
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        final String param = ParamUtil.getMapParam(map);
        Log.i("我的市场地址", Constant.BASE_URL + Constant.MYDEPATERMENT + "?" + param);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.MYDEPATERMENT + "?" + param)
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
                        Log.i("我的团队总数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                String team_count = jsonObject.getString("team_count");
                                invite_people.setText(team_count);
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

    @OnClick({R.id.back, R.id.re_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                if (!TextUtils.isEmpty(stringExtra)) {
                    if (stringExtra.equals(Constant.TOMAINTYPE)) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        finish();
                    }
                } else {
                    finish();
                }
                break;
            case R.id.re_search:
                Intent intent = new Intent(getApplicationContext(), MyDepatermentSearchActivity.class);
                startActivity(intent);
                break;
        }
    }

    private class MarkAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public MarkAdapter(List<Fragment> fragments, android.support.v4.app.FragmentManager fm) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return titles == null ? 0 : titles.length;
        }
    }

    Dialog loadingDialog;

    private void saveData(final String struct) {
        loadingDialog = DialogUtil.createLoadingDialog(MyTeamActivity.this, "正在保存");
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getApplicationContext(), "member_id"));
        map.put("struct", struct);
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.EDITPERSONALDATA + "?" + param)
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
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        DialogUtil.closeDialog(loadingDialog, MyTeamActivity.this);
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                PreferUtils.putString(getApplicationContext(), "wchatname", wchat_content);/*存储微信号*/
                                ToastUtils.showToast(getApplicationContext(), "保存成功");
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
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        DialogUtil.closeDialog(loadingDialog, MyTeamActivity.this);
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!TextUtils.isEmpty(stringExtra)) {
                if (stringExtra.equals(Constant.TOMAINTYPE)) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    finish();
                }
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        DialogUtil.closeDialog(loadingDialog, MyTeamActivity.this);
        super.onDestroy();
    }
}
