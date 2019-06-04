package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.guodongbaohe.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PicsAdapter extends RecyclerView.Adapter<PicsAdapter.PicsHolder> {
    private List<String> list_detail;
    private Context context;

    public PicsAdapter(List<String> list_detail) {
        this.list_detail = list_detail;
    }

    @Override
    public PicsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from( context ).inflate( R.layout.item_pics, parent, false );
        return new PicsHolder( view );
    }

    @Override
    public void onBindViewHolder(final PicsHolder holder, int position) {
        final int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        String url = list_detail.get( position );
        if (!url.substring( 0, 4 ).equals( "http" )) {
            url = "http:" + url;
        }
        Glide.with( context ).load( url ).asBitmap().placeholder( R.drawable.loading_img )
                .skipMemoryCache( true )
                .diskCacheStrategy( DiskCacheStrategy.NONE )
                .into( new SimpleTarget<Bitmap>( Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL ) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        //原始图片宽高
                        int imageWidth = resource.getWidth();
                        int imageHeight = resource.getHeight();
                        if (imageHeight > 300) {
                            //按比例收缩图片
                            float ratio = (float) ((imageWidth * 1.0) / (widthPixels * 1.0));
                            int height = (int) (imageHeight * 1.0 / ratio);
                            ViewGroup.LayoutParams params = holder.iv.getLayoutParams();
                            params.width = widthPixels;
                            params.height = height;
                            holder.iv.setImageBitmap( resource );
                        }
                    }
                } );
    }

    @Override
    public int getItemCount() {
        return list_detail == null ? 0 : (list_detail.size() > 30 ? 30 : list_detail.size());
    }

    public class PicsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        ImageView iv;

        public PicsHolder(View itemView) {
            super( itemView );
            ButterKnife.bind( this, itemView );
        }
    }

}
