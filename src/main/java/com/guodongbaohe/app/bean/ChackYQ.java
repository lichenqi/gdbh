package com.guodongbaohe.app.bean;

public class ChackYQ {

    /**
     * status : 0
     * result : {"invite_code":"919929","member_name":"李晨奇","avatar":"https://assets.mopland.com/image/2018/1224/5c209b4e97cab.jpg"}
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
         * invite_code : 919929
         * member_name : 李晨奇
         * avatar : https://assets.mopland.com/image/2018/1224/5c209b4e97cab.jpg
         */

        private String invite_code;
        private String member_name;
        private String avatar;

        public String getInvite_code() {
            return invite_code;
        }

        public void setInvite_code(String invite_code) {
            this.invite_code = invite_code;
        }

        public String getMember_name() {
            return member_name;
        }

        public void setMember_name(String member_name) {
            this.member_name = member_name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
