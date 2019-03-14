package com.guodongbaohe.app.bean;

public class ConfigurationBean {


    /**
     * status : 0
     * result : {"upgrade_vip_invite":"1","upgrade_partner_invite":"199","upgrade_invite_num":"5","upgrade_partner":"299","upgrade_partner_nocost":"50","upgrade_boss":"888","upgrade_boss_invite":"20","upgrade_boss_nocost":"50","upgrade_boss_hold":"100","upgrade_vip_cost":"199","upgrade_partner_vips":"10","upgrade_partner_money":"300","upgrade_boss_partners":"15","upgrade_boss_money":"3000","tax_rate":"0.12","min_withdraw_card":"20000","min_withdraw_alipay":"1","min_withdraw_credit":"1","is_update":"1","online_switch":"yes","taobao_switch":"yes","android_verify_version":"1.0.0","android_verify_version_other":"4.1.1","android_online_switch":"no","money_upgrade_switch":"no","start_guide_to_login":"no","is_update_android":"no","is_show_coupon":"no","is_show_ratio":"no","is_pop_window":"no","is_pop_window_vip":"no","is_show_money_vip":"no","is_show_money_partner":"no","is_force_update":"no","short_title":"全网隐藏优惠券，邀请好友一起省钱","share_friends_title":"全网隐藏优惠券，邀请好友一起省钱！","upgrade_tips_title":"添加客服微信","upgrade_tips_subtitle":"领取软件+教学课程","app_token_desc":"说明：果冻令牌可用于登录智慧大脑自动群发软件","android_version":"160","android_server_name":"gdbh160.apk","version_title":"1,优化用户体验","version_desc":"2,设置界面新增修改手机号功能","ios_version":"1.5.0","ios_title":"更新提示：/n1.淘宝购物车增加一键查券，购物更省钱/n2.App进行改版升级，会员推广收益翻倍/n3.增加换绑手机号/n4.细节优化，提升体验","order_new":"http://app.mopland.com/course/detail?id=11&type=xinshou","share_goods":"http://app.mopland.com/help/rule","question":"http://app.mopland.com/question/index","course":"http://app.mopland.com/course/index","invite_friends":"http://app.mopland.com/share/invite","about_us":"http://app.mopland.com/about/index","agreement":"http://app.mopland.com/help/protocol","online_switch_android":"no"}
     * page : {"save":{"title":"省钱教程","url":"http://app.mopland.com/help/save"},"vip":{"title":"vip权益","url":"http://app.mopland.com/help/vip"},"vip_pay":{"title":"升级vip","url":"http://app.mopland.com/help/payvip"},"partner":{"title":"升级合伙人","url":"http://app.mopland.com/help/member"},"president":{"title":"升级总裁","url":"http://app.mopland.com/help/president"},"boss":{"title":"总裁权益","url":"http://app.mopland.com/help/presidentone"},"course":{"title":"新手教程","url":"http://app.mopland.com/course/index"},"question":{"title":"常见问题","url":"http://app.mopland.com/question/index"},"about":{"title":"关于我们","url":"http://app.mopland.com/about/index"},"protocol":{"title":"用户协议","url":"http://app.mopland.com/help/protocol"},"rule":{"title":"分享商品","url":"http://app.mopland.com/help/rule"}}
     */

