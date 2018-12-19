package com.guodongbaohe.app.fragment;

public class UserDataBean {
    public UserData getResult() {
        return result;
    }

    public void setResult(UserData result) {
        this.result = result;
    }

    private UserData result;

    public class UserData {
        private String count;
        private String income;
        private String invite_code;
        private String member_role;

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getIncome() {
            return income;
        }

        public void setIncome(String income) {
            this.income = income;
        }

        public String getInvite_code() {
            return invite_code;
        }

        public void setInvite_code(String invite_code) {
            this.invite_code = invite_code;
        }

        public String getMember_role() {
            return member_role;
        }

        public void setMember_role(String member_role) {
            this.member_role = member_role;
        }
    }
}
