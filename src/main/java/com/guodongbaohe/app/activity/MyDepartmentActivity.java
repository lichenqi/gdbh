package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.DepartmentAdapter;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.DepartmentBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.EncryptUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.util.XRecyclerViewUtil;
import com.guodongbaohe.app.view.CircleImageView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyDepartmentActivity extends BaseActivity {
    private int pageNum = 1;
    String member_id, phoneNum, userImg, userName, invite_code;
    @BindView(R.id.num)
    TextView num;
    @BindView(R.id.invite_people)
    TextView invite_people;
    @BindView(R.id.circleimageview)
    CircleImageView circleimageview;
    @BindView(R.id.ed_name)
    TextView ed_name;
    @BindView(R.id.lookfor)
    TextView lookfor;
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    DepartmentAdapter adapter;
    List<DepartmentBean.DepartmentData> list = new ArrayList<>();
    @BindView(R.id.nodata)
    ImageView nodata;

    @Override
    public int getContainerView() {
        return R.layout.mydepartmentactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setMiddleTitle("我的市场");
        member_id = PreferUtils.getString(getApplicationContext(), "member_id");
        phoneNum = PreferUtils.getString(getApplicationContext(), "phoneNum");
        userImg = PreferUtils.getString(getApplicationContext(), "userImg");
        invite_code = PreferUtils.getString(getApplicationContext(), "invite_code");
        userName = PreferUtils.getString(getApplicationContext(), "userName");
        getData();
        initView();
    }

    @OnClick({R.id.lookfor})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lookfor:
                String trim = ed_name.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    ToastUtils.showToast(getApplicationContext(), "请先输入用户名或电话号码");
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), MyDepatermentSearchActivity.class);
                intent.putExtra("trim", trim);
                startActivity(intent);
                break;
        }
    }

    private void initView() {
        if (TextUtils.isEmpty(userImg)) {
            circleimageview.setImageResource(R.mipmap.login_logo);
        } else {
            Glide.with(getApplicationContext()).load(userImg).into(circleimageview);
        }
        invite_people.setText("我的邀请码: " + invite_code);
        xrecycler.setHasFixedSize(true);
        xrecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        XRecyclerViewUtil.setView(xrecycler);
        xrecycler.setPullRefreshEnabled(false);
        xrecycler.setLoadingMoreEnabled(true);
        adapter = new DepartmentAdapter(getApplicationContext(), list, userName);
        xrecycler.setAdapter(adapter);
        xrecycler.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                getData();
            }

            @Override
            public void onLoadMore() {
                pageNum++;
                getData();
            }
        });
        adapter.setonclicklistener(new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                int i = position - 1;
                showPersonalDataDialog(i);
            }
        });
    }

    private void showPersonalDataDialog(int i) {
        final Dialog dialog = new Dialog(MyDepartmentActivity.this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(R.layout.showpersonaldatadialog);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView cancel = dialog.findViewById(R.id.cancel);
        CircleImageView circleimageview = dialog.findViewById(R.id.circleimageview);
        TextView name = dialog.findViewById(R.id.name);
        TextView invite_code = dialog.findViewById(R.id.invite_code);
        TextView wechat = dialog.findViewById(R.id.wechat);
        TextView month = dialog.findViewById(R.id.month);
        TextView total = dialog.findViewById(R.id.total);
        TextView time = dialog.findViewById(R.id.time);
        String avatar = list.get(i).getAvatar();
        if (TextUtils.isEmpty(avatar)) {
            circleimageview.setImageResource(R.mipmap.login_logo);
        } else {
            Glide.with(getApplicationContext()).load(avatar).into(circleimageview);
        }
        name.setText(list.get(i).getMember_name());
        invite_code.setText(list.get(i).getInvite_code());
        String wechat_name = list.get(i).getWechat();
        if (TextUtils.isEmpty(wechat_name)) {
            wechat.setText("未填写");
        } else {
            wechat.setText(wechat_name);
        }
        month.setText(list.get(i).getLast_month_income());
//        total.setText(list.get(i).getIncome());
        time.setText("注册时间: " + list.get(i).getDatetime());
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    String soncount;

    private void getData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        HashMap<String, String> map = new HashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
//        map.put("kw", phoneNum);
        map.put("page", String.valueOf(pageNum));
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        final String param = ParamUtil.getMapParam(map);
        Log.i("我的市场地址", Constant.BASE_URL + Constant.MYDEPATERMENT + "?" + param);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.MYDEPATERMENT + "?" + param)
                .tag(this)
                .addHeader("x-userid", member_id)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getApplicationContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getApplicationContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getApplicationContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("市场", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getString("status").equals("0")) {
                                DepartmentBean bean = GsonUtil.GsonToBean(response.toString(), DepartmentBean.class);
                                if (bean == null) return;
                                List<DepartmentBean.DepartmentData> result = bean.getResult();
//                                soncount = bean.getSoncount();
                                num.setText(soncount);
                                if (soncount.equals("0")) {
                                    nodata.setVisibility(View.VISIBLE);
                                } else {
                                    nodata.setVisibility(View.GONE);
                                }
                                if (pageNum == 1) {
                                    list.clear();
                                    list.addAll(result);
                                    xrecycler.refreshComplete();
                                    adapter.notifyDataSetChanged();
                                } else {
                                    list.addAll(result);
                                    xrecycler.loadMoreComplete();
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                ToastUtils.showToast(getApplicationContext(), Constant.NONET);
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

}
