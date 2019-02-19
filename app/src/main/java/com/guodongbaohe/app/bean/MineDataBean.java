package com.guodongbaohe.app.bean;

public class MineDataBean {
    public MineData getResult() {
        return result;
    }

    public void setResult(MineData result) {
        this.result = result;
    }

    private MineData result;


    public class MineData {
        private String month;
        private String today;
        private String total;

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getToday() {
            return today;
        }

        public void setToday(String today) {
            this.today = today;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }
    }
}
