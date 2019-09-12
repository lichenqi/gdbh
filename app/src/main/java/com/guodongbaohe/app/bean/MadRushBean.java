package com.guodongbaohe.app.bean;

import java.util.List;

public class MadRushBean {

    private List<MadRushData> result;

    public List<MadRushData> getResult() {
        return result;
    }

    public void setResult(List<MadRushData> result) {
        this.result = result;
    }

    public class MadRushData {

        private String cate_name;
        private String extra_id;

        public String getCate_name() {
            return cate_name;
        }

        public void setCate_name(String cate_name) {
            this.cate_name = cate_name;
        }

        public String getExtra_id() {
            return extra_id;
        }

        public void setExtra_id(String extra_id) {
            this.extra_id = extra_id;
        }
    }
}
