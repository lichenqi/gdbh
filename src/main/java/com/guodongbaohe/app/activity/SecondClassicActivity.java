package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.adapter.SecondClassicShowAdapter;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.CommonBean;
import com.guodongbaohe.app.bean.SecondHeadBean;
import com.guodongbaohe.app.fragment.NewSecondClassicFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SecondClassicActivity extends BaseActivity {
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.re_top_parent)
    RelativeLayout re_top_parent;
    @BindView(R.id.tv_choose_tv)
    TextView tv_choose_tv;
    private List<Fragment> fragments;
    private TabFragmentAdapter adapter;
    int location, position;
    private List<SecondHeadBean> headList = new ArrayList<>();
    private List<CommonBean.CommonSecond> list;
    SecondHeadBean secondHeadBean;
    View popupView;
    RecyclerView recyclerview;

    @Override
    public int getContainerView() {
        return R.layout.secondclassicactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        list = (List<CommonBean.CommonSecond>) intent.getSerializableExtra("list_head");
        location = intent.getIntExtra("location", 0);
        position = intent.getIntExtra("position", 0);
        String title = intent.getStringExtra("title");
        setMiddleTitle(title);
        fragments = new ArrayList<>();
        popupView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.second_classic_show, null);
        recyclerview = (RecyclerView) popupView.findViewById(R.id.recyclerview);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new GridLayoutManager(getApplicationContext(), 4));
        initView();
    }

    @OnClick({R.id.iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv:
                initPopupwindow();
                break;
        }
    }

    private void initView() {
        secondHeadBean = new SecondHeadBean();
        secondHeadBean.setId("0");
        secondHeadBean.setTitle("全部");
        headList.add(secondHeadBean);
        for (int i = 0; i < list.size(); i++) {
            secondHeadBean = new SecondHeadBean();
            secondHeadBean.setTitle(list.get(i).getName());
            secondHeadBean.setId(list.get(i).getCate_id());
            headList.add(secondHeadBean);
        }

        for (int i = 0; i < list.size(); i++) {
            NewSecondClassicFragment secondClassicFragment = new NewSecondClassicFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            if (i == 0) {
                bundle.putString("title", "");
            } else {
                bundle.putString("title", headList.get(i).getTitle());
            }
            secondClassicFragment.setArguments(bundle);
            fragments.add(secondClassicFragment);
        }

        adapter = new TabFragmentAdapter(fragments, getSupportFragmentManager());
        viewpager.setAdapter(adapter);
        tablayout.setupWithViewPager(viewpager);
        tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewpager.setCurrentItem(location);
        initShowClassicView();

    }

    SecondClassicShowAdapter secondClassicShowAdapter;

    private void initShowClassicView() {
        secondClassicShowAdapter = new SecondClassicShowAdapter(headList);
        recyclerview.setAdapter(secondClassicShowAdapter);
        secondClassicShowAdapter.setonclicklistener(new OnItemClick() {
            @Override
            public void OnItemClickListener(View view, int position) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                for (SecondHeadBean bean : headList) {
                    bean.setCheck(false);
                }
                headList.get(position).setCheck(true);
                secondClassicShowAdapter.notifyDataSetChanged();
                viewpager.setCurrentItem(position);
            }
        });
    }

    PopupWindow popupWindow;

    private void initPopupwindow() {
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xFFf4f4f4));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        int[] location = new int[2];
        re_top_parent.getLocationOnScreen(location);
        popupWindow.setAnimationStyle(R.style.style_pop_animation);
        popupWindow.showAsDropDown(re_top_parent);
        startToAnimation(0, 180, iv);
        tv_choose_tv.setVisibility(View.VISIBLE);
        tablayout.setVisibility(View.GONE);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Log.i("状态", "状态");
                popupWindow = null;
                startToAnimation(180, 360, iv);
                tv_choose_tv.setVisibility(View.GONE);
                tablayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public class TabFragmentAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;

        public TabFragmentAdapter(List<Fragment> fragments, FragmentManager fm) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return headList == null ? 0 : headList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return headList.get(position).getTitle();
        }
    }

    private void startToAnimation(int i, int j, ImageView iv) {
        RotateAnimation rotateAnimation = new RotateAnimation(i, j, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setDuration(500);
        iv.startAnimation(rotateAnimation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (popupWindow != null) {
            popupWindow = null;
        }
    }
}
