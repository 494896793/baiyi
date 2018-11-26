package me.iwf.photopicker.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by donglua on 2016/10/19.
 */

public class PermissionsUtils {

    /**
     * 申请读取文件权限
     *
     * @param activity activity
     * @return 是否已经授权
     */
    public static boolean checkReadStoragePermission(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }
        int readStoragePermissionState =
                ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);

        boolean readStoragePermissionGranted = readStoragePermissionState == PackageManager.PERMISSION_GRANTED;

        if (!readStoragePermissionGranted) {
            ActivityCompat.requestPermissions(activity,
                    PermissionsConstant.PERMISSIONS_EXTERNAL_READ,
                    PermissionsConstant.REQUEST_EXTERNAL_READ);
        }
        return readStoragePermissionGranted;
    }

    /**
     * 申请写文件权限
     *
     * @param fragment fragment
     * @return 是否已经授权
     */
    public static boolean checkWriteStoragePermission(Fragment fragment) {

        int writeStoragePermissionState =
                ContextCompat.checkSelfPermission(fragment.getContext(), WRITE_EXTERNAL_STORAGE);

        boolean writeStoragePermissionGranted = writeStoragePermissionState == PackageManager.PERMISSION_GRANTED;

        if (!writeStoragePermissionGranted) {
            fragment.requestPermissions(PermissionsConstant.PERMISSIONS_EXTERNAL_WRITE,
                    PermissionsConstant.REQUEST_EXTERNAL_WRITE);
        }
        return writeStoragePermissionGranted;
    }
    /**
     * 申请写文件权限
     *
     * @param activity activity
     * @return 是否已经授权
     */
    public static boolean checkWriteStoragePermission(Activity activity) {

        int writeStoragePermissionState =
                ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        boolean writeStoragePermissionGranted = writeStoragePermissionState == PackageManager.PERMISSION_GRANTED;

        if (!writeStoragePermissionGranted) {
            ActivityCompat.requestPermissions(activity, PermissionsConstant.PERMISSIONS_EXTERNAL_WRITE,
                    PermissionsConstant.REQUEST_EXTERNAL_WRITE);
        }
        return writeStoragePermissionGranted;
    }
    /**
     * 申请开启相机权限
     *
     * @param fragment fragment
     * @return 是否已经授权
     */
    public static boolean checkCameraPermission(Fragment fragment) {
        int cameraPermissionState = ContextCompat.checkSelfPermission(fragment.getContext(), CAMERA);

        boolean cameraPermissionGranted = cameraPermissionState == PackageManager.PERMISSION_GRANTED;

        if (!cameraPermissionGranted) {
            fragment.requestPermissions(PermissionsConstant.PERMISSIONS_CAMERA,
                    PermissionsConstant.REQUEST_CAMERA);
        }
        return cameraPermissionGranted;
    }
    /**
     * 申请开启相机权限
     *
     * @param activity activity
     * @return 是否已经授权
     */
    public static boolean checkCameraPermission(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }
        int cameraPermissionState = ContextCompat.checkSelfPermission(activity, CAMERA);

        boolean cameraPermissionGranted = cameraPermissionState == PackageManager.PERMISSION_GRANTED;

        if (!cameraPermissionGranted) {
            ActivityCompat.requestPermissions(activity, PermissionsConstant.PERMISSIONS_CAMERA,
                    PermissionsConstant.REQUEST_CAMERA);
        }
        return cameraPermissionGranted;
    }

    /**
     * 申请定位权限
     *
     * @param activity activity
     * @return 是否已经授权
     */
    public static boolean checkLocationPermission(Activity activity) {
        int locationPermissionState = ContextCompat.checkSelfPermission(activity, ACCESS_COARSE_LOCATION);
        boolean locationPermissionGranted = locationPermissionState == PackageManager.PERMISSION_GRANTED;
        if (!locationPermissionGranted) {
            ActivityCompat.requestPermissions(activity, PermissionsConstant.PERMISSIONS_LOCATION, PermissionsConstant.REQUEST_LOCATION);
        }
        return locationPermissionGranted;
    }

    /**
     * 申请读取手机号码权限
     *
     * @param activity activity
     * @return 是否已经授权
     */
    public static boolean checkPhonePermission(Activity activity) {
        int phonePermissionState = ContextCompat.checkSelfPermission(activity, READ_PHONE_STATE);
        boolean phonePermissionGranted = phonePermissionState == PackageManager.PERMISSION_GRANTED;
        if (!phonePermissionGranted) {
            ActivityCompat.requestPermissions(activity, PermissionsConstant.PERMISSIONS_PHONE, PermissionsConstant.REQUEST_PHONE);
        }
        return phonePermissionGranted;
    }

}
