package www.qisu666.com.map.geoFence;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.DPoint;
import com.amap.api.maps.model.LatLng;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by wujiancheng on 2017/11/7.
 */
public class GeoFenceService extends Service implements GeoFenceListener {
    // 地理围栏客户端
    private GeoFenceClient fenceClient = null;
    // 触发地理围栏的行为，默认为进入提醒
    private int activatesAction = GeoFenceClient.GEOFENCE_IN;
    // 地理围栏的广播action
    static final String GEOFENCE_BROADCAST_ACTION = "com.example.geofence.polygon";
    // 记录已经添加成功的围栏
    private HashMap<String, GeoFence> fenceMap = new HashMap<String, GeoFence>();
    protected Subscription busSubscription;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化地理围栏
        fenceClient = new GeoFenceClient(getApplicationContext());

        /**
         * 创建pendingIntent
         */
        fenceClient.createPendingIntent(GEOFENCE_BROADCAST_ACTION);
        fenceClient.setGeoFenceListener(this);
        /**
         * 设置地理围栏的触发行为,默认为进入
         */
        fenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN | GeoFenceClient.GEOFENCE_OUT);

        IntentFilter filter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(GEOFENCE_BROADCAST_ACTION);
        registerReceiver(mGeoFenceReceiver, filter);

        observeRxEventCode();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            int fenceType = intent.getIntExtra("fenceType", GeoFenceManager.FENCE_TYPE_POLYGON);
            if (fenceType == GeoFenceManager.FENCE_TYPE_POLYGON) {
                List<LatLng> latLngs = intent.getParcelableArrayListExtra("latLngs");
                addPolygonFence(latLngs);
            } else if (fenceType == GeoFenceManager.FENCE_TYPE_CIRCLE) {
                LatLng centerLatLng = intent.getParcelableExtra("centerLatLng");
                float radius = intent.getFloatExtra("radius", 0.0f);
                addCircleFence(centerLatLng, radius);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void observeRxEventCode() {
        busSubscription = RxBus.getInstance()
                .toObservable(RxBusEvent.class)
                .subscribe(new Action1<RxBusEvent>() {
                    @Override
                    public void call(RxBusEvent rxBusEvent) {
                        int eventCode = rxBusEvent.getEventCode();
                        switch (eventCode) {
                            case RxEventCodes.CODE_REMOVE_GEO_FENCE://移除地理围栏
                                removeGeoFence();
                                break;
                        }
                    }
                });
    }

    List<GeoFence> fenceList = new ArrayList<GeoFence>();

    @Override
    public void onGeoFenceCreateFinished(final List<GeoFence> geoFenceList,
                                         int errorCode, String customId) {
        Logger.i("errorCode==" + errorCode);
        if (errorCode == GeoFence.ADDGEOFENCE_SUCCESS) {
            Message msg = Message.obtain();
            fenceList = geoFenceList;
            msg.obj = customId;
            msg.what = 0;
            handler.sendMessage(msg);
        }
    }

    /**
     * 接收触发围栏后的广播,当添加围栏成功之后，会立即对所有围栏状态进行一次侦测，如果当前状态与用户设置的触发行为相符将会立即触发一次围栏广播；
     * 只有当触发围栏之后才会收到广播,对于同一触发行为只会发送一次广播不会重复发送，除非位置和围栏的关系再次发生了改变。
     */
    private BroadcastReceiver mGeoFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 接收广播
            if (intent.getAction().equals(GEOFENCE_BROADCAST_ACTION)) {
                Bundle bundle = intent.getExtras();
//                String customId = bundle
//                        .getString(GeoFence.BUNDLE_KEY_CUSTOMID);
//                String fenceId = bundle.getString(GeoFence.BUNDLE_KEY_FENCEID);
                //status标识的是当前的围栏状态，不是围栏行为
                int status = bundle.getInt(GeoFence.BUNDLE_KEY_FENCESTATUS);
//                StringBuffer sb = new StringBuffer();
                RxBusEvent<Integer> event = new RxBusEvent<>();
                event.setEventCode(RxEventCodes.CODE_IS_TIP_CAN_RETURN);
                switch (status) {
                    case GeoFence.STATUS_LOCFAIL://定位失败
                        event.setContent(GeoFence.STATUS_OUT);
                        break;
                    case GeoFence.STATUS_IN://进入围栏
                        Logger.i("进入围栏");
                        event.setContent(GeoFence.STATUS_IN);
                        break;
                    case GeoFence.STATUS_OUT://离开围栏
                        Logger.i("离开围栏");
                        event.setContent(GeoFence.STATUS_OUT);
                        break;
                    case GeoFence.STATUS_STAYED://停留在围栏内
                        Logger.i("停留在围栏内");
//                        event.setContent(GeoFence.STATUS_STAYED);
                        event.setContent(GeoFence.STATUS_IN);
                        break;
                    default:
                        event.setContent(GeoFence.STATUS_OUT);//默认
                        break;
                }
                RxBus.getInstance().post(event);
            }
        }
    };

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0://创建围栏完成
                    drawFence2Map();
                    break;
                default:
                    break;
            }
        }
    };

    Object lock = new Object();

    void drawFence2Map() {
        new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (lock) {
                        if (null == fenceList || fenceList.isEmpty()) {
                            return;
                        }
                        for (GeoFence fence : fenceList) {
                            if (fenceMap.containsKey(fence.getFenceId())) {
                                continue;
                            }
                            drawFence(fence);
                            fenceMap.put(fence.getFenceId(), fence);
                        }
                    }
                } catch (Throwable e) {

                }
            }
        }.start();
    }

    private void drawFence(GeoFence fence) {
        switch (fence.getType()) {
            case GeoFence.TYPE_ROUND:
            case GeoFence.TYPE_AMAPPOI:
                GeoFenceManager.drawCircle(fence);
                break;
            case GeoFence.TYPE_POLYGON:
            case GeoFence.TYPE_DISTRICT:
                GeoFenceManager.drawPolygon(fence);
                break;
            default:
                break;
        }

        // 设置所有maker显示在当前可视区域地图中
//        LatLngBounds bounds = boundsBuilder.build();
//        mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
//        polygonPoints.clear();
//        removeMarkers();
    }

    /**
     * 添加多边形围栏
     *
     * @author hongming.wang
     * @since 3.2.0
     */
    private void addPolygonFence(List<LatLng> latLngs) {
        if (null == latLngs || latLngs.size() < 3) {
            stopSelf();
            return;
        }
        List<DPoint> pointList = new ArrayList<DPoint>();
        for (LatLng latLng : latLngs) {
            pointList.add(new DPoint(latLng.latitude, latLng.longitude));
        }
        fenceClient.addGeoFence(pointList, "");
    }

    /**
     * 添加圆形围栏
     *
     * @author hongming.wang
     * @since 3.2.0
     */
    private void addCircleFence(LatLng centerLatLng, float radius) {
        DPoint centerPoint = new DPoint(centerLatLng.latitude, centerLatLng.longitude);
        fenceClient.addGeoFence(centerPoint, radius, "");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mGeoFenceReceiver);
        removeGeoFence();
        if (busSubscription != null && !busSubscription.isUnsubscribed()) {
            busSubscription.unsubscribe();
        }
    }

    /**
     * 移除掉地理围栏
     */
    private void removeGeoFence() {
        if (null != fenceClient) {
            fenceClient.removeGeoFence();
        }
        GeoFenceManager.removeGeoFenceOverlay();
    }
}
