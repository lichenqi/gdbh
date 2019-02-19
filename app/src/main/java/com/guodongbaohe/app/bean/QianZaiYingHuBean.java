package com.guodongbaohe.app.bean;

import java.util.List;

public class QianZaiYingHuBean {


    public List<QianZaiYingData> getResult() {
        return result;
    }

    public void setResult(List<QianZaiYingData> result) {
        this.result = result;
    }

    private List<QianZaiYingData> result;

    public class QianZaiYingData {
        private String avatar;
        private String invitee_id;
        private String member_id;
        private String method;
        private String nickname;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getInvitee_id() {
            return invitee_id;
        }

        public void setInvitee_id(String invitee_id) {
            this.invitee_id = invitee_id;
        }

        public String getMember_id() {
            return member_id;
        }

        public void setMember_id(String member_id) {
            this.member_id = member_id;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }
}
