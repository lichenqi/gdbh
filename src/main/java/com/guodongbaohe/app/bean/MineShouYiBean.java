package com.guodongbaohe.app.bean;

public class MineShouYiBean {

    public MineShouYiBeanData getResult() {
        return result;
    }

    public void setResult(MineShouYiBeanData result) {
        this.result = result;
    }

    private MineShouYiBeanData result;


    public class MineShouYiBeanData {
        private MonthData month;
        private ProfitData profit;
        private YesterdayData yesterday;
        private LastMonth last_month;

        public LastMonth getLast_month() {
            return last_month;
        }

        public void setLast_month(LastMonth last_month) {
            this.last_month = last_month;
        }

        public MonthData getMonth() {
            return month;
        }

        public void setMonth(MonthData month) {
            this.month = month;
        }

        public ProfitData getProfit() {
            return profit;
        }

        public void setProfit(ProfitData profit) {
            this.profit = profit;
        }

        public YesterdayData getYesterday() {
            return yesterday;
        }

        public void setYesterday(YesterdayData yesterday) {
            this.yesterday = yesterday;
        }
    }

    public class MonthData {
        private String credit;
        private String deduct_money;
        private String income;

        public String getCredit() {
            return credit;
        }

        public void setCredit(String credit) {
            this.credit = credit;
        }

        public String getDeduct_money() {
            return deduct_money;
        }

        public void setDeduct_money(String deduct_money) {
            this.deduct_money = deduct_money;
        }

        public String getIncome() {
            return income;
        }

        public void setIncome(String income) {
            this.income = income;
        }
    }

    public class ProfitData {
        private String already;
        private String balance;
        private String total;

        public String getAlready() {
            return already;
        }

        public void setAlready(String already) {
            this.already = already;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getUnsettled() {
            return unsettled;
        }

        public void setUnsettled(String unsettled) {
            this.unsettled = unsettled;
        }

        private String unsettled;
    }

    public class YesterdayData {
        private String credit;
        private String deduct_money;

        public String getCredit() {
            return credit;
        }

        public void setCredit(String credit) {
            this.credit = credit;
        }

        public String getDeduct_money() {
            return deduct_money;
        }

        public void setDeduct_money(String deduct_money) {
            this.deduct_money = deduct_money;
        }

        public String getIncome() {
            return income;
        }

        public void setIncome(String income) {
            this.income = income;
        }

        private String income;
    }

    public class LastMonth {
        private String credit;
        private String income;
        private String settle_money;

        public String getCredit() {
            return credit;
        }

        public void setCredit(String credit) {
            this.credit = credit;
        }

        public String getIncome() {
            return income;
        }

        public void setIncome(String income) {
            this.income = income;
        }

        public String getSettle_money() {
            return settle_money;
        }

        public void setSettle_money(String settle_money) {
            this.settle_money = settle_money;
        }
    }
}
