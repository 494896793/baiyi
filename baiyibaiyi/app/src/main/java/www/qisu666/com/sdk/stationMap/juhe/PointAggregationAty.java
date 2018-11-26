package www.qisu666.com.sdk.stationMap.juhe;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.NaviPara;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import www.qisu666.com.R;
import www.qisu666.com.activity.BaseActivity;
import www.qisu666.com.activity.CaptureActivity;
import www.qisu666.com.activity.ChargingActivity;
import www.qisu666.com.activity.LoginActivity;
import www.qisu666.com.activity.NaviActivity;
import www.qisu666.com.activity.StationInfoActivity;
import www.qisu666.com.adapter.StationListAdapter;
import www.qisu666.com.anim.CloseEvarlutor;
import www.qisu666.com.anim.OpenEvarlutor;
import www.qisu666.com.app.MyApplication;
import www.qisu666.com.droid.Activity_SelectCity;
import www.qisu666.com.event.Message;
import www.qisu666.com.request.MyNetwork;
import www.qisu666.com.request.utils.FlatFunction;
import www.qisu666.com.request.utils.MyMessageUtils;
import www.qisu666.com.request.utils.ResultSubscriber;
import www.qisu666.com.request.utils.RxNetHelper;
import www.qisu666.com.sdk.stationMap.StationLocation;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.ConstantCode;
import www.qisu666.com.utils.DialogHelper;
import www.qisu666.com.utils.Distance;
import www.qisu666.com.utils.JsonUtils;
import www.qisu666.com.utils.LogUtils;
import www.qisu666.com.utils.MapUtils;
import www.qisu666.com.utils.PermissionUtil;
import www.qisu666.com.utils.StringUtil;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.widget.LoadingDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.squareup.picasso.Picasso;
import com.ta.utdid2.android.utils.NetworkUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.util.LogUtil;
import org.xutils.x;

import java.io.File;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//桩点列表
public class PointAggregationAty extends BaseActivity implements AMap.OnMapLoadedListener, LocationSource, OnClickListener, AMap.OnCameraChangeListener, AMap.OnMarkerClickListener, AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener {

    private MapView mapView;//**  地图view  */
    private AMap aMap;    //***  高德amap */
    private int screenHeight;// 屏幕高度(px)
    private int screenWidth;// 屏幕宽度(px)
    private ArrayList<MarkerOptions> markerOptionsListall = new ArrayList<MarkerOptions>();    //** 所有的marker  */
    private ArrayList<MarkerOptions> markerOptionsListInView = new ArrayList<MarkerOptions>();//**  视野内的marker  */
    private ImageView img_location;
    private LinearLayout tips;


    /**
     * 新添加  全部桩子的数据结构  ,保存从数据库中查询到的数据
     */
    List<StationLocation> infoList = new ArrayList<>();
    private List<Map<String, Object>> list = new ArrayList<>();
    Map<String, Object> maps_info;//全部桩子缓存

    private double curLat, curLon, markerLat, markerLon;//当前位置经纬度
    private boolean favorFlag = false;      //当前站点是否被收藏

    private TextView current_city;//当前城市
    private String current_city_str = "";//当前城市str
    //筛选条件
    private String charge_interface, charge_carr, charge_method, charge_pile_bel, parking_free, service_time;
    private Map<String, String> filterMap;  //当前筛选条件集合

    private LocationSource.OnLocationChangedListener mListener;//当前位置监听
    private GeocodeSearch geocoderSearch;                    //反地理编码
    //当前是否geo定位
    LatLonPoint afterGeoPoint;     //geo编码后的点
    private boolean isGeo = false; //是否切换城市
    private LoadingDialog dialog;           //请求定位的Dialog

    private PopupWindow mapPopupWindow;
    private AMapLocationClient mlocationClient;

    private String area_code = "5810";//定位城市code
    private String cityCode = "5801";

    //桩点列表
    private ImageView right_btn, imgCharge;

    private RelativeLayout layout_map, rl_list_frame, layout_main;
    private StationListAdapter adapter;
    private PullToRefreshListView lv_station_list;
    private boolean isShowList = false;
    TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_aggregation);
        initTitleBar();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = ScreenUtils.getScreenWidth(this);
        screenHeight = ScreenUtils.getScreenHeight(this);
        mapView = (MapView) findViewById(R.id.map);
        tips=findViewById(R.id.tips);
        mapView.onCreate(savedInstanceState);// 方法必须重写

        if (aMap == null) {
            aMap = mapView.getMap();
            UiSettings mUiSettings = aMap.getUiSettings();//拿到地图工具类
            mUiSettings.setTiltGesturesEnabled(false);// 禁用倾斜手势。
            mUiSettings.setRotateGesturesEnabled(false);// 禁用旋转手势。
            mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM);//放大缩小按钮放在屏幕中间
            aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
            aMap.setOnCameraChangeListener(this);// 对amap添加移动地图事件监听器
        }

        MyLocationStyle myLocationStyle = new MyLocationStyle(); // 自定义系统定位小蓝点
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.my_location_marker));// 设置小蓝点的图标
//        myLocationStyle.strokeColor(android.R.color.transparent);// 设置圆形的边框颜色
//        myLocationStyle.radiusFillColor(Color.argb(100,55,115,203));// 设置圆形的填充颜色
//        myLocationStyle.anchor(0.5f, 0.5f);//设置小蓝点的锚点
//        aMap.setMyLocationStyle(myLocationStyle);
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色   这两句取消原形边框
        aMap.setMyLocationStyle(myLocationStyle);

        aMap.setLocationSource(this);// 设置定位监听
