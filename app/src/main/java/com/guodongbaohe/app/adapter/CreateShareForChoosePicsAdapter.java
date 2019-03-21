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
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateShareForChoosePicsAdapter extends RecyclerView.Adapter<CreateShareForChoosePicsAdapter.CreateShareForChoosePicsHolder> {
    private Context context;
    private List<ChooseImagsNum> list;
    private int type;
    public OnItemClick onItemClick, iv_checked_click;

    public void setOnCheckedListener(OnItemClick iv_checked_click) {
        this.iv_checked_click = iv_checked_click;
    }

    public void setonclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public CreateShareForChoosePicsAdapter(Context context, List<ChooseImagsNum> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public CreateShareForChoosePicsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.createshareforchoosepicsadapter, parent, false);
        return new CreateShareForChoosePicsHolder(view);
    }

    @Override
    public void onBindViewHolder(final CreateShareForChoosePicsHolder holder, int position) {
        if (type == 1) {
            if (position == 0) {
                Bitmap bitmap = NetPicsToBitmap.convertStringToIcon(list.get(0).getUrl());
                holder.iv.setImageBitmap(bitmap);
            } else {
                NetImageLoadUtil.loadImage(list.get(position).getUrl(), context, holder.iv);
            }
        } else {
            NetImageLoadUtil.loadImage(list.get(position).getUrl(), context, holder.iv);
        }

        if (list.get(position).isChecked()) {
            holder.iv_check.setImageResource(R.mipmap.green_choose);
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

    public void refreDataChange(int type) {
        this.type = type;
        notifyDataSetChanged();
    }

    public class CreateShareForChoosePicsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        RoundedImageView iv;
        @BindView(R.id.iv_check)
        ImageView iv_check;

        public CreateShareForChoosePicsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
