package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.OrderBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.util.NetImageLoadUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderAdaptr extends RecyclerView.Adapter<OrderAdaptr.OrderHolder> {
    private Context context;
    private List<OrderBean.OrderData> list;
    private String member_role, son_count;
    BigDecimal bg3;
    String pub_share_pre_fee;
    double app_v;
    private OnItemClick onItemClick, onShopDetailClick;

    public void setonfuzhiclicklistener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setonshopdetailclicklistener(OnItemClick onShopDetailClick) {
        this.onShopDetailClick = onShopDetailClick;
    }

    public OrderAdaptr(Context context, List<OrderBean.OrderData> list, String member_role, String son_count) {
        this.context = context;
        this.list = list;
        this.member_role = member_role;
        this.son_count = son_count;
    }

    @Override
    public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.orderadaptr, parent, false);
        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderHolder holder, int position) {
        String tax_rate = PreferUtils.getString(context, "tax_rate");/*配置比例*/
        app_v = 1 - Double.valueOf(tax_rate);
        NetImageLoadUtil.loadImage(Constant.BASE_URL + Constant.ORDER_PHOTO + list.get(position).getNum_iid() + "?size=100", context, holder.iv);
        String tk_status = list.get(position).getTk_status();
        String freeze = list.get(position).getFreeze();
        holder.title.setText(list.get(position).getItem_title());
        holder.order_no.setText("订单号:" + list.get(position).getTrade_id());
        holder.order_price.setText("商品付款 ¥" + list.get(position).getAlipay_total_price());
        if (freeze.equals("1")) {
            holder.taonbao.setText("订单已冻结");
            holder.taonbao.setTextColor(0xffff0000);
            holder.end_time.setVisibility(View.GONE);
        } else {
            if (tk_status.equals("13")) {
                holder.taonbao.setText("订单失效");
                holder.taonbao.setTextColor(0xfff40000);
                holder.end_time.setVisibility(View.GONE);
            } else if (tk_status.equals("3")) {
                holder.taonbao.setTextColor(0xff008080);
                holder.taonbao.setText("订单结算");
                holder.end_time.setVisibility(View.VISIBLE);
                holder.end_time.setText("结算时间:" + list.get(position).getEarning_time());
            } else if (tk_status.equals("12")) {
                holder.taonbao.setTextColor(0xff000000);
                holder.taonbao.setText("订单付款");
                holder.end_time.setVisibility(View.GONE);
            } else if (tk_status.equals("14")) {
                holder.taonbao.setTextColor(0xff000000);
                holder.taonbao.setText("订单成功");
                holder.end_time.setVisibility(View.GONE);
            }
        }
        holder.time.setText("下单时间:" + list.get(position).getCreate_time());
        holder.makemoney.setText("你能赚¥" + list.get(position).getMoney());
        if (onItemClick != null) {
            holder.tv_order_no_fuzhi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.OnItemClickListener(holder.tv_order_no_fuzhi, holder.getAdapterPosition());
                }
            });
        }

        if (onShopDetailClick != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onShopDetailClick.OnItemClickListener(holder.itemView, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class OrderHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.taonbao)
        TextView taonbao;
        @BindView(R.id.iv)
        RoundedImageView iv;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.order_no)
        TextView order_no;
        @BindView(R.id.order_price)
        TextView order_price;
        @BindView(R.id.makemoney)
        TextView makemoney;
        @BindView(R.id.tv_order_no_fuzhi)
        TextView tv_order_no_fuzhi;
        @BindView(R.id.end_time)
        TextView end_time;

        public OrderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
