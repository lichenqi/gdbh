package com.guodongbaohe.app.bean;

public class ExclusiveTutorBean {

    public MyResult getResult() {
        return result;
    }

    public void setResult(MyResult result) {
        this.result = result;
    }

    public MyResult result;

    public class MyResult {

        private String wechat;
        private String avatar;

        public String getWechat() {
            return wechat;
        }

        public void setWechat(String wechat) {
            this.wechat = wechat;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }


    }
}
