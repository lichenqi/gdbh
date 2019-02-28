package com.guodongbaohe.app.bean;

public class VersionBean {
    public VersionData getResult() {
        return result;
    }

    public void setResult(VersionData result) {
        this.result = result;
    }

    private VersionData result;

    public class VersionData {
        private String desc;
        private String download;
        private String log;
        private String title;
        private String version;
        private String is_update;

        public String getIs_update() {
            return is_update;
        }

        public void setIs_update(String is_update) {
            this.is_update = is_update;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getDownload() {
            return download;
        }

        public void setDownload(String download) {
            this.download = download;
        }

        public String getLog() {
            return log;
        }

        public void setLog(String log) {
            this.log = log;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
