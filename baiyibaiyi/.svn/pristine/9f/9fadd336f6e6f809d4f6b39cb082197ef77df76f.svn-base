package www.qisu666.com.utils;

import android.text.TextUtils;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class DateUtils {

    public static Date getTomorrow() {
        String dateShort = dateToStr(new Date());//将当前转化成短字符串格式 yyyy-MM-dd
        Date dateNew = strToDate(dateShort);//将短时间格式字符串转换为时间 yyyy-MM-dd
        return new Date(dateNew.getTime() + 24 * 60 * 60 * 1000l);//第二天的凌晨00：00，
    }

    public static String getDay(String d) {
        try {
            if (!TextUtils.isEmpty(d)) {
                String[] a = d.split("-");
                return a[2];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getWeek() {
        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int week = c.get(Calendar.DAY_OF_WEEK);
        return week - 1;
    }

    public static long delayTime(long dats) {
        return dats * 24 * 60 * 60 * 1000l;
    }

    public static String formalTime(String createTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            JSONObject timeJson = new JSONObject(createTime);
            Long time = timeJson.getLong("time");
            Date d = sdf.parse(StringToDate(time));
            return sdf.format(d);
        } catch (Exception e) {
            return sdf.format(new Date());
        }
    }


    public static String setDefaultTime(boolean isNext) {
        Date currentTime = new Date();
        if (isNext) {
            long curr = currentTime.getTime() + 2 * 24 * 60 * 60 * 1000l;
            currentTime = new Date(curr);
        } else {
            long curr = currentTime.getTime() + 1 * 24 * 60 * 60 * 1000l;
            currentTime = new Date(curr);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(currentTime);
    }

    public static String setCurrDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月");
        return formatter.format(new Date());
    }

    /**
     * 将时间戳转化为--月--日 00:00
     *
     * @param timestamp
     * @return
     */
    public static String getMonthTime(long timestamp) {
        if (timestamp == 0) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日 HH:mm");
        Date date = new Date(timestamp);
        return formatter.format(date);
    }

    public static Long formatDate(String d) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(d).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0l;
    }

    public static Date setDate(int num, String time) {
        Date currentTime = new Date();
        SimpleDateFormat come = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = come.format(currentTime);
        if (num > 0) {
            try {
                Date nowNew = come.parse(dateString);
                long timeLong = nowNew.getTime() + num * 24 * 60 * 60 * 1000;
                Date result = new Date(timeLong);
                dateString = come.format(result);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        dateString += " " + time;
        SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date outDate = null;
        try {
            outDate = out.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outDate;
    }

    public static String getTimeShort(int num) {
        try {
            Date now = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = formatter.format(now);
            Date nowNew = formatter.parse(dateString);
            long time = nowNew.getTime() + num * 24 * 60 * 60 * 1000;
            Date result = new Date(time);
            SimpleDateFormat sResult = new SimpleDateFormat("MM-dd");
            return sResult.format(result);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public static long strToLong(String strDate, String formatType) {
        try {
            Date date = stringToDate(strDate, formatType);
            return dateToLong(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 时间戳转化为formatType具体时间
     *
     * @param timestamp
     * @param formatType
     * @return
     * @throws ParseException
     */
    public static String timestampToString(String timestamp, String formatType)
            throws ParseException {
        long stamp = 0;
        if (!TextUtils.isEmpty(timestamp) && StringUtils.isIntOrFloat(timestamp)) {
            stamp = Long.parseLong(timestamp);
        }
        Date date = new Date(stamp);
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        return formatter.format(date);
    }

    /**
     * 时间戳转化为formatType具体时间
     *
     * @param timestamp
     * @return
     * @throws ParseException
     */
    public static Date timestampToDate(String timestamp)
            throws ParseException {
        long stamp = 0;
        if (!TextUtils.isEmpty(timestamp) && StringUtils.isIntOrFloat(timestamp)) {
            stamp = Long.parseLong(timestamp);
        }
        Date date = new Date(stamp);

        return date;
    }

    // string类型转换为date类型
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = formatter.parse(strTime);
        return date;
    }

    // date类型转换为long类型
    // date要转换的date类型的时间
    public static long dateToLong(Date date) {
        return date.getTime();
    }


    public static String dateToStrL(String dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    public static String minuteToHour(int minute) {
        if (minute < 60) {
            return minute + "分钟";
        } else {
            int hour = minute / 60;
            int min = minute % 60;
            return hour + "小时" + min + "分钟";
        }
    }

    public static String minuteToDay(long minute) {
        if (minute < 60) {
            return minute + "分钟";
        } else if (minute < 60 * 24) {
            long hour = minute / 60;
            long min = minute % 60;
            return hour + "小时" + min + "分钟";
        } else {
            long hourTotal = minute / 60;
            long min = minute % 60;
            long hour = hourTotal % 24;
            long day = hourTotal / 24;

            return day + "天" + hour + "小时" + min + "分钟";
        }
    }

    /**
     * 将date格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
     *
     * @param dateToString
     * @param
     * @return
     */
    /**
     * 获取现在时间
     *
     * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
     */
    public static Date getNowDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        ParsePosition pos = new ParsePosition(0);
        Date currentTime_2 = formatter.parse(dateString, pos);
        return currentTime_2;
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
     *
     * @param dateDate
     * @return
     */
    public static String dateToStrTime(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
     *
     * @param
     * @return
     */
    public static String StrToDate(String str) {
        return new SimpleDateFormat("MM-dd HH:mm").format(DataUtils.toLong(str));
    }

    public static Date ToTime(String str) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(str, pos);
        return strtodate;
    }

    public static List<String> getTimes(String beginTime, String endTime) {
        Date beginDate = ToTime(beginTime);
        Date endDate = ToTime(endTime);
        List<String> lists = new ArrayList<String>();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        lists.add(formatter.format(beginDate));
        if (beginDate != null && endDate != null) {
            long begin = beginDate.getTime();
            long end = endDate.getTime();
            while (begin < end) {
                begin += 30 * 60 * 1000;
                lists.add(formatter.format(new Date(begin)));
            }
        }
        // lists.add(formatter.format(endDate));
        return lists;
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy-MM-dd HH:mm:ss
     *
     * @param
     * @return
     */
    public static String StringToDate(long str) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(str);
        return dateString;
    }

    /**
     * 将毫秒转换为字符串 yyyy-MM-dd HH:mm:ss
     *
     * @param
     * @return
     */
    public static Date longToDate(String str) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(Long.parseLong(str));
        Date date = strToDateLong(dateString);
        return date;
    }


    /**
     * 将短时间格式时间转换为字符串 yyyy-MM-dd
     *
     * @param dateDate
     * @param
     * @return
     */
    public static String dateToStr(java.util.Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式时间转换为字符串 HH:mm
     *
     * @param dateDate
     * @param
     * @return
     */
    public static String dateToTime(java.util.Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        String dateString = formatter.format(dateDate);
        return dateString;
    }


    /**
     * 将短时间格式字符串转换为时间 yyyy-MM-dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public static Date strToDate2(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 得到现在时间
     *
     * @return
     */
    public static Date getNow() {
        Date currentTime = new Date();
        return currentTime;
    }

    /**
     * 提取一个月中的最后一天
     *
     * @param day
     * @return
     */
    public static Date getLastDate(long day) {
        Date date = new Date();
        long date_3_hm = date.getTime() - 3600000 * 34 * day;
        Date date_3_hm_date = new Date(date_3_hm);
        return date_3_hm_date;
    }

    /**
     * 得到现在时间
     *
     * @return 字符串 yyyyMMdd HHmmss
     */
    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 得到现在小时
     */
    public static String getHour() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String hour;
        hour = dateString.substring(11, 13);
        return hour;
    }

    /**
     * 得到现在分钟
     *
     * @return
     */
    public static String getTime() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String min;
        min = dateString.substring(14, 16);
        return min;
    }

    /**
     * 根据用户传入的时间表示格式，返回当前时间的格式 如果是yyyyMMdd，注意字母y不能大写。
     *
     * @param sformat yyyyMMddhhmmss
     * @return
     */
    public static String getUserDate(String sformat) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(sformat);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 根据用户传入的时间表示格式，返回当前时间的格式 如果是yyyyMMdd，注意字母y不能大写。
     *
     * @param sformat yyyyMMddhhmmss
     * @return
     */
    public static String getrDate(String sformat) {
        Date currentTime = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = format.format(sformat);
        return dateString;
    }

    /**
     * 二个小时时间间的差值,必须保证二个时间都是"HH:MM"的格式，返回字符型的分钟
     */
    public static String getTwoHour(String st1, String st2) {
        String[] kk = null;
        String[] jj = null;
        kk = st1.split(":");
        jj = st2.split(":");
        if (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0]))
            return "0";
        else {
            double y = Double.parseDouble(kk[0]) + Double.parseDouble(kk[1]) / 60;
            double u = Double.parseDouble(jj[0]) + Double.parseDouble(jj[1]) / 60;
            if ((y - u) > 0)
                return y - u + "";
            else
                return "0";
        }
    }

    /**
     * 二个小时时间间的差值,必须保证二个时间都是"HH:MM:ss"的格式，返回字符型的分钟
     */
    public static String getTwoSecond(String st1, String st2) {
        String[] kk = null;
        String[] jj = null;
        kk = st1.split(":");
        jj = st2.split(":");
        if (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0]))
            return "0";
        else {
            double y = Double.parseDouble(kk[0]) + Double.parseDouble(kk[1]) / 60;
            double u = Double.parseDouble(jj[0]) + Double.parseDouble(jj[1]) / 60;
            if ((y - u) > 0)
                return y - u + "";
            else
                return "0";
        }
    }

    /**
     * 得到二个日期间的间隔天数
     */
    public static String getTwoDay(String sj1, String sj2) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        long day = 0;
        try {
            java.util.Date date = myFormatter.parse(sj1);
            java.util.Date mydate = myFormatter.parse(sj2);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return "";
        }
        return day + "";
    }

    /**
     * 根据秒数得到分秒值
     *
     * @param seconds
     * @return
     */
    public static String time(int seconds) {
        String timeShow = "";
        if (seconds > 59) {
            if (seconds % 60 == 0) {
                timeShow = seconds / 60 + "分";
            } else {
                timeShow = seconds / 60 + "分" + seconds % 60 + "秒";
            }
        } else {
            timeShow = seconds + "秒";
        }
        return timeShow;
    }

    /**
     * 显示分秒
     *
     * @param seconds 总秒数
     * @return
     */
    public static String time2MinuteSecond(int seconds) {
        String timeShow = "";
        if (seconds > 59) {
            if (seconds % 60 == 0) {
                int min = seconds / 60;
                if (min < 10) {
                    timeShow = "0" + min + ":00";
                } else {
                    timeShow = min + ":00";
                }
            } else {
                int min = seconds / 60;
                String minS;
                if (min < 10) {
                    minS = "0" + min;
                } else {
                    minS = min + "";
                }

                int sec = seconds % 60;
                String secS;
                if (sec < 10) {
                    secS = "0" + sec;
                } else {
                    secS = sec + "";
                }

                timeShow = minS + ":" + secS;
            }
        } else {
            if (seconds < 10) {
                timeShow = "00:0" + seconds;
            } else {
                timeShow = "00:" + seconds;
            }

        }
        return timeShow;
    }

    /**
     * 显示时分
     *
     * @param minute 总分数
     * @return
     */
    public static String time2HourMinute(int minute) {
        String timeShow = "";
        if (minute > 59) {
            if (minute % 60 == 0) {
                int h = minute / 60;
                if (h < 10) {
                    timeShow = "0" + h + ":00";
                } else {
                    timeShow = h + ":00";
                }
            } else {
                int h = minute / 60;
                String hS;
                if (h < 10) {
                    hS = "0" + h;
                } else {
                    hS = h + "";
                }

                int min = minute % 60;
                String minS;
                if (min < 10) {
                    minS = "0" + min;
                } else {
                    minS = min + "";
                }

                timeShow = hS + ":" + minS;
            }
        } else {
            if (minute < 10) {
                timeShow = "00:0" + minute;
            } else {
                timeShow = "00:" + minute;
            }

        }
        return timeShow;
    }

    /**
     * 显示时分秒
     *
     * @param seconds 总秒数
     * @return
     */
    public static String time2HourMinuteSecond(int seconds) {
        String hourStr, minuteStr, secondStr;
        if (seconds >= 60 * 60) {//大于等于1小时
            int hour = seconds / (60 * 60);
            hourStr = hour > 9 ? hour + "" : "0" + hour;
        } else {//小于1小时
            hourStr = "00";
        }
        seconds = seconds % (60 * 60);

        if (seconds >= 60) {//大于等于1分钟
            int minute = seconds / 60;
            minuteStr = minute > 9 ? minute + "" : "0" + minute;
        } else {//小于1分钟
            minuteStr = "00";
        }

        seconds = seconds % 60;
        secondStr = seconds > 9 ? seconds + "" : "0" + seconds;

        return hourStr + ":" + minuteStr + ":" + secondStr;
    }

    /**
     * 根据秒数得到分秒值
     *
     * @param seconds
     * @return
     */
    public static String timeColon(int seconds) {
        String timeShow = "";
        if (seconds > 59) {
            if (seconds % 60 == 0) {
                timeShow = seconds / 60 + "分";
            } else {
                int yu = seconds % 60;
                String secStr = yu + "";
                if (yu < 10) {
                    secStr = "0" + yu;
                }
                timeShow = seconds / 60 + "分" + secStr + "秒";
            }
        } else {
            timeShow = seconds + "秒";
        }
        return timeShow;
    }

    /**
     * 根据秒数得到时分秒值
     *
     * @param seconds
     * @return
     */
    public static String timeUserCarColon(int seconds) {
        String timeShow = "";
        if (seconds > 59 * 60) {
            int minu = seconds % (60 * 60);
            if (minu == 0) {
                timeShow = seconds / (60 * 60) + "时";
            } else {
                if (minu > 59) {
                    String timeStr = minutes(minu);
                    timeShow = seconds / (60 * 60) + "时" + timeStr;
                } else {
                    timeShow = seconds / (60 * 60) + "时0分" + minu + "秒";
                }
            }
        } else if (seconds > 59) {
            timeShow = minutes(seconds);
        } else {
            timeShow = seconds + "秒";
        }
        return timeShow;
    }

    private static String minutes(int seconds) {
        if (seconds % 60 == 0) {
            return seconds / 60 + "分";
        }
        int yu = seconds % 60;
        String secStr = yu + "";
        if (yu < 10) {
            secStr = "0" + yu;
        }
        return seconds / 60 + "分" + secStr + "秒";
    }

    /**
     * 根据秒数得到分秒值
     *
     * @param seconds
     * @return
     */
    public static String timeColonStr(int seconds) {
        String timeShow = "";
        if (seconds > 59) {
            if (seconds % 60 == 0) {
                timeShow = seconds / 60 + ":00";
            } else {
                int yu = seconds % 60;
                String secStr = yu + "";
                if (yu < 10) {
                    secStr = "0" + yu;
                }
                timeShow = seconds / 60 + ":" + secStr + "";
            }
        } else {
            timeShow = seconds + "";
        }
        return timeShow;
    }

    /**
     * 得到二个日期间的间隔天数
     */
    public static String getTwoDay(Date date, Date mydate) {
        long day = 0;
        try {
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return "";
        }
        return day + "";
    }

    /**
     * 得到二个日期间的间隔天数
     */
    public static long getTwoDayL(Date date, Date mydate) {
        long day = 0l;
        try {
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return 0l;
        }
        return day;
    }

    /**
     * 得到二个日期间的间隔秒数
     */
    public static int getTwoSecondL(Date date, Date mydate) {
        int second = 0;
        try {
            second = (int) ((date.getTime() - mydate.getTime()) / (1000));
        } catch (Exception e) {
            return 0;
        }
        return second;
    }

    /**
     * 时间前推或后推分钟,其中JJ表示分钟.
     */
    public static String getPreTime(String sj1, String jj) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String mydate1 = "";
        try {
            Date date1 = format.parse(sj1);
            long Time = (date1.getTime() / 1000) + Integer.parseInt(jj) * 60;
            date1.setTime(Time * 1000);
            mydate1 = format.format(date1);
        } catch (Exception e) {
        }
        return mydate1;
    }

    /**
     * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
     */
    public static String getNextDay(String nowdate, String delay) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String mdate = "";
            Date d = strToDate(nowdate);
            long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24 * 60 * 60;
            d.setTime(myTime * 1000);
            mdate = format.format(d);
            return mdate;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 判断是否润年
     *
     * @param ddate
     * @return
     */
    public static boolean isLeapYear(String ddate) {

        /**
         * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
         * 3.能被4整除同时能被100整除则不是闰年
         */
        Date d = strToDate(ddate);
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(d);
        int year = gc.get(Calendar.YEAR);
        if ((year % 400) == 0)
            return true;
        else if ((year % 4) == 0) {
            return (year % 100) != 0;
        } else
            return false;
    }

    /**
     * 返回美国时间格式 26 Apr 2006
     *
     * @param str
     * @return
     */
    public static String getEDate(String str) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(str, pos);
        String j = strtodate.toString();
        String[] k = j.split(" ");
        return k[2] + k[1].toUpperCase() + k[5].substring(2, 4);
    }

    /**
     * 获取一个月的最后一天
     *
     * @param dat
     * @return
     */
    public static String getEndDateOfMonth(String dat) {// yyyy-MM-dd
        String str = dat.substring(0, 8);
        String month = dat.substring(5, 7);
        int mon = Integer.parseInt(month);
        if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) {
            str += "31";
        } else if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
            str += "30";
        } else {
            if (isLeapYear(dat)) {
                str += "29";
            } else {
                str += "28";
            }
        }
        return str;
    }

    /**
     * 得到指定月的天数
     */
    public static int getMonthLastDay(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }


    /**
     * 产生周序列,即得到当前时间所在的年度是第几周
     *
     * @return
     */
    public static String getSeqWeek() {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        String week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
        if (week.length() == 1)
            week = "0" + week;
        String year = Integer.toString(c.get(Calendar.YEAR));
        return year + week;
    }

    /**
     * 根据日期获得星期
     *
     * @param date
     * @return
     */
    public static String getWeekOfDate(Date date) {
        String[] weekDaysName = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        String[] weekDaysCode = {"0", "1", "2", "3", "4", "5", "6"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysName[intWeek];
    }

    /**
     * 获得周一的日期
     *
     * @param date
     * @return
     */
    public static String getMonday(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return dateFormat.format(calendar.getTime());

    }

    /**
     * 获得周三的日期
     *
     * @param date
     * @return
     */
    public static String getWednesday(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        return dateFormat.format(calendar.getTime());

    }

    /**
     * 获得周五的日期
     *
     * @param date
     * @return
     */
    public static String getFriday(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        return dateFormat.format(calendar.getTime());
    }

    /**
     * 当前日期前几天或者后几天的日期
     *
     * @param n
     * @return
     */
    public static String afterNDay(int n) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, n);
        Date date = calendar.getTime();
        String s = dateFormat.format(date);
        return s;
    }

    /**
     * 获得一个日期所在的周的星期几的日期，如要找出2002年2月3日所在周的星期一是几号
     *
     * @param sdate
     * @param num
     * @return
     */
    public static String getWeek(String sdate, String num) {
        // 再转换为时间
        Date dd = DateUtils.strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(dd);
        if (num.equals("1")) // 返回星期一所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        else if (num.equals("2")) // 返回星期二所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        else if (num.equals("3")) // 返回星期三所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        else if (num.equals("4")) // 返回星期四所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        else if (num.equals("5")) // 返回星期五所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        else if (num.equals("6")) // 返回星期六所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        else if (num.equals("0")) // 返回星期日所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
    }

    /**
     * 根据一个日期，返回是星期几的字符串
     *
     * @param sdate
     * @return
     */
    public static String getWeek(String sdate) {
        // 再转换为时间
        Date date = DateUtils.strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        return new SimpleDateFormat("EEEE").format(c.getTime());
    }


    public static String getWeekStr(String sdate) {
        String str = "";
        str = DateUtils.getWeek(sdate);
        if ("1".equals(str)) {
            str = "星期日";
        } else if ("2".equals(str)) {
            str = "星期一";
        } else if ("3".equals(str)) {
            str = "星期二";
        } else if ("4".equals(str)) {
            str = "星期三";
        } else if ("5".equals(str)) {
            str = "星期四";
        } else if ("6".equals(str)) {
            str = "星期五";
        } else if ("7".equals(str)) {
            str = "星期六";
        }
        return str;
    }

    public static String getWeekStr(Date date) {
        String str = "";
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        str = new SimpleDateFormat("EEEE").format(c.getTime());
        if ("1".equals(str)) {
            str = "星期日";
        } else if ("2".equals(str)) {
            str = "星期一";
        } else if ("3".equals(str)) {
            str = "星期二";
        } else if ("4".equals(str)) {
            str = "星期三";
        } else if ("5".equals(str)) {
            str = "星期四";
        } else if ("6".equals(str)) {
            str = "星期五";
        } else if ("7".equals(str)) {
            str = "星期六";
        }
        return str;
    }

    /**
     * 两个时间之间的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long getDays(String date1, String date2) {
        if (date1 == null || date1.equals(""))
            return 0;
        if (date2 == null || date2.equals(""))
            return 0;
        // 转换为标准时间
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = null;
        java.util.Date mydate = null;
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
        } catch (Exception e) {
        }
        long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        return day;
    }

    /**
     * 两个时间之间的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long getMins(String date1, String date2) {
        if (date1 == null || date1.equals(""))
            return 0;
        if (date2 == null || date2.equals(""))
            return 0;
        // 转换为标准时间
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date = null;
        java.util.Date mydate = null;
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
//            date = strToDateLong(date1);
//            mydate = strToDateLong(date2);
        } catch (Exception e) {
        }
        long day = (date.getTime() - mydate.getTime())
                / (60 * 1000);
        return day;
    }


    /**
     * 形成如下的日历 ， 根据传入的一个时间返回一个结构 星期日 星期一 星期二 星期三 星期四 星期五 星期六 下面是当月的各个时间
     * 此函数返回该日历第一行星期日所在的日期
     *
     * @param sdate
     * @return
     */
