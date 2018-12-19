package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.FuzzyAdater;
import com.guodongbaohe.app.adapter.HotSearchAdapter;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.bean.ClipBean;
import com.guodongbaohe.app.bean.FuzzyData;
import com.guodongbaohe.app.bean.HotBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.itemdecoration.TimeItemDecoration;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DensityUtils;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransitionSearchActivity extends BigBaseActivity {
    /*返回按钮*/
    @BindView(R.id.tv_back)
    TextView tv_back;
    /*搜索关键字*/
    @BindView(R.id.ed_keyword)
    EditText ed_keyword;
    /*搜索按钮*/
    @BindView(R.id.tv_search)
    TextView tv_search;
    /*热门搜索布局*/
    @BindView(R.id.ll_hot_and_histoy)
    LinearLayout ll_hot_and_histoy;
    /*热门搜索*/
    @BindView(R.id.hot_recyclerview)
    RecyclerView hot_recyclerview;
    /*模糊搜索*/
    @BindView(R.id.fuzzy_recycler)
    RecyclerView fuzzy_recycler;
    Intent intent;

    @Override
    protected void onResume() {
        super.onResume();
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
        dialog = new Dialog(TransitionSearchActivity.this, R.style.transparentFrameWindowStyle);
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transitionsearchactivity);
        ButterKnife.bind(this);
        /*基本操作*/
        Intent intent = getIntent();
        String keyword = intent.getStringExtra("keyword");
        ed_keyword.setText(keyword);
        ed_keyword.setSelection(keyword.length());
        /*监听键盘输入的字*/
        setEditWatch();
        /*热门搜索布局*/
        initHotView();
        /*模糊查询布局*/
        initFuzzyView();
        initEditTextView();
    }

    @OnClick({R.id.tv_back, R.id.tv_search})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                EventBus.getDefault().post(Constant.SEARCH_BACK);
                finish();
                overridePendingTransition(R.anim.ap2, R.anim.ap1);
                break;
            case R.id.tv_search:
                String trim = ed_keyword.getText().toString().trim();
                if (TextUtils.isEmpty(trim)) {
                    ToastUtils.showToast(getApplicationContext(), "请先输入搜索关键字");
                    return;
                }
                intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra("keyword", trim);
                startActivity(intent);
                overridePendingTransition(R.anim.ap2, R.anim.ap1);
                break;
        }
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
                                    //TODO
                                    intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                                    intent.putExtra("keyword", result.get(position).get(0));
                                    startActivityForResult(intent, 1);
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

    private void setEditWatch() {
        ed_keyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String content = ed_keyword.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        ToastUtils.showToast(getApplicationContext(), "请先输入搜索关键字");
                    } else {
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


    private void initHotView() {
        hot_recyclerview.setHasFixedSize(true);
        GridLayoutManager manager = new GridLayoutManager(getApplicationContext(), 4);
        TimeItemDecoration itemDecoration = new TimeItemDecoration(DensityUtils.dip2px(getApplicationContext(), 10), DensityUtils.dip2px(getApplicationContext(), 5));
        hot_recyclerview.addItemDecoration(itemDecoration);
        hot_recyclerview.setLayoutManager(manager);
        getHotData();
    }

    /*热门搜索数据*/
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
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            EventBus.getDefault().post(Constant.SEARCH_BACK);
            finish();
            overridePendingTransition(R.anim.ap2, R.anim.ap1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}