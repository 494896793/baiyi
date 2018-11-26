package www.qisu666.com.constant;

public final class RequestUrls {
    public static String url;

    static {
//        url = "http://git.baogny.com/";//测试服务器
//        url = "http://192.168.2.16:7777/";//亚桥
//        url = "http://192.168.2.23:6004/";//张龙
        url = "http://interface.baogny.com/";//正式环境
//        url="http://192.168.2.254:7777/";    //郭东杨
//          url="http://192.168.2.118:8000/";    //常文兵
//        url = "http://192.168.2.18:6002/";//基
//        url = "http://120.79.34.192:7777/";//运营IP
//        url = "http://219.135.155.108:7778/";
//        url = "http://112.74.180.212:7777/";//灰度
//                url = "http://192.168.2.16:7777/";//
    }

    /**
     * 会员快速登录
     */
    public static final String FAST_LOGIN = "member/service/fastLogin";
    /**
     * 查询会员信息
     */
    public static final String QUERY_USER_INFO = "member/service/queryUserInfo";
    /**
     * 会员获取验证码
     */
    public static final String VERIFICATION_CODE = "member/service/getVerificationCode";
    /**
     * 状态信息恢复
     */
    public static final String RECOVER_DATA = "member/service/userResurgence";
    /**
     * 上传头像
     */
    public static final String UPLOAD_HEAD_PHOTO = "member/service/uploadHeadPortrait";
    /**
     * 上传会员身份证驾驶证
     */
    public static final String UPLOAD_VERIFY_PHOTOS = "member/service/perfectUserInfo";
    /**
     * 获取城市列表
     */
    public static final String QUERY_CITY_LIST = "common/service/queryAllCity";
    /**
     * 用户提交评价
     */
    public static final String MEMBER_SUBMIT_EVALUATE = "common/service/userSubmitEvaluate";
    /**
     * 会员请求查询评价标签
     */
    public static final String MEMBER_REQUEST_EVALUATE_LABEL = "common/service/userRequestEvaluateLable";
    /**
     * 查询版本号
     */
    public static final String QUERY_VERSION_RECORD = "common/service/queryVersionRecord";
    /**
     * 系统消息
     */
    public static final String QUERY_SYSTEM_MSG_LOG = "common/service/querySystemMsgLog";
    /**
     * 图文信息
     */
    public static final String QUERY_IMAGE_TEXT_MESSAGE = "common/service/queryImageTextMessage";
    /**
     * 系统参数
     */
    public static final String QUERY_SYSTEM_PARAM = "common/service/querySystemParameter";
    /**
     * 上传会员查看消息时间
     */
    public static final String UPLOAD_MEMBER_QUERY_MSG_TIME = "common/service/uploadUserQueryMsgTime";
    /**
     * 查询停车场列表
     */
    public static final String QUERY_PARK_LIST = "useCar/service/queryParkListByCityCode";
    /**
     * 分享信息
     */
    public static final String QUERY_SHARE_CONTENT = "common/service/queryShareContent";
    /**
     * 根据网点id查询车辆列表
     */
    public static final String QUERY_CAR_LIST_BY_PARK_ID = "useCar/service/queryCarListByDepotId";
    /**
     * 会员生成订单
     */
    public static final String USER_GENERATE_ORDER = "useCar/service/userGenerateSharingOrder";
    /**
     * 会员取消订单
     */
    public static final String USER_CANCEL_ORDER = "useCar/service/userCancelSharingOrder";
    /**
     * 找车，开门，关门
     * 参数：carNumber 车牌号
     * operateType 操作类型：findCar-找车,openDoor-打开车门,closeDoor-关闭车门
     */
    public static final String OPERATE_CAR = "useCar/service/operateCar";
    /**
     * 会员还车
     */
    public static final String USER_PARK_RETURN_CAR = "useCar/service/userReturnTheCar";
    /**
     * 一键用车
     */
    public static final String QUERY_NEARBY_CAR_LIST = "useCar/service/queryNearbyParkCarList";
    /**
     * 获取用车费用
     */
    public static final String QUERY_USE_CAR_COST = "useCar/service/queryUseCarCost";
    /**
     * 订单列表
     */
    public static final String QUERY_HISTORY_ORDER = "trip/service/queryHistoryCarSharingOrder";
    /**
     * 查询会员优惠券接口
     */
    public static final String QUERY_USER_BINDING_COUPON = "common/service/getCouponList";
    /**
     * 余额支付
     */
    public static final String TO_PAY_TIME_SHARE_ORDER = "money/service/toPayCarSharingOrder";
    /**
     * 预充值
     */
    public static final String PRE_RECHARGE = "money/service/customerPrepaReCharge";
    /**
     * 会员绑定优惠券接口
     */
    public static final String BIND_USER_AND_COUPON = "money/service/bindingUserAndCoupon";
    /**
     * 资金交易明细
     */
    public static final String USER_MONEY_LOG = "money/service/userMoneyLog";
    /**
     * 会员退款
     */
    public static final String USER_REQUEST_REFUND = "money/service/userRequestRefund";
    /**
     * 取消退款
     */
    public static final String CANCEL_REFUND = "money/service/userCancelRefund";
    /**
     * 切换地址
     */
    public static final String QUERY_INTERFACE_ADDRESS = "common/service/queryInterfaceAddress";
    /**
     * 网点分城市、区
     */
    public static final String QUERY_CITY_AREA_ALL_PARK = "useCar/service/queryCityAreaAllPark";
    /**
     * 按区分类的网点
     */
    public static final String QUERY_AREA_PARK_BY_CITYCODE = "useCar/service/queryAreaParkByCityId";
    /**
     * 违章查询
     */
    public static final String QUERY_VIOLATION_LIST = "member/service/wz_records_city_member";
    /**
     * 短租套餐列表
     */
    public static final String QUERY_LONG_RENT_PACKAGE_INFO = "useCar/service/queryRentPackageInfo";
    /**
     * 短租预约
     */
    public static final String QUERY_LONG_RENT_ORDER = "useCar/service/userGenerateRentOrder";
    /**
     * 取消短租套餐
     */
    public static final String QUERY_LONG_RENT_CANCEL_ORDER = "useCar/service/userCancelRentOrder";
    /**
     * 短租预支付
     */
    public static final String QUERY_LONG_RENT_PRE_PAY = "money/service/rentOrderPrePayWithCoupon";
    /**
     * 短租使用车中的费用
     */
    public static final String QUERY_USE_CAR_COST_LONG = "useCar/service/queryUseRentCarCost";
    /**
     * 短租还车
     */
    public static final String QUERY_LONG_RENT_RETURN_CAR = "useCar/service/rentOrderReturnCar";
    /**
     * 支付短租超时订单
     */
    public static final String QUERY_LONG_RENT_PAY_OUT_TIME = "money/service/rentOrderOutTimePay";
    /**
     * 中秋国庆活动
     */
    public static final String QUERY_PRIZE_CHANGE = "marketing/service/getPrizeChanceCallBack";
    /**
     * 上传发票图片
     */
    public static final String UPLOAD_INVOICE_PHOTO = "member/service/setParkingFeeInvoiceImg";

