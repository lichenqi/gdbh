package com.guodongbaohe.app.util;

import android.text.TextUtils;
import android.widget.TextView;

public class ShopTitleSetUtil {

    public static void setShopTitle(String source, String goods_name, String goods_short, TextView textView) {

        if (TextUtils.isEmpty( source )) {
            /*不是本地库 用goods_name*/
            if (TextUtils.isEmpty( goods_name )) {
                textView.setText( goods_short );
                textView.setMaxLines( 2 );
            } else {
                textView.setText( goods_name );
                textView.setMaxLines( 1 );
            }
        } else {
            /*本地商品 用goods_short*/
            if (TextUtils.isEmpty( goods_short )) {
                textView.setText( goods_name );
                textView.setMaxLines( 1 );
            } else {
                textView.setText( goods_short );
                textView.setMaxLines( 2 );
            }
        }
    }
}
