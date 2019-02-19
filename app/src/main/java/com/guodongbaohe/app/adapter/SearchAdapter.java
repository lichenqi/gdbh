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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SeachHolder> {
    private Context context;
    private List<String> list;
    private OnItemClick onItemClick;

    public void setonclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public SearchAdapter(List<String> list) {
        this.list = list;
    }

    @Override
    public SeachHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.serach_item, parent, false);
        return new SeachHolder(view);
    }

    @Override
    public void onBindViewHolder(final SeachHolder holder, int position) {
        holder.title.setText(list.get(position));
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
        return list == null ? 0 : (list.size() > 30 ? 30 : list.size());
    }

    public class SeachHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title)
        TextView title;

        public SeachHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
