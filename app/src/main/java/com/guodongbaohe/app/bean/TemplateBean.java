package com.guodongbaohe.app.bean;

public class TemplateBean {
    public TemplateData getResult() {
        return result;
    }

    public void setResult(TemplateData result) {
        this.result = result;
    }

    private TemplateData result;

    public class TemplateData {
        private String comment;
        private String content;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

    }
}
