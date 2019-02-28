package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.JhsAdapter;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.FourIVBean;
import com.guodongbaohe.app.bean.HomeListBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.ClipContentUtil;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.util.XRecyclerViewUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KesalanPathActivity extends BaseActivity {
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    @BindView(R.id.to_top)
    ImageView to_top;
    private int pageNum = 1;
    List<HomeListBean.ListData> list = new ArrayList<>();
    JhsAdapter adapter;
    RoundedImageView iv_head;

    // 声明一个订阅方法，用于接收事件茶空间了你我所取代我破产我就请稍等
    @Subscribe
    public void onEvent(String msg) {
        switch (msg) {
            case Constant.LOGIN_OUT:
                /*用户退出*/
                pageNum = 1;
                getData();
                break;
            case Constant.LOGINSUCCESS:
                /*登录成功*/
                pageNum = 1;
                getData();
                break;
            case Constant.USER_LEVEL_UPGRADE:
                /*用户等级升级成功*/
                pageNum = 1;
                getData();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*获取剪切板内容*/
        getClipContent();
    }

    Dialog dialog;

    private void getClipContent() {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (cm.hasPrimaryClip()) {
            ClipData data = cm.getPrimaryClip();
            if (data == null) return;
            ClipData.Item item = data.getItemAt(0);
            final String content = item.coerceToText(getApplicationContext()).toString().trim().replace("\r\n\r\n", "\r\n");
            if (TextUtils.isEmpty(content)) return;
            boolean isFirstClip = PreferUtils.getBoolean(getApplicationContext(), "isFirstClip");
            if (!isFirstClip) {
                showDialog(content);
            } else {
                String clip_content = PreferUtils.getString(getApplicationContext(), "clip_content");
                if (clip_content.equals(content)) return;
                showDialog(content);
            }
            PreferUtils.putBoolean(getApplicationContext(), "isFirstClip", true);
        }
    }

    private void showDialog(final String content) {
        PreferUtils.putString(getApplicationContext(), "clip_content", content);
        List<String> clip_list = ClipContentUtil.getInstance(getApplicationContext()).queryHistorySearchList();
        if (clip_list == null) return;
        for (int i = 0; i < clip_list.size(); i++) {
            if (clip_list.get(i).equals(content)) {
                return;
            }
        }
        guoDuTanKuang(content);
    }

    /*过渡弹框*/
    private void guoDuTanKuang(final String content) {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new Dialog(KesalanPathActivity.this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(R.layout.clip_search_dialog);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView sure = (TextView) dialog.findViewById(R.id.sure);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        TextView title = (TextView) dialog.findViewById(R.id.content);
        title.setText(content);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra("keyword", content);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    @Override
    public int getContainerView() {
        return R.layout.kesalanpathactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        String title = getIntent().getStringExtra("title");
        setMiddleTitle(title);
        getData();
        xrecycler.setHasFixedSize(true);
        xrecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        XRecyclerViewUtil.setView(xrecycler);
        adapter = new JhsAdapter(list, getApplicationContext());
        xrecycler.setAdapter(adapter);
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.base_head_img, null);
        iv_head = (RoundedImageView) view.findViewById(R.id.iv_head);
        xrecycler.addHeaderView(view);
        getFourHeadIVData();
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
                int pos = position - 2;
                Intent intent = new Intent(getApplicationContext(), ShopDetailActivity.class);
                intent.putExtra("goods_id", list.get(pos).getGoods_id());
                intent.putExtra("cate_route", list.get(pos).getCate_route());/*类目名称*/
                intent.putExtra("cate_category", list.get(pos).getCate_category());
                intent.putExtra("attr_price", list.get(pos).getAttr_price());
                intent.putExtra("attr_prime", list.get(pos).getAttr_prime());
                intent.putExtra("attr_ratio", list.get(pos).getAttr_ratio());
                intent.putExtra("sales_month", list.get(pos).getSales_month());
                intent.putExtra("goods_name", list.get(pos).getGoods_name());/*长标题*/
                intent.putExtra("goods_short", list.get(pos).getGoods_short());/*短标题*/
                intent.putExtra("seller_shop", list.get(pos).getSeller_shop());/*店铺姓名*/
                intent.putExtra("goods_thumb", list.get(pos).getGoods_thumb());/*单图*/
                intent.putExtra("goods_gallery", list.get(pos).getGoods_gallery());/*多图*/
                intent.putExtra("coupon_begin", list.get(pos).getCoupon_begin());/*开始时间*/
                intent.putExtra("coupon_final", list.get(pos).getCoupon_final());/*结束时间*/
                intent.putExtra("coupon_surplus", list.get(pos).getCoupon_surplus());/*是否有券*/
                intent.putExtra("coupon_explain", list.get(pos).getGoods_slogan());/*推荐理由*/
                intent.putExtra("attr_site", list.get(pos).getAttr_site());/*天猫或者淘宝*/
                intent.putExtra("coupon_total", list.get(pos).getCoupon_total());
                intent.putExtra("coupon_id", list.get(pos).getCoupon_id());/*优惠券id*/
                intent.putExtra(Constant.SHOP_REFERER, "local");/*商品来源*/
                intent.putExtra(Constant.GAOYONGJIN_SOURCE, list.get(pos).getSource());/*高佣金来源*/
                startActivity(intent);
            }
        });
        xrecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int i = recyclerView.computeVerticalScrollOffset();
                if (i > 1200) {
                    to_top.setVisibility(View.VISIBLE);
                } else {
                    to_top.setVisibility(View.GONE);
                }
            }
        });
        to_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xrecycler.scrollToPosition(0);
            }
        });
    }

    private void getData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("activity", "jhs");
        map.put("page", String.valueOf(pageNum));
        String mapParam = ParamUtil.getMapParam(map);
        Log.i("聚划算地址", Constant.BASE_URL + Constant.SHOP_LIST + "?" + mapParam);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.SHOP_LIST + "?" + mapParam)
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
                        Log.i("数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                HomeListBean bean = GsonUtil.GsonToBean(response.toString(), HomeListBean.class);
                                if (bean == null) return;
                                List<HomeListBean.ListData> result = bean.getResult();
                                if (result.size() == 0) {
                                    xrecycler.setNoMore(true);
                                    xrecycler.refreshComplete();
                                    xrecycler.loadMoreComplete();
                                } else {
                                    boolean isLogin = PreferUtils.getBoolean(getApplicationContext(), "isLogin");
                                    String son_count = PreferUtils.getString(getApplicationContext(), "son_count");
                                    String member_role = PreferUtils.getString(getApplicationContext(), "member_role");
                                    for (HomeListBean.ListData listData : result) {
                                        listData.setLogin(isLogin);
                                        listData.setSon_count(son_count);
                                        listData.setMember_role(member_role);
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
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                        xrecycler.refreshComplete();
                        xrecycler.loadMoreComplete();
                    }
                });
    }

    /*四大模块头部图片数据*/
    List<FourIVBean.FourIVData> four_iv_list;

    private void getFourHeadIVData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("type", "button");
        String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.BASE_URL + Constant.BANNER + "?" + param)
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
                        Log.i("四大模块头部图片数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                FourIVBean bean = GsonUtil.GsonToBean(response.toString(), FourIVBean.class);
                                if (bean == null) return;
                                four_iv_list = bean.getResult();
                                for (int i = 0; i < four_iv_list.size(); i++) {
                                    if (four_iv_list.get(i).getUrl().equals("jhs")) {
                                        Glide.with(getApplicationContext()).load(four_iv_list.get(i).getImage()).into(iv_head);
                                        return;
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {

                    }
                });
    }
}
