package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.HotBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HotSearchAdapter extends RecyclerView.Adapter<HotSearchAdapter.HotSearchHolder> {
    private Context context;
    private List<HotBean.HotBeanData> list;
    private OnItemClick onItemClick;

    public void setonclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public HotSearchAdapter(Context context, List<HotBean.HotBeanData> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public HotSearchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.serach_item, parent, false);
        return new HotSearchHolder(view);
    }

    @Override
    public void onBindViewHolder(final HotSearchHolder holder, int position) {
        holder.title.setTextColor(0xff585858);
        holder.title.setText(list.get(position).getWord());
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

    public class HotSearchHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;

        public HotSearchHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
