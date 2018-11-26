package www.qisu666.com.utils;

import android.text.TextUtils;

import www.qisu666.com.request.utils.Config;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**  Created by Administrator on 2015/8/7. */
public class StringUtil {

    public static String addImageHost(String add){
        if (TextUtils.isEmpty(add)){
            return Config.IMAGE_HOST;
        }else {
            return Config.IMAGE_HOST + add;
        }
    }

//    public static String getRealString(String ){
//        if (TextUtils.isEmpty(add)){
//            return ConstantUtil.IMAGE_HOST;
//        }else {
//            return ConstantUtil.IMAGE_HOST + add;
//        }
//    }

    public static boolean isMpos(String deviceName){
        boolean b;
        if(deviceName==null||deviceName.equals("")){
            b = false;
        } else {
            Pattern pattern = Pattern.compile("MPOS\\d{8,10}");
            Matcher matcher = pattern.matcher(deviceName);
            b = matcher.matches();
        }
        return b;
    }


    public static String saveTwoAmount(String amount){
        java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#0.00");
        return df.format( Double.parseDouble(amount)/1000);
    }
    /**
     *格式化日期，将yyyyMMdd改为yyyy-MM-dd
     */
    public static String dateForGeneral(String date){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat fat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = "";
        try {
            java.util.Date  timee  = formatter.parse(date);
            dateString = fat.format(timee);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }
    /**
    *格式化时间，将HHmmss改为HH:mm:ss
     */
    public static String timeForGeneral(String time){
        SimpleDateFormat fatTime = new SimpleDateFormat("HHmmss");
        SimpleDateFormat fatTime1 = new SimpleDateFormat("HH:mm:ss");
        String dateString = "";
        try {
            java.util.Date  timee =  fatTime.parse(time);
            dateString = fatTime1.format(timee);
//            LogUtils.d("timee:"+timee+","+"dateString:"+dateString);
//                            LogUtils.d("fat.format(dateString:" + fat.format(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    /**
    *格式化时间，将MMddHHmmss改为MM-dd HH:mm:ss
     */
    public static String transTimeForGeneral(String time){
        SimpleDateFormat fatTime = new SimpleDateFormat("MMddHHmmss");
        SimpleDateFormat fatTime1 = new SimpleDateFormat("MM-dd HH:mm:ss");
        String dateString = "";
        try {
            java.util.Date  timee =  fatTime.parse(time);
            dateString = fatTime1.format(timee);
//            LogUtils.d("timee:"+timee+","+"dateString:"+dateString);
//                            LogUtils.d("fat.format(dateString:" + fat.format(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }
    /**
    *格式化时间，将MMdd改为MM-dd
     */
    public static String transDateForGeneral(String time){
        SimpleDateFormat fatTime = new SimpleDateFormat("MMdd");
        SimpleDateFormat fatTime1 = new SimpleDateFormat("MM-dd");
        String dateString = "";
        try {
            java.util.Date  timee =  fatTime.parse(time);
            dateString = fatTime1.format(timee);
//            LogUtils.d("timee:"+timee+","+"dateString:"+dateString);
//                            LogUtils.d("fat.format(dateString:" + fat.format(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }

    /**
     * 返回true-含有特殊字符
     * */
    public static boolean SpecialFilter(String s){
        boolean b;
        boolean a;
        String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);

        b = m.find();
        a = m.matches();
        LogUtils.i("special "+b);

        return b;

    }
    /**
     * 保留卡号前四，后四位
     */
    public static String cardNoForPrivate(String cardNo){
        String topFour = cardNo.substring(0,6);
        String endFour = cardNo.substring(cardNo.length()-4);
        String privateStr = "*****";
        return topFour+privateStr+endFour;
    }


    /**
     *
     * */
    public static boolean ChineseFilter(String s){

        boolean b;
        boolean a;
        String regEx = "[\u4E00-\u9FA5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);

        b = m.matches();
        a = m.find();
        LogUtils.i("chinese "+a);
        return a;

    }


    public static boolean EnglishAndNumberFilter(String s){

        boolean b;
        boolean a;
        String regEx = "[a-zA-Z0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);

//        boolean b = m.matches();
        a = m.matches();
        b = m.find();
        LogUtils.i(""+b+" "+a);

        return b;

    }

    public static String FormatAmount(String amount)
    {
//        NumberFormat currency = NumberFormat.getCurrencyInstance();
//        return currency.format(new BigDecimal(amount));
//
        NumberFormat currency = NumberFormat.getInstance();
        currency.setMinimumFractionDigits(2);
        amount = amount.replace(",", "");
        return currency.format(new BigDecimal(amount));
    }


    public static boolean NumberFilter(String s){

        boolean b;
        boolean a;
        String regEx = "[0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(s);

//        boolean b = m.matches();
        a = m.matches();
        b = m.find();
        LogUtils.i("number "+b);

        return b;

    }

    /**
     * 转换成保留2位小数的字符串
     * @param object
     * @return
     */
    public static String formatDecimal(Object object){
        return new DecimalFormat("#0.00").format(object);
    }

    /**
     * 将字符串转换成日期
     * @param string
     * @return
     */
    public static String formatDate(String string){
        try {
            StringBuilder builder = new StringBuilder(string);
            builder.insert(4, "-").insert(7, "-").insert(10, " ").insert(13, ":").insert(16, ":");
            return builder.toString();
        }catch (Exception e){
            return "";
        }
    }

    /**
     * 将字符串转换成时间
     * @param string
     * @return
     */
    public static String formatTime(String string){
        StringBuilder builder = new StringBuilder(string);
        StringBuilder result = new StringBuilder();
        result.append(builder.substring(0, builder.length()-4)).append(":").append(builder.substring(builder.length()-4, builder.length()-2))
                .append(":").append(builder.substring(builder.length()-2, builder.length()));
        return result.toString();
    }

    /**
     * 将字符串转换成整点
     * @param string
     * @return
     */
    public static String formatHour(String string){
        StringBuilder builder;
        if(string.length()==3){
            builder = new StringBuilder("0"+string);
        } else {
            builder = new StringBuilder(string);
        }
        StringBuilder result = new StringBuilder();
        result.append(builder.substring(0, 2)).append(":").append(builder.substring(2, 4));
        return result.toString();
    }

    /**
     * 将字符串转换成日期忽略秒
     * @param string
     * @return
     */
    public static String formatDateWithoutSecond(String string){
        try {
            StringBuilder builder = new StringBuilder(string.substring(0, string.length() - 2));
            builder.insert(4, "-").insert(7, "-").insert(10, " ").insert(13, ":");
            return builder.toString();
        }catch (Exception e){
            return "";
        }
    }

    /**
     * 将字符串转换成经纬度
     * @param value
     * @return
     */
    public static String formatLatLng(double value){
        return new DecimalFormat("#0.000000").format(value);
    }

    /**
     * 将字符串转换成经纬度
     * @param string
     * @return
     */
    public static int stringToInt(String string){
        if(string.length()<2) return -1;
        if(string.startsWith("0")){
            return Integer.valueOf(string.substring(1,string.length()));
        }
        return Integer.valueOf(string);
    }

    /**
     * 全角转半角
     * @param input String.
     * @return 半角字符串
     */
    public static String toDBC(String input) {


        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        String returnString = new String(c);

        return returnString;
    }

    /**
     * 半角转全角
     * @param input String.
     * @return 全角字符串.
     */
    public static String toSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] == '\n') {
                continue;
            } else if (c[i] == '\u201C' || c[i] == '\u201D') {
                c[i] = '\uFF02';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

}
