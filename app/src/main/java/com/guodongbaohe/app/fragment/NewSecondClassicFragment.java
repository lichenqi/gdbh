package com.guodongbaohe.app.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.ShopDetailActivity;
import com.guodongbaohe.app.adapter.NinePinkageAdapter;
import com.guodongbaohe.app.bean.HomeListBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.XRecyclerViewUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewSecondClassicFragment extends BaseLazyLoadFragment {

    private View view;
    private int pageNum = 1;
    int position;
    String title;
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    NinePinkageAdapter ninePinkageAdapter;
    private List<HomeListBean.ListData> list = new ArrayList<>();

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case Constant.LOGIN_OUT:
                /*用户退出*/
                xrecycler.refresh();
                break;
            case Constant.LOGINSUCCESS:
                /*登录成功*/
                xrecycler.refresh();
                break;
            case Constant.USER_LEVEL_UPGRADE:
                /*用户等级升级成功*/
                xrecycler.refresh();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        if (getArguments() != null) {
            position = getArguments().getInt("position");
            title = getArguments().getString("title");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onLazyLoad() {
        Log.i("看俺onLazyLoad", "onLazyLoad");
        getData();
    }

    @Override
    public View initView(LayoutInflater inflater, @Nullable ViewGroup container) {
        if (view == null) {
            view = inflater.inflate(R.layout.secondclassicfragment, container, false);
            ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    public void initEvent() {
        xrecycler.setHasFixedSize(true);
        xrecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        XRecyclerViewUtil.setView(xrecycler);
        ninePinkageAdapter = new NinePinkageAdapter(getContext(), list);
        xrecycler.setAdapter(ninePinkageAdapter);
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
        ninePinkageAdapter.setonclicklistener(new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                int pos = position - 1;
                Intent intent = new Intent(getActivity(), ShopDetailActivity.class);
                intent.putExtra("goodsId", list.get(pos).getGoods_id());
                intent.putExtra("route", list.get(pos).getCate_route());
                startActivity(intent);
            }
        });
    }

    Dialog loadingDialog;

    private void getData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("sorts_type", "DESC");
        map.put("kw", title);
        map.put("sorts", "ration");
        map.put("page", String.valueOf(pageNum));
        map.put("cate_id", String.valueOf(position));
        String mapParam = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.OTHERGOODSLIST + "?" + mapParam)
                .tag(this)
                .addHeader("APPID", Constant.APPID)
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        HomeListBean bean = GsonUtil.GsonToBean(response.toString(), HomeListBean.class);
                        if (bean == null) return;
                        List<HomeListBean.ListData> result = bean.getResult();
                        if (result.size() == 0) {
                            xrecycler.setNoMore(true);
                            xrecycler.refreshComplete();
                            xrecycler.loadMoreComplete();
                        } else {
                            boolean isLogin = PreferUtils.getBoolean(getContext(), "isLogin");
                            String son_count = PreferUtils.getString(getContext(), "son_count");
                            String member_role = PreferUtils.getString(getContext(), "member_role");
                            for (HomeListBean.ListData nineData : result) {
                                nineData.setLogin(isLogin);
                                nineData.setMember_role(member_role);
                                nineData.setSon_count(son_count);
                            }
                            if (pageNum == 1) {
                                list.clear();
                                list.addAll(result);
                                ninePinkageAdapter.notifyDataSetChanged();
                                xrecycler.refreshComplete();
                            } else {
                                list.addAll(result);
                                ninePinkageAdapter.notifyDataSetChanged();
                                xrecycler.loadMoreComplete();
                            }
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
