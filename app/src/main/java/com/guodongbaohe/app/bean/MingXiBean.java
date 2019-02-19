package com.guodongbaohe.app.bean;

import java.util.List;

public class MingXiBean {
    public List<MingXiData> getResult() {
        return result;
    }

    public void setResult(List<MingXiData> result) {
        this.result = result;
    }

    private List<MingXiData> result;

    public class MingXiData {
        private String avatar;
        private String buyer_id;
        private String create_time;
        private String member_id;
        private String member_name;
        private String money;
        private String total;
        private String credit;
        private String dateline;
        private String tk_status;
        private String earning_time;
        private String collect;

        public String getCollect() {
            return collect;
        }

        public void setCollect(String collect) {
            this.collect = collect;
        }

        public String getEarning_time() {
            return earning_time;
        }

        public void setEarning_time(String earning_time) {
            this.earning_time = earning_time;
        }

        public String getTk_status() {
            return tk_status;
        }

        public void setTk_status(String tk_status) {
            this.tk_status = tk_status;
        }

        public String getCredit() {
            return credit;
        }

        public void setCredit(String credit) {
            this.credit = credit;
        }

        public String getDateline() {
            return dateline;
        }

        public void setDateline(String dateline) {
            this.dateline = dateline;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getBuyer_id() {
            return buyer_id;
        }

        public void setBuyer_id(String buyer_id) {
            this.buyer_id = buyer_id;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getMember_id() {
            return member_id;
        }

        public void setMember_id(String member_id) {
            this.member_id = member_id;
        }

        public String getMember_name() {
            return member_name;
        }

        public void setMember_name(String member_name) {
            this.member_name = member_name;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getTrade_id() {
            return trade_id;
        }

        public void setTrade_id(String trade_id) {
            this.trade_id = trade_id;
        }

        private String trade_id;
    }
}
