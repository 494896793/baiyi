package www.qisu666.com.request.utils;


import www.qisu666.com.event.Message;

/**
 * Created by admin on 2018/1/15.
 */

public abstract class ResultSubscriber<T> extends MyDisposableSubscriber<T> {

    @Override
    public void onError(Throwable e) {
        super.onError(e);
    }

    @Override
    public void onNext(Message<T> message) {
        super.onNext(message);
    }

    @Override
    public void onComplete() {
        super.onComplete();
    }

}
