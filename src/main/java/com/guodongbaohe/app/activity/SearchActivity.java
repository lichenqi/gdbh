package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
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
import com.guodongbaohe.app.bean.ClipBean;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;
import org.litepal.LitePal;

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
    /*历史搜索*/
    @BindView(R.id.recyclerview_histor_search)
    RecyclerView recyclerview_histor_search;
    /*模糊查询*/
    @BindView(R.id.fuzzy_recycler)
    RecyclerView fuzzy_recycler;
    @BindView(R.id.ll_hot_and_histoy)
    LinearLayout ll_hot_and_histoy;
    Intent intent;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (dialog != null) {
            dialog.dismiss();
        }
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
        /*历史搜索布局*/
        initHistorView();
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
                } else {
                    fuzzy_recycler.setVisibility(View.GONE);
                    ll_hot_and_histoy.setVisibility(View.VISIBLE);
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

    private void initHistorView() {
        recyclerview_histor_search.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 4);
        TimeItemDecoration itemDecoration = new TimeItemDecoration(DensityUtils.dip2px(getApplicationContext(), 10), DensityUtils.dip2px(getApplicationContext(), 5));
        recyclerview_histor_search.addItemDecoration(itemDecoration);
        recyclerview_histor_search.setLayoutManager(manager);
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
        /*获取剪切板内容*/
        getClipContent();
    }

    Dialog dialog;

    private void getClipContent() {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData data = cm.getPrimaryClip();
        if (data == null) return;
        ClipData.Item item = data.getItemAt(0);
        final String content = item.getText().toString();
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

    private void showDialog(final String content) {
        PreferUtils.putString(getApplicationContext(), "clip_content", content);
        List<ClipBean> all = LitePal.findAll(ClipBean.class);
        if (all == null) return;
        for (ClipBean bean : all) {
            if (bean.getTitle().equals(content)) {
                return;
            }
        }
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new Dialog(SearchActivity.this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(R.layout.clip_search_dialog);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER | Gravity.CENTER);
        TextView sure = dialog.findViewById(R.id.sure);
        TextView cancel = dialog.findViewById(R.id.cancel);
        TextView title = dialog.findViewById(R.id.content);
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
        adapter = new SearchAdapter(list);
        recyclerview_histor_search.setAdapter(adapter);
        if (list.size() > 0) {
            ll_histoy_notiy.setVisibility(View.VISIBLE);
        } else {
            ll_histoy_notiy.setVisibility(View.GONE);
        }
        adapter.setonclicklistener(new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra("keyword", list.get(position));
                startActivityForResult(intent, 1);
            }
        });
    }

    @OnClick({R.id.ll_histoy_notiy, R.id.finish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_histoy_notiy:
                HistorySearchUtil.getInstance(getApplicationContext()).deleteAllHistorySearch();
                getHistoryList();
                break;
            case R.id.finish:
                finish();
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
