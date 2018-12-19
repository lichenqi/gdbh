package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.SecondHeadBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SecondClassicShowAdapter extends RecyclerView.Adapter<SecondClassicShowAdapter.MyHolder> {
    private Context context;
    private List<SecondHeadBean> headList;
    private OnItemClick onItemClick;

    public void setonclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public SecondClassicShowAdapter(List<SecondHeadBean> headList) {
        this.headList = headList;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.secondclassicshowadapter, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {
        holder.name.setText(headList.get(position).getTitle());
        if (onItemClick != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.OnItemClickListener(holder.itemView, holder.getAdapterPosition());
                }
            });
        }
        if (headList.get(position).isCheck()) {
            holder.name.setTextColor(0xffe03c56);
        } else {
            holder.name.setTextColor(0xff585858);
        }
    }

    @Override
    public int getItemCount() {
        return headList == null ? 0 : headList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name)
        TextView name;

        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