//    public static String getNowMonth(String sdate) {
//        // 取该时间所在月的一号
//        sdate = sdate.substring(0, 8) + "01";
//
//        // 得到这个月的1号是星期几
//        Date date = VeDate.strToDate(sdate);
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        int u = c.get(Calendar.DAY_OF_WEEK);
//        String newday = VeDate.getNextDay(sdate, (1 - u) + "");
//        return newday;
//    }

    /**
     * 取得数据库主键 生成格式为yyyymmddhhmmss+k位随机数
     *
     * @param k 表示是取几位随机数，可以自己定
     */

    public static String getNo(int k) {

        return getUserDate("yyyyMMddhhmmss") + getRandom(k);
    }

    /**
     * 返回一个随机数
     *
     * @param i
     * @return
     */
    public static String getRandom(int i) {
        Random jjj = new Random();
        // int suiJiShu = jjj.nextInt(9);
        if (i == 0)
            return "";
        String jj = "";
        for (int k = 0; k < i; k++) {
            jj = jj + jjj.nextInt(9);
        }
        return jj;
    }

    /**
     * @param args
     */
//    public static boolean RightDate(String date) {
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        ;
//        if (date == null)
//            return false;
//        if (date.length() > 10) {
//            sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        } else {
//            sdf = new SimpleDateFormat("yyyy-MM-dd");
//        }
//        try {
//            sdf.parse(date);
//        } catch (ParseException pe) {
//            return false;
//        }
//        return true;
//    }

    /***************************************************************************
     * //nd=1表示返回的值中包含年度 //yf=1表示返回的值中包含月份 //rq=1表示返回的值中包含日期 //format表示返回的格式 1
     * 以年月日中文返回 2 以横线-返回 // 3 以斜线/返回 4 以缩写不带其它符号形式返回 // 5 以点号.返回
     **************************************************************************/
