package www.qisu666.com.map.cluster;

import android.content.Context;
import android.text.TextUtils;
import android.util.LruCache;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import www.qisu666.com.callback.ParkListByCityAreaResp;
import www.qisu666.com.callback.ParksResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.rx.RxSchedulersHelper;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.StringUtils;
import www.qisu666.com.utils.UserUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action1;


/**
 * Created by yiyi.qi on 16/10/10.
 * 整体设计采用了两个线程,一个线程用于计算组织聚合数据,一个线程负责处理Marker相关操作
 */
public class ClusterOverlay implements AMap.OnCameraChangeListener, AMap.OnMapClickListener,
        AMap.OnMarkerClickListener {
    private static final String TAG = ClusterOverlay.class.getSimpleName();
    private AMap mAMap;
    private Context mContext;
    private List<Cluster> mClusters = new ArrayList<>();//当前屏幕要显示的聚合点列表
    private int mClusterSize;
    private ClusterClickListener mClusterClickListener;
    private ClusterRender mClusterRender;
    private List<Marker> mAddMarkers = new ArrayList<Marker>();

    private LruCache<Integer, BitmapDescriptor> mLruCache;
    private float mPXInMeters;
    private boolean mIsCanceled = false;
    private OnCameraUpdateFinishListener mOnCameraUpdateFinishListener;
    private OnHideInfoWindowListener mOnHideInfoWindowListener;
    private OnMapClickListener mOnMapClickListener;

    private Marker curShowWindowMarker;//保存当前点击的Marker，以便点击地图其他地方设置InfoWindow消失
    private List<ParkListByCityAreaResp> parkListByCityResps = new ArrayList<>();//按城市的网点

    private ParksResp showParksResp;

    private Subscription subscription;
    private Subscription preSubscription;
    public static final int NO_CLUSTER = -1, PARK_CLUSTER = 0, AREA_CLUSTER = 1, CITY_CLUSTER = 2;
    private int clusterLevel = NO_CLUSTER;//-1：还没聚合，0：网点级聚合，1：区级聚合，2：城市级聚合
    private OnClusterLevelListener onClusterLevelListener;

    /**
     * 构造函数,批量添加聚合元素时,调用此构造函数
     *
     * @param amap
     * @param clusterItems 聚合元素
     * @param clusterSize
     * @param context
     */
    public ClusterOverlay(AMap amap, List<ParkListByCityAreaResp> clusterItems,
                          int clusterSize, Context context, ParksResp showPark) {
        //默认最多会缓存80张图片作为聚合显示元素图片,根据自己显示需求和app使用内存情况,可以修改数量
        mLruCache = new LruCache<Integer, BitmapDescriptor>(80) {
            protected void entryRemoved(boolean evicted, Integer key, BitmapDescriptor oldValue, BitmapDescriptor newValue) {
                oldValue.getBitmap().recycle();
            }
        };

        if (clusterItems != null) {
            parkListByCityResps.clear();
            parkListByCityResps.addAll(clusterItems);
        }
        mContext = context;
        mClusters = new ArrayList<Cluster>();
        this.mAMap = amap;
        mClusterSize = clusterSize;
        this.showParksResp = showPark;
        if (showParksResp != null) {
            showParksResp.setShowInfoWindow(false);
        }
        mPXInMeters = mAMap.getScalePerPixel();
        amap.setOnCameraChangeListener(this);
        amap.setOnMarkerClickListener(this);
        amap.setOnMapClickListener(this);
//        Logger.i(TAG, "刚进入时判断一下聚合");
        if (parkListByCityResps.size() > 0) {
            assignClusters();
        }
    }

    /**
     * 设置聚合点的点击事件
     *
     * @param clusterClickListener
     */
    public void setOnClusterClickListener(
            ClusterClickListener clusterClickListener) {
        mClusterClickListener = clusterClickListener;
    }

    /**
     * 设置聚合元素的渲染样式，不设置则默认为气泡加数字形式进行渲染
     *
     * @param render
     */
    public void setClusterRenderer(ClusterRender render) {
        mClusterRender = render;
    }

    public void onDestroy() {
        mIsCanceled = true;
        for (Marker marker : mAddMarkers) {
            marker.remove();

        }
        mAddMarkers.clear();
        mLruCache.evictAll();
    }

    @Override
    public void onCameraChange(CameraPosition arg0) {


    }

    @Override
    public void onCameraChangeFinish(CameraPosition arg0) {
        mPXInMeters = mAMap.getScalePerPixel();
        Logger.i(TAG, "assignClusters();222");
        assignClusters();
    }

    //点击事件
    @Override
    public boolean onMarkerClick(Marker arg0) {
        if (mClusterClickListener == null) {
            return true;
        }
        if ("my".equals(arg0.getTitle())) {
            return true;
        }
        Cluster cluster = (Cluster) arg0.getObject();
        if (cluster != null) {
            List<ParksResp> parksResps = cluster.getClusterItems();
            if (parksResps.size() == 1) {
                if (mOnHideInfoWindowListener != null) {
                    mOnHideInfoWindowListener.onHideInfoWindow();
                }

                //将上一次点击的marker恢复原来的样式
                resetMarkerIcon();

                ParksResp parksResp = parksResps.get(0);
                if (null != parksResp) {
                    //当前点击的标记为选中
                    parksResp.setSelected(true);
//                    if (parksResp.isNearest()) {
                        arg0.showInfoWindow();//提示 离我最近 的infoWindow
//                    } else {
//                        hideInfoWindow();
//                    }
                }

                //保存当前点击的Marker，以便点击地图其他地方设置InfoWindow消失
                curShowWindowMarker = arg0;
                //更改当前点击的marker样式为黄色
                arg0.setIcon(getBitmapDes(cluster));
            } else {
                if (parksResps.size() > 0) {
                    sortParkDataNew(parksResps);
                    showParksResp = parksResps.get(0);
                }
            }

            mClusterClickListener.onClick(arg0);
            return true;
        }
        return false;
    }

    /**
     * 还原Marker的图标
     */
    private void resetMarkerIcon() {
        if (null == curShowWindowMarker) {
            return;
        }
        Object oldObject = curShowWindowMarker.getObject();
        if (oldObject instanceof Cluster) {
            Cluster oldCluster = (Cluster) oldObject;
            List<ParksResp> oldParksResps = oldCluster.getClusterItems();
            if (oldParksResps.size() == 1) {
                ParksResp oldParksResp = oldParksResps.get(0);
                if (null != oldParksResp) {
                    //标记为未选中
                    oldParksResp.setSelected(false);
                }
            }
            curShowWindowMarker.setIcon(getBitmapDes(oldCluster));
        }
    }

    private synchronized List<Cluster> calculateClusters() {
        mIsCanceled = false;
        float zoom = mAMap.getCameraPosition().zoom;
        mClusters.clear();
        int tmpClusterLevel = clusterLevel;
        for (ParkListByCityAreaResp resp : parkListByCityResps) {
            List<ParkListByCityAreaResp.AreaList> mClusterItems = resp.getAreaList();

            Logger.i(TAG, "zoom==" + zoom);
            if ((clusterLevel != AREA_CLUSTER || clusterLevel != PARK_CLUSTER) && zoom > Config.MAP_ZOOM_9) {//非城市级
                Logger.i(TAG, "进来聚合");
                for (ParkListByCityAreaResp.AreaList clusterItem : mClusterItems) {
                    if (clusterLevel != AREA_CLUSTER && zoom < Config.MAP_ZOOM_13 && null != clusterItem) {//区级
                        if (onClusterLevelListener != null) {
                            onClusterLevelListener.onClusterLevel(AREA_CLUSTER);
                        }
                        String lat = clusterItem.getLatitude();
                        String lng = clusterItem.getLongitude();
                        if (StringUtils.isIntOrFloat(lat) && StringUtils.isIntOrFloat(lng)) {
                            LatLng latLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));//区的经纬度
                            Cluster cluster = new Cluster(latLng, resp.getCode(), clusterItem.getName());//区
                            List<ParksResp> parksResps = clusterItem.getParkList();
                            if (null != parksResps && parksResps.size() > 0) {//区下面的网点数的个数大于0
                                int freeCarNum = 0;
                                for (ParksResp item : clusterItem.getParkList()) {
                                    if (null != item) {
                                        String freeCarNumS = item.getParkFreeCarNum();
                                        if (!TextUtils.isEmpty(freeCarNumS)) {
                                            int num = Integer.parseInt(freeCarNumS);
                                            freeCarNum += num;
                                            cluster.addClusterItem(item);
                                        }
                                        //缩放到区级后，去掉选中的网点
                                        if (item.isSelected()) {
                                            item.setSelected(false);
                                            curShowWindowMarker = null;
                                        }
                                    }
                                }
                                //区下面的空闲车辆数
                                cluster.setFreeCarNum(freeCarNum);
                                mClusters.add(cluster);
                                tmpClusterLevel = AREA_CLUSTER;
                                cluster.setClusterLevel(tmpClusterLevel);

                                //清除规划路线
                                if (mOnMapClickListener != null) {
                                    mOnMapClickListener.onMapClick();
                                }
                            }
                        }
                    } else if (clusterLevel != PARK_CLUSTER && zoom >= Config.MAP_ZOOM_13 && null != clusterItem) {//网点级
                        if (onClusterLevelListener != null) {
                            onClusterLevelListener.onClusterLevel(PARK_CLUSTER);
                        }
                        for (ParksResp item : clusterItem.getParkList()) {
                            if (mIsCanceled) {
                                return new ArrayList<>();
                            }
                            LatLng latlng = item.getLatlng();
                            Cluster cluster = new Cluster(latlng, item.getCityCode(), "");
                            String freeCarNum = item.getParkFreeCarNum();
                            int num = 0;
                            if (!TextUtils.isEmpty(freeCarNum)) {
                                num = Integer.parseInt(freeCarNum);
                            }
                            cluster.setFreeCarNum(num);
                            cluster.addClusterItem(item);

                            mClusters.add(cluster);
                            tmpClusterLevel = PARK_CLUSTER;
                            cluster.setClusterLevel(tmpClusterLevel);
//                            }
                        }
                    }
                }

            } else if (clusterLevel != CITY_CLUSTER) {
                if (onClusterLevelListener != null) {
                    onClusterLevelListener.onClusterLevel(CITY_CLUSTER);
                }
                String lat = resp.getLatitude();
                String lng = resp.getLongitude();
                if (StringUtils.isIntOrFloat(lat) && StringUtils.isIntOrFloat(lng)) {
                    LatLng latLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
                    Cluster cluster = new Cluster(latLng, resp.getCode(), resp.getName());//城市级
                    if (null != mClusterItems && mClusterItems.size() > 0) {//区的个数大于0
                        mClusters.add(cluster);
                        tmpClusterLevel = CITY_CLUSTER;
                        cluster.setClusterLevel(tmpClusterLevel);
                    }
                }
            }
        }

        if (clusterLevel == tmpClusterLevel) {//和上一次的一样，不改，地图上的marker不变
            return new ArrayList<>();
        }
        clusterLevel = tmpClusterLevel;

        Logger.i(TAG, "要添加的Cluster个数=" + mClusters.size());

        //复制一份数据，规避同步
        List<Cluster> clusters = new ArrayList<Cluster>();
        clusters.addAll(mClusters);

        if (mIsCanceled) {
            return new ArrayList<>();
        }
        return clusters;
    }

    /**
     * 将聚合元素添加至地图上
     */
    private void addClusterToMap(List<Cluster> clusters) {
        ArrayList<Marker> removeMarkers = new ArrayList<>();
        for (Marker marker : mAddMarkers) {
            marker.remove();
            removeMarkers.add(marker);

        }
        mAddMarkers.removeAll(removeMarkers);
        Logger.i(TAG, "还保留marker个数：" + mAddMarkers.size());
        Logger.i(TAG, "还保留clusters个数：" + clusters.size());
        addSingleClusterToMap(clusters);
    }

    /**
     * 将单个聚合元素添加至地图显示
     *
     * @param clusters
     */
    private void addSingleClusterToMap(List<Cluster> clusters) {
        for (Cluster cluster : clusters) {
            LatLng latlng = cluster.getCenterLatLng();
            List<ParksResp> parksResps = cluster.getClusterItems();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.anchor(0.5f, 0.5f)
                    .icon(getBitmapDes(cluster)).position(latlng);
            Marker marker = mAMap.addMarker(markerOptions);
            if (cluster.getClusterLevel() == PARK_CLUSTER) {
                marker.setTitle(parksResps.get(0).getParkName());
                marker.setZIndex(1);
            }
            marker.setObject(cluster);

            cluster.setMarker(marker);
            mAddMarkers.add(marker);
        }
        Logger.i(TAG, "地图上marker个数：" + mAddMarkers.size());
        showInfoWindow();
    }


    /**
     * 对点进行聚合
     */
    private void assignClusters() {
        mIsCanceled = true;
        Observable<List<Cluster>> observable = Observable.create(new Observable.OnSubscribe<List<Cluster>>() {
            @Override
            public void call(Subscriber<? super List<Cluster>> subscriber) {
                if (null != preSubscription && !preSubscription.isUnsubscribed()) {
                    preSubscription.unsubscribe();
                }
                if (null != subscription && !subscription.isUnsubscribed()) {
                    preSubscription = subscription;
                    List<Cluster> clusters = calculateClusters();
                    subscriber.onNext(clusters);
                }
            }
        });
        Action1<List<Cluster>> action1 = new Action1<List<Cluster>>() {
            @Override
            public void call(List<Cluster> clusters) {
                if (clusters != null && clusters.size() > 0) {
                    addClusterToMap(clusters);
                }
                subscription.unsubscribe();
            }
        };
        Logger.i(TAG, "assignClusters 开始订阅");
        subscription = observable.compose(RxSchedulersHelper.<List<Cluster>>ioMain())
                .subscribe(action1);

    }

    /**
     * 获取每个聚合点的绘制样式
     */
    private BitmapDescriptor getBitmapDes(Cluster cluster) {
        View view = mClusterRender.getView(cluster);
        return BitmapDescriptorFactory.fromView(view);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (mOnMapClickListener != null) {
            mOnMapClickListener.onMapClick();
        }
        dealMapClick();
    }

    /**
     * 处理地图点击
     */
    public void dealMapClick() {
        //将上一次点击的marker恢复原来的样式
        resetMarkerIcon();
        hideInfoWindow();
        //没有选中的marker，最后一步置为null
        curShowWindowMarker = null;
    }

    /**
     * 隐藏地图的InfoWindow
     */
    private void hideInfoWindow() {
        Logger.i(TAG, "hideInfoWindow");
        //隐藏marker的infoWindow
        if (mAMap != null && curShowWindowMarker != null) {
            if (curShowWindowMarker.isInfoWindowShown()) {
                curShowWindowMarker.hideInfoWindow();
                Logger.i(TAG, "hideInfoWindow了------");
            }
        }
    }

    public void showInfoWindow() {
        if (showParksResp != null && !showParksResp.isShowInfoWindow()) {
            synchronized (ClusterOverlay.class) {
                if (showParksResp != null) {
                    for (Marker marker : mAddMarkers) {
                        Cluster cluster = (Cluster) marker.getObject();
                        String parkName = showParksResp.getParkName();
                        if (cluster != null && cluster.getClusterItems().size() == 1 && null != parkName && parkName.equals(marker.getTitle())) {
                            hideInfoWindow();
                            marker.showInfoWindow();
                            curShowWindowMarker = marker;
                            showParksResp.setShowInfoWindow(true);

                            RxBusEvent event = new RxBusEvent();
                            event.setEventCode(RxEventCodes.CODE_TO_ROUTE);
                            event.setContent(curShowWindowMarker);
                            RxBus.getInstance().post(event);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void setShowParksResp(ParksResp showParksResp) {
        this.showParksResp = showParksResp;
    }

    public interface OnCameraUpdateFinishListener {
        void onCameraUpdateFinish(List<Cluster> clusters);
    }

    /**
     * 地图更新完成
     *
     * @param onCameraUpdateFinishListener
     */
    public void setCameraUpdateFinishListener(OnCameraUpdateFinishListener onCameraUpdateFinishListener) {
        this.mOnCameraUpdateFinishListener = onCameraUpdateFinishListener;
    }

    public interface OnHideInfoWindowListener {
        void onHideInfoWindow();
    }

    /**
     * 隐藏marker的InfoWindow
     *
     * @param onHideInfoWindowListener
     */
    public void setOnHideInfoWindowListener(OnHideInfoWindowListener onHideInfoWindowListener) {
        this.mOnHideInfoWindowListener = onHideInfoWindowListener;
    }

    public interface OnMapClickListener {
        void onMapClick();
    }

    /**
     * 点击地图
     *
     * @param onMapClickListener
     */
    public void setOnMapClickListener(OnMapClickListener onMapClickListener) {
        this.mOnMapClickListener = onMapClickListener;
    }

    public void setParkListByCityResps(List<ParkListByCityAreaResp> parkListByCityResps) {
        this.parkListByCityResps.clear();
        this.parkListByCityResps.addAll(parkListByCityResps);
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
                    Float d2 = AMapUtils.calculateLineDistance(rhs.getLatlng(), UserUtils.getLatLng());

                    return d1.compareTo(d2);
                }
            });
        }
    }

    public interface OnClusterLevelListener {
        void onClusterLevel(int clusterLevel);
    }

    /**
     * 聚合等级监听
     *
     * @param onClusterLevelListener
     */
    public void setOnClusterLevelListener(OnClusterLevelListener onClusterLevelListener) {
        this.onClusterLevelListener = onClusterLevelListener;
    }
}