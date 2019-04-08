package com.guodongbaohe.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.guodongbaohe.app.util.CommonUtil;
import com.guodongbaohe.app.util.NetUtil;
import com.guodongbaohe.app.util.ToastUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageLookAndLoadActivity extends BigBaseActivity {
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.tv_save)
    TextView tv_save;
    ArrayList<String> bannerList;
    int currentPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagelookandloadactivity);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        bannerList = intent.getStringArrayListExtra("bannerList");
        currentPosition = intent.getIntExtra("currentPosition", 0);
        viewpager.setAdapter(new ImageviewAdapter());
        viewpager.setCurrentItem(currentPosition);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.tv_save})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.tv_save:
                if (NetUtil.getNetWorkState(ImageLookAndLoadActivity.this) < 0) {
                    ToastUtils.showToast(getApplicationContext(), "您的网络异常，请联网重试");
                    return;
                }
                if (ContextCompat.checkSelfPermission(ImageLookAndLoadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(ImageLookAndLoadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    //没有存储权限
                    ActivityCompat.requestPermissions(ImageLookAndLoadActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    savePicsToLocal();
                }
                break;
        }
    }

    /*直接保存图片*/
    private void savePicsToLocal() {
        /*网络路劲存储*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL imageurl;
                try {
                    imageurl = new URL(bannerList.get(currentPosition));
                    HttpURLConnection conn = (HttpURLConnection) imageurl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                    Message msg = new Message();
                    // 把bm存入消息中,发送到主线程
                    msg.obj = bitmap;
                    handler.sendMessage(msg);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            CommonUtil.saveBitmap2file(bitmap, getApplicationContext());
        }
    };

    public class ImageviewAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return bannerList == null ? 0 : bannerList.size();
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
            Glide.with(getApplicationContext()).load(bannerList.get(position)).placeholder(R.drawable.loading_img).into(imageView);
            container.addView(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });
            return imageView;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getApplicationContext(), "图片保存需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length <= 1 || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.showToast(getApplicationContext(), "图片保存需要打开存储权限，请前往设置-应用-果冻宝盒-权限进行设置");
                    return;
                } else if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    savePicsToLocal();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
