package www.qisu666.com.request.utils;

import android.content.Context;
import android.content.DialogInterface;

import www.qisu666.com.event.Message;
import www.qisu666.com.widget.LoadingDialog;


import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by admin on 2018/1/15.
 */

public class RxNetHelper {

     /**
     * 基本请求,默认带Dialog
     */
    public static <T> FlowableTransformer<Message<T>, Message<T>> io_main() {
        return new FlowableTransformer<Message<T>, Message<T>>() {
            @Override
            public Publisher<Message<T>> apply(Flowable<Message<T>> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 带进度条的请求
     */
    public static <T> FlowableTransformer<Message<T>, Message<T>> io_main(final LoadingDialog loadingDialog) {
        return new FlowableTransformer<Message<T>, Message<T>>() {
            @Override
            public Publisher<Message<T>> apply(Flowable<Message<T>> upstream) {
                return upstream
                        //为了让进度条保持一会儿做了个延时
//                        .delay(1, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Subscription>() {
                            @Override
                            public void accept(final Subscription subscription) throws Exception {
                                if (loadingDialog != null){
                                    loadingDialog.show();
                                    loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            subscription.cancel();
                                        }
                                    });
                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 基本请求,默认带Dialog
     */
    public static <T> FlowableTransformer<T, T> io_other_main(final Context context) {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 带进度条的请求
     */
    public static <T> FlowableTransformer<T, T> io_other_main(final LoadingDialog loadingDialog) {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream
                        //为了让进度条保持一会儿做了个延时
//                        .delay(1, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .doOnSubscribe(new Consumer<Subscription>() {
                            @Override
                            public void accept(final Subscription subscription) throws Exception {
                                if (loadingDialog != null){
                                    loadingDialog.show();
                                    loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialog) {
                                            subscription.cancel();
                                        }
                                    });
                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

}
