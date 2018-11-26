package www.qisu666.com.controls

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.amap.api.maps.*
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.services.core.LatLonPoint
import www.qisu666.com.adapter.InfoWindowNearestAdapter
import www.qisu666.com.callback.ParkListByCityAreaResp
import www.qisu666.com.callback.ParksResp
import www.qisu666.com.constant.Config
import www.qisu666.com.map.cluster.Cluster
import www.qisu666.com.map.cluster.ClusterClickListener
import www.qisu666.com.map.cluster.ClusterOverlay
import www.qisu666.com.map.cluster.ClusterRender
import www.qisu666.com.map.route.RouteSearchManager
import www.qisu666.com.utils.CacheUtils
import www.qisu666.com.utils.DensityUtil
import www.qisu666.com.utils.UserUtils
import www.qisu666.com.utils.logI
import www.qisu666.com.view.ViewShowUtil

/**
 * Created by wujiancheng on 2018/2/7.
 * 首页地图逻辑
 */
class MapCtrl(val activity: Activity, val mapView: MapView, val savedInstanceState: Bundle?) : ClusterRender, ClusterClickListener {

    var onSelectedParkListener: OnSelectedParkListener? = null
    var onClusterLevelListener: OnClusterLevelListener? = null
    var onMapClickListener: OnMapClickListener? = null
    private var aMap: AMap? = null
    private var uiSettings: UiSettings? = null
    private var mClusterOverlay: ClusterOverlay? = null //聚合
    private val clusterRadius = 40//聚合
    private var routeSearchManager: RouteSearchManager? = null
    private var myMarker: Marker? = null

    init {
        onCreate(savedInstanceState)
        aMap = mapView.map
        uiSettings = aMap?.uiSettings

        //去掉缩放按钮
        uiSettings?.isZoomControlsEnabled = false//去掉放大缩小按钮
        uiSettings?.isMyLocationButtonEnabled = false// 设置默认定位按钮是否显示
        var adapter=InfoWindowNearestAdapter(activity)
        aMap?.setInfoWindowAdapter(adapter)
        aMap?.isMyLocationEnabled = true// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap?.setMyLocationType(AMap.LOCATION_TYPE_LOCATE)
    }

