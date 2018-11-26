package www.qisu666.com.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import www.qisu666.com.utils.Logger;

public class TimeCountService extends Service {
    public static TimeCountListener listener;
    private static MyCount myCount;
    private static boolean isFinish = true;

    public static void setTime(int time) {
        if (myCount == null) {
            isFinish = true;
            myCount = new MyCount(time, 1000);
            Logger.i("开始计时111");
            myCount.start();
        } else {
            if (isFinish) {
                myCount = new MyCount(time, 1000);
                myCount.start();
                Logger.i("开始计时222");
            }
        }
    }

    public static void setTime(int time, int type) {
        if (myCount == null) {
            isFinish = true;
            myCount = new MyCount(time, 1000);

        }
        Logger.i("开始计时333");
        myCount.start();
    }

    public static void stopTime() {
        if (myCount != null) {
            isFinish = true;
            myCount.cancel();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isFinish = true;
    }


    /*定义一个倒计时的内部类*/
    static class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            isFinish = true;
            if (listener != null) {
                listener.timeOver();
            }
        }

        @Override
        public void onTick(final long millisUntilFinished) {
            isFinish = false;
            if (listener != null) {
                listener.timeGo((int) (millisUntilFinished / 1000));
            }
        }
    }

    public interface TimeCountListener {

        void timeGo(int millisUntilFinished);

        void timeOver();
    }

}
