package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.DepartmentAdapter;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyDepatermentSearchActivity extends BigBaseActivity {
    String member_id, userName;
    private int pageNum = 1;
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    /*返回按钮*/
    @BindView(R.id.iv_back)
    ImageView iv_back;
    /*关键字*/
    @BindView(R.id.input_word)
    EditText input_word;
    /*搜索按钮*/
    @BindView(R.id.search)
    TextView search;
    @BindView(R.id.nodata)
    TextView nodata;
    List<DepartmentBean.DepartmentData> list = new ArrayList<>();
    DepartmentAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mydepatermentsearchactivity);
        ButterKnife.bind(this);
        member_id = PreferUtils.getString(getApplicationContext(), "member_id");
        userName = PreferUtils.getString(getApplicationContext(), "userName");
        initView();
        setEditWatch();
    }

    private void setEditWatch() {
        input_word.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    trim = input_word.getText().toString().trim();
                    if (TextUtils.isEmpty(trim)) {
                        ToastUtils.showToast(getApplicationContext(), "请输入用户名或手机号");
                    } else {
                        pageNum = 1;
                        getData(trim);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    String trim;

    @OnClick({R.id.search, R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search:
                trim = input_word.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    ToastUtils.showToast(getApplicationContext(), "请输入用户名或手机号");
                    return;
                }
                getData(trim);
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void initView() {
        xrecycler.setHasFixedSize(true);
        xrecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        XRecyclerViewUtil.setView(xrecycler);
        adapter = new DepartmentAdapter(getApplicationContext(), list, userName);
        xrecycler.setAdapter(adapter);
        xrecycler.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                if (!TextUtils.isEmpty(trim)) {
                    getData(trim);
                }
            }

            @Override
            public void onLoadMore() {
                pageNum++;
                if (!TextUtils.isEmpty(trim)) {
                    getData(trim);
                }
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

    BigDecimal bg3;

    private void showPersonalDataDialog(int i) {
        final Dialog dialog = new Dialog(MyDepatermentSearchActivity.this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(R.layout.showpersonaldatadialog);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        CircleImageView circleimageview = (CircleImageView) dialog.findViewById(R.id.circleimageview);
        TextView name = (TextView) dialog.findViewById(R.id.name);
        TextView invite_code = (TextView) dialog.findViewById(R.id.invite_code);
        TextView wechat = (TextView) dialog.findViewById(R.id.wechat);
        TextView month = (TextView) dialog.findViewById(R.id.month);
        TextView total = (TextView) dialog.findViewById(R.id.total);
        TextView time = (TextView) dialog.findViewById(R.id.time);
        String avatar = list.get(i).getAvatar();
        if (TextUtils.isEmpty(avatar)) {
            circleimageview.setImageResource(R.mipmap.user_moren_logo);
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
        String last_month_income = list.get(i).getLast_month_income();
        String total_income = list.get(i).getTotal_income();
        bg3 = new BigDecimal(Double.valueOf(last_month_income));
        double last_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        month.setText(String.valueOf(last_money));
        bg3 = new BigDecimal(Double.valueOf(total_income));
        double total_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        total.setText(String.valueOf(total_money));
        time.setText("注册时间: " + list.get(i).getDatetime());
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /*搜索关键字*/
    private void getData(String content) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("kw", content);
        map.put("page", String.valueOf(pageNum));
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        final String param = ParamUtil.getMapParam(map);
        Log.i("我的市场搜索地址", Constant.BASE_URL + Constant.MYDEPATERMENT_SEARCH + "?" + param);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.MYDEPATERMENT_SEARCH + "?" + param)
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
                        Log.i("搜索市场", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                DepartmentBean bean = GsonUtil.GsonToBean(response.toString(), DepartmentBean.class);
                                if (bean == null) return;
                                List<DepartmentBean.DepartmentData> result = bean.getResult();
                                if (pageNum == 1) {
                                    if (result.size() == 0) {
                                        nodata.setVisibility(View.VISIBLE);
                                    } else {
                                        nodata.setVisibility(View.GONE);
                                    }
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
                                String result = jsonObject.getString("result");
                                xrecycler.refreshComplete();
                                xrecycler.loadMoreComplete();
                                ToastUtils.showToast(getApplicationContext(), result);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        xrecycler.refreshComplete();
                        xrecycler.loadMoreComplete();
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }
}
