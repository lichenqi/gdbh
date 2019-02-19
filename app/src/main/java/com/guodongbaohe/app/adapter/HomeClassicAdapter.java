package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.FiveMoKuaiBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeClassicAdapter extends RecyclerView.Adapter<HomeClassicAdapter.HomeClassicHolder> {
    private Context context;
    private OnItemClick onItemClick;
    private List<FiveMoKuaiBean.FiveMoKuaiData> mokuaiList;

    public void setOnClickListener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public HomeClassicAdapter(Context context, List<FiveMoKuaiBean.FiveMoKuaiData> mokuaiList) {
        this.context = context;
        this.mokuaiList = mokuaiList;
    }

    @NonNull
    @Override
    public HomeClassicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeClassicHolder(LayoutInflater.from(context).inflate(R.layout.home_classic_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeClassicHolder holder, int position) {
        Glide.with(context).load(mokuaiList.get(position).getImage()).placeholder(R.drawable.loading_img).into(holder.iv);
        holder.name.setText(mokuaiList.get(position).getTitle());
        if (onItemClick != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.OnItemClickListener(holder.itemView, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mokuaiList == null ? 0 : mokuaiList.size();
    }

    public class HomeClassicHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        ImageView iv;
        @BindView(R.id.name)
        TextView name;


        public HomeClassicHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
