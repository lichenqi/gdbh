package com.guodongbaohe.app.bean;

import java.util.List;

public class FiveMoKuaiBean {
    public List<FiveMoKuaiData> getResult() {
        return result;
    }

    public void setResult(List<FiveMoKuaiData> result) {
        this.result = result;
    }

    private List<FiveMoKuaiData> result;

    public class FiveMoKuaiData {
        private String extend;
        private String image;
        private String sort;
        private String title;
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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }
}