    /**
     * marker的点击事件
     */
    override fun onClick(marker: Marker?) {
        if (marker == null || "my" == marker.title) {
            return
        }
        val cluster = marker.`object` as Cluster ?: return
        val clusterLevel = cluster.clusterLevel
        if (clusterLevel == ClusterOverlay.CITY_CLUSTER) {//城市级
            aMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.centerLatLng, Config.MAP_ZOOM_11.toFloat()))
        } else if (clusterLevel == ClusterOverlay.PARK_CLUSTER) {//网点级
            route(marker, true)
            //显示选中的网点的信息
            showSelectedParkInfo(marker)
        } else if (clusterLevel == ClusterOverlay.AREA_CLUSTER) {//区级
            val resps = cluster.clusterItems
            if (null != resps && resps.size > 0) {
                val latLng = resps[0].latlng

                aMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Config.MAP_ZOOM_16.toFloat()))
            }
        }
    }

    override fun getView(cluster: Cluster?): View {
        return ViewShowUtil.setClusterMarker(activity, cluster)
    }

    private fun onCreate(savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
    }

    fun onResume() {
        mapView.onResume()
    }

    fun onPause() {
        mapView.onPause()
    }

    fun onDestroy() {
        mapView.onDestroy()
    }

    fun onSaveInstanceState(outState: Bundle?) {
        mapView.onSaveInstanceState(outState)
    }

    /**
     * 清空地图上的覆盖物
     */
    fun clear() {
        aMap?.clear()
    }

    /**
     * 初始化聚合物
     */
    fun initCluster(parkListByCityResps: List<ParkListByCityAreaResp>, showPark: ParksResp, resetClusterOverlay: Boolean) {
        if (resetClusterOverlay) {
            mClusterOverlay = null
        }
        if (null == mClusterOverlay) {
            synchronized(MapCtrl::class.java) {
                if (null == mClusterOverlay) {
                    logI("初始化initCluster")
                    mClusterOverlay = ClusterOverlay(aMap, parkListByCityResps,
                            DensityUtil().dp2px(activity, clusterRadius.toFloat()),
                            activity, showPark)
                    mClusterOverlay?.setClusterRenderer(this)
                    mClusterOverlay?.setOnClusterClickListener(this)
                    //点击地图清空规划路线
                    mClusterOverlay?.setOnMapClickListener({
                        clearRoute()
                        onMapClickListener?.onMapClick()
                    })
                    //地图网点聚合等级监听
                    mClusterOverlay?.setOnClusterLevelListener({
                        onClusterLevelListener?.onClusterLevel(it)
                    })
                }
            }
        }
    }

    private fun clearRoute() {
        routeSearchManager?.clearRoute()
    }

    /**
     * 处理地图点击
     */
    fun dealMapClick() {
        mClusterOverlay?.dealMapClick()
        clearRoute()
    }

    /**
     * 移动到LatLng
     *
     * @param latLng
     */
    fun animateToLatLng(latLng: LatLng) {
        aMap?.animateCamera(CameraUpdateFactory.changeLatLng(latLng))
    }

    /**
     * 有动画的缩放到指定zoom
     */
    fun animateToZoom(zoom: Float) {
        aMap?.animateCamera(CameraUpdateFactory.zoomTo(zoom))
    }

    /**
     * 有时间延迟的且有动画的缩放到指定zoom
     */
    fun animateToZoomDelay(zoom: Float) {
        mapView.postDelayed({ animateToZoom(zoom) }, 1700)
    }

    /**
     * 从当前位置导航到指定的marker
     */
    fun route(marker: Marker, isMove: Boolean) {
        val latLng = marker.position
        val userLat = UserUtils.getLatitude().toDouble()
        val userLng = UserUtils.getLongitude().toDouble()

        val distance = AMapUtils.calculateLineDistance(LatLng(userLat, userLng), latLng).toDouble()
        if (distance <= 5000 && myMarker != null) {//导航条件
            logI("导航")
            if (routeSearchManager == null) {
                routeSearchManager = RouteSearchManager()
            }
            val startPoint = LatLonPoint(userLat, userLng)
            val endPoint = LatLonPoint(latLng.latitude, latLng.longitude)

            routeSearchManager?.route(activity, aMap, startPoint, endPoint, myMarker, marker, RouteSearchManager.WALK_ROUTE)
        } else {
            if (isMove) {
                logI("不导航")
                animateToLatLng(latLng)
            }
        }
    }

    /**
     * 设置我的位置Marker
     */
    fun setMyMarker() {
        myMarker?.remove()
        myMarker = ViewShowUtil.setMyMarker(aMap)
    }

    /**
     * 移动到当前位置
     */
    fun animateToMyPosition() {
        val myLatLng = LatLng(UserUtils.getLatitude().toDouble(), UserUtils.getLongitude().toDouble())
        myMarker?.position = myLatLng
        if (!TextUtils.isEmpty(CacheUtils.getIn().getStr(CacheUtils.locationOk))) {
            aMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, Config.MAP_ZOOM_18.toFloat()))
        }
    }

    /**
     * 设置地图的中心点
     */
    fun setMapCenterPoint(x: Int, y: Int) {
        aMap?.setPointToCenter(x, y)
    }

    /**
     * 展示选中网点信息
     *
     * @param marker
     */
    private fun showSelectedParkInfo(marker: Marker) {
        val o = marker.`object`
        if (null != o) {
            val cluster = o as Cluster
            val parksResps = cluster.clusterItems
            if (null != parksResps && parksResps.size == 1) {
                val resp = parksResps[0]
                if (null != resp) {
                    onSelectedParkListener?.onSelectedPark(resp)
                }
            }
        }
    }

    /**
     * 选中的网点显示infoWindow
     */
    fun showInfoWindow(showPark: ParksResp) {
        mClusterOverlay?.setShowParksResp(showPark)
        mClusterOverlay?.showInfoWindow()
    }

    /**
     * 选择网点监听
     */
    interface OnSelectedParkListener {
        fun onSelectedPark(parkInfo: ParksResp)
    }

    /**
     * 地图网点聚合等级监听
     */
    interface OnClusterLevelListener {
        fun onClusterLevel(clusterLevel: Int)
    }

    /**
     * 点击地图监听
     */
    interface OnMapClickListener {
        fun onMapClick()
    }
}