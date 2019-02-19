package com.guodongbaohe.app.bean;

public class ShopIsCollectBean {
    private ShopIsCollectData result;

    public ShopIsCollectData getResult() {
        return result;
    }

    public void setResult(ShopIsCollectData result) {
        this.result = result;
    }

    public class ShopIsCollectData {
        private String count;

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }
    }
}
