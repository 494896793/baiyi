package www.qisu666.com.utils;

import java.text.DecimalFormat;

/**
 * 获取两点之间GPS距离的方法，结果单位为千米
 *
 * @author Administrator
 */
public class InstanceUtils {
    private static double EARTH_RADIUS = 6378.137;

    private static DecimalFormat decimalFormat = new DecimalFormat("##0.00");
    private static DecimalFormat decimalFormatSingle = new DecimalFormat(".0");

    public static String instance(String longitude, String latitude) {
        try {
            double lat1 = DataUtils.toDouble(latitude);
            double lng1 = DataUtils.toDouble(longitude);
            double lat2 = DataUtils.toDouble(UserUtils.getLatitude());
            double lng2 = DataUtils.toDouble(UserUtils.getLongitude());
            double dis = GetDistance(lat1, lng1, lat2, lng2);
            return getNumberEng(dis);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 传入的经纬度和用户所在位置的经纬度的距离
     * @param longitude
     * @param latitude
     * @return
     */
    public static Double insDouble(String longitude, String latitude) {
        try {
            Double lat1 = DataUtils.toDouble(latitude);
            Double lng1 = DataUtils.toDouble(longitude);
            Double lat2 = DataUtils.toDouble(UserUtils.getLatitude());
            Double lng2 = DataUtils.toDouble(UserUtils.getLongitude());
            return GetDistance(lat1, lng1, lat2, lng2);
        } catch (Exception e) {
            return 0d;
        }
    }

    /**
     * 传入的两个经纬度之间的距离
     * @param longitude
     * @param latitude
     * @param longitude1
     * @param latitude2
     * @return
     */
    public static Double insDoubleBc(String longitude, String latitude, double longitude1, double latitude2) {
        try {
            Double lat1 = DataUtils.toDouble(latitude);
            Double lng1 = DataUtils.toDouble(longitude);
            Double lat2 = latitude2;
            Double lng2 = longitude1;
            return GetDistance(lat1, lng1, lat2, lng2);
        } catch (Exception e) {
            return 0d;
        }
    }

    public static String instanceEng(String longitude, String latitude,
                                     String longitude1, String latitude2) {
        try {
            double lat1 = Double.valueOf(latitude);
            double lng1 = Double.valueOf(longitude);
            double lat2 = Double.valueOf(latitude2);
            double lng2 = Double.valueOf(longitude1);

            double dis = GetDistance(lat1, lng1, lat2, lng2);
            return getNumberEng(dis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String instanceEngInt(String longitude, String latitude,
                                        String longitude2, String latitude2) {
        try {
            double lat1 = Double.valueOf(latitude);
            double lng1 = Double.valueOf(longitude);
            double lat2 = Double.valueOf(latitude2);
            double lng2 = Double.valueOf(longitude2);
            double dis = GetDistance(lat1, lng1, lat2, lng2);
            return getNumberEngfloat(dis);
            //    return getNumberEngInt(dis);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getNumber(double dis) {
        if (dis > 900) {
            dis = dis / 1000d;
            return decimalFormat.format(dis) + "公里";//此处“公里”不可随意更改，代码里面需要用这个参数做校验
        } else {
            return decimalFormat.format(dis) + "米";
        }
    }

    public static String getNumberEng(double dis) {
        if (dis > 900) {
            dis = dis / 1000d;
            return decimalFormat.format(dis) + "km";//此处“公里”不可随意更改，代码里面需要用这个参数做校验
        } else {
            return decimalFormat.format(dis) + "m";
        }
    }

    public static String getNumberEngInt(double dis) {
        if (dis > 900) {
            dis = dis / 1000d;
            return (int) dis + "km";//此处“公里”不可随意更改，代码里面需要用这个参数做校验
        } else {
            return (int) dis + "m";
        }
    }

    public static String getNumberEngfloat(double dis) {
        if (dis >= 1000) {
            dis = dis / 1000d;
            return decimalFormatSingle.format(dis) + "km";//此处“公里”不可随意更改，代码里面需要用这个参数做校验
        } else {
            return (int) dis + "m";
        }
    }

    public static String instanceEN(String longitude, String latitude,
                                    String longitude1, String latitude2) {
        try {
            double lat1 = Double.valueOf(latitude);
            double lng1 = Double.valueOf(longitude);
            double lat2 = Double.valueOf(latitude2);
            double lng2 = Double.valueOf(longitude1);

            double dis = GetDistance(lat1, lng1, lat2, lng2);
            return getNumberEN(dis);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getNumberEN(double dis) {
        if (dis > 900) {
            dis = dis / 1000d;
            return decimalFormat.format(dis) + "KM";//此处“公里”不可随意更改，代码里面需要用这个参数做校验
        } else {
            return decimalFormat.format(dis) + "M";
        }
    }

    public static String getMin(double dis) {
        if (dis > 900) {
            dis = dis / 1000d;
            return decimalFormat.format(dis) + "分钟";//此处“公里”不可随意更改，代码里面需要用这个参数做校验
        } else {
            return "1分钟";
        }
    }

    public static double GetDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        return s * 1000;
    }

    public static double instanceStr(String longitude, String latitude,
                                     String longitude1, String latitude2) {
        try {
            double lat1 = Double.valueOf(latitude);
            double lng1 = Double.valueOf(longitude);
            double lat2 = Double.valueOf(latitude2);
            double lng2 = Double.valueOf(longitude1);
            double dis = GetDistance(lat1, lng1, lat2, lng2);
            return dis;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
}
