package www.qisu666.com.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import www.qisu666.com.R;

/**
 * Created by wujiancheng on 2017/7/29.
 * 是否进入地理围栏的提示
 */

public class FenceInfoWindowAdapter implements AMap.InfoWindowAdapter {
    private static final String TAG = FenceInfoWindowAdapter.class.getSimpleName();
    private Activity activity;

    public FenceInfoWindowAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        LayoutInflater mInflater = LayoutInflater.from(activity);
        return mInflater.inflate(R.layout.layout_info_window_fence, null);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
