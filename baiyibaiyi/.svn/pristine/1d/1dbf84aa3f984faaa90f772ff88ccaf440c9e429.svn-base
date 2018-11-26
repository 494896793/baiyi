package www.qisu666.com.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import www.qisu666.com.app.MyApplication;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.Logger;
import com.igexin.sdk.PushManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class IPushService extends Service {
    private MyApplication application;
    private PushManager push;
    private boolean isFlag = true;
    public static CIdHaveObserver cidObserver;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = (MyApplication) getApplicationContext();
        push = PushManager.getInstance();
        push.initialize(application);
        application.cid = push.getClientid(this);
        if (application.cid != null && TextUtils.isEmpty(CacheUtils.getIn().getStr(CacheUtils.cid_key))) {
            CacheUtils.getIn().saveStr(CacheUtils.cid_key, application.cid);
        } else if (application.cid == null && !TextUtils.isEmpty(CacheUtils.getIn().getStr(CacheUtils.cid_key))) {
            application.cid = CacheUtils.getIn().getStr(CacheUtils.cid_key);
        }
        getCid();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);

    }


    private void getCid() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isFlag) {
                    if (application.isNetworkAvailable()) {
                        application.cid = push.getClientid(getApplicationContext());
                        if (application.cid != null) {
                            if (cidObserver != null) {
                                cidObserver.afterCid();
                            }
                            isFlag = false;
                        }
                    } else {
                        isFlag = false;
                    }
                }
                Logger.e("--------------------------之后------------------------cid=" + application.cid);
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        push.stopService(application);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    if (TextUtils.isEmpty(application.cid)) {
                        getCid();
                    }
                    getNetIp();

                } else {
                    Log.e("mark", "没有可用网络");
                }
            }
        }
    };

    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * 获取当前ip地址Me
     *
     * @param context
     * @return
     */
    public static String getLocalIpAddress(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            return int2ip(i);
        } catch (Exception ex) {
            return " 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();
        }
    }

    /**
     * @return public ip
     */
    private void getNetIp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL infoUrl = null;
                InputStream inStream = null;
                try {
                    infoUrl = new URL("http://www.cmyip.com");
                    URLConnection connection = infoUrl.openConnection();
                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
                    int responseCode = httpConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        inStream = httpConnection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
                        StringBuilder strber = new StringBuilder();
                        String line = null;
                        while ((line = reader.readLine()) != null)
                            strber.append(line);
                        inStream.close();
//                        application.ip = strber.toString();

                        int start = strber.indexOf("My IP Address is ") + "My IP Address is ".length(); // Log.d(zph, + start); //
                        int end = start + 16;
                        line = strber.substring(start, end);
                        line = line.replaceAll(" ", "");
                        line = line.replaceAll("<", "");
                        line = line.replaceAll("a", "");
                        line = line.replaceAll("c", "");
                        line = line.replaceAll("l", "");
                        line = line.replaceAll("s", "");
                        line = line.replaceAll("=", "");
                        line = line.replaceAll("i", "");
                        application.ip = line;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (application.ip == null) {
                    application.ip = getLocalIpAddress(application);
                }
                Logger.e("本手机iP地址:" + application.ip);
            }
        }).start();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

}
