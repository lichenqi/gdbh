package com.guodongbaohe.app.bean;

import java.util.List;

public class PublicityMaterialBean {

    public List<PublicityMaterialData> getResult() {
        return result;
    }

    public void setResult(List<PublicityMaterialData> result) {
        this.result = result;
    }

    private List<PublicityMaterialData> result;

    public class PublicityMaterialData {
        private String article;
        private String dateline;
        private String id;
        private String ip;
        private String member_id;
        private String status;
        private String thumb;
        private String timeline;

        public String getThumb() {
            return thumb;
        }

        public void setThumb(String thumb) {
            this.thumb = thumb;
        }

        public String getArticle() {
            return article;
        }

        public void setArticle(String article) {
            this.article = article;
        }

        public String getDateline() {
            return dateline;
        }

        public void setDateline(String dateline) {
            this.dateline = dateline;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getMember_id() {
            return member_id;
        }

        public void setMember_id(String member_id) {
            this.member_id = member_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimeline() {
            return timeline;
        }

        public void setTimeline(String timeline) {
            this.timeline = timeline;
        }

    }
}
