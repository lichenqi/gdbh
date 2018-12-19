package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RelevanceAdapter extends RecyclerView.Adapter<RelevanceAdapter.RelevanceHolder> {
    private Context context;
    private List<List<String>> list;
    private OnItemClick onItemClick;

    public void setonclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public RelevanceAdapter(Context context, List<List<String>> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public RelevanceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.relevanceadapter, parent, false);
        return new RelevanceHolder(view);
    }

    @Override
    public void onBindViewHolder(final RelevanceHolder holder, int position) {
        holder.tv.setText(list.get(position).get(0));
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

    public class RelevanceHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv)
        TextView tv;

        public RelevanceHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
