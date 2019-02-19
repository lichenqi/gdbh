package com.guodongbaohe.app.bean;

import java.util.List;

public class HotBean {
    public List<HotBeanData> getResult() {
        return result;
    }

    public void setResult(List<HotBeanData> result) {
        this.result = result;
    }

    private List<HotBeanData> result;

    public class HotBeanData {
        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        private String word;
    }
}
