package com.guodongbaohe.app.bean;

public class GaoYongJinBean {
    public GaoYongJinData getResult() {
        return result;
    }

    public void setResult(GaoYongJinData result) {
        this.result = result;
    }

    private GaoYongJinData result;

    public class GaoYongJinData {
        private String category_id;
        private String coupon_click_url;
        private String coupon_end_time;
        private String coupon_info;
        private String coupon_remain_count;
        private String coupon_start_time;
        private String coupon_total_count;
        private String coupon_type;
        private String item_id;
        private String item_url;
        private String max_commission_rate;

        public String getCategory_id() {
            return category_id;
        }

        public void setCategory_id(String category_id) {
            this.category_id = category_id;
        }

        public String getCoupon_click_url() {
            return coupon_click_url;
        }

        public void setCoupon_click_url(String coupon_click_url) {
            this.coupon_click_url = coupon_click_url;
        }

        public String getCoupon_end_time() {
            return coupon_end_time;
        }

        public void setCoupon_end_time(String coupon_end_time) {
            this.coupon_end_time = coupon_end_time;
        }

        public String getCoupon_info() {
            return coupon_info;
        }

        public void setCoupon_info(String coupon_info) {
            this.coupon_info = coupon_info;
        }

        public String getCoupon_remain_count() {
            return coupon_remain_count;
        }

        public void setCoupon_remain_count(String coupon_remain_count) {
            this.coupon_remain_count = coupon_remain_count;
        }

        public String getCoupon_start_time() {
            return coupon_start_time;
        }

        public void setCoupon_start_time(String coupon_start_time) {
            this.coupon_start_time = coupon_start_time;
        }

        public String getCoupon_total_count() {
            return coupon_total_count;
        }

        public void setCoupon_total_count(String coupon_total_count) {
            this.coupon_total_count = coupon_total_count;
        }

        public String getCoupon_type() {
            return coupon_type;
        }

        public void setCoupon_type(String coupon_type) {
            this.coupon_type = coupon_type;
        }

        public String getItem_id() {
            return item_id;
        }

        public void setItem_id(String item_id) {
            this.item_id = item_id;
        }

        public String getItem_url() {
            return item_url;
        }

        public void setItem_url(String item_url) {
            this.item_url = item_url;
        }

        public String getMax_commission_rate() {
            return max_commission_rate;
        }

        public void setMax_commission_rate(String max_commission_rate) {
            this.max_commission_rate = max_commission_rate;
        }
    }
}
