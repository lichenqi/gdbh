package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.guodongbaohe.app.MainActivity;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.OrderH5Bean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.fragment.AllOrderFragment;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.PreferUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyOrderActivity extends BaseActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.radiogroup)
    RadioGroup radiogroup;
    @BindView(R.id.radio_one)
    RadioButton radio_one;
    @BindView(R.id.radio_two)
    RadioButton radio_two;
    @BindView(R.id.radio_three)
    RadioButton radio_three;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.order_shuoming)
    RelativeLayout order_shuoming;
    private List<Fragment> fragments;
    Bundle bundle;
    String stringExtra;
    ImageView iv_back;

    @Override
    public int getContainerView() {
        return R.layout.myorderactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        stringExtra = intent.getStringExtra(Constant.TOMAINTYPE);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        setMiddleTitle("全部订单");
        getData();
        fragments = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            AllOrderFragment fragment = new AllOrderFragment();
            bundle = new Bundle();
            bundle.putInt("position", i);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        viewpager.setAdapter(new MyOrderAdapter(getSupportFragmentManager()));
        viewpager.addOnPageChangeListener(this);
        radiogroup.setOnCheckedChangeListener(this);
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
    }

    private void getData() {
        order_shuoming.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BaseH5Activity.class);
                intent.putExtra("url", PreferUtils.getString(getApplicationContext(), "order_new"));
                startActivity(intent);
            }
        });
        MyApplication.getInstance().getMyOkHttp().post().url(Constant.BASE_URL + "assets/template?type=order").tag(this)
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                OrderH5Bean bean = GsonUtil.GsonToBean(response.toString(), OrderH5Bean.class);
                                if (bean == null) return;
                                final String content = bean.getResult().get(0).getContent();
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        getTabState(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.radio_one:
                viewpager.setCurrentItem(0);
                break;
            case R.id.radio_two:
                viewpager.setCurrentItem(1);
                break;
            case R.id.radio_three:
                viewpager.setCurrentItem(2);
                break;
        }
    }

    private void getTabState(int index) {
        radio_one.setChecked(false);
        radio_two.setChecked(false);
        radio_three.setChecked(false);
        switch (index) {
            case 0:
                radio_one.setChecked(true);
                break;
            case 1:
                radio_two.setChecked(true);
                break;
            case 2:
                radio_three.setChecked(true);
                break;
        }
    }


    private class MyOrderAdapter extends FragmentPagerAdapter {

        public MyOrderAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
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
