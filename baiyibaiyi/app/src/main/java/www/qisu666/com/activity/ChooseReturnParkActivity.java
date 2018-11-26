package www.qisu666.com.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.fence.GeoFence;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import www.qisu666.com.R;
import www.qisu666.com.adapter.FenceInfoWindowAdapter;
import www.qisu666.com.app.CarOrderState;
import www.qisu666.com.app.OrderType;
import www.qisu666.com.app.SensorEventHelper;
import www.qisu666.com.app.UserState;
import www.qisu666.com.callback.AppointmentsReturnParkResp;
import www.qisu666.com.callback.BillLongRentResp;
import www.qisu666.com.callback.BillResp;
import www.qisu666.com.callback.GeoFenceDataResp;
import www.qisu666.com.callback.ParksResp;
import www.qisu666.com.callback.UserInfoResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.map.geoFence.GeoFenceManager;
import www.qisu666.com.map.route.RouteSearchManager;
import www.qisu666.com.receiver.PReceiver;
import www.qisu666.com.request.AppointmentsReturnParkRequest;
import www.qisu666.com.request.GeoFenceDataRequest;
import www.qisu666.com.request.ParkPointListRequest;
import www.qisu666.com.request.ReturnCarRequest;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.DataUtils;
import www.qisu666.com.utils.DateUtils;
import www.qisu666.com.utils.InstanceUtils;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.NavigationUtils;
import www.qisu666.com.utils.SBUtils;
import www.qisu666.com.utils.StringUtils;
import www.qisu666.com.utils.TVUtils;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.utils.UserUtils;
import www.qisu666.com.view.ChooseReturnParkView;
import www.qisu666.com.view.CustomAlertDialog;
import www.qisu666.com.view.CustomAlertDialog2;
import www.qisu666.com.view.ViewShowUtil;

import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 选择还车网点
 */
