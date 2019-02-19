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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.GScAdapter;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.GCollectionBean;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.guodongbaohe.app.common_constant.MyApplication.getContext;

public class GCollectionActivity extends BaseActivity implements View.OnClickListener, GScAdapter.OnItemClickListener {
    private static final int MYLIVE_MODE_CHECK = 0;
    private static final int MYLIVE_MODE_EDIT = 1;
    ImageView iv_back, nodata;
    LinearLayout rl_bottom, show_no;
    TextView tv_right_name;
    TextView tv_title;
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    private GScAdapter adapter;
    private int mEditMode = MYLIVE_MODE_CHECK;
    //全选
    CheckBox ckAll;
    //删除
    TextView tvSettlement;
    private int pageNum = 1;
    List<GCollectionBean.ResultBean> list = new ArrayList<>();

    @Override
    public int getContainerView() {
        return R.layout.activity_gcollection;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        member_id = PreferUtils.getString(getApplicationContext(), "member_id");
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_right_name = (TextView) findViewById(R.id.tv_right_name);
        tv_title.setText("我的收藏");
        tv_right_name.setText("编辑");
        nodata = (ImageView) findViewById(R.id.nodata);
        ckAll = (CheckBox) findViewById(R.id.ck_all);
        tvSettlement = (TextView) findViewById(R.id.login);
        rl_bottom = (LinearLayout) findViewById(R.id.rl_bottom);
        show_no = (LinearLayout) findViewById(R.id.show_no);
        getListData();
        initXrecycler();
        initListener();
        ckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAllMain();
            }
        });
    }

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case Constant.COLLECT_CHANGE:
                pageNum = 1;
                getListData();
                break;
        }
    }

    private boolean isSelectAll = false;
    private int index = 0;

    /**
     * 全选和反选
     */
    private void selectAllMain() {
        if (adapter == null) return;
        if (!isSelectAll) {
            for (int i = 0, j = list.size(); i < j; i++) {
                list.get(i).setIschoosed(true);
                id = list.get(i).getGoods_id();
                list_id.add(id);
            }

            index = list.size();
            tvSettlement.setEnabled(true);
            isSelectAll = true;
        } else {
            for (int i = 0, j = list.size(); i < j; i++) {
                list.get(i).setIschoosed(false);
                list_id.clear();
            }
            index = 0;
            isSelectAll = false;
        }
        adapter.notifyDataSetChanged();
    }

    Dialog dialog;

    /**
     * 删除逻辑
     */
    private void deleteVideo() {
        if (index == 0) {
            return;
        }
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new Dialog(GCollectionActivity.this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(R.layout.pop_user);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView msg = (TextView) dialog.findViewById(R.id.tv_msg);
        TextView sure = (TextView) dialog.findViewById(R.id.btn_sure);
        TextView cancle = (TextView) dialog.findViewById(R.id.btn_cancle);
        if (msg == null || cancle == null || sure == null) return;
        if (index == 1) {
            msg.setText("删除后不可恢复，是否删除该条目？");
        } else {
            msg.setText("删除后不可恢复，是否删除这" + index + "个条目？");
        }
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = list.size(), j = 0; i > j; i--) {
                    GCollectionBean.ResultBean myLive = list.get(i - 1);
                    if (myLive.isIschoosed()) {
                        list.remove(myLive);
                        index--;
                    }
                }
                if (list.size() == 0) {
                    rl_bottom.setVisibility(View.GONE);
                }
                DeleteData();
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void initListener() {
        adapter.setOnItemClickListener(this);
        tvSettlement.setOnClickListener(this);
        tv_right_name.setOnClickListener(this);
    }

    String member_id;

    /*列表数据*/
    private void getListData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("page", String.valueOf(pageNum));
        map.put("limit", "14");
        String param = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(param + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String mapParam = ParamUtil.getMapParam(map);
        Log.i("列表数据参数", Constant.BASE_URL + Constant.SHOUCANGFY + "?" + mapParam);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.SHOUCANGFY + "?" + mapParam)
                .tag(this)
                .addHeader("x-userid", member_id)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getApplicationContext(), PreferUtils.getString(getApplicationContext(), "member_id")))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("列表数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                GCollectionBean bean = GsonUtil.GsonToBean(response.toString(), GCollectionBean.class);
                                if (bean == null) return;
                                List<GCollectionBean.ResultBean> result = bean.getResult();
                                if (pageNum == 1 && result.size() == 0) {
                                    nodata.setVisibility(View.VISIBLE);
                                    tv_right_name.setVisibility(View.GONE);
                                } else {
                                    nodata.setVisibility(View.GONE);
                                    tv_right_name.setVisibility(View.VISIBLE);
                                }
                                if (pageNum == 1) {
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

    private void initXrecycler() {
        xrecycler.setHasFixedSize(true);
        xrecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        XRecyclerViewUtil.setView(xrecycler);
        adapter = new GScAdapter(getApplicationContext(), list);
        xrecycler.setAdapter(adapter);
        xrecycler.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                getListData();
            }

            @Override
            public void onLoadMore() {
                pageNum++;
                getListData();
            }
        });
    }

    StringBuilder str = new StringBuilder();
    String goods_id;

    /*列表数据*/
    private void DeleteData() {
        for (int i = 0; i < list_id.size(); i++) {
            if (i == list_id.size() - 1)//当循环到最后一个的时候 就不添加逗号,
            {
                str.append(list_id.get(i));
            } else {
                str.append(list_id.get(i));
                str.append(",");
            }
        }
        goods_id = str.toString();
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("goods_id", goods_id);
        String param = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(param + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String mapParam = ParamUtil.getMapParam(map);
        Log.i("列表数据参数", Constant.BASE_URL + Constant.CANCEL_COLLECT_SHOP + "?" + mapParam);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.CANCEL_COLLECT_SHOP + "?" + mapParam)
                .tag(this)
                .addHeader("x-userid", member_id)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", ParamUtil.GroupMap(getApplicationContext(), PreferUtils.getString(getApplicationContext(), "member_id")))
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("列表数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                pageNum = 1;
                                getListData();
                                EventBus.getDefault().post(Constant.COLLECT_LIST_CHANGE);
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
                        ToastUtils.showToast(getContext(), Constant.NONET);
                    }
                });
    }

    /*商品详情头部信息*/
    private void getShopBasicData(int pos) {
        GCollectionBean.ResultBean result = list.get(pos);
        String cate_category = result.getCate_category();
        if (!TextUtils.isEmpty(cate_category)) {
            Intent intent = new Intent(getContext(), ShopDetailActivity.class);
            intent.putExtra("goods_id", result.getGoods_id());
            intent.putExtra("cate_route", result.getCate_route());/*类目名称*/
            intent.putExtra("cate_category", result.getCate_category());/*类目id*/
            intent.putExtra("attr_price", result.getAttr_price());
            intent.putExtra("attr_prime", result.getAttr_prime());
            intent.putExtra("attr_ratio", result.getAttr_ratio());
            intent.putExtra("sales_month", result.getSales_month());
            intent.putExtra("goods_name", result.getGoods_name());/*长标题*/
            intent.putExtra("goods_short", result.getGoods_short());/*短标题*/
            intent.putExtra("seller_shop", result.getSeller_shop());/*店铺姓名*/
            intent.putExtra("goods_thumb", result.getGoods_thumb());/*单图*/
            intent.putExtra("goods_gallery", result.getGoods_gallery());/*多图*/
            intent.putExtra("coupon_begin", result.getCoupon_begin());/*开始时间*/
            intent.putExtra("coupon_final", result.getCoupon_final());/*结束时间*/
            intent.putExtra("coupon_surplus", result.getCoupon_surplus());/*是否有券*/
            intent.putExtra("coupon_explain", "");/*推荐理由*/
            intent.putExtra("attr_site", result.getAttr_site());/*天猫或者淘宝*/
            intent.putExtra("coupon_total", result.getCoupon_total());
            intent.putExtra(Constant.SHOP_REFERER, "favorite");/*商品来源*/
            startActivity(intent);
        } else {
            ToastUtils.showToast(getContext(), "商品信息不全，请试试其他商品");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_right_name:
                updataEditMode();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.login:
                deleteVideo();
                break;
        }

    }

    private void updataEditMode() {
        mEditMode = mEditMode == MYLIVE_MODE_CHECK ? MYLIVE_MODE_EDIT : MYLIVE_MODE_CHECK;
        if (mEditMode == MYLIVE_MODE_EDIT) {
            tv_right_name.setText("取消");
            xrecycler.setLoadingMoreEnabled(true);
            xrecycler.setPullRefreshEnabled(false);
            rl_bottom.setVisibility(View.VISIBLE);
            editorStatus = true;
            tvSettlement.setEnabled(true);
        } else {
            ckAll.setChecked(false);
            tv_right_name.setText("编辑");
            xrecycler.setPullRefreshEnabled(true);
            isSelectAll = true;
            selectAllMain();
            rl_bottom.setVisibility(View.GONE);
            editorStatus = false;
        }
        adapter.setEditMode(mEditMode);
    }

    private boolean editorStatus = false;
    String id;
    List<String> list_id = new ArrayList<>();

    @Override
    public void onItemClickListener(int pos, List<GCollectionBean.ResultBean> myLiveList) {
        GCollectionBean.ResultBean myLive = myLiveList.get(pos);
        if (editorStatus) {
            id = myLive.getGoods_id();
            boolean isSelect = myLive.isIschoosed();
            if (!isSelect) {
                index++;
                if (list_id.size() > 0) {
                    for (int i = 0; i < list_id.size(); i++) {
                        if (!list_id.get(i).equals(id)) {
                            list_id.add(id);
                        }
                    }
                } else {
                    list_id.add(id);
                }
                myLive.setIschoosed(true);
            } else {
                myLive.setIschoosed(false);
                index--;
                for (int i = 0; i < list_id.size(); i++) {
                    if (list_id.get(i).equals(id)) {
                        list_id.remove(i);
                    }
                    ckAll.setChecked(false);
                }
                isSelectAll = false;
            }
            adapter.notifyDataSetChanged();
        } else {
            getShopBasicData(pos);
        }
    }
}
