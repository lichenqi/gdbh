package com.guodongbaohe.app.util;

import android.content.Context;

import com.guodongbaohe.app.common_constant.Constant;

import java.util.LinkedHashMap;

public class WebViewUtil {

    public static LinkedHashMap getWebViewHead(Context context) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("x-userid", PreferUtils.getString(context, "member_id"));
        map.put("x-appid", Constant.APPID);
        map.put("x-devid", PreferUtils.getString(context, Constant.PESUDOUNIQUEID));
        map.put("x-nettype", PreferUtils.getString(context, Constant.NETWORKTYPE));
        map.put("x-agent", VersionUtil.getH5VersionCode(context));
        map.put("x-platform", Constant.ANDROID);
        map.put("x-devtype", Constant.IMEI);
        map.put("x-token", ParamUtil.GroupMap(context, PreferUtils.getString(context, "member_id")));
        return map;
    }
}