//    public static String getStringDateMonth(String sdate, String nd, String yf, String rq, String format) {
//        Date currentTime = new Date();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        String dateString = formatter.format(currentTime);
//        String s_nd = dateString.substring(0, 4); // 年份
//        String s_yf = dateString.substring(5, 7); // 月份
//        String s_rq = dateString.substring(8, 10); // 日期
//        String sreturn = "";
//        roc.util.MyChar mc = new roc.util.MyChar();
//        if (sdate == null || sdate.equals("") || !mc.Isdate(sdate)) { // 处理空值情况
//            if (nd.equals("1")) {
//                sreturn = s_nd;
//                // 处理间隔符
//                if (format.equals("1"))
//                    sreturn = sreturn + "年";
//                else if (format.equals("2"))
//                    sreturn = sreturn + "-";
//                else if (format.equals("3"))
//                    sreturn = sreturn + "/";
//                else if (format.equals("5"))
//                    sreturn = sreturn + ".";
//            }
//            // 处理月份
//            if (yf.equals("1")) {
//                sreturn = sreturn + s_yf;
//                if (format.equals("1"))
//                    sreturn = sreturn + "月";
//                else if (format.equals("2"))
//                    sreturn = sreturn + "-";
//                else if (format.equals("3"))
//                    sreturn = sreturn + "/";
//                else if (format.equals("5"))
//                    sreturn = sreturn + ".";
//            }
//            // 处理日期
//            if (rq.equals("1")) {
//                sreturn = sreturn + s_rq;
//                if (format.equals("1"))
//                    sreturn = sreturn + "日";
//            }
//        } else {
//            // 不是空值，也是一个合法的日期值，则先将其转换为标准的时间格式
//            sdate = roc.util.RocDate.getOKDate(sdate);
//            s_nd = sdate.substring(0, 4); // 年份
//            s_yf = sdate.substring(5, 7); // 月份
//            s_rq = sdate.substring(8, 10); // 日期
//            if (nd.equals("1")) {
//                sreturn = s_nd;
//                // 处理间隔符
//                if (format.equals("1"))
//                    sreturn = sreturn + "年";
//                else if (format.equals("2"))
//                    sreturn = sreturn + "-";
//                else if (format.equals("3"))
//                    sreturn = sreturn + "/";
//                else if (format.equals("5"))
//                    sreturn = sreturn + ".";
//            }
//            // 处理月份
//            if (yf.equals("1")) {
//                sreturn = sreturn + s_yf;
//                if (format.equals("1"))
//                    sreturn = sreturn + "月";
//                else if (format.equals("2"))
//                    sreturn = sreturn + "-";
//                else if (format.equals("3"))
//                    sreturn = sreturn + "/";
//                else if (format.equals("5"))
//                    sreturn = sreturn + ".";
//            }
//            // 处理日期
//            if (rq.equals("1")) {
//                sreturn = sreturn + s_rq;
//                if (format.equals("1"))
//                    sreturn = sreturn + "日";
//            }
//        }
//        return sreturn;
//    }

