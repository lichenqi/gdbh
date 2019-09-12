package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.OnItemClick;
import com.guodongbaohe.app.R;
import com.guodongbaohe.app.bean.MadRushListBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.util.IconAndTextGroupUtil;
import com.guodongbaohe.app.util.NetImageLoadUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.StringCleanZeroUtil;
import com.guodongbaohe.app.util.TextViewUtil;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MadRushListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<MadRushListBean.MadListData> list;
    private int item_normal = 1;/*正常布局*/
    private int item_foot = 2;/*加载更多脚布局*/
    private int state;
    BigDecimal bg3;
    private String member_role, tax_rate, attr_price, attr_ratio;
    double app_v;
    private OnItemClick onItemClick;

    public void setOnClickListener(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void changeState(int state) {
        this.state = state;
        notifyDataSetChanged();
    }

    public MadRushListAdapter(Context context, List<MadRushListBean.MadListData> list, String member_role) {
        this.context = context;
        this.list = list;
        this.member_role = member_role;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == item_normal) {
            view = LayoutInflater.from( context ).inflate( R.layout.madrushlistadapter, parent, false );
            return new MyHolder( view );
        } else if (viewType == item_foot) {
            view = LayoutInflater.from( context ).inflate( R.layout.load_more_foot_view, parent, false );
            return new FootHolder( view );
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyHolder) {
            tax_rate = PreferUtils.getString( context, "tax_rate" );/*配置比例*/
            app_v = 1 - Double.valueOf( tax_rate );
            NetImageLoadUtil.loadImage( list.get( position ).getGoods_thumb(), context, ((MyHolder) holder).iv );
            String sales_hours = "近2小时爆卖" + list.get( position ).getSales_hours() + "件";
            TextViewUtil.setTextViewColorAndSize( sales_hours, ((MyHolder) holder).tv_sale_num );
            IconAndTextGroupUtil.setTextView( context, ((MyHolder) holder).tv_title, list.get( position ).getGoods_name(), list.get( position ).getAttr_site() );
            String coupon_surplus = list.get( position ).getCoupon_surplus();
            String attr_prime = list.get( position ).getAttr_prime();
            attr_price = list.get( position ).getAttr_price();
            attr_ratio = list.get( position ).getAttr_ratio();
            StringCleanZeroUtil.StringFormat( attr_price, ((MyHolder) holder).tv_price );
            if (Double.valueOf( coupon_surplus ) > 0) {
                /*有券显示*/
                double d_price = Double.valueOf( attr_prime ) - Double.valueOf( attr_price );
                bg3 = new BigDecimal( d_price );
                double d_money = bg3.setScale( 2, BigDecimal.ROUND_HALF_UP ).doubleValue();
                ((MyHolder) holder).tv_coupon_money.setText( StringCleanZeroUtil.DoubleFormat( d_money ) + " 元券" );
                TvClassicType( (MyHolder) holder, "券后" );
            } else {
                /*无券显示 显示折扣或者抢购价*/
                double d_price = Double.valueOf( attr_prime ) - Double.valueOf( attr_price );
                if (d_price > 0) {
                    double disaccount = Double.valueOf( attr_price ) / Double.valueOf( attr_prime ) * 10;
                    bg3 = new BigDecimal( disaccount );
                    double d_zhe = bg3.setScale( 1, BigDecimal.ROUND_HALF_UP ).doubleValue();
                    ((MyHolder) holder).tv_coupon_money.setText( d_zhe + " 折" );
                    TvClassicType( (MyHolder) holder, "折后" );
                } else {
                    ((MyHolder) holder).tv_coupon_money.setText( "立即抢购" );
                    TvClassicType( (MyHolder) holder, "特惠价" );
                }
            }
            if (Constant.BOSS_USER_LEVEL.contains( member_role )) {
                /*总裁比例*/
                setDataBiLi( 90, ((MyHolder) holder).tv_share_money );
            } else if (Constant.PARTNER_USER_LEVEL.contains( member_role )) {
                /*合伙人比例*/
                setDataBiLi( 80, ((MyHolder) holder).tv_share_money );
            } else {
                /*VIP比例*/
                setDataBiLi( 55, ((MyHolder) holder).tv_share_money );
            }
            switch (position) {
                case 0:
                    ((MyHolder) holder).iv_level.setVisibility( View.VISIBLE );
                    ((MyHolder) holder).re_other.setVisibility( View.GONE );
                    ((MyHolder) holder).iv_level.setImageResource( R.mipmap.top_one );
                    break;
                case 1:
                    ((MyHolder) holder).iv_level.setVisibility( View.VISIBLE );
                    ((MyHolder) holder).re_other.setVisibility( View.GONE );
                    ((MyHolder) holder).iv_level.setImageResource( R.mipmap.top_two );
                    break;
                case 2:
                    ((MyHolder) holder).iv_level.setVisibility( View.VISIBLE );
                    ((MyHolder) holder).re_other.setVisibility( View.GONE );
                    ((MyHolder) holder).iv_level.setImageResource( R.mipmap.top_three );
                    break;
                case 3:
                    ((MyHolder) holder).iv_level.setVisibility( View.VISIBLE );
                    ((MyHolder) holder).re_other.setVisibility( View.GONE );
                    ((MyHolder) holder).iv_level.setImageResource( R.mipmap.top_four );
                    break;
                default:
                    if (position <= 100) {
                        ((MyHolder) holder).iv_level.setVisibility( View.GONE );
                        ((MyHolder) holder).re_other.setVisibility( View.VISIBLE );
                        ((MyHolder) holder).tv_other.setText( String.valueOf( position ) );
                    } else {
                        ((MyHolder) holder).iv_level.setVisibility( View.GONE );
                        ((MyHolder) holder).re_other.setVisibility( View.GONE );
                    }
                    break;
            }
            if (onItemClick != null) {
                holder.itemView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClick.OnItemClickListener( ((MyHolder) holder).itemView, holder.getAdapterPosition() );
                    }
                } );
            }
        } else if (holder instanceof FootHolder) {
            switch (state) {
                case 1:/*还有数据*/
                    ((FootHolder) holder).ll_parent.setVisibility( View.VISIBLE );
                    ((FootHolder) holder).tv_foot.setVisibility( View.GONE );
                    break;
                case -1:/*没有更多数据了*/
                    ((FootHolder) holder).ll_parent.setVisibility( View.GONE );
                    ((FootHolder) holder).tv_foot.setVisibility( View.VISIBLE );
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return item_foot;
        } else {
            return item_normal;
        }
    }

    /*券后  折后  特惠价显示*/
    private void TvClassicType(MyHolder holder, String content) {
        holder.tv_classic_type.setText( content );
    }

    private void setDataBiLi(int num, TextView tv_share_money) {
        double result = Double.valueOf( attr_price ) * Double.valueOf( attr_ratio ) * app_v * num / 10000;
        BigDecimal bg3 = new BigDecimal( result );
        double money = bg3.setScale( 2, BigDecimal.ROUND_HALF_UP ).doubleValue();
        tv_share_money.setText( "分享赚 ¥ " + money );
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_level)
        ImageView iv_level;
        @BindView(R.id.re_other)
        RelativeLayout re_other;
        @BindView(R.id.tv_other)
        TextView tv_other;
        @BindView(R.id.iv)
        RoundedImageView iv;
        @BindView(R.id.tv_sale_num)
        TextView tv_sale_num;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_classic_type)
        TextView tv_classic_type;
        @BindView(R.id.tv_price)
        TextView tv_price;
        @BindView(R.id.tv_coupon_money)
        TextView tv_coupon_money;
        @BindView(R.id.tv_share_money)
        TextView tv_share_money;

        public MyHolder(@NonNull View itemView) {
            super( itemView );
            ButterKnife.bind( this, itemView );
        }
    }

    /*加载底部holder*/
    public class FootHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_parent)
        LinearLayout ll_parent;
        @BindView(R.id.progressBar)
        ProgressBar progressBar;
        @BindView(R.id.tv_foot)
        TextView tv_foot;

        public FootHolder(View itemView) {
            super( itemView );
            ButterKnife.bind( this, itemView );
        }
    }

}
