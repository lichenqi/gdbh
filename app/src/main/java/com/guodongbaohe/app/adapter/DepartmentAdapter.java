package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.DepartmentBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.view.CircleImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.DepartmentHolder> {
    private Context context;
    private List<DepartmentBean.DepartmentData> list;
    private String userName;
    private OnItemClick onItemClick;

    public void setonclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public DepartmentAdapter(Context context, List<DepartmentBean.DepartmentData> list, String userName) {
        this.context = context;
        this.list = list;
        this.userName = userName;
    }

    @Override
    public DepartmentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.departmentadapter, parent, false);
        return new DepartmentHolder(view);
    }

    @Override
    public void onBindViewHolder(final DepartmentHolder holder, int position) {
        String avatar = list.get(position).getAvatar();
        if (TextUtils.isEmpty(avatar)) {
            holder.circleimageview.setImageResource(R.mipmap.user_moren_logo);
        } else {
            Glide.with(context).load(list.get(position).getAvatar()).into(holder.circleimageview);
        }
        holder.name.setText(list.get(position).getMember_name());
        String member_role = list.get(position).getMember_role();
        String counts = list.get(position).getFans();
        if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
            holder.level.setText("总裁");
        } else if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
            holder.level.setText("合伙人");
        } else if (Constant.VIP_USER_LEVEL.contains(member_role)) {
            holder.level.setText("VIP");
        } else {
            holder.level.setText("普通会员");
        }
        holder.num.setText("邀请人数" + counts + "人");
        holder.yaoqingren.setText("邀请人: " + list.get(position).getParent_name());
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
        return list == null ? 0 : list.size();
    }

    public class DepartmentHolder extends RecyclerView.ViewHolder {
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


        public DepartmentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
