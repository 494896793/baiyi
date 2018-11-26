package www.qisu666.com.map.geoFence;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.amap.api.fence.GeoFence;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wujiancheng on 2017/11/7.
 * 地理围栏
 */

public class GeoFenceManager {
    public static final int FENCE_TYPE_CIRCLE = 1;
    public static final int FENCE_TYPE_POLYGON = 2;

    /**
     * 地图中绘制多边形、圆形的边界颜色
     *
     * @since 3.3.0
     */
    public static final int STROKE_COLOR = Color.argb(120,11,18,34);
    /**
     * 地图中绘制多边形、圆形的填充颜色
     *
     * @since 3.3.0
     */
    public static final int FILL_COLOR = Color.argb(60, 81, 231 , 211);

    /**
     * 地图中绘制多边形、圆形的边框宽度
     *
     * @since 3.3.0
     */
    public static final float STROKE_WIDTH = 2F;

    private Context context;
    private static AMap aMap;
    private static int strokeColor, fillColor;
    private static float strokeWidth;
    private static Polygon prePolygon;//记录之前的多边形地理围栏
    private static Circle preCircle;//记录之前的圆形地理围栏
    private Intent intent;

    /**
     * @param context
     * @param aMap
     * @param strokeColor
     * @param fillColor
     * @param strokeWidth
     */
    public GeoFenceManager(Context context, AMap aMap, int strokeColor, int fillColor, float strokeWidth) {
        this.context = context;
        GeoFenceManager.aMap = aMap;
        GeoFenceManager.strokeColor = strokeColor;
        GeoFenceManager.fillColor = fillColor;
        GeoFenceManager.strokeWidth = strokeWidth;
    }

    /**
     * 添加多边形的地理围栏
     *
     * @param latLngs
     */
    public void addGeoFencePolygon(ArrayList<LatLng> latLngs) {
        if (intent == null) {
            intent = new Intent(context, GeoFenceService.class);
        }
        intent.putExtra("fenceType", FENCE_TYPE_POLYGON);
        intent.putParcelableArrayListExtra("latLngs", latLngs);
        context.startService(intent);
    }

    /**
     * 添加圆形地理围栏
     *
     * @param centerLatLng 圆心经纬度
     * @param radius       半径
     */
    public void addGeoFenceCircle(LatLng centerLatLng, float radius) {
        if (intent == null) {
            intent = new Intent(context, GeoFenceService.class);
        }
        intent.putExtra("fenceType", FENCE_TYPE_CIRCLE);
        intent.putExtra("centerLatLng", centerLatLng);
        intent.putExtra("radius", radius);
        context.startService(intent);
    }

    /**
     * 圆形地理围栏
     *
     * @param fence
     */
    public static void drawCircle(GeoFence fence) {
//        removeGeoFenceOverlay();
        LatLng center = new LatLng(fence.getCenter().getLatitude(),
                fence.getCenter().getLongitude());
        // 绘制一个圆形
        preCircle = aMap.addCircle(new CircleOptions().center(center)
                .radius(fence.getRadius()).strokeColor(strokeColor)
                .fillColor(fillColor).strokeWidth(strokeWidth));
//        boundsBuilder.include(center);
    }

    /**
     * 多边形地理围栏
     *
     * @param fence
     */
    public static void drawPolygon(GeoFence fence) {
//        removeGeoFenceOverlay();
        final List<List<DPoint>> pointList = fence.getPointList();
        if (null == pointList || pointList.isEmpty()) {
            return;
        }

        for (List<DPoint> subList : pointList) {
            List<LatLng> lst = new ArrayList<LatLng>();

            PolygonOptions polygonOption = new PolygonOptions();
            for (DPoint point : subList) {
                lst.add(new LatLng(point.getLatitude(), point.getLongitude()));
//                boundsBuilder.include(
//                        new LatLng(point.getLatitude(), point.getLongitude()));
            }
            polygonOption.addAll(lst);

            polygonOption.strokeColor(strokeColor)
                    .fillColor(fillColor).strokeWidth(strokeWidth);
            prePolygon = aMap.addPolygon(polygonOption);
        }
    }

    /**
     * 移除掉地理围栏
     */
    public static void removeGeoFenceOverlay() {
        if (null != prePolygon) {
            prePolygon.remove();
        }
        if (null != preCircle) {
            preCircle.remove();
        }
    }

    /**
     * 停止服务
     */
    public void onDestroy() {
        if (null != intent) {
            context.stopService(intent);
        }
    }
}
