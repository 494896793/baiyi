package www.qisu666.com.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.activity.LoginActivity;
import www.qisu666.com.activity.UseCarOrderingLongRentActivity;
import www.qisu666.com.activity.WebActivity;
import www.qisu666.com.adapter.LongRentComboAdapter;
import www.qisu666.com.app.MyApplication;
import www.qisu666.com.callback.CarResp;
import www.qisu666.com.callback.LongRentComboListResp;
import www.qisu666.com.callback.LongRentOrderCarResp;
import www.qisu666.com.callback.LongRentRecoverComboResp;
import www.qisu666.com.callback.LongRentRecoverResp;
import www.qisu666.com.callback.ParkResp;
import www.qisu666.com.callback.SystemConfigResp;
import www.qisu666.com.callback.UserInfoResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.request.LongRentComboListRequest;
import www.qisu666.com.request.LongRentOrderCarRequest;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.InstanceUtils;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.SharedPreferencesUtils;
import www.qisu666.com.utils.TVUtils;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.utils.UserUtils;
import www.qisu666.com.view.CustomAlertDialog;
import www.qisu666.com.view.NestedListView;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by wujiancheng on 2017/9/20.
 * 订车之前，短租
 */

public class UseCarPreOrderLongRentFragment extends BaseFragment {
    private static final String TAG = UseCarPreOrderLongRentFragment.class.getSimpleName();
    @BindView(R.id.lvLongRent)
    NestedListView lvLongRent;
    @BindView(R.id.sbCoupon)
    SwitchButton sbCoupon;
    @BindView(R.id.tvInsuranceDesc)
    TextView tvInsuranceDesc;//不计免赔描述
    @BindView(R.id.tvConfirmUseCar)
    TextView tvConfirmUseCar;

    private static final int REQUEST_LONG_RENT_COMBO_LIST = 0;//短租套餐列表
    private static final int REQUEST_ORDER_LONG_RENT = 1;//预约短租套餐
    private static final int REQUEST_SPRING_FESTIVAL_LONG_RENT_COMBO_LIST = 2;//春节短租套餐列表

    private static final int DIALOG_INSURANCE_TIP = 2;//取消不计免赔的提示

    private CarResp carResp;
    private ParkResp parkResp;

    //复活的数据
    private LongRentRecoverResp longRentRecoverResp;
    private LongRentRecoverComboResp longRentRecoverComboResp;
    private boolean isRecover;//是否复活

    //是否选择不计免赔
    private boolean isSelectInsurance = true;
    private LongRentComboAdapter comboAdapter;
    private List<LongRentComboListResp> mData = new ArrayList<>();
    private LongRentComboListResp selectedCombo;//选中的套餐

    //不计免赔金额
    private int maxInsuranceMoney;
    private LongRentOrderCarResp orderCarResp;//预约成功账单信息

    @Override
    public int setLayoutResId() {
        return R.layout.fragment_use_car_pre_order_long_rent;
    }

