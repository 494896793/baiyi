package www.qisu666.com.utils;

/**
 * 717219917@qq.com 2018/4/16 19:38.
 */

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class L {

    private L() {
        throw new UnsupportedOperationException("对象不能被初始化");
    }

    private static final String TAG = "";
    public static boolean isDebug = true;
    public static boolean isWrite = true;
    public static final String DEFAULT_DIR = "andlp";
    private static String APP_DIR = DEFAULT_DIR;
    public static final String LOG_DIR = "lp";
    private  static int loglevel = Log.VERBOSE;
    public static void i(String msg) {
        if (isDebug) {

            loglevel= Log.INFO;

            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);

            log(tag,msg,tag);
        }

    }

    public static void d(String msg) {
        if (isDebug) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            loglevel= Log.DEBUG;
            log(tag,msg,tag);
        }

    }

    public static void e(String msg) {
        if (isDebug) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);
            loglevel= Log.ERROR;
            log(tag,msg,tag);
        }

    }

    public static void v(String msg) {
        if (isDebug) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);

            loglevel= Log.VERBOSE;
            log(tag,msg,tag);
        }

    }

    public static void w(String msg) {
        if (isDebug) {
            StackTraceElement caller = getCallerStackTraceElement();
            String tag = generateTag(caller);

            loglevel= Log.WARN;
            log(tag,msg,tag);
        }

    }


    public static void i(String tag, String msg) {
        if (isDebug) {

            loglevel= Log.INFO;

            StackTraceElement caller = getCallerStackTraceElement();
            String stack = generateTag(caller);

            log(tag,msg,stack);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            loglevel= Log.DEBUG;

            StackTraceElement caller = getCallerStackTraceElement();
            String stack = generateTag(caller);
            log(tag,msg,stack);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            loglevel= Log.ERROR;

            StackTraceElement caller = getCallerStackTraceElement();
            String stack = generateTag(caller);
            log(tag,msg,stack);

        }
    }

    public static void v(String tag, String msg) {
        if (isDebug) {
            loglevel= Log.VERBOSE;

            StackTraceElement caller = getCallerStackTraceElement();
            String stack = generateTag(caller);

            log(tag,msg,stack);
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            loglevel= Log.WARN;

            StackTraceElement caller = getCallerStackTraceElement();
            String stack = generateTag(caller);
            log(tag,msg,stack);
        }
    }

    private static String generateTag(StackTraceElement caller) {
        String tag = "%s.%s():%d";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName
                .lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(),
                caller.getLineNumber());
        return tag;
    }

    public static void setAppDir(String appDir) {
        APP_DIR = appDir;
    }
    public static String getAppDir() {
        return APP_DIR;
    }
    public static String getOrCreateChildDir(String dir) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File f = new File(Environment.getExternalStorageDirectory()
                    + String.format("/%s/%s/", APP_DIR, dir));
            if (f.mkdirs() || f.isDirectory()) {
                return f.getPath();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static void io(String msg) {
        if (isWrite) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd", Locale.CHINA);
            String logName = String.format("%s/lp_%s.txt",
                    getOrCreateChildDir(LOG_DIR), formatter.format(new Date()));

            io(msg, logName);
        }

    }

    public static void io(String msg, String fileName) {

        if (isWrite) {
            StringBuilder sb = new StringBuilder();
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "[yyyy-MM-dd HH:mm:ss]  ", Locale.CHINA);
            Date firstDate = new Date(System.currentTimeMillis());
            String str = formatter.format(firstDate);
            sb.append(str);
            sb.append(msg).append("\n");
            try {
                File files = new File(fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(files,
                        true);
                fileOutputStream.write(sb.toString().getBytes());
                fileOutputStream.close();

            } catch (Exception e) {
            }
        }

    }

    public static void io(Exception e) {
        if (isWrite) {
            StringWriter writer = new StringWriter();
            PrintWriter pw = new PrintWriter(writer, true);
            e.printStackTrace(pw);
            io(writer.toString());
        }

    }

    public static void io(Throwable ex) {
        if (isWrite) {
            StringWriter writer = new StringWriter();
            PrintWriter pw = new PrintWriter(writer, true);
            ex.printStackTrace(pw);
            io(writer.toString());
        }

    }

    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    //log4k
    public static void log(String tag, String str, String stack) {

        if(!tag.equals(stack)){

            if(loglevel== Log.VERBOSE){
                Log.v(tag,"log------------>"+stack);
            }else if(loglevel== Log.INFO){
                Log.i(tag,"log------------>"+stack);
            }  else if(loglevel== Log.DEBUG){
                Log.d(tag,"log------------>"+stack);
            }   else  if(loglevel== Log.ERROR){
                Log.e(tag,"log------------>"+stack);
            }else  if(loglevel== Log.WARN){
                Log.w(tag,"log------------>"+stack);
            }else  {
                Log.i(tag,"log------------>");
            }

        }
        int index = 0;
        int max = 3800; // max  no  4000
        String sub;
        while (index < str.length()) {

            if (str.length() < max) {
                max = str.length();
                sub = str.substring(index, max);
            } else {
                sub = str.substring(index, max);
            }

            if(loglevel== Log.VERBOSE){
                Log.v(tag, sub);
            }else if(loglevel== Log.INFO){
                Log.i(tag, sub);
            }  else if(loglevel== Log.DEBUG){
                Log.d(tag, sub);
            }   else  if(loglevel== Log.ERROR){
                Log.e(tag, sub);
            }else  if(loglevel== Log.WARN){
                Log.w(tag, sub);
            }else  {
                Log.i(tag, sub);
            }
            index = max;
            max += 3800;
        }

    }




}