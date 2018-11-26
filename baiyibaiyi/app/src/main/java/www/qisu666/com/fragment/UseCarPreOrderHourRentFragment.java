package www.qisu666.com.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import www.qisu666.com.R;
import www.qisu666.com.activity.LoginActivity;
import www.qisu666.com.activity.UseCarOrderingActivity;
import www.qisu666.com.activity.WebActivity;
import www.qisu666.com.app.MyApplication;
import www.qisu666.com.callback.CarResp;
import www.qisu666.com.callback.OrderCarResp;
import www.qisu666.com.callback.ParkResp;
import www.qisu666.com.callback.ParksResp;
import www.qisu666.com.callback.SystemConfigResp;
import www.qisu666.com.callback.UserInfoResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.map.route.RouteSearchManager;
import www.qisu666.com.request.OrderCarRequest;
import www.qisu666.com.request.RecordChooseParkRequest;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.HighlightUtil;
import www.qisu666.com.utils.InstanceUtils;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.StringUtils;
import www.qisu666.com.utils.TVUtils;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.view.CustomAlertDialog;
import www.qisu666.com.view.FeeUnitMoneyView;
import www.qisu666.com.view.ReturnCarFeeEstimateView;
import com.kyleduo.switchbutton.SwitchButton;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by wujiancheng on 2017/9/20.
 * 订车之前，时租
 */

public class UseCarPreOrderHourRentFragment extends BaseFragment {
    private static final String TAG = UseCarPreOrderHourRentFragment.class.getSimpleName();
    private static final int ORDER_CAR = 0;
    private static final int RECORD_CHOOSE_PARK = 1;//记录选择的还车网点

    private static final int DIALOG_INSURANCE_TIP = 2;//取消不计免赔的提示

    @BindView(R.id.feeUnitMoneyView)
    FeeUnitMoneyView feeUnitMoneyView;
    @BindView(R.id.llytUseCarTypeContainer)
    LinearLayout llytUseCarTypeContainer;//个人和企业支付方式选择
    @BindView(R.id.ivSelectPerson)
    ImageView ivSelectPerson;
    @BindView(R.id.ivSelectCompany)
    ImageView ivSelectCompany;
    @BindView(R.id.rlytUseCarCompany)
    RelativeLayout rlytUseCarCompany;
    @BindView(R.id.tvUseCarCompany)
    TextView tvUseCarCompany;
    @BindView(R.id.tvCompanyLeftAmount)
    TextView tvCompanyLeftAmount;//企业剩余额度
    @BindView(R.id.tvDiscountCompany)
    TextView tvDiscountCompany;//企业额度折扣
    @BindView(R.id.sbCoupon)
    SwitchButton sbCoupon;//不计免赔
    @BindView(R.id.tvInsuranceDesc)
    TextView tvInsuranceDesc;//不计免赔描述
    @BindView(R.id.returnCarFeeEstimateView)
    ReturnCarFeeEstimateView returnCarFeeEstimateView;//还车费用预估

    private CarResp carResp;
    private ParkResp parkResp;//取车网点
    private ParksResp parksRespChooseReturn;//选择还车的网点

    //企业用户
//    private UserInfoResp.Company company;
    //是否选择企业用车
//    private boolean isChooseCompany = true;
    //是否选择不计免赔
    private boolean isSelectInsurance = true;

    @Override
    public int setLayoutResId() {
        return R.layout.fragment_use_car_pre_order_hour_rent;
    }

    @Override
    public void initDatas(View view) {
        Bundle bundle = getArguments();
        carResp = (CarResp) bundle.getSerializable("carResp");
        parkResp = (ParkResp) bundle.getSerializable("parkResp");
        if (null != carResp) {

            feeUnitMoneyView.setData(carResp);

            //不计免赔描述
            int insuranceMoney = carResp.getInsuranceMoney();
            int maxInsuranceMoney = carResp.getMaxInsuranceMoney();
            tvInsuranceDesc.setText(TVUtils.toString(insuranceMoney / 100.00) + "元/时，24小时内" + TVUtils.toString(maxInsuranceMoney / 100.00) + "元封顶，不可用优惠券抵用");
        }
//        setCompanyInfo();
        returnCarFeeEstimateView.setFrom(RxEventCodes.CODE_SEARCH_PARK_FROM_PRE_ORDER_HOUR_RENT);

        sbCoupon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isSelectInsurance && !isChecked) {//取消不计免赔
                    showDialog(Config.CANCEL_INSURANCE, DIALOG_INSURANCE_TIP);
                } else {//选择不计免赔
                    if (isChecked) {
                        chooseInsurance();
                    }
                }
            }
        });

        observeEvent();
    }

    @Override
    public void onComplete(String result, int type) {
        if (isSuccess(result)) {
            if (type == ORDER_CAR) {//预约成功
                OrderCarResp orderCarResp = getBean(result, OrderCarResp.class);
                Intent intent = new Intent(activity, UseCarOrderingActivity.class);
                intent.putExtra("carInfo", carResp);
                intent.putExtra("parkInfo", parkResp);
                intent.putExtra("orderCarResp", orderCarResp);
                if (parksRespChooseReturn != null) {//有选择还车网点
                    intent.putExtra("parksRespChooseReturn", parksRespChooseReturn);
                }
                startActivity(intent);

                activity.setResult(Activity.RESULT_OK);
                activity.finish();
            }
        } else {
            if (type == ORDER_CAR) {//预约失败，通知弹出提示框
                String code = getCode(result);
                RxBusEvent event = new RxBusEvent();
                event.setEventCode(RxEventCodes.CODE_ORDER_CAR_ERROR);
                Map<String, String> map = new HashMap<>();
                map.put("code", code);
                map.put("result", result);
                event.setContent(map);
                RxBus.getInstance().post(event);
            }

        }
    }

    @Override
    public void onFailure(String msg, int type) {
        ToastUtil.show(activity, "网络异常，请重试");
    }

    /**
     * 判断是否是企业用户，设置企业信息
     */
