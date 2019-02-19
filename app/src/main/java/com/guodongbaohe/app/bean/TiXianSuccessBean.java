package com.guodongbaohe.app.bean;

public class TiXianSuccessBean {
    public ResultData getResult() {
        return result;
    }

    public void setResult(ResultData result) {
        this.result = result;
    }

    private ResultData result;

    public class ResultData {
        public String getTip() {
            return tip;
        }

        public void setTip(String tip) {
            this.tip = tip;
        }

        private String tip;
    }
}
