package www.qisu666.com.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.PowerManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.ListView;

import www.qisu666.com.app.MyApplication;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by admin on 2016/10/10.
 */
public class NetWorkUtils {
    private static int netType;

    /**
     * 判断网络是否打开
     *
     * @return
     */
    public static boolean isNet() {
        ConnectivityManager manager = (ConnectivityManager) MyApplication.getApplication()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            NetworkInfo info = manager.getNetworkInfo(manager.getActiveNetwork());
            return info != null && info.isConnected();
        } else {
            NetworkInfo mobileInfo = manager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiInfo = manager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return (mobileInfo != null && mobileInfo.isConnected())
                    || (wifiInfo != null && wifiInfo.isConnected());
        }
    }

    /**
     * 判断网络类型
     */
    public static int inNetType() {
        ConnectivityManager manager = (ConnectivityManager) MyApplication.getApplication()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= 23) {
            NetworkInfo info = manager.getNetworkInfo(manager.getActiveNetwork());
            return info.getSubtype();
        } else {
            NetworkInfo mobileInfo = manager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            return mobileInfo.getSubtype();
        }
    }

    /**
     * 判断GPS是否打开
     *
     * @return
     */
    public static boolean initGPS(Context context) {
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        return locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 跳转设置网络
     */
    public static void openNetset(Context context) {
        // 跳转到系统的网络设置界面
        Intent intent = null;
        // 先判断当前系统版本
        if (android.os.Build.VERSION.SDK_INT > 10) {  // 3.0以上
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        } else {
            intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
        }
        context.startActivity(intent);
    }

    /**
     * 判断手机屏幕是否锁定
     *
     * @param c
     * @return
     */
    public static boolean isScreenOn(Context c) {
        PowerManager powermanager;
        powermanager = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
        return powermanager.isScreenOn();
    }


    /**
     * 获取ListView高度
     *
     * @param
     * @return
     */
    public static void isLvHight(Adapter adapter, ListView ls) {
        if (adapter == null || ls == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, ls);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = ls.getLayoutParams();
        params.height = totalHeight + (ls.getDividerHeight() * (adapter.getCount() - 1));
        ls.setLayoutParams(params);
    }

    public static int lsHight(Adapter adapter, ListView ls) {
        if (adapter == null || ls == null) {
            return 0;
        }
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, ls);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = ls.getLayoutParams();
        params.height = totalHeight + (ls.getDividerHeight() * (adapter.getCount() - 1));
        return params.height;
    }

    /**
     * 隐藏软键盘
     *
     * @param
     * @return
     */
    public static void hiddenKeyboard(Context context) {
        Activity activity = (Activity) context;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * 判断是否安装微信客户端
     *
     * @param
     * @return
     */

    public static boolean isWXAppInstalledAndSupported(Context context) {
        IWXAPI msgApi = WXAPIFactory.createWXAPI(context, null);
        msgApi.registerApp(Constants.PARAM_APP_ID);

        boolean sIsWXAppInstalledAndSupported = msgApi.isWXAppInstalled()
                && msgApi.isWXAppSupportAPI();

        return sIsWXAppInstalledAndSupported;
    }

}
