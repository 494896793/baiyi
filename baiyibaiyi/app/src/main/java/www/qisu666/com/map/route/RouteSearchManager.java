package www.qisu666.com.map.route;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.Polyline;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import www.qisu666.com.map.overlay.DrivingRouteOverlay;
import www.qisu666.com.map.overlay.WalkRouteOverlay;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.ToastUtil;

import java.util.List;

/**
 * Created by wujiancheng on 2017/9/1.
 * 路线规划管理类
 */

public class RouteSearchManager {
    private static final String TAG = RouteSearchManager.class.getSimpleName();
    public static final int WALK_ROUTE = 1;
    public static final int DRIVE_ROUTE = 2;
    private WalkRouteOverlay walkRouteOverlay;
    private DrivingRouteOverlay drivingRouteOverlay;
    private OnDriveRouteCompleteListener onDriveRouteCompleteListener;

    public void route(final Context context, final AMap aMap, LatLonPoint mStartPoint, LatLonPoint mEndPoint, final Marker startMarker, final Marker endMarker, int routType) {
        RouteSearch routeSearch = new RouteSearch(context);
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);

        clearRoute();
        if (routType == WALK_ROUTE) {
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault);
            routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        } else if (routType == DRIVE_ROUTE) {
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾驶模式查询
        }
        routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int errorCode) {
                if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (driveRouteResult != null && driveRouteResult.getPaths() != null) {
                        clearRoute();
                        if (driveRouteResult.getPaths().size() > 0) {
                            final DrivePath drivePath = driveRouteResult.getPaths()
                                    .get(0);

                            if (null != onDriveRouteCompleteListener) {
                                onDriveRouteCompleteListener.onDriveRouteComplete(drivePath);
                            }

                            if (null != aMap) {
                                drivingRouteOverlay = new DrivingRouteOverlay(
                                        context, aMap, drivePath,
                                        driveRouteResult.getStartPos(),
                                        driveRouteResult.getTargetPos(), null);
                                drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                                drivingRouteOverlay.setIsColorfulline(false);//是否用颜色展示交通拥堵情况，默认true
                                drivingRouteOverlay.removeFromMap();
                                drivingRouteOverlay.addToMap(startMarker, endMarker);
//                                drivingRouteOverlay.zoomToSpan();
                            }

                        } else if (driveRouteResult.getPaths() == null) {
                            if (null != aMap) {
//                                ToastUtil.show(context, "规划路线失败");
                            }
                        }
                    } else {
                        if (null != aMap) {
//                            ToastUtil.show(context, "规划路线失败");
                        }
                    }
                } else {
                    if (null != aMap) {
//                        ToastUtil.show(context, "规划路线失败");
                    }
                }
            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int errorCode) {
                if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (walkRouteResult != null && walkRouteResult.getPaths() != null) {
                        clearRoute();
                        if (walkRouteResult.getPaths().size() > 0) {
                            final WalkPath walkPath = walkRouteResult.getPaths()
                                    .get(0);
                            walkRouteOverlay = new WalkRouteOverlay(
                                    context, aMap, walkPath,
                                    walkRouteResult.getStartPos(),
                                    walkRouteResult.getTargetPos());
                            walkRouteOverlay.removeFromMap();
                            walkRouteOverlay.addToMap(startMarker, endMarker);
//                            walkRouteOverlay.zoomToSpan();

                        } else if (walkRouteResult.getPaths() == null) {
                            ToastUtil.show(context, "规划路线失败");
                        }
                    } else {
                        ToastUtil.show(context, "规划路线失败");
                    }
                } else {
                    Logger.i(TAG, "errorCode==" + errorCode);
                }
            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

            }
        });
    }

    /**
     * 清除路线
     */
    public void clearRoute() {
        if (walkRouteOverlay != null) {
            List<Polyline> allPolyLines = walkRouteOverlay.getAllPolyLines();
            for (Polyline polyline : allPolyLines) {
                polyline.remove();
            }
        } else if (drivingRouteOverlay != null) {
            List<Polyline> allPolyLines = drivingRouteOverlay.getAllPolyLines();
            Logger.i(TAG, "drivingRouteOverlay allPolyLines.size=" + allPolyLines.size());
            for (Polyline polyline : allPolyLines) {
                polyline.remove();
            }
        }
    }

    /**
     * 驾车路线规划结束
     */
    public interface OnDriveRouteCompleteListener {
        void onDriveRouteComplete(DrivePath drivePath);
    }

    public void setOnDriveRouteCompleteListener(OnDriveRouteCompleteListener onDriveRouteCompleteListener) {
        this.onDriveRouteCompleteListener = onDriveRouteCompleteListener;
    }
}
