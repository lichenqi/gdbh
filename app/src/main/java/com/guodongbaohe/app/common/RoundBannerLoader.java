package com.guodongbaohe.app.common;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.bean.BannerDataBean;
import com.makeramen.roundedimageview.RoundedImageView;
import com.youth.banner.loader.ImageLoader;

public class RoundBannerLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with( context ).load( ((BannerDataBean.BannerList) path).getImage() ).into( imageView );
    }

    @Override
    public ImageView createImageView(Context context) {
        RoundedImageView rv = new RoundedImageView( context );
        rv.setScaleType( ImageView.ScaleType.FIT_CENTER );
        rv.setCornerRadius( 10 );
        rv.setPadding( 30, 0, 30, 0 );
        return rv;
    }
}
