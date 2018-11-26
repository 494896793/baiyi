package www.qisu666.com.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import www.qisu666.com.BuildConfig;
import www.qisu666.com.R;
import www.qisu666.com.adapter.CarsListAdapter;
import www.qisu666.com.adapter.MyFragmentStatePagerAdapter;
import www.qisu666.com.app.MyApplication;
import www.qisu666.com.app.UserState;
import www.qisu666.com.callback.AppUpdateResp;
import www.qisu666.com.callback.CarParkResp;
import www.qisu666.com.callback.CarResp;
import www.qisu666.com.callback.OneKeyUseCarResp;
import www.qisu666.com.callback.ParkListByCityAreaResp;
import www.qisu666.com.callback.ParkRedPacketCarResp;
import www.qisu666.com.callback.ParkResp;
import www.qisu666.com.callback.ParksResp;
import www.qisu666.com.callback.RedPacketCar;
import www.qisu666.com.callback.SecData;
import www.qisu666.com.callback.SplashAndActivityResp;
import www.qisu666.com.callback.SystemConfigResp;
import www.qisu666.com.callback.UserInfoResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.controls.MapCtrl;
import www.qisu666.com.event.CemareOnActivityResultEvent;
import www.qisu666.com.event.DrawalayoutEvent;
import www.qisu666.com.event.DrawalayoutEvents;
import www.qisu666.com.event.MapVisibleEvent;
import www.qisu666.com.event.Message;
import www.qisu666.com.fragment.BaseFragment;
import www.qisu666.com.fragment.CarsListFragment;
import www.qisu666.com.map.cluster.ClusterOverlay;
import www.qisu666.com.receiver.PReceiver;
import www.qisu666.com.receiver.PublicReceiver;
import www.qisu666.com.request.APPVersionRequest;
import www.qisu666.com.request.GetCarListRequest;
import www.qisu666.com.request.InterfaceAddressRequest;
import www.qisu666.com.request.MyNetwork;
import www.qisu666.com.request.OneKeyUseCarRequest;
import www.qisu666.com.request.ParkPointListRequest;
import www.qisu666.com.request.ParkRedPacketCarRequest;
import www.qisu666.com.request.RecoverDataRequest;
import www.qisu666.com.request.SystemArgumentRequest;
import www.qisu666.com.request.UserInfoRequest;
import www.qisu666.com.request.VerifyInfoRequest;
import www.qisu666.com.request.utils.FlatFunction;
import www.qisu666.com.request.utils.MyMessageUtils;
import www.qisu666.com.request.utils.ResultSubscriber;
import www.qisu666.com.request.utils.RxNetHelper;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.service.UpdateService;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.DataUtils;
import www.qisu666.com.utils.DensityUtil;
import www.qisu666.com.utils.FileSizeUtil;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.SBUtils;
import www.qisu666.com.utils.SharedPreferencesUtils;
import www.qisu666.com.utils.StringUtils;
import www.qisu666.com.utils.TVUtils;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.utils.UploadFile;
import www.qisu666.com.utils.UserUtils;
import www.qisu666.com.view.AdPPW;
import www.qisu666.com.view.CircleImageView;
import www.qisu666.com.view.CustomAlertDialog;
import www.qisu666.com.view.CustomAlertDialogVerify;
import www.qisu666.com.view.KfEarPhoneView;
import www.qisu666.com.view.NotificationView;
import www.qisu666.com.view.ParkInfoView;
import www.qisu666.com.view.TitleMainView;
import www.qisu666.com.view.UpdateView;
import www.qisu666.com.view.ViewShowUtil;
import com.bumptech.glide.Glide;
import com.yinglan.scrolllayout.ScrollLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

public class MainActivity extends BaseFragment {
    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.rl_main_all)
    RelativeLayout rlMainAll;
    @BindView(R.id.titleMainView)
    TitleMainView titleMainView;
    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.llytViolateWarn)
    LinearLayout llytViolateWarn;//违章提示
    @BindView(R.id.kfEarPhoneView)
    KfEarPhoneView kfEarPhoneView;//客服体系
    @BindView(R.id.llytScrollLayoutContainer)
    LinearLayout llytScrollLayoutContainer;
    @BindView(R.id.scrollDownLayout)
    ScrollLayout scrollDownLayout;
    @BindView(R.id.scrollLayoutContent)
    LinearLayout scrollLayoutContent;
    @BindView(R.id.rlytButtonContainer)
    RelativeLayout rlytButtonContainer;
    @BindView(R.id.ivDragHandle)
    ImageView ivDragHandle;
    @BindView(R.id.parkInfoView)
    ParkInfoView parkInfoView;
    @BindView(R.id.llytCarContent)
    LinearLayout llytCarContent;
    @BindView(R.id.viewPagerContainer)
    LinearLayout viewPagerContainer;
    @BindView(R.id.viewPagerCarInfo)
    ViewPager viewPagerCarInfo;
    @BindView(R.id.viewDivideLinePoint)
    View viewDivideLinePoint;
    @BindView(R.id.llytPointContainer)
    LinearLayout llytPointContainer;
    @BindView(R.id.tvNoCarTip)
    TextView tvNoCarTip;
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
    @BindView(R.id.commonQuestion)
    TextView commonQuestion;
    @BindView(R.id.customerService)
    TextView customerService;


    private Handler mHandler = new Handler();
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
    private List<ParksResp> parksResps = new ArrayList<>();//网点
    private List<ParkListByCityAreaResp> parkListByCityResps = new ArrayList<>();//按城市的网点
    protected static int checkUpdate = 0;
    private static final int QUERY_USER_INFO = 1;
    private static final int QUERY_INTERFACE_ADDRESS = 2;
    //    private static int QUERY_PARK_LIST = 4;
    private static int QUERY_CITY_PARK_BY_CITYCODE = 5;
    private static int REQUEST_RECOVER_DATA = 8;
    private static int SYSTEM_PARAM = 9;
    private static int QUERY_PARK_RED_PACKET_CAR = 10;
    private static final int QUERY_CAR_LIST = 11;
    private static final int QUERY_ONE_KEY_USE_CAR = 12;
    private PackageInfo pi;

    private boolean isShow = true;
    private PublicReceiver systemReceiver;
    private PublicReceiver updateAllData;
    private UpdateView updateView;
    private PReceiver loginReceiver;
    private PublicReceiver clearChargeOrderReceiver;

    //一键用车的网点列表
    private List<ParksResp> oneKeyParks = new ArrayList<>();
    //点击一键用车
