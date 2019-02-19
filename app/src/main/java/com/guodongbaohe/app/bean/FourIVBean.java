package com.guodongbaohe.app.bean;

import java.util.List;

public class FourIVBean {
    public List<FourIVData> getResult() {
        return result;
    }

    public void setResult(List<FourIVData> result) {
        this.result = result;
    }

    private List<FourIVData> result;

    public class FourIVData {
        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        private String image;
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
