package www.qisu666.com.utils;

import android.util.Log;

import www.qisu666.com.constant.Config;

/**
 * @author wujiancheng
 *         对日志的封装
 */
public class Logger {
    private static final String TAG = Logger.class.getSimpleName();
    private static final String PRE = "-----》》";

    public static void i(String tag, String log) {
        if (Config.DEBUG) {
            Log.i(tag, PRE + log);
        }
    }
    public static void i(String log) {
        if (Config.DEBUG) {
            Log.i(TAG, PRE + log);
        }
    }

    public static void d(String tag, String log) {
        if (Config.DEBUG) {
            Log.d(tag, PRE + log);
        }
    }

    public static void d(String log) {
        if (Config.DEBUG) {
            Log.d(TAG, PRE + log);
        }
    }

    public static void e(String tag, String log) {
        if (Config.DEBUG) {
            Log.e(tag, PRE + log);
        }
    }

    public static void e(String log) {
        if (Config.DEBUG) {
            Log.e(TAG, PRE + log);
        }
    }
}
