package com.guodongbaohe.app.bean;

public class YouMengPushBean {

    public PushExtraData getExtra() {
        return extra;
    }

    public void setExtra(PushExtraData extra) {
        this.extra = extra;
    }

    private PushExtraData extra;

    public class PushExtraData {
        private String target;
        private String content;

        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

    }
}
