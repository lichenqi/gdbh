package com.guodongbaohe.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.base_activity.BigBaseActivity;
import com.guodongbaohe.app.util.NetPicsToBitmap;
import com.guodongbaohe.app.util.PreferUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PosterPhotoViewPagerActivity extends BigBaseActivity {
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.show_origin_pic_dot)
    TextView show_origin_pic_dot;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    ArrayList<String> pics_list;
    int mode;
    List<String> all_list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transparencypicsactivity);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        int current = intent.getIntExtra("current", 0);
        mode = intent.getIntExtra("mode", 0);
        pics_list = intent.getStringArrayListExtra("pics_list");
        String one_buide_pic = PreferUtils.getString(getApplicationContext(), "one_buide_pic");
        all_list = new ArrayList<>();
        if (mode == 1) {
            all_list.add(0, one_buide_pic);
        }
        all_list.addAll(pics_list);
        viewpager.setAdapter(new ImageviewAdapter());
        viewpager.setCurrentItem(current);
        show_origin_pic_dot.setText((current + 1) + "/" + all_list.size());
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                show_origin_pic_dot.setText((position + 1) + "/" + all_list.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.ap2, R.anim.ap1);
            }
        });
    }

    public class ImageviewAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return all_list == null ? 0 : all_list.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            ImageView imageView = new ImageView(container.getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            if (mode == 1) {
                if (position == 0) {
                    Bitmap bitmap = NetPicsToBitmap.convertStringToIcon(all_list.get(0));
                    imageView.setImageBitmap(bitmap);
                } else {
                    Glide.with(getApplicationContext()).load(all_list.get(position)).placeholder(R.drawable.loading_img).into(imageView);
                }
            } else {
                Glide.with(getApplicationContext()).load(all_list.get(position)).placeholder(R.drawable.loading_img).into(imageView);
            }
            container.addView(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    overridePendingTransition(R.anim.ap2, R.anim.ap1);
                }
            });
            return imageView;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            overridePendingTransition(R.anim.ap2, R.anim.ap1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
