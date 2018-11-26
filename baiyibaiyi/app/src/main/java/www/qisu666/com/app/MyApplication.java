package www.qisu666.com.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.asa.okvolley.LruBitmapCache;
import com.asa.okvolley.OkHttpStack;
import www.qisu666.com.constant.Config;
import www.qisu666.com.interfaces.CrashHandler;
import www.qisu666.com.service.IPushService;
import www.qisu666.com.service.MyLocationService;
import www.qisu666.com.service.TimeCountService;
import www.qisu666.com.service.UpdateService;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.DateUtils;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.SharedPreferencesUtils;
import www.qisu666.com.utils.TimerUtil;
import www.qisu666.com.utils.UserUtils;
import com.igexin.sdk.PushManager;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import org.xutils.DbManager;
import org.xutils.x;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class MyApplication extends MultiDexApplication implements CrashHandler.OnBeforeHandleExceptionListener {
    public static boolean isLoginSuccess = false;
    public String cid;
    public String curLocationSite = "";
    public String latitude = "0.0";
    public String longitude = "0.0";
    public String addressNowCity = "深圳";
    public String cityCodeNow = "440300";
    private List<SoftReference<Activity>> activitys = new ArrayList<SoftReference<Activity>>();
    public String ip = "";
    public final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
    public String oauth2_id = null;
    public String typeLogin = null;
    private static MyApplication application;
    //    private static Context instance;
    private boolean isDownload;
    public int payType = 1;//完成分时租赁订单的页面1，为即时，；2为延时。
    public boolean newContext = false, isNeedForceUpdate = false;
    public static boolean timingStart = true, isWaiting = true;
    private int payFrom;//余额充值、押金充值、用车支付、订单支付
    private String APP_ID = "";
    private boolean isShowAd = true;

    /**
     * Volley image cache
     */
    private LruBitmapCache mLruBitmapCache;
    /**
     * Volley image loader
     */
    private ImageLoader mImageLoader;

    /**
     * xutils的数据库模块
     */
    public DbManager db;

    /**
     * Volley request queue
     */
    private RequestQueue mRequestQueue;

//    public static Context getContext() {
//        return instance;
//    }

    public void exitApp() {
        for (SoftReference<Activity> activity : activitys) {
            Activity temp;
            if ((temp = activity.get()) != null) {
                temp.finish();
            }
        }
        stopService(new Intent(getBaseContext(), MyLocationService.class));
        stopService(new Intent(getBaseContext(), IPushService.class));
        stopService(new Intent(getBaseContext(), UpdateService.class));
        stopService(new Intent(getBaseContext(), TimeCountService.class));

        MobclickAgent.onKillProcess(this);
        Logger.e("退出app了");
        System.exit(0);
    }

    /**
     * 初始化xtuisl的数据库设置
     */
    public void initDbXutil(){
        db= x.getDb(daoConfig);
        Log.i("==","==");
    }

    DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
            //设置数据库名
            .setDbName("qisu.db")
            // 不设置dbDir时, 默认存储在app的私有目录.
            // "sdcard"的写法并非最佳实践, 这里为了简单, 先这样写了.
            // .setDbDir(new File("/sdcard"))
            .setDbVersion(2)
            .setDbOpenListener(new DbManager.DbOpenListener() {
                @Override
                public void onDbOpened(DbManager db) {
                    // 开启WAL, 对写入加速提升巨大
                    db.getDatabase().enableWriteAheadLogging();
                }
            })
            .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                @Override public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                    // TODO: ...
                    // db.addColumn(...);
                    // db.dropTable(...);
                    // ...
                    // or
                    // db.dropDb();
                }
            });

    public static MyApplication getApplication() {
        return application;
    }

    public void addActivates(Activity activity) {
        activitys.add(new SoftReference<>(activity));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.e("application-time onCreate:" + DateUtils.getStringToday());
//        instance = this;
        application = this;
/*        CommandShare.getInstance().setmContext(this);
        CommandShare.getInstance().init();*/
        CrashReport.initCrashReport(getApplicationContext(), "4da7e8de58", Config.DEBUG);
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.setmListener(this);
//        crashHandler.init(getApplicationContext());
        x.Ext.init(this);
        x.Ext.setDebug(Config.DEBUG);
        initDbXutil();
//        startService(new Intent(getBaseContext(), MyLocationService.class));
//        DeviceUUidFactory uuidFactory = new DeviceUUidFactory(getApplicationContext());
//        UUID = uuidFactory.getDeviceUuid().toString();
    }

    public ImageLoader getVolleyImageLoader() {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader
                    (
                            getVolleyRequestQueue(),
                            MyApplication.getApplication().getVolleyImageCache()
                    );
        }

        return mImageLoader;
    }

    /**
     * Returns a bitmap cache to use with volley.
     *
     * @return {@link LruBitmapCache}
     */
    private LruBitmapCache getVolleyImageCache() {
        if (mLruBitmapCache == null) {
            mLruBitmapCache = new LruBitmapCache(getApplication());
        }
        return mLruBitmapCache;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * Adds a request to the Volley request queue with a given tag
     *
     * @param request is the request to be added
     * @param tag     tag identifying the request
     */
    public static void addRequest(@NonNull final Request<?> request, @NonNull final String tag) {
        request.setTag(tag);
        addRequest(request);
    }

    public static void addRequest(@NonNull final Request<?> request) {
        getApplication().getVolleyRequestQueue().add(request);
    }

    /** Returns a Volley request queue for creating network requests
     * @return {@link RequestQueue} */
    public RequestQueue getVolleyRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this, new OkHttpStack(new OkHttpClient.Builder().build()));
        }
        return mRequestQueue;
    }

    public String getCID() {
        return cid;
    }


    @SuppressWarnings("deprecation")
    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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


    public void logOut() {
        if (!TextUtils.isEmpty(UserUtils.getPhone())) {
            PushManager.getInstance().unBindAlias(this, UserUtils.getPhone(), true);
        }
        CacheUtils.getIn().clearMyInfo();
        SharedPreferencesUtils.putString(this, Config.LOGIN_TOKEN, "");
        TimerUtil.getInstance().cancel();
    }

    public void setDownload(boolean isDownload) {
        this.isDownload = isDownload;
    }

    @Override
    public void onBeforeHandleException() {
        Logger.e("捕获异常");
//        if (activitys != null && activitys.size() > 0) {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            Logger.e("捕获异常：移除后此时activitys个数："+activitys.size());
//        } else {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//        }
    }

    public int getPayFrom() {
        return payFrom;
    }

    public void setPayFrom(int payFrom) {
        this.payFrom = payFrom;
    }

    public String getAPP_ID() {
        return APP_ID;
    }

    public void setAPP_ID(String APP_ID) {
        this.APP_ID = APP_ID;
    }

    public boolean isShowAd() {
        return isShowAd;
    }

    public void setShowAd(boolean showAd) {
        isShowAd = showAd;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Logger.e("应用onLowMemory");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Logger.e("应用onTerminate");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Logger.e("应用onTrimMemory level=" + level);
    }
}
