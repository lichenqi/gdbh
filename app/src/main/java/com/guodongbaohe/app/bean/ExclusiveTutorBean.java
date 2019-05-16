package com.guodongbaohe.app.bean;

public class ExclusiveTutorBean {
    public MyResult getResult() {
        return result;
    }

    public void setResult(MyResult result) {
        this.result = result;
    }

    private MyResult result;

    public class MyResult {
        public String getWechat() {
            return wechat;
        }

        public void setWechat(String wechat) {
            this.wechat = wechat;
        }

        private String wechat;
    }
}
