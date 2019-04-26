package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.HomeListBean;
import com.guodongbaohe.app.util.NetImageLoadUtil;
import com.guodongbaohe.app.util.NumUtil;
import com.guodongbaohe.app.util.StringCleanZeroUtil;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HoursHortAdapter extends RecyclerView.Adapter<HoursHortAdapter.HoursHortHolder> {
    private Context context;
    private List<HomeListBean.ListData> hoursList;
    private OnItemClick onItemClick;

    public void setonclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public HoursHortAdapter(Context context, List<HomeListBean.ListData> hoursList) {
        this.context = context;
        this.hoursList = hoursList;
    }

    @Override
    public HoursHortHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hourshortadapter, parent, false);
        return new HoursHortHolder(view);
    }

    @Override
    public void onBindViewHolder(final HoursHortHolder holder, int position) {
        NetImageLoadUtil.loadImage(hoursList.get(position).getGoods_thumb(), context, holder.iv);
        String goods_short = hoursList.get(position).getGoods_short();/*短标题*/
        String goods_name = hoursList.get(position).getGoods_name();/*长标题*/
        if (!TextUtils.isEmpty(goods_short)) {
            holder.title.setText(goods_short);
            holder.title.setMaxLines(1);
        } else {
            holder.title.setText(goods_name);
            holder.title.setMaxLines(1);
        }
        String attr_prime = hoursList.get(position).getAttr_prime();/*原价*/
        String attr_price = hoursList.get(position).getAttr_price();/*券后价*/
        StringCleanZeroUtil.StringFormatWithYuan(attr_price, holder.sale_price);
        holder.old_price.setText("¥" + attr_prime);
        holder.old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        holder.num.getBackground().setAlpha(180);
        holder.num.setText("月销" + NumUtil.getNum(hoursList.get(position).getSales_month()));
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
        return hoursList == null ? 0 : hoursList.size();
    }

    public class HoursHortHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv)
        RoundedImageView iv;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.sale_price)
        TextView sale_price;
        @BindView(R.id.old_price)
        TextView old_price;
        @BindView(R.id.num)
        TextView num;

        public HoursHortHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
