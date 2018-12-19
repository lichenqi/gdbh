package com.guodongbaohe.app.bean;

public class MyWalletBean {
    public MyWalletData getResult() {
        return result;
    }

    public void setResult(MyWalletData result) {
        this.result = result;
    }

    private MyWalletData result;

    public class MyWalletData {
        private String alipay;
        private String bank_branch;
        private String bank_card;
        private String bank_name;
        private String realname;

        public String getAlipay() {
            return alipay;
        }

        public void setAlipay(String alipay) {
            this.alipay = alipay;
        }

        public String getBank_branch() {
            return bank_branch;
        }

        public void setBank_branch(String bank_branch) {
            this.bank_branch = bank_branch;
        }

        public String getBank_card() {
            return bank_card;
        }

        public void setBank_card(String bank_card) {
            this.bank_card = bank_card;
        }

        public String getBank_name() {
            return bank_name;
        }

        public void setBank_name(String bank_name) {
            this.bank_name = bank_name;
        }

        public String getRealname() {
            return realname;
        }

        public void setRealname(String realname) {
            this.realname = realname;
        }
    }
}
