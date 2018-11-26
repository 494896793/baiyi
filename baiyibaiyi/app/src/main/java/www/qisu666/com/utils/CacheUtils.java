package www.qisu666.com.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import www.qisu666.com.app.MyApplication;
import www.qisu666.com.callback.AppUpdateResp;
import www.qisu666.com.callback.SystemConfigResp;
import www.qisu666.com.callback.UserInfoResp;
import www.qisu666.com.view.GetMoneyAccountView;

import it.sauronsoftware.base64.Base64;

public class CacheUtils {

    private static final String cache_data = "cache_data";
    public static final String user_info = "user_info";
    public static final String system_Info = "system_Info";
    public static final String cid_key = "bycx_cid";
    public static final String phone = "bycx_phone";
    public static final String latitude = "latitude";
    public static final String longitude = "longitude";
    public static final String apkUrl = "apkUrl";
    public static final String curLocationSite = "curLocationSite";
    public static final String appInfo = "appInfo";
    public static final String curCity = "curCityName";
    public static final String cityCode = "curCityCode";
    public static final String locationOk = "locationOk";
    public static final String Searchlatitude = "Searchlatitude";
    public static final String Searchlongitude = "Searchlongitude";
    public static final String searchaddr = "searchaddr";
    private String LONG_RENT_TIP = "longRentTip";

    private static CacheUtils mCacheUtils;

    public static CacheUtils getIn() {
        if (mCacheUtils == null) {
            synchronized (CacheUtils.class) {
                mCacheUtils = new CacheUtils();
            }
        }
        return mCacheUtils;
    }

    public void saveStr(String key, String value) {
        getEditor().putString(key, encode(value)).commit();
    }

    public void saveBoolen(String key, boolean value) {
        getEditor().putBoolean(key, value).commit();
    }

    public boolean getboolen(String key) {
        return getSP().getBoolean(key, false);
    }

    public String getStr(String key) {
        return decode(getSP().getString(key, ""));
    }

    public void save(Object mInfo) {
        if (mInfo != null) {
            String data = encode(JSONObject.toJSONString(mInfo));
            if (mInfo instanceof UserInfoResp) {
                getEditor().putString(user_info, data).commit();
            } else if (mInfo instanceof SystemConfigResp) {
                getEditor().putString(system_Info, data).commit();
            } else if (mInfo instanceof AppUpdateResp) {
                getEditor().putString(appInfo, data).commit();
            }
        }
    }

    public UserInfoResp getUserInfo() {
        String jsonuser = decode(getSP().getString(user_info, ""));
        MyApplication.isLoginSuccess = !TextUtils.isEmpty(jsonuser);
        if (!TextUtils.isEmpty(jsonuser)) {
            return JSONObject.parseObject(jsonuser, UserInfoResp.class);
        }
        return null;
    }

    public SystemConfigResp getSystem_Info() {
        String jsonuser = decode(getSP().getString(system_Info, ""));
        if (!TextUtils.isEmpty(jsonuser)) {
            return JSONObject.parseObject(jsonuser, SystemConfigResp.class);
        }
        return null;
    }

    public AppUpdateResp getappInfo() {
        String jsonuser = decode(getSP().getString(appInfo, ""));
        if (!TextUtils.isEmpty(jsonuser)) {
            return JSONObject.parseObject(jsonuser, AppUpdateResp.class);
        }
        return null;
    }

    public boolean isLogin() {
        return getUserInfo() != null;
    }

    public void clear(String key) {
        getEditor().putString(key, encode("")).commit();
    }

    public void clearUserInfo() {
        getEditor().putString(user_info, encode("")).commit();
    }

    public void clearMyInfo() {
        getEditor().putString(user_info, encode("")).commit();
        SharedPreferencesUtils.putString(MyApplication.getApplication(), GetMoneyAccountView.ZHIFUBAO_ACCOUNT_NAME, "");
        SharedPreferencesUtils.putString(MyApplication.getApplication(), GetMoneyAccountView.ZHIFUBAO_ACCOUNT_NO, "");
    }

    public void clear() {
        getEditor().clear().commit();
    }

    private String formatString(String str) {
        return str == null ? "" : str;
    }

    private String encode(String result) {
        return Base64.encode(formatString(result));
    }

    private String decode(String result) {
        return Base64.decode(formatString(result));
    }

    private SharedPreferences.Editor getEditor() {
        return getSP().edit();
    }

    private SharedPreferences getSP() {
        return MyApplication.getApplication().getSharedPreferences(cache_data, Context.MODE_PRIVATE);
    }

    public void setLongRentTipFlag(Context context, int tipFlag) {
        SharedPreferencesUtils.putInt(context, LONG_RENT_TIP, tipFlag);
    }

    public int getLongRentTipFlag(Context context) {
        return SharedPreferencesUtils.getInt(context, LONG_RENT_TIP, 0);
    }
}
