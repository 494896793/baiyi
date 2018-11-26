package www.qisu666.com.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 定时器...
 * Created by Lver on 2016/5/30.
 */
public class TimerUtil {
    private static TimerUtil instance;
    private StringBuilder sb = new StringBuilder();
    private Timer timer = null;
    private long orderTime;
    private long systemTime;
    private long differenceTime;

    private static final int TIMEOUT = 10022;
    private final byte[] lock = {};

    private TimerTickListener mListener;

    private boolean isRunning = false;
    private TimerTask task;


    public static TimerUtil getInstance() {
        if (instance == null) {
            synchronized (TimerUtil.class) {
                instance = new TimerUtil();
            }
        }
        return instance;
    }

    public void setTime(long orderTime, long systemTime, long differenceTime) {
        this.orderTime = orderTime;
        this.systemTime = systemTime;
        this.differenceTime = differenceTime;
        start();
    }


    public void start() {
        synchronized (lock) {
            if (!isRunning) {
                long timeTick = 1000;
                try {
                    if (timer != null) {
                        timer.cancel();
                        timer.purge();
                    }
                    if (task != null) {
                        task.cancel();
                    }
                    timer = new Timer();
                    task = new TimerTask() {
                        @Override
                        public void run() {
                            mHandler.sendEmptyMessage(TIMEOUT);
                        }
                    };
                    timer.scheduleAtFixedRate(task, timeTick, timeTick);
                    isRunning = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void cancel() {
        synchronized (lock) {
            if (timer != null) {
                timer.cancel();
                timer.purge();
            }
            if (task != null) {
                task.cancel();
            }
            task = null;
            timer = null;
            isRunning = false;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    //时间计数器，如需要更大小时数需要改改方法
    public String showTimeCount() {
        if (orderTime < 0) {
            return "00:00:00";
        }
        long addTime = (systemTime - orderTime + differenceTime) / 1000;
        return DateUtils.timeUserCarColon((int) addTime);
    }

    private void onTick() {
        if (mListener != null) {
            mListener.onTick(this);
        }
    }

    public void setTimerTickListener(TimerTickListener mListener) {
        this.mListener = mListener;
    }

    public void clearTimerTickListener() {
        this.mListener = null;
    }

    public void setSystemTime(long systemTime) {
        this.systemTime = systemTime;
    }

    static class TimeoutHandler extends Handler {
        private final WeakReference<TimerUtil> mTimer;

        public TimeoutHandler(TimerUtil timer) {
            mTimer = new WeakReference<>(timer);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TIMEOUT) {
                // Log.i("Timer-->handleMessage-->", "TIMEOUT");
                TimerUtil tempTimer = mTimer.get();
                if (tempTimer != null) {
                    tempTimer.onTick();
                }
            } else {
                super.handleMessage(msg);
            }
        }
    }


    private final TimeoutHandler mHandler = new TimeoutHandler(this);

    public interface TimerTickListener {

        void onTick(TimerUtil timer);
    }

}
