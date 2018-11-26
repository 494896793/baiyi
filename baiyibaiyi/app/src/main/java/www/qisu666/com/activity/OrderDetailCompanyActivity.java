package www.qisu666.com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import www.qisu666.com.R;
import www.qisu666.com.app.CarOrderState;
import www.qisu666.com.app.OrderType;
import www.qisu666.com.app.PayMode;
import www.qisu666.com.callback.CouponBean;
import www.qisu666.com.callback.CouponListChooseResp;
import www.qisu666.com.callback.OrderListResp;
import www.qisu666.com.callback.ParkingFeeAuditStatusResp;
import www.qisu666.com.callback.PrePayResp;
import www.qisu666.com.callback.UserInfoResp;
import www.qisu666.com.callback.WeixinPayData;
import www.qisu666.com.constant.Config;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.pay.ali.AlipayManager;
import www.qisu666.com.pay.wx.WxPayManager;
import www.qisu666.com.request.CouponListChooseRequest;
import www.qisu666.com.request.ParkingFeeAuditStatusRequest;
import www.qisu666.com.request.UseBillRequest;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.utils.Arith;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.DateUtils;
import www.qisu666.com.utils.StringUtils;
import www.qisu666.com.utils.TVUtils;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.utils.UserUtils;
import www.qisu666.com.view.ChooseCouponView;
import www.qisu666.com.view.ChoosePayTypeView;
import www.qisu666.com.view.OrderDiscountView;
import com.bumptech.glide.Glide;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 订单详情，时租，企业订单
 */
public class OrderDetailCompanyActivity extends BaseActivity {
    @BindView(R.id.ivTitleLeft)
    ImageView ivTitleLeft;
    @BindView(R.id.tvTitleName)
    TextView tvTitleName;
    @BindView(R.id.tvTitleRight)
    TextView tvTitleRight;
    @BindView(R.id.llytTitleRight)
    LinearLayout llytTitleRight;
    @BindView(R.id.ivCarPic)
    ImageView ivCarPic;
    @BindView(R.id.llytOrderDetailUnfold)
    LinearLayout llytOrderDetailUnfold;
    @BindView(R.id.tvCarNumber)
    TextView tvCarNumber;
    @BindView(R.id.tvCarTypeName)
    TextView tvCarTypeName;
    @BindView(R.id.llyt_car_number)
    LinearLayout llytCarNumber;
    @BindView(R.id.tvCarOrderNumber)
    TextView tvCarOrderNumber;
    @BindView(R.id.tvUseCarTime)
    TextView tvUseCarTime;
    @BindView(R.id.tvGetCarAddr)
    TextView tvGetCarAddr;
    @BindView(R.id.tvReturnCarAddr)
    TextView tvReturnCarAddr;
    @BindView(R.id.tvTotalPay)
    TextView tvTotalPay;
    @BindView(R.id.tv_order_mileage)
    TextView tvOrderMileage;
    @BindView(R.id.tv_calculate_mileage)
    TextView tvCalculateMileage;
    @BindView(R.id.tv_returncar_mileage_cost)
    TextView tvReturncarMileageCost;
    @BindView(R.id.rl_mileage_cost)
    LinearLayout rlMileageCost;
    @BindView(R.id.tv_returncar_order_cost_time)
    TextView tvReturncarOrderCostTime;
    @BindView(R.id.tv_calculate_time)
    TextView tvCalculateTime;
    @BindView(R.id.tv_returncar_order_time_cost)
    TextView tvReturncarOrderTimeCost;
    @BindView(R.id.rl_order_cost_time)
    LinearLayout rlOrderCostTime;
    @BindView(R.id.orderDiscountView)
    OrderDiscountView orderDiscountView;
    @BindView(R.id.tvDiscountCompany)
    TextView tvDiscountCompany;//标签
    @BindView(R.id.llytPayContainer)
    LinearLayout llytPayContainer;
    @BindView(R.id.sbCompanyPay)
    SwitchButton sbCompanyPay;//企业支付开关
    @BindView(R.id.llytChooseCouponContainer)
    LinearLayout llytChooseCouponContainer;
    @BindView(R.id.chooseCouponView)
    ChooseCouponView chooseCouponView;//选择优惠券
    @BindView(R.id.choosePayTypeView)
    ChoosePayTypeView choosePayTypeView;//支付
    @BindView(R.id.tvConfirmPay)
    TextView tvConfirmPay;//确认支付
    @BindView(R.id.tvStatus)
    TextView tvStatus;//停车费报销状态

