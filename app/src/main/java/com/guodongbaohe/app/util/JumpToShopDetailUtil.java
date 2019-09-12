package com.guodongbaohe.app.util;

import android.content.Context;
import android.content.Intent;

import com.guodongbaohe.app.activity.ShopDetailActivity;
import com.guodongbaohe.app.bean.AllNetBean;
import com.guodongbaohe.app.bean.HomeListBean;
import com.guodongbaohe.app.bean.MadRushListBean;
import com.guodongbaohe.app.bean.RouteBean;
import com.guodongbaohe.app.bean.ShopBasicBean;
import com.guodongbaohe.app.common_constant.Constant;

public class JumpToShopDetailUtil {

    /*独立商品详情信息跳转*/
    public static void start2ActivityOfHeadBean(Context context, ShopBasicBean.ShopBasicData result) {
        Intent intent = new Intent( context, ShopDetailActivity.class );
        intent.putExtra( "goods_id", result.getGoods_id() );/*商品id*/
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
        intent.putExtra( Constant.SHOP_REFERER, "local" );/*商品来源*/
        intent.putExtra( Constant.GAOYONGJIN_SOURCE, result.getSource() );/*高佣金来源*/
        intent.putExtra( "seller_id", result.getSeller_id() );/*店铺id*/
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity( intent );
    }

    /*商品列表跳转到商品详情*/
    public static void start2ShopDetailOfListBean(Context context, HomeListBean.ListData result) {
        Intent intent = new Intent( context, ShopDetailActivity.class );
        intent.putExtra( "goods_id", result.getGoods_id() );
        intent.putExtra( "cate_route", result.getCate_route() );/*类目名称*/
        intent.putExtra( "cate_category", result.getCate_category() );
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
        intent.putExtra( "coupon_id", result.getCoupon_id() );/*优惠券id*/
        intent.putExtra( Constant.SHOP_REFERER, "local" );/*商品来源*/
        intent.putExtra( Constant.GAOYONGJIN_SOURCE, result.getSource() );/*高佣金来源*/
        intent.putExtra( "seller_id", result.getSeller_id() );/*店铺id*/
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity( intent );
    }

    public static void startToDetailOfSearch(Context context, AllNetBean.AllNetData result) {
        Intent intent = new Intent( context, ShopDetailActivity.class );
        intent.putExtra( "goods_id", result.getGoods_id() );
        intent.putExtra( "cate_route", result.getCate_route() );/*类目名称*/
        intent.putExtra( "cate_category", result.getCate_category() );
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
        intent.putExtra( Constant.SHOP_REFERER, "search" );/*商品来源*/
        intent.putExtra( Constant.GAOYONGJIN_SOURCE, result.getSource() );/*高佣金来源*/
        intent.putExtra( "seller_id", result.getSeller_id() );/*店铺id*/
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity( intent );
    }

    public static void startToDetailOfLike(Context context, RouteBean.RouteData result) {
        Intent intent = new Intent( context, ShopDetailActivity.class );
        intent.putExtra( "goods_id", result.getGoods_id() );
        intent.putExtra( "cate_route", result.getCate_route() );/*类目名称*/
        intent.putExtra( "cate_category", result.getCate_category() );
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
        intent.putExtra( Constant.SHOP_REFERER, "search" );/*商品来源*/
        intent.putExtra( Constant.GAOYONGJIN_SOURCE, result.getSource() );/*高佣金来源*/
        intent.putExtra( "seller_id", result.getSeller_id() );/*店铺id*/
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity( intent );
    }

    public static void startToDetailOfMadRushList(Context context, MadRushListBean.MadListData result) {
        Intent intent = new Intent( context, ShopDetailActivity.class );
        intent.putExtra( "goods_id", result.getGoods_id() );
        intent.putExtra( "cate_route", result.getCate_route() );/*类目名称*/
        intent.putExtra( "cate_category", result.getCate_category() );
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
        intent.putExtra( Constant.SHOP_REFERER, "search" );/*商品来源*/
        intent.putExtra( Constant.GAOYONGJIN_SOURCE, result.getSource() );/*高佣金来源*/
        intent.putExtra( "seller_id", result.getSeller_id() );/*店铺id*/
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        context.startActivity( intent );
    }

}
