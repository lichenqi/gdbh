package com.guodongbaohe.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.FuzzyAdater;
import com.guodongbaohe.app.adapter.HotSearchAdapter;
import com.guodongbaohe.app.adapter.SearchAdapter;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.FuzzyData;
import com.guodongbaohe.app.bean.HotBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.itemdecoration.TimeItemDecoration;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DensityUtils;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.HistorySearchUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.guodongbaohe.app.view.FlowLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends BaseActivity {
    /*取消按钮*/
    @BindView(R.id.finish)
    TextView finish;
    /*搜索框*/
    @BindView(R.id.ed_keyword)
    EditText ed_keyword;
    /*r热门搜索*/
    @BindView(R.id.hot_recyclerview)
    RecyclerView hot_recyclerview;
    /*历史搜索布局*/
    @BindView(R.id.ll_histoy_notiy)
    RelativeLayout ll_histoy_notiy;
    /*历史搜索 流式布局*/
    @BindView(R.id.flow_layout)
    FlowLayout flow_layout;
    /*模糊查询*/
    @BindView(R.id.fuzzy_recycler)
    RecyclerView fuzzy_recycler;
    @BindView(R.id.ll_hot_and_histoy)
    LinearLayout ll_hot_and_histoy;
    @BindView(R.id.iv_cancel)
    RelativeLayout iv_cancel;
    Intent intent;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    // 声明一个订阅方法，用于接收事件
    @Subscribe
    public void onEvent(String msg) {
        if (msg.equals(Constant.SEARCH_BACK)) {
            finish();
        }
    }

    @Override
    public int getContainerView() {
        return R.layout.searchactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setMiddleTitle("搜索");
        getHistoryList();
        /*监听键盘输入的字*/
        setEditWatch();
        /*热门搜索布局*/
        initHotView();
        /*模糊查询布局*/
        initFuzzyView();
        initEditTextView();
    }

    private void initEditTextView() {
        ed_keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString())) {
                    getFuzzyData(s.toString());
                    iv_cancel.setVisibility(View.VISIBLE);
                } else {
                    fuzzy_recycler.setVisibility(View.GONE);
                    ll_hot_and_histoy.setVisibility(View.VISIBLE);
                    iv_cancel.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getFuzzyData(String s) {
        HashMap<String, String> map = new HashMap<>();
        map.put("q", s);
        map.put("code", "utf-8");
        String param = ParamUtil.getMapParam(map);
        Log.i("搜索词组", s);
        MyApplication.getInstance().getMyOkHttp().post()
                .url(Constant.FUZZY_DATA + param)
                .tag(this)
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("模糊", response.toString());
                        FuzzyData fuzzyData = GsonUtil.GsonToBean(response.toString(), FuzzyData.class);
                        final List<List<String>> result = fuzzyData.getResult();
                        if (result.size() > 0) {
                            fuzzy_recycler.setVisibility(View.VISIBLE);
                            ll_hot_and_histoy.setVisibility(View.GONE);
                            FuzzyAdater fuzzyAdater = new FuzzyAdater(getApplicationContext(), result);
                            fuzzy_recycler.setAdapter(fuzzyAdater);
                            fuzzyAdater.setonclicklistener(new OnItemClick() {
                                @Override
                                public void OnItemClickListener(View view, int position) {
                                    //TODO
                                    HistorySearchUtil.getInstance(SearchActivity.this).putNewSearch(result.get(position).get(0));//保存记录到数据库
                                    intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                                    intent.putExtra("keyword", result.get(position).get(0));
                                    startActivityForResult(intent, 1);
                                    getHistoryList();
                                }
                            });
                        } else {
                            fuzzy_recycler.setVisibility(View.GONE);
                            ll_hot_and_histoy.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

    private void initFuzzyView() {
        hot_recyclerview.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        fuzzy_recycler.setLayoutManager(manager);
    }

    private void initHotView() {
        hot_recyclerview.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 4);
        TimeItemDecoration itemDecoration = new TimeItemDecoration(DensityUtils.dip2px(getApplicationContext(), 10), DensityUtils.dip2px(getApplicationContext(), 5));
        hot_recyclerview.addItemDecoration(itemDecoration);
        hot_recyclerview.setLayoutManager(manager);
        getHotData();
    }

    private void getHotData() {
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + Constant.HOT_SEARCH)
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
                        Log.i("热门", response.toString());
                        HotBean hotBean = GsonUtil.GsonToBean(response.toString(), HotBean.class);
                        if (hotBean == null) return;
                        final List<HotBean.HotBeanData> hot_list = hotBean.getResult();
                        HotSearchAdapter hotSearchAdapter = new HotSearchAdapter(getApplicationContext(), hot_list);
                        hot_recyclerview.setAdapter(hotSearchAdapter);
                        hotSearchAdapter.setonclicklistener(new OnItemClick() {
                            @Override
                            public void OnItemClickListener(View view, int position) {
                                //TODO
                                intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                                intent.putExtra("keyword", hot_list.get(position).getWord());
                                startActivityForResult(intent, 1);
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast(getApplicationContext(), Constant.NONET);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ed_keyword.setFocusable(true);
        ed_keyword.setFocusableInTouchMode(true);
        ed_keyword.requestFocus();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(ed_keyword, 0);
            }
        }, 100);
    }

    private void setEditWatch() {
        ed_keyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String content = ed_keyword.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        ToastUtils.showToast(getApplicationContext(), "请先输入搜索关键字");
                    } else {
                        HistorySearchUtil.getInstance(SearchActivity.this).putNewSearch(content);//保存记录到数据库
                        getHistoryList();
                        intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                        intent.putExtra("keyword", content);
                        startActivityForResult(intent, 1);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    SearchAdapter adapter;

    private void getHistoryList() {
        final List<String> list = HistorySearchUtil.getInstance(getApplicationContext()).queryHistorySearchList();
        Collections.reverse(list);
        if (list.size() > 0) {
            flow_layout.setVisibility(View.VISIBLE);
        } else {
            flow_layout.setVisibility(View.GONE);
        }
        //设置标签
        flow_layout.setLables(list, false);
        flow_layout.setOnClickListener(new FlowLayout.OnItem() {
            @Override
            public void OnItemClick(String name) {
                intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra("keyword", name);
                startActivityForResult(intent, 1);
            }
        });
    }

    @OnClick({R.id.ll_histoy_notiy, R.id.finish, R.id.iv_cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_histoy_notiy:
                HistorySearchUtil.getInstance(getApplicationContext()).deleteAllHistorySearch();
                getHistoryList();
                break;
            case R.id.finish:
                String content = ed_keyword.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showToast(getApplicationContext(), "请先输入搜索关键字");
                } else {
                    HistorySearchUtil.getInstance(SearchActivity.this).putNewSearch(content);//保存记录到数据库
                    getHistoryList();
                    intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                    intent.putExtra("keyword", content);
                    startActivityForResult(intent, 1);
                }
                break;
            case R.id.iv_cancel:
                String content1 = ed_keyword.getText().toString().trim();
                if (!TextUtils.isEmpty(content1)) {
                    ed_keyword.setText("");
                    iv_cancel.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1) {
            String keyword = data.getStringExtra("keyword");
            ed_keyword.setText(keyword);
            ed_keyword.setSelection(keyword.length());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
