package com.yun.software.kaadas.Http;

public class ApiConstants {
    //    public static final String SERVER = "http://shixiangyoupin.com:8080";
    public static final String SERVER = "https://test.zrytech.com/";
    public static final int SAVE_IMG_SUCCESS = 2019624;


    private enum EnvironmentConfig {
        /**
         * 生产环境
         */
        PRODUCTION,
        /**
         * 测试外网
         */
        EXTRANET,

        NEIWANG,

    }


    public static String BASE_URL = "";
    public static String webUrl = "";
    /**
     * 发布环境配置
     */
    public static final EnvironmentConfig ENVIRONMENTCONFIG = EnvironmentConfig.EXTRANET;

    static {
        if (ENVIRONMENTCONFIG == EnvironmentConfig.PRODUCTION) {
            BASE_URL = "https://www.kaishuzhijia.com:8080";
            webUrl = "https://www.kaishuzhijia.com/h5/index.html#/";
        } else if (ENVIRONMENTCONFIG == EnvironmentConfig.EXTRANET) {
            BASE_URL = "https://www.kaishuzhijia.com:8080";
            webUrl = "https://test.zrytech.com/h5/index.html#/";
        } else if (ENVIRONMENTCONFIG == EnvironmentConfig.NEIWANG) {
            BASE_URL = "http://192.168.0.128:8080";
        }
    }

    /**
     * ================================地址管理==================================================
     * 1地址管理
     */
    public static final String ADDRESS_MANAGER = SERVER + "/device/userAddress/find";
    /**
     * 2删除地址
     */
    public static final String ADDRESS_DELETE = SERVER + "/device/userAddress/delete/";
    /**
     * 3添加地址
     */
    public static final String ADDRESS_ADD = SERVER + "/device/userAddress/create";
    /**
     * 4修改地址
     */
    public static final String ADDRESS_EDIT = SERVER + "/device/userAddress/update";
    /**
     * 获取用户信息
     */
    public static final String CUSTOMER_GET = "/systemUpgradeApp/getSystem";
    /**
     * 升级
     */
    public static final String CUSTOMER_UPDATE = "";
    public static final String CUSTOMER_WX_LOGIN = "/wxApp/login";
    /**
     * 登录
     */
    public static final String CUSTOMER_LOGIN = "/customer/login";
    /**
     * 验证码登录
     */
    public static final String CUSTOMER_CODELOGIN = "";
    /**
     * 获取验证码
     */
    public static final String TX_SENDVERIFCODEBYALIYUN = "/customerApp/getCode";
    /**
     * 检查手机号
     */
    public static final String CUSTOMER_CHECKPHONE = "/customerApp/checkPhoneCode";
    /**
     * 验证码
     */
    public static final String CUSTOMER_CHECKCODE = "/customerApp/register";
    /**
     * 注册
     */
    public static final String CUSTOMER_REGEISTER = "/customerApp/initPwd";
    /**
     * 手机号验证(忘记密码第一步)
     */
    public static final String CUSTOMER_FORGETCHECKTEL = "/customer/forgetCheckTel";


    /**
     * 验证码验证(忘记密码第二步)
     */
    public static final String CUSTOMER_FORGETCHECKCODE = "/customer/forgetCheckCode";

    /**
     * 忘记密码第三步
     */
    public static final String CUSTOMER_FORGET = "/customer/forget";
    /**
     * 退出登录
     */
    public static final String CUSTOMER_LOGIN_OUT = "/customer/logout";
    /**
     * 地址管理
     */
    public static String ADDRESSNAME = "alladdress.json";

    /**
     * 获取轮播图，视频，分类
     */
    public static final String HOME_GETFIRSTPART = "/home/getFirstPart";


    /**
     * 获取灵动系列，推拉系列，活动
     */
    public static final String HOME_GETSECDPART = "/home/getSecdPart";


