package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.CommonBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeChoiceAdapter extends RecyclerView.Adapter<HomeChoiceAdapter.HomeChoiceHolder> {
    private Context context;
    private List<CommonBean.CommonResult> titleList;
    private OnItemClick onItemClick;

    public void setOnClickListener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public HomeChoiceAdapter(Context context, List<CommonBean.CommonResult> titleList) {
        this.context = context;
        this.titleList = titleList;
    }

    @Override
    public HomeChoiceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_choice_item, parent, false);
        return new HomeChoiceHolder(view);
    }

    @Override
    public void onBindViewHolder(final HomeChoiceHolder holder, int position) {
        holder.title.setText(titleList.get(position).getName());
        Glide.with(context).load("https://assets.mopland.com/image/2018/1127/5bfcfa0796e1e.png").into(holder.iv);
        if (onItemClick != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.OnItemClickListener(holder.itemView, holder.getAdapterPosition());
                }
            });
        }
        if (titleList.get(position).isChoose()) {
            holder.title.setTextColor(0xffEFA818);
        } else {
            holder.title.setTextColor(0xff1a1a1a);
        }
    }

    @Override
    public int getItemCount() {
        return titleList == null ? 0 : titleList.size();
    }

    public class HomeChoiceHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.iv)
        ImageView iv;

        public HomeChoiceHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
