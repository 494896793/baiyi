package www.qisu666.com.utils;

import android.util.Log;

/**
 * 自定义日志工具类
 * 
 * @author Zed
 *
 */
public class LogUtils {
	
	public static final String tag = "LogUtils";

	public static final int VERBOSE = 1;
	
	public static final int DEBUG = 2;
	
	public static final int INFO = 3;
	
	public static final int WARN = 4;
	
	public static final int ERROR = 5;
	
	public static final int NOTHING = 6;
	
	public static final int LEVEL = VERBOSE;//通过设置这里，可以开启打印日志的级别
	

	public static void v(String msg) {
		if (LEVEL <= VERBOSE) {
			Log.v(tag, msg);
		}
	}

	public static void d(String msg) {
		if (LEVEL <= DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void i(String msg) {
		if (LEVEL <= INFO) {
			Log.i(tag, msg);
		}
	}

	public static void w(String msg) {
		if (LEVEL <= WARN) {
			Log.w(tag, msg);
		}
	}

	public static void e(String msg) {
		if (LEVEL <= ERROR) {
			Log.e(tag, msg);
		}
	}

}
