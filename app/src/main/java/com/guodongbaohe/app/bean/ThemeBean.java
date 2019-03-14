package com.guodongbaohe.app.bean;

import java.util.List;

public class ThemeBean {
    private List<ThemeData> result;

    public List<ThemeData> getResult() {
        return result;
    }

    public void setResult(List<ThemeData> result) {
        this.result = result;
    }


    public class ThemeData {
        private String extend;
        private String image;
        private String sort;
        private String title;
        private String type;
        private String url;

        public String getExtend() {
            return extend;
        }

        public void setExtend(String extend) {
            this.extend = extend;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