public class ChooseReturnParkActivity extends BaseActivity implements
        AMap.OnMarkerClickListener {
    private static final String TAG = ChooseReturnParkActivity.class.getSimpleName();
    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.tvTitleName)
    TextView tvTitleName;
    @BindView(R.id.rl_main_all)
    RelativeLayout rlMainAll;
    @BindView(R.id.chooseReturnParkView)
    ChooseReturnParkView chooseReturnParkView;

    private static final int QUERY_PARK_LIST = 1;//搜索网点数据
    private static final int RETURN_CAR = 2;//还车
    private static final int QUERY_GEO_FENCE_DATA = 3;//获取地理围栏数据
    private static final int APPOINTMENTS_RETURN_CAR_PARK = 4;//预约还车网点

    private AMap aMap;
    private Marker myMarker;
    private RouteSearchManager routeSearchManager;
    private List<ParksResp> parksResps = new ArrayList<>();//网点
    private List<Marker> markerAll = new ArrayList<>();
    private boolean isPosition = true;
    private int getDataCount = 0;
    private SensorEventHelper mSensorHelper;
    private boolean isFresh = false, isShowToast = true,
            showPgs = false;
    private PReceiver loginReceiver;
    private ParksResp curParksResp;//当前选中的网点数据
    private Marker curMarker;//当前选中的网点marker
    //    private Marker curShowWindowMarker;//保存当前点击的Marker，以便点击地图其他地方设置InfoWindow消失
    private String orderId;
    //    private boolean shouldShowInfoWindow = false;
    private FenceInfoWindowAdapter fenceInfoWindowAdapter;

    //判断是时租还车还是短租还车
    private int fromReturn = Config.RETURN_CAR_HOUR;
    private GeoFenceManager geoFenceManager;
    private boolean isQueryingFence = false;//是否正在请求地理围栏数据（有可能第二次请求的数据）
    private boolean isAppointmentsReturnCarPark = false;//是否需要预约还车网点
    private AppointmentsReturnParkResp appointmentsReturnParkResp;//预约还车数据
    private boolean isFirstGetAppointmentData = true;//是否是第一次请求预约数据

    //预约倒计时-------------------------------------------------------------------
    private static final int TIME_DOWN_COUNT = 1;
    //服务器当前时间
    private long systemTimeL;
    //预约时间
    private long orderTimeL;
    //服务器当前时间和手机当前时间之间的差
    private long timeDelta;

    private int timeDownAll = Config.TIME_DOWN;
    private int timeDown = Config.TIME_DOWN;
    private MyHandler handler;

    //预约倒计时----------------------------------------------------------------------
    private static class MyHandler extends Handler {
        WeakReference<ChooseReturnParkActivity> weakReference;

        public MyHandler(ChooseReturnParkActivity activity) {
            weakReference = new WeakReference<ChooseReturnParkActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIME_DOWN_COUNT:
                    if (null != weakReference) {
                        ChooseReturnParkActivity activity = weakReference.get();
                        if (activity != null) {
                            activity.timeDown = msg.arg1;
//                            Logger.i(TAG, "预约还车倒计时timeDown = " + activity.timeDown);
                            if (activity.timeDown <= 0) {
                                removeCallbacksAndMessages(null);
                                //隐藏倒计时
                                if (null != activity.curParksResp) {
                                    activity.chooseReturnParkView.setDiscountInfo(activity.curParksResp, "");
                                    //停留时间超过了，如果当前的网点还是预约的网点，那么要改变文字和修改为需要预约
                                    if (activity.curParksResp.getId().equals(activity.appointmentsReturnParkResp.getId())) {
                                        activity.chooseReturnParkView.setReturnCarButtonText("预约还车");
                                        activity.isAppointmentsReturnCarPark = true;//需要预约
                                    }
                                }
                            } else {
                                //倒计时
                                if (null != activity.curParksResp) {
                                    if (activity.curParksResp.getId().equals(activity.appointmentsReturnParkResp.getId())) {
                                        activity.chooseReturnParkView.setDiscountInfo(activity.curParksResp, DateUtils.time2MinuteSecond(activity.timeDown));
                                    } else {
                                        activity.chooseReturnParkView.setDiscountInfo(activity.curParksResp, "");
                                    }
                                }
                                activity.timeDown--;
                                Message message = activity.handler.obtainMessage();
                                message.what = TIME_DOWN_COUNT;
                                message.arg1 = activity.timeDown;
                                activity.handler.sendMessageDelayed(message, 1000);
                            }
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void setView() {
        setContentView(R.layout.activity_choose_return_car);
    }

    @Override
    public void initDatas() {
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        fromReturn = intent.getIntExtra("fromReturn", Config.RETURN_CAR_HOUR);
        initMap();
        getParkInfoCode();

        DataUtils.permissLoca(ChooseReturnParkActivity.this);

        tvTitleName.setText("选择网点还车");

        observeRxEventCode();
    }

    private void observeRxEventCode() {
        busSubscription = RxBus.getInstance()
                .toObservable(RxBusEvent.class)
                .subscribe(new Action1<RxBusEvent>() {
                    @Override
                    public void call(RxBusEvent rxBusEvent) {
                        int eventCode = rxBusEvent.getEventCode();
                        switch (eventCode) {
                            case RxEventCodes.SERVER_PUSH_CLOSE_DOOR_SUCCESS://关门成功
                                Logger.i(TAG, "关门成功");
                                doCheck(rxBusEvent.getContent().toString(), true);
                                break;
                            case RxEventCodes.SERVER_PUSH_DETECTION_SUCCESS://车辆检测成功
                                Logger.i(TAG, "车辆检测成功");
                                doCheck(rxBusEvent.getContent().toString(), true);
                                break;
                            case RxEventCodes.CODE_LOCATE_STATUS://定位结果
                                int code = (int) rxBusEvent.getContent();
                                locateResult(code);
                                break;
                            case RxEventCodes.CODE_IS_TIP_CAN_RETURN://是否显示“已进入还车范围”提示
                                if (null != myMarker) {
                                    int geoFenceStatus = (int) rxBusEvent.getContent();
                                    if (geoFenceStatus == GeoFence.STATUS_IN) {//在地理围栏内
                                        if (!myMarker.isInfoWindowShown()) {
                                            myMarker.showInfoWindow();
                                        }
                                        isAppointmentsReturnCarPark = false;
                                        chooseReturnParkView.setReturnCarButtonText("确认还车");

                                    } else if (geoFenceStatus == GeoFence.STATUS_OUT) {
                                        if (myMarker.isInfoWindowShown()) {
                                            myMarker.hideInfoWindow();
                                        }
                                        if (curParksResp != null && null != curParksResp.getDiscountLimit()) {//折扣网点
                                            chooseReturnParkView.setReturnCarButtonText("预约还车");
                                            isAppointmentsReturnCarPark = true;//需要预约
                                        } else {
                                            chooseReturnParkView.setReturnCarButtonText("确认还车");
                                            isAppointmentsReturnCarPark = false;
                                        }
                                    }
                                    //如果存在预约中的网点，并且当前选择的网点就是预约的网点，则不需要预约了
                                    if (appointmentsReturnParkResp != null && appointmentsReturnParkResp.getId() != null
                                            && curParksResp != null && appointmentsReturnParkResp.getId().equals(curParksResp.getId())) {
                                        chooseReturnParkView.setReturnCarButtonText("立即还车");
                                        isAppointmentsReturnCarPark = false;//不需要预约
                                        //接着倒计时

                                    }
                                }
                                break;
                        }
                    }
                });
    }

    /**
     * 更新全部数据
     */
    private void refreshAllDatas() {
        parksResps.clear();
        isPosition = true;
        getDataCount = 0;

        //重置数据
        curParksResp = null;
        curMarker = null;
        isFirstGetAppointmentData = true;

        setMyMarker();
        getParkInfoCode();
    }

    private void locationOK() {
        Logger.i("显示定位提示框");

        if (!TextUtils.isEmpty(CacheUtils.getIn().getStr(CacheUtils.locationOk))) {
            if (isPosition) {
                if (parksResps != null && parksResps.size() > 0) {
                    setParkView();
                    doUpdateMap();
                    isPosition = false;
                } else {
                    if (getDataCount <= 3) {//重试3次
                        getParkInfoCode();
                        getDataCount++;
                    } else {
                        if (isShowToast) {
                            showToast("当前区域无可用服务网点!");
                        }
                        isShowToast = false;
                    }
                }
            }
            Logger.i("关闭定位提示框");
        }
        Logger.i("开启GPS？showPgs==" + showPgs);
        if (showPgs) {
            showPgs = false;
        }
    }

    @Override
    public void onComplete(String result, int type) {
        if (isSuccess(result)) {
            if (type == QUERY_PARK_LIST) {//网点列表
                parksResps.clear();
                List<ParksResp> parks = getList(result, ParksResp.class);
                if (parks != null && parks.size() > 0) {
                    parksResps.addAll(parks);
                }
                sortParkDataNew(parksResps);
                Logger.i("网点数量 == " + parksResps.size());
                locationOK();

                //查询预约网点
                appointmentsReturnCarPark();

            } else if (type == RETURN_CAR) {//还车成功
                ToastUtil.showImage(mContext, "还车成功");
                Logger.i(TAG, "------还车成功------");
                UserInfoResp userInfoResp = CacheUtils.getIn().getUserInfo();
                if (null != userInfoResp) {
                    userInfoResp.setStatus(UserState.USER_STATUS_READY);
                    CacheUtils.getIn().save(userInfoResp);
                }

                if (fromReturn == Config.RETURN_CAR_HOUR) {//时租还车
                    Logger.i(TAG, "时租还车");
                    BillResp billResp = getBean(result, BillResp.class);
                    returnHourSuccess(billResp);
                } else if (fromReturn == Config.RETURN_CAR_LONG) {//短租还车
                    //订单完成，短租提示全部结束
                    CacheUtils.getIn().setLongRentTipFlag(mContext, 0);
                    Logger.i(TAG, "短租还车");
                    BillLongRentResp billLongRentResp = getBean(result, BillLongRentResp.class);
                    returnLongSuccess(billLongRentResp);
                }
                //更新首页数据
                SBUtils.send(null, SBUtils.UPDATE_MAP_PARKS_INFO);
                finish();
            } else if (type == QUERY_GEO_FENCE_DATA) {//获取地理围栏数据成功
                if (!isQueryingFence) {
                    return;
                }
                GeoFenceDataResp resp = getBean(result, GeoFenceDataResp.class);
                if (null != resp) {
                    LatLngBounds.Builder builder = LatLngBounds.builder();
                    //是否存在电子围栏(0存在，1不存在)
                    int isElectronicFence = resp.getIsElectronicFence();
                    Logger.i(TAG, "isElectronicFence=" + isElectronicFence);
                    if (0 == isElectronicFence) {
                        Logger.i(TAG, "listRail.size==" + resp.getListRail().size());
                        ArrayList<LatLng> latLngs = new ArrayList<>();
                        for (GeoFenceDataResp.Rail rail : resp.getListRail()) {
                            Double lat = rail.getLatitude();
                            Double lng = rail.getLongitude();
                            LatLng latLng = new LatLng(lat, lng);
                            latLngs.add(latLng);
                            //加入多边形围栏的经纬度
                            builder.include(latLng);
                        }
                        geoFenceManager.addGeoFencePolygon(latLngs);
                    } else if (1 == isElectronicFence) {
                        float radius = resp.getRailRadius();
                        LatLng latLng = curParksResp.getLatlng();
                        //加入圆心
                        builder.include(latLng);
                        geoFenceManager.addGeoFenceCircle(latLng, radius);
                        double[] minMaxLatLng = DataUtils.getAround(Double.valueOf(curParksResp.getLatitude()), Double.valueOf(curParksResp.getLongitude()), radius);
                        //加入左上角和右下角的经纬度
                        builder.include(new LatLng(minMaxLatLng[0], minMaxLatLng[1]));
                        builder.include(new LatLng(minMaxLatLng[2], minMaxLatLng[3]));
                    }

                    LatLng myLatlng = new LatLng(Double.valueOf(application.latitude), Double.valueOf(application.longitude));
                    builder.include(myLatlng);
                    aMap.animateCamera(CameraUpdateFactory
                            .newLatLngBounds(builder.build(), 200));

                }
                isQueryingFence = false;
                chooseReturnParkView.setEnabled(true);
            } else if (type == APPOINTMENTS_RETURN_CAR_PARK) {//预约还车网点
                appointmentsReturnParkResp = getBean(result, AppointmentsReturnParkResp.class);
                if (null != appointmentsReturnParkResp && !StringUtils.isEmpty(appointmentsReturnParkResp.getId())) {//有预约网点
                    isAppointmentsReturnCarPark = false;//预约成功后，标记不需要预约
                    chooseReturnParkView.setReturnCarButtonText("立即还车");

                    //第一次刚进入地图页时，如果有预约网点,移动到预约的网点
                    if (isFirstGetAppointmentData) {
                        isFirstGetAppointmentData = false;
                        for (int i = 0; i < parksResps.size(); i++) {
                            ParksResp parksResp = parksResps.get(i);
                            if (null != parksResps && parksResp.getId() != null && parksResp.getId().equals(appointmentsReturnParkResp.getId())) {
                                if (i == 0) {
                                    //该预约网点是离我最近的网点
                                    parksResp.setNearest(true);
                                }
                                //找到预约的网点
                                showAPark(parksResp, i);
                                break;
                            }
                        }
                    }
                    if (curParksResp != null) {
                        curParksResp.setDiscountLimit(appointmentsReturnParkResp.getDiscountLimit());
                    }

                    //停止之前的倒计时，重新倒计时
                    if (null != handler) {
                        handler.removeCallbacksAndMessages(null);
                    }
                    String systemS = appointmentsReturnParkResp.getSystemTime();
                    String orderTimeS = appointmentsReturnParkResp.getOrderTime();
                    if (!StringUtils.isEmpty(systemS)) {
                        systemTimeL = Long.parseLong(systemS);
                    }
                    if (!StringUtils.isEmpty(orderTimeS)) {
                        orderTimeL = Long.parseLong(orderTimeS);
                    }
                    timeDelta = systemTimeL - System.currentTimeMillis();
                    //倒计时总时长
                    String orderSubsistNum = appointmentsReturnParkResp.getOrderSubsistNum();
                    if (!StringUtils.isEmpty(orderSubsistNum)) {
                        timeDownAll = Integer.parseInt(orderSubsistNum) * 60;
                    }
                    //倒计时
                    timeDown();

                } else {//没有预约网点
                    //第一次刚进入地图页时，如果没有预约网点,移动到离我最近的网点
                    if (isFirstGetAppointmentData) {
                        isFirstGetAppointmentData = false;
                        moveToFirstPark();
                    }
                }
            }
        } else {
            if (type == RETURN_CAR) {//还车
                if (Config.REQUEST_ORDER_NOT_EXIST.equals(getCode(result))) {//订单不存在
                    UserInfoResp mUser = CacheUtils.getIn().getUserInfo();
                    if (mUser != null) {
                        mUser.setStatus(UserState.USER_STATUS_READY);
                        CacheUtils.getIn().save(mUser);
                    }

                    activityUtil.jumpTo(ControlerActivity.class);
                    finish();
                } else {
//                    showFailureDialog(1, "还车失败", getMsg(result), "取消", "重试");
                    if ("请将车开到您选择的网点进行还车".equals(getMsg(result))) {
                        showReturnCarTip(2, "请将车开到您选择的网点进行还车", null, "我已在还车点", "知道了");
                    } else if ("车辆通讯故障,请稍后再试.".equals(getMsg(result))) {
                        showReturnCarTip(3, "车辆通讯故障,请稍后再试.", null, "申请还车", "知道了");
                    }
                }
            } else if (type == QUERY_GEO_FENCE_DATA) {//获取地理围栏数据成功失败，但请求完成
                isQueryingFence = false;
                chooseReturnParkView.setEnabled(true);
            } else if (type == APPOINTMENTS_RETURN_CAR_PARK) {//查询预约还车网点失败
                if (isFirstGetAppointmentData) {
                    isFirstGetAppointmentData = false;
                    //第一次刚进入地图页时，如果出错，则移动到离我最近的一个网点
                    moveToFirstPark();
                }
                if ("600301".equals(getCode(result))) {
                    ToastUtil.show(mContext, getMsg(result));
                }
            }
        }
    }

    @Override
    public void onFailure(String msg, int type) {
        if (type == RETURN_CAR) {//还车
            showFailureDialog(1, "还车失败", "出问题了，请稍后重试！", "取消", "重试");
        } else if (type == QUERY_GEO_FENCE_DATA) {//获取地理围栏数据成功失败，但请求完成
            isQueryingFence = false;
            if (null != chooseReturnParkView) {
                chooseReturnParkView.setEnabled(true);
            }
        } else if (type == APPOINTMENTS_RETURN_CAR_PARK) {//查询预约还车网点失败
            if (isFirstGetAppointmentData) {
                isFirstGetAppointmentData = false;
                //第一次刚进入地图页时，如果出错，则移动到离我最近的一个网点
                moveToFirstPark();
            }
        }
    }

    private void moveToFirstPark() {
        //移动到第一个网点
        if (parksResps != null && parksResps.size() > 0) {
            for (int i = 0, size = parksResps.size(); i < size; i++) {
                final ParksResp parksResp = parksResps.get(i);
                if (parksResp == null) {
                    continue;
                }
                parksResp.setNearest(true);//离我最近
                boolean b = showAPark(parksResp, i);
                if (!b) {
                    continue;
                }
                break;
            }
        }
    }

    private boolean showAPark(ParksResp parksResp, int index) {

        Marker marker = getMarker(parksResp.getParkName());
        if (null == marker) {
            return false;
        }

        setDataAndRoute(parksResp, marker);
        //获取地理围栏
        queryGeoFenceData();

        chooseReturnParkView.setVisibility(View.VISIBLE);
        chooseReturnParkView.setOnReturnCarClickListener(new ChooseReturnParkView.OnReturnCarClickListener() {
            @Override
            public void onReturnCarClick(@Nullable ParksResp parksResp) {
                if (null == parksResp) {
                    return;
                }
                if (isAppointmentsReturnCarPark) {//需要预约
                    if (appointmentsReturnParkResp != null) {
                        String orderSubsistNum = appointmentsReturnParkResp.getOrderSubsistNum();
                        String tip = String.format(Config.APPOINTMENT_RETURN_CAR_TIP, orderSubsistNum);
                        SpannableStringBuilder ssb = new SpannableStringBuilder(tip);
                        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
                        ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.parseColor("#02b2e4"));
                        ssb.setSpan(blueSpan, tip.indexOf(orderSubsistNum), orderSubsistNum.length() + tip.indexOf(orderSubsistNum), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        showReturnCarTip(4, "", ssb, "取消", "确定预约");
                    }
                } else {//直接还车，不需要预约
                    showReturnCarTip(1, Config.RETURN_CAR_TIP, null, "取消", "确认还车");
                }
            }
        });
        chooseReturnParkView.setOnNavigateClickListener(new ChooseReturnParkView.OnNavigateClickListener() {
            @Override
            public void onNavigateClick(@Nullable ParksResp parksResp) {
                if (null == parksResp) {
                    return;
                }
                String lat = parksResp.getLatitude();
                String lng = parksResp.getLongitude();
                if (StringUtils.isIntOrFloat(lat) && StringUtils.isIntOrFloat(lng)) {
                    LatLng latLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
                    NavigationUtils.goNavigation(ChooseReturnParkActivity.this, latLng, 0);
                }
            }
        });
        moveMarker(index);
        return true;
    }

    /**
     * 时租还车成功
     *
     * @param billResp
     */
    private void returnHourSuccess(BillResp billResp) {
        if (null != billResp) {
            UserInfoResp userInfoResp = CacheUtils.getIn().getUserInfo();
            if (null != userInfoResp) {
                UserInfoResp.Company company = userInfoResp.getCompany();
                if (null != company) {
                    Intent intent = new Intent(mContext, ReturnCarSuccessBillCompanyActivity.class);
                    intent.putExtra("billResp", billResp);
                    startActivity(intent);
                } else {
                    if (CarOrderState.Car_Order_Status_Finish.equals(billResp.getCarSharingPayStatus())) {
                        toComment(billResp.getCarSharingOrderNumber());
                    } else {
                        Intent intent = new Intent(mContext, ReturnCarSuccessBillActivity.class);
                        intent.putExtra("billResp", billResp);
                        startActivity(intent);
                    }
                }
            }
        }
    }

    /**
     * 短租还车成功
     *
     * @param billLongRentResp
     */
    private void returnLongSuccess(BillLongRentResp billLongRentResp) {
        if (null != billLongRentResp) {
            if (!CarOrderState.Car_Order_Status_Finish.equals(billLongRentResp.getFinishPayStatus())) {
                Intent intent = new Intent(mContext, ReturnCarSuccessBillLongRentActivity.class);
                intent.putExtra("billLongRentResp", billLongRentResp);
                startActivity(intent);
            } else {
                toComment(billLongRentResp.getRentOrderId());
            }
        }
    }

    private void toComment(String orderNo) {
        Bundle bundle = new Bundle();
        bundle.putString("orderNo", orderNo);
        if (fromReturn == Config.RETURN_CAR_HOUR) {//时租还车
            bundle.putString("type", OrderType.CAR_ORDER);
            bundle.putString("orderType", "");//订单类型，时租可不传，红包用到
        } else {
            bundle.putString("type", Config.ORDER_CATEGORY_LONG_RENT);
            bundle.putString("orderType", Config.ORDER_CATEGORY_LONG_RENT);//订单类型，红包用到
        }
        bundle.putInt("showTip", 1);//是否显示支付成功提示,1:显示
        bundle.putInt("fromPage", 1);

        //评价后列表页面刷新
        Intent intent = new Intent(this, CommentsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 网点排序
     *
     * @param
     */
    private void sortParkDataNew(final List<ParksResp> parksResps) {
        if (parksResps != null && parksResps.size() > 0) {
            Collections.sort(parksResps, new Comparator<ParksResp>() {
                @Override
                public int compare(ParksResp lhs, ParksResp rhs) {
                    Double d1 = InstanceUtils.insDouble(lhs.getLongitude(),
                            lhs.getLatitude());
                    if (d1 > 1000) {
                        lhs.setDistance(TVUtils.toString(d1 / 1000.00) + "KM");
                    } else {
                        lhs.setDistance(TVUtils.toStringInt(d1 + "") + "m");
                    }
                    Double d2 = InstanceUtils.insDouble(rhs.getLongitude(),
                            rhs.getLatitude());
                    if (d2 > 1000) {
                        rhs.setDistance(TVUtils.toString(d2 / 1000.00) + "KM");
                    } else {
                        rhs.setDistance(TVUtils.toStringInt(d2 + "") + "m");
                    }

                    return d1.compareTo(d2);
                }
            });
        }
    }

    private void setParkView() {
        aMap.clear();
        setParkMarker();
    }

    private void getParkInfoCode() {
        if (!isFresh) {
            isFresh = false;
        }
        parksResps.clear();
        getReturnParks();
    }

    /**
     * 获取还车网点
     */
    private void getReturnParks() {
        ParkPointListRequest data = new ParkPointListRequest();
        data.setCityCode(UserUtils.getCityCode());
        data.setMethod(RequestUrls.QUERY_RETURN_PARK);
        doGet(data, QUERY_PARK_LIST, "获取附近网点...", true);
    }

    /**
     * 移动Marker
     *
     * @param positionMarker
     */
    private void moveMarker(int positionMarker) {
        if (parksResps != null && parksResps.size() > 0) {
            ParksResp pic = parksResps.get(positionMarker);
            if (pic != null) {
                if (StringUtils.isIntOrFloat(pic.getLatitude())
                        && StringUtils.isIntOrFloat(pic.getLongitude())) {
                    LatLng mLatLng = new LatLng(Double.parseDouble(pic.getLatitude()),
                            Double.parseDouble(pic.getLongitude()));
                    aMap.animateCamera(CameraUpdateFactory.changeLatLng(mLatLng));
                }
            }
        }
    }

    /**
     * 设置停车场位置
     *
     * @param
     */
    public void setParkMarker() {
        aMap.clear();
        markerAll.clear();
        setMyMarker();
        if (parksResps != null && parksResps.size() > 0) {
            for (int i = 0, size = parksResps.size(); i < size; i++) {
                bindMarkerData(i);
            }
        }
    }

    /**
     * 给marker绑定数据
     *
     * @param i
     */
    private void bindMarkerData(int i) {
        MarkerOptions markerOptions = null;
        ParksResp mParkInfo = parksResps.get(i);
        String latitude = mParkInfo.getLatitude();
        String longitude = mParkInfo.getLongitude();
        String parkName = mParkInfo.getParkName();
        if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
            LatLng mLatLng = new LatLng(DataUtils.toDouble(latitude),
                    DataUtils.toDouble(longitude));
            markerOptions = new MarkerOptions();
            markerOptions.anchor(0.5f, 0.5f);
            markerOptions.title(parkName);
            markerOptions.position(mLatLng);
            markerOptions.period(i + 1);
            //设置网点的有车和无车marker
            setMarkerType(markerOptions, i);
        }
        Marker marker = aMap.addMarker(markerOptions);
        if (null != marker) {
            marker.setObject(mParkInfo);
            markerAll.add(marker);
        }
    }

    /**
     * 设置marker的类型：有车和无车
     *
     * @param markerOptions
     * @param i
     */
    private void setMarkerType(MarkerOptions markerOptions, int i) {
        ParksResp parksResp = parksResps.get(i);
        if (parksResp == null) {
            return;
        }

        View view = ViewShowUtil.getUnselectedReturnMarker(mContext, parksResp.getDiscountLimit());
        markerOptions.icon(BitmapDescriptorFactory.fromView(view)).draggable(true);
    }

    /**
     * 地图相关方法
     */
    private void initMap() {
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        mSensorHelper = new SensorEventHelper(mContext);
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
        UiSettings uiSettings = aMap.getUiSettings();
        //去掉缩放按钮
        uiSettings.setZoomControlsEnabled(false);//去掉放大缩小按钮
        uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示

        aMap.setOnMarkerClickListener(this);
//        aMap.setOnMapClickListener(this);
        fenceInfoWindowAdapter = new FenceInfoWindowAdapter(ChooseReturnParkActivity.this);
        aMap.setInfoWindowAdapter(fenceInfoWindowAdapter);
//        aMap.setOnCameraChangeListener(new MyOnCameraChangeListener());

        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        if (!TextUtils.isEmpty(CacheUtils.getIn().getStr(CacheUtils.locationOk))) {
//            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UserUtils.getLatLng(), 11f));
            setMyPosition();
        }
        geoFenceManager = new GeoFenceManager(mContext, aMap, GeoFenceManager.STROKE_COLOR, GeoFenceManager.FILL_COLOR, GeoFenceManager.STROKE_WIDTH);
    }

    private void doUpdateMap() {
        if (mapView != null) {
            mapView.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    mapZoom();
                }
            }, 1700);
        }
    }

    private void mapZoom() {
        aMap.animateCamera(CameraUpdateFactory.zoomTo(Config.MAP_ZOOM_13), 500,
                new AMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        aMap.stopAnimation();
                    }

                    @Override
                    public void onCancel() {
                    }
                });
    }

