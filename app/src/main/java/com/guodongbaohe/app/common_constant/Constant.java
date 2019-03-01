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

    /*测试服务*/
    public static final String BASE_LOCAL_URL = "";
    /*订单图像字段*/
    public static final String ORDER_PHOTO = "assets/thumb/";
    //首页轮播图
    public static final String BANNER = "assets/poster";
    //商品一级分类
    public static final String GOODS_CATES = "goods/cates";
    /*其他商品列表*/
    public static final String OTHERGOODSLIST = "goods/search/";
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
    /*获取用户基本信息*/
    public static final String USER_BASIC_INFO = "member/profile";
    /*排行榜*/
    public static final String RANKINGLIST = "ranking/goods";
    /*用户头像上传*/
    public static final String UPDATEUSERHEAD = "member/avatar";
    /*编辑用户资料*/
    public static final String EDITPERSONALDATA = "member/update";
    /*我的市场*/
    public static final String MYDEPATERMENT = "member/market";
    /*我的市场搜索*/
    public static final String MYDEPATERMENT_SEARCH = "member/marketSearch";
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
    public static final String USER_DATA_PARA = "member_id,member_name,member_role,invite_code,gender,wechat,qq,province,city,avatar,balance,credits,phone,platform,validity,dateline,lasttime,fans,income,status";
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
    public static final String ERWEIMAA = "share/goods";
    /*待审核提现金额*/
    public static final String GETWAITMONEY = "wallet/money";
    /*搜索返回字段*/
    public static final String SEARCH_BACK = "SEARCH_BACK";
    /*版本升级接口*/
    public static final String VERSIONUPDATE = "assets/appUpdate";
    /*邀请好友海报接口*/
    public static final String INVITEFRIENDHAIBAO = "share/invite";
    /*安卓邀请海报*/
    public static final String ANDROIDINVITEHAIBAO = "assets/invitePoster";
    /*获取邀请码人的信息*/
    public static final String JY_NUMBER = "auth/checkAuth";
    /*邀请关系查询*/
    public static final String INVITE_CONTACT_CHECK = "auth/searchInvite";
    /*收藏夹列表*/
    public static final String SHOUCANGFY = "favorite/list";
    /*添加收藏*/
    public static final String ADD_COLLECT = "favorite/add";
    /*删除收藏*/
    public static final String CANCEL_COLLECT_SHOP = "favorite/delete";
    /*收藏夹改变*/
    public static final String COLLECT_LIST_CHANGE = "collect_list_change";
    /*潜在用户*/
    public static final String QIANZAIYONGHU = "member/invite";
    /*官方微信*/
    public static final String GF_WCHAT = "member/inviter";
    /*商品是否收藏*/
    public static final String SHOP_IS_COLLECT = "favorite/judge";
    /*错误日志*/
    public static final String ERROR_MESSAGE = "api/front";
    /*到首页字段*/
    public static final String TOMAINTYPE = "tomain";
    /*友盟推送别名字段*/
    public static final String YOUMENGPUSH = "guodong_alias";
    /*商品来源字段*/
    public static final String SHOP_REFERER = "referer";
    /*高佣金来源字段*/
    public static final String GAOYONGJIN_SOURCE = "source";
    /*修改邀请码*/
    public static final String MODIFY_CODE = "member/modifyCode";
    /*修改用户基本信息字段*/
    public static final String EDITUSERINFO = "headimgChange";
    /*取消提现申请*/
    public static final String QUXIAOSHENQING = "wallet/cancel";
    /*备案查询*/
    public static final String BEIANCHECK = "promotion/beian";
    /*保存备案*/
    public static final String SAVEBEIAN = "promotion/saved";
    /*普通用户级别*/
    public static final String COMMON_USER_LEVEL = "0";
    /*Vip用户级别*/
    public static final String VIP_USER_LEVEL = "3";
    /*合伙人用户级别*/
    public static final String PARTNER_USER_LEVEL = "1|5";
    /*总裁用户级别*/
    public static final String BOSS_USER_LEVEL = "2|7";
    /*购物车优惠券数据*/
    public static final String SHOPPING_CART_LIST_DATA = "goods/coupon";
    /*收藏改变*/
    public static final String COLLECT_CHANGE = "collect_change";
    /*手机令牌*/
    public static final String GET_TOKEN = "member/getToken";
    /*修改绑定手机号*/
    public static final String CHANGE_PHONE = "member/modifyOldPhone";
    /*绑定新手机号*/
    public static final String SET_NEW_PHONE = "member/modifyNewPhone";
    /*官方微信客服*/
    public static final String WEIXIN_KEFU = "assets/upgradeCourse";
}
