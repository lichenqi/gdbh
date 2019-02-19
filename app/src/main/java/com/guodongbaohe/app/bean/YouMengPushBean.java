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
        public String getTarget() {
            return target;
        }

        public void setTarget(String target) {
            this.target = target;
        }

        private String target;
    }
}