//    public class MyOnCameraChangeListener implements AMap.OnCameraChangeListener {
//
//
//        @Override
//        public void onCameraChange(CameraPosition cameraPosition) {
//
//        }
//
//        @Override
//        public void onCameraChangeFinish(CameraPosition cameraPosition) {
//            Logger.i(TAG, "onCameraChangeFinish");
////            if (curShowWindowMarker != null && shouldShowInfoWindow) {
////                curShowWindowMarker.showInfoWindow();
////            }
//        }
//    }

    @OnClick({R.id.ivTitleLeft, R.id.ivRefreshMap, R.id.ivLocateMyPosition})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivTitleLeft:
                finish();
                break;
            case R.id.ivRefreshMap://刷新地图
                refreshAllDatas();
                break;
            case R.id.ivLocateMyPosition:
                setMyPosition();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
        if (mapView != null) {
            mapView.onDestroy();
        }

        try {
            unregisterReceiver(loginReceiver);
        } catch (Exception e) {
            loginReceiver = null;
        }
        if (null != geoFenceManager) {
            //停止服务
            geoFenceManager.onDestroy();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    public void setMyPosition() {
//        doUpdateMap();

        LatLng myLatLng = new LatLng(Double.parseDouble(application.latitude),
                Double.parseDouble(application.longitude));
        Marker marker = getMarker("my");
        if (marker != null) {
            marker.setPosition(myLatLng);
        }
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, Config.MAP_ZOOM_18));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 设置我的位置Marker
     */
    private void setMyMarker() {
        if (myMarker != null) {
            myMarker.remove();
        }
        myMarker = ViewShowUtil.setMyMarker(aMap);
        if (myMarker != null) {
            myMarker.setZIndex(parksResps.size() + 1);
            mSensorHelper.setCurrentMarker(myMarker);
        }
    }

    /**
     * 定位结果
     *
     * @param code
     */
    public void locateResult(int code) {
        Logger.i(TAG, "收到定位结果=" + code);
        if (code == 12) {//定位错误
            DataUtils.permissLoca(ChooseReturnParkActivity.this);
        }
        if (myMarker != null) {
            LatLng lng = new LatLng(DataUtils.toDouble(application.latitude),
                    DataUtils.toDouble(application.longitude));
            myMarker.setPosition(lng);
        }
        locationOK();
        if (curParksResp != null && curMarker != null) {
            setDataAndRoute(curParksResp, curMarker);
        }
    }

