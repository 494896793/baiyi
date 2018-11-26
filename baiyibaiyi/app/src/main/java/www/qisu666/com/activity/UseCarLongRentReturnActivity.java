package www.qisu666.com.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.app.UserState;
import www.qisu666.com.callback.CarParkResp;
import www.qisu666.com.callback.CarResp;
import www.qisu666.com.callback.LongRentComboListResp;
import www.qisu666.com.callback.LongRentRecoverComboResp;
import www.qisu666.com.callback.ParkingFeeAuditStatusResp;
import www.qisu666.com.callback.ParksResp;
import www.qisu666.com.callback.SystemConfigResp;
import www.qisu666.com.callback.UseCostLongResp;
import www.qisu666.com.callback.UserInfoResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.receiver.PublicReceiver;
import www.qisu666.com.request.ControlCarRequest;
import www.qisu666.com.request.ParkingFeeAuditStatusRequest;
import www.qisu666.com.request.RecordChooseParkRequest;
import www.qisu666.com.request.UseCarCostRequest;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.DateUtils;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.SBUtils;
import www.qisu666.com.utils.StringUtils;
import www.qisu666.com.utils.TVUtils;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.view.CarInfoView;
import www.qisu666.com.view.CustomAlertDialog;
import www.qisu666.com.view.CustomAlertDialog2;
import www.qisu666.com.view.FeeUnitMoneyTipView;
import www.qisu666.com.view.LongRentLeftTimeView;
import www.qisu666.com.view.ReturnCarParkNaviView;
import www.qisu666.com.view.SpringFestivalPPW;
import www.qisu666.com.view.UseCarCostLongView;
import www.qisu666.com.view.UseCarCostView;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import me.iwf.photopicker.utils.PermissionsConstant;
import me.iwf.photopicker.utils.PermissionsUtils;
import rx.functions.Action1;

/**
 * Created by wujiancheng on 2017/7/29.
 * 短租立即还车
 */

public class UseCarLongRentReturnActivity extends BaseActivity {
    private static final String TAG = UseCarLongRentReturnActivity.class.getSimpleName();
    private static final int FIND_CAR_CODE = 2;
    private static final int OPEN_DOOR_CODE = 3;
    private static final int CLOSE_DOOR_CODE = 4;
    private static final int USE_CAR_COST = 5;
    private static final int POWER_ON_CODE = 8;
    private static final int POWER_OFF_CODE = 9;
    private static final int QUERY_AUDIT_STATUS = 6;//查询停车费报销审核进度
    private static final int RECORD_CHOOSE_PARK = 7;//记录选择的还车网点

    private static final String FIND_CAR = "findCar";//-找车
    private static final String OPEN_DOOR = "openDoor";//-打开车门
    private static final String CLOSE_DOOR = "closeDoor";//-关闭车门
    private static final String POWER_ON = "POWER_ON";    //开启动力
    private static final String POWER_OFF = "POWER_OFF";    //关闭动力

    private static final int TIP_CHARGE_POWER = 1;//充电
    private static final int TIP_RETURN_CAR = 2;//还车
    private static final int TIP_BEFORE_FINISH = 3;//套餐到期之前提示

    @BindView(R.id.tvTitleName)
    TextView tvTitleName;
    @BindView(R.id.tvTitleRight)
    TextView tvTitleRight;
    @BindView(R.id.carInfoView)
    CarInfoView carInfoView;
    @BindView(R.id.useCarCostLongView)
    UseCarCostLongView useCarCostLongView;//套餐使用情况
    @BindView(R.id.longRentLeftTimeView)
    LongRentLeftTimeView longRentLeftTimeView;//套餐内剩余时间
    @BindView(R.id.useCarCostViewContainer)
    LinearLayout useCarCostViewContainer;//套餐外超出部分使用情况
    @BindView(R.id.useCarCostView)
    UseCarCostView useCarCostView;//套餐外超出部分使用情况
    @BindView(R.id.returnCarParkNaviView)
    ReturnCarParkNaviView returnCarParkNaviView;//网点选择
    @BindView(R.id.tvComboTime2)
    TextView tvComboTime2;
    @BindView(R.id.tvComboMileage2)
    TextView tvComboMileage2;
    @BindView(R.id.tvComboFee2)
    TextView tvComboFee2;
    @BindView(R.id.power_linear)
    LinearLayout power_linear;
    @BindView(R.id.open_power_linear)
    LinearLayout open_power_linear;
    @BindView(R.id.close_power_linear)
    LinearLayout close_power_linear;
    @BindView(R.id.power_line)
    View power_line;
    @BindView(R.id.open_power)
    ImageView open_power;
    @BindView(R.id.close_power)
    ImageView close_power;