    /**
     * 所有的枚举
     */
    public static final String COMMONAPP_GETALLKEYVALUE = "/commonApp/getAllKeyValue";


    /**
     * 搜索
     */
    public static final String HOME_GETFIRSTSEARCH = "/home/getFirstSearch";
    /**
     * 商品详情
     */
    public static final String HOME_SHOPDETAIL = "/front/product/get";

    /**
     * 商品分类
     */
    public static final String SYSCATEGORY_TREE = "/sysCategory/tree";

    /**
     * 获取商品列表
     */
    public static final String FRONT_PRODUCT_PAGE = "front/product/page";

    /**
     * 获取商品评论列表
     */
    public static final String FRONT_COMMENT_PAGE = "/front/product/getCommentListByProductId";

    /**
     * 今日活动商品全部列表
     */
    public static final String HOME_GETFIRSTALL = "/home/getFirstAll";

    /**
     * 商品属性.
     */
    public static final String FRONT_PRODUCT_GETSKU = "/front/product/getSku";

    /**
     * 添加商品到购物车
     */
    public static final String SHOPCAR_SAVE = "/shopcar/save";

    /**
     * 查看购物车列表
     */
    public static final String SHOPCAR_LIST = "/shopcar/list";

    /**
     * 修改购物车中商品的数量
     */
    public static final String SHOPCAR_UPDATEQTY = "/shopcar/updateQty";

    /**
     * 删除购物车中的商品
     */
    public static final String SHOPCAR_DELETE = "/shopcar/delete";

    /**
     * 收货地址信息分页列表
     */
    public static final String USERADDRESSAPP_PAGE = "/userAddressApp/page";

    /**
     * 普通商品订单预览
     */
    public static final String INDENT_PREVIEWBASEINDENT = "/indent/previewBaseIndent";

    /**
     * 普通商品下单
     */
    public static final String INDENT_CREATEBASEINDENT = "/indent/createBaseIndent";

    /**
     * 活动商品下单
     */
    public static final String INDENT_CREATEACTIVITYINDENT = "/indent/createActivityIndent";


    /**
     * 新增收货地址信息信息
     */
    public static final String USERADDRESSAPP_SAVE = "/userAddressApp/save";

    /**
     * 获取分类下一级
     */
    public static final String SYSCATEGORY_GETCHILDLIST = "/sysCategory/getChildList";


    /**
     * 买家查看订单分页
     */
    public static final String INDENT_MYINDENTSEARCH = "/indent/myIndentSearch";

    /**
     * 买家查看订单分页新
     */
    public static final String INDENT_MYINDENTSEARCH_NEW = "/returnBill/page";

    /**
     * 删除收货地址信息
     */
    public static final String USERADDRESSAPP_DELETE = "/userAddressApp/delete";

    /**
     * 收货地址信息信息更新
     */
    public static final String USERADDRESSAPP_UPDATE = "/userAddressApp/update";

    /**
     * 修改收货地址信息
     */
    public static final String USERADDRESSAPP_UPDATEADDRESS = "/userAddressApp/updateAddress";

    /**
     * 添加收藏
     */
    public static final String COLLECTAPP_ADDCOLLECT = "/collectApp/addCollect";


    /**
     * 取消收藏
     */
    public static final String COLLECTAPP_CANCELCOLLECT = "/collectApp/cancelCollect";

    /**
     * 获取我的优惠券 提交订单
     */
    public static final String COUPON_GETMYLIST = "/coupon/getMyList";
    /**
     * 获取我的优惠券
     */
    public static final String COUPON_GETMYCOUPON = "/coupon/getMyCoupon";

    /**
     * 买家查看订单详情
     */
    public static final String INDENT_DETAILS = "/indent/details";
    /**
     * 售后提交
     */
    public static final String SUBMIT_AFTER_SALE = "/returnBill/save";
    /**
     * 售后详情
     */
    public static final String AFTER_SALE_DETAIL = "/returnBill/get";

