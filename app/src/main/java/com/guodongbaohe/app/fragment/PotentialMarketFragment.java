package com.guodongbaohe.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.YaoQingFriendActivity;
import com.guodongbaohe.app.adapter.QianZaiAdapter;
import com.guodongbaohe.app.bean.QianZaiYingHuBean;
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
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PotentialMarketFragment extends android.support.v4.app.Fragment {
    private View view;
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    @BindView(R.id.re_no_shichang)
    RelativeLayout re_no_shichang;
    @BindView(R.id.lijishare)
    TextView lijishare;
    String member_id, userName;
    private int pageNum = 1;
    List<QianZaiYingHuBean.QianZaiYingData> list = new ArrayList<>();
    QianZaiAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.potentialmarketfragment, container, false);
            ButterKnife.bind(this, view);
            member_id = PreferUtils.getString(getContext(), "member_id");
            userName = PreferUtils.getString(getContext(), "userName");
            getPotentialMarketData();
            initXrecycler();
        }
        return view;
    }

    @OnClick({R.id.lijishare})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lijishare:
                Intent intent = new Intent(getContext(), YaoQingFriendActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void initXrecycler() {
        xrecycler.setHasFixedSize(true);
        xrecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        XRecyclerViewUtil.setView(xrecycler);
        adapter = new QianZaiAdapter(getContext(), list, userName);
        xrecycler.setAdapter(adapter);
        xrecycler.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                getPotentialMarketData();
            }

            @Override
            public void onLoadMore() {
                pageNum++;
                getPotentialMarketData();
            }
        });
    }

    /*潜在市场数据*/
    private void getPotentialMarketData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("page", String.valueOf(pageNum));
        map.put("type", "3");
        map.put("limit", "15");
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        final String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.QIANZAIYONGHU + "?" + param)
                .tag(this)
                .addHeader("x-userid", member_id)
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
                        Log.i("潜在市场", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                QianZaiYingHuBean bean = GsonUtil.GsonToBean(response.toString(), QianZaiYingHuBean.class);
                                List<QianZaiYingHuBean.QianZaiYingData> result = bean.getResult();
                                if (pageNum == 1) {
                                    if (result.size() == 0) {
                                        re_no_shichang.setVisibility(View.VISIBLE);
                                    } else {
                                        re_no_shichang.setVisibility(View.GONE);
                                    }
                                    list.clear();
                                    list.addAll(result);
                                    adapter.notifyDataSetChanged();
                                    xrecycler.refreshComplete();
                                } else {
                                    list.addAll(result);
                                    adapter.notifyDataSetChanged();
                                    xrecycler.loadMoreComplete();
                                }
                            } else {
                                xrecycler.refreshComplete();
                                xrecycler.loadMoreComplete();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast(getContext(), Constant.NONET);
                        xrecycler.refreshComplete();
                        xrecycler.loadMoreComplete();
                    }
                });
    }
}
