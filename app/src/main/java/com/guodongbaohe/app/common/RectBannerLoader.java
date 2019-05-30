package com.guodongbaohe.app.common;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.bean.BannerDataBean;
import com.makeramen.roundedimageview.RoundedImageView;
import com.youth.banner.loader.ImageLoader;

public class RectBannerLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        imageView.setScaleType( ImageView.ScaleType.FIT_CENTER );
        Glide.with( context ).load( ((BannerDataBean.BannerList) path).getImage() ).into( imageView );
    }

}
