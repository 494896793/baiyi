package www.qisu666.com.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by admin on 2017/3/6.
 */
public class CountDownTimerUtils extends Service {
    public static countDownTimerIf countDownTimerListener;
    private static Context c;
    private static StartCountDownTime myCountTime;

    public static void setTime(int time) {
        int timeAll;
        if ((60 * 20 - time) > 0) {
            timeAll = 60 * 20 - time;
        } else {
            timeAll = 30;
        }
        if (myCountTime != null) {
            myCountTime.cancel();
        }
        myCountTime = new StartCountDownTime(timeAll * 1000, 1000);
        myCountTime.start();
    }

    public static void stopTime() {
        if (myCountTime != null) {
            myCountTime.cancel();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        c = this;
    }

    static class StartCountDownTime extends CountDownTimer {
        /**
         * 最简单的倒计时类，实现了官方的CountDownTimer类（没有特殊要求的话可以使用）
         * 即使退出activity，倒计时还能进行，因为是创建了后台的线程。
         * 有onTick，onFinsh、cancel和start方法
         */

        public StartCountDownTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //每隔countDownInterval秒会回调一次onTick()方法
            if (countDownTimerListener != null) {
                countDownTimerListener.timeStart((int) (millisUntilFinished / 1000));
            }
        }

        @Override
        public void onFinish() {
            countDownTimerListener.Over();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface countDownTimerIf {
        void timeStart(int timeMillisUntil);

        void Over();
    }
}