//    @Override
//    public void onMapClick(LatLng latLng) {
//        Logger.e("点击地图：" + latLng);
//        hideInfoWindow();
////        shouldShowInfoWindow = false;
//
//    }

    private Marker getMarker(String obj) {
//        List<Marker> mapScreenMarkers = aMap.getMapScreenMarkers();
        List<Marker> mapScreenMarkers = markerAll;

        for (int i = 0; i < mapScreenMarkers.size(); i++) {
            Marker marker = mapScreenMarkers.get(i);
            String title = marker.getTitle();
            if (title != null && title.equals(obj)) {
                return marker;
            }
        }
        return null;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Logger.e(marker.getPeriod() + " 点击");
        if ("my".equals(marker.getTitle())) {
            return true;
        }
//        hideInfoWindow();
//        marker.showInfoWindow();
//        curShowWindowMarker = marker;//保存当前点击的Marker，以便点击地图其他地方设置InfoWindow消失
        ParksResp parksResp = (ParksResp) marker.getObject();
        if (null != parksResp) {
            if (!marker.equals(curMarker)) {
                Logger.i(TAG, "点击的不是同一个网点");
                setDataAndRoute(parksResp, marker);
                //获取地理围栏
                queryGeoFenceData();
            } else {
                Logger.i(TAG, "点击的是同一个网点");
            }
        }

        //不弹窗
        return true;
    }

    /**
     * 隐藏地图的InfoWindow
     */
