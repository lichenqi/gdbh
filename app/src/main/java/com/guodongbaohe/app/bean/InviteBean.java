package com.guodongbaohe.app.bean;

public class InviteBean {
    private InviteCode result;

    public InviteCode getResult() {
        return result;
    }

    public void setResult(InviteCode result) {
        this.result = result;
    }

    public class InviteCode {
        private String invite_code;

        public String getInvite_code() {
            return invite_code;
        }

        public void setInvite_code(String invite_code) {
            this.invite_code = invite_code;
        }

    }
}
