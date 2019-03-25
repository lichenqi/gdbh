package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.NinePinkageAdapter;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.HomeListBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
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

public class NewSecondClassicActivity extends BaseActivity {
    ImageView iv_right;
    String cate_id, parent_id;
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    @BindView(R.id.tv_newly)
    TextView tv_newly;
    @BindView(R.id.tv_sale)
    TextView tv_sale;
    @BindView(R.id.tv_price)
    TextView tv_price;
    @BindView(R.id.tv_renqi)
    TextView tv_renqi;
    @BindView(R.id.to_top)
    ImageView to_top;
    Intent intent;
    private int pageNum = 1;
    private List<HomeListBean.ListData> list = new ArrayList<>();
    NinePinkageAdapter ninePinkageAdapter;
    private String type = "";

    @Override
    protected void onResume() {
        super.onResume();
        userLevelChange();
    }

    @Override
    public int getContainerView() {
        return R.layout.newsecondclassicactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        intent = getIntent();
        String name = intent.getStringExtra("name");
        cate_id = intent.getStringExtra("cate_id");
        parent_id = intent.getStringExtra("parent_id");
        iv_right = (ImageView) findViewById(R.id.iv_right);
        setRightIVVisible();
        iv_right.setImageResource(R.drawable.huise_search);
        setMiddleTitle(name);
        iv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        getListData();
        xrecycler.setHasFixedSize(true);
        xrecycler.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        XRecyclerViewUtil.setView(xrecycler);
        ninePinkageAdapter = new NinePinkageAdapter(getApplicationContext(), list);
        xrecycler.setAdapter(ninePinkageAdapter);
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
        ninePinkageAdapter.setonclicklistener(new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                int pos = position - 1;
                intent = new Intent(getApplicationContext(), ShopDetailActivity.class);
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
                intent.putExtra("coupon_id", list.get(pos).getCoupon_id());
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
    }

    @OnClick({R.id.tv_newly, R.id.tv_sale, R.id.tv_price, R.id.tv_renqi, R.id.to_top})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_newly:
                setNewColor();
                break;
            case R.id.tv_sale:
                setXiaoliangColor();
                break;
            case R.id.tv_price:
                setPriceColor();
                break;
            case R.id.tv_renqi:
                setRenqiColor();
                break;
            case R.id.to_top:
                xrecycler.scrollToPosition(0);
                break;
        }
    }

    private void setNewColor() {
        type = "";
        tv_newly.setTextColor(0xfff6c15b);
        tv_sale.setTextColor(0xff000000);
        tv_price.setTextColor(0xff000000);
        tv_renqi.setTextColor(0xff000000);
        pageNum = 1;
        xrecycler.refresh();
    }

    private void setXiaoliangColor() {
        type = "sales";
        tv_newly.setTextColor(0xff000000);
        tv_sale.setTextColor(0xfff6c15b);
        tv_price.setTextColor(0xff000000);
        tv_renqi.setTextColor(0xff000000);
        pageNum = 1;
        xrecycler.refresh();
    }

    private void setPriceColor() {
        type = "price";
        tv_newly.setTextColor(0xff000000);
        tv_sale.setTextColor(0xff000000);
        tv_price.setTextColor(0xfff6c15b);
        tv_renqi.setTextColor(0xff000000);
        pageNum = 1;
        xrecycler.refresh();
    }

    private void setRenqiColor() {
        type = "commission";
        tv_newly.setTextColor(0xff000000);
        tv_sale.setTextColor(0xff000000);
        tv_price.setTextColor(0xff000000);
        tv_renqi.setTextColor(0xfff6c15b);
        pageNum = 1;
        xrecycler.refresh();
    }

    private void getListData() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("sort", type);
        map.put("supid", parent_id);
        map.put("subid", cate_id);
        map.put("page", String.valueOf(pageNum));
        map.put("limit", "12");
        String mapParam = ParamUtil.getMapParam(map);
        Log.i("李晨奇其他商品列表", Constant.BASE_URL + Constant.SHOP_LIST + "?" + mapParam);
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
                                        ninePinkageAdapter.notifyDataSetChanged();
                                        xrecycler.refreshComplete();
                                    } else {
                                        list.addAll(result);
                                        ninePinkageAdapter.notifyDataSetChanged();
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

    /*佣金和人气切换（初始布局）*/
    private void userLevelChange() {
        if (PreferUtils.getBoolean(getApplicationContext(), "isLogin")) {
            String member_role = PreferUtils.getString(getApplicationContext(), "member_role");
            if (Constant.COMMON_USER_LEVEL.contains(member_role)) {
                /*普通用户*/
                tv_renqi.setText("人气");
            } else {
                /*vip及以上*/
                tv_renqi.setText("佣金");
            }
        } else {
            tv_renqi.setText("人气");
        }
    }

}
