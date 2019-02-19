package com.guodongbaohe.app.bean;

import java.util.List;

public class BuyUserBean {
    private List<BuyUser> result;

    public List<BuyUser> getResult() {
        return result;
    }

    public void setResult(List<BuyUser> result) {
        this.result = result;
    }

    public class BuyUser {
        private String avatar;
        private String order_time;
        private String member_id;
        private String member_name;
        private String money;

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getOrder_time() {
            return order_time;
        }

        public void setOrder_time(String order_time) {
            this.order_time = order_time;
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
    }
}