    /**
     * 买家取消订单
     */
    public static final String INDENT_CANCEL = "/indent/cancel";

    /**
     * 获取资讯分页列表
     */
    public static final String ARTICLEAPP_INFORMATIONPAGE = "/articleApp/informationPage";

    /**
     * 获取帖子分页列表
     */
    public static final String ARTICLEAPP_INVITATIONPAGE = "/articleApp/invitationPage";

    /**
     * 获取活动分页列表
     */
    public static final String ARTICLEAPP_ACTIVITYPAGE = "/appAactivity/getList";

    /**
     * 获取轮播图
     */
    public static final String COMMONAPP_GETBANNERLIST = "/commonApp/getBannerList";

    /**
     * 用户评论产品
     */
    public static final String TBCOMMENT_SAVE = "/tbComment/save";

    /**
     * 图片上传
     */
    public static final String COMMONAPP_IMGUPLOAD = "/commonApp/imgUpload";

    /**
     * 发布帖子
     */
    public static final String ARTICLEAPP_INVITATIONSAVE = "/articleApp/invitationSave";

    /**
     * 获取用户个人信息接口
     */
    public static final String CUSTOMERAPP_MYINFO = "/customerApp/myInfo";

    /**
     * 修改用户信息
     */
    public static final String CUSTOMERAPP_UPDATE = "/customerApp/update";

    /**
     * 设置密码
     */
    public static final String CUSTOMERAPP_SETPWD = "/customerApp/setPwd";

    /**
     * 绑定手机号
     */
    public static final String CUSTOMERAPP_BINGTEL = "/customerApp/bingTel";

    /**
     * 绑定邀请码
     */
    public static final String CUSTOMERAPP_BINGINVITECODE = "/customerApp/bingInviteCode";

    /**
     * 我的资产
     */
    public static final String BUSINESSCENTER_MYASSETS = "/front/businessCenter/myAssets";

    /**
     * 我的资产
     */
    public static final String BUSINESSCENTER_MYASSETSDETAIL = "/front/businessCenter/myAssetsDetail";

    /**
     * 我的团队人数
     */
    public static final String BUSINESSCENTER_MYTEAMCOUNT = "/front/businessCenter/myTeamCount";

    /**
     * 我的团队 列表
     */
    public static final String BUSINESSCENTER_MYTEAM = "/front/businessCenter/myTeam";

    /**
     * 我的收藏
     */
    public static final String COLLECTAPP_PAGE = "/collectApp/page";

    /**
     * 我的足迹
     */
    public static final String COLLECTAPP_VIEWPAGE = "/collectApp/viewPage";

    /**
     * 获取我的帖子分页列表
     */
    public static final String ARTICLEAPP_MYINVITATIONPAGE = "/articleApp/myInvitationPage";

    /**
     * 获取我的回复分页列表
     */
    public static final String ARTICLEAPP_MYREPLY = "/articleApp/myReply";

    /**
     * 获取门店列表
     */
    public static final String SHOPAPP_PAGE = "/shopApp/page";

    /**
     * 获取门店的详情
     */
    public static final String SHOPAPP_DETAILS = "/shopApp/details";

    /**
     * 提交反馈
     */
    public static final String FEEDBACK_SAVE = "feedback/save";

    /**
     * 用户激活优惠券
     */
    public static final String COUPON_ACTIVATION = "coupon/activation";

    /**
     * 获取过期的优惠券
     */
    public static final String COUPON_GETMYPASTDUECOUPON = "coupon/getMyPastDueCoupon";

    /**
     * 收益明细
     */
    public static final String BUSINESSCENTER_EARNDETAILS = "/front/businessCenter/earnDetails";

    /**
     * 我的任务(进行中)
     */
    public static final String MYTASK_ONGOING = "myTask/ongoing";

    /**
     * 我的任务(已完成)
     */
    public static final String MYTASK_COMPLETE = "myTask/complete";

