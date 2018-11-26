package www.qisu666.com.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import www.qisu666.com.R;
import www.qisu666.com.activity.ChooseReturnParkActivity;
import www.qisu666.com.callback.ParksResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.request.ReturnCarRequest;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.NavigationUtils;
import www.qisu666.com.utils.StringUtils;
import www.qisu666.com.utils.UserUtils;
import www.qisu666.com.view.CustomAlertDialog;

/**
 * Created by wujiancheng on 2017/7/29.
 * 还车infoWindow
 */

public class ParkInfoWindowReturnAdapter implements AMap.InfoWindowAdapter {
    private static final String TAG = ParkInfoWindowReturnAdapter.class.getSimpleName();
    private Activity activity;
    private String orderId;
    private int fromReturn;

    public ParkInfoWindowReturnAdapter(Activity activity, String orderId, int fromReturn) {
        this.activity = activity;
        this.orderId = orderId;
        this.fromReturn = fromReturn;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        LayoutInflater mInflater = LayoutInflater.from(activity);
        View view = mInflater.inflate(R.layout.layout_info_window_return_car, null);
        TextView tvParkName = (TextView) view.findViewById(R.id.tvParkName);
        TextView tvParkNamedDetail = (TextView) view.findViewById(R.id.tvParkNamedDetail);
        LinearLayout llytNavigate = (LinearLayout) view.findViewById(R.id.llytNavigate);
        TextView tvDistance = (TextView) view.findViewById(R.id.tvDistance);
        TextView tvReturnCar = (TextView) view.findViewById(R.id.tvReturnCar);
//        TextView tvFreeCarportNum = (TextView) view.findViewById(R.id.tvFreeCarportNum);

        final ParksResp parksResp = (ParksResp) marker.getObject();
        if (null != parksResp) {
            tvParkName.setText(parksResp.getParkName());
            tvParkNamedDetail.setText(parksResp.getParkAddress());
            tvDistance.setText(parksResp.getDistance());

            tvReturnCar.setText("立即还车");
            setFreeCarNumBg(tvReturnCar, R.color.color_blue_02b2e4, R.drawable.bg_blue_stroke_half_radius);
            tvReturnCar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CustomAlertDialog alertDialog = CustomAlertDialog.getAlertDialog(activity, false, false);
                    alertDialog.setMessage("请遵守交通规则正确停放，如违规停放，后果将由您自行承担，是否确认还车？")
                            .setBtnCancelColor(R.color.main_background)
                            .setBtnConfirmColor(R.color.new_primary)
                            .setOnNegativeClickListener("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            })
                            .setOnPositiveClickListener("确认还车", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    returnCar(parksResp);
                                    alertDialog.dismiss();
                                }
                            }).show();
                }
            });

            //导航
            llytNavigate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String lat = parksResp.getLatitude();
                    String lng = parksResp.getLongitude();
                    if (StringUtils.isIntOrFloat(lat) && StringUtils.isIntOrFloat(lng)) {
                        LatLng latLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
                        NavigationUtils.goNavigation(activity, latLng, 0);
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

    public void returnCar(ParksResp parksResp) {
        Logger.i(TAG, "还车的网点==" + parksResp.getParkName());

        ReturnCarRequest request = new ReturnCarRequest();
        request.setCustomerId(UserUtils.getCustomerId());
        request.setLatitude(UserUtils.getLatitude());
        request.setLongitude(UserUtils.getLongitude());
        if (fromReturn == Config.RETURN_CAR_HOUR) {//时租还车
            request.setCarSharingOrderNumber(orderId);
            request.setMethod(RequestUrls.USER_PARK_RETURN_CAR);
        } else if (fromReturn == Config.RETURN_CAR_LONG) {//短租还车
            request.setCarRentOrderNumber(orderId);
            request.setMethod(RequestUrls.QUERY_LONG_RENT_RETURN_CAR);
        }
        request.setDepotId(parksResp.getId());
        request.setIsCheck("1");
        request.setEnjoyDiscounts("true");
        if (activity instanceof ChooseReturnParkActivity) {
//            ((ChooseReturnParkActivity) activity).doGet(request, ChooseReturnParkActivity.RETURN_CAR, "正在检测车辆状态", true);
        }
    }
}
