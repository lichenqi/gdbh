package com.guodongbaohe.app.bean;
/**
 * s手机令牌
 * */
public class TokenBean {

    /**
     * status : 0
     * result : {"code":"7E412D","exp_time":1550732238}
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
         * code : 7E412D
         * exp_time : 1550732238
         */

        private String code;
        private int exp_time;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getExp_time() {
            return exp_time;
        }

        public void setExp_time(int exp_time) {
            this.exp_time = exp_time;
        }
    }
}
