package www.qisu666.com.map.search;

import android.content.Context;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

/**
 * Created by wujiancheng on 2018/1/25.
 * 地理编码与逆地理编码功能
 */

public class GeoCoderManager implements GeocodeSearch.OnGeocodeSearchListener {
    private OnReGeoCodeSearchListener onReGeoCodeSearchListener;
    private GeocodeSearch geocoderSearch;

    public GeoCoderManager(Context context) {
        geocoderSearch = new GeocodeSearch(context);
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(LatLng latLng) {
        if (null == latLng) {
            return;
        }
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                String addressName = result.getRegeocodeAddress().getFormatAddress();
                if (null != onReGeoCodeSearchListener) {
                    onReGeoCodeSearchListener.onReGeoCodeSearch(addressName);
                }
            }
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    public interface OnReGeoCodeSearchListener {
        void onReGeoCodeSearch(String addressName);
    }

    public void setOnReGeoCodeSearchListener(OnReGeoCodeSearchListener onReGeoCodeSearchListener) {
        this.onReGeoCodeSearchListener = onReGeoCodeSearchListener;
    }
}
