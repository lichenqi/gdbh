package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.QRCodePicsBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeQRCodeAdapter extends RecyclerView.Adapter<ChangeQRCodeAdapter.ChangeQRCodeHolder> {
    private Context context;
    private List<QRCodePicsBean> pics;
    private OnItemClick onItemClick;

    public void setonclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public ChangeQRCodeAdapter(Context context, List<QRCodePicsBean> pics) {
        this.context = context;
        this.pics = pics;
    }


    @Override
    public ChangeQRCodeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.changeqrcodeadapter, parent, false);
        return new ChangeQRCodeHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChangeQRCodeHolder holder, int position) {
        Glide.with(context).load(pics.get(position).getUrl()).placeholder(R.drawable.loading_img).into(holder.iv_zhu);
        if (pics.get(position).isIswhich()) {
            holder.iv_change.setImageResource(R.mipmap.xainshiyjin);
        } else {
            holder.iv_change.setImageResource(R.mipmap.buxianshiyjin);
        }
        if (onItemClick != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.OnItemClickListener(holder.itemView, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return pics == null ? 0 : pics.size();
    }

    public class ChangeQRCodeHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_zhu)
        ImageView iv_zhu;
        @BindView(R.id.iv_change)
        ImageView iv_change;

        public ChangeQRCodeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
