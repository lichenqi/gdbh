package com.guodongbaohe.app.bean;

import org.litepal.crud.LitePalSupport;

public class ClipBean extends LitePalSupport {
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
