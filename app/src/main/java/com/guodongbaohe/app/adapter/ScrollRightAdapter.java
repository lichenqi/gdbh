package com.guodongbaohe.app.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.ScrollBean;
import com.guodongbaohe.app.util.NetPicsToBitmap;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.guodongbaohe.app.R.id.right_image;

/**
 * Created by Raul_lsj on 2018/3/28.
 */

public class ScrollRightAdapter extends BaseSectionQuickAdapter<ScrollBean, BaseViewHolder> {

    public ScrollRightAdapter(int layoutResId, int sectionHeadResId, List<ScrollBean> data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, ScrollBean item) {
        helper.setText(R.id.right_title, item.header);

    }

    @Override
    protected void convert(final BaseViewHolder helper, final ScrollBean item) {
      final   ScrollBean.ScrollItemBean t = item.t;
        helper.setText(R.id.right_text, t.getText());
        ImageView imageView=helper.getView(R.id.right_image);
        Glide.with(mContext).load(t.getType()).into(imageView);
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,t.getText(),Toast.LENGTH_SHORT).show();
            }
        });
    }


}
