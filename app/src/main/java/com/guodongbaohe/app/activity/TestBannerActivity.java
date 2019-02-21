package com.guodongbaohe.app.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BaseActivity;
import com.guodongbaohe.app.bean.BannerDataBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.VersionUtil;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestBannerActivity extends BaseActivity {
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.llpoint)
    LinearLayout llpoint;

    @Override
    public int getContainerView() {
        return R.layout.testbanneractivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMiddleTitle("轮播测试");
        ButterKnife.bind(this);
        /*获取数据集*/
        getBannerData();

    }

    ImageView[] indicators;
    List<BannerDataBean.BannerList> banner_result;

    private void getBannerData() {
        String member_id = PreferUtils.getString(getApplicationContext(), "member_id");
        if (TextUtils.isEmpty(member_id)) {
            member_id = "";
        }
        String url = Constant.BASE_URL + Constant.BANNER + "/";
        MyApplication.getInstance().getMyOkHttp().post().url(url).tag(this)
                .addHeader("x-userid", member_id)
                .addHeader("x-appid", Constant.APPID)
                .addHeader("x-devid", PreferUtils.getString(getApplicationContext(), Constant.PESUDOUNIQUEID))
                .addHeader("x-nettype", PreferUtils.getString(getApplicationContext(), Constant.NETWORKTYPE))
                .addHeader("x-agent", VersionUtil.getVersionCode(getApplicationContext()))
                .addHeader("x-platform", Constant.ANDROID)
                .addHeader("x-devtype", Constant.IMEI)
                .addHeader("x-token", member_id)
                .tag(this)
                .enqueue(new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess(statusCode, response);
                        Log.i("banner数据", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            if (jsonObject.getInt("status") >= 0) {
                                BannerDataBean bannerDataBean = GsonUtil.GsonToBean(response.toString(), BannerDataBean.class);
                                if (bannerDataBean == null) return;
                                banner_result = bannerDataBean.getResult();
                                if (banner_result.size() == 0) return;
                                indicators = new ImageView[banner_result.size()];
                                for (int i = 0; i < banner_result.size(); i++) {
                                    View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.view_cycle_viewpager_indicator, null);
                                    ImageView iv = (ImageView) view.findViewById(R.id.image_indicator);
                                    indicators[i] = iv;
                                    llpoint.addView(view);
                                }
                                setIndicator(0);
                                viewpager.setAdapter(new MyPagerAdapter());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                    }
                });
    }

    /*banner适配器*/
    public class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (banner_result == null) {
                return 0;
            }
            if (banner_result.size() == 1) {
                return 1;
            } else {
                return Integer.MAX_VALUE;
            }
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.banner_viewpager, container, false);
            RoundedImageView iv = (RoundedImageView) view.findViewById(R.id.iv);
            Glide.with(getApplicationContext()).load(banner_result.get(position % banner_result.size()).getImage()).into(iv);
            container.addView(view);
            return view;
        }
    }

    /*设置指示器*/
    private void setIndicator(int selectedPosition) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[i].setBackgroundResource(R.drawable.indicator_unselected1);
        }
        indicators[selectedPosition % indicators.length].setBackgroundResource(R.drawable.indicator_selected1);
    }

}
