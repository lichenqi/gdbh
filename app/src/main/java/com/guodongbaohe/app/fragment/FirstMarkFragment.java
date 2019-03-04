package com.guodongbaohe.app.fragment;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.YaoQingFriendActivity;
import com.guodongbaohe.app.adapter.DepartmentAdapter;
import com.guodongbaohe.app.bean.DepartmentBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.popupwindow_util.PopupWindowUtil;
import com.guodongbaohe.app.util.ClipContentUtil;
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

public class FirstMarkFragment extends android.support.v4.app.Fragment {
    private View view, shaixuanview;
    @BindView(R.id.xrecycler)
    XRecyclerView xrecycler;
    @BindView(R.id.re_no_shichang)
    RelativeLayout re_no_shichang;
    @BindView(R.id.lijishare)
    TextView lijishare;
    String member_id, userName;
    int pageNum = 1;
    List<DepartmentBean.DepartmentData> list = new ArrayList<>();
    DepartmentAdapter adapter;
    @BindView(R.id.ll_choose)
    LinearLayout ll_choose;
    @BindView(R.id.tv_choose)
    TextView tv_choose;
    @BindView(R.id.ll_time)
    LinearLayout ll_time;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.iv_time)
    ImageView iv_time;
    @BindView(R.id.ll_scale)
    LinearLayout ll_scale;
    @BindView(R.id.tv_scale)
    TextView tv_scale;
    @BindView(R.id.iv_scale)
    ImageView iv_scale;
    PopupWindow popupWindow;
    TextView all, boss, partner, vip;
    /*筛选条件 0:全部；1：合伙人；2：总裁*/
    private String choose_filter = "";
    /*排序条件*/
    private String sort = "DESC";
    /*时间和规模*/
    private String field = "member_id";
    /*团队规模排序*/
    private boolean is_scale = true;
    /*加入时间排序*/
    private boolean is_time = true;
    int which_poition;
    ClipboardManager cm;
    ClipData mClipData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            which_poition = getArguments().getInt("which_poition");
        }
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.firstmarkfragment, container, false);
            ButterKnife.bind(this, view);
            member_id = PreferUtils.getString(getContext(), "member_id");
            userName = PreferUtils.getString(getContext(), "userName");
            getFirstMarketData();
            initRecyclerview();
            shaixuanview = LayoutInflater.from(getContext()).inflate(R.layout.shaixuanview, null);
            all = (TextView) shaixuanview.findViewById(R.id.all);
            boss = (TextView) shaixuanview.findViewById(R.id.boss);
            partner = (TextView) shaixuanview.findViewById(R.id.partner);
            vip = (TextView) shaixuanview.findViewById(R.id.vip);
            iniChooseListener();
        }
        return view;
    }

    /*筛选点击*/
    private void iniChooseListener() {
        /*全部按钮*/
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                    backgroundAlpha(1f);
                }
                all.setTextColor(0xfff6c15b);
                boss.setTextColor(0xff000000);
                partner.setTextColor(0xff000000);
                vip.setTextColor(0xff000000);
                tv_choose.setTextColor(0xff000000);
                choose_filter = "";
                pageNum = 1;
                getFirstMarketData();
            }
        });
        /*总裁按钮*/
        boss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                    backgroundAlpha(1f);
                }
                all.setTextColor(0xff000000);
                boss.setTextColor(0xfff6c15b);
                partner.setTextColor(0xff000000);
                vip.setTextColor(0xff000000);
                tv_choose.setTextColor(0xfff6c15b);
                choose_filter = "7";
                pageNum = 1;
                getFirstMarketData();
            }
        });
        /*合伙人按钮*/
        partner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                    backgroundAlpha(1f);
                }
                all.setTextColor(0xff000000);
                boss.setTextColor(0xff000000);
                partner.setTextColor(0xfff6c15b);
                vip.setTextColor(0xff000000);
                tv_choose.setTextColor(0xfff6c15b);
                choose_filter = "5";
                pageNum = 1;
                getFirstMarketData();
            }
        });
        /*vip按钮*/
        vip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                    backgroundAlpha(1f);
                }
                all.setTextColor(0xff000000);
                boss.setTextColor(0xff000000);
                partner.setTextColor(0xff000000);
                vip.setTextColor(0xfff6c15b);
                tv_choose.setTextColor(0xfff6c15b);
                choose_filter = "3";
                pageNum = 1;
                getFirstMarketData();
            }
        });
    }

    @OnClick({R.id.ll_choose, R.id.ll_time, R.id.ll_scale, R.id.lijishare})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_choose:
                showShaiXuanPopupindow();
                break;
            case R.id.ll_time:
                field = "member_id";
                tv_time.setTextColor(0xfff6c15b);
                tv_scale.setTextColor(0xff000000);
                iv_scale.setImageResource(R.mipmap.moren_black);
                if (is_time) {
                    iv_time.setImageResource(R.mipmap.moren_yellow_shang);
                    sort = "ASC";
                } else {
                    iv_time.setImageResource(R.mipmap.moren_yellow_xia);
                    sort = "DESC";
                }
                is_time = !is_time;
                pageNum = 1;
                getFirstMarketData();
                break;
            case R.id.ll_scale:/*团队规模*/
                field = "fans";
                tv_scale.setTextColor(0xfff6c15b);
                tv_time.setTextColor(0xff000000);
                iv_time.setImageResource(R.mipmap.moren_black);
                if (is_scale) {
                    sort = "DESC";
                    iv_scale.setImageResource(R.mipmap.moren_yellow_xia);
                } else {
                    sort = "ASC";
                    iv_scale.setImageResource(R.mipmap.moren_yellow_shang);
                }
                is_scale = !is_scale;
                pageNum = 1;
                getFirstMarketData();
                break;
            case R.id.lijishare:
                Intent intent = new Intent(getContext(), YaoQingFriendActivity.class);
                startActivity(intent);
                break;
        }
    }

    /*筛选弹框提示*/
    private void showShaiXuanPopupindow() {
        popupWindow = new PopupWindow(shaixuanview, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        backgroundAlpha(0.5f);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置好参数之后再show
        int windowPos[] = PopupWindowUtil.calculatePopWindowPos(ll_choose, shaixuanview);
        int xOff = 40; // 可以自己调整偏移
        windowPos[0] -= xOff;
        popupWindow.showAtLocation(ll_choose, Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

    private void initRecyclerview() {
        xrecycler.setHasFixedSize(true);
        xrecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        XRecyclerViewUtil.setView(xrecycler);
        adapter = new DepartmentAdapter(getContext(), list, userName);
        xrecycler.setAdapter(adapter);
        xrecycler.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                getFirstMarketData();
            }

            @Override
            public void onLoadMore() {
                pageNum++;
                getFirstMarketData();
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

    private void showPersonalDataDialog(final int i) {
        final Dialog dialog = new Dialog(getContext(), R.style.transparentFrameWindowStyle);
        dialog.setContentView(R.layout.showpersonaldatadialog);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView cancel = (TextView) dialog.findViewById(R.id.cancel);
        CircleImageView circleimageview = (CircleImageView) dialog.findViewById(R.id.circleimageview);
        TextView name = (TextView) dialog.findViewById(R.id.name);
        final TextView invite_code = (TextView) dialog.findViewById(R.id.invite_code);
        TextView wechat = (TextView) dialog.findViewById(R.id.wechat);
        TextView month = (TextView) dialog.findViewById(R.id.month);
        TextView total = (TextView) dialog.findViewById(R.id.total);
        TextView time = (TextView) dialog.findViewById(R.id.time);
        RelativeLayout re_id = (RelativeLayout) dialog.findViewById(R.id.re_id);
        RelativeLayout re_wchat_num = (RelativeLayout) dialog.findViewById(R.id.re_wchat_num);
        String avatar = list.get(i).getAvatar();
        if (TextUtils.isEmpty(avatar)) {
            circleimageview.setImageResource(R.mipmap.user_moren_logo);
        } else {
            Glide.with(getContext()).load(avatar).into(circleimageview);
        }
        name.setText(list.get(i).getMember_name());
        invite_code.setText(list.get(i).getInvite_code());
        final String wechat_name = list.get(i).getWechat();
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
        re_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                mClipData = ClipData.newPlainText("Label", list.get(i).getInvite_code());
                cm.setPrimaryClip(mClipData);
                ToastUtils.showToast(getContext(), "邀请ID复制成功");
                ClipContentUtil.getInstance(getContext()).putNewSearch(list.get(i).getInvite_code());//保存记录到数据库
            }
        });
        re_wchat_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(wechat_name)) {
                    cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    mClipData = ClipData.newPlainText("Label", wechat_name);
                    cm.setPrimaryClip(mClipData);
                    ToastUtils.showToast(getContext(), "微信号复制成功");
                    ClipContentUtil.getInstance(getContext()).putNewSearch(wechat_name);//保存记录到数据库
                }
            }
        });
        dialog.show();
    }

    private void getFirstMarketData() {
        long timelineStr = System.currentTimeMillis() / 1000;
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put(Constant.TIMELINE, String.valueOf(timelineStr));
        map.put(Constant.PLATFORM, Constant.ANDROID);
        map.put("member_id", member_id);
        map.put("page", String.valueOf(pageNum));
        map.put("type", String.valueOf(which_poition + 1));
        map.put("filter", choose_filter);
        map.put("sort", sort);
        map.put("field", field);
        map.put("limit", "15");
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
                        Log.i("第一市场", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                DepartmentBean bean = GsonUtil.GsonToBean(response.toString(), DepartmentBean.class);
                                List<DepartmentBean.DepartmentData> result = bean.getResult();
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
                        xrecycler.refreshComplete();
                        xrecycler.loadMoreComplete();
                        ToastUtils.showToast(getContext(), Constant.NONET);
                    }
                });
    }
}
