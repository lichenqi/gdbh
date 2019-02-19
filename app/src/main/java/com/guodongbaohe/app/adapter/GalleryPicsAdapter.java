package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GalleryPicsAdapter extends RecyclerView.Adapter<GalleryPicsAdapter.GalleryPicsHolder> {
    private Context context;
    private List<String> pics_list;
    private OnItemClick onItemClick;

    public void setonclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public GalleryPicsAdapter(Context context, List<String> pics_list) {
        this.pics_list = pics_list;
        this.context = context;
    }

    @Override
    public GalleryPicsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gallerypicsadapter, parent, false);
        return new GalleryPicsHolder(view);
    }

    @Override
    public void onBindViewHolder(final GalleryPicsHolder holder, int position) {
        Glide.with(context).load(pics_list.get(position)).placeholder(R.drawable.loading_img).into(holder.iv);
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
        return pics_list == null ? 0 : pics_list.size();
    }

    public class GalleryPicsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        ImageView iv;

        public GalleryPicsHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
