package com.guodongbaohe.app.bean;

public class PinZheBean {

    public PinZheData getResult() {
        return result;
    }

    public void setResult(PinZheData result) {
        this.result = result;
    }

    private PinZheData result;

    public class PinZheData {
        public String getPromote_id() {
            return promote_id;
        }

        public void setPromote_id(String promote_id) {
            this.promote_id = promote_id;
        }

        private String promote_id;
    }
}
