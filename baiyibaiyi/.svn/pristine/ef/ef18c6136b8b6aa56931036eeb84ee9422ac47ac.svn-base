package www.qisu666.com.rx;

import www.qisu666.com.utils.Logger;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by Administrator on 2017/4/13.
 * 倒计时
 */

public class RxTimeCountDown {
    private static final String TAG = RxTimeCountDown.class.getSimpleName();

    public static Observable<Integer> timeCountDown(int time) {
        if (time < 0) time = 0;

        final int countTime = time;
        return Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Long, Integer>() {
                    @Override
                    public Integer call(Long increaseTime) {
                        return countTime - increaseTime.intValue();
                    }
                })
                .take(countTime + 1);
    }

    public static Observable<Integer> timeCountUp(int time) {
        Logger.i(TAG, "timeCountUp 1111 === " + time);
        if (time < 0) time = 0;

        final int countTime = time;
        return Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Long, Integer>() {
                    @Override
                    public Integer call(Long increaseTime) {
//                        Logger.i(TAG, "timeCountUp == " + (countTime + increaseTime.intValue()));
                        return countTime + increaseTime.intValue();
                    }
                });
//                .take(countTime + 1);
    }
}
