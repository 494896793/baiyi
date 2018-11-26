package www.qisu666.com.rx;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Administrator on 2017/3/16.
 */

public class RxManager {

    public CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    /**
     * 将Subscription添加到CompositeSubscription中
     *
     * @param subscription 要添加的Subscription
     */
    public void add(Subscription subscription) {
        mCompositeSubscription.add(subscription);
    }

    /**
     * 将CompositeSubscription中的Subscription全部移除
     */
    public void clear() {
        mCompositeSubscription.clear();
    }

    /**
     * 移除特定的Subscription
     *
     * @param subscription 将要移除的Subscription
     */
    public void remove(Subscription subscription) {
        mCompositeSubscription.remove(subscription);
    }
}
