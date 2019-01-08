package com.guodongbaohe.app.activity;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.ClipBean;
import com.guodongbaohe.app.fragment.RankingListFragment;
import com.guodongbaohe.app.util.PreferUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopRangingClassicActivity extends BaseActivity {
    @BindView(R.id.tablayout)
    TabLayout tablayout;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    private String[] titles = {"2小时排行", "全天排行"};
    private List<Fragment> fragments;
    private TabFragmentAdapter adapter;

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
        final String content = item.coerceToText(getApplicationContext()).toString();
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
        dialog = new Dialog(ShopRangingClassicActivity.this, R.style.transparentFrameWindowStyle);
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
        return R.layout.shoprangingclassicactivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        String title = intent.getStringExtra("title");
        setMiddleTitle(title);
        fragments = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            RankingListFragment fragment = new RankingListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", i);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        adapter = new TabFragmentAdapter(fragments, getSupportFragmentManager());
        viewpager.setAdapter(adapter);
        tablayout.setupWithViewPager(viewpager);
        viewpager.setCurrentItem(position);
        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab tab = tablayout.getTabAt(i);//获得每一个tab
            tab.setCustomView(R.layout.tablayout_item);//给每一个tab设置view
            TextView textView = (TextView) tab.getCustomView().findViewById(R.id.tab_text);
            textView.setText(titles[i]);//设置tab上的文字
            textView.setSelected(false);
            if (i == position) {
                // 设置第一个tab的TextView是被选择的样式
                tab.getCustomView().findViewById(R.id.tab_text).setSelected(true);//第一个tab被选中
            }
        }
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.tab_text).setSelected(true);
                viewpager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getCustomView().findViewById(R.id.tab_text).setSelected(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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
            return titles == null ? 0 : titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