//    private void hideInfoWindow() {
//        //隐藏marker的infoWindow
//        if (aMap != null && curShowWindowMarker != null) {
//            if (curShowWindowMarker.isInfoWindowShown()) {
//                curShowWindowMarker.hideInfoWindow();
//            }
//        }
//    }

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
                        returnCar(curParksResp);
                        dialog2.dismiss();
                    }
                });
        if (!isFinishing()) {
            dialog2.show();
        }
    }

    /**
     * 还车
     *
     * @param parksResp
     */
    public void returnCar(ParksResp parksResp) {
        Logger.i(TAG, "还车的网点==" + parksResp.getParkName());

        ReturnCarRequest request = new ReturnCarRequest();
        request.setCustomerId(UserUtils.getCustomerId());
        request.setLatitude(UserUtils.getLatitude());
        request.setLongitude(UserUtils.getLongitude());
        if (fromReturn == Config.RETURN_CAR_HOUR) {//时租还车
            request.setCarSharingOrderNumber(orderId);
            request.setMethod(RequestUrls.USER_PARK_RETURN_CAR);
        } else if (fromReturn == Config.RETURN_CAR_LONG) {//短租还车
            request.setCarRentOrderNumber(orderId);
            request.setMethod(RequestUrls.QUERY_LONG_RENT_RETURN_CAR);
        }
        request.setDepotId(parksResp.getId());
        request.setIsCheck("1");
        request.setEnjoyDiscounts("true");
        doGet(request, RETURN_CAR, "正在检测车辆状态", true);
    }

    /**
     * 还车前提示
     */
    private void showReturnCarTip(final int type, final String msg, SpannableStringBuilder ssb, String negativeText, String positiveText) {
        final CustomAlertDialog alertDialog = CustomAlertDialog.getAlertDialog(this, false, false);
        if (type == 4) {//预约提示
            alertDialog.setMessage(ssb);
        } else {
            alertDialog.setMessage(msg);
        }
        alertDialog.setBtnCancelColor(R.color.main_background)
                .setBtnConfirmColor(R.color.new_primary)
                .setOnNegativeClickListener(negativeText, new View.OnClickListener() {
                    @Override                                                                                       
                    public void onClick(View v) {
                        if (type == 2 || type == 3) {//还车失败
                            Intent intent = new Intent(mContext, ConnectReturnCarActivity.class);
                            intent.putExtra("orderId", orderId);
                            intent.putExtra("cause", msg);
                            startActivity(intent);
                        }
                        alertDialog.dismiss();
                    }
                })
                .setOnPositiveClickListener(positiveText, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (type == 1) {//点击还车按钮之后的再次确认提示
                            returnCar(curParksResp);
                        } else if (type == 4) {//预约提示
                            appointmentsReturnCarPark();
                        }

                        alertDialog.dismiss();
                    }
                }).show();
    }

    /**
     * 设置选择的网点的数据，并且规划路线
     *
     * @param parksResp
     * @param selectedMarker 终点，选中的marker
     */
    private void setDataAndRoute(ParksResp parksResp, Marker selectedMarker) {
        chooseReturnParkView.setEnabled(false);
        chooseReturnParkView.setData(parksResp);

        LatLng latLng = parksResp.getLatlng();
        LatLonPoint startPoint = new LatLonPoint(Double.valueOf(application.latitude), Double.valueOf(application.longitude));
        LatLonPoint endPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        if (null == routeSearchManager) {
            routeSearchManager = new RouteSearchManager();
        }
        if (selectedMarker != null) {
            if (null != curMarker) {
                //之前选中的网点恢复没选中状态
                curMarker.setIcon(BitmapDescriptorFactory.fromView(ViewShowUtil.getUnselectedReturnMarker(mContext, curParksResp.getDiscountLimit())));
            }
            //记录当前选中的网点
            curParksResp = parksResp;
            curMarker = selectedMarker;
            //选中状态
            selectedMarker.setIcon(BitmapDescriptorFactory.fromView(ViewShowUtil.getSelectedReturnMarker(mContext, parksResp.getDiscountLimit())));
            //规划路线
            routeSearchManager.route(mContext, aMap, startPoint, endPoint, myMarker, selectedMarker, RouteSearchManager.DRIVE_ROUTE);
        }
        chooseReturnParkView.setDiscountInfo(curParksResp, "");
    }

    /**
     * 获取地理围栏的数据
     */
    private void queryGeoFenceData() {
        //先移除地理围栏
        RxBusEvent event = new RxBusEvent();
        event.setEventCode(RxEventCodes.CODE_REMOVE_GEO_FENCE);
        RxBus.getInstance().post(event);
        //隐藏infoWindow
        if (null != myMarker && myMarker.isInfoWindowShown()) {
            myMarker.hideInfoWindow();
        }
        if (null != curParksResp) {
            isQueryingFence = true;
            GeoFenceDataRequest request = new GeoFenceDataRequest(curParksResp.getId());
            request.setMethod(RequestUrls.QUERY_GEO_FENCE_DATA);
            doGet(request, QUERY_GEO_FENCE_DATA, "", false);
        }
    }

    /**
     * 预约还车网点
     */
    private void appointmentsReturnCarPark() {
        String parkId = "";
        if (null != curParksResp) {
            parkId = curParksResp.getId();
        }
        AppointmentsReturnParkRequest request = new AppointmentsReturnParkRequest(orderId, parkId);
        request.setMethod(RequestUrls.APPOINTMENTS_RETURN_CAR_PARK);
        doGet(request, APPOINTMENTS_RETURN_CAR_PARK, Config.LOADING_STRING, true);
    }

    private void timeDown() {
        timeDown = timeDownAll - (int) ((System.currentTimeMillis() + timeDelta) / 1000 - orderTimeL / 1000);
        //倒计时
        handler = new MyHandler(this);
        Message msg = handler.obtainMessage();
        msg.what = TIME_DOWN_COUNT;
        msg.arg1 = timeDown - 1;
        handler.sendMessage(msg);
    }
}