package www.qisu666.com.adapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import www.qisu666.com.R;
import www.qisu666.com.callback.ParkResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.utils.DateUtils;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.NavigationUtils;
import www.qisu666.com.utils.StringUtils;

/**
 * Created by wujiancheng on 2017/7/29.
 * 预约中infoWindow
 */

public class ParkInfoWindowOrderingAdapter implements AMap.InfoWindowAdapter {
    private static final String TAG = ParkInfoWindowOrderingAdapter.class.getSimpleName();
    private static final int TIME_DOWN_COUNT = 1;
    private Activity activity;
    private int timeDown = Config.TIME_DOWN;
    private String carNumber;
    private MyHandler handler;
    private TextView tvTime;
    View view;

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIME_DOWN_COUNT:
                    timeDown = msg.arg1;
                    if (tvTime != null) {
                        tvTime.setText(DateUtils.time2MinuteSecond(timeDown));
                        tvTime.setVisibility(View.VISIBLE);
                    }
                    timeDown--;
                    if (timeDown <= 0) {
                        removeCallbacksAndMessages(null);
                        //取消订单
                        activity.finish();
                    } else {
                        Message message = handler.obtainMessage();
                        message.what = TIME_DOWN_COUNT;
                        message.arg1 = timeDown;
                        handler.sendMessageDelayed(message, 1000);
                    }

                    break;
            }
        }
    }

    public ParkInfoWindowOrderingAdapter(Activity activity, int timeDown, String carNumber) {
        this.activity = activity;
        this.timeDown = timeDown;
        this.carNumber = carNumber;

        timeDown(timeDown);
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        Logger.i(TAG, "getInfoWindow");
        ViewHolder holder;
        if (null == view) {
            holder = new ViewHolder();
            LayoutInflater mInflater = LayoutInflater.from(activity);
            view = mInflater.inflate(R.layout.layout_info_window_ordering, null);
            holder.tvParkName = (TextView) view.findViewById(R.id.tvParkName);
            holder.tvParkNamedDetail = (TextView) view.findViewById(R.id.tvParkNamedDetail);
            holder.tvCarNumber = (TextView) view.findViewById(R.id.tvCarNumber);
            holder.llytNavigate = (LinearLayout) view.findViewById(R.id.llytNavigate);
            holder.tvDistance = (TextView) view.findViewById(R.id.tvDistance);
            holder.tvGetCar = (LinearLayout) view.findViewById(R.id.tvGetCar);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            view.setTag(holder);
//        tvTime.setText(DateUtils.time2MinuteSecond(timeDown));
//        timeDown(timeDown);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final ParkResp parkResp = (ParkResp) marker.getObject();
        if (null != parkResp) {
            holder.tvParkName.setText(parkResp.getParkName());
            holder.tvParkNamedDetail.setText(parkResp.getParkAddress());
            holder.tvDistance.setText(parkResp.getDistance());
            holder.tvCarNumber.setText(carNumber);
            //返回取车
            holder.tvGetCar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
            //导航
            holder.llytNavigate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String lat = parkResp.getLatitude();
                    String lng = parkResp.getLongitude();
                    if (StringUtils.isIntOrFloat(lat) && StringUtils.isIntOrFloat(lng)) {
                        LatLng latLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
                        NavigationUtils.goNavigation(activity, latLng, 2);
                    }
                }

            });
        }
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    /**
     * 设置可用的车辆的按钮颜色
     */
    private void setFreeCarNumBg(TextView tvLookCars, int colorId, int bgDrawable) {
        tvLookCars.setTextColor(ContextCompat.getColor(activity, colorId));
        tvLookCars.setBackgroundResource(bgDrawable);
    }

    private void timeDown(int timeDown) {
        if (tvTime != null) {
            tvTime.setText(DateUtils.time2MinuteSecond(timeDown));
        }
        //倒计时
        handler = new MyHandler();
        Message msg = handler.obtainMessage();
        msg.what = TIME_DOWN_COUNT;
        this.timeDown = timeDown;
        msg.arg1 = this.timeDown;
        handler.sendMessage(msg);
    }

    public void destroyHandler() {
        if (null != handler) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    static class ViewHolder {
        TextView tvParkName;
        TextView tvParkNamedDetail;
        TextView tvCarNumber;
        LinearLayout llytNavigate;
        TextView tvDistance;
        LinearLayout tvGetCar;
    }
}
