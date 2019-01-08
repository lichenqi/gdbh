package com.guodongbaohe.app.bean;

public class UmessageBean {

    /**
     * msg_id : uu481201399440513912
     * display_type : notification
     * alias :
     * random_min : 0
     * body : {"title":"测试自定义参数","ticker":"测试自定义参数","text":"无","after_open":"go_app","url":"","activity":"","custom":"","play_vibrate":"true","play_sound":"true","play_lights":"true"}
     * extra : {"key1":"value1","key2":"value2"}
     */

    private String msg_id;
    private String display_type;
    private String alias;
    private int random_min;
    private BodyBean body;
    private ExtraBean extra;

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getDisplay_type() {
        return display_type;
    }

    public void setDisplay_type(String display_type) {
        this.display_type = display_type;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getRandom_min() {
        return random_min;
    }

    public void setRandom_min(int random_min) {
        this.random_min = random_min;
    }

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public ExtraBean getExtra() {
        return extra;
    }

    public void setExtra(ExtraBean extra) {
        this.extra = extra;
    }

    public static class BodyBean {
        /**
         * title : 测试自定义参数
         * ticker : 测试自定义参数
         * text : 无
         * after_open : go_app
         * url :
         * activity :
         * custom :
         * play_vibrate : true
         * play_sound : true
         * play_lights : true
         */

        private String title;
        private String ticker;
        private String text;
        private String after_open;
        private String url;
        private String activity;
        private String custom;
        private String play_vibrate;
        private String play_sound;
        private String play_lights;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTicker() {
            return ticker;
        }

        public void setTicker(String ticker) {
            this.ticker = ticker;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getAfter_open() {
            return after_open;
        }

        public void setAfter_open(String after_open) {
            this.after_open = after_open;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getActivity() {
            return activity;
        }

        public void setActivity(String activity) {
            this.activity = activity;
        }

        public String getCustom() {
            return custom;
        }

        public void setCustom(String custom) {
            this.custom = custom;
        }

        public String getPlay_vibrate() {
            return play_vibrate;
        }

        public void setPlay_vibrate(String play_vibrate) {
            this.play_vibrate = play_vibrate;
        }

        public String getPlay_sound() {
            return play_sound;
        }

        public void setPlay_sound(String play_sound) {
            this.play_sound = play_sound;
        }

        public String getPlay_lights() {
            return play_lights;
        }

        public void setPlay_lights(String play_lights) {
            this.play_lights = play_lights;
        }
    }

    public static class ExtraBean {
        /**
         * key1 : value1
         * key2 : value2
         */

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
