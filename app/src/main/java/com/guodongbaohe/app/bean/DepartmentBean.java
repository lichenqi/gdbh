package com.guodongbaohe.app.bean;

import java.util.List;

public class DepartmentBean {
    private List<DepartmentData> result;

    public List<DepartmentData> getResult() {
        return result;
    }

    public void setResult(List<DepartmentData> result) {
        this.result = result;
    }


    public class DepartmentData {
        private String avatar;
        private String dateline;
        private String datetime;
        private String fans;
        private String invite_code;
        private String last_month_income;
        private String member_id;
        private String member_name;
        private String member_role;
        private String phone;
        private String total_income;
        private String wechat;
        private String parent_name;

        public String getParent_name() {
            return parent_name;
        }

        public void setParent_name(String parent_name) {
            this.parent_name = parent_name;
        }


        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getDateline() {
            return dateline;
        }

        public void setDateline(String dateline) {
            this.dateline = dateline;
        }

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }

        public String getFans() {
            return fans;
        }

        public void setFans(String fans) {
            this.fans = fans;
        }

        public String getInvite_code() {
            return invite_code;
        }

        public void setInvite_code(String invite_code) {
            this.invite_code = invite_code;
        }

        public String getLast_month_income() {
            return last_month_income;
        }

        public void setLast_month_income(String last_month_income) {
            this.last_month_income = last_month_income;
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

        public String getMember_role() {
            return member_role;
        }

        public void setMember_role(String member_role) {
            this.member_role = member_role;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getTotal_income() {
            return total_income;
        }

        public void setTotal_income(String total_income) {
            this.total_income = total_income;
        }

        public String getWechat() {
            return wechat;
        }

        public void setWechat(String wechat) {
            this.wechat = wechat;
        }
    }
}