    /**
     * 获取用户报销信息
     */
    public static final String QUERY_CUSTOMER_PARKING_FEE = "useCar/service/getCustomerParkingFee";
    /**
     * 提交用户报销信息
     */
    public static final String COMMIT_PARKING_FEE = "useCar/service/setParkingFeeExpenseAccount";
    /**
     * 查询停车费报销审核进度
     */
    public static final String QUERY_PARKING_FEE_AUDIT_STATUS = "useCar/service/getParkingFeeAuditStatus";
    /**
     * 微信通过code获取access_token
     */
    public static final String WX_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";
    /**
     * 微信获取用户个人信息
     */
    public static final String WX_GET_USER_INFO = "https://api.weixin.qq.com/sns/userinfo";
    /**
     * 提交退押金申请
     */
    public static final String COMMIT_REFUND_PLEDGE = "money/service/requireRefundDepositApplication";
    /**
     * 检查用户是否可以押金原路退还
     */
    public static final String CHECK_IS_ORIGINAL_REFUND = "money/service/checkIsRefundReturn";
    /**
     * 用车保证金变化说明
     */
    public static final String QUERY_PLEDGE_CHANGE_EXPLAIN = "money/service/depositChangeExplain";
    /**
     * 退款时间线
     */
    public static final String QUERY_REFUND_TIME_LINE = "money/service/refundTimeLine";
    /**
     * 退还押金明细
     */
    public static final String QUERY_REFUND_PLEDGE_HISTORY = "money/service/repositHistoryList";
    /**
     * 获取用户提交过的支付信息
     */
    public static final String QUERY_CUSTOMER_PAY_INFO = "money/service/getCustomerPayInfo";
    /**
     * 充值押金赠送金额
     */
    public static final String QUERY_RECHARGE_GIFT = "common/service/userQueryRechargeGift";
    /**
     * 常用的网点
     */
    public static final String QUERY_FREQUENT_PARKS = "useCar/service/queryUserRecordList";
    /**
     * 记录选择的还车网点
     */
    public static final String RECORD_CHOOSE_PARK = "useCar/service/setUserParkSearchRecord";
    /**
     * 获取地理围栏
     */
    public static final String QUERY_GEO_FENCE_DATA = "useCar/service/getPaseinfoElectronicFence";
    /**
     * 无法还车上传图片
     */
    public static final String UPLOAD_RETURN_CAR_IMG = "member/service/setReturnTheCarImg";
    /**
     * 扫码绑定优惠券
     */
    public static final String SCAN_QRCODE_GET_COUPON = "common/service/scanQRCodeGetCoupon";
    /**
     * 支付时可用的优惠券
     */
    public static final String QUERY_ORDER_CAN_USE_COUPON = "common/service/getOrderCanUseCouponWithDiscountList";
    /**
     * 查询网点红包车
     */
    public static final String QUERY_PARK_RED_PACKET_CAR = "useCar/service/getParkCarInformationize";
    /**
     * 查询还车网点
     */
    public static final String QUERY_RETURN_PARK = "useCar/service/getParkListByCityCode";
    /**
     * 预约还车网点
     */
    public static final String APPOINTMENTS_RETURN_CAR_PARK = "useCar/service/appointmentsReturnCarPark";
    /**
     * 发送红包车红包
     */
    public static final String SEND_RED_PACKET = "useCar/service/sendRPCarRedRedPackage";
    /**
     * 获取用户分享信息数据
     */
    public static final String QUERY_INVITE_FRIENDS_DATA = "member/service/getCustomerShareInformation";
    /**
     * 获取启动页与活动页的信息
     */
    public static final String QUERY_SPLASH_AND_ACTIVITY = "marketing/service/getStartPageAndActivityPage";
    /**
     * 查询长租春节套餐信息
     */
    public static final String QUERY_SPRING_FESTIVAL_LONG_RENT = "useCar/service/queryFestivalRentPackageInfo";
    /**
     * 获取常规充值可充值金额及其属性列表
     */
    public static final String QUERY_RECHARGE_LIST = "money/service/getRechargeList";
    /**
     * 常规充值支付
     */
    public static final String QUERY_PRE_PAY_RECHARGE = "money/service/customerPrepaReChargeCommon";
    /**
     * 获取故障类型
     */
    public static final String QUERY_PROBLEM_TYPE = "useCar/service/getCarFaultTypeList";
    /**
     * 车辆故障上报
     */
    public static final String QUERY_UPLOAD_PROBLEM = "useCar/service/userCarFaultSubmit";
    /**
     * 用户推荐新建网点
     */
    public static final String QUERY_RECOMMEND_PARK = "useCar/service/setRecommendPark";
    /**
     * 一键用车
     */
    public static final String QUERY_RECENTELY_PARK = "useCar/service/queryRecentlyParkCarList";

}
