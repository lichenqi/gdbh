package com.guodongbaohe.app.bean;

public class NewUserLevelData {

    private NewUserLevel result;

    public NewUserLevel getResult() {
        return result;
    }

    public void setResult(NewUserLevel result) {
        this.result = result;
    }

    public class NewUserLevel {
        private ChallengeOne challenge_one;
        private ChallengeTwo challenge_two;
        private ModeOne mode_one;
        private ModeTwo mode_two;
        private String avatar;
        private String income;
        private String layer;
        private String levelOne;
        private String levelTwo;
        private String levelMore;
        private String member_name;
        private String member_role;
        private String direct_partners;

        public String getLevelMore() {
            return levelMore;
        }

        public void setLevelMore(String levelMore) {
            this.levelMore = levelMore;
        }

        public String getDirect_partners() {
            return direct_partners;
        }

        public void setDirect_partners(String direct_partners) {
            this.direct_partners = direct_partners;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getIncome() {
            return income;
        }

        public void setIncome(String income) {
            this.income = income;
        }

        public String getLayer() {
            return layer;
        }

        public void setLayer(String layer) {
            this.layer = layer;
        }

        public String getLevelOne() {
            return levelOne;
        }

        public void setLevelOne(String levelOne) {
            this.levelOne = levelOne;
        }

        public String getLevelTwo() {
            return levelTwo;
        }

        public void setLevelTwo(String levelTwo) {
            this.levelTwo = levelTwo;
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

        public ChallengeOne getChallenge_one() {
            return challenge_one;
        }

        public void setChallenge_one(ChallengeOne challenge_one) {
            this.challenge_one = challenge_one;
        }

        public ChallengeTwo getChallenge_two() {
            return challenge_two;
        }

        public void setChallenge_two(ChallengeTwo challenge_two) {
            this.challenge_two = challenge_two;
        }

        public ModeOne getMode_one() {
            return mode_one;
        }

        public void setMode_one(ModeOne mode_one) {
            this.mode_one = mode_one;
        }

        public ModeTwo getMode_two() {
            return mode_two;
        }

        public void setMode_two(ModeTwo mode_two) {
            this.mode_two = mode_two;
        }

    }

    public class ChallengeOne {
        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        private String target;
    }

    public class ChallengeTwo {
        public Section getSection() {
            return section;
        }

        public void setSection(Section section) {
            this.section = section;
        }

        private Section section;
    }

    public class ModeOne {

        private String levelOne;
        private String income;

        public String getIncome() {
            return income;
        }

        public void setIncome(String income) {
            this.income = income;
        }

        public String getLevelOne() {
            return levelOne;
        }

        public void setLevelOne(String levelOne) {
            this.levelOne = levelOne;
        }

    }

    public class ModeTwo {

        private String income;
        private String levelOne;
        private String levelTwo;

        public String getIncome() {
            return income;
        }

        public void setIncome(String income) {
            this.income = income;
        }

        public String getLevelOne() {
            return levelOne;
        }

        public void setLevelOne(String levelOne) {
            this.levelOne = levelOne;
        }

        public String getLevelTwo() {
            return levelTwo;
        }

        public void setLevelTwo(String levelTwo) {
            this.levelTwo = levelTwo;
        }

    }

    public class Section {

        private String end;
        private String start;

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

    }
}
