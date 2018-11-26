package www.qisu666.com.utils;

import java.math.BigDecimal;

/**
 * Created by admin on 2016/7/22.
 */
public class NumberUtils {

    /**
     *
     * @param f 需要格式化的数
     * @param decimal_place 保留位数
     * @return
     */
    public static double getRoundNumber(double f, int decimal_place){
        BigDecimal b   =   new BigDecimal(f);
        double   f1   =   b.setScale(decimal_place,   BigDecimal.ROUND_HALF_UP).doubleValue();
        return f1;
    }

    /**
     *
     * @param f 需要格式化的数
     * @param decimal_place 保留位数
     * @return
     */
    public static String getRoundNumber(String f, int decimal_place){
        BigDecimal b   =   new BigDecimal(f);
        b = b.setScale(decimal_place,   BigDecimal.ROUND_HALF_UP);
        return b.toString();
    }

    /**
     *
     * @param f 需要格式化的数
     * @param decimal_place 保留位数
     * @return
     */
    public static String getRoundStringPercent(String f, int decimal_place){
        BigDecimal b   =   new BigDecimal(f);
        b = new BigDecimal(b.doubleValue() / 100);
        double   f1   =   b.setScale(decimal_place,   BigDecimal.ROUND_HALF_UP).doubleValue();
//        DecimalFormat df = new DecimalFormat("#.00");
        return String.valueOf(f1);
    }

}
