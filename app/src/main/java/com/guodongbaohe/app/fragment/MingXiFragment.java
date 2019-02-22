package com.guodongbaohe.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.MingXiBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DateUtils;
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
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MingXiFragment extends Fragment {
    private View view;
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    @BindView(R.id.nodata)
    ImageView nodata;
    int type;
    private int pageNum = 1;
    List<MingXiBean.MingXiData> list = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            type = getArguments().getInt("position");
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.mingxifragment, container, false);
            ButterKnife.bind(this, view);
            initView();
            getData();
        }
        return view;
    }

    private void getData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", PreferUtils.getString(getContext(), "member_id"));
        map.put("page", String.valueOf(pageNum));
        map.put("limit", "10");
        if (type == 0) {
            map.put("type", "money");
        } else if (type == 1) {
            map.put("type", "credit");
        }
        final String param = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(param + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        String mapParam = ParamUtil.getMapParam(map);
        Log.i("收入明细", Constant.BASE_URL + Constant.SHOURUMINGXI + "?" + mapParam);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.SHOURUMINGXI + "?" + mapParam)
                .tag(this)
                .addHeader("x-userid", PreferUtils.getString(getContext(), "member_id"))
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
                        Log.i("明细数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                MingXiBean bean = GsonUtil.GsonToBean(response.toString(), MingXiBean.class);
                                if (bean == null) return;
                                List<MingXiBean.MingXiData> result = bean.getResult();
                                if (pageNum == 1) {
                                    if (result.size() == 0) {
                                        nodata.setVisibility(View.VISIBLE);
                                    } else {
                                        nodata.setVisibility(View.GONE);
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
                        nodata.setVisibility(View.VISIBLE);
                        xrecycler.refreshComplete();
                        xrecycler.loadMoreComplete();
                        ToastUtils.showToast(getContext(), Constant.NONET);
                    }
                });
    }

    MingXiAdapter adapter;

    private void initView() {
        xrecycler.setHasFixedSize(true);
        xrecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        XRecyclerViewUtil.setView(xrecycler);
        adapter = new MingXiAdapter(list);
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
    }

    public class MingXiAdapter extends RecyclerView.Adapter<MingXiHolder> {
        private List<MingXiBean.MingXiData> list;

        public MingXiAdapter(List<MingXiBean.MingXiData> list) {
            this.list = list;
        }

        @Override
        public MingXiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.mingxiadapter, parent, false);
            return new MingXiHolder(view);
        }

        @Override
        public void onBindViewHolder(MingXiHolder holder, int position) {
            String avatar = list.get(position).getAvatar();
            String buyer_id = list.get(position).getBuyer_id();
            String member_id = list.get(position).getMember_id();
            String member_name = list.get(position).getMember_name();
            if (TextUtils.isEmpty(avatar)) {
                holder.iv.setImageResource(R.mipmap.user_moren_logo);
            } else {
                Glide.with(getContext()).load(avatar).into(holder.iv);
            }
            if (type == 0) {
                if (buyer_id.equals(member_id)) {
                    holder.title.setText("您在" + list.get(position).getCreate_time() + "推广成功，预计商品佣金:" + list.get(position).getMoney() + "元，预计结算时间:收货后次月25日结算");
                } else {
                    holder.title.setText(member_name + "在" + list.get(position).getCreate_time() + "推广成功，预计商品佣金:" + list.get(position).getMoney() + "元，预计结算时间:收货后次月25日结算");
                }
                holder.order.setText("订单号:  " + list.get(position).getTrade_id());
                holder.order_money.setText("订单金额:  " + list.get(position).getTotal() + "元");
                holder.shop_money.setText("获得商品佣金:  " + list.get(position).getMoney() + "元");
                holder.status.setVisibility(View.VISIBLE);
                String tk_status = list.get(position).getTk_status();
                String collect = list.get(position).getCollect();/*是否已转余额字段*/
                if (list.get(position).getFreeze().equals("1")) {
                    holder.status.setText("订单已冻结");
                    holder.status.setTextColor(0xffff0000);
                    holder.jiesuan_time.setVisibility(View.GONE);
                } else {
                    if (tk_status.equals("3")) {
                        if (!TextUtils.isEmpty(collect)) {
                            if (Double.valueOf(collect) > 0) {
                                holder.status.setText("已转余额");
                                holder.status.setTextColor(0xff17a2b8);
                            } else {
                                holder.status.setText("订单结算");
                                holder.status.setTextColor(0xff28a745);
                            }
                        } else {
                            holder.status.setText("订单结算");
                            holder.status.setTextColor(0xff28a745);
                        }
                        holder.jiesuan_time.setVisibility(View.VISIBLE);
                        holder.jiesuan_time.setText("结算时间: " + list.get(position).getEarning_time());
                    } else if (tk_status.equals("12")) {
                        holder.status.setText("订单付款");
                        holder.status.setTextColor(0xffdc3545);
                        holder.jiesuan_time.setVisibility(View.GONE);
                    } else if (tk_status.equals("13")) {
                        holder.status.setText("订单失效");
                        holder.status.setTextColor(0xff6c757d);
                        holder.jiesuan_time.setVisibility(View.GONE);
                    } else if (tk_status.equals("14")) {
                        holder.status.setText("订单成功");
                        holder.status.setTextColor(0xffdc3545);
                        holder.jiesuan_time.setVisibility(View.GONE);
                    }
                }
            } else if (type == 1) {
                holder.title.setText("恭喜来自您团队成员 【" + list.get(position).getMember_name() + "】 的团队奖金到账啦");
                holder.order.setText("缴纳金额:  " + list.get(position).getTotal() + "元");
                holder.order_money.setText("获得团队奖金:  " + list.get(position).getCredit() + "元");
                holder.shop_money.setText("结算时间:  " + DateUtils.getTimeToString(Long.valueOf(list.get(position).getDateline()) * 1000));
                holder.status.setVisibility(View.GONE);
                holder.jiesuan_time.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }
    }

    public class MingXiHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        CircleImageView iv;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.order)
        TextView order;
        @BindView(R.id.order_money)
        TextView order_money;
        @BindView(R.id.shop_money)
        TextView shop_money;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.jiesuan_time)
        TextView jiesuan_time;

        public MingXiHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
