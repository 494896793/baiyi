package me.iwf.photopicker.utils;

import android.Manifest;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by donglua on 2016/10/19.
 */

public class PermissionsConstant {

    public static final int REQUEST_CAMERA = 1;
    public static final int REQUEST_EXTERNAL_READ = 2;
    public static final int REQUEST_EXTERNAL_WRITE = 3;
    public static final int REQUEST_LOCATION = 4;
    public static final int REQUEST_PHONE = 5;
    /**
     * 使用相机权限
     */
    public static final String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /**
     * 写文件权限
     */
    public static final String[] PERMISSIONS_EXTERNAL_WRITE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /**
     * 读取文件权限
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static final String[] PERMISSIONS_EXTERNAL_READ = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    /**
     * 定位权限
     */
    public static final String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    /**
     * 手机号码权限
     */
    public static final String[] PERMISSIONS_PHONE = {
            Manifest.permission.READ_PHONE_STATE
    };
}
