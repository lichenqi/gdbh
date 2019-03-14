package com.guodongbaohe.app.bean;

import java.util.List;

public class NewBannerBean {

    /**
     * status : 0
     * result : [{"title":"19.9包邮","sort":"1","image":"https://assets.mopland.com/image/2019/0314/5c89fd514308a.png","url":"jkj","extend":"","type":""},{"title":"疯抢榜","sort":"2","image":"https://assets.mopland.com/image/2019/0314/5c89fe3c75c13.png","url":"fqb","extend":"","type":""},{"title":"聚划算","sort":"3","image":"https://assets.mopland.com/image/2019/0314/5c89fe661f91c.png","url":"jhs","extend":"","type":""},{"title":"淘抢购","sort":"4","image":"https://assets.mopland.com/image/2019/0314/5c89fe89d629b.png","url":"tqg","extend":"","type":""},{"title":"天猫国际","sort":"5","image":"https://assets.mopland.com/image/2019/0124/5c498cffc528e.png","url":"tmgj","extend":"https://jellybox.mopland.com/assets/address/1545633024507","type":"app_theme"},{"title":"购物车","sort":"6","image":"https://assets.mopland.com/image/2019/0311/5c8623a884710.png","url":"gwc","extend":"gwc","type":""},{"title":"天猫超市","sort":"7","image":"https://assets.mopland.com/image/2019/0124/5c498d22bfe00.png","url":"tmall","extend":"https://chaoshi.tmall.com","type":"tmall"},{"title":"9.9抢购","sort":"8","image":"https://assets.mopland.com/image/2019/0311/5c8610d53ffe2.png","url":"9.9","extend":"http://test.tgbaohe.net/cheap","type":"app_theme"},{"title":"升级VIP","sort":"9","image":"https://assets.mopland.com/image/2019/0124/5c498b1b031a0.png","url":"upgrade","extend":"https://app.mopland.com/help/vip","type":""},{"title":"邀请好友","sort":"10","image":"https://assets.mopland.com/image/2019/0311/5c8610a64cbab.png","url":"yqtz","extend":"","type":""},{"title":"大额券榜","sort":"14","image":"https://assets.mopland.com/image/2019/0313/5c891dd9304f1.jpg","url":"deq","extend":"http://jellybox.mopland.com/assets/address/mo","type":"normal"},{"title":"领券教程","sort":"15","image":"https://assets.mopland.com/image/2019/0314/5c89f875e16ab.png","url":"lqjc","extend":"http://app.mopland.com/help/save","type":"normal"}]
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
         * title : 19.9包邮
         * sort : 1
         * image : https://assets.mopland.com/image/2019/0314/5c89fd514308a.png
         * url : jkj
         * extend :
         * type :
         */

        private String title;
        private String sort;
        private String image;
        private String url;
        private String extend;
        private String type;

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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
