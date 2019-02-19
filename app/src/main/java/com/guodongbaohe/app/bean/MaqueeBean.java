package com.guodongbaohe.app.bean;

import java.util.List;

public class MaqueeBean {
    public List<MaqueeData> getResult() {
        return result;
    }

    public void setResult(List<MaqueeData> result) {
        this.result = result;
    }

    private List<MaqueeData> result;

    public class MaqueeData {
        private String image;
        private String sort;
        private String title;

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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        private String url;
    }
}