//        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置定位按钮是否显示
//        aMap.getUiSettings().setZoomControlsEnabled(true);// 设置缩放按钮是否显示
        aMap.setMyLocationEnabled(true);


        tips.getBackground().setAlpha(240);

        try {//先获取到当前位置
            mlocationClient = new AMapLocationClient(this);
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            mlocationClient.setLocationListener(this);//设置定位监听
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置为高精度定位模式
            mlocationClient.setLocationOption(mLocationOption);//设置定位参数
            mLocationOption.setOnceLocation(true);
            mlocationClient.startLocation();
            x.task().postDelayed(new Runnable() {
                @Override
                public void run() {
                    x.task().autoPost(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mlocationClient.stopLocation();
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                    });
                }
            }, 1500);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        initView();
        initPopupWindow();
        initMapPopupWindow();
        // 添加临时数据
        initDatass();

        CameraPosition.Builder builder = CameraPosition.builder();
        builder.target(new LatLng(22.5472000000, 114.0842620000));//先移动地图到广州   反地理编码失效
        builder.zoom(16.0f);
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
    }



    private void initTitleBar() {
        title = (TextView) findViewById(R.id.tv_title);
        title.setText("桩点地图");
        View left_btn = findViewById(R.id.img_title_left);
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowList) {
                    showOrHideList();
                    return;
                } else {
                    finish();
                }//兼容列表的返回

            }
        });

        current_city = (TextView) findViewById(R.id.current_city);//当前城市
        current_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCity();//测试移动地图    移动城市无法选择
            }
        });

    }


    void initView() {
        img_location = (ImageView) findViewById(R.id.img_location);
        imgCharge = (ImageView) findViewById(R.id.img_charge);
        img_location.setOnClickListener(this);
        rl_list_frame = (RelativeLayout) findViewById(R.id.rl_list_frame);
        rl_list_frame.setVisibility(View.GONE);
        layout_map = (RelativeLayout) findViewById(R.id.layout_map);
        layout_main = (RelativeLayout) findViewById(R.id.point_main);
        lv_station_list = (PullToRefreshListView) findViewById(R.id.lv_station_list);
        lv_station_list.setMode(PullToRefreshBase.Mode.BOTH);

        lv_station_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

            //下拉刷新时会回调的方法
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                pageIndex = 1;
                getList();
            }

            //上啦加载时执行的方法
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                pageIndex++;
                getList();
            }
        });

        lv_station_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                station_id = list.get(position).get("stationId").toString();
                station_name = list.get(position).get("stationName").toString();
                intoDetail();
            }
        });
        imgCharge.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //共享充电
                if (CacheUtils.getIn().isLogin()) {
//                    doCharge();
                    connToServerE106(true);
                } else{
                    ToastUtil.showToast(R.string.toast_prompt_login);
                    Intent i = new Intent(PointAggregationAty.this, LoginActivity.class);
                    startActivityForResult(i, ConstantCode.REQ_LOGIN);
                }
            }
        });
        initFilter();
    }

    private void doCharge() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (PermissionUtil.cameraIsCanUse()) {
                startActivity(new Intent(this, CaptureActivity.class));
            } else {
                ToastUtil.showToast(R.string.toast_permission_camera);
            }
        } else {
            if (PermissionUtil.checkPermission(this, ConstantCode.CAMERA,
                    Manifest.permission.CAMERA)) {
//                connToServerE106(true);
                startActivity(new Intent(this, CaptureActivity.class));
            }
        }

    }

    private void connToServerE106(final boolean isOnClick) {

        String url = "api/charge/order/status/query";
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", CacheUtils.getIn().getUserInfo().getId());

        MyNetwork.getMyApi()
                .carRequest(url, MyMessageUtils.addBody(map))
                .map(new FlatFunction<>(Object.class))
                .compose(RxNetHelper.<Object>io_main())
                .subscribe(new ResultSubscriber<Object>() {
                    @Override
                    public void onSuccessCode(Message object) {

                    }

                    @Override
                    @SuppressWarnings("unchecked")
                    public void onSuccess(Object bean) {
                        // 对象转json
                        String s = JsonUtils.objectToJson(bean);
                        // json转 map
                        Map jsonToMap = JsonUtils.jsonToMap(s);

                        String state = jsonToMap.get("orderStatus").toString();
//                        if (ChargeStatus.INSTANCE.getStatus() != ChargeStatus.STATUS_PREPARE_STOP) {
//                            if ("02".equals(state)) {
//                                ChargeStatus.INSTANCE.setStatus(ChargeStatus.STATUS_CHARGING);
//                                // tv_index_charge.setText(R.string.home_is_charging);
//                            } else if ("03".equals(state)) {
//                                ChargeStatus.INSTANCE.setStatus(ChargeStatus.STATUS_UNKNOWN);
//                                // tv_index_charge.setText(R.string.home_charge_now);
//                            } else {
//                                ChargeStatus.INSTANCE.setStatus(ChargeStatus.STATUS_FREE);
//                                // tv_index_charge.setText(R.string.home_charge_now);
//                            }
//
//
//                        }
                        //                out_trade_no = map.get("out_trade_no").toString();

                        if (isOnClick) {
                            if ("02".equals(state)) {
                                //todo
                                // 充电页面
                                CacheUtils.getIn().getUserInfo().setOut_trade_no(jsonToMap.get("outTradeNo").toString());
                                Log.e("aaaa", "outTradeNo" + jsonToMap.get("outTradeNo").toString());
                                Log.e("aaaa", "INSTANCE" + CacheUtils.getIn().getUserInfo().getOut_trade_no());

                                startActivity(new Intent(PointAggregationAty.this, ChargingActivity.class));
                            } else {
                                // 扫码充电
//                                startActivity(new Intent(PointAggregationAty.this, CaptureActivity.class));
                                doCharge();
                            }
                        }
                    }

                    @Override
                    public void onFail(Message<Object> bean) {
                        ToastUtil.showToast(bean.msg);
                        Log.e("aaaa", "获取失败：" + bean.toString());
                    }

                });
    }

    /**
     * 初始化筛选条件
     */
    private void initFilter() {
        right_btn = (ImageView) findViewById(R.id.img_title_right);
        ImageView right_btn2 = (ImageView) findViewById(R.id.img_title_right2);
        right_btn.setImageResource(R.mipmap.ic_station_list);
        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrHideList();
            }
        });

//        right_btn2.setImageResource(R.mipmap.ic_station_filter);
//        right_btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(PointAggregationAty.this, ChargingFilterActivity.class);
//                intent.putExtra("filterMap", (HashMap) filterMap);
//                startActivityForResult(intent, ConstantCode.REQ_FILTER);
//            }
//        });

        filterMap = new HashMap<>();
        filterMap.put("charge_interface", null);
        filterMap.put("charge_carr", null);
        filterMap.put("charge_method", null);
        filterMap.put("charge_pile_bel", null);
