package www.qisu666.com.request.utils;

import android.text.TextUtils;
import android.util.Log;

import www.qisu666.com.R;
import www.qisu666.com.event.Message;
import www.qisu666.com.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;

import io.reactivex.subscribers.DisposableSubscriber;
import okhttp3.ResponseBody;


/**
 * Created by admin on 2018/1/16.
 */

public abstract class MyDisposableSubscriber<T> extends DisposableSubscriber<Message<T>> {

    private final String TAG = MyDisposableSubscriber.class.getName();

    public static final int IDENTITY_NOT_CONFIRM = -1145;
    public static final int OBJDECT_EMPTY = -1001 ;
    public static final int USER_LOGIN_FAIL = -1201 ;
    public static final int USER_UNDER_IDENTITY = -1159;

    @Override
    public void onNext(Message<T> message) {
        try{
            LogUtil.e( TAG + "进入onNext中:" + message.msg + "," + message.code);
        }catch (Throwable t){
            t.printStackTrace();
        }
        if (message != null){
            if (message.code > 0){
                try {
                    onSuccessCode(message);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if (message.data != null&&!message.data.equals("")){
                    try { onSuccess(message.data);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }else {
                    LogUtil.e( TAG + "MyDisposableSubscriber  Message Data Empty");
                    failed(message);
                }
            }else {
                failed(message);
            }
        }else {
            Log.e( TAG," Message Bean Empty");
        }
    }

    private void failed(Message<T> message){
        if (message == null) {
            return;
        }
        // 判断身份是否认证或者回传对象是否为空
        if (!TextUtils.isEmpty(message.msg) && message.code != IDENTITY_NOT_CONFIRM && message.code != OBJDECT_EMPTY) {
            Log.w(TAG, "失败 failed: " + message.msg + message.code);
            if (!message.msg.contains("不存在") && !message.msg.contains("已领取") && !message.msg.contains("无计费") && !message.msg.contains("重新登陆") && !message.msg.contains("暂无查询数据")) {
                // 未认证或者未注册 提示用户不存在
                // TODO 处理用户唯一编号为空
                EventBus.getDefault().post("用户唯一编号为空");
                LogUtil.e("Message is " + message.msg);
                LogUtil.e("Message Code is " + message.code);
            }
            if (message.code == USER_UNDER_IDENTITY) {
                EventBus.getDefault().post("身份证人工审核中");
            }
            if (message.code == USER_LOGIN_FAIL){
                EventBus.getDefault().post("登陆失效");
            }

        }
        onFail(message);
    }

    @Override
    public void onError(Throwable error) {
        error.printStackTrace();
        LogUtil.e( TAG+"进入error中");
        LogUtil.e( TAG+"进入error中"+error.getMessage());
        if(error!=null){
            LogUtil.e( TAG+"ResultSubscriber:"+error.getMessage());
            ToastUtil.showToast(R.string.toast_network_server_outage);
        } else {
            ToastUtil.showToast(R.string.toast_network_interrupt);
        }
    }

    @Override
    public void onComplete() {
        LogUtil.e( TAG+"进入onComplete中");
    }

    public abstract void onSuccessCode(Message object);

    public abstract void onSuccess(T bean);

    public abstract void onFail(Message<T> bean);
}
