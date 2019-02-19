package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.NewBannerBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoreNewClassicAdapter extends RecyclerView.Adapter<MoreNewClassicAdapter.MoreNewClassicHolder> {
    private Context context;
    private List<NewBannerBean.ResultBean> big_list;

    public MoreNewClassicAdapter(Context context, List<NewBannerBean.ResultBean> big_list) {
        this.big_list = big_list;
        this.context = context;
    }

    @Override
    public MoreNewClassicHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MoreNewClassicHolder(LayoutInflater.from(context).inflate(R.layout.home_classic_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MoreNewClassicHolder holder, int position) {
        Glide.with(context).load(big_list.get(position).getImage()).placeholder(R.drawable.loading_img).into(holder.iv);
        holder.name.setText(big_list.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return big_list == null ? 0 : big_list.size();
    }

    public class MoreNewClassicHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        ImageView iv;
        @BindView(R.id.name)
        TextView name;

        public MoreNewClassicHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