    @Override
    public void initDatas(View view) {
        Bundle bundle = getArguments();
        carResp = (CarResp) bundle.getSerializable("carResp");
        parkResp = (ParkResp) bundle.getSerializable("parkResp");

        if (null != carResp) {
            //不计免赔描述
            maxInsuranceMoney = carResp.getMaxInsuranceMoney();
            tvInsuranceDesc.setText(TVUtils.toString(maxInsuranceMoney / 100.00) + "元/天，不可用优惠券抵用");
        }

        //复活数据
        longRentRecoverResp = (LongRentRecoverResp) bundle.getSerializable("userResurgenceRentOrderInfo");
        longRentRecoverComboResp = (LongRentRecoverComboResp) bundle.getSerializable("packageInfo");
        if (null != longRentRecoverResp && null != longRentRecoverComboResp) {
            isRecover = true;
            selectedCombo = new LongRentComboListResp();
            selectedCombo.setId(longRentRecoverComboResp.getId());
            selectedCombo.setName(longRentRecoverComboResp.getName());
            selectedCombo.setBeforeMoney(longRentRecoverComboResp.getBeforeMoney());
            selectedCombo.setMoney(longRentRecoverComboResp.getMoney());
            selectedCombo.setDays(longRentRecoverComboResp.getDays());
            selectedCombo.setStartTime(longRentRecoverComboResp.getStartTime());
            selectedCombo.setEndTime(longRentRecoverComboResp.getEndTime());
            selectedCombo.setSystemTime(carResp.getSystemTime());
            //春节短租套餐信息
            selectedCombo.setIsFestival(longRentRecoverComboResp.getIsFestival());
            selectedCombo.setGiveDays(longRentRecoverComboResp.getGiveDays());
            LongRentRecoverComboResp.FestivalShareVo festivalShareVo = longRentRecoverComboResp.getFestivalShareVo();
            if (null != festivalShareVo) {
                LongRentComboListResp.FestivalShareVo festivalShareVo1 = new LongRentComboListResp.FestivalShareVo();
                festivalShareVo1.setContent(festivalShareVo.getContent());
                festivalShareVo1.setShareUrl(festivalShareVo.getShareUrl());
                festivalShareVo1.setTitle(festivalShareVo.getTitle());
                selectedCombo.setFestivalShareVo(festivalShareVo1);
            }

            orderCarResp = new LongRentOrderCarResp();
            orderCarResp.setOrderTime(carResp.getOrderTime());
            orderCarResp.setSystemTime(carResp.getSystemTime());
            orderCarResp.setCarRentOrderNumber(carResp.getCarSharingOrderNo());

            isSelectInsurance = "1".equals(longRentRecoverResp.getIsInsurance());
            showPayPPW(activity);

        }

        comboAdapter = new LongRentComboAdapter(getActivity(), mData);
        lvLongRent.setAdapter(comboAdapter);

        lvLongRent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                comboAdapter.setSelectedPos(position);
                if (position >= 0 && position < mData.size()) {
                    selectedCombo = mData.get(position);
                }
            }
        });

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

        /**
         * 获取短租套餐
         */
        requestLongRentCombo();

        observeEvent();
    }

    private void observeEvent() {
        busSubscription = RxBus.getInstance()
                .toObservable(RxBusEvent.class)
                .subscribe(new Action1<RxBusEvent>() {
                    @Override
                    public void call(RxBusEvent rxBusEvent) {
                        switch (rxBusEvent.getEventCode()) {
                            //登录成功，没有跳转到恢复页面，本页面要更新一下
                            case RxEventCodes.CODE_UPDATE_PRE_ORDER_CAR:
                                requestLongRentCombo();
                                break;
                            case RxEventCodes.CODE_CANCEL_LONG_RENT_ORDER://取消短租订单
                                Logger.i(TAG, "取消短租订单后");
                                if (isRecover && null != mData && mData.size() > 0) {//如果是复活，则取消后订单后默认选择第一个套餐
                                    isRecover = false;
                                    selectedCombo = mData.get(0);
                                    //默认选择不计免赔
                                    isSelectInsurance = true;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    @Override
    public void onComplete(String result, int type) {
        if (isSuccess(result)) {
            if (type == REQUEST_LONG_RENT_COMBO_LIST) {//短租套餐列表
                List<LongRentComboListResp> comboListResp = getList(result, LongRentComboListResp.class);
                if (null != comboListResp && comboListResp.size() > 0) {
                    mData.clear();
                    mData.addAll(comboListResp);
                    requestSpringFestivalLongRentCombo();
                }
            } else if (type == REQUEST_ORDER_LONG_RENT) {//预约短租套餐
                orderCarResp = getBean(result, LongRentOrderCarResp.class);
                showPayPPW(activity);
            } else if (type == REQUEST_SPRING_FESTIVAL_LONG_RENT_COMBO_LIST) {//春节短租套餐列表
                List<LongRentComboListResp> comboListResp = getList(result, LongRentComboListResp.class);
                if (null != comboListResp && comboListResp.size() > 0) {
                    mData.addAll(comboListResp);
                }
                updateComboList();
            }
        } else {
            if (type == REQUEST_ORDER_LONG_RENT) {//预约短租套餐失败，弹出提示框
                String code = getCode(result);
                RxBusEvent event = new RxBusEvent();
                event.setEventCode(RxEventCodes.CODE_ORDER_CAR_ERROR);
                Map<String, String> map = new HashMap<>();
                map.put("code", code);
                map.put("result", result);
                event.setContent(map);
                RxBus.getInstance().post(event);
            } else if (type == REQUEST_SPRING_FESTIVAL_LONG_RENT_COMBO_LIST) {//春节短租套餐列表，失败
                updateComboList();
            } else {
                ToastUtil.show(activity, getMsg(result));
            }
        }
    }

    @Override
    public void onFailure(String msg, int type) {
        if (type == REQUEST_SPRING_FESTIVAL_LONG_RENT_COMBO_LIST) {//春节短租套餐列表，失败
            updateComboList();
        }
    }

    /**
     * 更新套餐列表
     */
    private void updateComboList() {
        comboAdapter.notifyDataSetChanged();
        if (!isRecover) {//不是复活，默认选择第一个；是复活，则会构造一个数据
            selectedCombo = mData.get(0);
        }
    }

    @OnClick({R.id.tvConfirmUseCar, R.id.llytInsuranceHelp})
    public void onClickedView(View view) {
        switch (view.getId()) {
            case R.id.llytInsuranceHelp://不计免赔帮助
                SystemConfigResp systemConfigResp = CacheUtils.getIn().getSystem_Info();
                Intent intentInsurance = new Intent(activity, WebActivity.class);
                intentInsurance.putExtra("url", systemConfigResp.getIsInsuranceUrl());
                startActivity(intentInsurance);
                break;
            case R.id.tvConfirmUseCar://确认用车，预约
                if (MyApplication.isLoginSuccess) {
                    requestOrderLongRentCombo();
                } else {
                    Intent intent = new Intent(activity, LoginActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    /**
     * 短租套餐列表
     */
    private void requestLongRentCombo() {
        LongRentComboListRequest comboListRequest = new LongRentComboListRequest();
        comboListRequest.setCityCode(UserUtils.getCityCode());
        comboListRequest.setMethod(RequestUrls.QUERY_LONG_RENT_PACKAGE_INFO);
        doGet(comboListRequest, REQUEST_LONG_RENT_COMBO_LIST, "", false);
    }

    /**
     * 春节短租套餐列表
     */
    private void requestSpringFestivalLongRentCombo() {
        LongRentComboListRequest comboListRequest = new LongRentComboListRequest();
        comboListRequest.setMethod(RequestUrls.QUERY_SPRING_FESTIVAL_LONG_RENT);
        doGet(comboListRequest, REQUEST_SPRING_FESTIVAL_LONG_RENT_COMBO_LIST, "", false);
    }

    /**
     * 短租套餐预约套餐
     */
    public void requestOrderLongRentCombo() {

        if (selectedCombo != null) {
            double distance = 10000000;
            if (carResp != null) {
                CacheUtils.getIn().save(carResp);
                if (null != parkResp) {
                    distance = InstanceUtils.instanceStr(parkResp.getLongitude(),
                            parkResp.getLatitude(), application.longitude, application.latitude);
                }
                Logger.i(TAG, "distance=" + distance);
                if (distance <= 1000000) {
                    LongRentOrderCarRequest request = new LongRentOrderCarRequest();
                    request.setIsInsurance(isSelectInsurance ? "1" : "0");
                    UserInfoResp userInfoResp = CacheUtils.getIn().getUserInfo();
                    if (null != userInfoResp) {
                        request.setCustomerId(userInfoResp.getId());
                    }
                    if (null != carResp) {
                        request.setCarNumber(carResp.getCarNumber());
                    }
                    request.setRentThePackageId(selectedCombo.getId());
                    request.setMethod(RequestUrls.QUERY_LONG_RENT_ORDER);
                    doGet(request, REQUEST_ORDER_LONG_RENT, Config.LOADING_STRING, true);
                } else {
                    ToastUtil.show(activity, "太远了，无法预约该车辆！");
                }
            }
        } else {
            ToastUtil.show(activity, "暂无短租套餐");
        }
    }

    /**
     * 支付ppw
     */
    private void showPayPPW(final Activity context) {
        if (null != selectedCombo) {
            Logger.i(TAG, "selectedCombo.money=" + selectedCombo.getMoney());
            Logger.i(TAG, "isSelectInsurance=" + isSelectInsurance);
            Intent intent = new Intent(context, UseCarOrderingLongRentActivity.class);
            intent.putExtra("isSelectInsurance", isSelectInsurance);
            intent.putExtra("orderCarResp", orderCarResp);
            intent.putExtra("selectedCombo", selectedCombo);
            intent.putExtra("carResp", carResp);
            startActivity(intent);
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

    private void showDialog(String msg, int type) {
        final CustomAlertDialog alertDialog = CustomAlertDialog.getAlertDialog(activity, false, false);
        alertDialog.setMessage(msg);
        if (type == DIALOG_INSURANCE_TIP) {//取消不计免赔提示
            alertDialog
                    .setBtnConfirmColor(R.color.color_blue_02b2e4)
                    .setOnPositiveClickListener("选择不计免赔", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            chooseInsurance();
                            alertDialog.dismiss();
                        }
                    })
                    .setBtnCancelColor(R.color.color_gray_999999)
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
}
