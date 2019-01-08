package com.guodongbaohe.app.common_constant;

/*常量类*/
public class Constant {
    /*微信appid*/
    public static final String WCHATAPPID = "wx84008f9992caeaf3";
    public static final String WCHATAPPKEY = "8bdafdd4c81eb96550dd88bff9b7d99a";
    public static final String NONET = "网络异常，请检查重试";
    public static final String HTTP = "http:";
    public static final String HTTPS = "https:";
    /*友盟统计appkey*/
    public static final String UMENGAPPKEY = "5c075c04b465f599ba000466";
    /*手机唯一标识字段*/
    public static final String IMEI = "IMEI";
    /*配置7.0以上手机读写权限字段*/
    public static final String FILEPROVIDER = "com.guodongbaohe.app.provider";
    //appid
    public static final String APPID = "1811091319257836";
    //appkey
    public static final String APPKEY = "11b77f7c5ed99d0142d45376ffca6f38";
    //转token常量
    public static final String NETKEY = "11b77f7c5ed99d0142d45376ffca6f38";
    /*时间常量key*/
    public static final String TIMELINE = "timestamp";
    /*平台常量key*/
    public static final String PLATFORM = "platform";
    /*平台常量value*/
    public static final String ANDROID = "android";
    /*token字符串*/
    public static final String TOKEN = "token";
    //服务器地址
    public static final String BASE_URL = "https://jellybox.mopland.com/";
    //首页轮播图
    public static final String BANNER = "assets/poster";
    //轮播图下面的跑马灯
    public static final String BANNER_SCROLL = "?type=scroll_app";
    /*合伙人优秀数据图片*/
    public static final String PATERIMGDATA = "?type=cases";
    /*启动广告页数据*/
    public static final String STARTADVERTISEMENT = "?type=guideimg";
    //9.9商品列表
    public static final String GOODSLIST = "goods/search";
    //首页列表数据
    public static final String GOODS_TODAY = "goods/search/";
    //商品一级分类
    public static final String GOODS_CATES = "goods/cates";
    /*其他商品列表*/
    public static final String OTHERGOODSLIST = "goods/search/";
    /*搜索app*/
    public static final String SEARCHAPP = "goods/search";
    /*搜全网*/
    public static final String SEARCHALLNET = "taoke/superSearch";
    /*商品详情*/
    public static final String GOODSDETAIL = "goods/basic";
    /*商品详情里面随机购买的人数*/
    public static final String GOODSDETAIL_BUY_NUMS = "member/rand_user";
    /*验证码*/
    public static final String GETCODE = "auth/send_msg";
    /*注册*/
    public static final String REGISTER = "http://agent.zhizimofang.com/auth/sign";
    /*校验手机号*/
    public static final String CHECKPHONE = "auth/checkLogin";
    /*验证邀请码*/
    public static final String CHECKINVITEDCODE = "auth/checkInvite";
    /*手机号登录*/
    public static final String PHONELOGIN = "auth/login";
    /*新用户登录*/
    public static final String FIRSTUSERLOGIN = "auth/signin";
    /*每日爆款&宣传素材*/
    public static final String EVERYDAYHOSTGOODS = "assets/circle";
    /*微信登录接口*/
    public static final String WCHATLOGIN = "auth/wxLogin";
    /*微信新用户登录接口*/
    public static final String WCHATREGISTER = "auth/wxSign";
    /*获取app用户信息*/
    public static final String APPCONFIGURATION = "member/profile";
    /*排行榜*/
    public static final String RANKINGLIST = "ranking/goods";
    /*用户头像上传*/
    public static final String UPDATEUSERHEAD = "member/avatar";
    /*权限常配置*/
    public static final String PERMISSION = "com.lcq.zhizi.provider";
    /*编辑用户资料*/
    public static final String EDITPERSONALDATA = "member/update";
    /*我的市场*/
    public static final String MYDEPATERMENT = "member/market";
    /*我的市场搜索*/
    public static final String MYDEPATERMENT_SEARCH = "member/marketSearch";
    /*用户信息接口*/
    public static final String MINEDATA = "member/profile";
    /*我的收益*/
    public static final String MINESHOUYIDATA = "member/profit";
    /*我的订单列表*/
    public static final String MYORDERLIST = "order/list";
    /*高佣金接口*/
    public static final String GAOYONGIN = "taoke/privilege";
    /*生成淘口令*/
    public static final String GETTAOKOULING = "taoke/gettkl";
    /*赚钱*/
    public static final String MAKEMONEY = "member/money";
    /*排行榜首页数据*/
    public static final String RANKING_LIST = "member/rank";
    /*热门搜索*/
    public static final String HOT_SEARCH = "ranking/search";
    /*模糊查询*/
    public static final String FUZZY_DATA = "https://suggest.taobao.com/sug?";
    /*支付数据*/
    public static final String PAY_DATA = "member/upgrade";
    /*生成订单号接口*/
    public static final String PAY_ORDER_NO = "payment/create?";
    /*提现*/
    public static final String WITHDRAW_DSPOSIT = "wallet/apply?";
    /*提现记录*/
    public static final String TIXIAN_RECORD = "wallet/withdraw?";
    /*获取app配置信息*/
    public static final String APPPEIZHIDATA = "assets/appConfig";
    /*用户信息基本参数拼接*/
    public static final String USER_DATA_PARA = "member_id,member_name,member_role,invite_code,gender,wechat,qq,province,city,avatar,balance,credits,phone,platform,validity,dateline,lasttime,fans,income";
    /*我的界面数据*/
    public static final String MINE_DATA = "member/mine";
    /*登录成功字符*/
    public static final String LOGINSUCCESS = "loginSuccess";
    /*用户等级升级成功字符*/
    public static final String USER_LEVEL_UPGRADE = "userUpgradeSuccess";
    /*退出登录字符*/
    public static final String LOGIN_OUT = "login_out";
    /*记录分享次数*/
    public static final String SHARE_NUM = "assets/shareCircle";
    /*账户信息*/
    public static final String MY_WALLE_DATA = "wallet/account";
    /*支付宝或者银行卡信息修改*/
    public static final String EDITALIPAYDATA = "wallet/update";
    /*提现成功状态*/
    public static final String TIIXANSUCCESS = "tixiansuccess";
    /*合伙人数据*/
    public static final String HEHUOREDATA = "member/fansRole";
    /*宝盒转换佣金*/
    public static final String BAOHETOYONGJIN = "wallet/exchange";
    /*商品列表接口*/
    public static final String SHOP_LIST = "goods/query";
    /*设备唯一标识字段*/
    public static final String PESUDOUNIQUEID = "pesudoUniqueID";
    /*网络类型字段*/
    public static final String NETWORKTYPE = "networktype";
    /*小提示*/
    public static final String NOTICE = "assets/notice?";
    /*收入明细*/
    public static final String SHOURUMINGXI = "wallet/detail";
    /*剪切板服务*/
    public static final String SERVICE_ACTION = "com.service.clipservice";
    /*商品详情头部基本信息*/
    public static final String SHOP_HEAD_BASIC = "goods/basic";
    /*二维码生成*/
    public static final String ERWEIMAA = "qrcode/goods";
    /*待审核提现金额*/
    public static final String GETWAITMONEY = "wallet/money";
    /*搜索返回字段*/
    public static final String SEARCH_BACK = "SEARCH_BACK";
    /*版本升级接口*/
    public static final String VERSIONUPDATE = "assets/appUpdate";
    /*邀请好友海报接口*/
    public static final String INVITEFRIENDHAIBAO = "qrcode/invite";

    /*收藏夹列表*/
    public static final String SHOUCANG = "member/collectList";

    public static final String SHOUCANGFY="favorite/list";

    /*收藏夹数据删除*/
    public static final String SC_DELETE = "favorite/delete";

    /*校验邀请码*/
    public static final String JY_NUMBER = "auth/checkAuth";
}
