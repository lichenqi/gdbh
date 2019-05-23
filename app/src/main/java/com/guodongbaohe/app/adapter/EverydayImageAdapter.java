package com.guodongbaohe.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guodongbaohe.app.R;
import com.guodongbaohe.app.activity.PicsLookActivity;
import com.guodongbaohe.app.activity.ShopDetailActivity;
import com.guodongbaohe.app.bean.ShopBasicBean;
import com.guodongbaohe.app.common_constant.Constant;
import com.guodongbaohe.app.common_constant.MyApplication;
import com.guodongbaohe.app.myokhttputils.response.JsonResponseHandler;
import com.guodongbaohe.app.util.DensityUtils;
import com.guodongbaohe.app.util.GsonUtil;
import com.guodongbaohe.app.util.NetImageLoadUtil;
import com.guodongbaohe.app.util.ParamUtil;
import com.guodongbaohe.app.util.PreferUtils;
import com.guodongbaohe.app.util.ToastUtils;
import com.guodongbaohe.app.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/*每日爆款组图*/
public class EverydayImageAdapter extends RecyclerView.Adapter<EverydayImageAdapter.EverydayImageHolder> {
    private Context context;
    private List<String> list_imgs;
    private FragmentActivity activity;
    private String status;
    private String goods_id_list;
    private String attr_price_list;
    private DisplayMetrics displayMetrics;
    private int width;
    Intent intent;
    String[] good_id;

    public EverydayImageAdapter(List<String> list_imgs, Context context, FragmentActivity activity, String status, String goods_id_list, String attr_price_list) {
        this.list_imgs = list_imgs;
        this.context = context;
        this.activity = activity;
        this.attr_price_list = attr_price_list;
        this.status = status;
        this.goods_id_list = goods_id_list;
        displayMetrics = context.getResources().getDisplayMetrics();
        int dip2px = DensityUtils.dip2px( this.context, 95 );
        width = (displayMetrics.widthPixels - dip2px) / 3;
    }

