package www.qisu666.com.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import www.qisu666.com.app.MyApplication;
import www.qisu666.com.constant.Config;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.UserUtils;

public class MyLocationService extends Service implements
        AMapLocationListener {
    private static final String TAG = MyLocationService.class.getSimpleName();

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private Handler mainHandler;
    private MyApplication application;
    private long curInterval;
    public String gps = "0,0";

    public static final long INTERVAL_1_SECOND = 1000L;
    public static final long INTERVAL_10_SECONDS = 10 * 1000L;

    private String curCityCode;
    private String cityCodePosition;
    private String addressNowCity = Config.DEFAULT_CITY_NAME;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = (MyApplication) getApplication();
        mainHandler = new Handler(getMainLooper());
        curInterval = INTERVAL_1_SECOND;
        CacheUtils.getIn().saveStr(CacheUtils.locationOk, "");
        setClientInfo();
    }

    public void setClientInfo() {
        if (mLocationOption == null) {
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            mLocationOption.setHttpTimeOut(30 * 1000);
            mLocationOption.setGpsFirst(true);
            //设置是否只定位一次,默认为false
            mLocationOption.setOnceLocation(false);
            //设置是否强制刷新WIFI，默认为强制刷新
            mLocationOption.setWifiActiveScan(true);
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(false);
        }
        getLocationManager();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        RxBus rxBus = RxBus.getInstance();
        RxBusEvent<Integer> event = new RxBusEvent<>();
        //通知定位结果
        event.setEventCode(RxEventCodes.CODE_LOCATE_STATUS);
        if (null == amapLocation) {
            //定位错误
            event.setContent(-1);
            rxBus.post(event);
            return;
        }
        if (amapLocation.getErrorCode() == 0) {
            Logger.i(TAG, "定位成功了");

            double dis = AMapUtils.calculateLineDistance(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()), UserUtils.getLatLng());
            //如果本次定位和上次地位的距离小于2米，则不传给页面
            if (dis < 2) {
                Logger.e(TAG, "位置变化太小");
                return;
            }

            // 定位成功回调信息，设置相关消息
            application.latitude = amapLocation.getLatitude() + "";
            application.longitude = amapLocation.getLongitude() + "";
            gps = application.longitude + "," + application.latitude;

            String address = amapLocation.getAddress();
            if (address != null && address.length() > 0) {
                CacheUtils.getIn().saveStr(CacheUtils.curLocationSite, address);
            }
            if (!amapLocation.getCityCode().equals(curCityCode)) {
                addressNowCity = amapLocation.getCity();
                curCityCode = amapLocation.getCityCode();
                String adcode = amapLocation.getAdCode();
                cityCodePosition = adcode.substring(0, adcode.length() - 2) + "00";
            }

            //定位成功后将1秒定位1次更改为10秒定位1次
            resetLocationInterval(INTERVAL_10_SECONDS);

            Logger.i(TAG, "latitude=" + application.latitude + ",longitude="
                    + application.longitude + ",gps=" + gps + ",addressNowCity=" +
                    addressNowCity + ",cityCodeNow=" + cityCodePosition);
            application.addressNowCity = addressNowCity;
            application.cityCodeNow = cityCodePosition;
            CacheUtils.getIn().saveStr(CacheUtils.cityCode, cityCodePosition);
            CacheUtils.getIn().saveStr(CacheUtils.curCity, addressNowCity);
            CacheUtils.getIn().saveStr(CacheUtils.locationOk, CacheUtils.locationOk);
        } else {
            Log.e(TAG, "定位错误：" + amapLocation.getErrorCode());
            CacheUtils.getIn().saveStr(CacheUtils.cityCode, Config.DEFAULT_CITY_CODE);
            CacheUtils.getIn().saveStr(CacheUtils.curCity, Config.DEFAULT_CITY_NAME);
        }
        //12，要去打开权限
        event.setContent(amapLocation.getErrorCode());
        rxBus.post(event);
    }

    private void getLocationManager() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Logger.i("设置定位频率curInterval=" + curInterval);
                if (mLocationClient == null) {
                    //初始化定位
                    mLocationClient = new AMapLocationClient(getApplicationContext());
                    mLocationClient.setLocationListener(MyLocationService.this);
                } else {
                    mLocationClient.stopLocation();
                }
                //设置定位间隔,单位毫秒,默认为2000ms
                mLocationOption.setInterval(curInterval);
                //给定位客户端对象设置定位参数
                mLocationClient.setLocationOption(mLocationOption);
                //启动定位
                mLocationClient.startLocation();
            }
        });
    }

    /**
     * 重新设置定位频率
     *
     * @param interval 毫秒
     */
    private void resetLocationInterval(long interval) {
        if (interval != curInterval && !application.latitude.equals("0.0")) {
            Logger.i(TAG, "重置定位频率:" + curInterval + "->" + interval);
            curInterval = interval;
            getLocationManager();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
    }
}
