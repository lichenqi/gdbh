package com.guodongbaohe.app.bean;

public class WchatInviteCodePeopleBean {
    public WchatBean getResult() {
        return result;
    }

    public void setResult(WchatBean result) {
        this.result = result;
    }

    private WchatBean result;

    public class WchatBean {
        private String avatar;
        private String invite_code;
        private String member_id;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
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

        private String member_name;
    }

}
