package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.QianZaiYingHuBean;
import com.guodongbaohe.app.view.CircleImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QianZaiAdapter extends RecyclerView.Adapter<QianZaiAdapter.QianZaiHolder> {
    private String userName;
    private Context context;
    private List<QianZaiYingHuBean.QianZaiYingData> list;

    public QianZaiAdapter(Context context, List<QianZaiYingHuBean.QianZaiYingData> list, String userName) {
        this.context = context;
        this.userName = userName;
        this.list = list;
    }

    @Override
    public QianZaiHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.departmentadapter, parent, false);
        return new QianZaiHolder(view);
    }

    @Override
    public void onBindViewHolder(final QianZaiHolder holder, int position) {
        String avatar = list.get(position).getAvatar();
        String method = list.get(position).getMethod();
        if (TextUtils.isEmpty(avatar)) {
            holder.circleimageview.setImageResource(R.mipmap.user_moren_logo);
        } else {
            Glide.with(context).load(list.get(position).getAvatar()).into(holder.circleimageview);
        }
        holder.name.setText(list.get(position).getNickname());
        holder.level.setText("潜在用户");
        if (method.equals("weixin")) {
            holder.num.setText("来源: " + "微信");
        } else {
            holder.num.setText("来源: " + "其他");
        }
        holder.yaoqingren.setText("邀请人: " + userName);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class QianZaiHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.circleimageview)
        CircleImageView circleimageview;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.level)
        TextView level;
        @BindView(R.id.num)
        TextView num;
        @BindView(R.id.yaoqingren)
        TextView yaoqingren;


        public QianZaiHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
