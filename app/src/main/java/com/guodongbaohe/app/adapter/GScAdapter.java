package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.GCollectionBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.util.IconAndTextGroupUtil;
import com.guodongbaohe.app.util.NetImageLoadUtil;
import com.guodongbaohe.app.util.NumUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.StringCleanZeroUtil;
import com.guodongbaohe.app.util.TextViewUtil;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GScAdapter extends RecyclerView.Adapter<GScAdapter.ViewHolder> {
    private static final int MYLIVE_MODE_CHECK = 0;
    int mEditMode = MYLIVE_MODE_CHECK;
    String id;
    private int secret = 0;
    private String title = "";
    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private List<GCollectionBean.ResultBean> list;

    public GScAdapter(Context context, List<GCollectionBean.ResultBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gcollection_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    BigDecimal bg3;
    String tax_rate, attr_price, attr_prime, attr_ratio;
    double app_v;

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String member_role = PreferUtils.getString(context, "member_role");
        String son_count = PreferUtils.getString(context, "son_count");
        final GCollectionBean.ResultBean myLive = list.get(position);
        tax_rate = PreferUtils.getString(context, "tax_rate");/*配置比例*/
        app_v = 1 - Double.valueOf(tax_rate);
        NetImageLoadUtil.loadImage(myLive.getGoods_thumb(), context, holder.iv);
        id = myLive.getGoods_id();
        attr_price = myLive.getAttr_price();
        attr_prime = myLive.getAttr_prime();
        attr_ratio = myLive.getAttr_ratio();
        String sales_month = myLive.getSales_month();
        holder.title.setText(myLive.getGoods_name());
        holder.tv_price.setText(myLive.getAttr_price());
        IconAndTextGroupUtil.setTextView(context, holder.dianpu_name, myLive.getSeller_shop(), myLive.getAttr_site());
        StringCleanZeroUtil.StringFormat(myLive.getAttr_price(), holder.tv_price);
        String coupon_surplus = myLive.getCoupon_surplus();
        double d_price = Double.valueOf(myLive.getAttr_prime()) - Double.valueOf(myLive.getAttr_price());
        bg3 = new BigDecimal(d_price);
        double d_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (Double.valueOf(coupon_surplus) > 0) {
            holder.sjizhuan.setText(StringCleanZeroUtil.DoubleFormat(d_money) + "元券");
            holder.tv_classic_type.setText("券后");
        } else {
            if (d_price > 0) {
                double disaccount = Double.valueOf(myLive.getAttr_price()) / Double.valueOf(myLive.getAttr_prime()) * 10;
                bg3 = new BigDecimal(disaccount);
                double d_zhe = bg3.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
                holder.sjizhuan.setText(d_zhe + "折");
                holder.tv_classic_type.setText("折后");
            } else {
                holder.sjizhuan.setText("立即抢购");
                holder.tv_classic_type.setText("特惠价");
            }
        }

        if (Constant.BOSS_USER_LEVEL.contains(member_role)) {
            /*总裁用户*/
            YouMakeMoney(holder, 90);
            holder.tv_sale_num.setText("月销" + NumUtil.getNum(sales_month));
            holder.tv_sale_num.getPaint().setFlags(1);
        } else if (Constant.PARTNER_USER_LEVEL.contains(member_role)) {
            /*合伙人用户*/
            YouMakeMoney(holder, 80);
            holder.tv_sale_num.setText("月销" + NumUtil.getNum(sales_month));
            holder.tv_sale_num.getPaint().setFlags(1);
        }
//        else if (Constant.VIP_USER_LEVEL.contains(member_role)) {
//            /*合伙人用户*/
//            YouMakeMoney(holder, 55);
//            holder.tv_sale_num.setText("月销" + NumUtil.getNum(sales_month));
//            holder.tv_sale_num.getPaint().setFlags(1);
//        }
        else {
            /*普通用户*/
            YouMakeMoney(holder, 55);
            holder.tv_sale_num.setText("月销" + NumUtil.getNum(sales_month));
            holder.tv_sale_num.getPaint().setFlags(1);
//            holder.ninengzhuan.setText("月销" + NumUtil.getNum(sales_month));
//            holder.tv_sale_num.setText("¥" + attr_prime);
//            holder.tv_sale_num.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        }

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
                mOnItemClickListener.onItemClickListener(holder.getAdapterPosition() - 1, list);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClickListener(int pos, List<GCollectionBean.ResultBean> myLiveList);
    }

    public void setEditMode(int editMode) {
        mEditMode = editMode;
        notifyDataSetChanged();
    }

    /*你能赚显示*/
    private void YouMakeMoney(ViewHolder holder, int num) {
        double sj_result = Double.valueOf(attr_price) * Double.valueOf(attr_ratio) * num / 10000 * app_v;
        bg3 = new BigDecimal(sj_result);
        double sj_money = bg3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        String ninengzhuan_money = "你能赚¥" + sj_money;
        TextViewUtil.setTextViewSize(ninengzhuan_money, holder.ninengzhuan);
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
        @BindView(R.id.tv_classic_type)
        TextView tv_classic_type;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
