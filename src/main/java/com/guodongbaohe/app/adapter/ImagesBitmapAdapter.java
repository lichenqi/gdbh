package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.ChooseImagsNum;
import com.guodongbaohe.app.util.NetImageLoadUtil;
import com.guodongbaohe.app.util.NetPicsToBitmap;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImagesBitmapAdapter extends RecyclerView.Adapter<ImagesBitmapAdapter.ImagesBitmapHolder> {
    private Context context;
    private List<ChooseImagsNum> list;
    public OnItemClick onItemClick, iv_checked_click;

    public void setOnCheckedListener(OnItemClick iv_checked_click) {
        this.iv_checked_click = iv_checked_click;
    }

    public void setonclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public ImagesBitmapAdapter(Context context, List<ChooseImagsNum> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ImagesBitmapHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gallerypicsadapter, parent, false);
        return new ImagesBitmapHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImagesBitmapHolder holder, final int position) {
        if (position == 0) {
            Bitmap bitmap = NetPicsToBitmap.convertStringToIcon(list.get(0).getUrl());
            holder.iv.setImageBitmap(bitmap);
        } else {
            NetImageLoadUtil.loadImage(list.get(position).getUrl(), context, holder.iv);
        }
        if (list.get(position).isChecked()) {
            holder.iv_check.setImageResource(R.mipmap.xainshiyjin);
        } else {
            holder.iv_check.setImageResource(R.mipmap.buxianshiyjin);
        }
        if (onItemClick != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.OnItemClickListener(holder.itemView, holder.getAdapterPosition());
                }
            });
        }
        if (iv_checked_click != null) {
            holder.iv_check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iv_checked_click.OnItemClickListener(holder.iv_check, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ImagesBitmapHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        ImageView iv;
        @BindView(R.id.iv_check)
        ImageView iv_check;

        public ImagesBitmapHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
