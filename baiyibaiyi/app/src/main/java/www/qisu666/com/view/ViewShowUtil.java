package www.qisu666.com.view;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import www.qisu666.com.R;
import www.qisu666.com.callback.ParksResp;
import www.qisu666.com.map.cluster.Cluster;
import www.qisu666.com.map.cluster.ClusterOverlay;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.TVUtils;
import www.qisu666.com.utils.UserUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by baby on 2016/4/7.
 */
public class ViewShowUtil {
    private static List<ParksResp> parkInfos;

    public static boolean show(List<View> list, int[] args) {
        if (list != null && list.size() > 0 && args != null & args.length > 0) {
            int size = args.length;
            for (int position = 0; position < size; position++) {
                int arg = args[position];
                if (arg == 1) {
                    list.get(position).setVisibility(View.VISIBLE);
                } else if (arg == 0) {
                    list.get(position).setVisibility(View.GONE);
                } else if (arg == 2) {
                    list.get(position).setVisibility(View.INVISIBLE);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 设置个人位置
     *
     * @param
     * @param aMap
     */
    public static Marker setMyMarker(AMap aMap) {
        Logger.i("UserUtils.getLatitude()=" + UserUtils.getLatitude());
        if ("0.0".equals(UserUtils.getLatitude()) || "0.0".equals(UserUtils.getLongitude())) {
            return null;
        }
        Marker marker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .position(UserUtils.getLatLng())
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.my_position))
                .period(1)
                .draggable(true).period(10));
        marker.setTitle("my");
        return marker;
    }

    public static void setMyMarker(AMap aMap, LatLng l) {
        aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .position(l)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_main))
                .draggable(true).period(10)).setTitle("my");
    }

    /**
     * 设置圆圈
     */
    public static Circle setMapCircle(AMap aMap, LatLng latLng, int radius, Context context) {
        return aMap.addCircle(new CircleOptions().
                center(latLng).
                radius(radius).
                fillColor(Color.argb(1, 1, 1, 1)).
                strokeColor(ContextCompat.getColor(context, R.color.slblue)).
                strokeWidth(2.0F));
    }

    /**
     * 设置停车场位置
     *
     * @param mContext
     * @param aMap
     */
    public static void addParkMarker(Context mContext, AMap aMap, List<ParksResp> mParkInfos, int position) {
        for (int i = 0, size = mParkInfos.size(); i < size + 1; i++) {
            MarkerOptions markerOptions = null;
            if (i != size) {
                ParksResp mParkInfo = mParkInfos.get(i);
                if (!TextUtils.isEmpty(mParkInfo.getLatitude()) && !TextUtils.isEmpty(mParkInfo.getLongitude())) {
                    LatLng mLatLng = new LatLng(Double.valueOf(mParkInfo.getLatitude()),
                            Double.valueOf(mParkInfo.getLongitude()));
                    if (position != i) {
                        markerOptions = new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .position(mLatLng)
                                .period(i + 1)
//                            .title(i + "")
                                .icon(BitmapDescriptorFactory.fromView(addParkToMap(mContext, mParkInfo.getParkFreeCarNum()))).draggable(true);
                    } else if (i == position) {
                        continue;
                    }
                }
            } else {
                if (!TextUtils.isEmpty(mParkInfos.get(position).getLatitude()) && !TextUtils.isEmpty(mParkInfos.get(position).getLongitude())) {
                    LatLng mLatLng = new LatLng(Double.valueOf(mParkInfos.get(position).getLatitude()),
                            Double.valueOf(mParkInfos.get(position).getLongitude()));
                    markerOptions = new MarkerOptions()
                            .anchor(0.5f, 0.5f)
                            .position(mLatLng)
                            .period(position + 1)
//                            .title(i + "")
                            .icon(BitmapDescriptorFactory.fromView(addRedParkToMap(mContext, mParkInfos.get(position).getParkFreeCarNum()))).draggable(true);
                }
            }
            aMap.addMarker(markerOptions).showInfoWindow();
        }
        parkInfos = mParkInfos;
    }

    /**
     * 设置停车场移动后位置
     *
     * @param mContext
     * @param aMap
     */
    public static void setParkClickMarker(Context mContext, AMap aMap, List<ParksResp> mParkInfos, int position, int positionBefore) {
        setBlueMarker(mContext, positionBefore, mParkInfos, aMap);
        setRedMarker(mContext, position, mParkInfos, aMap);
    }

    private static void setBlueMarker(Context mContext, int position, List<ParksResp> mParkInfos, AMap aMap) {
        LatLng mLatLng = new LatLng(Double.valueOf(mParkInfos.get(position).getLatitude()),
                Double.valueOf(mParkInfos.get(position).getLongitude()));
        MarkerOptions markerOptions = new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .position(mLatLng)
                .period(position + 1)
                .icon(BitmapDescriptorFactory.fromView(addParkToMap(mContext, mParkInfos.get(position).getParkFreeCarNum()))).draggable(true);
        aMap.addMarker(markerOptions).showInfoWindow();
    }

    public static void setRedMarker(Context mContext, int position, List<ParksResp> mParkInfos, AMap aMap) {
        if (!TextUtils.isEmpty(mParkInfos.get(position).getLatitude()) && !TextUtils.isEmpty(mParkInfos.get(position).getLongitude())) {
            LatLng mLatLng = new LatLng(Double.valueOf(mParkInfos.get(position).getLatitude()),
                    Double.valueOf(mParkInfos.get(position).getLongitude()));
            MarkerOptions markerOptions = new MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .position(mLatLng)
                    .period(position + 1)
                    .icon(BitmapDescriptorFactory.fromView(addRedParkToMap(mContext, mParkInfos.get(position).getParkFreeCarNum()))).draggable(true);
            aMap.addMarker(markerOptions).showInfoWindow();
        }
    }

    /**
     * 停车场
     *
     * @param mContext
     * @param num
     * @return
     */
    public static View addParkToMap(Context mContext, String num) {
        View view = LayoutInflater.from(mContext).inflate(
                null, null);
        return view;
    }

    /**
     * 将充电站添加至地图
     *
     * @param mContext
     * @param num
     * @return
     */
    public static View addChargeToMap(Context mContext, String num) {
        View view = LayoutInflater.from(mContext).inflate(
                null, null);
        return view;
    }

    /**
     * 停车场
     *
     * @param mContext
     * @param num
     * @return
     */
    public static View addRedParkToMap(Context mContext, String num) {
        View view = LayoutInflater.from(mContext).inflate(
                null, null);
        return view;
    }

    /**
     * 设置停车场marker
     *
     * @param mContext
     * @param freeCarportNum
     * @return
     */
    public static View addParkMarker(Context mContext, int freeCarportNum) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.layout_park_choose_home, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_choose_marker);
        if (freeCarportNum > 0) {
            imageView.setImageResource(R.mipmap.marker_has_space);
        } else {
            imageView.setImageResource(R.mipmap.marker_no_space);
        }
        return view;
    }

    /**
     * 选择还车网点，marker的选中的正常状态
     *
     * @param context
     * @return
     */
    public static View getSelectedReturnMarker(Context context, Float discount) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_park_choose_home, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_choose_marker);
        TextView tvDiscount = (TextView) view.findViewById(R.id.tvDiscount);
        if (null != discount) {//打折网点
            imageView.setImageResource(R.mipmap.marker_selected_discount);
            //折扣
            tvDiscount.setText(TVUtils.toString1(discount / 10));
            tvDiscount.setVisibility(View.VISIBLE);
        } else {
            imageView.setImageResource(R.mipmap.marker_selected);
        }
        return view;
    }

    /**
     * 还车网点，marker的未选中的正常状态
     *
     * @param context
     * @return
     */
    public static View getUnselectedReturnMarker(Context context, Float discount) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.layout_park_choose_home, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_choose_marker);
        TextView tvDiscount = (TextView) view.findViewById(R.id.tvDiscount);
        if (null != discount) {//打折网点
            imageView.setImageResource(R.mipmap.marker_unselected_discount);
            //折扣
            tvDiscount.setText(TVUtils.toString1(discount / 10));
            tvDiscount.setVisibility(View.VISIBLE);
        } else {
            imageView.setImageResource(R.mipmap.marker_has_space);
        }
        return view;
    }

    /**
     * 设置停车场的聚合点marker
     *
     * @param mContext
     * @param cluster
     * @return
     */
    public static View setClusterMarker(Context mContext, Cluster cluster) {
        int clusterLevel = cluster.getClusterLevel();//聚合的等级
        int carNum = cluster.getFreeCarNum();//聚合点中所有的车的数目
        String cityName = cluster.getCityName();//城市名称
        List<ParksResp> parksResps = cluster.getClusterItems();

        View view;
        if (clusterLevel != ClusterOverlay.CITY_CLUSTER) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.layout_park_cluster, null);
        } else {//显示城市名称
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.layout_park_cluster_city, null);
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.ivClusterMarker);
        TextView textView = (TextView) view.findViewById(R.id.tvCarNum);

        int resId = R.mipmap.sy_7;//默认
        if (clusterLevel != ClusterOverlay.CITY_CLUSTER) {
            if (clusterLevel == ClusterOverlay.PARK_CLUSTER) {//没有聚合
                resId = R.mipmap.yc_3;
                if (parksResps != null && parksResps.size() == 1) {
                    ParksResp parksResp = parksResps.get(0);
                    if (parksResp.isSelected()) {
//                        resId = R.mipmap.marker_selected_discount;//选中背景
                    } else {
                        if (carNum > 0) {
                            if (parksResp.getRedPacketCarNum() > 0) {
                                resId = R.mipmap.marker_red_packet;//红包车
                                textView.setVisibility(View.GONE);
                            }
                        } else {//没车
                            resId = R.mipmap.yc_6;
                        }
                    }
                }
                textView.setPadding(0,0,0,mContext.getResources().getDimensionPixelOffset(R.dimen.dimen_8dp));
                textView.setText(String.valueOf(carNum));
            } else if (clusterLevel == ClusterOverlay.AREA_CLUSTER) {//区
                resId = R.mipmap.sy_7;
                textView.setText(cityName);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            }
        } else {//显示城市名称
            resId = R.mipmap.sy_7;
            textView.setText(cityName);
        }

        imageView.setImageResource(resId);
        return view;
    }

    /**
     * 获取有无车辆的marker
     *
     * @param category
     * @param carNumber
     * @param isBlue
     * @return
     */
    public static int getCarMarker(int category, int carNumber, boolean isBlue) {
        if (isBlue) {
            if (carNumber > 0) {
//                if (category == ParkType.ParkType_1s) {
//                    return R.mipmap.branches_1;
//                } else if (category == ParkType.ParkType_2s) {
//                    return R.mipmap.branches_2;
//                } else if (category == ParkType.ParkType_3s) {
//                    return R.mipmap.icon_3s;
//                } else if (category == ParkType.ParkType_6s) {
//                    return R.mipmap.branches_6;
//                }
                return R.mipmap.marker_has_space;
            } else {
//                if (category == ParkType.ParkType_1s) {
//                    return R.mipmap.no_branches_1;
//                } else if (category == ParkType.ParkType_2s) {
//                    return R.mipmap.no_branches_2;
//                } else if (category == ParkType.ParkType_3s) {
//                    return R.mipmap.icon_3sg;
//                } else if (category == ParkType.ParkType_6s) {
//                    return R.mipmap.no_branches_6;
//                }
                return R.mipmap.marker_no_space;
            }
        }
        return 0;
    }

    //view 转bitmap
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * @param context
     * @param cr
     * @param Imagepath
     * @return
     */
    public static Bitmap getImageThumbnail(Context context, ContentResolver cr, String Imagepath) {
        ContentResolver testcr = context.getContentResolver();
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID,};
        String whereClause = MediaStore.Images.Media.DATA + " = '" + Imagepath + "'";
        Cursor cursor = testcr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, whereClause,
                null, null);
        int _id = 0;
        String imagePath = "";
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        if (cursor.moveToFirst()) {
            int _idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            int _dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

            do {
                _id = cursor.getInt(_idColumn);
                imagePath = cursor.getString(_dataColumn);
            } while (cursor.moveToNext());
        }
        cursor.close();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, _id, MediaStore.Images.Thumbnails.MINI_KIND,
                options);
        return bitmap;
    }

    public static Bitmap getThumbnail(Context mContext, Uri uri, int size) throws IOException {
        InputStream input = mContext.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.RGB_565;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;
        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
        double ratio = (originalSize > size) ? (originalSize / size) : 1.0;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither = true;//optional
        bitmapOptions.outHeight = (int) (onlyBoundsOptions.outHeight / ratio);
        bitmapOptions.outWidth = (int) (onlyBoundsOptions.outWidth / ratio);
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;//optional
        input = mContext.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio)) + 1;
        if (k == 0) {
            return 1;
        } else {
            return k;
        }
    }
}