    private int status;
    private ResultBean result;
    private PageBean page;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
    }

    public static class ResultBean {
        /**
         * upgrade_vip_invite : 1
         * upgrade_partner_invite : 199
         * upgrade_invite_num : 5
         * upgrade_partner : 299
         * upgrade_partner_nocost : 50
         * upgrade_boss : 888
         * upgrade_boss_invite : 20
         * upgrade_boss_nocost : 50
         * upgrade_boss_hold : 100
         * upgrade_vip_cost : 199
         * upgrade_partner_vips : 10
         * upgrade_partner_money : 300
         * upgrade_boss_partners : 15
         * upgrade_boss_money : 3000
         * tax_rate : 0.12
         * min_withdraw_card : 20000
         * min_withdraw_alipay : 1
         * min_withdraw_credit : 1
         * is_update : 1
         * online_switch : yes
         * taobao_switch : yes
         * android_verify_version : 1.0.0
         * android_verify_version_other : 4.1.1
         * android_online_switch : no
         * money_upgrade_switch : no
         * start_guide_to_login : no
         * is_update_android : no
         * is_show_coupon : no
         * is_show_ratio : no
         * is_pop_window : no
         * is_pop_window_vip : no
         * is_show_money_vip : no
         * is_show_money_partner : no
         * is_force_update : no
         * short_title : 全网隐藏优惠券，邀请好友一起省钱
         * share_friends_title : 全网隐藏优惠券，邀请好友一起省钱！
         * upgrade_tips_title : 添加客服微信
         * upgrade_tips_subtitle : 领取软件+教学课程
         * app_token_desc : 说明：果冻令牌可用于登录智慧大脑自动群发软件
         * android_version : 160
         * android_server_name : gdbh160.apk
         * version_title : 1,优化用户体验
         * version_desc : 2,设置界面新增修改手机号功能
         * ios_version : 1.5.0
         * ios_title : 更新提示：/n1.淘宝购物车增加一键查券，购物更省钱/n2.App进行改版升级，会员推广收益翻倍/n3.增加换绑手机号/n4.细节优化，提升体验
         * order_new : http://app.mopland.com/course/detail?id=11&type=xinshou
         * share_goods : http://app.mopland.com/help/rule
         * question : http://app.mopland.com/question/index
         * course : http://app.mopland.com/course/index
         * invite_friends : http://app.mopland.com/share/invite
         * about_us : http://app.mopland.com/about/index
         * agreement : http://app.mopland.com/help/protocol
         * online_switch_android : no
         */

        private String upgrade_vip_invite;
        private String upgrade_partner_invite;
        private String upgrade_invite_num;
        private String upgrade_partner;
        private String upgrade_partner_nocost;
        private String upgrade_boss;
        private String upgrade_boss_invite;
        private String upgrade_boss_nocost;
        private String upgrade_boss_hold;
        private String upgrade_vip_cost;
        private String upgrade_partner_vips;
        private String upgrade_partner_money;
        private String upgrade_boss_partners;
        private String upgrade_boss_money;
        private String tax_rate;
        private String min_withdraw_card;
        private String min_withdraw_alipay;
        private String min_withdraw_credit;
        private String is_update;
        private String online_switch;
        private String taobao_switch;
        private String android_verify_version;
        private String android_verify_version_other;
        private String android_online_switch;
        private String money_upgrade_switch;
        private String start_guide_to_login;
        private String is_update_android;
        private String is_show_coupon;
        private String is_show_ratio;
        private String is_pop_window;
        private String is_pop_window_vip;
        private String is_show_money_vip;
        private String is_show_money_partner;
        private String is_force_update;
        private String short_title;
        private String share_friends_title;
        private String upgrade_tips_title;
        private String upgrade_tips_subtitle;
        private String app_token_desc;
        private String android_version;
        private String android_server_name;
        private String version_title;
        private String version_desc;
        private String ios_version;
        private String ios_title;
        private String order_new;
        private String share_goods;
        private String question;
        private String course;
        private String invite_friends;
        private String about_us;
        private String agreement;
        private String online_switch_android;
        private String is_index_activity;

        public String getIs_index_activity() {
            return is_index_activity;
        }

        public void setIs_index_activity(String is_index_activity) {
            this.is_index_activity = is_index_activity;
        }

        public String getUpgrade_vip_invite() {
            return upgrade_vip_invite;
        }

        public void setUpgrade_vip_invite(String upgrade_vip_invite) {
            this.upgrade_vip_invite = upgrade_vip_invite;
        }

        public String getUpgrade_partner_invite() {
            return upgrade_partner_invite;
        }

        public void setUpgrade_partner_invite(String upgrade_partner_invite) {
            this.upgrade_partner_invite = upgrade_partner_invite;
        }

        public String getUpgrade_invite_num() {
            return upgrade_invite_num;
        }

        public void setUpgrade_invite_num(String upgrade_invite_num) {
            this.upgrade_invite_num = upgrade_invite_num;
        }

        public String getUpgrade_partner() {
            return upgrade_partner;
        }

        public void setUpgrade_partner(String upgrade_partner) {
            this.upgrade_partner = upgrade_partner;
        }

        public String getUpgrade_partner_nocost() {
            return upgrade_partner_nocost;
        }

        public void setUpgrade_partner_nocost(String upgrade_partner_nocost) {
            this.upgrade_partner_nocost = upgrade_partner_nocost;
        }

        public String getUpgrade_boss() {
            return upgrade_boss;
        }

        public void setUpgrade_boss(String upgrade_boss) {
            this.upgrade_boss = upgrade_boss;
        }

        public String getUpgrade_boss_invite() {
            return upgrade_boss_invite;
        }

        public void setUpgrade_boss_invite(String upgrade_boss_invite) {
            this.upgrade_boss_invite = upgrade_boss_invite;
        }

        public String getUpgrade_boss_nocost() {
            return upgrade_boss_nocost;
        }

        public void setUpgrade_boss_nocost(String upgrade_boss_nocost) {
            this.upgrade_boss_nocost = upgrade_boss_nocost;
        }

        public String getUpgrade_boss_hold() {
            return upgrade_boss_hold;
        }

        public void setUpgrade_boss_hold(String upgrade_boss_hold) {
            this.upgrade_boss_hold = upgrade_boss_hold;
        }

        public String getUpgrade_vip_cost() {
            return upgrade_vip_cost;
        }

        public void setUpgrade_vip_cost(String upgrade_vip_cost) {
            this.upgrade_vip_cost = upgrade_vip_cost;
        }

        public String getUpgrade_partner_vips() {
            return upgrade_partner_vips;
        }

        public void setUpgrade_partner_vips(String upgrade_partner_vips) {
            this.upgrade_partner_vips = upgrade_partner_vips;
        }

        public String getUpgrade_partner_money() {
            return upgrade_partner_money;
        }

        public void setUpgrade_partner_money(String upgrade_partner_money) {
            this.upgrade_partner_money = upgrade_partner_money;
        }

        public String getUpgrade_boss_partners() {
            return upgrade_boss_partners;
        }

        public void setUpgrade_boss_partners(String upgrade_boss_partners) {
            this.upgrade_boss_partners = upgrade_boss_partners;
        }

        public String getUpgrade_boss_money() {
            return upgrade_boss_money;
        }

        public void setUpgrade_boss_money(String upgrade_boss_money) {
            this.upgrade_boss_money = upgrade_boss_money;
        }

        public String getTax_rate() {
            return tax_rate;
        }

        public void setTax_rate(String tax_rate) {
            this.tax_rate = tax_rate;
        }

        public String getMin_withdraw_card() {
            return min_withdraw_card;
        }

        public void setMin_withdraw_card(String min_withdraw_card) {
            this.min_withdraw_card = min_withdraw_card;
        }

        public String getMin_withdraw_alipay() {
            return min_withdraw_alipay;
        }

        public void setMin_withdraw_alipay(String min_withdraw_alipay) {
            this.min_withdraw_alipay = min_withdraw_alipay;
        }

        public String getMin_withdraw_credit() {
            return min_withdraw_credit;
        }

        public void setMin_withdraw_credit(String min_withdraw_credit) {
            this.min_withdraw_credit = min_withdraw_credit;
        }

        public String getIs_update() {
            return is_update;
        }

        public void setIs_update(String is_update) {
            this.is_update = is_update;
        }

        public String getOnline_switch() {
            return online_switch;
        }

        public void setOnline_switch(String online_switch) {
            this.online_switch = online_switch;
        }

        public String getTaobao_switch() {
            return taobao_switch;
        }

        public void setTaobao_switch(String taobao_switch) {
            this.taobao_switch = taobao_switch;
        }

        public String getAndroid_verify_version() {
            return android_verify_version;
        }

        public void setAndroid_verify_version(String android_verify_version) {
            this.android_verify_version = android_verify_version;
        }

        public String getAndroid_verify_version_other() {
            return android_verify_version_other;
        }

        public void setAndroid_verify_version_other(String android_verify_version_other) {
            this.android_verify_version_other = android_verify_version_other;
        }

        public String getAndroid_online_switch() {
            return android_online_switch;
        }

        public void setAndroid_online_switch(String android_online_switch) {
            this.android_online_switch = android_online_switch;
        }

        public String getMoney_upgrade_switch() {
            return money_upgrade_switch;
        }

        public void setMoney_upgrade_switch(String money_upgrade_switch) {
            this.money_upgrade_switch = money_upgrade_switch;
        }

        public String getStart_guide_to_login() {
            return start_guide_to_login;
        }

        public void setStart_guide_to_login(String start_guide_to_login) {
            this.start_guide_to_login = start_guide_to_login;
        }

        public String getIs_update_android() {
            return is_update_android;
        }

        public void setIs_update_android(String is_update_android) {
            this.is_update_android = is_update_android;
        }

        public String getIs_show_coupon() {
            return is_show_coupon;
        }

        public void setIs_show_coupon(String is_show_coupon) {
            this.is_show_coupon = is_show_coupon;
        }

        public String getIs_show_ratio() {
            return is_show_ratio;
        }

        public void setIs_show_ratio(String is_show_ratio) {
            this.is_show_ratio = is_show_ratio;
        }

        public String getIs_pop_window() {
            return is_pop_window;
        }

        public void setIs_pop_window(String is_pop_window) {
            this.is_pop_window = is_pop_window;
        }

        public String getIs_pop_window_vip() {
            return is_pop_window_vip;
        }

        public void setIs_pop_window_vip(String is_pop_window_vip) {
            this.is_pop_window_vip = is_pop_window_vip;
        }

        public String getIs_show_money_vip() {
            return is_show_money_vip;
        }

        public void setIs_show_money_vip(String is_show_money_vip) {
            this.is_show_money_vip = is_show_money_vip;
        }

        public String getIs_show_money_partner() {
            return is_show_money_partner;
        }

        public void setIs_show_money_partner(String is_show_money_partner) {
            this.is_show_money_partner = is_show_money_partner;
        }

        public String getIs_force_update() {
            return is_force_update;
        }

        public void setIs_force_update(String is_force_update) {
            this.is_force_update = is_force_update;
        }

        public String getShort_title() {
            return short_title;
        }

        public void setShort_title(String short_title) {
            this.short_title = short_title;
        }

        public String getShare_friends_title() {
            return share_friends_title;
        }

        public void setShare_friends_title(String share_friends_title) {
            this.share_friends_title = share_friends_title;
        }

        public String getUpgrade_tips_title() {
            return upgrade_tips_title;
        }

        public void setUpgrade_tips_title(String upgrade_tips_title) {
            this.upgrade_tips_title = upgrade_tips_title;
        }

        public String getUpgrade_tips_subtitle() {
            return upgrade_tips_subtitle;
        }

        public void setUpgrade_tips_subtitle(String upgrade_tips_subtitle) {
            this.upgrade_tips_subtitle = upgrade_tips_subtitle;
        }

        public String getApp_token_desc() {
            return app_token_desc;
        }

        public void setApp_token_desc(String app_token_desc) {
            this.app_token_desc = app_token_desc;
        }

        public String getAndroid_version() {
            return android_version;
        }

        public void setAndroid_version(String android_version) {
            this.android_version = android_version;
        }

        public String getAndroid_server_name() {
            return android_server_name;
        }

        public void setAndroid_server_name(String android_server_name) {
            this.android_server_name = android_server_name;
        }

        public String getVersion_title() {
            return version_title;
        }

        public void setVersion_title(String version_title) {
            this.version_title = version_title;
        }

        public String getVersion_desc() {
            return version_desc;
        }

        public void setVersion_desc(String version_desc) {
            this.version_desc = version_desc;
        }

        public String getIos_version() {
            return ios_version;
        }

        public void setIos_version(String ios_version) {
            this.ios_version = ios_version;
        }

        public String getIos_title() {
            return ios_title;
        }

        public void setIos_title(String ios_title) {
            this.ios_title = ios_title;
        }

        public String getOrder_new() {
            return order_new;
        }

        public void setOrder_new(String order_new) {
            this.order_new = order_new;
        }

        public String getShare_goods() {
            return share_goods;
        }

        public void setShare_goods(String share_goods) {
            this.share_goods = share_goods;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getCourse() {
            return course;
        }

        public void setCourse(String course) {
            this.course = course;
        }

        public String getInvite_friends() {
            return invite_friends;
        }

        public void setInvite_friends(String invite_friends) {
            this.invite_friends = invite_friends;
        }

        public String getAbout_us() {
            return about_us;
        }

        public void setAbout_us(String about_us) {
            this.about_us = about_us;
        }

        public String getAgreement() {
            return agreement;
        }

        public void setAgreement(String agreement) {
            this.agreement = agreement;
        }

        public String getOnline_switch_android() {
            return online_switch_android;
        }

        public void setOnline_switch_android(String online_switch_android) {
            this.online_switch_android = online_switch_android;
        }
    }

    public static class PageBean {
        /**
         * save : {"title":"省钱教程","url":"http://app.mopland.com/help/save"}
         * vip : {"title":"vip权益","url":"http://app.mopland.com/help/vip"}
         * vip_pay : {"title":"升级vip","url":"http://app.mopland.com/help/payvip"}
         * partner : {"title":"升级合伙人","url":"http://app.mopland.com/help/member"}
         * president : {"title":"升级总裁","url":"http://app.mopland.com/help/president"}
         * boss : {"title":"总裁权益","url":"http://app.mopland.com/help/presidentone"}
         * course : {"title":"新手教程","url":"http://app.mopland.com/course/index"}
         * question : {"title":"常见问题","url":"http://app.mopland.com/question/index"}
         * about : {"title":"关于我们","url":"http://app.mopland.com/about/index"}
         * protocol : {"title":"用户协议","url":"http://app.mopland.com/help/protocol"}
         * rule : {"title":"分享商品","url":"http://app.mopland.com/help/rule"}
         */

        private SaveBean save;
        private VipBean vip;
        private VipPayBean vip_pay;
        private PartnerBean partner;
        private PresidentBean president;
        private BossBean boss;
        private CourseBean course;
        private QuestionBean question;
        private AboutBean about;
        private ProtocolBean protocol;
        private RuleBean rule;

        public SaveBean getSave() {
            return save;
        }

        public void setSave(SaveBean save) {
            this.save = save;
        }

        public VipBean getVip() {
            return vip;
        }

        public void setVip(VipBean vip) {
            this.vip = vip;
        }

        public VipPayBean getVip_pay() {
            return vip_pay;
        }

        public void setVip_pay(VipPayBean vip_pay) {
            this.vip_pay = vip_pay;
        }

        public PartnerBean getPartner() {
            return partner;
        }

        public void setPartner(PartnerBean partner) {
            this.partner = partner;
        }

        public PresidentBean getPresident() {
            return president;
        }

        public void setPresident(PresidentBean president) {
            this.president = president;
        }

        public BossBean getBoss() {
            return boss;
        }

        public void setBoss(BossBean boss) {
            this.boss = boss;
        }

        public CourseBean getCourse() {
            return course;
        }

        public void setCourse(CourseBean course) {
            this.course = course;
        }

        public QuestionBean getQuestion() {
            return question;
        }

        public void setQuestion(QuestionBean question) {
            this.question = question;
        }

        public AboutBean getAbout() {
            return about;
        }

        public void setAbout(AboutBean about) {
            this.about = about;
        }

        public ProtocolBean getProtocol() {
            return protocol;
        }

        public void setProtocol(ProtocolBean protocol) {
            this.protocol = protocol;
        }

        public RuleBean getRule() {
            return rule;
        }

        public void setRule(RuleBean rule) {
            this.rule = rule;
        }

        public static class SaveBean {
            /**
             * title : 省钱教程
             * url : http://app.mopland.com/help/save
             */

            private String title;
            private String url;

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

        public static class VipBean {
            /**
             * title : vip权益
             * url : http://app.mopland.com/help/vip
             */

            private String title;
            private String url;

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

        public static class VipPayBean {
            /**
             * title : 升级vip
             * url : http://app.mopland.com/help/payvip
             */

            private String title;
            private String url;

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

        public static class PartnerBean {
            /**
             * title : 升级合伙人
             * url : http://app.mopland.com/help/member
             */

            private String title;
            private String url;

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

        public static class PresidentBean {
            /**
             * title : 升级总裁
             * url : http://app.mopland.com/help/president
             */

            private String title;
            private String url;

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

        public static class BossBean {
            /**
             * title : 总裁权益
             * url : http://app.mopland.com/help/presidentone
             */

            private String title;
            private String url;

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

        public static class CourseBean {
            /**
             * title : 新手教程
             * url : http://app.mopland.com/course/index
             */

            private String title;
            private String url;

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

        public static class QuestionBean {
            /**
             * title : 常见问题
             * url : http://app.mopland.com/question/index
             */

            private String title;
            private String url;

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

        public static class AboutBean {
            /**
             * title : 关于我们
             * url : http://app.mopland.com/about/index
             */

            private String title;
            private String url;

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

        public static class ProtocolBean {
            /**
             * title : 用户协议
             * url : http://app.mopland.com/help/protocol
             */

            private String title;
            private String url;

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

        public static class RuleBean {
            /**
             * title : 分享商品
             * url : http://app.mopland.com/help/rule
             */

            private String title;
            private String url;

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
}
