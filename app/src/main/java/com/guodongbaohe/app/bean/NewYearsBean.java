package com.guodongbaohe.app.bean;

import java.util.List;

public class NewYearsBean {

    /**
     * status : 0
     * result : [{"title":"新春","sort":"0","image":"https://assets.mopland.com/image/2019/0114/5c3c3f1fab9c4.png","url":"https://s.click.taobao.com/t?e=m%3D2%26s%3DyS%2Bb44ZplKgcQipKwQzePCperVdZeJvipRe%2F8jaAHci5VBFTL4hn2eEemHBxLPcL8KZ3OPYu2FOR4ypTBJBwtP%2FCccdPzIn0dSuwsRkNflCHWWrZt0BFT5BOig4Q%2Bt4xKQlzryg1l8c7Ys%2FxssswngCwY0sGA6bJQRpqMC8XMCe9TrI5ZrzPHPQxQIubt7ug1mm%2Fu84HLdF8T4fSBsLQqtsPU7N2SHNyvDovBBh%2F%2FUnpllOKvXKGpb2YnZ7gs%2Faz&pid=","extend":""}]
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
         * title : 新春
         * sort : 0
         * image : https://assets.mopland.com/image/2019/0114/5c3c3f1fab9c4.png
         * url : https://s.click.taobao.com/t?e=m%3D2%26s%3DyS%2Bb44ZplKgcQipKwQzePCperVdZeJvipRe%2F8jaAHci5VBFTL4hn2eEemHBxLPcL8KZ3OPYu2FOR4ypTBJBwtP%2FCccdPzIn0dSuwsRkNflCHWWrZt0BFT5BOig4Q%2Bt4xKQlzryg1l8c7Ys%2FxssswngCwY0sGA6bJQRpqMC8XMCe9TrI5ZrzPHPQxQIubt7ug1mm%2Fu84HLdF8T4fSBsLQqtsPU7N2SHNyvDovBBh%2F%2FUnpllOKvXKGpb2YnZ7gs%2Faz&pid=
         * extend :
         */

        private String title;
        private String sort;
        private String image;
        private String url;
        private String extend;
        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

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
