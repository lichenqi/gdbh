package com.guodongbaohe.app.bean;

public class PaiHangBangBean {
    public PaiHangBangData getResult() {
        return result;
    }

    public void setResult(PaiHangBangData result) {
        this.result = result;
    }

    private PaiHangBangData result;

    public class PaiHangBangData {
        private String balance_rank;
        private String credits_rank;
        private String fans_rank;
        private String income_rank;
        private String team_income;
        private String team_income_rank;

        public String getTeam_income() {
            return team_income;
        }

        public void setTeam_income(String team_income) {
            this.team_income = team_income;
        }

        public String getTeam_income_rank() {
            return team_income_rank;
        }

        public void setTeam_income_rank(String team_income_rank) {
            this.team_income_rank = team_income_rank;
        }

        public String getBalance_rank() {
            return balance_rank;
        }

        public void setBalance_rank(String balance_rank) {
            this.balance_rank = balance_rank;
        }

        public String getCredits_rank() {
            return credits_rank;
        }

        public void setCredits_rank(String credits_rank) {
            this.credits_rank = credits_rank;
        }

        public String getFans_rank() {
            return fans_rank;
        }

        public void setFans_rank(String fans_rank) {
            this.fans_rank = fans_rank;
        }

        public String getIncome_rank() {
            return income_rank;
        }

        public void setIncome_rank(String income_rank) {
            this.income_rank = income_rank;
        }
    }
}
