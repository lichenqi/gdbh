package com.guodongbaohe.app.bean;

import java.util.List;

public class OrderH5Bean {
    public List<OrderH5Data> getResult() {
        return result;
    }

    public void setResult(List<OrderH5Data> result) {
        this.result = result;
    }

    private List<OrderH5Data> result;

    public class OrderH5Data {
        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        private String content;

    }
}
