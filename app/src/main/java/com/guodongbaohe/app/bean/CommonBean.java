package com.guodongbaohe.app.bean;

import java.io.Serializable;
import java.util.List;

public class CommonBean {

    private List<CommonResult> result;

    public List<CommonResult> getResult() {
        return result;
    }

    public void setResult(List<CommonResult> result) {
        this.result = result;
    }


    public class CommonResult implements Serializable {
        private String cate_id;
        private List<CommonSecond> child;
        private String name;
        private String slogan;
        private String stats;
        private String label;
        private boolean isChoose;
        private String thumb;

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public boolean isChoose() {
            return isChoose;
        }

        public void setChoose(boolean choose) {
            isChoose = choose;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public List<CommonSecond> getChild() {
            return child;
        }

        public void setChild(List<CommonSecond> child) {
            this.child = child;
        }

        public String getCate_id() {
            return cate_id;
        }

        public void setCate_id(String cate_id) {
            this.cate_id = cate_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSlogan() {
            return slogan;
        }

        public void setSlogan(String slogan) {
            this.slogan = slogan;
        }

        public String getStats() {
            return stats;
        }

        public void setStats(String stats) {
            this.stats = stats;
        }

    }

    public class CommonSecond implements Serializable {
        private String cate_id;
        private String name;
        private String slogan;
        private String stats;
        private String thumb;

        public String getCate_id() {
            return cate_id;
        }

        public void setCate_id(String cate_id) {
            this.cate_id = cate_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSlogan() {
            return slogan;
        }

        public void setSlogan(String slogan) {
            this.slogan = slogan;
        }

        public String getStats() {
            return stats;
        }

        public void setStats(String stats) {
            this.stats = stats;
        }

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

    }

}