    private static final int COUPON_LIST = 0;
    private static final int PAY_BALANCE = 1;
    private static final int QUERY_AUDIT_STATUS = 2;//查询停车费报销审核进度

    private OrderListResp mData;
    //总计费用
    private double costTotal = 0;
    private ParkingFeeAuditStatusResp parkingFeeAuditStatusResp;

    //选择的优惠券
    private CouponBean couponBean;
    private CouponListChooseResp couponListChooseResp;

    //支付类型
    private String payType;
    //是否选择优惠券
    private boolean isChooseCoupon = true;
    private boolean isChooseCompanyPay = true;
    private int companyDiscount;
    private String companyId;
    private String discountLimitPerson;
    //不计免赔
    private double insuranceD = 0;

    //1:后台还车后跳转到订单列表后，按返回键跳到主页
    private int toMain = 0;

    @Override
    public void setView() {
        setContentView(R.layout.activity_order_detail_company);
    }

    @Override
    public void initDatas() {
        tvTitleName.setText("订单详情");

        UserInfoResp userInfoResp = CacheUtils.getIn().getUserInfo();
        //企业
        UserInfoResp.Company company = userInfoResp.getCompany();
        if (null != company) {
            //企业折扣
            companyDiscount = company.getDiscount();
            companyId = company.getId();
            discountLimitPerson = company.getDiscountLimitPerson();
        }
        toMain = getIntent().getExtras().getInt("toMain");

        mData = (OrderListResp) getIntent().getExtras().getSerializable("orderItem");
        setTextData();
        observeEvent();
    }