    @NonNull
    @Override
    public EverydayImageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from( context ).inflate( R.layout.everydayimageholder, viewGroup, false );
        return new EverydayImageHolder( view );
    }

    @Override
    public void onBindViewHolder(@NonNull EverydayImageHolder holder, int position) {
        ViewGroup.LayoutParams layoutParams = holder.iv.getLayoutParams();
        ViewGroup.LayoutParams layoutParams1 = holder.v_go.getLayoutParams();
        ViewGroup.LayoutParams layoutParams_tv = holder.tv_price.getLayoutParams();
        ViewGroup.LayoutParams layoutParams_pa = holder.re_parent.getLayoutParams();
        layoutParams1.width = width;
        layoutParams1.height = width;
        layoutParams.width = width;
        layoutParams.height = width;
        layoutParams_tv.width = width * 2 / 3;
        layoutParams_pa.width = width;
        holder.iv.setLayoutParams( layoutParams );
        holder.v_go.setLayoutParams( layoutParams1 );
        holder.tv_price.setLayoutParams( layoutParams_tv );
        holder.re_parent.setLayoutParams( layoutParams_pa );
        NetImageLoadUtil.loadImage( list_imgs.get( position ), context, holder.iv );
        /*商品图片判断*/
        if (TextUtils.isEmpty( goods_id_list )) {/*普通商品*/
            holder.tv_price.setVisibility( View.GONE );
            /*已抢光判断*/
            if (!TextUtils.isEmpty( status )) {
                if (Double.valueOf( status ) > 0) {
                    holder.v_go.setVisibility( View.GONE );
                } else {
                    holder.v_go.setVisibility( View.VISIBLE );
                }
            } else {
                holder.v_go.setVisibility( View.VISIBLE );
            }
        } else {/*专场商品*/
            holder.tv_price.setVisibility( View.VISIBLE );
            holder.v_go.setVisibility( View.GONE );
            if (attr_price_list.contains( "||" )) {
                String[] price = attr_price_list.replace( "||", "," ).split( "," );
                if (price[position].equals( "0" )) {
                    holder.tv_price.setVisibility( View.GONE );
                } else {
                    holder.tv_price.setVisibility( View.VISIBLE );
                }
                holder.tv_price.setText( "¥ " + price[position] );
            }
        }

        /*商品id分开*/
        if (goods_id_list.contains( "||" )) {
            good_id = goods_id_list.replace( "||", "," ).split( "," );
        }

        /*点击图片跳转*/
        holder.iv.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty( goods_id_list )) {
                    intent = new Intent( context, PicsLookActivity.class );
                    intent.putStringArrayListExtra( "split", (ArrayList<String>) list_imgs );
                    intent.putExtra( "position", position );
                    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                    context.startActivity( intent );
                    activity.overridePendingTransition( R.anim.fade_in, R.anim.fade_out );
                } else {
                    if (TextUtils.isEmpty( status ) || status.equals( "0" )) {
                        ToastUtils.showToast( context, "该商品抢光呢!" );
                        return;
                    }
                    if (good_id[position].equals( "0" )) {
                        return;
                    }
                    /*请求商品头部信息*/
                    getShopBasicData( good_id[position] );
                }
            }
        } );

    }

    @Override
    public int getItemCount() {
        return list_imgs == null ? 0 : list_imgs.size();
    }

    public class EverydayImageHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.re_parent)
        RelativeLayout re_parent;
        @BindView(R.id.iv)
        ImageView iv;
        @BindView(R.id.v_go)
        ImageView v_go;
        @BindView(R.id.tv_price)
        TextView tv_price;

        public EverydayImageHolder(@NonNull View itemView) {
            super( itemView );
            ButterKnife.bind( this, itemView );
        }
    }

    /*商品详情头部信息*/
    private void getShopBasicData(String id) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put( "goods_id", id );
        String param = ParamUtil.getMapParam( map );
        MyApplication.getInstance().getMyOkHttp().post().url( Constant.BASE_URL + Constant.SHOP_HEAD_BASIC + "?" + param )
                .tag( this )
                .addHeader( "x-appid", Constant.APPID )
                .addHeader( "x-devid", PreferUtils.getString( context, Constant.PESUDOUNIQUEID ) )
                .addHeader( "x-nettype", PreferUtils.getString( context, Constant.NETWORKTYPE ) )
                .addHeader( "x-agent", VersionUtil.getVersionCode( context ) )
                .addHeader( "x-platform", Constant.ANDROID )
                .addHeader( "x-devtype", Constant.IMEI )
                .addHeader( "x-token", ParamUtil.GroupMap( context, "" ) )
                .enqueue( new JsonResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, JSONObject response) {
                        super.onSuccess( statusCode, response );
                        try {
                            JSONObject jsonObject = new JSONObject( response.toString() );
                            if (jsonObject.getInt( "status" ) >= 0) {
                                ShopBasicBean bean = GsonUtil.GsonToBean( response.toString(), ShopBasicBean.class );
                                if (bean == null) return;
                                ShopBasicBean.ShopBasicData result = bean.getResult();
                                Intent intent = new Intent( context, ShopDetailActivity.class );
                                intent.putExtra( "goods_id", result.getGoods_id() );
                                intent.putExtra( "cate_route", result.getCate_route() );/*类目名称*/
                                intent.putExtra( "cate_category", result.getCate_category() );/*类目id*/
                                intent.putExtra( "attr_price", result.getAttr_price() );
                                intent.putExtra( "attr_prime", result.getAttr_prime() );
                                intent.putExtra( "attr_ratio", result.getAttr_ratio() );
                                intent.putExtra( "sales_month", result.getSales_month() );
                                intent.putExtra( "goods_name", result.getGoods_name() );/*长标题*/
                                intent.putExtra( "goods_short", result.getGoods_short() );/*短标题*/
                                intent.putExtra( "seller_shop", result.getSeller_shop() );/*店铺姓名*/
                                intent.putExtra( "goods_thumb", result.getGoods_thumb() );/*单图*/
                                intent.putExtra( "goods_gallery", result.getGoods_gallery() );/*多图*/
                                intent.putExtra( "coupon_begin", result.getCoupon_begin() );/*开始时间*/
                                intent.putExtra( "coupon_final", result.getCoupon_final() );/*结束时间*/
                                intent.putExtra( "coupon_surplus", result.getCoupon_surplus() );/*是否有券*/
                                intent.putExtra( "coupon_explain", result.getGoods_slogan() );/*推荐理由*/
                                intent.putExtra( "attr_site", result.getAttr_site() );/*天猫或者淘宝*/
                                intent.putExtra( "coupon_total", result.getCoupon_total() );
                                intent.putExtra( "coupon_id", result.getCoupon_id() );
                                intent.putExtra( Constant.SHOP_REFERER, "circle" );/*商品来源*/
                                intent.putExtra( Constant.GAOYONGJIN_SOURCE, result.getSource() );/*高佣金来源*/
                                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                context.startActivity( intent );
                                activity.overridePendingTransition( R.anim.fade_in, R.anim.fade_out );
                            } else {
                                String result = jsonObject.getString( "result" );
                                ToastUtils.showToast( context, result );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, String error_msg) {
                        ToastUtils.showToast( context, Constant.NONET );
                    }
                } );
    }

}
