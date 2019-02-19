package com.guodongbaohe.app.bean;

import java.util.List;

public class NewBannerBean {

    /**
     * status : 0
     * result : [{"title":"fsdg","sort":"0","image":"https://assets.mopland.com/image/2019/0121/5c457ee174c77.png","url":"","extend":""},{"title":"sdfsf","sort":"0","image":"https://assets.mopland.com/image/2019/0121/5c457f172799a.png","url":"","extend":""},{"title":"asdasd","sort":"0","image":"https://assets.mopland.com/image/2019/0121/5c457f21296a9.png","url":"","extend":""},{"title":"sgfsdg","sort":"0","image":"https://assets.mopland.com/image/2019/0121/5c457f2a9970f.png","url":"","extend":""},{"title":"asdfgsdg","sort":"0","image":"https://assets.mopland.com/image/2019/0121/5c457f37d4bde.png","url":"","extend":""},{"title":"sadfsadf","sort":"0","image":"https://assets.mopland.com/image/2019/0121/5c457f4285572.png","url":"","extend":""},{"title":"asfsadf","sort":"0","image":"https://assets.mopland.com/image/2019/0121/5c457f6998d8b.png","url":"","extend":""},{"title":"asfsadf","sort":"0","image":"https://assets.mopland.com/image/2019/0121/5c457f75cf85f.png","url":"","extend":""},{"title":"sadfsadfs","sort":"0","image":"https://assets.mopland.com/image/2019/0121/5c457f8182953.png","url":"","extend":""},{"title":"asdfasd","sort":"0","image":"https://assets.mopland.com/image/2019/0121/5c457f8b978e9.png","url":"","extend":""},{"title":"asdfsadf","sort":"0","image":"https://assets.mopland.com/image/2019/0121/5c457fa18a9f5.png","url":"","extend":""},{"title":"afadf","sort":"0","image":"https://assets.mopland.com/image/2019/0121/5c457faac2afb.png","url":"","extend":""}]
     */

    private int status;
    private List<ResultBean> result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * title : fsdg
         * sort : 0
         * image : https://assets.mopland.com/image/2019/0121/5c457ee174c77.png
         * url :
         * extend :
         */

        private String title;
        private String sort;
        private String image;
        private String url;
        private String extend;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getExtend() {
            return extend;
        }

        public void setExtend(String extend) {
            this.extend = extend;
        }
    }
}