//    public static String getNextMonthDay(String sdate, int m) {
//        sdate = getOKDate(sdate);
//        int year = Integer.parseInt(sdate.substring(0, 4));
//        int month = Integer.parseInt(sdate.substring(5, 7));
//        month = month + m;
//        if (month < 0) {
//            month = month + 12;
//            year = year - 1;
//        } else if (month > 12) {
//            month = month - 12;
//            year = year + 1;
//        }
//        String smonth = "";
//        if (month < 10)
//            smonth = "0" + month;
//        else
//            smonth = "" + month;
//        return year + "-" + smonth + "-10";
//    }

//    public static String getOKDate(String sdate) {
//        if (sdate == null || sdate.equals(""))
//            return getStringDateShort();
//
//        if (!VeStr.Isdate(sdate)) {
//            sdate = getStringDateShort();
//        }
//        // 将“/”转换为“-”
//        sdate = VeStr.Replace(sdate, "/", "-");
//        // 如果只有8位长度，则要进行转换
//        if (sdate.length() == 8)
//            sdate = sdate.substring(0, 4) + "-" + sdate.substring(4, 6) + "-" + sdate.substring(6, 8);
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        ParsePosition pos = new ParsePosition(0);
//        Date strtodate = formatter.parse(sdate, pos);
//        String dateString = formatter.format(strtodate);
//        return dateString;
//    }
    public static List<String> weekStr(int week) {
        List<String> list = new ArrayList<>();
        list.clear();
        for (int i = week; i < 37 + week; i++) {
            switch (i % 7) {
                case 0:
                    list.add("周日");
                    break;
                case 1:
                    list.add("周一");
                    break;
                case 2:
                    list.add("周二");
                    break;
                case 3:
                    list.add("周三");
                    break;
                case 4:
                    list.add("周四");
                    break;
                case 5:
                    list.add("周五");
                    break;
                case 6:
                    list.add("周六");
                    break;
                default:
                    break;
            }
        }
        return list;
    }

    public static String weekDay(int week) {
        String weekDay = "周日";
        switch (week % 7) {
            case 0:
                weekDay = "周日";
                break;
            case 1:
                weekDay = "周一";
                break;
            case 2:
                weekDay = "周二";
                break;
            case 3:
                weekDay = "周三";
                break;
            case 4:
                weekDay = "周四";
                break;
            case 5:
                weekDay = "周五";
                break;
            case 6:
                weekDay = "周六";
                break;
            default:
                break;
        }
        return weekDay;
    }

    public static int getWeekDay(String str) {
        if (str.equals("星期一")) {
            return 1;
        } else if (str.equals("星期二")) {
            return 2;
        } else if (str.equals("星期三")) {
            return 3;
        } else if (str.equals("星期四")) {
            return 4;
        } else if (str.equals("星期五")) {
            return 5;
        } else if (str.equals("星期六")) {
            return 6;
        } else {
            return 7;
        }
    }

    /**
     * 获取夜间时间段
     */
    public static String getNightTimeSection(String nightStart, String nightEnd) {
        if (StringUtils.isEmpty(nightStart)) {
            nightStart = "00";
        }
        if (StringUtils.isEmpty(nightEnd)) {
            nightEnd = "00";
        }
        if (StringUtils.isEmpty(nightStart) && StringUtils.isEmpty(nightEnd)) {
            return "";
        }
        return nightStart + ":00-" + nightEnd + ":00";
    }

    /**
     * 获取日间时间段
     */
    public static String getDayTimeSection(String nightStart, String nightEnd) {
        if (StringUtils.isEmpty(nightStart)) {
            nightStart = "00";
        }
        if (StringUtils.isEmpty(nightEnd)) {
            nightEnd = "00";
        }
        if (StringUtils.isEmpty(nightStart) && StringUtils.isEmpty(nightEnd)) {
            return "";
        }
        return nightEnd + ":00-" + nightStart + ":00";
    }

    /**
     * 判断当前时间是否是夜间
     */
    public static boolean isNightTimeSection(String nightStart, String nightEnd) {
        if (TextUtils.isEmpty(nightStart) || TextUtils.isEmpty(nightEnd)) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("HH", Locale.getDefault());
        String now = sdf.format(new Date());
        if (nightStart.compareTo(nightEnd) <= 0) {//不跨天
            return now.compareTo(nightStart) >= 0 && now.compareTo(nightEnd) < 0;
        } else {//跨天
            return now.compareTo(nightStart) >= 0 || now.compareTo(nightEnd) < 0;
        }
    }
}
