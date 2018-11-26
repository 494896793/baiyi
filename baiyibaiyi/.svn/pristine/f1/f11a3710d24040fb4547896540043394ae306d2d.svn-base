package www.qisu666.com.utils;

/**
 * 717219917@qq.com ${DATA} 14:57.
 */

public class Distance {
    /**
     * 计算两点之间距离 米
     * @param  start_lat  开始的lat点
     * @param start_lon   开始的lon点
     *
     * @param  end_lat  结束的lat点
     * @param  end_lon  结束的lon点
     * @return  单位/米
     */
    public static double getDistance(double start_lat,double start_lon,double end_lat,double end_lon)
    {

        double lon1 = (Math.PI / 180) * start_lon;
        double lat1 = (Math.PI / 180) * start_lat;

        double lon2 = (Math.PI / 180) * end_lon;
        double lat2 = (Math.PI / 180) * end_lat;

        // double Lat1r = (Math.PI/180)*(gp1.getLatitudeE6()/1E6);
        // double Lat2r = (Math.PI/180)*(gp2.getLatitudeE6()/1E6);
        // double Lon1r = (Math.PI/180)*(gp1.getLongitudeE6()/1E6);
        // double Lon2r = (Math.PI/180)*(gp2.getLongitudeE6()/1E6);

        // 地球半径
        double R = 6371;

        // 两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;

        return d ;//这是千米
//        return d * 1000;//这是米
    }

}