    private PublicReceiver closeHandler;
    private String orderId;

    //复活
    private CarResp carResp;
    //是否处于复活状态
    private boolean isRevival = false;
    private long useTime;//分钟

    private MyHandler handler;
    private boolean isStartTimeDown;
    private long orderEndTime;//订单结束时间
    private long leftSeconds;//短租剩余时间
    //服务器当前时间和手机当前时间之间的差
    private long timeDelta;
    private boolean isOverTime = false;//是否超出了套餐时间
    private String days = "";//套餐时间
    private LongRentComboListResp selectedCombo;//选中的长租套餐

    private static class MyHandler extends Handler {
        WeakReference<UseCarLongRentReturnActivity> weakReference;

        public MyHandler(UseCarLongRentReturnActivity activity) {
            weakReference = new WeakReference<UseCarLongRentReturnActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (null != weakReference) {
                        UseCarLongRentReturnActivity activity = weakReference.get();
                        if (activity != null) {
                            activity.getFee();
                            Message message = activity.handler.obtainMessage();
                            message.what = 1;
                            activity.handler.sendMessageDelayed(message, 1000 * 60);
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void setView() {
        setContentView(R.layout.activity_use_car_long_rent_return);
    }

    @Override
    public void initDatas() {
        tvTitleName.setText("用车");
        tvTitleRight.setText("车辆位置");
        Intent intent = getIntent();
        carResp = (CarResp) intent.getSerializableExtra("carResp");
        orderId = intent.getStringExtra("orderId");
        selectedCombo = (LongRentComboListResp) intent.getSerializableExtra("selectedCombo");

        UserInfoResp mUser = CacheUtils.getIn().getUserInfo();
        if (null != mUser && UserState.isUsingCar(mUser.getStatus())) {
            getCarParkInfo();
        }

        if (null != carResp) {

            if (carResp.getBootType() == 1) {
                power_linear.setVisibility(View.VISIBLE);
            } else {
                power_linear.setVisibility(View.GONE);
            }

            //电池剩余量
            String battery = carResp.getBatteryResidual() + "";
            int batteryPercent = getBatteryLeftPercent(battery);
            //车辆信息
            carInfoView.setData(carResp.getCarColor(), carResp.getCarSetsNums() + "座", carResp.getCarNumber(),
                    carResp.getCarBrand() + carResp.getModels(),
                    batteryPercent, carResp.getCanUseMileage(), carResp.getIsRedPkCar(), false);
        }

        longRentLeftTimeView.setOnTimeTipClickListener(new LongRentLeftTimeView.OnTimeTipClickListener() {
            @Override
            public void onTimeTipClickListener() {
                showFeeUnitTip("超出部分按分时租赁收费");
            }
        });
//        longRentOverTimeView.setOnTimeTipClickListener(new LongRentOverTimeView.OnTimeTipClickListener() {
//            @Override
//            public void onTimeTipClickListener() {
//                showFeeUnitTip();
//            }
//        });

        longRentLeftTimeView.setOnTimeDownFinishListener(new LongRentLeftTimeView.OnTimeDownFinishListener() {
            @Override
            public void onTimeDownFinish() {
                //倒计时完成自动完成一个计费更新
                getFee();
            }
        });

        handler = new MyHandler(this);
        Message msg = handler.obtainMessage();
        msg.what = 1;
        handler.sendMessageDelayed(msg, 1000 * 60);

        returnCarParkNaviView.setFrom(RxEventCodes.CODE_SEARCH_PARK_FROM_USE_CAR);

        closeHandler = new PublicReceiver(SBUtils.UPDATE_MAP_PARKS_INFO);
        closeHandler.setBeanReceive(new PublicReceiver.IBeanReceive() {
            @Override
            public void getBean() {
                Logger.i(TAG, "还车成功后关掉Handler");
                if (handler != null) {
                    Logger.e(TAG, "销毁handler-----------");
                    handler.removeCallbacksAndMessages(null);
                    finish();
                }
            }
        });

        observeRxEventCode();

        if (selectedCombo != null) {
            //是否是春节活动
            String isSpringFestival = selectedCombo.getIsFestival();
            if ("1".equals(isSpringFestival)) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isFinishing()) {
                                    //春节活动分享
                                    showSpringFestivalDialog();
                                }
                            }
                        });
                    }
                }, 600);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        PermissionsUtils.checkLocationPermission(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFee();
        //判断GPS模块是否开启
        isOpenGps();
    }

    /**
     * 显示计费提示框
     */
    private void showFeeUnitTip(String title) {
        final CustomAlertDialog customAlertDialog = CustomAlertDialog.getAlertDialog(mContext, true, true);
        FeeUnitMoneyTipView feeUnitMoneyTipView = new FeeUnitMoneyTipView(mContext);
        feeUnitMoneyTipView.setData(title, carResp);
        customAlertDialog
                .setViewContainer(feeUnitMoneyTipView)
                .setBtnConfirmColor(R.color.new_primary)
                .setOnPositiveClickListener("知道了", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customAlertDialog.dismiss();
                    }
                }).show();
    }

    private void showTipDialog(String msg, String negativeText, String positiveText, final int type) {
        final CustomAlertDialog alertDialog = CustomAlertDialog.getAlertDialog(mContext, true, true);
        alertDialog.setMessage(msg);
        if (type == TIP_CHARGE_POWER || type == TIP_RETURN_CAR) {
            alertDialog.setBtnCancelColor(R.color.main_background)
                    .setOnNegativeClickListener(negativeText, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
        }
        alertDialog.setBtnConfirmColor(R.color.new_primary)
                .setOnPositiveClickListener(positiveText, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (type == TIP_CHARGE_POWER) {//充电提示
                            SystemConfigResp systemConfigResp = CacheUtils.getIn().getSystem_Info();
                            if (null != systemConfigResp) {
                                Intent intent2 = new Intent(mContext, WebActivity.class);
                                intent2.putExtra("url", systemConfigResp.getFindChargingPileUrl());
                                intent2.putExtra("type", WebActivity.TYPE_DOWNLOAD_APK);
                                startActivity(intent2);
                            }
                        } else if (type == TIP_RETURN_CAR) {//还车提示
                            Intent intent1 = new Intent(mContext, ChooseReturnParkActivity.class);
                            intent1.putExtra("orderId", orderId);
                            intent1.putExtra("fromReturn", Config.RETURN_CAR_LONG);
                            startActivity(intent1);
                        }
                        alertDialog.dismiss();
                    }
                }).show();
    }

    private void observeRxEventCode() {
        busSubscription = RxBus.getInstance()
                .toObservable(RxBusEvent.class)
                .subscribe(new Action1<RxBusEvent>() {
                    @Override
                    public void call(RxBusEvent rxBusEvent) {
                        int eventCode = rxBusEvent.getEventCode();
                        switch (eventCode) {
                            case RxEventCodes.SERVER_PUSH_RETURN_CAR://后台还车
                                Intent intent = new Intent(mContext, AllOrderActivity.class);
                                intent.putExtra("toMain", 1);
                                startActivity(intent);
                                break;
                            case RxEventCodes.CODE_SEARCH_PARK_FROM_USE_CAR://选择网点后
                                ParksResp parksResp = (ParksResp) rxBusEvent.getContent();
                                if (null != parksResp) {
                                    returnCarParkNaviView.setData(UseCarLongRentReturnActivity.this, parksResp.getParkName(),
                                            parksResp.getParkAddress(), parksResp.getLatlng());
                                    //记录该还车网点
                                    recordChoosePark(parksResp.getId());
                                }
                                break;
                            case RxEventCodes.CODE_LONG_RENT_BEFORE_FINISH_TIP://短租套餐到期之前提示
                                String endTime = "";
                                try {
                                    endTime = DateUtils.timestampToString(String.valueOf(orderEndTime), "MM月dd日 HH:mm分");
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                showTipDialog(String.format(Config.LONG_RENT_BEFORE_FINISH_TIP, days, endTime), "", "知道了", TIP_BEFORE_FINISH);
                                break;
                        }
                    }
                });
    }

    /**
     * 获取电池的剩余百分比
     *
     * @return
     */
    private int getBatteryLeftPercent(String battery) {
        int batteryPercent = 0;
        if (!StringUtils.isEmpty(battery)) {
            double batteryD = Double.valueOf(battery);
            if (batteryD > 100) {
                batteryD = 100;
            } else if (batteryD < 0) {
                batteryD = 0;
            }
            batteryPercent = (int) batteryD;
        }
        return batteryPercent;
    }

    /**
     * 复活的车辆信息
     */
    private void getCarParkInfo() {
        CarParkResp carParkResp = (CarParkResp) getIntent().getSerializableExtra("recoverData");
        if (null == carParkResp) {
            return;
        }
        carResp = carParkResp.getCarBaseInfo();
        if (carResp != null) {
            orderId = carResp.getCarSharingOrderNo();
            isRevival = true;
        }
        CarParkResp.BeforeHandPark beforeHandPark = carParkResp.getBeforehandPark();
        if (null != beforeHandPark) {
            returnCarParkNaviView.setData(UseCarLongRentReturnActivity.this, beforeHandPark.getParkName(),
                    beforeHandPark.getParkAddress(), beforeHandPark.getLatlng());
        }

        LongRentRecoverComboResp longRentRecoverComboResp = carParkResp.getPackageInfo();
        selectedCombo = new LongRentComboListResp();

        //春节短租套餐信息
        selectedCombo.setIsFestival(longRentRecoverComboResp.getIsFestival());
        LongRentRecoverComboResp.FestivalShareVo festivalShareVo = longRentRecoverComboResp.getFestivalShareVo();
        if (null != festivalShareVo) {
            LongRentComboListResp.FestivalShareVo festivalShareVo1 = new LongRentComboListResp.FestivalShareVo();
            festivalShareVo1.setContent(festivalShareVo.getContent());
            festivalShareVo1.setShareUrl(festivalShareVo.getShareUrl());
            festivalShareVo1.setTitle(festivalShareVo.getTitle());
            selectedCombo.setFestivalShareVo(festivalShareVo1);
        }
    }

    @Override
    public void onComplete(String result, int type) {
        if (isSuccess(result)) {
            if (type == FIND_CAR_CODE) {//找车
                showSuccessDialog("鸣笛成功");
            } else if (type == OPEN_DOOR_CODE) {//开锁
                showSuccessDialog("开锁成功");
            } else if (type == CLOSE_DOOR_CODE) {//锁门
                showSuccessDialog("锁门成功");
            } else if (type == POWER_OFF_CODE) {
                open_power.setImageResource(R.mipmap.yc_64);
                close_power.setImageResource(R.mipmap.yc_63);
                showSuccessDialog("关闭动力成功");
            } else if (type == POWER_ON_CODE) {
                open_power.setImageResource(R.mipmap.yc_63);
                close_power.setImageResource(R.mipmap.yc_64);
                showSuccessDialog("开启动力成功");
            } else if (type == USE_CAR_COST) {//计费
                UseCostLongResp useCostLongResp = getBean(result, UseCostLongResp.class);
                if (null == useCostLongResp) {
                    return;
                }

                long systemTime = useCostLongResp.getSystemTime();
                orderEndTime = useCostLongResp.getOrderEndTime();
//                long leftMinute = (orderEndTime - systemTime) / 1000 / 60;
                leftSeconds = (orderEndTime - systemTime) / 1000;
                timeDelta = systemTime - System.currentTimeMillis();
                Logger.i(TAG, "服务器和本地时间差=" + timeDelta);
                String useTimeStr = useCostLongResp.getUseTime();
                if (!StringUtils.isEmpty(useTimeStr)) {
                    useTime = Long.parseLong(useTimeStr);
                    long tmp = useTime;
                    useTime = useTime / 1000 / 60;//分钟
                    if (tmp % (1000 * 60) > 0) {
                        useTime += 1;
                    }
                    if (leftSeconds > 0) {//套餐内
                        useCarCostLongView.setComboData(useCostLongResp.getName(), orderEndTime + "", DateUtils.minuteToDay(useTime),
                                useCostLongResp.getOrderMileage(), useCostLongResp.getOrderPrePayMoney());
                        tvComboTime2.setText(Html.fromHtml("时长:<font color='#ffffff'>" + DateUtils.minuteToDay(useTime) + "</font>"));
                        tvComboMileage2.setText(Html.fromHtml("里程:<font color='#ffffff'>" + TVUtils.toString2(useCostLongResp.getOrderMileage()) + "公里" + "</font>"));
                        tvComboFee2.setText(Html.fromHtml("费用:<font color='#ffffff'>" + TVUtils.toString(useCostLongResp.getOrderPrePayMoney() / 100.0) + "元</font>"));
                    }
                }

                days = useCostLongResp.getDays();
                if (leftSeconds > 0) {//套餐内
                    longRentLeftTimeView.setTimeDown((int) leftSeconds);
                } else {//超出套餐时间
                    isOverTime = true;
                    //套餐已过，颜色为灰，时长为套餐的天数
                    useCarCostLongView.setComboOverData(useCostLongResp.getName(), orderEndTime + "", days,
                            useCostLongResp.getOrderMileage(), useCostLongResp.getOrderPrePayMoney());
                    tvComboTime2.setText(Html.fromHtml("时长:<font color='#ffffff'>" + days + "</font>"));
                    tvComboMileage2.setText(Html.fromHtml("里程:<font color='#ffffff'>" + TVUtils.toString2(useCostLongResp.getOrderMileage()) + "公里" + "</font>"));
                    tvComboFee2.setText(Html.fromHtml("费用:<font color='#ffffff'>" + TVUtils.toString(useCostLongResp.getOrderPrePayMoney() / 100.0) + "元</font>"));


                    //隐藏倒计时
                    longRentLeftTimeView.setVisibility(View.GONE);
                    //停止倒计时
                    longRentLeftTimeView.destroyView();
                    //超出部分按分时租赁计费
                    useCarCostViewContainer.setVisibility(View.VISIBLE);
                    useCarCostView.setData(useCostLongResp.getOutUseTime(), useCostLongResp.getOutTimeMilage(), useCostLongResp.getOutTimeCost());
                    //分时租赁帮助
                    useCarCostView.setOnUseCarHelpClickListener(new UseCarCostView.OnUseCarHelpClickListener() {
                        @Override
                        public void onUseCarHelpClick() {
                            showFeeUnitTip("分时租赁收费");
                        }
                    });

                    String overUseTimeStr = useCostLongResp.getOutUseTime();
                    if (!StringUtils.isEmpty(overUseTimeStr) && !StringUtils.isEmpty(days)) {
                        int overUseTime = Integer.parseInt(overUseTimeStr);
                        overUseTime = overUseTime / 1000 / 60;//分钟
                        useTime = Integer.parseInt(days) * 24 * 60 + overUseTime;//套餐内分钟 + 超时分钟
                    }
                }

                if (null != carResp) {
                    //电池剩余量
                    String battery = useCostLongResp.getBattery();
                    int batteryPercent = getBatteryLeftPercent(battery);
                    //车辆信息
                    carInfoView.updateBatteryData(batteryPercent, useCostLongResp.getEnduranceMileage());
                }
            } else if (type == QUERY_AUDIT_STATUS) {//查询停车费报销审核进度
                ParkingFeeAuditStatusResp resp = getBean(result, ParkingFeeAuditStatusResp.class);

                if (StringUtils.isEmpty(result)) {
                    //填写报销单
                    startToParkingFeeReturnActivity();
                } else {
                    //查看进度条
                    Intent intent = new Intent(mContext, ParkingFeeStatusActivity.class);
                    intent.putExtra("auditStatus", resp);
                    if (null != carResp) {
                        intent.putExtra("carNumber", carResp.getCarNumber());
                        intent.putExtra("orderId", orderId);
                    }
                    intent.putExtra("orderCategory", Config.ORDER_CATEGORY_LONG_RENT);
                    startActivity(intent);
                }
            }
        } else {
            if (type == FIND_CAR_CODE) {//找车
                showFailureDialog(2, "鸣笛失败", getMsg(result), "取消", "重试");
            } else if (type == OPEN_DOOR_CODE) {//开锁
                showFailureDialog(3, "开锁失败", getMsg(result), "取消", "重试");
            } else if (type == CLOSE_DOOR_CODE) {//锁门
                showFailureDialog(4, "锁门失败", getMsg(result), "取消", "重试");
            } else if (type == QUERY_AUDIT_STATUS) {//查询停车费报销审核进度
                //填写报销单
                startToParkingFeeReturnActivity();
            } else if (type == POWER_OFF_CODE) {
                showFailureDialog(5, "关闭动力失败", getMsg(result), "取消", "重试");
            } else if (type == POWER_ON_CODE) {
                showFailureDialog(6, "开启动力失败", getMsg(result), "取消", "重试");
            }
        }
    }

    @Override
    public void onFailure(String msg, int type) {
        if (type == FIND_CAR_CODE) {//找车
            showFailureDialog(2, "鸣笛失败", "当前网络不太好，请检查您的网络连接", "取消", "重试");
        } else if (type == OPEN_DOOR_CODE) {//开锁
            showFailureDialog(3, "开锁失败", "当前网络不太好，请检查您的网络连接", "取消", "重试");
        } else if (type == CLOSE_DOOR_CODE) {//锁门
            showFailureDialog(4, "锁门失败", "当前网络不太好，请检查您的网络连接", "取消", "重试");
        }
    }

    @OnClick({R.id.open_power_linear, R.id.close_power_linear, R.id.ivTitleLeft, R.id.llytTitleRight, R.id.tvWhistle, R.id.tvOpenDoor, R.id.tvCloseDoor, R.id.tvReturnCar, R.id.tvToRecharge, R.id.tv_refund_park_fee})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivTitleLeft:
                finishPage();
                break;
            case R.id.llytTitleRight://车辆位置
                Intent intent = new Intent(mContext, CarPositionActivity.class);
                intent.putExtra("carInfoCode", carResp);
                intent.putExtra("from", Config.USING_LONG_CAR_POSITIOIN);//车辆位置
                intent.putExtra("useTime", useTime + "");
                intent.putExtra("orderId", orderId);
                startActivity(intent);
                break;
            case R.id.tvWhistle://鸣笛
                if (null != carResp) {
                    controlCar(carResp.getCarNumber(), FIND_CAR);
                }
                break;
            case R.id.tvOpenDoor://开锁
                if (null != carResp) {
                    controlCar(carResp.getCarNumber(), OPEN_DOOR);
                }
                break;
            case R.id.tvCloseDoor:
                if (null != carResp) {
                    controlCar(carResp.getCarNumber(), CLOSE_DOOR);
                }
                break;
            case R.id.close_power_linear:
                if (null != carResp) {
                    controlCar(carResp.getCarNumber(), POWER_OFF);
                }
                break;
            case R.id.open_power_linear:
                if (null != carResp) {
                    controlCar(carResp.getCarNumber(), POWER_ON);
                }
                break;
            case R.id.tvReturnCar://还车
                if (isOverTime) {//套餐外不提示
                    Intent intent1 = new Intent(mContext, ChooseReturnParkActivity.class);
                    intent1.putExtra("orderId", orderId);
                    intent1.putExtra("fromReturn", Config.RETURN_CAR_LONG);
                    startActivity(intent1);
                } else {//套餐内提示
                    String endTime = "";
                    try {
                        endTime = DateUtils.timestampToString(String.valueOf(orderEndTime), "MM月dd日 HH:mm分");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    showTipDialog(String.format(Config.RETURN_CAR_LONG_TIP, endTime), "取消", "确定", TIP_RETURN_CAR);
                }
                break;
            case R.id.tvToRecharge://我要充电
                showTipDialog(Config.RECHARGE_TIP, "知道了", "我要充电", TIP_CHARGE_POWER);
                break;
            case R.id.tv_refund_park_fee://报销停车费
                getParkingFeeAuditStatus();
                break;
        }
    }

    /**
     * 控制车辆
     *
     * @param lpn  车牌号
     * @param type findCar-找车,openDoor-打开车门,closeDoor-关闭车门
     */
    public void controlCar(String lpn, String type) {
        ControlCarRequest request = new ControlCarRequest();
        request.setCarNumber(lpn);
        request.setOperateType(type);
        request.setMethod(RequestUrls.OPERATE_CAR);
        if (FIND_CAR.equals(type)) {
            doGet(request, FIND_CAR_CODE, "正在鸣笛...", true);
        } else if (OPEN_DOOR.equals(type)) {
            doGet(request, OPEN_DOOR_CODE, "正在开锁...", true);
        } else if (CLOSE_DOOR.equals(type)) {
            doGet(request, CLOSE_DOOR_CODE, "正在锁门...", true);
        } else if (POWER_OFF.equals(type)) {
            doGet(request, POWER_OFF_CODE, "正在关闭动力...", true);
        } else if (POWER_ON.equals(type)) {
            doGet(request, POWER_ON_CODE, "正在开启动力...", true);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {

            finishPage();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        longRentLeftTimeView.stopTime();
    }

    @Override
    protected void onDestroy() {
        longRentLeftTimeView.destroyView();
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void finishPage() {
        //跳转到复活的地图页面
        Intent intent = new Intent(mContext, CarPositionActivity.class);
        intent.putExtra("carInfoCode", carResp);
        intent.putExtra("from", Config.USING_LONG_RECOVER);//复活
        intent.putExtra("useTime", useTime + "");
        intent.putExtra("orderId", orderId);
        startActivity(intent);
    }

    /**
     * 获取短租费用
     */
    private void getFee() {
        UseCarCostRequest request = new UseCarCostRequest();
        request.setCarSharingOrderNumber(orderId);
        request.setMethod(RequestUrls.QUERY_USE_CAR_COST_LONG);
        doGet(request, USE_CAR_COST, "", false);
    }

    /**
     * 显示成功弹窗
     */
    private void showSuccessDialog(String text) {
        ToastUtil.showImage(this, text);
    }

    /**
     * 显示失败对话框
     */
    private void showFailureDialog(final int type, String title, String message, String negativeText, String positiveText) {
        final CustomAlertDialog2 dialog2 = CustomAlertDialog2.getAlertDialog(mContext, true, true);
        dialog2.setTitle(title);
        dialog2.setBackgroundImg(R.mipmap.dialog_failure_bg);
        dialog2.setMessage(message)
                .setOnNegativeClickListener(negativeText, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog2.dismiss();
                    }
                })
                .setOnPositiveClickListener(positiveText, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (2 == type) {//找车
                            if (null != carResp) {
                                controlCar(carResp.getCarNumber(), FIND_CAR);
                            }
                        } else if (3 == type) {//开锁
                            if (null != carResp) {
                                controlCar(carResp.getCarNumber(), OPEN_DOOR);
                            }
                        } else if (4 == type) {//锁门
                            if (null != carResp) {
                                controlCar(carResp.getCarNumber(), CLOSE_DOOR);
                            }
                        } else if (5 == type) {  //关动力
                            if (null != carResp) {
                                controlCar(carResp.getCarNumber(), POWER_OFF);
                            }
                        } else if (6 == type) {    //开动力
                            if (null != carResp) {
                                controlCar(carResp.getCarNumber(), POWER_ON);
                            }
                        }
                        dialog2.dismiss();
                    }
                })
                .show();
    }

    /**
     * 查询停车费报销审核进度
     */
    public void getParkingFeeAuditStatus() {
        if (carResp != null) {
            ParkingFeeAuditStatusRequest request = new ParkingFeeAuditStatusRequest();
            request.setOrderId(orderId);
            request.setOrderCategory(Config.ORDER_CATEGORY_LONG_RENT);
            request.setMethod(RequestUrls.QUERY_PARKING_FEE_AUDIT_STATUS);
            doGet(request, QUERY_AUDIT_STATUS, Config.LOADING_STRING, true);
        }
    }

    /**
     * 填写报销单
     */
    private void startToParkingFeeReturnActivity() {
        if (carResp != null) {
            Intent intent2 = new Intent(this, ParkingFeeReturnActivity.class);
            intent2.putExtra("carNumber", carResp.getCarNumber());
            intent2.putExtra("orderId", orderId);
            intent2.putExtra("orderCategory", Config.ORDER_CATEGORY_LONG_RENT);
            startActivity(intent2);
        }
    }

    /**
     * 记录选择的还车网点
     */
    private void recordChoosePark(String parkId) {
        RecordChooseParkRequest request = new RecordChooseParkRequest();
        request.setParkId(parkId);
        request.setOrderId(orderId);
        request.setMethod(RequestUrls.RECORD_CHOOSE_PARK);
        doGet(request, RECORD_CHOOSE_PARK, "", false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PermissionsConstant.REQUEST_LOCATION) {
                //提示打开定位权限
                toOpenLocatedPermission();
            }
        }
    }

    /**
     * 显示春节分享对话框
     */
    private void showSpringFestivalDialog() {
        new SpringFestivalPPW().showSpringFestivalPPW(this, selectedCombo.getFestivalShareVo());
    }
}