//    private boolean isOneKeyUseCar = false;

    //是否已经移动到最近的网点
    private boolean isMoveToFirstPark = false;

    //要搜索的网点
    private ParksResp searchParksResp = null;

    private ParksResp showPark = null;
    private ParkRedPacketCarResp parkRedPacketCarResp;//红包车数据
    //第一个进入在onResume中不请求用户信息，在灰度接口请求完成后才请求用户信息接口
    private boolean isGetUserInfo = false;
    //未读消息数量
    private int newMessageNum;
    private MapCtrl mapCtrl;
    //是否要重新初始化
    private boolean resetClusterOverlay = false;

    //一个网点中的车辆数
    private List<CarResp> carList = new ArrayList<>();
    private List<Fragment> carsListFragments = new ArrayList<>();
    //    private static final int CARS_PAGE = 0, CARS_LIST = 1;
//    private int scrollLayoutStatus = CARS_PAGE;
    private CarsListAdapter carsListAdapter;

    private int scrollLayoutPadding = 20;
    private int btnPaddingBottom = 45;//dp
    private LatLng selectedLatLng;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public int setLayoutResId() {
        return R.layout.activity_main;
    }

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view=inflater.inflate(R.layout.activity_main,null);
//        return view;
//    }

    private void initView(View view) {
        rlMainAll = view.findViewById(R.id.rl_main_all);
        titleMainView = view.findViewById(R.id.titleMainView);
        mapView = view.findViewById(R.id.mapView);
        llytViolateWarn = view.findViewById(R.id.llytViolateWarn);
        kfEarPhoneView = view.findViewById(R.id.kfEarPhoneView);
        llytScrollLayoutContainer = view.findViewById(R.id.llytScrollLayoutContainer);
        scrollDownLayout = view.findViewById(R.id.scrollDownLayout);
        scrollLayoutContent = view.findViewById(R.id.scrollLayoutContent);
        rlytButtonContainer = view.findViewById(R.id.rlytButtonContainer);
        ivDragHandle = view.findViewById(R.id.ivDragHandle);
        parkInfoView = view.findViewById(R.id.parkInfoView);
        llytCarContent = view.findViewById(R.id.llytCarContent);
        viewPagerContainer = view.findViewById(R.id.viewPagerContainer);
        viewPagerCarInfo = view.findViewById(R.id.viewPagerCarInfo);
        viewDivideLinePoint = view.findViewById(R.id.viewDivideLinePoint);
        llytPointContainer = view.findViewById(R.id.llytPointContainer);
        tvNoCarTip = view.findViewById(R.id.tvNoCarTip);
        drawerLayout = view.findViewById(R.id.drawerLayout);
        LinearLayout left_layout = view.findViewById(R.id.left_layout);
        LinearLayout setting_linear = view.findViewById(R.id.setting_linear);
        LinearLayout goods_linear = view.findViewById(R.id.goods_linear);
        LinearLayout money_backage_linear = view.findViewById(R.id.money_backage_linear);
        LinearLayout order_linear = view.findViewById(R.id.order_linear);
        LinearLayout rz_linear = view.findViewById(R.id.rz_linear);
        LinearLayout wz_linear = view.findViewById(R.id.wz_linear);
        LinearLayout companny_linear = view.findViewById(R.id.companny_linear);
        RelativeLayout person_re = view.findViewById(R.id.person_re);
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        CircleImageView iv_myhead_logo = view.findViewById(R.id.iv_myhead_logo);
        TextView commonQuestion = view.findViewById(R.id.commonQuestion);
        TextView customerService = view.findViewById(R.id.customerService);
    }

    @Subscribe
    public void onEvent(MapVisibleEvent event) {
        Log.i("======", "===event");
        if (mapView != null) {
            if (event.visibleCode == 1) {
                mapView.setVisibility(View.GONE);
            } else {
                mapView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void initDatas(View view) {
        //        titleMainView.setTitleNameImg(R.mipmap.title_main);

        initMap();
        initDrawLayout();

        observeRxEventCode();
        queryInterfaceAddress();

        DataUtils.verifyStoragePermiss(getActivity());

        //登录成功
        loginReceiver = new PReceiver(getActivity(), SBUtils.loginOk);
        loginReceiver.setReceive(new PReceiver.IDoReceiver() {
            @Override
            public void doReceiver(int type) {
                Logger.e(TAG, "loginReceiver");
                mapCtrl.animateToZoom(Config.MAP_ZOOM_17);
                getSystemInfo();
            }
        });

        systemReceiver = new PublicReceiver(SBUtils.SystemInfo);
        systemReceiver.setBeanReceive(new PublicReceiver.IBeanReceive() {
            @Override
            public void getBean() {
                Logger.e(TAG, "systemReceiver");
                getSystemInfo();
            }
        });
        //还车成功后更新数据
        updateAllData = new PublicReceiver(SBUtils.UPDATE_MAP_PARKS_INFO);
        updateAllData.setBeanReceive(new PublicReceiver.IBeanReceive() {
            @Override
            public void getBean() {
                Logger.i(TAG, "还车成功后更新首页数据");
                refreshAllData();
            }
        });
        //活动数据
        ArrayList<SplashAndActivityResp> splashAndActivityResps = getArguments().getParcelableArrayList("splashAndActivityResps");
        //显示活动广告
        showActivityAd(splashAndActivityResps);
        //客服控件
        kfEarPhoneView.setData(getActivity());

        llytScrollLayoutContainer.setPadding(scrollLayoutPadding, 0, scrollLayoutPadding, scrollLayoutPadding);
        scrollDownLayout.setOnScrollChangedListener(new ScrollLayout.OnScrollChangedListener() {
            @Override
            public void onScrollProgressChanged(float currentProgress) {
                double paddingBottom = new DensityUtil().dp2px(getActivity(), btnPaddingBottom) + getContentHeight() * (1 - currentProgress);
                rlytButtonContainer.setPadding(0, 0, 0, (int) paddingBottom);

                if (currentProgress == 1) {
                    //取消网点选择
                    Logger.i(TAG, "取消网点");
                    showPark = null;
                    mapCtrl.dealMapClick();
//                    setMapCenter(getTitleWidth(), getScreenHeight(), 0, getTitleHeight());
                }
            }

            @Override
            public void onScrollFinished(ScrollLayout.Status currentStatus) {

            }

            @Override
            public void onChildScroll(int top) {

            }
        });

    }

    private void initDrawLayout() {
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        final RxBus rxBus = RxBus.getInstance();
        final RxBusEvent<Boolean> event = new RxBusEvent<>();
        event.setEventCode(RxEventCodes.CODE_DRAWLAYOUT_STATUS);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset != 0) {//打开了
                    event.setContent(true);
                    rxBus.post(event);
                } else {
                    EventBus.getDefault().post(new DrawalayoutEvents());
                    event.setContent(false);
                    rxBus.post(event);
                }
            }
        });
    }

    @Subscribe
    public void onEvent(DrawalayoutEvent event) {
        drawerLayout.openDrawer(left_layout);
    }

    private void initMap() {
        mapCtrl = new MapCtrl(getActivity(), mapView, savedInstanceState);
        mapCtrl.animateToMyPosition();
        //marker点击监听
        mapCtrl.setOnSelectedParkListener(new MapCtrl.OnSelectedParkListener() {
            @Override
            public void onSelectedPark(@NotNull ParksResp parkInfo) {
                showPark = parkInfo;
                //显示当前选中的网点的信息
                showSelectedParkInfo();
                getCarList();
            }
        });
        mapCtrl.setOnClusterLevelListener(new MapCtrl.OnClusterLevelListener() {
            @Override
            public void onClusterLevel(int clusterLevel) {
                if (clusterLevel == ClusterOverlay.CITY_CLUSTER
                        || clusterLevel == ClusterOverlay.AREA_CLUSTER) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            hideParkCarInfo();
//                            setMapCenter(getTitleWidth(), getScreenHeight(), 0, getTitleHeight());
                        }
                    });
                }
            }
        });
        mapCtrl.setOnMapClickListener(new MapCtrl.OnMapClickListener() {
            @Override
            public void onMapClick() {
                hideParkCarInfo();
//                setMapCenter(getTitleWidth(), getScreenHeight(), 0, getTitleHeight());
            }
        });
    }

    private void observeRxEventCode() {
        busSubscription = RxBus.getInstance()
                .toObservable(RxBusEvent.class)
                .subscribe(new Action1<RxBusEvent>() {
                    @Override
                    public void call(RxBusEvent rxBusEvent) {
                        int eventCode = rxBusEvent.getEventCode();
                        switch (eventCode) {
                            case RxEventCodes.SERVER_PUSH_BING_COUPON://绑定优惠券
                                NotificationView.show(getActivity(), CouponActivity.class, "绑定优惠券", rxBusEvent.getContent().toString());
                                break;
                            case RxEventCodes.SERVER_PUSH_COUPON_SUCCESS://注册送优惠券
                                NotificationView.show(getActivity(), CouponActivity.class, "注册送优惠券", rxBusEvent.getContent().toString());
                                break;
                            case RxEventCodes.CODE_SEARCH_PARK://搜索网点
                                ParksResp parksResp = (ParksResp) rxBusEvent.getContent();
                                searchPark(parksResp);
                                break;
                            case RxEventCodes.CODE_TO_ROUTE://导航
                                Marker marker = (Marker) rxBusEvent.getContent();
                                mapCtrl.route(marker, false);
                                break;
                            case RxEventCodes.CODE_REQUEST_HUI_DU://灰度
                                //重置
                                resetClusterOverlay = true;
                                mapCtrl.animateToZoom(Config.MAP_ZOOM_17);
                                queryInterfaceAddress();
                                break;
                            case RxEventCodes.CODE_LOCATE_STATUS://定位结果
                                int code = (int) rxBusEvent.getContent();
                                locateResult(code);
                                break;
                        }
                    }
                });
    }

    @Subscribe
    public void onEvent(CemareOnActivityResultEvent event) {
        if (event.resultCode == NONE) {
            return;
        }
        Bitmap mBitmapPhotoData = null;
        // 拍照
        if (event.requestCode == PHOTOHRAPH) {
            if (mPicFile != null && mPicFile.exists()) {
                try {

                    //判断是否是AndroidN以及更高的版本
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileProvider", mPicFile);
                        mBitmapPhotoData = ViewShowUtil.getThumbnail(getActivity(), contentUri, 160);
                    } else {
                        mBitmapPhotoData = ViewShowUtil.getThumbnail(getActivity(), Uri.fromFile(mPicFile), 160);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                return;
            }
        } else {
            //相册
            if (event.data == null) {
                return;
            }
            Uri uri = event.data.getData();
            if (uri == null) {
                Bundle extras = event.data.getExtras();
                if (extras != null) {
                    mBitmapPhotoData = (Bitmap) extras.get("data");
                }
            } else {
                try {
                    mBitmapPhotoData = ViewShowUtil.getThumbnail(getActivity(), uri, 160);
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
                        ToastUtil.show(getActivity(), "头像上传成功");
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
    }

    private void showTipDialog(String msg) {
        final CustomAlertDialog alertDialog = CustomAlertDialog.getAlertDialog(getActivity(), true, true);
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
    private void refreshAllData() {
//        setMapCenter(getTitleWidth(),getScreenHeight(),0,getTitleHeight());
        //重置
        resetClusterOverlay = true;
        mapCtrl.setMyMarker();
        queryData();
    }

    /**
     * 只有定位成功后，才设置当前位置的marker
     */
    private void locationComplete() {
        if (!TextUtils.isEmpty(CacheUtils.getIn().getStr(CacheUtils.locationOk))) {
            if (parksResps != null && parksResps.size() > 0) {
                mapCtrl.clear();
                mapCtrl.setMyMarker();
                mapCtrl.animateToZoomDelay(Config.MAP_ZOOM_18);
            }
        }
    }


    @Override
    public void onComplete(String result, int type) {
        if (isSuccess(result)) {
            if (type == QUERY_CITY_PARK_BY_CITYCODE) {//网点列表
                closeDialog();
                parksResps.clear();
                parkListByCityResps.clear();
                parkListByCityResps = getList(result, ParkListByCityAreaResp.class);

                for (ParkListByCityAreaResp resp : parkListByCityResps) {
                    if (resp == null) {
                        continue;
                    }
                    for (ParkListByCityAreaResp.AreaList areaList : resp.getAreaList()) {
                        if (areaList == null) {
                            continue;
                        }
                        if (null != parkRedPacketCarResp) {
                            for (ParksResp parksResp : areaList.getParkList()) {
                                String parkId = parksResp.getId();
                                if (null != parkId) {
                                    Map<String, RedPacketCar> map = parkRedPacketCarResp.getMap();
                                    RedPacketCar redPacketCar = map.get(parkId);
                                    if (redPacketCar != null) {
                                        String redPacketCarNum = redPacketCar.getRedPacketCarNum();
                                        if (!StringUtils.isEmpty(redPacketCarNum)) {
                                            parksResp.setRedPacketCarNum(Integer.parseInt(redPacketCarNum));
                                        }
                                    }
                                }
                                parksResps.add(parksResp);
                            }
                        } else {
                            parksResps.addAll(areaList.getParkList());
                        }
                    }
                }
                sortParkDataNew(parksResps);

                locationComplete();

//                if (!isOneKeyUseCar) {//不是一键用车
                moveToFirstMarker();
                mapCtrl.initCluster(parkListByCityResps, showPark, resetClusterOverlay);
//                } else {
//                    getNearCars();
//                    isOneKeyUseCar = false;
//                }

                preSetScrollLayout();

            } else if (type == QUERY_PARK_RED_PACKET_CAR) {//红包车数量
                Map<String, RedPacketCar> map = JSON.parseObject(getBean(result, String.class), new TypeReference<Map<String, RedPacketCar>>() {
                });
                parkRedPacketCarResp = new ParkRedPacketCarResp(map);
                queryCityParkByCityCode();
            } else if (type == QUERY_ONE_KEY_USE_CAR) {//一键用车
                OneKeyUseCarResp resp = getBean(result, OneKeyUseCarResp.class);
                if (null != resp && resp.getQueryCarVoList().size() > 0 && parksResps != null && parksResps.size() > 0) {
                    ParksResp parksResp = resp.getDepot();
                    for (int i = 0; i < parksResps.size(); i++) {
                        ParksResp foundPark = parksResps.get(i);
                        if (foundPark != null && foundPark.getId().equals(parksResp.getId())) {
                            foundPark = parksResp;
                            foundPark.setSelected(true);
                            foundPark.setNearest(true);
                            showPark = foundPark;
                            mapCtrl.showInfoWindow(showPark);
                            mapCtrl.animateToLatLng(foundPark.getLatlng());
                            carList = resp.getQueryCarVoList();
                            //显示当前选中的网点的信息
                            showSelectedParkInfo();
                            setCarListData();
                            break;
                        }
                    }
                }
            } else if (type == REQUEST_RECOVER_DATA) {//复活
                CarParkResp data = getBean(result, CarParkResp.class);
                if (data != null) {
                    UserInfoResp mUser = CacheUtils.getIn().getUserInfo();

                    CarResp mCar = data.getCarBaseInfo();
                    ParkResp mPark = data.getParkBaseinfo();

                    if (MyApplication.isLoginSuccess && UserState.isNeedRevival(mUser.getStatus())) {
                        if (null != mCar && null != mPark && UserState.isOrdering(mUser.getStatus())) {
                            Intent intent = new Intent();
                            if (Config.ORDER_CATEGORY_LONG_RENT.equals(data.getOrderType())) {//短租
                                intent.setClass(getActivity(), UseCarPreOrderingActivity.class);
                            } else {//短租
                                intent.setClass(getActivity(), UseCarOrderingActivity.class);
                            }
                            intent.putExtra("recoverData", data);
                            startActivity(intent);
                        } else if (null != mCar && UserState.isUsingCar(mUser.getStatus())) {
                            Intent intent = new Intent();
                            if (Config.ORDER_CATEGORY_LONG_RENT.equals(data.getOrderType())) {//短租
                                intent.setClass(getActivity(), UseCarLongRentReturnActivity.class);
                            } else {//短租
                                intent.setClass(getActivity(), UseCarReturnActivity.class);
                            }
                            intent.putExtra("recoverData", data);
                            startActivity(intent);
                        }
                    }
                }
            } else if (type == SYSTEM_PARAM) {
                getSystemInfoOk(getBean(result, SystemConfigResp.class));
                EventBus.getDefault().post(getBean(result, SystemConfigResp.class));
            } else if (type == QUERY_USER_INFO) {
                UserInfoResp userInfoResp = getBean(result, UserInfoResp.class);
                if (null != userInfoResp) {
                    userInfoResp.setToken(CacheUtils.getIn().getUserInfo().getToken());
                    CacheUtils.getIn().save(userInfoResp);
                    String status = userInfoResp.getStatus();
                    if (UserState.isOrdering(status) || UserState.isUsingCar(status)) {
                        //恢复数据接口
                        revivalData(userInfoResp);
                    }
                    //是否有违章
                    violateNum = userInfoResp.getUnDoWzCount();
                    if (!TextUtils.isEmpty(violateNum) && !"0".equals(violateNum)) {
                        llytViolateWarn.setVisibility(View.VISIBLE);
                    } else {
                        llytViolateWarn.setVisibility(View.GONE);
                    }
                }

                init();
            } else if (type == QUERY_INTERFACE_ADDRESS) {
                String changeUrl = getMsg(result);
                if (!TextUtils.isEmpty(changeUrl) && !"null".equals(changeUrl)) {
                    RequestUrls.url = changeUrl;
                }
                isGetUserInfo = true;
                if (CacheUtils.getIn().isLogin()) {
                    //重新获取用户信息
                    queryUserInfo();
                }

                queryData();
                checkUpdate();
            }
            if (type == checkUpdate) {
                AppUpdateResp appUpdateResp = getBean(result, AppUpdateResp.class);
                if (appUpdateResp == null) {
                    return;
                }
                int records = appUpdateResp.getVersionRecord();
                Logger.i("服务器的版本号=" + records + ",本地版本号=" + pi.versionCode);
                if (records > pi.versionCode) {
                    application.isNeedForceUpdate = true;
                    CacheUtils.getIn().save(appUpdateResp);
                    checkUpdateVersion();
                    SharedPreferencesUtils.putInt(getActivity(), "oldVersion", pi.versionCode);
                }
            } else if (type == QUERY_CAR_LIST) {//网点中的车辆列表
                carList = getList(result, CarResp.class);
                setCarListData();
            }
        } else {
            if (type == QUERY_CITY_PARK_BY_CITYCODE) {
                closeDialog();
                Logger.i(TAG, "请求网点失败");
                if (!TextUtils.isEmpty(CacheUtils.getIn().getStr(CacheUtils.locationOk))) {
                    Logger.i(TAG, "请求网点失败 定位成功");
                    mapCtrl.setMyMarker();
                    mapCtrl.animateToMyPosition();
                }
            } else if (type == QUERY_PARK_RED_PACKET_CAR) {//红包车数量
                queryCityParkByCityCode();
            } else if (type == QUERY_INTERFACE_ADDRESS) {
                isGetUserInfo = true;
                if (CacheUtils.getIn().isLogin()) {
                    //重新获取用户信息
                    queryUserInfo();
                }
                queryData();
                checkUpdate();
            }
        }
    }

    @Override
    public void onFailure(String msg, int type) {
        if (type == QUERY_CITY_PARK_BY_CITYCODE) {
            closeDialog();
            Logger.i(TAG, "请求网点失败");
            if (!TextUtils.isEmpty(CacheUtils.getIn().getStr(CacheUtils.locationOk))) {
                Logger.i(TAG, "请求网点失败 定位成功");
                mapCtrl.setMyMarker();
                mapCtrl.animateToMyPosition();
            }
        } else if (type == QUERY_PARK_RED_PACKET_CAR) {//红包车数量
            queryCityParkByCityCode();
        } else if (type == QUERY_INTERFACE_ADDRESS) {
            isGetUserInfo = true;
            if (CacheUtils.getIn().isLogin()) {
                //重新获取用户信息
                queryUserInfo();
            }
            queryData();
            checkUpdate();
        }
    }

    private void moveToFirstMarker() {
        mapCtrl.clear();
        mapCtrl.setMyMarker();

        isMoveToFirstPark = false;

        if (parksResps != null && parksResps.size() > 0) {
            for (int i = 0, size = parksResps.size(); i < size; i++) {
                final ParksResp parksResp = parksResps.get(i);
                if (parksResp == null) {
                    continue;
                }
                if (searchParksResp != null) {//移动到搜索的网点
                    if (searchParksResp.getId().equals(parksResp.getId())) {
                        String freeCarNum = parksResp.getParkFreeCarNum();
                        if (!TextUtils.isEmpty(freeCarNum)) {
                            int freeCar = DataUtils.toInt(freeCarNum);
                            if (freeCar > 0) {
                                searchParksResp.setNearest(true);
                            }
                        }
                        //将搜索的网点设置选择
                        searchParksResp.setSelected(true);
                        showPark = searchParksResp;
                        mapCtrl.animateToLatLng(searchParksResp.getLatlng());
                        isMoveToFirstPark = true;
                        searchParksResp = null;

//                        ViewShowUtil.getSelectedReturnMarker()
                        //显示当前选中的网点的信息
                        showSelectedParkInfo();
                        getCarList();

                        break;
                    }
                } else {//移动到第一个有车的网点
                    String freeCarNum = parksResp.getParkFreeCarNum();
                    if (!TextUtils.isEmpty(freeCarNum)) {
                        int freeCar = DataUtils.toInt(freeCarNum);
                        if (freeCar > 0) {
                            parksResp.setNearest(true);
                            showPark = parksResp;
                            Logger.i(TAG, "移动到第" + i + "个网点");
                            mapCtrl.animateToLatLng(parksResp.getLatlng());
                            isMoveToFirstPark = true;
                            break;
                        }
                    }
                }
            }
        }
        //没有移动到第一个有车辆的网点的时候
        if (!isMoveToFirstPark) {
//                    本次定位成功，就移动到定位的位置
            if (!TextUtils.isEmpty(CacheUtils.getIn().getStr(CacheUtils.locationOk))) {
                mapCtrl.animateToMyPosition();
            } else {//本次定位不成功，移动到第一个网点的位置
                if (null != parksResps && parksResps.size() > 0)
                    mapCtrl.animateToLatLng(parksResps.get(0).getLatlng());
            }
            if (null != parksResps && parksResps.size() > 0) {
                ParksResp park = parksResps.get(0);
                park.setNearest(true);
                showPark = park;
            }
            isMoveToFirstPark = true;
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
            person_re.setEnabled(true);

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
     * 网点排序
     *
     * @param
     */
    private void sortParkDataNew(final List<ParksResp> parksResps) {
        if (parksResps != null && parksResps.size() > 0) {
            Collections.sort(parksResps, new Comparator<ParksResp>() {
                @Override
                public int compare(ParksResp lhs, ParksResp rhs) {
                    Float d1 = AMapUtils.calculateLineDistance(lhs.getLatlng(), UserUtils.getLatLng());
                    if (d1 > 1000) {
                        lhs.setDistance(TVUtils.toString(d1 / 1000.00) + "KM");
                    } else {
                        lhs.setDistance(TVUtils.toStringInt(d1 + "") + "m");
                    }
                    Float d2 = AMapUtils.calculateLineDistance(rhs.getLatlng(), UserUtils.getLatLng());
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

    /**
     * 请求数据
     */
    private void queryData() {
        queryParkRedPacketCar();
        getSystemInfo();
    }

    @OnClick({R.id.customerService, R.id.commonQuestion, R.id.tv_near_car, R.id.ivRefreshMap, R.id.ivLocateMyPosition, R.id.llytViolateWarn, R.id.ivDragHandle, R.id.money_backage_linear, R.id.order_linear, R.id.rz_linear, R.id.wz_linear, R.id.companny_linear, R.id.goods_linear, R.id.setting_linear, R.id.person_re})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.customerService:
//                final String kefuTel=CacheUtils.getIn().getSystem_Info().getKfphone()==null?"":CacheUtils.getIn().getSystem_Info().getKfphone();
//                //拨打客服电话
//                final CustomAlertDialog alertDialog = CustomAlertDialog.getAlertDialog(getActivity(), true, true);
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

                Intent userProtocolIntent = new Intent(getActivity(), WebActivity.class);
                String urlProtocol = CacheUtils.getIn().getSystem_Info().getUserProtocol() == null ? "" : CacheUtils.getIn().getSystem_Info().getUserProtocol();
                userProtocolIntent.putExtra("url", urlProtocol);
                startActivity(userProtocolIntent);
                break;
            case R.id.commonQuestion:
                Intent commonQuestionIntent = new Intent(getActivity(), WebActivity.class);
                String url = CacheUtils.getIn().getSystem_Info().getCommonQuestion() == null ? "" : CacheUtils.getIn().getSystem_Info().getCommonQuestion();
                commonQuestionIntent.putExtra("url", url);
                startActivity(commonQuestionIntent);
                break;
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
                    Intent intent = new Intent(getActivity(), IdVerifyStatusActivity.class);
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
                    Intent intent1 = new Intent(getActivity(), TrafficViolationActivity.class);
                    startActivity(intent1);
                }
                break;
            case R.id.companny_linear:
                if (!CacheUtils.getIn().isLogin()) {
                    activityUtil.jumpTo(LoginActivity.class);
                } else {
                    //认证企业
                    Intent intent = new Intent(getActivity(), MyCompanyActivity.class);
                    intent.putExtra("myCompanyName", myCompanyName);
                    intent.putExtra("myLeftAmount", myLeftAmount);
                    startActivity(intent);
                }
                break;
            case R.id.goods_linear:
                Intent intent = new Intent(getActivity(), InviteFriendsActivity.class);
                startActivity(intent);
                break;
            case R.id.setting_linear:
                activityUtil.jumpTo(SettingActivity.class);
                break;
            case R.id.tv_near_car://一键用车
//                isOneKeyUseCar = true;
//                //重新请求一下，获取最新的数据
//                Logger.i(TAG, "queryCityParkByCityCode一键用车");
//                queryParkRedPacketCar();

                getNearCars();
//                queryOneKeyUseCar();
                break;
            case R.id.ivRefreshMap://刷新地图
                Logger.i(TAG, "ivRefreshMap");
                mapCtrl.animateToZoom(Config.MAP_ZOOM_17);
                refreshAllData();
                break;
            case R.id.ivLocateMyPosition://回到当前位置
                Logger.i(TAG, "ivLocateMyPosition");
                mapCtrl.animateToMyPosition();
                break;
            case R.id.llytViolateWarn://违章处理
                Intent traffIntent = new Intent(getActivity(), TrafficViolationActivity.class);
                startActivity(traffIntent);
                break;
            case R.id.ivDragHandle://关闭网点和车辆内容
                hideParkCarInfo();
                break;
        }
    }

    /**
     * 选择头像
     */
    private void chooseHeadPic() {
        final CustomAlertDialogVerify dialogVerify = CustomAlertDialogVerify.getAlertDialog(getActivity(), true, true);
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
            ToastUtil.show(getActivity(), Config.PERMISSION_CAMERA);
            DataUtils.permissCamera(getActivity());
            return;
        }
        if (!DataUtils.permissCamera(getActivity())) {
            ToastUtil.show(getActivity(), Config.PERMISSION_CAMERA);
            return;
        }
        if (!DataUtils.permissStorage(getActivity())) {
            ToastUtil.show(getActivity(), Config.PERMISSION_STORAGE);
            return;
        }

        UUID uuid1 = UUID.randomUUID();
        picname = uuid1.toString() + ".jpg";
        mPicFile = new File(Environment.getExternalStorageDirectory(), picname);
        Intent cIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileProvider", mPicFile);
            cIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {
            cIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPicFile));
        }
        getActivity().startActivityForResult(cIntent, PHOTOHRAPH);
    }

    // 本地相册选择
    protected void gralleryUpload() {
        if (!DataUtils.permissStorage(getActivity())) {
            ToastUtil.show(getActivity(), Config.PERMISSION_STORAGE);
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
     * 一键用车
     */
    private void getNearCars() {
        String parkIds = getNearParkIds();
        if (!"".equals(parkIds)) {
            Logger.i(TAG, "收到的parkIds==" + parkIds);
            Intent intent = new Intent(getActivity(), ParkListActivity.class);
            intent.putExtra("parkIds", parkIds);
            intent.putExtra("oneKeyParks", (Serializable) oneKeyParks);
            startActivity(intent);
        } else {
            ToastUtil.show(getActivity(), "附近没有可用车辆");
        }
    }

    /**
     * 恢复数据
     */
    private void revivalData(UserInfoResp userInfoResp) {
        RecoverDataRequest data = new RecoverDataRequest();
        data.setMethod(RequestUrls.RECOVER_DATA);
        data.setCustomerPhone(userInfoResp.getPhone());
        doGet(data, REQUEST_RECOVER_DATA, Config.LOADING_STRING, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapCtrl.onResume();

        //登录后，或灰度接口请求完成后才请求用户信息
        if (CacheUtils.getIn().isLogin() && isGetUserInfo) {
            //重新获取用户信息
            queryUserInfo();
            updateUnreadMessage();
        } else {
            llytViolateWarn.setVisibility(View.GONE);
        }

        //判断GPS模块是否开启
        isOpenGps(getActivity());
        init();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapCtrl.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapCtrl.onDestroy();
        EventBus.getDefault().unregister(this);
        try {
            getActivity().unregisterReceiver(loginReceiver);
            getActivity().unregisterReceiver(systemReceiver);
            getActivity().unregisterReceiver(systemReceiver);
        } catch (Exception e) {
            loginReceiver = null;
            systemReceiver = null;
            systemReceiver = null;
        }

        try {
            getActivity().unregisterReceiver(clearChargeOrderReceiver);
        } catch (Exception e) {
            clearChargeOrderReceiver = null;
        }
        RxBusEvent event = new RxBusEvent();
        event.setEventCode(RxEventCodes.CODE_FINISH_LOAD_ACTIVITY);
        RxBus.getInstance().post(event);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapCtrl.onSaveInstanceState(outState);
    }

    /**
     * 定位结果
     *
     * @param code
     */
    public void locateResult(int code) {
        Logger.i(TAG, "收到定位结果=" + code);
        //定位结束后
        if (code == 12) {//定位错误
            DataUtils.permissLoca(getActivity());
        }
        mapCtrl.setMyMarker();
    }

    /**
     * 版本升级
     */
    private void checkUpdateVersion() {
        //版本升级
        if (application.isNeedForceUpdate && isShow) {
            showUpdateView();
            isShow = false;
        }
    }

    private void getSystemInfoOk(SystemConfigResp data) {
        Logger.i(TAG, "getSystemInfoOk");
        if (data != null) {
            CacheUtils.getIn().save(data);

            if (MyApplication.getApplication().isShowAd()) {
                //显示未读信息红点
                newMessageNum = data.getNewMessageNum();
                titleMainView.setUnReadMsgNum(newMessageNum);
            }
        }
    }

    /**
     * 获取附近空闲车辆停车场Id
     */
    private String getNearParkIds() {
        int freeCarNm = 9;

        int nearFreeCar = 0;
        String parkIds = "";
        oneKeyParks.clear();
        for (int i = 0; i < parksResps.size(); i++) {
            ParksResp parksResp = parksResps.get(i);
            int freeCarNum = DataUtils.toInt(parksResp.getParkFreeCarNum());
            nearFreeCar += freeCarNum;
            if (freeCarNum > 0) {
                if (parkIds.length() <= 0) {
                    parkIds = parksResp.getId();
                } else {
                    parkIds = parkIds + "," + parksResp.getId();
                }
                oneKeyParks.add(parksResp);
                Logger.i(TAG, "parkIds==" + parkIds);
                Logger.i(TAG, "车辆数==" + nearFreeCar);
                if (nearFreeCar > freeCarNm) {
                    Logger.i(TAG, "大于规定的车辆数==" + nearFreeCar);
                    return parkIds;
                }
            }
        }
        return parkIds;
    }

    /**
     * 显示下载框
     */
    private void showUpdateView() {
        if (updateView == null) {
            updateView = new UpdateView(getActivity());
            updateView.setListener(new UpdateView.UpdateListener() {
                @Override
                public void cancel() {
                    rlMainAll.removeView(updateView);
                }

                @Override
                public void confirm(String isUpdate) {
                    showToast(getActivity(), "开始下载");
                    if (!isUpdate.equals("true")) {
                        rlMainAll.removeView(updateView);
                    }
                    Intent it = new Intent(getActivity(), UpdateService.class);
                    getActivity().startService(it);
                    getActivity().bindService(it, conn, Context.BIND_AUTO_CREATE);
                    UpdateService.downoverListener = new UpdateService.DownoverListener() {
                        @Override
                        public void downOver() {
                            getActivity().unbindService(conn);
                        }
                    };
                }
            });
        }
        rlMainAll.addView(updateView, layoutParams);
    }

    public UpdateService.DownloadBinder binder;
    ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binder.cancel();
            getActivity().stopService(new Intent(getActivity(), UpdateService.class));
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (UpdateService.DownloadBinder) service;
            // 开始下载
            binder.start();
        }
    };

    /**
     * 获取系统参数
     */
    private void getSystemInfo() {
        SystemArgumentRequest data = new SystemArgumentRequest();
        data.setAddressType(RequestUrls.url);
        data.setMethod(RequestUrls.QUERY_SYSTEM_PARAM);
        doGet(data, SYSTEM_PARAM, null, false);
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


    private void searchPark(ParksResp parksRespSearch) {

        if (null == parksRespSearch) {
            return;
        }
        if (showPark != null) {
            //将之前选中的网点设置为非选中
            showPark.setSelected(false);
        }
        searchParksResp = parksRespSearch;
        refreshAllData();
    }

    /**
     * 切换地址接口
     */
    private void queryInterfaceAddress() {
        InterfaceAddressRequest request = new InterfaceAddressRequest();
        request.setPhone(UserUtils.getPhone());
        request.setMethod(RequestUrls.QUERY_INTERFACE_ADDRESS);
        doGet(request, QUERY_INTERFACE_ADDRESS, "", false);
    }

    /**
     * 网点分城市、区
     */
    private void queryCityParkByCityCode() {
        ParkPointListRequest data = new ParkPointListRequest();
        data.setMethod(RequestUrls.QUERY_CITY_AREA_ALL_PARK);
        doGet(data, QUERY_CITY_PARK_BY_CITYCODE, Config.LOADING_STRING, false);
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        PackageManager pm = application.getPackageManager();
        try {
            pi = pm.getPackageInfo(application.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        APPVersionRequest data = new APPVersionRequest();
        if (pi != null) {
            data.setAppVersionRecords(pi.versionCode + "");
        }
        data.setAppVersionType("android");
        data.setAddressType(RequestUrls.url);
        data.setMethod(RequestUrls.QUERY_VERSION_RECORD);
        doGet(data, checkUpdate, "", false);
    }

    /**
     * 查询网点红包车
     */
    private void queryParkRedPacketCar() {
        doCheck(Config.LOADING_STRING, true);
        ParkRedPacketCarRequest request = new ParkRedPacketCarRequest(UserUtils.getCityCode());
        request.setMethod(RequestUrls.QUERY_PARK_RED_PACKET_CAR);
        doGet(request, QUERY_PARK_RED_PACKET_CAR, Config.LOADING_STRING, false);
    }

    /**
     * 刷新消息的红点
     */
    private void updateUnreadMessage() {
        SystemConfigResp systemConfigResp = CacheUtils.getIn().getSystem_Info();
        if (null != systemConfigResp) {
            newMessageNum = systemConfigResp.getNewMessageNum();
            titleMainView.setUnReadMsgNum(newMessageNum);
        }
    }

    /**
     * 显示活动广告弹窗
     *
     * @param splashAndActivityResps
     */
    private void showActivityAd(final ArrayList<SplashAndActivityResp> splashAndActivityResps) {
        if (splashAndActivityResps != null && splashAndActivityResps.size() > 1) {
            //延迟500ms显示活动弹框
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<SplashAndActivityResp> data = new ArrayList<>();
                            data.addAll(splashAndActivityResps);
                            data.remove(0);//移除的第一个是启动页的图片
                            if (data.size() > 1) {
                                //大于一张图片要循环滚动
                                SplashAndActivityResp first = data.get(0);
                                SplashAndActivityResp last = data.get(data.size() - 1);
                                data.add(first);//在原本的数据列表的最后一个位置添加第一条数据
                                data.add(0, last);//在原本的数据列表的第一个位置添加最后一条数据
                            }
                            new AdPPW().showAdPPW(getActivity(), data);

                        }
                    });
                }
            }, 500);
            MyApplication.getApplication().setShowAd(false);
        }
    }

    /**
     * 获取车辆列表
     */
    private void getCarList() {
        if (showPark == null) {
            return;
        }
        GetCarListRequest data = new GetCarListRequest();
        data.setDepotId(showPark.getId());
        data.setMethod(RequestUrls.QUERY_CAR_LIST_BY_PARK_ID);
        doGet(data, QUERY_CAR_LIST, Config.LOADING_STRING, true);
    }

    private void preSetScrollLayout() {
        scrollDownLayout.setMaxOffset(0);
        scrollDownLayout.setToOpen();
    }



    /**
     * 设置scrollLayout属性
     */
    private void setScrollLayout() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //屏幕高度，不包括状态栏
                        int screenHeight = getScreenHeight();
                        //显示的内容区域的高度
                        int contentHeight = getContentHeight();
                        int titleHeight = getTitleHeight();
                        int titleWidth = getTitleWidth();

                        setMapCenter(titleWidth, screenHeight, contentHeight, titleHeight);
                        if (showPark != null) {
                            mapCtrl.animateToLatLng(showPark.getLatlng());
                        }

                        //显示内容
//                        scrollDownLayout.setVisibility(View.VISIBLE);
                        scrollDownLayout.setMinOffset(screenHeight - contentHeight);
                        scrollDownLayout.setMaxOffset(0);
                        scrollDownLayout.setIsSupportExit(false);
                        scrollDownLayout.setAllowHorizontalScroll(true);
                        scrollDownLayout.setToClosed();
                    }
                });
            }
        }, 100);
    }

    private int getScreenHeight() {
        return scrollDownLayout.getScreenHeight();
    }

    private int getContentHeight() {
        return ivDragHandle.getMeasuredHeight() +
                parkInfoView.getMeasuredHeight() + llytCarContent.getMeasuredHeight() + scrollLayoutPadding;
    }

    private int getTitleHeight() {
        return titleMainView.getMeasuredHeight();
    }

    private int getTitleWidth() {
        return titleMainView.getMeasuredWidth();
    }

    private void setMapCenter(int titleWidth, int screenHeight, int contentHeight,
                              int titleHeight) {
        //设置地图中心点
        mapCtrl.setMapCenterPoint(titleWidth / 2, (screenHeight - contentHeight - titleHeight) / 2);
    }

    /**
     * 设置车辆列表信息
     */
    private void setCarsListPagerData() {
//        if (scrollLayoutStatus == CARS_PAGE) {
        if (carsListFragments.size() == 0) {
            boolean hasCar = carList.size() > 0;

            if (hasCar) {
                //移除所有小圆点
                llytPointContainer.removeAllViews();
                for (int i = 0; i < carList.size(); i++) {
                    if (i % 2 == 0) {
                        CarsListFragment fragment = new CarsListFragment();
                        List<CarResp> list = new ArrayList<>();
                        Bundle bundle = new Bundle();
                        list.add(carList.get(i));
                        int j = i + 1;
                        if (j < carList.size()) {
                            list.add(carList.get(j));
                        }
                        bundle.putSerializable("carList", (Serializable) list);
                        bundle.putSerializable("parkInfo", showPark);
                        fragment.setArguments(bundle);
                        carsListFragments.add(fragment);

                        if (carList.size() > 1) {//多于一页才有圆点
                            View view = getLayoutInflater().inflate(R.layout.layout_point, llytPointContainer, false);
                            if (i == 0) {//第一个选中，蓝色
                                View viewPoint = view.findViewById(R.id.viewPoint);
                                viewPoint.setBackgroundResource(R.drawable.point_blue);
                            }
                            llytPointContainer.addView(view);
                        }
                    }
                }

                if (carList.size() <= 1) {
                    //隐藏底部的小圆点
                    viewDivideLinePoint.setVisibility(View.GONE);
                    llytPointContainer.setVisibility(View.GONE);
                } else {
                    viewDivideLinePoint.setVisibility(View.VISIBLE);
                    llytPointContainer.setVisibility(View.VISIBLE);
                }
                MyFragmentStatePagerAdapter fragmentStatePagerAdapter = new MyFragmentStatePagerAdapter(getActivity().getSupportFragmentManager(), carsListFragments);
                viewPagerCarInfo.setAdapter(fragmentStatePagerAdapter);

                viewPagerCarInfo.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        int pointCount = llytPointContainer.getChildCount();

                        for (int i = 0; i < pointCount; i++) {
                            View view = llytPointContainer.getChildAt(i);
                            View viewPoint = view.findViewById(R.id.viewPoint);
                            if (i == position) {
                                viewPoint.setBackgroundResource(R.drawable.point_blue);
                            } else {
                                viewPoint.setBackgroundResource(R.drawable.point_gray_aaa);
                            }
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
            viewPagerContainer.setVisibility(hasCar ? View.VISIBLE : View.GONE);
            tvNoCarTip.setVisibility(!hasCar ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 显示当前选中的网点的信息
     */
    private void showSelectedParkInfo() {
        if (showPark == null) {
            return;
        }
        parkInfoView.setData(getActivity(), showPark);
    }

    /**
     * 隐藏网点和车辆信息
     */
    private void hideParkCarInfo() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //        scrollDownLayout.setVisibility(View.INVISIBLE);
                if (scrollDownLayout != null) {
                    scrollDownLayout.scrollToOpen();
                    showPark = null;
                }
            }
        });
    }

    private void setCarListData() {
//                scrollLayoutStatus = CARS_PAGE;
        carsListFragments.clear();
        setCarsListPagerData();
        setScrollLayout();
    }

    /**
     * 一键用车
     */
    private void queryOneKeyUseCar() {
        OneKeyUseCarRequest request = new OneKeyUseCarRequest();
        request.setMethod(RequestUrls.QUERY_RECENTELY_PARK);

        doGet(request, QUERY_ONE_KEY_USE_CAR, Config.LOADING_STRING, true);
    }
}