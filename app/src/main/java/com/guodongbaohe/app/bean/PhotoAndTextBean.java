package com.guodongbaohe.app.bean;

import java.util.List;

public class PhotoAndTextBean {
    private DetailObj result;

    public DetailObj getResult() {
        return result;
    }

    public void setResult(DetailObj result) {
        this.result = result;
    }


    public class DetailObj {

        public List<String> getDetail() {
            return detail;
        }

        public void setDetail(List<String> detail) {
            this.detail = detail;
        }

        private List<String> detail;

    }

}