//    private void setCompanyInfo() {
//        UserInfoResp userInfoResp = CacheUtils.getIn().getUserInfo();
//        if (null != userInfoResp) {
//            company = userInfoResp.getCompany();
//            if (null != company) {//企业员工
//                llytUseCarTypeContainer.setVisibility(View.VISIBLE);
//                String amount = userInfoResp.getQuotaRemain();//剩余额度
//                if (!TextUtils.isEmpty(amount) && !"0".equals(amount)) {
//                    tvCompanyLeftAmount.setText("(剩余¥" + TVUtils.toString(Integer.parseInt(amount) / 100.00) + ")");
//                } else {
//                    tvCompanyLeftAmount.setText("(剩余¥0.00)");
//                    //默认个人用车，企业用车置灰，不可点
//                    ivSelectPerson.setImageResource(R.mipmap.pay_checked);
//                    ivSelectCompany.setVisibility(View.GONE);
//                    isChooseCompany = false;
//
//                    tvUseCarCompany.setTextColor(ContextCompat.getColor(activity, R.color.color_gray_cccccc));
//                    tvCompanyLeftAmount.setTextColor(ContextCompat.getColor(activity, R.color.color_gray_cccccc));
//
//                    rlytUseCarCompany.setEnabled(false);
//                }
//                int discount = company.getDiscount();
//                if (discount != 0) {
//                    tvDiscountCompany.setText(TVUtils.toString1(discount / 10.0f) + "折");
//                } else {
//                    tvDiscountCompany.setVisibility(View.GONE);
//                }
//
//            } else {//个人
//                llytUseCarTypeContainer.setVisibility(View.GONE);
//            }
//        }
//    }
    private void observeEvent() {
        busSubscription = RxBus.getInstance()
                .toObservable(RxBusEvent.class)
                .subscribe(new Action1<RxBusEvent>() {
                    @Override
                    public void call(RxBusEvent rxBusEvent) {
                        switch (rxBusEvent.getEventCode()) {
                            //登录成功，没有跳转到恢复页面，本页面要更新一下，从而知道是企业用户还是普通用户
                            case RxEventCodes.CODE_UPDATE_PRE_ORDER_CAR:
//                                setCompanyInfo();
                                break;
                            case RxEventCodes.CODE_SEARCH_PARK_FROM_PRE_ORDER_HOUR_RENT://选择网点完成
                                parksRespChooseReturn = (ParksResp) rxBusEvent.getContent();
                                if (null != parksRespChooseReturn) {
                                    if (null != parksRespChooseReturn.getId() && !parksRespChooseReturn.getId().equals(parkResp.getId())) {//取车网点和选择的还车网点不是同一个
                                        returnCarFeeEstimateView.setData(parksRespChooseReturn.getParkName(), "计算中...");
                                        String lat = parksRespChooseReturn.getLatitude();
                                        String lng = parksRespChooseReturn.getLongitude();
                                        if (!StringUtils.isEmpty(lat) && !StringUtils.isEmpty(lng)) {
                                            LatLonPoint startPoint = new LatLonPoint(Double.valueOf(application.latitude), Double.valueOf(application.longitude));
                                            LatLonPoint endPoint = new LatLonPoint(Double.valueOf(lat), Double.valueOf(lng));
                                            RouteSearchManager routeSearchManager = new RouteSearchManager();
                                            routeSearchManager.route(getActivity(), null, startPoint, endPoint, null, null, RouteSearchManager.DRIVE_ROUTE);
                                            routeSearchManager.setOnDriveRouteCompleteListener(new RouteSearchManager.OnDriveRouteCompleteListener() {
                                                @Override
                                                public void onDriveRouteComplete(DrivePath drivePath) {
                                                    //预估费用以日间单价为准
                                                    String dayMilesUnit = carResp.getMilesMoney();
                                                    String dayTimeUnit = carResp.getTimeMoney();
                                                    double distanceFee = Integer.parseInt(dayMilesUnit) / 100.00;
                                                    double timeFee = Integer.parseInt(dayTimeUnit) / 100.00;
                                                    double distance = drivePath.getDistance();
                                                    double time = drivePath.getDuration();
                                                    Logger.i(TAG, "预估distance=" + distance + ",time=" + time);
                                                    //预估费用
                                                    double carFee = distanceFee * distance / 1000.00 + timeFee * time / 60.00;
                                                    returnCarFeeEstimateView.setData(parksRespChooseReturn.getParkName(), Html.fromHtml(HighlightUtil.convertHightlightText(TVUtils.toString2(carFee + "") + "元", TVUtils.toString2(carFee + ""), "#FF520C")));
                                                }
                                            });
                                        }
                                    } else {//取车网点和选择的还车网点  是同一个，不显示价格
                                        returnCarFeeEstimateView.setData(parksRespChooseReturn.getParkName(), "");
                                    }
                                    //记录该还车网点
                                    recordChoosePark(parksRespChooseReturn.getId());
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    @OnClick({R.id.tvConfirmUseCar, R.id.llytUseCarPerson, R.id.rlytUseCarCompany, R.id.llytInsuranceHelp})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvConfirmUseCar://确认用车
                orderCar();
                break;
            case R.id.llytUseCarPerson://个人用车
                ivSelectPerson.setImageResource(R.mipmap.pay_checked);
                ivSelectCompany.setImageResource(R.mipmap.pay_unchecked);
//                isChooseCompany = false;
                break;
            case R.id.rlytUseCarCompany://企业用车
                ivSelectCompany.setImageResource(R.mipmap.pay_checked);
                ivSelectPerson.setImageResource(R.mipmap.pay_unchecked);
//                isChooseCompany = true;
                break;
            case R.id.llytInsuranceHelp://不计免赔帮助
                SystemConfigResp systemConfigResp = CacheUtils.getIn().getSystem_Info();
                Intent intent = new Intent(activity, WebActivity.class);
                intent.putExtra("url", systemConfigResp.getIsInsuranceUrl());
                startActivity(intent);
                break;
        }
    }

    /**
     * 选择不计免赔
     */
    private void chooseInsurance() {
        isSelectInsurance = true;
        sbCoupon.setChecked(true);
        tvInsuranceDesc.setVisibility(View.VISIBLE);
    }

    /**
     * 取消不计免赔
     */
    private void cancelInsurance() {
        isSelectInsurance = false;
        sbCoupon.setChecked(false);
        tvInsuranceDesc.setVisibility(View.GONE);
    }

    /**
     * 预约车辆
     */
    private void orderCar() {
        if (MyApplication.isLoginSuccess) {
            orderCarRequest();
        } else {
            Intent intent = new Intent(activity, LoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 请求预约
     */
    public void orderCarRequest() {
        double distance = 10000000;
        if (carResp != null) {
            CacheUtils.getIn().save(carResp);
            if (null != parkResp) {
                distance = InstanceUtils.instanceStr(parkResp.getLongitude(),
                        parkResp.getLatitude(), application.longitude, application.latitude);
//                parkInfo.setDistance(distance + "");
            }
            Logger.i(TAG, "distance=" + distance);
            if (distance <= 1000000) {
                OrderCarRequest info = new OrderCarRequest();
                try {
                    info.setCarNumber(URLEncoder.encode(carResp.getCarNumber(), "UTF-8"));
                    UserInfoResp userInfoResp = CacheUtils.getIn().getUserInfo();
                    if (null != userInfoResp) {
                        info.setCustomerId(userInfoResp.getId());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
//                if (company != null) {//企业用户
//                    info.setIsCompanyOrder(isChooseCompany + "");
//                    info.setCompanyId(company.getId());
//                }
                info.setIsCompanyOrder("false");
                info.setIsInsurance(isSelectInsurance ? "1" : "0");
                if (parksRespChooseReturn != null) {
                    info.setBeforehandParkId(parksRespChooseReturn.getId());
                }
                info.setMethod(RequestUrls.USER_GENERATE_ORDER);
                doGet(info, ORDER_CAR, "正在预约...", true);
            } else {
                ToastUtil.show(activity, "太远了，无法预约该车辆！");
            }
        }
    }

    private void showDialog(String msg, int type) {
        final CustomAlertDialog alertDialog = CustomAlertDialog.getAlertDialog(activity, false, false);
        alertDialog.setMessage(msg);
        if (type == DIALOG_INSURANCE_TIP) {//取消不计免赔提示
            alertDialog
                    .setBtnConfirmColor(R.color.new_primary)
                    .setOnPositiveClickListener("选择不计免赔", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chooseInsurance();
                            alertDialog.dismiss();
                        }
                    })
                    .setBtnCancelColor(R.color.main_background)
                    .setOnNegativeClickListener("知道了", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                            cancelInsurance();
                        }
                    });
        }

        alertDialog.show();
    }

    /**
     * 记录选择的还车网点
     */
    private void recordChoosePark(String parkId) {
        RecordChooseParkRequest request = new RecordChooseParkRequest();
        request.setParkId(parkId);
        request.setMethod(RequestUrls.RECORD_CHOOSE_PARK);
        doGet(request, RECORD_CHOOSE_PARK, "", false);
    }
}