    private void observeEvent() {
        busSubscription = RxBus.getInstance()
                .toObservable(RxBusEvent.class)
                .subscribe(new Action1<RxBusEvent>() {
                    @Override
                    public void call(RxBusEvent rxBusEvent) {
                        switch (rxBusEvent.getEventCode()) {
                            case RxEventCodes.CHOOSE_COUPON:
                                couponBean = (CouponBean) rxBusEvent.getContent();
                                if (null == couponBean) {
                                    break;
                                }
                                chooseCouponView.setData("¥" + getCouponValue(), 0);
                                //重置余额是否可用
                                choosePayTypeView.setPayMoney(getActualPayMoney(), Config.ORDER_CATEGORY_TIME_RENT, isChooseCompanyPay);
                                break;
                            case RxEventCodes.CODE_USE_CAR_PAY_SUCCESS:
                                closeDialog();
//                                toComments();
                                finishPage();
                                break;
                            case RxEventCodes.CODE_CLOSE_WX_CLIENT_TIP:
                                closeDialog();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void setTextData() {
        if (null != mData) {
            Glide.with(mContext).load(mData.getCarImgUrl()).into(ivCarPic);
            tvCarNumber.setText(mData.getCarNumber() + "(" + mData.getSeat() + "座)");
            tvCarTypeName.setText(mData.getBrand() + mData.getModels() + " | " + mData.getColor());
            tvCarOrderNumber.setText("订单编号:" + mData.getOrderId());

            long timestampStartL = 0, timestampEndL = 0;
            String timestampStart = mData.getBeginTime();
            if (!TextUtils.isEmpty(timestampStart)) {
                timestampStartL = Long.valueOf(timestampStart);

            }
            String timestampEnd = mData.getEndTime();
            if (!TextUtils.isEmpty(timestampEnd)) {
                timestampEndL = Long.valueOf(timestampEnd);

            }
            //用车时间
            tvUseCarTime.setText(DateUtils.getMonthTime(timestampStartL) + "--" + DateUtils.getMonthTime(timestampEndL));
            //取车地点
            tvGetCarAddr.setText(mData.getStartParkName());
            //还车地点
            tvReturnCarAddr.setText(mData.getEndParkName());

            String costS = mData.getOrderMoney() + "";
            String insuranceMoney = mData.getInsuranceMoney();//不计免赔
            if (!StringUtils.isEmpty(costS)) {
                try {
                    costTotal = Arith.div(Double.valueOf(costS), 100.00, 2);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(insuranceMoney) && !"0".equals(insuranceMoney)) {
                    insuranceD = Integer.parseInt(insuranceMoney) / 100.00;
                }
            }
            tvTotalPay.setText("总计" + TVUtils.toString(costTotal) + "元");
            costTotal = Arith.sub(costTotal, insuranceD);
            //里程费
            tvOrderMileage.setText("(" + mData.getSpendMileage() + "公里)");
            String mileageUnit = mData.getMileageUtil();
            if (!TextUtils.isEmpty(mileageUnit)) {
                tvCalculateMileage.setText(mData.getSpendMileage() + "x" + (Integer.parseInt(mileageUnit) / 100.00) + "元");
            }

            String mileageCostS = mData.getSpendMileageMoney() + "";
            double mileageCost = 0;
            if (!TextUtils.isEmpty(mileageCostS)) {
                mileageCost = Double.valueOf(mileageCostS) / 100.00;
            }
            tvReturncarMileageCost.setText(TVUtils.toString(mileageCost) + "元");
            //时长费
            String costTimeS = mData.getSpendTime() + "";
            if (!TextUtils.isEmpty(costTimeS)) {
                int costTime = (int) (Double.parseDouble(costTimeS));
                tvReturncarOrderCostTime.setText("(" + DateUtils.minuteToDay(costTime) + ")");
            } else {
                tvReturncarOrderCostTime.setText("(" + costTimeS + "分钟)");
            }

            String timeUnit = mData.getTimeUtil();
            if (!TextUtils.isEmpty(timeUnit)) {
                tvCalculateTime.setText(costTimeS + "x" + (Integer.parseInt(timeUnit) / 100.00) + "元");
            }

            String timeCostS = mData.getSpendTimeMoney() + "";
            double timeCost = 0;
            if (!TextUtils.isEmpty(timeCostS)) {
                timeCost = Double.valueOf(timeCostS) / 100.00;
            }
            tvReturncarOrderTimeCost.setText(TVUtils.toString(timeCost) + "元");

            tvDiscountCompany.setVisibility(View.VISIBLE);
            if (CarOrderState.Car_Order_Status_nopay.equals(mData.getPayStatus())) {
                tvDiscountCompany.setText("个人");
            } else {
                tvDiscountCompany.setText("企业");
            }

            //夜间优惠
            orderDiscountView.setNightDiscountData(mData.getNightDiscountsMoney());
            //网点折扣
            orderDiscountView.setParkDiscountData(mData.getParkDiscountMoney(), mData.getParkDiscountLimit());
            //优惠券优惠，隐藏
            orderDiscountView.setCouponDiscountData("");
            //不计免赔
            orderDiscountView.setInsuranceMoneyData(mData.getInsuranceMoney(), "不享受折扣优惠");

            if (CarOrderState.Car_Order_Status_nopay.equals(mData.getPayStatus())) {
                //未支付
                //企业折扣
                orderDiscountView.setCompanyDiscountData("");

                //企业支付开关
                sbCompanyPay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        isChooseCompanyPay = isChecked;
                        choosePayTypeView.setPayMoney(getActualPayMoney(), Config.ORDER_CATEGORY_TIME_RENT, isChooseCompanyPay);
                        llytChooseCouponContainer.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                    }
                });

                chooseCouponView.setOnCheckedChangeListener(new ChooseCouponView.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChange(boolean isChecked) {
                        isChooseCoupon = isChecked;
                        choosePayTypeView.setPayMoney(getActualPayMoney(), Config.ORDER_CATEGORY_TIME_RENT, isChooseCompanyPay);
                    }
                });
                chooseCouponView.setOnCouponChooseListener(new ChooseCouponView.OnCouponChooseListener() {
                    @Override
                    public void onCouponChoose() {
                        Intent intent1 = new Intent(mContext, CouponChoiceActivity.class);
                        intent1.putExtra("couponData", couponListChooseResp);
                        intent1.putExtra("orderId", mData.getOrderId());

                        startActivity(intent1);
                    }
                });

                //获取优惠券列表
                getCouponsList();

                choosePayTypeView.setOnPayTypeChooseListener(new ChoosePayTypeView.OnPayTypeChooseListener() {
                    @Override
                    public void onPayTypeChoose(String payType) {
                        OrderDetailCompanyActivity.this.payType = payType;
                        //实付金额
                        double actualPayMoney = getActualPayMoney();
                        tvConfirmPay.setText("确认支付￥" + TVUtils.toString2((actualPayMoney) + ""));
                    }
                });

                choosePayTypeView.setPayMoney(getActualPayMoney(), Config.ORDER_CATEGORY_TIME_RENT, isChooseCompanyPay);
            } else {
                //已支付
                //企业折扣
                orderDiscountView.setCompanyDiscountData(mData.getDiscountMoney());
                //隐藏支付
                llytPayContainer.setVisibility(View.GONE);
                tvConfirmPay.setVisibility(View.GONE);
            }
            orderDiscountView.isViewDiscountLineVisibility();

            //显示还车详情
            llytOrderDetailUnfold.setVisibility(View.GONE);

            String status = mData.getParkingFeeStatusName();
            if (!StringUtils.isEmpty(status)) {
                tvStatus.setText(status);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getParkingFeeAuditStatus();
    }

    @Override
    public void onComplete(String result, int type) {
        if (isSuccess(result)) {
            if (type == QUERY_AUDIT_STATUS) {//查询停车费报销审核进度
                parkingFeeAuditStatusResp = getBean(result, ParkingFeeAuditStatusResp.class);
                if (null != parkingFeeAuditStatusResp) {
                    String status = parkingFeeAuditStatusResp.getParkingFeeStatus();
                    List<ParkingFeeAuditStatusResp.ParkingFeeAuditList> list = parkingFeeAuditStatusResp.getList();
                    if (null != list) {
                        for (ParkingFeeAuditStatusResp.ParkingFeeAuditList parkingFeeAuditList : list) {
                            if (!StringUtils.isEmpty(status) && status.equals(parkingFeeAuditList.getStatus())) {
                                tvStatus.setText(parkingFeeAuditList.getStatusName());
                                break;
                            }
                        }
                    }
                }
            } else if (type == COUPON_LIST) {
                couponListChooseResp = getBean(result, CouponListChooseResp.class);
                if (null != couponListChooseResp && couponListChooseResp.getCanUseList() != null && couponListChooseResp.getCanUseList().size() > 0) {
                    //默认选择的优惠券的位置
//                    int defaultChoosePosition = UserUtils.getIntCoupon(costTotal, data);
//                    if (defaultChoosePosition >= 0 && defaultChoosePosition < data.size()) {
//                        couponListResp = data.get(defaultChoosePosition);
//                    }
                    chooseCouponView.setData("可用(" + couponListChooseResp.getCanUseList().size() + ")", R.color.color_blue_02b2e4);
                } else {
                    setCouponUnenable();
                }
                choosePayTypeView.setPayMoney(getActualPayMoney(), Config.ORDER_CATEGORY_TIME_RENT, isChooseCompanyPay);

            } else if (type == PAY_BALANCE) {
                PrePayResp msg = getBean(result, PrePayResp.class);
                if (PayMode.BALANCE_PAY_TYPE.equals(payType) || PayMode.COMPANY_PAY_TYPE.equals(payType) || msg.getCarSharingPayMoney() == 0) {
                    ToastUtil.showImage(mContext, "支付成功");
                    //支付成功进入评价页面
//                    toComments();
                    finishPage();
                } else if (PayMode.WEIXIN_PAY_TYPE.equals(payType)) {
                    WeixinPayData data = JSON.parseObject(msg.getCarSharingPayRequestData(), WeixinPayData.class);
                    WxPayManager wxPayManager = new WxPayManager();
                    wxPayManager.pay(this, application, data, msg.getCarSharingPayMoney(), RxEventCodes.CODE_USE_CAR_PAY_SUCCESS);
                } else if (PayMode.ALI_PAY_TYPE.equals(payType)) {
                    doCheck(Config.PAYING_STRING, true);
                    AlipayManager alipayManager = new AlipayManager();
                    alipayManager.pay(this, msg.getCarSharingPayRequestData(), RxEventCodes.CODE_USE_CAR_PAY_SUCCESS);
                }
            }
        } else {
            if (type == COUPON_LIST) {
                setCouponUnenable();
            }
        }
    }

    @Override
    public void onFailure(String msg, int type) {
        if (type == COUPON_LIST) {
            setCouponUnenable();
        }
    }

    private void toComments() {
        Bundle bundle = new Bundle();
        if (null != mData) {
            bundle.putString("orderNo", mData.getOrderId());
        }
        bundle.putString("type", OrderType.CAR_ORDER);
        bundle.putInt("fromPage", 1);
        bundle.putInt("showTip", 1);//是否显示支付成功提示,1:显示

        bundle.putString("orderType", "");//订单类型，时租可不传，红包用到
        bundle.putBoolean("isPayComplete", true);//支付完成，红包用到

        //评价后列表页面刷新
        Intent intent = new Intent(this, CommentsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @OnClick({R.id.ivTitleLeft, R.id.llytToRefundParkFee, R.id.tvConfirmPay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivTitleLeft:
                finish();
                break;
            case R.id.llytToRefundParkFee://停车费报销
//                getParkingFeeAuditStatus();
                if (null == parkingFeeAuditStatusResp) {
                    //填写报销单
                    startToParkingFeeReturnActivity();
                } else {
                    //查看进度条
                    Intent intent = new Intent(mContext, ParkingFeeStatusActivity.class);
                    intent.putExtra("auditStatus", parkingFeeAuditStatusResp);
                    intent.putExtra("carNumber", mData.getCarNumber());
                    intent.putExtra("orderId", mData.getOrderId());
                    startActivity(intent);
                }
                break;
            case R.id.tvConfirmPay://确认支付
                toPayOrder();
                break;
        }
    }

    /**
     * 查询停车费报销审核进度
     */
    public void getParkingFeeAuditStatus() {
        ParkingFeeAuditStatusRequest request = new ParkingFeeAuditStatusRequest();
//        request.setParkingFeeId(mData.getParkingFeeId());
        request.setOrderId(mData.getOrderId());
        request.setOrderCategory(mData.getOrderCategory());
        request.setMethod(RequestUrls.QUERY_PARKING_FEE_AUDIT_STATUS);
        doGet(request, QUERY_AUDIT_STATUS, Config.LOADING_STRING, true);
    }

    /**
     * 填写报销单
     */
    private void startToParkingFeeReturnActivity() {
        Intent intent2 = new Intent(this, ParkingFeeReturnActivity.class);
        intent2.putExtra("carNumber", mData.getCarNumber());
        intent2.putExtra("orderId", mData.getOrderId());
        startActivity(intent2);
    }

    /**
     * 实付金额
     *
     * @return 实付金额
     */
    private double getActualPayMoney() {
        double payMoney;
        if (isChooseCompanyPay) {//选择企业支付
            if ("1".equals(discountLimitPerson) || PayMode.COMPANY_PAY_TYPE.equals(payType)) {
                payMoney = Double.valueOf(TVUtils.toString(Arith.mul(companyDiscount / 100.0, costTotal)));
                double discountMoney = Arith.sub(costTotal + "", payMoney + "");
                choosePayTypeView.setCompanyDiscountMoney(TVUtils.toString(discountMoney));
            } else {
                payMoney = costTotal;
            }
        } else {
            payMoney = Double.valueOf(TVUtils.toString(costTotal - getCouponValue()));
        }
        if (payMoney < 0) {
            payMoney = 0.00;
        }
        return payMoney + insuranceD;
    }

    /**
     * 获取选中优惠券的值
     *
     * @return
     */
    private int getCouponValue() {
        int coupon = 0;
        if (couponBean != null) {
            //选择了优惠券
            String couponBalance = couponBean.getMoney();
            if (StringUtils.isIntOrFloat(couponBalance)) {
                coupon = (int) (Integer.parseInt(couponBalance) / 100.00);
            }
        }
        if (!isChooseCoupon) {//没选择优惠券
            coupon = 0;
        }
        return coupon;
    }

    private void setCouponUnenable() {
        chooseCouponView.setData("无可用", R.color.color_gray_999999);
    }

    /**
     * 获取优惠券
     */
    public void getCouponsList() {
        CouponListChooseRequest data = new CouponListChooseRequest();
        data.setCustomerId(UserUtils.getCustomerId());
        if (null != mData) {
            data.setOrderId(mData.getOrderId());
        }
        data.setMethod(RequestUrls.QUERY_ORDER_CAN_USE_COUPON);
        doGet(data, COUPON_LIST, Config.LOADING_STRING, true);
    }

    /**
     * 支付订单
     */
    public void toPayOrder() {
        UseBillRequest data = new UseBillRequest();
        data.setCustomerId(UserUtils.getCustomerId());
        if (null != mData) {
            data.setCarSharingOrderNumber(mData.getOrderId());
        }
        data.setCarSharingPayType(payType);
        //没选企业支付，并且选择了优惠券
        if (!isChooseCompanyPay && null != couponBean && isChooseCoupon) {
            data.setCouponCode(couponBean.getCouponCode());
        } else {
            data.setIsCompanyDiscount(isChooseCompanyPay ? "1" : "0");
            data.setCompanyId(companyId);
        }
        data.setMethod(RequestUrls.TO_PAY_TIME_SHARE_ORDER);
        doGet(data, PAY_BALANCE, Config.PAYING_STRING, true);
    }

    private void finishPage() {
        RxBus rxBus = RxBus.getInstance();
        RxBusEvent event = new RxBusEvent();
        event.setEventCode(RxEventCodes.REFRESH_COUPON);
        event.setContent(toMain);
        rxBus.post(event);
        finish();
    }
}
