package www.qisu666.com.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import www.qisu666.com.BuildConfig;
import www.qisu666.com.R;
import www.qisu666.com.adapter.ParkInfoWindowOrderingAdapter;
import www.qisu666.com.adapter.ParkInfoWindowUsingAdapter;
import www.qisu666.com.app.MyApplication;
import www.qisu666.com.app.SensorEventHelper;
import www.qisu666.com.app.UserState;
import www.qisu666.com.callback.CarResp;
import www.qisu666.com.callback.ParkResp;
import www.qisu666.com.callback.SecData;
import www.qisu666.com.callback.SystemConfigResp;
import www.qisu666.com.callback.UseCostLongResp;
import www.qisu666.com.callback.UseFeeResp;
import www.qisu666.com.callback.UserInfoResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.event.DrawalayoutEvent;
import www.qisu666.com.receiver.PublicReceiver;
import www.qisu666.com.request.SystemArgumentRequest;
import www.qisu666.com.request.UseCarCostRequest;
import www.qisu666.com.request.UserInfoRequest;
import www.qisu666.com.request.VerifyInfoRequest;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.DataUtils;
import www.qisu666.com.utils.FileSizeUtil;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.StringUtils;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.utils.UploadFile;
import www.qisu666.com.utils.UserUtils;
import www.qisu666.com.view.CircleImageView;
import www.qisu666.com.view.CustomAlertDialog;
import www.qisu666.com.view.CustomAlertDialogVerify;
import www.qisu666.com.view.ViewShowUtil;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 预约中车辆位置
 */
public class CarPositionActivity extends BaseActivity implements AMap.OnMarkerClickListener {
    private static final String TAG = CarPositionActivity.class.getSimpleName();
    private static final int USE_CAR_LOCATION = 1;
    private static final int SYSTEM_PARAM = 2;

