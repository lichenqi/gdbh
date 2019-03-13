package com.guodongbaohe.app.bean;

import java.io.Serializable;

public class ChooseImagsNum implements Serializable {
    private String url;
    private boolean isChecked;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
