package com.guodongbaohe.app.bean;

public class MakeMoneyBean {
    private MakeMoneyData result;

    public MakeMoneyData getResult() {
        return result;
    }

    public void setResult(MakeMoneyData result) {
        this.result = result;
    }


    public class MakeMoneyData {
        private String month_boss;
        private String month_partner;
        private String month_vip;
        private String today_boss;
        private String today_partner;
        private String today_vip;
        private String total_income;

        public String getMonth_boss() {
            return month_boss;
        }

        public void setMonth_boss(String month_boss) {
            this.month_boss = month_boss;
        }

        public String getMonth_partner() {
            return month_partner;
        }

        public void setMonth_partner(String month_partner) {
            this.month_partner = month_partner;
        }

        public String getMonth_vip() {
            return month_vip;
        }

        public void setMonth_vip(String month_vip) {
            this.month_vip = month_vip;
        }

        public String getToday_boss() {
            return today_boss;
        }

        public void setToday_boss(String today_boss) {
            this.today_boss = today_boss;
        }

        public String getToday_partner() {
            return today_partner;
        }

        public void setToday_partner(String today_partner) {
            this.today_partner = today_partner;
        }

        public String getToday_vip() {
            return today_vip;
        }

        public void setToday_vip(String today_vip) {
            this.today_vip = today_vip;
        }

        public String getTotal_income() {
            return total_income;
        }

        public void setTotal_income(String total_income) {
            this.total_income = total_income;
        }

    }


}
