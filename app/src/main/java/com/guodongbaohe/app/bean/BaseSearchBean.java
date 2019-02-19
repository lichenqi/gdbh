package com.guodongbaohe.app.bean;

public class BaseSearchBean {

    private String pics_url;
    private String goods_name;
    private String old_price;
    private String sale_price;
    private String sale_num;
    private String good_id;
    private String ratio;
    private boolean isLogin;
    private String son_count;

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public String getSon_count() {
        return son_count;
    }

    public void setSon_count(String son_count) {
        this.son_count = son_count;
    }

    public String getMember_role() {
        return member_role;
    }

    public void setMember_role(String member_role) {
        this.member_role = member_role;
    }

    private String member_role;

    public String getPics_url() {
        return pics_url;
    }

    public void setPics_url(String pics_url) {
        this.pics_url = pics_url;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getOld_price() {
        return old_price;
    }

    public void setOld_price(String old_price) {
        this.old_price = old_price;
    }

    public String getSale_price() {
        return sale_price;
    }

    public void setSale_price(String sale_price) {
        this.sale_price = sale_price;
    }

    public String getSale_num() {
        return sale_num;
    }

    public void setSale_num(String sale_num) {
        this.sale_num = sale_num;
    }

    public String getGood_id() {
        return good_id;
    }

    public void setGood_id(String good_id) {
        this.good_id = good_id;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

}
