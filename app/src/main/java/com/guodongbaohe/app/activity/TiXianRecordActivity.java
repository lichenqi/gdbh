package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guodongbaohe.app.MainActivity;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.RecordBean;
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
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TiXianRecordActivity extends BaseActivity {
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    @BindView(R.id.nodata)
    ImageView nodata;
    TiXianRecordAdapter adapter;
    private int pageNum = 1;
    String member_id, stringExtra;
    List<RecordBean.RecordData> list = new ArrayList<>();
    ImageView iv_back;

    @Override
    public int getContainerView() {
        return R.layout.tixianrecordactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        setMiddleTitle("提现记录");
        Intent intent = getIntent();
        stringExtra = intent.getStringExtra(Constant.TOMAINTYPE);
        member_id = PreferUtils.getString(getApplicationContext(), "member_id");
        getData();
        xrecycler.setHasFixedSize(true);
        xrecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        XRecyclerViewUtil.setView(xrecycler);
        adapter = new TiXianRecordAdapter(list);
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
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        adapter.setOnClickListener(new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                int pos = position - 1;
                /*取消提现记录*/
                cancelTiXianRecord(pos);
            }
        });
    }

    private void getData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("page", String.valueOf(pageNum));
        map.put("limit", "10");
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        final String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.TIXIAN_RECORD + param)
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
                        Log.i("明细", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            int status = jsonObject.getInt("status");
                            if (status >= 0) {
                                RecordBean bean = GsonUtil.GsonToBean(response.toString(), RecordBean.class);
                                if (bean == null) return;
                                List<RecordBean.RecordData> result = bean.getResult();
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
                                xrecycler.loadMoreComplete();
                                xrecycler.refreshComplete();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        xrecycler.loadMoreComplete();
                        xrecycler.refreshComplete();
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

    /*取消提现记录*/
    private void cancelTiXianRecord(int pos) {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("withdraw_id", list.get(pos).getId());
        String qianMingMapParam = ParamUtil.getQianMingMapParam(map);
        String token = EncryptUtil.encrypt(qianMingMapParam + Constant.NETKEY);
        map.put(Constant.TOKEN, token);
        final String param = ParamUtil.getMapParam(map);
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.QUXIAOSHENQING + "?" + param)
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
                        Log.i("取消提现记录", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                xrecycler.refresh();
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
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

    public class TiXianRecordAdapter extends RecyclerView.Adapter<TiXianRecordHolder> {
        private List<RecordBean.RecordData> list;
        private OnItemClick onItemClick;

        private void setOnClickListener(OnItemClick onItemClick) {
            this.onItemClick = onItemClick;
        }

        public TiXianRecordAdapter(List<RecordBean.RecordData> list) {
            this.list = list;
        }

        @Override
        public TiXianRecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.tixianrecordadapter, parent, false);
            return new TiXianRecordHolder(view);
        }

        @Override
        public void onBindViewHolder(final TiXianRecordHolder holder, int position) {
            holder.money.setText("¥" + list.get(position).getMoney());
            String datetime = list.get(position).getDateline();
            String timeToString = DateUtils.getTimeToString(Long.valueOf(datetime) * 1000);
            holder.time.setText(timeToString);
            String status = list.get(position).getStatus();
            String reason = list.get(position).getReason();
            if (TextUtils.isEmpty(reason)) {
                holder.tv_result.setText("拒绝理由: 暂无");
            } else {
                holder.tv_result.setText("拒绝理由: " + reason);
            }
            if (status.equals("-1")) {
                holder.status.setText("申请拒绝");
                holder.status.setTextColor(0xfff40000);
                holder.tv_quxiao.setVisibility(View.GONE);
                holder.tv_result.setVisibility(View.VISIBLE);
            } else if (status.equals("0")) {
                holder.status.setText("等待处理");
                holder.status.setTextColor(0xff008080);
                holder.tv_quxiao.setVisibility(View.VISIBLE);
                holder.tv_result.setVisibility(View.GONE);
            } else if (status.equals("1")) {
                holder.status.setText("提现成功");
                holder.status.setTextColor(0xff000000);
                holder.tv_quxiao.setVisibility(View.GONE);
                holder.tv_result.setVisibility(View.GONE);
            }
            if (onItemClick != null) {
                holder.tv_quxiao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClick.OnItemClickListener(holder.itemView, holder.getAdapterPosition());
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }
    }

    public class TiXianRecordHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.money)
        TextView money;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.status)
        TextView status;
        @BindView(R.id.tv_quxiao)
        TextView tv_quxiao;
        @BindView(R.id.tv_result)
        TextView tv_result;

        public TiXianRecordHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
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

}
