package www.qisu666.com.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import www.qisu666.com.R;
import www.qisu666.com.activity.CarListActivity;
import www.qisu666.com.activity.WebActivity;
import www.qisu666.com.callback.ParksResp;
import www.qisu666.com.callback.SystemConfigResp;
import www.qisu666.com.map.cluster.Cluster;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.NavigationUtils;
import www.qisu666.com.utils.StringUtils;
import www.qisu666.com.view.CustomImageSpan;

/**
 * Created by wujiancheng on 2017/7/29.
 * 主页infoWindow
 */

public class ParkInfoWindowAdapter implements AMap.InfoWindowAdapter {
    private static final String TAG = ParkInfoWindowAdapter.class.getSimpleName();
    private Activity activity;

    public ParkInfoWindowAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View getInfoWindow(final Marker marker) {
        View view = null;
        Object object = marker.getObject();
        Cluster cluster = null;
        ParksResp parksResp;
        if (object instanceof Cluster) {
            cluster = (Cluster) marker.getObject();
        } else {
            Logger.i(TAG, "InfoWindow为空");
        }
        if (null != cluster && cluster.getClusterItems().size() == 1) {
            parksResp = cluster.getClusterItems().get(0);
            if (null != parksResp) {
                LayoutInflater mInflater = LayoutInflater.from(activity);
                if (parksResp.getRedPacketCarNum() <= 0) {
                    view = mInflater.inflate(R.layout.layout_park_info_window, null);
                } else {//红包车
                    view = mInflater.inflate(R.layout.layout_park_info_window_red_packet, null);
                    TextView tvRedPacketNum = (TextView) view.findViewById(R.id.tvRedPacketNum);
                    tvRedPacketNum.setText("红包车" + parksResp.getRedPacketCarNum() + "辆");
                    tvRedPacketNum.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SystemConfigResp systemConfigResp = CacheUtils.getIn().getSystem_Info();
                            if (null != systemConfigResp) {
                                Intent intent = new Intent(activity, WebActivity.class);
                                intent.putExtra("url", systemConfigResp.getRedpackageUseRule());
                                activity.startActivity(intent);
                            }
                        }
                    });
                }
                TextView tvParkName = (TextView) view.findViewById(R.id.tvParkName);
                TextView tvParkNamedDetail = (TextView) view.findViewById(R.id.tvParkNamedDetail);
                LinearLayout llytNavigate = (LinearLayout) view.findViewById(R.id.llytNavigate);
                TextView tvDistance = (TextView) view.findViewById(R.id.tvDistance);
                TextView tvLookCars = (TextView) view.findViewById(R.id.tvLookCars);

                String parkName = parksResp.getParkName();
                boolean isNearest = parksResp.isNearest();
                if (isNearest) {
                    String replace = "离我最近";
                    SpannableString spannableString = new SpannableString(parkName + " " + replace);

                    //获取图片
                    CustomImageSpan span = new CustomImageSpan(activity, R.mipmap.nearest_icon, CustomImageSpan.ALIGN_FONT_CENTER);

                    spannableString.setSpan(span, (parkName + " ").length(), (parkName + " " + replace).length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    tvParkName.setText(spannableString);
                } else {
                    tvParkName.setText(parkName);
                }
                tvParkNamedDetail.setText(parksResp.getParkAddress());
                tvDistance.setText(parksResp.getDistance());

                String freeCarNum = parksResp.getParkFreeCarNum();
                if (!TextUtils.isEmpty(freeCarNum)) {
                    tvLookCars.setText(freeCarNum + "辆可用车辆");
                    int num = Integer.parseInt(freeCarNum);
                    if (num > 0) {
                        setFreeCarNumBg(tvLookCars, R.color.color_blue_02b2e4, R.drawable.bg_blue_stroke_half_radius);
                        final ParksResp finalParksResp = parksResp;
                        tvLookCars.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(activity, CarListActivity.class);
                                intent.putExtra("parksResp", finalParksResp);
                                activity.startActivity(intent);

//                    marker.hideInfoWindow();
                            }
                        });
                    } else {
                        setFreeCarNumBg(tvLookCars, R.color.color_gray_999999, R.drawable.bg_gray_stroke_half_radius);
                    }
                } else {
                    tvLookCars.setText("0辆可用车辆");
                    setFreeCarNumBg(tvLookCars, R.color.color_gray_999999, R.drawable.bg_gray_stroke_half_radius);
                }

                //导航
                final ParksResp finalParksResp1 = parksResp;
                llytNavigate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String lat = finalParksResp1.getLatitude();
                        String lng = finalParksResp1.getLongitude();
                        if (StringUtils.isIntOrFloat(lat) && StringUtils.isIntOrFloat(lng)) {
                            LatLng latLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
                            NavigationUtils.goNavigation(activity, latLng, 2);
                        }
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

    /**
     * 设置可用的车辆的按钮颜色
     */
    private void setFreeCarNumBg(TextView tvLookCars, int colorId, int bgDrawable) {
        tvLookCars.setTextColor(ContextCompat.getColor(activity, colorId));
        tvLookCars.setBackgroundResource(bgDrawable);
    }
}
