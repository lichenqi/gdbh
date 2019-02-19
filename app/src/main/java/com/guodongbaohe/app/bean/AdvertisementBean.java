package com.guodongbaohe.app.bean;

import java.util.List;

public class AdvertisementBean {
    public List<AdvertisementData> getResult() {
        return result;
    }

    public void setResult(List<AdvertisementData> result) {
        this.result = result;
    }

    private List<AdvertisementData> result;

    public class AdvertisementData {

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        private String image;
    }
}
