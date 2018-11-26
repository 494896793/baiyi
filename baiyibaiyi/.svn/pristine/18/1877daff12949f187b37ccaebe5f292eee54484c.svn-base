package www.qisu666.com.adapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import www.qisu666.com.R;
import www.qisu666.com.callback.CarResp;
import www.qisu666.com.utils.DateUtils;
import www.qisu666.com.utils.Logger;

/**
 * Created by wujiancheng on 2017/7/29.
 * 用车中infoWindow
 */

public class ParkInfoWindowUsingAdapter implements AMap.InfoWindowAdapter {
    private static final String TAG = ParkInfoWindowUsingAdapter.class.getSimpleName();
    private Activity activity;
    private TextView tvUsingTime;
    private MyHandler handler;
    private int useTime = 1;
    private View view;

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    int time = (int) msg.obj;
                    time++;
                    if (null != tvUsingTime) {
                        tvUsingTime.setText(DateUtils.minuteToDay(time));
                    }
                    Message message = handler.obtainMessage();
                    message.what = 1;
                    message.obj = time;
                    handler.sendMessageDelayed(message, 1000 * 60);

                    break;
            }
        }
    }

    public ParkInfoWindowUsingAdapter(Activity activity, int useTime) {
        this.activity = activity;
        this.useTime = useTime;

        handler = new MyHandler();
        Message msg = handler.obtainMessage();
        msg.what = 1;
        msg.obj = this.useTime;
        handler.sendMessageDelayed(msg, 1000 * 60);
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        Logger.i(TAG, "getInfoWindow");
        LayoutInflater mInflater = LayoutInflater.from(activity);
        if (null == view) {
            view = mInflater.inflate(R.layout.layout_info_window_using, null);
            TextView tvCarNumber = (TextView) view.findViewById(R.id.tvCarNumber);
            tvUsingTime = (TextView) view.findViewById(R.id.tvUsingTime);
            LinearLayout llytBack = (LinearLayout) view.findViewById(R.id.llytBack);

            tvUsingTime.setText(DateUtils.minuteToDay(useTime));
            final CarResp carInfoCode = (CarResp) marker.getObject();
            if (null != carInfoCode) {
                tvCarNumber.setText(carInfoCode.getCarNumber());

                //返回用车
                llytBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.finish();
                    }

                });
            }
        }
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public void destroyHandler() {
        if (null != handler) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
