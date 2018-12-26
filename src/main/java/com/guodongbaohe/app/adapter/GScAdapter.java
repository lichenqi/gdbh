package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.AllNetBean;
import com.guodongbaohe.app.bean.GCollectionBean;
import com.guodongbaohe.app.util.NetImageLoadUtil;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GScAdapter extends RecyclerView.Adapter<GScAdapter.ViewHolder>{
    private static final int MYLIVE_MODE_CHECK = 0;
    int mEditMode = MYLIVE_MODE_CHECK;
    String id;
    private int secret = 0;
    private String title = "";
    private Context context;
    private List<GCollectionBean.ResultBean> mMyLiveList;
    private OnItemClickListener mOnItemClickListener;

    public GScAdapter(Context context) {
        this.context = context;
    }


    public void notifyAdapter(List<GCollectionBean.ResultBean> myLiveList, boolean isAdd) {
        if (!isAdd) {
            this.mMyLiveList = myLiveList;
        } else {
            this.mMyLiveList.addAll(myLiveList);
        }
        notifyDataSetChanged();
    }

    public List<GCollectionBean.ResultBean> getMyLiveList() {
        if (mMyLiveList == null) {
            mMyLiveList = new ArrayList<>();
        }
        return mMyLiveList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gcollection_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public int getItemCount() {
        return mMyLiveList.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final GCollectionBean.ResultBean myLive = mMyLiveList.get(holder.getAdapterPosition());
        NetImageLoadUtil.loadImage(myLive.getGoods_thumb(), context, holder.iv);
         id=myLive.getGoods_id();
        holder.title.setText(myLive.getGoods_name());
        holder.tv_price.setText(myLive.getAttr_price());
//        holder.dianpu_name.setText(myLive.getSeller_shop());
        holder.tv_sale_num.setText(myLive.getSales_month());
//        holder.ninengzhuan.setText();
        if (mEditMode == MYLIVE_MODE_CHECK) {
            holder.ck_chose.setVisibility(View.GONE);
        } else {
            holder.ck_chose.setVisibility(View.VISIBLE);

            if (myLive.isIschoosed()) {

                holder.ck_chose.setImageResource(R.mipmap.xainshiyjin);
            } else {
                holder.ck_chose.setImageResource(R.mipmap.buxianshiyjin);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClickListener(holder.getAdapterPosition(), mMyLiveList);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
    public interface OnItemClickListener {
        void onItemClickListener(int pos,List<GCollectionBean.ResultBean> myLiveList);
    }
    public void setEditMode(int editMode) {
        mEditMode = editMode;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        RoundedImageView iv;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.dianpu_name)
        TextView dianpu_name;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.tv_sale_num)
        TextView tv_sale_num;
        @BindView(R.id.ninengzhuan)
        TextView ninengzhuan;
        @BindView(R.id.sjizhuan)
        TextView sjizhuan;
        @BindView(R.id.ck_chose)
        ImageView ck_chose;



        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
