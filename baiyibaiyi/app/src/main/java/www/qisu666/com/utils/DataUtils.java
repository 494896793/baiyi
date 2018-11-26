package www.qisu666.com.utils;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import www.qisu666.com.interfaces.PermissionListener;

import java.util.List;

import static com.tencent.open.utils.Global.getPackageName;

public class DataUtils {

    public static boolean permissLoca(Activity c) {
        int checkPermission = ContextCompat.checkSelfPermission(c,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(c, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return false;
        } else {
            return true;
        }
    }

    public static boolean permissStorage(Activity c) {
        int checkPermission = ContextCompat.checkSelfPermission(c,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(c, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        } else {
            return true;
        }
    }

    public static boolean permissCamera(Activity c) {
        int checkPermission = ContextCompat.checkSelfPermission(c,
                Manifest.permission.CAMERA);
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(c, new String[]{Manifest.permission.CAMERA}, 1);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取通许录权限
     */

    public static boolean permissContacts(Activity c) {
        int checkPermission = ContextCompat.checkSelfPermission(c,
                Manifest.permission.READ_CONTACTS);
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(c, new String[]{Manifest.permission.READ_CONTACTS}, 1);
            return false;
        } else {
            return true;
        }
    }

    private static void jumpPermiss(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        context.startActivity(localIntent);
    }

    /**
     * 判断摄像头是否可用
     */
    public static boolean isCameraCanUse() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            // setParameters 是针对魅族MX5 做的。MX5 通过Camera.open() 拿到的Camera
            // 对象不为null
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            canUse = false;
        }
        if (mCamera != null) {
            mCamera.release();
        }
        return canUse;
    }

    /**
     * 校验某个权限是否还存在
     */
    public static boolean requestPermissions(final Activity activity, String permisSions, final int type) {
        if (PermissionsUtil.hasPermission(activity, permisSions)) {
            //有访问摄像头的权限
            return true;
        } else {
            PermissionsUtil.requestPermission(activity, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
                    //用户授予了访问摄像头的权限
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
                    //用户拒绝了访问摄像头的申请
                    if (type == 1) {
                        ToastUtil.show(activity, "请打开通话权限");
                    } else if (type == 2) {
                        ToastUtil.show(activity, "请打开摄像头权限");
                    } else if (type == 3) {
                        ToastUtil.show(activity, "请打开读取相册权限");
                    }
                }
            }, new String[]{permisSions});
            return false;
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static void verifyStoragePermiss(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    public static double toDouble(String str) {
        double dou = 0f;
        try {
            dou = Double.parseDouble(str);
        } catch (NumberFormatException e) {

        }
        return dou;
    }

    public static int toInt(String str) {
        int result = 0;
        try {
            result = Integer.parseInt(str);
        } catch (NumberFormatException e) {
        }
        return result;
    }

    public static Long toLong(String str) {
        Long result = 0l;
        try {
            result = Long.parseLong(str);
        } catch (NumberFormatException e) {
        }
        return result;
    }

    /**
     * 判断服务是否正在运行
     *
     * @param
     * @param className 判断的服务名字：包名+类名
     * @return true在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static int getWidth(Context mContext) {
        return ((WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
    }

    public static String getLpn(String lpn) {
        if (!TextUtils.isEmpty(lpn) && lpn.length() > 2) {
            return lpn.substring(0, 2) + "·" + lpn.substring(2, lpn.length());
        }
        return "";
    }

    /**
     * 适配机型
     *
     * @param context
     * @return
     */
    public static float phoneSize(Context context) {
        int screenWidth = ScreenUtils.getScreenWidth(context);
        float zoomValue = 14.6f;
        if (screenWidth > 1000) {
            zoomValue = 15.2f;
        } else if (screenWidth > 1280) {
            zoomValue = 15.7f;
        }
        return zoomValue;

    }

    /**
     * 隐藏键盘
     *
     * @param
     */
    public static void packupKeyBor(Context mContext) {
        Activity activity = (Activity) mContext;
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * 显示键盘
     *
     * @param
     */
    public static void openKeyBor(Context mContext, View view) {
        InputMethodManager imm = ((InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE));
        if (imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }

    /**
     * 关闭动画
     *
     * @param view
     */
    public static void animateClose(final View view) {
        int origHeight = view.getHeight();
        ValueAnimator animator = createDropAnimator(view, origHeight, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }

        });
        animator.start();
    }

    /**
     * 展开动画
     *
     * @param
     */
    public static void animateOpen(View v, Context context, int hight) {
        if (hight <= 0) {
            return;
        }
        v.setVisibility(View.VISIBLE);
        ValueAnimator animator = createDropAnimator(v, 0,
                (int) (context.getResources().getDisplayMetrics().density * hight + 0.5));
        animator.start();
    }

    public static ValueAnimator createDropAnimator(final View v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                int value = (int) arg0.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    /**
     * 向上弹出
     *
     * @param mContext
     */
    public static Animation animatorUp(Context mContext) {
        TranslateAnimation tanimation = new TranslateAnimation
                (0, 0, ScreenUtils.getScreenHeight(mContext), 0);
        tanimation.setDuration(600);
        return tanimation;
    }

    /**
     * 渐现
     *
     * @param mContext
     */
    public static ObjectAnimator animatorAlpha(Context mContext, RelativeLayout rl) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(rl, "alpha", 0f, 1f);
        animator.setDuration(300);
        animator.start();
        return animator;
    }

    private static double PI = 3.14159265;
    private static double EARTH_RADIUS = 6378137;
    private static double RAD = Math.PI / 180.0;
    /**
     * 根据提供的经度和纬度、以及半径，取得此半径内的最大最小经纬度
     */
    public static double[] getAround(double lat, double lon, double radius) {

        Double latitude = lat;
        Double longitude = lon;

        Double degree = (24901 * 1609) / 360.0;

        Double dpmLat = 1 / degree;
        Double radiusLat = dpmLat * radius;
        Double minLat = latitude - radiusLat;
        Double maxLat = latitude + radiusLat;

        Double mpdLng = degree * Math.cos(latitude * (PI / 180));
        Double dpmLng = 1 / mpdLng;
        Double radiusLng = dpmLng * radius;
        Double minLng = longitude - radiusLng;
        Double maxLng = longitude + radiusLng;
        return new double[]{minLat, minLng, maxLat, maxLng};
    }
}