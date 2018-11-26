package www.qisu666.com.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import www.qisu666.com.app.MyApplication;
import www.qisu666.com.receiver.PublicReceiver;
import www.qisu666.com.receiver.PReceiver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 发送广播统一工具类
 * Created by jimmy on 2016/10/10.
 */

public class SBUtils<T> {
    public static final String MAPKEY = "mapkey";
    //各个广播的action
    public static final String token_error = "com.bycx.token_error";
    public static final String close_history = "close_history";
    public static final String loginOk = "loginOk";
    public static final String SystemInfo = "SystemInfo";
    public static final String UPDATE_MAP_PARKS_INFO = "update.map.parks.info";

    private static SBUtils mSBUtils;

    public static SBUtils getIn() {
        if (mSBUtils == null) {
            mSBUtils = new SBUtils();
        }
        return mSBUtils;
    }

    // 发送广播//不带参数的
    public static void send(String action, int type) {
        Intent in = new Intent();
        in.setAction(action);
        in.putExtra(MAPKEY, type);
        MyApplication.getApplication().sendBroadcast(in);
    }

    // 发送广播//不带参数的
    public static void send(Context c, String action, int type) {
        Intent in = new Intent();
        in.setAction(action);
        in.putExtra(MAPKEY, type);
        c.sendBroadcast(in);
    }

    // 发送广播//带参数对象的
    public static void send(Object data, String action) {
        Intent in = new Intent();
        in.setAction(action);
        Bundle b = new Bundle();
        if (data != null) {
            b.putSerializable(MAPKEY, (Serializable) data);
        } else {
            b.putSerializable(MAPKEY, new HashMap());
        }
        in.putExtras(b);
        MyApplication.getApplication().sendBroadcast(in);
    }

    // 发送广播//带参数对象的
    public static void send(Context c, Object data, String action) {
        Intent in = new Intent();
        in.setAction(action);
        Bundle b = new Bundle();
        if (data != null) {
            b.putSerializable(MAPKEY, (Serializable) data);
        } else {
            b.putSerializable(MAPKEY, new HashMap());
        }
        in.putExtras(b);
        c.sendBroadcast(in);
    }

    // 发送登录广播
    public static void sendList(List datas, String action) {
        Intent in = new Intent();
        in.setAction(action);
        Bundle b = new Bundle();
        b.putCharSequenceArrayList(MAPKEY, (ArrayList) datas);
        in.putExtras(b);
        MyApplication.getApplication().sendBroadcast(in);
    }

    // 注册广播
    public static void register(Context context, PReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(receiver.getAction());
        context.registerReceiver(receiver, filter);
    }

    // 注册广播
    public static void register(Context context, PublicReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(receiver.getAction());
        context.registerReceiver(receiver, filter);
    }

    // 注册广播
    public static void register(PublicReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(receiver.getAction());
        MyApplication.getApplication().registerReceiver(receiver, filter);
    }

    // 注册广播
    public static void register(PReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(receiver.getAction());
        MyApplication.getApplication().registerReceiver(receiver, filter);
    }
}
