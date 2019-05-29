package com.guodongbaohe.app.common;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.bean.BannerDataBean;
import com.youth.banner.loader.ImageLoader;

public class RectBannerLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context).load(((BannerDataBean.BannerList) path).getImage())
                .into(imageView);
    }


    @Override
    public ImageView createImageView(Context context) {
        ImageView imageView = new ImageView(context);
        return imageView;
    }
}
