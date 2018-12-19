package com.guodongbaohe.app.bean;

public class NoticeBean {
    public NoticeData getResult() {
        return result;
    }

    public void setResult(NoticeData result) {
        this.result = result;
    }

    private NoticeData result;

    public class NoticeData {
        private String id;
        private String title;
        private String url;
        private String validity;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getValidity() {
            return validity;
        }

        public void setValidity(String validity) {
            this.validity = validity;
        }
    }
}
