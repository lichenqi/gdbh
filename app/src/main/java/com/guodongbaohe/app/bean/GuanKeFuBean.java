package com.guodongbaohe.app.bean;

public class GuanKeFuBean {


    /**
     * status : 0
     * result : {"wechat":"guodongzhushou","title":"升级领取教程文案标题","sub":"升级领取教程文案标题"}
     */

    private int status;
    private ResultBean result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * wechat : guodongzhushou
         * title : 升级领取教程文案标题
         * sub : 升级领取教程文案标题
         */

        private String wechat;
        private String title;
        private String sub;

        public String getWechat() {
            return wechat;
        }

        public void setWechat(String wechat) {
            this.wechat = wechat;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSub() {
            return sub;
        }

        public void setSub(String sub) {
            this.sub = sub;
        }
    }
}
