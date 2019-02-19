package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.CommonBean;
import com.guodongbaohe.app.util.NetImageLoadUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OtherCommonHeadAdapter extends RecyclerView.Adapter<OtherCommonHeadAdapter.MyHoolder> {
    private Context context;
    private List<CommonBean.CommonSecond> list;
    private OnItemClick onItemClick;

    public void setonclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public OtherCommonHeadAdapter(List<CommonBean.CommonSecond> list) {
        this.list = list;
    }

    @Override
    public MyHoolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.othercommonheadadapter, parent, false);
        return new MyHoolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHoolder holder, int position) {
        holder.title.setText(list.get(position).getName());
        NetImageLoadUtil.loadImage(list.get(position).getThumb(), context, holder.iv);
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
        return list == null ? 0 : list.size();
    }

    public class MyHoolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        ImageView iv;
        @BindView(R.id.title)
        TextView title;

        public MyHoolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
