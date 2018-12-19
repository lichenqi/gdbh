package com.guodongbaohe.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.NinePinkageAdapter;
import com.guodongbaohe.app.bean.HomeListBean;
import com.guodongbaohe.app.bean.NineBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.XRecyclerViewUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SecondClassicFragment extends Fragment {
    private View view;
    private int pageNum = 1;
    int position;
    String title;
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    NinePinkageAdapter ninePinkageAdapter;
    private List<HomeListBean.ListData> list = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            position = getArguments().getInt("position");
            title = getArguments().getString("title");
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.secondclassicfragment, container, false);
            ButterKnife.bind(this, view);
            getData();
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
        }
        return view;
    }

    private void getData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("sorts_type", "DESC");
        map.put("kw", title);
        map.put("sorts", "ration");
        String mapParam = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.OTHERGOODSLIST + position + "/" + pageNum + "?" + mapParam)
                .tag(this)
                .addHeader("APPID", Constant.APPID)
                .addHeader("APPKEY", Constant.APPKEY)
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
                        xrecycler.refreshComplete();
                        xrecycler.loadMoreComplete();
                    }
                });
    }
}