//        filterMap.put("pile_state", null);
//        filterMap.put("charging_gun", null);
        filterMap.put("parking_free", null);
        filterMap.put("service_time", null);


        list = new ArrayList<>();
        adapter = new StationListAdapter(this, list, new StationListAdapter.OnChargeBtnClickListener() {
            @Override
            public void onChargeClick(LatLng latLng) {
                markerLat = latLng.latitude;
                markerLon = latLng.longitude;
                mapPopupWindow.showAtLocation(findViewById(R.id.layout_map), Gravity.BOTTOM, 0, 0);
            }
        });
        lv_station_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    private void initDatass() {//模拟1000条数据
        update_fromeDB();
//		for(int i=0;i<1000;i++) {
//			Random r = new Random();
//			double lat = (290000 + r.nextInt(30000)) / 10000.0D;
//			double lng = (1120000 + r.nextInt(30000)) / 10000.0D;
//			addDate(lat, lng,i+","+i%3+"."+i*2);//桩子总数,类型，station id
//		}
    }

    // 添加临时数据
    private void addDate(double latitude, double longitude, String str) {
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        markerOptions.title(str);
        markerOptionsListall.add(markerOptions);
    }

    float leavel = 0;

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.e("点击mark", "");
        Log.e("点击mark", "" + marker.getOptions().getTitle());
        String aa=marker.getOptions().getTitle();
        if (marker.getOptions().getTitle().equals("d")) {
            leavel = aMap.getCameraPosition().zoom;
            leavel = leavel + 3;
            aMap.moveCamera(CameraUpdateFactory.zoomTo(leavel));
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker.getPosition()));
        } else {
            markerLat = marker.getPosition().latitude;
            markerLon = marker.getPosition().longitude;
            aMap.moveCamera(CameraUpdateFactory.zoomTo( 19));
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(marker.getPosition()));
            String tmp = marker.getOptions().getTitle();
            station_id = tmp.substring(tmp.indexOf(".") + 1, tmp.indexOf("-"));
            if (marker.getTitle().contains("当前")) {
                return true;
            }  //如果是当前位置 不显示pop
            getPopInfo();
        }
        return true;
    }

    //获取弹窗信息
    private void getPopInfo() {

        String url = "api/pile/station/query";
        HashMap<String, Object> map = new HashMap<>();
        map.put("latitude", "" + curLat);
        map.put("longitude", "" + curLon);
        map.put("stationId", station_id);

        MyNetwork.getMyApi()
                .carRequest(url, MyMessageUtils.addBody(map))
                .map(new FlatFunction<>(Object.class))
                .compose(RxNetHelper.<Object>io_main())
                .subscribe(new ResultSubscriber<Object>() {
                    @Override
                    public void onSuccessCode(Message object) {

                    }

                    @Override
                    @SuppressWarnings("unchecked")
                    public void onSuccess(Object bean) {
                        // 对象转json
                        String s = JsonUtils.objectToJson(bean);
                        // json转 map
                        Map jsonToMap = JsonUtils.jsonToMap(s);
                        Log.e("aaaa", "获取到单个桩子信息 ：" + jsonToMap.toString());
                        try {
                            showPopWindow(jsonToMap);
                        } catch (Throwable t) {
                            LogUtil.e("获取到单个桩子信息 进入异常");
                            t.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(Message<Object> bean) {
                        Log.e("aaaa", "获取失败：" + bean.toString());
                    }

                });
    }

    /**
     * 新添加 单个桩点信息的重载
     */
    @SuppressLint("SetTextI18n")
    private void showPopWindow(Map<String, Object> map) throws JSONException {
//        Object oRecordList = map.get("record_list");
//        JSONObject object_item = null;
        if (null != map) {
//            String recordList = oRecordList.toString();
//            object_item = new JSONObject(recordList);
//
//            LogUtils.d("recordList: " + object_item);
//            try {
//                JSONArray array = new JSONArray(recordList);
//                int count = array.length();
//                if (count == 0) {
//                    DialogHelper.alertDialog(StationMapActivity_amap.this, getString(R.string.dialog_D101_no_data));
//                } else {
//                    object_item = array.getJSONObject(0);//拿到信息后   String oString = object_item.toString();// LogUtils.d("oString: " + oString);
//                }
//            } catch (Throwable t) {
//                t.printStackTrace();
//            }
//            if (object_item == null) {
//                return;
//            }
            String is_favor = map.get("isFavor").toString();
            favorFlag = is_favor.equals("0");
            if (is_favor.equals("0")) {
                Picasso.with(PointAggregationAty.this).load(R.mipmap.ic_collection_yellow).noFade().into(iv_collection);
            } else if (is_favor.equals("1")) {
                Picasso.with(PointAggregationAty.this).load(R.mipmap.ic_collection_gray).noFade().into(iv_collection);
            }
            station_name = map.get("stationName").toString();
            tv_map_title.setText(map.get("stationName").toString());


//            double   charge_fee= new BigDecimal((String) object_item.get("charge_fee_per")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();//保留两位小数
//            double   service_fee= new BigDecimal((String) object_item.get("service_fee")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();//保留两位小数
//            double   park_fee= new BigDecimal((String) object_item.get("park_fee")).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();//保留两位小数
//
//            tv_charging_fee.setText(charge_fee+"");       //当前电费
//            tv_service_fee.setText(service_fee+"");       //服务费
//            tv_station_parking_fee.setText(park_fee+"");  //停车费

            try {//当前电费  计算结果
                double chargingfee = Double.parseDouble(map.get("chargeFeePer").toString());
                double servicefee = Double.parseDouble(map.get("serviceFee").toString());
                tv_charging_fee.setText(new BigDecimal(chargingfee - servicefee).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "");//充电费=chargingFee-服务费
            } catch (Throwable t) {
                t.printStackTrace();
            }

//            tv_charging_fee.setText((String) object_item.get("charge_fee_per"));//当前电费  未计算
            tv_service_fee.setText(map.get("serviceFee").toString());//服务费
            tv_station_parking_fee.setText(map.get("parkFee").toString());//停车费

            String s1_lat = map.get("latitude").toString();
            String s1_lon = map.get("longitude").toString();

            double km = 0.0;
            try {
                double lat = Double.parseDouble(s1_lat);
                double lon = Double.parseDouble(s1_lon);
                km = Distance.getDistance(curLat, curLon, lat, lon);
                km = new BigDecimal(km).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();//保留两位小数
            } catch (Throwable t) {
                t.printStackTrace();
                km = 0.0;
            }
            tv_map_distance.setText(new BigDecimal(map.get("chargeDistance").toString()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + " km");

//            double b = new BigDecimal((String) object_item.get("charge_distance")).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
//            tv_map_distance.setText(String.valueOf(b) + "km");
            tv_free_fast.setText("空闲" + map.get("pileFastNumFree").toString());
            tv_free_slow.setText("空闲" + map.get("pileSlowNumFree").toString());
            tv_map_address.setText(map.get("chargeAddress").toString());
            // tv_elec_type.setText(markerModel.getElec_type().equals("00") ? "交流" : markerModel.getElec_type().toString().equals("01") ? "直流" : "直流/交流");
            tv_elec_type.setText("总桩数" + map.get("totalPileCount").toString());
            pop.showAtLocation(findViewById(R.id.point_main), Gravity.BOTTOM, 0, 0);
        }
    }


    /**
     * 桩子信息等textview的声明
     */
    private TextView tv_map_address, tv_map_title, tv_charging_fee, tv_service_fee, tv_station_parking_fee, tv_map_distance, tv_free_fast, tv_free_slow, tv_elec_type;
    private ImageView iv_collection;
    private View p_station_window;
    private PopupWindow pop;
    private String station_id = ""; //选中的站点id
    private String station_name = "";//选中的站点名称

    /**
     * 站点详情PopupWindow
     */
    private void initPopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.popup_station_info, null); // 一个自定义的布局，作为显示的内容
        View btn_map_charging = contentView.findViewById(R.id.btn_map_charging);
        tv_map_address = (TextView) contentView.findViewById(R.id.tv_map_address);
        tv_map_title = (TextView) contentView.findViewById(R.id.tv_map_title);
        tv_charging_fee = (TextView) contentView.findViewById(R.id.tv_charging_fee);
        tv_service_fee = (TextView) contentView.findViewById(R.id.tv_service_fee);
        tv_station_parking_fee = (TextView) contentView.findViewById(R.id.tv_station_parking_fee);
        tv_map_distance = (TextView) contentView.findViewById(R.id.tv_map_distance);
        tv_free_fast = (TextView) contentView.findViewById(R.id.tv_free_fast);
        tv_free_slow = (TextView) contentView.findViewById(R.id.tv_free_slow);
        tv_elec_type = (TextView) contentView.findViewById(R.id.tv_elec_type);
        iv_collection = (ImageView) contentView.findViewById(R.id.iv_collection);
        if (TextUtils.isEmpty(CacheUtils.getIn().getUserInfo().getId())) {
            iv_collection.setVisibility(View.GONE);
        } else {
            iv_collection.setVisibility(View.VISIBLE);
        }

        tv_map_address.setText("定位中...");

        p_station_window = contentView.findViewById(R.id.p_station_window);
        p_station_window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intoDetail();
                pop.dismiss();
            }
        });

        iv_collection.setOnClickListener(this);
        btn_map_charging.setOnClickListener(this);

        pop = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setTouchable(true);
        pop.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        pop.setBackgroundDrawable(getResources().getDrawable(R.color.white));
        pop.setAnimationStyle(R.style.Popup_Anim_Bottom);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                try {
                    pop.dismiss();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });

    }

    /**
     * 选择地图PopupWindow
     */
    private void initMapPopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.popup_choice_map, null);// 一个自定义的布局，作为显示的内容

        TextView tv_start_navi = (TextView) contentView.findViewById(R.id.tv_start_navi);
        TextView tv_amap = (TextView) contentView.findViewById(R.id.tv_amap);
        TextView tv_baidu = (TextView) contentView.findViewById(R.id.tv_baidu);
        TextView tv_cancel = (TextView) contentView.findViewById(R.id.tv_cancel);

        tv_start_navi.setOnClickListener(this);
        tv_amap.setOnClickListener(this);
        tv_baidu.setOnClickListener(this);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapPopupWindow.dismiss();
            }
        });
        mapPopupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mapPopupWindow.setTouchable(true);
        mapPopupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        mapPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
        mapPopupWindow.setAnimationStyle(R.style.Popup_Anim_Bottom);
    }


    /**
     * 选择城市 按钮事件
     */
    private void selectCity() {
        LogUtils.i("点击城市选择按钮");
        Intent intent = new Intent(PointAggregationAty.this, Activity_SelectCity.class);//跳转城市选择页面  接收返回的结果
        startActivityForResult(intent, ConstantCode.SELECT_CITY);
    }

    /**
     * 跳转到桩子详情页面
     */
    private void intoDetail() {
        LogUtil.e("跳转到详情页id: " + station_id + ",name:" + station_name);
        Intent intent = new Intent(PointAggregationAty.this, StationInfoActivity.class);
        intent.putExtra("station_id", station_id);
        intent.putExtra("station_name", station_name);
        startActivityForResult(intent, ConstantCode.REQ_OPEN_STATION_INFO);
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
    } //地图改变时

    boolean isfistFinish = false;//首次进入地图 缩放级别16

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

        try {
            locationMarker = null;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        resetMarks();
        //可在其中解析amapLocation获取相应内容。
        try {
            aMap.getMapScreenMarkers().remove(locationMarker);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        x.task().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {//尝试添加自己位置的图标
                    LatLng latLng = new LatLng(curLat, curLon);//取出经纬度
                    //添加Marker显示定位位置
                    if (locationMarker == null) {
                        //如果是空的添加一个新的,icon方法就是设置定位图标，可以自定义
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.visible(true);
                        markerOptions.title("当前位置");
                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_nearby_charge));
                        markerOptions.icon(bitmapDescriptor);
                        aMap.addMarker(markerOptions);
                        locationMarker = aMap.addMarker(markerOptions);
                        if (isfistFinish) {
                            CameraPosition.Builder builder = CameraPosition.builder();
                            builder.target(latLng);//移动地图到定位地点
                            builder.zoom(16.0f);
                            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
                            isfistFinish = false;
                        }

//				locationMarker.showInfoWindow();//主动显示indowindow
//			aMap.addText(new TextOptions().position(latLng).text(aMapLocation2.getAddress()));
                        //固定标签在屏幕中央
//                    locationMarker.setPositionByPixels(aMap.getWidth() / 2,mMapView.getHeight() / 2);
                    } else {
                        //已经添加过了，修改位置即可
                        locationMarker.setPosition(latLng);
                        if (isfistFinish) {
                            CameraPosition.Builder builder = CameraPosition.builder();
                            builder.target(latLng);//移动地图到定位地点
                            builder.zoom(16.0f);
                            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
                            isfistFinish = false;
                        }
                    }


                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }, 2000);


    }// 地图改变完之后的监听

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }//必须重写

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            markerOptionsListall.clear();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        try {
            mapView.onDestroy();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    //聚合  入口点

    /**
     * 获取视野内的marker 根据聚合算法合成自定义的marker 显示视野内的marker
     */
    private void resetMarks() {
        Projection projection = aMap.getProjection();         // 开始刷新
        Point p = null;
        markerOptionsListInView.clear();
        for (MarkerOptions mp : markerOptionsListall) {       // 获取在当前视野内的marker;提高效率
            p = projection.toScreenLocation(mp.getPosition());//计算屏幕中点坐标
            if (p.x < 0 || p.y < 0 || p.x > screenWidth || p.y > screenHeight) {// 屏幕外

            } else {
                markerOptionsListInView.add(mp);//加入计算
            }
        }
        //自定义的聚合类MyMarkerCluster
        ArrayList<MarkerImageView> clustersMarker = new ArrayList<MarkerImageView>();
        for (MarkerOptions mp : markerOptionsListInView) {
            if (clustersMarker.size() == 0) {             // 添加一个新的自定义marker
                clustersMarker.add(new MarkerImageView(PointAggregationAty.this, mp, projection, 120, 1));// 80=相距多少才聚合
            } else {
                boolean isIn = false;
                for (MarkerImageView cluster : clustersMarker) {
                    if (cluster.getBounds().contains(mp.getPosition())) {// 判断当前的marker是否在前面marker的聚合范围内 并且每个marker只会聚合一次。
                        cluster.addMarker(mp);
                        isIn = true;
                        break;
                    }
                }
                // 如果没在任何范围内，自己单独形成一个自定义marker。在和后面的marker进行比较
                if (!isIn) {
                    clustersMarker.add(new MarkerImageView(PointAggregationAty.this, mp, projection, 120, clustersMarker.size()));// 80=相距多少才聚合
                }
            }
        }
        // 设置聚合点的位置和icon
        for (MarkerImageView mmc : clustersMarker) {
            mmc.setpositionAndIcon();
        }
        aMap.clear();
        // 重新添加 marker
        for (MarkerImageView cluster : clustersMarker) {
            Marker marker = aMap.addMarker(cluster.getOptions());
            marker.setTitle(cluster.getOptions().getTitle());
        }

        EventBus.getDefault().post("我的位置");
    }

    //原始逻辑

    /**
     * D101的本地数据更新 如果本地有数据不再调用connToServer()改为调用该函数
     */
    private void updateToServer() {  //从本地数据进行更新
        aMap.clear(true);//清空缓冲数据   防止不更新
        list.clear();
        Object oRecordList = maps_info.get("record_list"); //
        if (null != oRecordList) {
            String recordList = oRecordList.toString();
            LogUtils.d("recordList: " + recordList);
            try {
                JSONArray array = new JSONArray(recordList);
                int count = array.length();
                if (count == 0) {
                    DialogHelper.alertDialog(this, getString(R.string.dialog_D101_no_data));
                } else {
                    for (int i = 0; i < count; i++) {
                        JSONObject object = array.getJSONObject(i);
                        String oString = object.toString();
                        LogUtil.d("转换总数: " + i + "/" + count);
                        LogUtil.d("oString: " + oString);
                        list.add(JsonUtils.jsonToMap(oString));//添加桩子
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 0325修改-获取30km内地图桩子信息
     */
    private void connToServer2() {
//        connToServer();
        update_fromeDB();//走原来的逻辑
    }

    //从本地数据库中获取 桩子信息数据
    private void update_fromeDB() {
        x.task().run(new Runnable() {
            @Override
            public void run() {
                try {
                    infoList = MyApplication.getApplication().db.findAll(StationLocation.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (infoList == null) {
                        x.task().autoPost(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast("暂未获取到数据,请检查网络并重试!");
                            }
                        });
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                x.task().autoPost(new Runnable() {
                    @Override
                    public void run() {
                        update_mark();
                    }
                });  //在ui线程中进行更新
            }
        });

    }



    //获取桩子信息后 在地图上更新ui层
    private void update_mark() {
        try {
            if (infoList == null) {
                return;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return;
        }//为空返回,异常返回
        x.task().run(new Runnable() {
            @Override
            public void run() {  //异步更新
                try {
                    aMap.clear(true);
//                Collections.sort(infoList, new newSortByDistance());//排序    // newSortByDistance   不再排序
                    LogUtil.e("数据库中 获取到的桩子信息 size：" + infoList.size());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                if (infoList == null) {
                    return;
                }
                final int count = infoList.size();
                if (count <= 0) {
                    return;
                }  //提示没有桩子信息

                x.task().autoPost(new Runnable() {
                    @Override
                    public void run() {
                        if (!markerOptionsListall.isEmpty()) {
                            return;
                        }
                        for (int i = 0; i < count; i++) {
                            try {
                                double lat = Double.parseDouble(infoList.get(i).latitude);
                                double lng = Double.parseDouble(infoList.get(i).longitude);
                                int fast = Integer.parseInt(infoList.get(i).getPileFastNumFree());
                                int slow = Integer.parseInt(infoList.get(i).getPileSlowNumFree());
                                addDate(lat, lng, (Integer.valueOf(infoList.get(i).getPileFastNumFree())+ Integer.valueOf(infoList.get(i).getPileSlowNumFree())) + "," + infoList.get(i).getChargeStationType() + "." + infoList.get(i).getStationId() + "-" + (Double.valueOf(infoList.get(i).pileFastNumFree) + Double.valueOf(infoList.get(i).getPileSlowNumFree())));//桩子总数,类型，station id
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        }
                        CameraPosition.Builder builder = CameraPosition.builder();//深圳  lat lon  22.5472000000,114.0842620000
                        builder.target(new LatLng(22.5472000000, 114.0842620000));//先移动地图到广州
                        builder.zoom(8);
                        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
                    }
                });


                //1.1.0版本 未聚合
//            for (int a=0;a<25;a++){
//                try {
//                    StationLocation stationLocation = infoList.get(a);
//                    LogUtil.e(a + "数据库中 获取到的桩子信息 station id：" + infoList.get(a).getStation_id());  ///
//                    LogUtil.e(a + "数据库中 获取到的桩子信息 station lat：" + infoList.get(a).getLatitude());
//                    LogUtil.e(a + "数据库中 获取到的桩子信息 station lon：" + infoList.get(a).getLongitude());
//                    LogUtil.e(a + "数据库中 获取到的桩子信息 station type：" + infoList.get(a).getCharge_station_type());
//                    double lat = Double.parseDouble(stationLocation.getLatitude());
//                    double lon = Double.parseDouble(stationLocation.getLongitude());
//                    LogUtil.e(a + "数据库中 获取到的桩子信息 --------------------------------------------lat:" + lat + ",lon:" + lon);
//                    MarkerOptions markerOption = new MarkerOptions();
//                    markerOption.position(new LatLng(lat, lon));//这里可能需要处理异常    markerOption.draggable(true);//
//                    int pile_fast_num_free = Integer.valueOf(stationLocation.getPile_fast_num_free()); // Integer.valueOf(m.get("pile_fast_num_free").toString());
//                    int pile_slow_num_free = Integer.valueOf(stationLocation.getPile_slow_num_free()); //Integer.valueOf(m.get("pile_slow_num_free").toString());
//                    LogUtil.e(a + "数据库中 获取到的桩子信息 slow：" + pile_slow_num_free);
//                    LogUtil.e(a + "数据库中 获取到的桩子信息 fast：" + pile_fast_num_free);
//                    markerOption.icons(getBitmapDscriptors(stationLocation.getCharge_station_type(), String.valueOf(pile_fast_num_free + pile_slow_num_free)));
//                    markerOption.period(10);//去掉动画
//
//                   if (Distance.getDistance(afterGeoPoint.getLatitude(),afterGeoPoint.getLongitude(),lat,lon)<500){ //500公里以内的 25个数据
//                       Marker marker = aMap.addMarker(markerOption);
//                       marker.setObject(stationLocation.getStation_id());//传递充电桩id
//                   }
//                }catch (Throwable  t){t.printStackTrace();}
//
//            }//遍历结束
                LogUtil.e("数据库中 获取到的桩子信息遍历结束后 size：" + infoList.size());
            }
        });


    }

    @Override
    public void setView() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void onComplete(String result, int type) {

    }

    @Override
    public void onFailure(String msg, int type) {

    }


    //sort排序自定义规则为 哪个点离当前的经纬度距离近
    class SortByDistance implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            Map<String, Object> s1 = (Map<String, Object>) o1;
            Map<String, Object> s2 = (Map<String, Object>) o2;
            double s1_lat = 0.0, s1_lon = 0.0, s2_lat = 0.0, s2_lon = 0.0, s1_distance = 0.0, s2_distance = 0.0;

            try {
                s1_lat = Double.parseDouble(s1.get("latitude").toString());
                s1_lon = Double.parseDouble(s1.get("longitude").toString());
                s2_lat = Double.parseDouble(s2.get("latitude").toString());
                s2_lon = Double.parseDouble(s2.get("longitude").toString());

                s1_distance = Distance.getDistance(afterGeoPoint.getLatitude(), afterGeoPoint.getLongitude(), s1_lat, s1_lon);//
                s2_distance = Distance.getDistance(afterGeoPoint.getLatitude(), afterGeoPoint.getLongitude(), s2_lat, s2_lon);
            } catch (Throwable t) {
                t.printStackTrace();
            }


            if (s1_distance > s2_distance) {
                return 1;
            }
            return -1;
//            return s1.getAge().compareTo(s2.getAge());
//          if (s1.getAge() > s2.getAge())
//           return 1;
//          return -1;
        }
    }


    boolean isclickedLocation = false;//防止多次点击 定位

    /**
     * 统一处理click事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_amap:
                mapPopupWindow.dismiss();
                openAMapNavi();
                break;//打开高德地图
            case R.id.tv_start_navi://开始导航
                Intent intent = new Intent(this, NaviActivity.class);
                intent.putExtra("current_lat", curLat + "");
                intent.putExtra("current_lon", curLon + "");
                intent.putExtra("target_lat", markerLat + "");
                intent.putExtra("target_lon", markerLon + "");
//				intent.putExtra("current", new NaviLatLng(curLat, curLon));
//				intent.putExtra("target", new NaviLatLng(markerLat, markerLon));
                startActivity(intent);
                mapPopupWindow.dismiss();
                break;
            case R.id.tv_baidu:
                mapPopupWindow.dismiss();
                openBaiduMapNavi();
                break;//打开百度地图
            case R.id.btn_map_charging:
                pop.dismiss();
                mapPopupWindow.showAtLocation(findViewById(R.id.point_main), Gravity.BOTTOM, 0, 0);
                break;//点击充电 弹出桩子信息
            case R.id.iv_collection:
                favorAction();
                break;//收藏or取消收藏
            case R.id.img_location://定位,点击蓝色原点
                if (isclickedLocation) {
                    return;
                }
                isclickedLocation = !isclickedLocation;
                LogUtil.e("点击定位按钮");
//				CameraPosition.Builder builder = CameraPosition.builder();
//				builder.target(new LatLng(23.117055306224895, 113.2759952545166));//先移动地图到广州
//				builder.zoom(aMap.getCameraPosition().zoom+1);
//				builder.zoom(aMap.getCameraPosition().zoom-10);
//				aMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
//              aMap.setMyLocationEnabled(true);                        // 是否进行定位

                mlocationClient = new AMapLocationClient(this);
                AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
                mlocationClient.setLocationListener(this);//设置定位监听
                mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置为高精度定位模式
                mlocationClient.setLocationOption(mLocationOption);//设置定位参数
                mLocationOption.setOnceLocation(true);
                mlocationClient.startLocation();
                x.task().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        x.task().autoPost(new Runnable() {
                            @Override
                            public void run() {
                                isclickedLocation = !isclickedLocation;
                                mlocationClient.stopLocation();
                            }
                        });
                    }
                }, 1500);


//                x.task().postDelayed(new Runnable() { @Override public void run() {
//                    aMap.moveCamera(CameraUpdateFactory.zoomTo(15f));
//                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(curLat, curLon)));
//                    aMap.setMyLocationEnabled(false);
//                }  },1500);

//                if(mlocationClient!=null && finishLocation){
//                    dialog.show();
//                    finishLocation = false;
////                    mlocationClient.startLocation();
//				aMap.setMyLocationEnabled(true);                        // 是否进行定位
//				x.task().postDelayed(new Runnable() { @Override public void run() {
//					aMap.moveCamera(CameraUpdateFactory.zoomTo(15f));
//					aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(curLat, curLon)));
//					aMap.setMyLocationEnabled(false);
//				}  },1500);

//                }
                break;
            default:
                break;
        }
    }


    int pageIndex = 1;

    //请求附近桩子列表  showOrHideList 时调用
    private void getList() {

        String url = "api/pile/station/page/query";
        HashMap<String, Object> map = new HashMap<>();
        map.put("longitude", curLon + "");           //经度
        map.put("latitude", curLat + "");            //纬度
//        map.put("distance", "100");  // (不传则全部)
        map.put("pageIndex", pageIndex + "");        //页码,需要自增
        map.put("pageNum", "10");         //每条显示条数  不写默认100

        MyNetwork.getMyApi()
                .carRequest(url, MyMessageUtils.addBody(map))
                .map(new FlatFunction<>(Object.class))
                .compose(RxNetHelper.<Object>io_main())
                .subscribe(new ResultSubscriber<Object>() {
                    @Override
                    public void onSuccessCode(Message object) {

                    }

                    @Override
                    @SuppressWarnings("unchecked")
                    public void onSuccess(Object bean) {
                        if(dialog!=null){
                            dialog.dismiss();
                        }
                        lv_station_list.onRefreshComplete();
                        // 对象转json
                        String s = JsonUtils.objectToJson(bean);
                        // json转 list
                        List<String> strings = JsonUtils.jsonToList(s);
                        if (strings != null) {
                            Log.e("aaaa", "recordList: " + strings.toString());
                            try {
                                JSONArray array = new JSONArray(strings);
                                int count = array.length();
                                if (count == 0) {
                                    if (pageIndex==1) {
                                        DialogHelper.alertDialog(PointAggregationAty.this, getString(R.string.dialog_D101_no_data));
                                    }else{
                                        DialogHelper.alertDialog(PointAggregationAty.this, "没有更多了");
                                    }
                                } else {
                                    rl_list_frame.setVisibility(View.VISIBLE);
                                    lv_station_list.setVisibility(View.VISIBLE);
                                    List<Map<String, Object>> list_temp = new ArrayList<>();
                                    for (int i = 0; i < count; i++) {
                                        JSONObject object = array.getJSONObject(i);
                                        String oString = object.toString();
                                        LogUtils.d("oString: " + oString);
                                        list_temp.add(JsonUtils.jsonToMap(oString));
//                                list.add(JsonUtils.jsonToMap(oString));//添加桩子
                                    }
                                    if(pageIndex==1){
                                        adapter.refreshData(list_temp);
                                    }else{
                                        adapter.loadData(list_temp);
                                    }
                                    if (list_temp.size() > 0) {
                                        list.clear();//先清空数据
                                        list.addAll(list_temp);//再进行添加
//								Collections.sort(list, new SortByDistance());//按照距离 排序
//                                        adapter.setList(list);
                                    }//有数据就加载,不应先供后续逻辑
//                                    if (list_temp.size() < 10) {
//                                        ToastUtil.showToast("已全部加载！");
//                                        return;
//                                    }
//                            setMarkerToMap(list);
                                }
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onFail(Message<Object> bean) {
                        if(dialog!=null){
                            dialog.dismiss();
                        }
                        lv_station_list.onRefreshComplete();
                        Log.e("aaaa", "获取失败：" + bean.toString());
                    }

                });
    }

    //桩子列表
    private void showOrHideList() {
        dialog = DialogHelper.loadingDialog(this, getString(R.string.dialog_loading));//加载中
        try {
            adapter.refreshData(list);
            adapter.notifyDataSetChanged();
        } catch (Throwable t) {
            t.printStackTrace();
        }//刷新列表数据
        if (!isShowList) {//显示列表
            getList();//显示时才加载数据
            rl_list_frame.setVisibility(View.VISIBLE);
            lv_station_list.setVisibility(View.VISIBLE);
            title.setText(getResources().getString(R.string.station_list_title));
            right_btn.setImageResource(R.mipmap.yd_33);
            ValueAnimator va = ValueAnimator.ofObject(new OpenEvarlutor(), 0, layout_map.getHeight());
            va.setDuration(800);
            va.setInterpolator(new LinearInterpolator());
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    rl_list_frame.setVisibility(View.VISIBLE);
                    rl_list_frame.setTranslationY(-Float.parseFloat(animation.getAnimatedValue().toString()));
//                    layout_main.postInvalidate();
                }
            });
            va.start();
        } else {//隐藏列表
            if(dialog!=null){
                dialog.dismiss();
            }
            rl_list_frame.setVisibility(View.GONE);
            lv_station_list.setVisibility(View.GONE);
            title.setText(getResources().getString(R.string.station_map_title));
            right_btn.setImageResource(R.mipmap.ic_station_list);
            ValueAnimator va = ValueAnimator.ofObject(new CloseEvarlutor(), layout_map.getHeight(), 0);
            va.setInterpolator(new AccelerateInterpolator(1.5f));
            va.setDuration(650);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
//                    lp.bottomMargin = Integer.parseInt(animation.getAnimatedValue().toString());
//                    LogUtils.e(lp.bottomMargin+"bottom-margin");
                    rl_list_frame.setTranslationY(-Float.parseFloat(animation.getAnimatedValue().toString()));
//                    layout_main.postInvalidate();
                }
            });
            va.start();
        }
        LogUtils.e(layout_map.getHeight() + "height");
        isShowList = !isShowList;
    }

    /**
     * 包路径是否存在
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    /**
     * 打开高德地图
     */
    public void openAMapNavi() {
        // 构造导航参数
        NaviPara naviPara = new NaviPara();
        // 设置终点位置
        LatLng latLng = new LatLng(markerLat, markerLon);
        naviPara.setTargetPoint(latLng);
        // 设置导航策略，这里是避免拥堵
        naviPara.setNaviStyle(AMapUtils.DRIVING_AVOID_CONGESTION);

        // 调起高德地图导航
        try {
            AMapUtils.openAMapNavi(naviPara, getApplicationContext());
        } catch (com.amap.api.maps.AMapException e) {
            // 如果没安装会进入异常，调起下载页面
            AMapUtils.getLatestAMapApp(getApplicationContext());

        }
    }

    /**
     * 打开百度地图
     */
    public void openBaiduMapNavi() {
        try {
            LogUtils.i("markerLat:" + markerLat + "||markerLon:" + markerLon);
            Intent intent = Intent.parseUri("intent://map/direction?destination=" + MapUtils.bd_encrypt(markerLat, markerLon) + "&mode=driving&src=iDianNiu|iDianNiu#Intent;scheme=b" +
                    "dapp;package=com.baidu.BaiduMap;end", 0);
            if (isInstallByread("com.baidu.BaiduMap")) {
                startActivity(intent); //启动调用
                LogUtils.e("百度地图客户端已经安装");
            } else {
                ToastUtil.showToast(R.string.toast_app_uninstall_baidu);
                LogUtils.e("没有安装百度地图客户端");
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 收藏 取消站点
     */
    private void favorAction() {
        String url = "api/favor/station/operation";
        HashMap<String, Object> map = new HashMap<>();
        if (!favorFlag) {
            //收藏站点
            map.put("userId", CacheUtils.getIn().getUserInfo().getId());
            map.put("favorType", "1");
            map.put("stationId", station_id);
        } else {
            //取消收藏站点
            map.put("userId", CacheUtils.getIn().getUserInfo().getId());
            map.put("favorType", "2");
            map.put("stationId", station_id);
        }

        MyNetwork.getMyApi()
                .carRequest(url, MyMessageUtils.addBody(map))
                .map(new FlatFunction<>(Object.class))
                .compose(RxNetHelper.<Object>io_main())
                .subscribe(new ResultSubscriber<Object>() {
                    @Override
                    public void onSuccessCode(Message object) {

                    }

                    @Override
                    @SuppressWarnings("unchecked")
                    public void onSuccess(Object bean) {
                        favorFlag = !favorFlag;
                        if (favorFlag) {
                            ToastUtil.showToast(R.string.toast_D104);
                            Picasso.with(PointAggregationAty.this).load(R.mipmap.ic_collection_yellow).noFade().into(iv_collection);
                        } else {
                            ToastUtil.showToast(R.string.toast_D106);
                            Picasso.with(PointAggregationAty.this).load(R.mipmap.ic_collection_gray).noFade().into(iv_collection);
                        }
                    }

                    @Override
                    public void onFail(Message<Object> bean) {
                        Log.e("aaaa", "获取失败：" + bean.toString());
                    }

                });
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /**
     * activity返回的结果result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantCode.REQ_FILTER && resultCode == ConstantCode.RES_FILTER) {
            LogUtils.d(data.getSerializableExtra("filter").toString());
            HashMap<String, String> map = (HashMap<String, String>) data.getSerializableExtra("filter");
            charge_interface = map.get("charge_interface");
            charge_carr = map.get("charge_carr");
            charge_method = map.get("charge_method");
            charge_pile_bel = map.get("charge_pile_bel");
//            pile_state = map.get("pile_state");
//            charging_gun = map.get("charging_gun");
            parking_free = map.get("parking_free");
            service_time = map.get("service_time");

            for (Map.Entry<String, String> entry1 : filterMap.entrySet()) {
                String m1value = entry1.getValue() == null ? "" : entry1.getValue();
                String m2value = map.get(entry1.getKey()) == null ? "" : map.get(entry1.getKey());
                if (!m1value.equals(m2value)) { //若两个map中相同key对应的value不相等 ,筛选条件变更重新请求
                    filterMap.clear();
                    filterMap = map;
//                    connToServer();
                    connToServer2();//调试新桩子地图接口
                    break;
                }
            }
        } else if (requestCode == ConstantCode.REQ_OPEN_STATION_INFO && resultCode == ConstantCode.RES_OPEN_STATION_INFO && data != null) {
//            if(curMarker != null){  //是否收藏
//                MarkerModel m = (MarkerModel) curMarker.getObject();
//                boolean is_favor = data.getBooleanExtra("is_favor", false);
//                m.setIs_favor(is_favor ? "0" : "1");
//                curMarker.setObject(m);
//            }
        } else if (requestCode == ConstantCode.SELECT_CITY) {

//			aMap.setLocationSource(mListener);// 设置定位监听
            LogUtils.d("获取到的城市---:" + data);
            try {
                LogUtils.d("获取到的城市未:" + data.getStringExtra("city"));
            } catch (Throwable t) {
                t.printStackTrace();
            }//防止异常
            String str = "";
            try {
                str = data.getStringExtra("city");
                Log.e("aaa", str + ".....................");
            } catch (Throwable t) {
                t.printStackTrace();
                str = "";
            }
            if (!str.equals("")) {
                current_city_str = str;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("aaaa", "current_city:" + current_city);
                        Log.e("aaaa", "current_city_str:" + current_city_str);
                        current_city.setText(current_city_str);//更新当前城市名
                    }
                });
                GeocodeSearch geocodeSearch = new GeocodeSearch(this);//构造 GeocodeSearch 对象，并设置监听。
                geocodeSearch.setOnGeocodeSearchListener(this);
                //通过GeocodeQuery设置查询参数,调用getFromLocationNameAsyn(GeocodeQuery geocodeQuery) 方法发起请求。
                //address表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode都ok
                GeocodeQuery query = new GeocodeQuery(current_city_str, str);//str 为城市  南京 北京
                geocoderSearch.getFromLocationNameAsyn(query);
            }

        }
    }


    //激活高德地图的回调
    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;

//        if (mlocationClient == null) {
//            mlocationClient = new AMapLocationClient(this);
//            mLocationOption = new AMapLocationClientOption();
//            mlocationClient.setLocationListener(this);//设置定位监听
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置为高精度定位模式
//            mlocationClient.setLocationOption(mLocationOption);//设置定位参数
//            mLocationOption.setOnceLocation(true);
//            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
//            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
//            // 在定位结束后，在合适的生命周期调用onDestroy()方法
//            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//            AMapLocationClient mlocationClient;  //声明mlocationClient对象
//            AMapLocationClientOption mLocationOption = null;//声明mLocationOption对象
//            mlocationClient = new AMapLocationClient(this);//初始化定位参数
//            mLocationOption = new AMapLocationClientOption();
//            mlocationClient.setLocationListener(this);//设置定位监听
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
//            mLocationOption.setInterval(2000);//设置定位间隔,单位毫秒,默认为2000ms
//            mlocationClient.setLocationOption(mLocationOption);//设置定位参数
// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
// 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
// 在定位结束后，在合适的生命周期调用onDestroy()方法
// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//启动定位
//        }
//        mlocationClient.startLocation();
    }

    @Override
    public void deactivate() {
        try {
            mListener = null;
            if (mlocationClient != null) {
                mlocationClient.stopLocation();
                mlocationClient.onDestroy();
            }
            mlocationClient = null;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    Marker locationMarker = null;

    @Override
    public void onLocationChanged(final AMapLocation aMapLocation2) {
        LogUtil.e("位置改变:" + aMapLocation2);
        LogUtil.e("位置改变22:" + aMapLocation2.getErrorCode());
        LogUtil.e("city:" + aMapLocation2.getCity());
        try {
            dialog.cancel();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        LogUtil.e("位置改变22:" + aMapLocation2.getErrorCode());
        if (aMapLocation2.getErrorCode() == 0) {
            try {
                mListener.onLocationChanged(aMapLocation2);
            } catch (Throwable t) {
                t.printStackTrace();
            }// 显示系统小蓝点
            try {
                curLat = aMapLocation2.getLatitude();
                curLon = aMapLocation2.getLongitude();
                if (afterGeoPoint == null) {
                    afterGeoPoint = new LatLonPoint(curLat, curLon);
                } else {
                    afterGeoPoint.setLatitude(curLat);
                    afterGeoPoint.setLongitude(curLon);
                }//每次new
            } catch (Throwable t) {
                t.printStackTrace();
            }
            //这里不变 否则一直显示深圳
            try {
                current_city_str = aMapLocation2.getCity().replace("市", "");
            } catch (Throwable t) {
                t.printStackTrace();
            }
//				runOnUiThread(new Runnable() {
//					@Override public void run() {
            try {
                if (TextUtils.isEmpty(current_city_str)) {
                } else {
//                    current_city.setText(current_city_str);
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }//ToastUtil.showToast(city);

            try {
                locationMarker = null;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            resetMarks();
            //可在其中解析amapLocation获取相应内容。
            try {
                aMap.getMapScreenMarkers().remove(locationMarker);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            x.task().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {//尝试添加自己位置的图标
                        LatLng latLng = new LatLng(curLat, curLon);//取出经纬度
                        //添加Marker显示定位位置
                        if (locationMarker == null) {
                            //如果是空的添加一个新的,icon方法就是设置定位图标，可以自定义
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.visible(true);
                            markerOptions.title("当前位置");
                            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_nearby_charge));
                            markerOptions.icon(bitmapDescriptor);
                            aMap.addMarker(markerOptions);
                            locationMarker = aMap.addMarker(markerOptions);
                        } else { //已经添加过了，修改位置即可
                            locationMarker.setPosition(latLng);
                        }
                        CameraPosition.Builder builder = CameraPosition.builder();
                        builder.target(latLng);//移动地图到定位地点
                        builder.zoom(16.0f);
                        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
                        isfistFinish = false;


                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }, 2000);

        } else {
            String errText = "定位失败," + aMapLocation2.getErrorCode() + ": " + aMapLocation2.getErrorInfo();
            LogUtils.e(errText);
            if (NetworkUtils.isConnected(this)) {
                if (aMapLocation2.getErrorCode() == 12) {
                    ToastUtil.showToast(R.string.toast_permission_location);
                } else {
                    ToastUtil.showToast(getString(R.string.toast_map_location_failed) + aMapLocation2.getErrorInfo());
                }
            }
        }
//		}
    }


    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int rCode) {
        if (rCode == 1000) {
            try {
                regeocodeResult.getRegeocodeAddress().getRoads().get(0).getLatLngPoint();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                tv_map_address.setText(regeocodeResult.getRegeocodeAddress().getFormatAddress());
                float dis_m = AMapUtils.calculateLineDistance(new LatLng(curLat, curLon), new LatLng(markerLat, markerLon)) / 1000;
                DecimalFormat format = new DecimalFormat("##0.0");
                tv_map_distance.setText(format.format(dis_m) + "km");
            } else {
                ToastUtil.showToast(R.string.toast_map_regeocode_no_data);
            }
        } else {
            ToastUtil.showToast(R.string.toast_map_regeocode_no_data);
            LogUtils.d("获取地址失败，错误码：" + String.valueOf(rCode));
        }
        resetMarks();//更新marker点
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        if (geocodeResult != null && geocodeResult.getGeocodeAddressList().size() > 0) {
            LatLonPoint myPoint = geocodeResult.getGeocodeAddressList().get(0).getLatLonPoint();
//			CameraPosition.Builder builder = CameraPosition.builder();
////      builder.target(new LatLng(113.2759952545166 , 23.117055306224895));//先移动地图到广州
////			builder.target(new LatLng(myPoint.getLatitude(),myPoint.getLongitude()));// 移动地图
//			builder.zoom(aMap.getCameraPosition().zoom-1);//
//			aMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));///
//            aMap.moveCamera(CameraUpdateFactory.zoomTo(15f));
//            aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(myPoint.getLatitude(),myPoint.getLongitude())));

            CameraPosition.Builder builder = CameraPosition.builder();
            builder.target(new LatLng(myPoint.getLatitude(), myPoint.getLongitude()));//先移动地图到广州
            builder.zoom(16.0f);
            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(builder.build()));

            afterGeoPoint = myPoint;//更新切换城市后 经纬度坐标问题
            isGeo = true;

//            curLat=myPoint.getLatitude();//尝试更新 当前位置
//            curLon=myPoint.getLongitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        current_city.setText(current_city_str);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            });
//                resetMarks();

//            connToServer();//不再重新请求点信息
//			connToServer2();//调试新桩子地图接口
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * 重写返回键,解决列表返回异常
     */
    @Override
    public void onBackPressed() { //正在显示列表的话// isShowList = !isShowList;
        if (isShowList) {
            showOrHideList();
            return;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMapLoaded() {

    }


}