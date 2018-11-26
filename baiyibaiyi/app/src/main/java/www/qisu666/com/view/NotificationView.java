package www.qisu666.com.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import www.qisu666.com.R;
import www.qisu666.com.activity.MessageActivity;

public class NotificationView {
    private static int notifyId = 1;

    public static void show(Context context, String title, String msg, String type) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.logo)    //设置图标
                .setContentTitle(title)  //设置标题
                .setContentText(msg)  //设置在下拉菜单中的显示内容
                .setTicker("佰壹出行系统消息")            //设置在最顶端的显示内容
                .setAutoCancel(true);
        NotificationManager mNotifiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        // 点击notification自动消失
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        Intent appIntent;
        if (type.equals("0")) {
            appIntent = new Intent(context, MessageActivity.class);
        } else {
            appIntent = new Intent(context, context.getClass());
        }
        appIntent.setAction(Intent.ACTION_MAIN);
        appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//关键的一步，设置启动模式
        PendingIntent pi = PendingIntent.getActivity(context, 0,
                appIntent, 0);
        notification.contentIntent = pi;
        // 发送到手机的通知栏
        notifyId++;
        mNotifiManager.notify(notifyId, notification);
    }

    public static void show(Context context, Class clazz, String title, String msg) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.logo)    //设置图标
                .setContentTitle(title)  //设置标题
                .setContentText(msg)  //设置在下拉菜单中的显示内容
                .setTicker("佰壹出行系统消息")            //设置在最顶端的显示内容
                .setAutoCancel(true);
        NotificationManager mNotifiManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        // 点击notification自动消失
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        Intent appIntent;
        appIntent = new Intent(context, clazz);
        appIntent.setAction(Intent.ACTION_MAIN);
        appIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);//关键的一步，设置启动模式
        PendingIntent pi = PendingIntent.getActivity(context, 0,
                appIntent, 0);
        notification.contentIntent = pi;
        // 发送到手机的通知栏
        notifyId++;
        mNotifiManager.notify(notifyId, notification);
    }
}
