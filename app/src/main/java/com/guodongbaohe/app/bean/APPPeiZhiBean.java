package com.guodongbaohe.app.bean;

public class APPPeiZhiBean {
    private APPPeiZhiData result;
    private String status;

    public APPPeiZhiData getResult() {
        return result;
    }

    public void setResult(APPPeiZhiData result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public class APPPeiZhiData {
        private String count;
        private String invite_code;
        private String member_id;
        private String member_name;

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getInvite_code() {
            return invite_code;
        }

        public void setInvite_code(String invite_code) {
            this.invite_code = invite_code;
        }

        public String getMember_id() {
            return member_id;
        }

        public void setMember_id(String member_id) {
            this.member_id = member_id;
        }

        public String getMember_name() {
            return member_name;
        }

        public void setMember_name(String member_name) {
            this.member_name = member_name;
        }

        public String getMember_role() {
            return member_role;
        }

        public void setMember_role(String member_role) {
            this.member_role = member_role;
        }

        private String member_role;
    }
}
