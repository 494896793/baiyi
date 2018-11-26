package www.qisu666.com.rx;


import www.qisu666.com.utils.Logger;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * RxBus
 * Created by izhonghong on 2016/12/28.
 */
public class RxBus {
    private static final String TAG = RxBus.class.getSimpleName();
    private static volatile RxBus mInstance;
    private Subject<Object, Object> bus;

    private RxBus() {
        bus = new SerializedSubject<>(PublishSubject.create());
    }

    public static RxBus getInstance() {
        if (mInstance == null) {
            synchronized (RxBus.class) {
                if (mInstance == null) {
                    RxBus tmp = null;
                    try {
                        tmp = new RxBus();
                    } catch (Exception e) {
                        Logger.e(TAG, "rxBus出问题了：" + e);
                    }
                    mInstance = tmp;
                }
            }
        }
        return mInstance;
    }

    public void post(Object o) {
        bus.onNext(o);
    }

    public <T> void post(RxBusEvent<T> event) {
        bus.onNext(event);
    }

    public <T> Observable<T> toObservable(Class<T> eventType) {
        return bus.ofType(eventType);
    }
}
