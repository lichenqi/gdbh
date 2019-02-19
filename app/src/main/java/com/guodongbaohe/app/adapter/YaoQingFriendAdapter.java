package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.InviteAwardBean;
import com.guodongbaohe.app.util.PreferUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YaoQingFriendAdapter extends RecyclerView.Adapter<YaoQingFriendAdapter.YaoQingFriendHolder> {
    private Context context;
    private List<InviteAwardBean.InviteAwardData> result;
    private Bitmap qrCodeBitmap;
    private OnItemClick onItemClick;

    public void onclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public YaoQingFriendAdapter(Context context, List<InviteAwardBean.InviteAwardData> result, Bitmap qrCodeBitmap) {
        this.context = context;
        this.result = result;
        this.qrCodeBitmap = qrCodeBitmap;
    }

    @Override
    public YaoQingFriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.yaoqingfriendadapter, parent, false);
        return new YaoQingFriendHolder(view);
    }

    @Override
    public void onBindViewHolder(final YaoQingFriendHolder holder, int position) {
        Glide.with(context).load(result.get(position).getImage()).asBitmap().into(holder.iv);
        holder.code.setText("邀请码:" + PreferUtils.getString(context, "invite_code"));
        holder.qrcode.setImageBitmap(qrCodeBitmap);
        if (result.get(position).isChoose()) {
            holder.iv_choose.setImageResource(R.mipmap.green_choose);
        } else {
            holder.iv_choose.setImageResource(R.mipmap.buxianshiyjin);
        }
        if (onItemClick != null) {
            holder.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.OnItemClickListener(holder.iv, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return result == null ? 0 : result.size();
    }

    public class YaoQingFriendHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        ImageView iv;
        @BindView(R.id.code)
        TextView code;
        @BindView(R.id.qrcode)
        ImageView qrcode;
        @BindView(R.id.iv_choose)
        ImageView iv_choose;

        public YaoQingFriendHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
