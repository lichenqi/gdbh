package com.guodongbaohe.app.bean;

public class BeiAnBean {
    private BeiAnData result;
    private String special;
    private String status;

    public BeiAnData getResult() {
        return result;
    }

    public void setResult(BeiAnData result) {
        this.result = result;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public  class BeiAnData {

        private String note;
        private String url;

        public String getScript() {
            return script;
        }

        public void setScript(String script) {
            this.script = script;
        }

        private String script;

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }
}