    /**
     * 取消收藏
     */
    public static final String COLLECTAPP_CANCELCOLLECTBYIDS = "/collectApp/cancelCollectByIds";

    /**
     * 删除足迹
     */
    public static final String COLLECTAPP_DELETE = "/collectApp/delete";

    /**
     * 消息数量
     */
    public static final String SYSMESSAGE_GETSYSMESSAGENUMBER = "/sysMessage/getSysMessageNumber";

    /**
     * 根据类型获取该类型下的消息列表
     */
    public static final String SYSMESSAGE_GETSYSMESSAGELISTBYTYPE = "/sysMessage/getSysMessageListByType";

    /**
     * 获取参数根据渠道商品id
     */
    public static final String PARAMETERAPP_GETPARAMETERAPP = "/parameterApp/getParameterApp";

    /**
     * 删除订单
     */
    public static final String INDENT_DELETE = "/indent/delete";

    /**
     * 活动商品订单预览
     */
    public static final String INDENT_PREVIEWACTIVITYINDENT = "/indent/previewActivityIndent";

    /**
     * 支付
     */
    public static final String PAYAPP_PAYMENT = "/payApp/payment";

    /**
     * 用户确认安装
     */
    public static final String INDENT_AFFIRM = "/indent/affirm";

    /**
     * 评论分类个数
     */
    public static final String FRONT_PRODUCT_GETCOMMENTLISTBYPRODUCTIDTONUM = "/front/product/getCommentListByProductIdToNum";

    /**
     * 客户数据
     */
    public static final String SERVERCONFIG_GETLIST = "/serverconfig/getList";

    /**
     * 获取新人优惠券 用户未登录
     */
    public static final String APPCOUNPON_GETNOTOKENCOUPON = "/appcounpon/getNotokenCoupon";

    /**
     * 获取新人优惠券 用户已登录
     */
    public static final String APPCOUNPON_GETCOUPON = "/appcounpon/getCoupon";

    /**
     * 获取消息
     */
    public static final String SYSMESSAGE_GETSYSMESSAGELIST = "/sysMessage/getSysMessageList";


    /**
     * 支付
     */
    public static final String YS_PAY_APP = "/ys/pay/app";
    /**
     * 支付宝
     */
    public static final String ALLAT_PAY_APP = "aliApp/payment";

    /**
     * 首页获取数量
     */
    public static final String SHOPCAR_GETNUMBER = "/shopcar/getNumber";

    /**
     * 获取订单状态
     */
    public static final String INDENT_GETSTATUSBYNO = "/indent/getStatusByNo";

    /**
     * 砍价帮列表
     */
    public static final String APPBARGAIN_GETBARGAINRECORD = "/appBargain/getBargainRecord";

    /**
     * 发起砍价
     */
    public static final String APPBARGAIN_STARTBARGAIN = "/appBargain/startBargain";

    /**
     * 砍价详情
     */
    public static final String APPBARGAIN_DETAIL = "/appBargain/detail";

    /**
     * 我的砍价列表
     */
    public static final String APPBARGAIN_GETMYBARGAINLIST = "/appBargain/getMyBargainList";

    /**
     * 提现
     */
    public static final String PAYAPP_WITHDRAW = "/payApp/withdraw";

    /**
     * 获取默认银行卡
     */
    public static final String PAYAPP_WITHDRAWREMARK = "/payApp/withdrawRemark";

    /**
     * 是否手机号登录
     */
    public static final String DICT_GETLISTVALUE = "/dict/getListValue";

    /**
     * 分享记录
     */
    public static final String SHARE_SAVE = "share/save";

    /**
     * 微信支付
     */
    public static final String WXAPP_PAYMENT = "wxApp/payment";

    /**
     * 领延保卡
     */
    public static final String COUPON_GETTIMEDELAYCOUPON = "/coupon/getTimeDelayCoupon";

    /**
     * 积分列表
     */
    public static final String INTEGRALWATER_PAGE = "/integralWater/page";
}
