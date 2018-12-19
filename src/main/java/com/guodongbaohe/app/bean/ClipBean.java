package com.guodongbaohe.app.bean;

import org.litepal.crud.LitePalSupport;

public class ClipBean extends LitePalSupport {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;
}
