package com.guodongbaohe.app.bean;

import org.litepal.crud.LitePalSupport;

public class LocalCollectBean extends LitePalSupport {
    private String collectshop;

    public String getCollectshop() {
        return collectshop;
    }

    public void setCollectshop(String collectshop) {
        this.collectshop = collectshop;
    }
}
