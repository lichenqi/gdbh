package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.HomeHorizontalListBean;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeHorizontalAdapter extends RecyclerView.Adapter<HomeHorizontalAdapter.HomeHorizontalHolder> {

    private Context context;
    private List<HomeHorizontalListBean> horizontalList;

    public HomeHorizontalAdapter(Context context, List<HomeHorizontalListBean> horizontalList) {
        this.context = context;
        this.horizontalList = horizontalList;
    }

    @Override
    public HomeHorizontalHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.homehorizontalholder, parent, false);
        return new HomeHorizontalHolder(view);
    }

    @Override
    public void onBindViewHolder(HomeHorizontalHolder holder, int position) {
        Glide.with(context).load(horizontalList.get(position).getImage()).into(holder.iv);
    }

    @Override
    public int getItemCount() {
        return horizontalList == null ? 0 : horizontalList.size();
    }

    public class HomeHorizontalHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        RoundedImageView iv;

        public HomeHorizontalHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
