package com.guodongbaohe.app.bean;

import java.io.Serializable;
import java.util.List;

public class InviteAwardBean {
    public List<InviteAwardData> getResult() {
        return result;
    }

    public void setResult(List<InviteAwardData> result) {
        this.result = result;
    }

    private List<InviteAwardData> result;

    public class InviteAwardData implements Serializable {
        private String image;
        private String sort;
        private String title;
        private boolean isChoose;

        public boolean isChoose() {
            return isChoose;
        }

        public void setChoose(boolean choose) {
            isChoose = choose;
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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        private String url;
    }
}