    @BindView(R.id.titleBack)
    RelativeLayout titleBack;
    @BindView(R.id.titleMain)
    RelativeLayout titleMain;
    @BindView(R.id.tvTitleRight)
    TextView tvTitleRight;
    @BindView(R.id.tvTitleName)
    TextView tvTitleName;
    @BindView(R.id.ivTitleName)
    ImageView ivTitleName;
    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.ivLocateMyPosition)
    ImageView ivLocateMyPosition;
    @BindView(R.id.ivRefreshMap)
    ImageView ivRefreshMap;
    @BindView(R.id.ivTitleInfo)
    ImageView ivTitleInfo;
    @BindView(R.id.ivTitleInfoUnread)
    ImageView ivTitleInfoUnread;
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.left_layout)
    LinearLayout left_layout;
    @BindView(R.id.setting_linear)
    LinearLayout setting_linear;
    @BindView(R.id.goods_linear)
    LinearLayout goods_linear;
    @BindView(R.id.money_backage_linear)
    LinearLayout money_backage_linear;
    @BindView(R.id.order_linear)
    LinearLayout order_linear;
    @BindView(R.id.rz_linear)
    LinearLayout rz_linear;
    @BindView(R.id.wz_linear)
    LinearLayout wz_linear;
    @BindView(R.id.companny_linear)
    LinearLayout companny_linear;
    @BindView(R.id.person_re)
    RelativeLayout person_re;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.iv_myhead_logo)
    CircleImageView iv_myhead_logo;


    private static final int QUERY_USER_INFO = 1;
    private String picname;
    private File mPicFile;
    public static final int NONE = 0;
    public static final int PHOTOHRAPH = 1;// 拍照
    public static final int PHOTORESOULT = 3;// 结果
    private String violateNum = "";//违章数量
    private boolean needVerify = true;//是否需要认证
    private int driverNumberStatus;//驾照认证状态 1:驳回，2，3:过期
    private int deposit;//已交押金
    private int shouldDeposit;//应交押金
    private int verifyStatus = 3;//身份证和驾照 1：已认证，2：审核中，3：未认证
    private String myCompanyName = "";//公司名称
    private String myLeftAmount = "";//企业额度
    private AMap aMap;
    private SensorEventHelper mSensorHelper;
    private boolean positioning = true;
    private PublicReceiver systemReceiver;
    private ParkResp parkInfo;
    private CarResp carInfoCode;
    private int from = Config.ORDERING_HOUR_RECOVER;
    private int timeDown;
    private int useTime;//分钟
    private String orderId;
    private String carNumber;
    private ParkInfoWindowOrderingAdapter parkInfoWindowOrderingAdapter;
    private ParkInfoWindowUsingAdapter parkInfoWindowUsingAdapter;
    private Marker preMarker;

    private MyHandler handler;

    private List<Integer> fromOrderings;
    private List<Integer> fromUsings;
    private List<Integer> fromRecovers;
    private List<Integer> fromCarPositions;
    private List<Integer> fromUsingsLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        application = MyApplication.getApplication();
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return "my".equals(marker.getTitle());
    }

    private static class MyHandler extends Handler {
        WeakReference<CarPositionActivity> weakReference;

        public MyHandler(CarPositionActivity activity) {
            weakReference = new WeakReference<CarPositionActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (null != weakReference) {
                        CarPositionActivity activity = weakReference.get();
                        if (activity != null) {
                            activity.getCarLocation();
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
        setContentView(R.layout.activity_car_position);
    }

    @Override
    public void initDatas() {

        fromOrderings = new ArrayList<Integer>() {
            {
                add(Config.ORDERING_HOUR_RECOVER);
                add(Config.ORDERING_HOUR_CAR_POSITIOIN);
            }
        };
        fromUsings = new ArrayList<Integer>() {
            {
                add(Config.USING_HOUR_RECOVER);
                add(Config.USING_HOUR_CAR_POSITIOIN);
                add(Config.USING_LONG_RECOVER);
                add(Config.USING_LONG_CAR_POSITIOIN);
            }
        };
        fromRecovers = new ArrayList<Integer>() {{
            add(Config.ORDERING_HOUR_RECOVER);
            add(Config.USING_HOUR_RECOVER);
            add(Config.USING_LONG_RECOVER);
        }};
        fromCarPositions = new ArrayList<Integer>() {{
            add(Config.ORDERING_HOUR_CAR_POSITIOIN);
            add(Config.USING_HOUR_CAR_POSITIOIN);
            add(Config.USING_LONG_CAR_POSITIOIN);
        }};

        fromUsingsLong = new ArrayList<Integer>() {
            {
                add(Config.USING_LONG_RECOVER);
                add(Config.USING_LONG_CAR_POSITIOIN);
            }
        };

        from = getIntent().getIntExtra("from", Config.ORDERING_HOUR_RECOVER);
        timeDown = getIntent().getIntExtra("timeDown", timeDown);
        if (getIntent().getStringExtra("useTime") == null || getIntent().getStringExtra("useTime").equals("")) {
            useTime = 0;
        } else {
            useTime = Integer.valueOf(getIntent().getStringExtra("useTime"));
        }
        orderId = getIntent().getStringExtra("orderId");
        carNumber = getIntent().getStringExtra("carNumber");

        if (fromOrderings.contains(from)) {//预约中
            parkInfo = (ParkResp) getIntent().getSerializableExtra("parkInfo");
        } else if (fromUsings.contains(from)) {//用车中
            carInfoCode = (CarResp) getIntent().getSerializableExtra("carInfoCode");
        }

        if (fromRecovers.contains(from)) {
            titleBack.setVisibility(View.GONE);
            titleMain.setVisibility(View.VISIBLE);
//            ivTitleName.setVisibility(View.VISIBLE);
            tvTitleName.setVisibility(View.GONE);

            //消息
            getSystemInfo();
        } else if (fromCarPositions.contains(from)) {
            tvTitleName.setText("车辆位置");
            titleBack.setVisibility(View.VISIBLE);
            titleMain.setVisibility(View.GONE);
        }

        initMap();

        if (fromOrderings.contains(from)) {//预约中
            setCarMarker();
        } else if (fromUsings.contains(from)) {//用车中

            getCarLocation();
            handler = new MyHandler(this);
            Message msg = handler.obtainMessage();
            msg.what = 1;
            handler.sendMessageDelayed(msg, 1000 * 60);
        }

        DataUtils.permissLoca(CarPositionActivity.this);

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
                            case RxEventCodes.CODE_LOCATE_STATUS://定位结果
                                int code = (int) rxBusEvent.getContent();
                                locateResult(code);
                                break;
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == NONE) {
            return;
        }
        Bitmap mBitmapPhotoData = null;
        // 拍照
        if (requestCode == PHOTOHRAPH) {
            if (mPicFile != null && mPicFile.exists()) {
                try {

                    //判断是否是AndroidN以及更高的版本
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", mPicFile);
                        mBitmapPhotoData = ViewShowUtil.getThumbnail(mContext, contentUri, 160);
                    } else {
                        mBitmapPhotoData = ViewShowUtil.getThumbnail(mContext, Uri.fromFile(mPicFile), 160);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                return;
            }
        } else {
            //相册
            if (data == null) {
                return;
            }
            Uri uri = data.getData();
            if (uri == null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    mBitmapPhotoData = (Bitmap) extras.get("data");
                }
            } else {
                try {
                    mBitmapPhotoData = ViewShowUtil.getThumbnail(mContext, uri, 160);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(mPicFile);
            mBitmapPhotoData.compress(Bitmap.CompressFormat.PNG, 60,
                    out);// (0-100)压缩文件
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Logger.e("文件的路径：" + mPicFile.getAbsolutePath() + ";文件大小=" + FileSizeUtil.getFileOrFilesSize(mPicFile.getAbsolutePath(), FileSizeUtil.SIZETYPE_B));
        final Bitmap finalMBitmapPhotoData = mBitmapPhotoData;
        UploadFile uploadFile = new UploadFile(new UploadFile.UploadImgListener() {

            @Override
            public void before() {
                doCheck("正在上传头像...", true);
            }

            @Override
            public void after(SecData response) {
                closeDialog();
                if (response != null) {
                    if (response.getCode().equals(Config.REQUEST_SUCCESS)) {
                        queryUserInfo();

                        iv_myhead_logo.setImageBitmap(finalMBitmapPhotoData);
                        ToastUtil.show(mContext, "头像上传成功");
                    } else {
                        showTipDialog(response.getMsg());
                    }
                } else {
                    showTipDialog("上传失败");
                }
            }
        }, application);
        Map<String, File> files = new HashMap<>();
        files.put("headPortraitFile", mPicFile);
        uploadFile.setFiles(files);
        uploadFile.setMethod(RequestUrls.UPLOAD_HEAD_PHOTO);
        uploadFile.upLoad(new VerifyInfoRequest());
        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 获取用户信息
     */
    private void queryUserInfo() {
        Logger.i(TAG, "queryUserInfo");
        UserInfoRequest data = new UserInfoRequest();
        UserInfoResp userInfoResp = CacheUtils.getIn().getUserInfo();
        if (userInfoResp != null) {
            data.setCustomerPhone(userInfoResp.getPhone());
            data.setMethod(RequestUrls.QUERY_USER_INFO);
            doGet(data, QUERY_USER_INFO, "", false);
        }
    }

    @Subscribe
    public void onEvent(DrawalayoutEvent event) {
        drawerLayout.openDrawer(left_layout);
    }

    private void showTipDialog(String msg) {
        final CustomAlertDialog alertDialog = CustomAlertDialog.getAlertDialog(mContext, true, true);
        alertDialog.setMessage(msg)
                .setBtnConfirmColor(R.color.new_primary)
                .setOnPositiveClickListener("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                }).show();
    }

    /**
     * 更新全部数据
     */
    private void refleshAllDatas() {
        setMyMarker();
    }

    @Override
    public void onComplete(String result, int type) {
        if (isSuccess(result)) {
            if (type == SYSTEM_PARAM) {
                getSystemInfoOk(getBean(result, SystemConfigResp.class));
            } else if (type == USE_CAR_LOCATION) {//每分钟更新一次车辆位置
                Logger.i(TAG, "更新位置");
                String lat, lng;
                if (fromUsingsLong.contains(from)) {//短租用车中
                    UseCostLongResp useFeeResp = getBean(result, UseCostLongResp.class);
                    lat = useFeeResp.getLatitude();
                    lng = useFeeResp.getLongitude();
                } else {
                    UseFeeResp useFeeResp = getBean(result, UseFeeResp.class);
                    lat = useFeeResp.getLatitude();
                    lng = useFeeResp.getLongitude();
                }

                setCarUsingMarker(lat, lng);
            } else if (type == QUERY_USER_INFO) {
                UserInfoResp userInfoResp = getBean(result, UserInfoResp.class);
                if (null != userInfoResp) {
                    userInfoResp.setToken(CacheUtils.getIn().getUserInfo().getToken());
                    CacheUtils.getIn().save(userInfoResp);
                    String status = userInfoResp.getStatus();
                    if (UserState.isOrdering(status) || UserState.isUsingCar(status)) {
                        //恢复数据接口
//                        revivalData(userInfoResp);
                    }
                    //是否有违章
                    violateNum = userInfoResp.getUnDoWzCount();
                    if (!TextUtils.isEmpty(violateNum) && !"0".equals(violateNum)) {
//                        llytViolateWarn.setVisibility(View.VISIBLE);
                    } else {
//                        llytViolateWarn.setVisibility(View.GONE);
                    }
                }

                init();
            }
        }
    }

    private void init() {
        UserInfoResp mUser = CacheUtils.getIn().getUserInfo();
        if (mUser != null) {
            String userName = mUser.getName();
            if (!StringUtils.isEmpty(userName)) {
                tvUserName.setText(userName);
            } else {
                String phone = mUser.getPhone();
                if (!StringUtils.isEmpty(phone) && phone.length() >= 11) {
                    String tmp = phone.substring(3, 7);
                    tvUserName.setText(phone.replace(tmp, "****"));
                } else {
                    tvUserName.setText(phone);
                }
            }

            //用户状态判断verifyStatus 1：已认证，2：审核中，3：未认证
            String status = mUser.getStatus();
            //驾照认证状态 1:驳回，2,3:过期
            driverNumberStatus = mUser.getDriverNumberUpdate();
            if ("experience".equals(status)) {
                if (UserUtils.isNeedUpdateDriverCard(driverNumberStatus)) {
                    verifyStatus = 3;
                } else if (TextUtils.isEmpty(mUser.getIdcardUrl()) || "null".equals(mUser.getIdcardUrl())
                        || mUser.getIdcardUrl().equals("")) {
                    verifyStatus = 3;
                } else if (!"".equals(mUser.getDescription()) && !"null".equals(mUser.getDescription()) && null != mUser.getDescription()) {
                    verifyStatus = 3;
                } else if (mUser.getIdcardUrl().length() > 0) {
                    verifyStatus = 2;
                }

            } else {
                if (driverNumberStatus == 1) {
                    verifyStatus = 3;
                } else {
                    verifyStatus = 1;
                }
            }

            if (!TextUtils.isEmpty(mUser.getPortraitUrl())) {
                Glide.with(this).load(mUser.getPortraitUrl()).into(iv_myhead_logo);
            }

            //已登录的不能点击头像
            person_re.setEnabled(false);

            //企业账号
            UserInfoResp.Company company = mUser.getCompany();
            if (null != company) {
                myCompanyName = company.getCompanyName();
            }
            //剩余额度
            myLeftAmount = mUser.getQuotaRemain();

            //押金交纳状态
            if (!StringUtils.isEmpty(mUser.getDeposit())) {
                deposit = Integer.parseInt(mUser.getDeposit());
            }
            if (!StringUtils.isEmpty(mUser.getShouldDeposit())) {
                shouldDeposit = Integer.parseInt(mUser.getShouldDeposit());
            }

            //资质认证状态:包括身份认证和押金交纳情况
            if (verifyStatus == 1 && deposit != 0 && deposit >= shouldDeposit) {
                //已认证
                needVerify = false;
            } else if (verifyStatus == 3 && deposit == 0) {
                //未认证
                needVerify = true;
            } else {
                //未完成
                needVerify = true;
            }

        } else {//未登录
            tvUserName.setText("点击登录");
            //默认头像
            iv_myhead_logo.setImageResource(R.mipmap.ge_1);
            //能点击，点击登录
            person_re.setEnabled(true);

            needVerify = false;
            myCompanyName = "";
            violateNum = "";
        }

//        //有无违章记录
//        boolean hasViolate = false;
//        if (!StringUtils.isEmpty(violateNum) && !"0".equals(violateNum)) {
//            hasViolate = true;
//        }
//        for (PersonCenterItem item : personCenterItems) {
//            if (PersonCenterItem.Companion.getMy_ids().equals(item.getItemKey())) {
//                //是否要资质认证
//                item.setHasNewMessage(needVerify);
//            } else if (PersonCenterItem.Companion.getMy_violate().equals(item.getItemKey())) {
//                //是否有违章记录
//                item.setHasNewMessage(hasViolate);
//            } else if (PersonCenterItem.Companion.getMy_company().equals(item.getItemKey())) {
//                //公司名称
//                item.setItemDetail(myCompanyName);
//            }
//        }
//        personCenterAdapter.notifyDataSetChanged();
    }

    /**
     * 设置车辆位置,预约中
     */
    private void setCarMarker() {
        if (null != parkInfo) {
            MarkerOptions markerOptions = null;
            String latitude = parkInfo.getLatitude();
            String longitude = parkInfo.getLongitude();
            String parkName = parkInfo.getParkName();
            if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
                LatLng mLatLng = new LatLng(DataUtils.toDouble(latitude),
                        DataUtils.toDouble(longitude));
                markerOptions = new MarkerOptions();
                markerOptions.anchor(0.5f, 0.5f);
                markerOptions.title(parkName);
                markerOptions.position(mLatLng);
                //有车的图片
                View view = ViewShowUtil.addParkMarker(mContext, 1);
                markerOptions.icon(BitmapDescriptorFactory.fromView(view));

                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 11f));
            }
            Marker marker = aMap.addMarker(markerOptions);
            marker.setObject(parkInfo);

            marker.showInfoWindow();
        }
    }

    /**
     * 设置车辆位置,用车中
     */
    private void setCarUsingMarker(String latitude, String longitude) {
        if (null != carInfoCode) {
            MarkerOptions markerOptions = null;
//            String latitude = carInfoCode.getLatitude();
//            String longitude = carInfoCode.getLongitude();
            String carNumber = carInfoCode.getCarNumber();
            Logger.i(TAG, "latitude=" + latitude + ",,longitude=" + longitude);
            if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
                LatLng mLatLng = new LatLng(DataUtils.toDouble(latitude),
                        DataUtils.toDouble(longitude));
                markerOptions = new MarkerOptions();
                markerOptions.anchor(0.5f, 0.5f);
                markerOptions.title(carNumber);
                markerOptions.position(mLatLng);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.yc_41));

                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 11f));
            }
            Marker marker = aMap.addMarker(markerOptions);
            marker.setObject(carInfoCode);

            if (null != preMarker) {
                preMarker.hideInfoWindow();
                preMarker.remove();
            }
            marker.showInfoWindow();
            preMarker = marker;
        }
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

        if (fromOrderings.contains(from)) {
            //预约中
            parkInfoWindowOrderingAdapter = new ParkInfoWindowOrderingAdapter(this, timeDown, carNumber);
            aMap.setInfoWindowAdapter(parkInfoWindowOrderingAdapter);
        } else if (fromUsings.contains(from)) {
            //用车中
            parkInfoWindowUsingAdapter = new ParkInfoWindowUsingAdapter(this, useTime);
            aMap.setInfoWindowAdapter(parkInfoWindowUsingAdapter);
        }
        aMap.setOnMarkerClickListener(this);
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        if (!TextUtils.isEmpty(CacheUtils.getIn().getStr(CacheUtils.locationOk))) {
            setMyMarker();
        }
    }

    private void doUpateMap() {
        if (mapView != null) {
            mapView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mapZoom();
                }
            }, 2000);
        }
    }

    private void mapZoom() {
        aMap.animateCamera(CameraUpdateFactory.zoomTo(DataUtils.phoneSize(mContext)), 500,
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

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        init();
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
        EventBus.getDefault().unregister(this);
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
        if (mapView != null) {
            mapView.onDestroy();
        }
        try {
            unregisterReceiver(systemReceiver);
        } catch (Exception e) {
            systemReceiver = null;
            systemReceiver = null;
        }

        if (parkInfoWindowOrderingAdapter != null) {
            parkInfoWindowOrderingAdapter.destroyHandler();
        }
        if (parkInfoWindowUsingAdapter != null) {
            parkInfoWindowUsingAdapter.destroyHandler();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
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
        Marker myMarker = getMarker("my");
        if (myMarker != null) {
            myMarker.remove();
        }
        ViewShowUtil.setMyMarker(aMap);
        Marker myLocation = getMarker("my");
        if (myLocation != null) {
            mSensorHelper.setCurrentMarker(myLocation);
        }
    }

    /**
     * 定位结果
     *
     * @param code
     */
    public void locateResult(int code) {
        Logger.i(TAG, "收到定位结果=" + code);
        if (code == 12) {//没打开定位权限
            DataUtils.permissLoca(CarPositionActivity.this);
        }
        Marker marker = getMarker("my");
        if (marker != null) {
            LatLng lng = new LatLng(DataUtils.toDouble(application.latitude),
                    DataUtils.toDouble(application.longitude));
            marker.setPosition(lng);
        }
        if (positioning) {
            setChangeLatLng();
        }
    }

    /**
     * 移动到当前位置
     */
    private void setChangeLatLng() {
        if (!TextUtils.isEmpty(CacheUtils.getIn().getStr(CacheUtils.locationOk))) {
//            aMap.animateCamera(CameraUpdateFactory.changeLatLng(UserUtils.getLatLng()));
            positioning = false;
        }
    }

    private Marker getMarker(String obj) {
        List<Marker> mapScreenMarkers = aMap.getMapScreenMarkers();
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
    public void onFailure(String msg, int type) {
        dismissProgress();
    }

    @OnClick({R.id.ivTitleLeftMain, R.id.ivTitleLeft, R.id.llytTitleRight, R.id.ivRefreshMap, R.id.ivLocateMyPosition, R.id.ivTitleInfo, R.id.ivTitleSearch, R.id.money_backage_linear, R.id.order_linear, R.id.rz_linear, R.id.wz_linear, R.id.companny_linear, R.id.goods_linear, R.id.setting_linear, R.id.person_re})
    public void onViewClicked(View view) {
        switch (view.getId()) {
//            case R.id.customerService:
//                final String kefuTel=CacheUtils.getIn().getSystem_Info().getKfphone()==null?"":CacheUtils.getIn().getSystem_Info().getKfphone();
//                //拨打客服电话
//                final CustomAlertDialog alertDialog = CustomAlertDialog.getAlertDialog(mContext, true, true);
//                alertDialog.setMessage("确认拨打" + kefuTel)
//                        .setBtnCancelColor(R.color.main_background)
//                        .setBtnConfirmColor(R.color.new_primary)
//                        .setOnNegativeClickListener("取消", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                alertDialog.dismiss();
//                            }
//                        })
//                        .setOnPositiveClickListener("呼叫", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Uri uri = Uri.parse("tel:" + kefuTel);
//                                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
//                                startActivity(intent);
//                                alertDialog.dismiss();
//                            }
//                        }).show();
//                break;
//            case R.id.useCarGuide:
//                Intent useCarGuideIntent=new Intent(this,WebActivity.class);
//                String url=CacheUtils.getIn().getSystem_Info().getUserGuideUrl()==null?"":CacheUtils.getIn().getSystem_Info().getUserGuideUrl();
//                useCarGuideIntent.putExtra("url",url);
//                startActivity(useCarGuideIntent);
//                break;
            case R.id.person_re:
                if (!CacheUtils.getIn().isLogin()) {
                    activityUtil.jumpTo(LoginActivity.class);
                } else {
                    chooseHeadPic();
                }
                break;
            case R.id.money_backage_linear:
                //钱包
                if (CacheUtils.getIn().isLogin()) {
                    activityUtil.jumpTo(MyWalletActivity.class);
                } else {
                    activityUtil.jumpTo(LoginActivity.class);
                }
                break;
            case R.id.order_linear:
                if (CacheUtils.getIn().isLogin()) {
                    activityUtil.jumpTo(AllOrderActivity.class);
                } else {
                    activityUtil.jumpTo(LoginActivity.class);
                }
                break;
            case R.id.rz_linear:
                if (CacheUtils.getIn().isLogin()) {
                    Intent intent = new Intent(mContext, IdVerifyStatusActivity.class);
                    intent.putExtra("verifyStatus", verifyStatus);
                    intent.putExtra("deposit", deposit);
                    intent.putExtra("shouldDeposit", shouldDeposit);
                    intent.putExtra("driverNumberStatus", driverNumberStatus);
                    startActivity(intent);
                } else {
                    activityUtil.jumpTo(LoginActivity.class);
                }
                break;
            case R.id.wz_linear:
                //违章记录
                if (!CacheUtils.getIn().isLogin()) {
                    activityUtil.jumpTo(LoginActivity.class);
                } else {
                    Intent intent1 = new Intent(mContext, TrafficViolationActivity.class);
                    startActivity(intent1);
                }
                break;
            case R.id.companny_linear:
                if (!CacheUtils.getIn().isLogin()) {
                    activityUtil.jumpTo(LoginActivity.class);
                } else {
                    //认证企业
                    Intent intent = new Intent(mContext, MyCompanyActivity.class);
                    intent.putExtra("myCompanyName", myCompanyName);
                    intent.putExtra("myLeftAmount", myLeftAmount);
                    startActivity(intent);
                }
                break;
            case R.id.goods_linear:
                Intent intent = new Intent(mContext, InviteFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_linear:
                activityUtil.jumpTo(SettingActivity.class);
                break;
            case R.id.ivTitleLeftMain:
                //跳转到个人中心
                drawerLayout.openDrawer(left_layout);
//                activityUtil.jumpTo(PersonCenterActivity.class);
                break;
            case R.id.ivTitleLeft:
                finish();
                break;
            case R.id.llytTitleRight:
                break;
            case R.id.ivRefreshMap://刷新地图
                refleshAllDatas();
                break;
            case R.id.ivLocateMyPosition:
                setMyPosition();
                setMyMarker();
                break;
            case R.id.ivTitleInfo://消息
                activityUtil.jumpTo(MessageActivity.class);
                //红点消失
                ivTitleInfoUnread.setVisibility(View.GONE);
                break;
            case R.id.ivTitleSearch:
                if (fromOrderings.contains(from)) {
                    //预约中
                    ToastUtil.show(mContext, "预约状态不能使用该功能");
                } else if (fromUsings.contains(from)) {
                    //用车中
                    ToastUtil.show(mContext, "用车状态不能使用该功能");
                }

                break;
        }
    }

    // 本地相册选择
    protected void gralleryUpload() {
        if (!DataUtils.permissStorage(this)) {
            ToastUtil.show(mContext, Config.PERMISSION_STORAGE);
            return;
        }
        UUID uuid = UUID.randomUUID();
        picname = uuid.toString() + ".jpg";
        mPicFile = new File(Environment.getExternalStorageDirectory(), picname);
        Intent gIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gIntent, PHOTORESOULT);
    }

    /**
     * 选择头像
     */
    private void chooseHeadPic() {
        final CustomAlertDialogVerify dialogVerify = CustomAlertDialogVerify.getAlertDialog(mContext, true, true);
        dialogVerify
                .setOnAlbumClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gralleryUpload();
                        dialogVerify.dismiss();
                    }
                })
                .setOnCameraClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cameraUpload();
                        dialogVerify.dismiss();
                    }
                }).show();
    }

    // 照相机
    protected void cameraUpload() {
        if (!DataUtils.isCameraCanUse()) {
            ToastUtil.show(mContext, Config.PERMISSION_CAMERA);
            DataUtils.permissCamera(this);
            return;
        }
        if (!DataUtils.permissCamera(this)) {
            ToastUtil.show(mContext, Config.PERMISSION_CAMERA);
            return;
        }
        if (!DataUtils.permissStorage(this)) {
            ToastUtil.show(mContext, Config.PERMISSION_STORAGE);
            return;
        }

        UUID uuid1 = UUID.randomUUID();
        picname = uuid1.toString() + ".jpg";
        mPicFile = new File(Environment.getExternalStorageDirectory(), picname);
        Intent cIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", mPicFile);
            cIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            cIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPicFile));
        }
        startActivityForResult(cIntent, PHOTOHRAPH);
    }

    private void setMyPosition() {
        doUpateMap();
        LatLng myLatLng = new LatLng(Double.parseDouble(application.latitude),
                Double.parseDouble(application.longitude));
        Marker marker = getMarker("my");
        if (marker != null) {
            marker.setPosition(myLatLng);
        }
        aMap.animateCamera(CameraUpdateFactory.changeLatLng(myLatLng));
    }

    /**
     * 获取消息
     */
    private void getSystemInfo() {
        SystemArgumentRequest data = new SystemArgumentRequest();
        data.setAddressType(RequestUrls.url);
        data.setMethod(RequestUrls.QUERY_SYSTEM_PARAM);
        doGet(data, SYSTEM_PARAM, null, false);
    }

    private void getSystemInfoOk(SystemConfigResp data) {
        if (data != null) {
            CacheUtils.getIn().save(data);
            if (data.getNewMessageNum() > 0) {
                ivTitleInfoUnread.setVisibility(View.VISIBLE);
            } else {
                ivTitleInfoUnread.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 获取汽车的位置
     */
    private void getCarLocation() {
        UseCarCostRequest request = new UseCarCostRequest();
        request.setCarSharingOrderNumber(orderId);
        if (fromUsingsLong.contains(from)) {//短租用车中
            request.setMethod(RequestUrls.QUERY_USE_CAR_COST_LONG);
        } else {//时租用车中
            request.setMethod(RequestUrls.QUERY_USE_CAR_COST);
        }
        doGet(request, USE_CAR_LOCATION, "", false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (fromRecovers.contains(from)) {
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && event.getRepeatCount() == 0) {
                exitApplication();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}