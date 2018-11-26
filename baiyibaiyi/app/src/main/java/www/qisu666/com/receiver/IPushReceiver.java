package www.qisu666.com.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import www.qisu666.com.callback.GeneralPush;
import www.qisu666.com.constant.PushKeys;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.utils.Logger;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

public class IPushReceiver extends BroadcastReceiver {
    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                byte[] payload = bundle.getByteArray("payload");
                String taskId = bundle.getString("taskid");
                String messageId = bundle.getString("messageid");
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskId, messageId, 90001);
                Logger.e("推送" + (result ? "成功" : "失败"));
                if (payload != null) {
                    String data = new String(payload);
                    Logger.e("服务器推送的消息：" + data);
                    try {
                        GeneralPush pushResponse = JSON.parseObject(data, GeneralPush.class);
                        if (pushResponse != null && pushResponse.getCode() != null) {
                            String eventCode = pushResponse.getEventCode();
                            RxBusEvent event = new RxBusEvent();

                            if (eventCode.equals(PushKeys.SERVER_PUSH_CLOSE_DOOR_SUCCESS)) {//关门成功
                                event.setEventCode(RxEventCodes.SERVER_PUSH_CLOSE_DOOR_SUCCESS);
                                event.setContent(pushResponse.getMsg());
                            } else if (eventCode.equals(PushKeys.SERVER_PUSH_DETECTION_SUCCESS)) {//车辆检测成功
                                event.setEventCode(RxEventCodes.SERVER_PUSH_DETECTION_SUCCESS);
                                event.setContent(pushResponse.getMsg());
                            } else if (eventCode.equals(PushKeys.SERVER_PUSH_ORDER_CANCEL)) {//订单取消
                                event.setEventCode(RxEventCodes.SERVER_PUSH_ORDER_CANCEL);
                                event.setContent(pushResponse.getMsg());
                            } else if (eventCode.equals(PushKeys.SERVER_PUSH_COUPON_SUCCESS)) {//注册送优惠券
                                event.setEventCode(RxEventCodes.SERVER_PUSH_COUPON_SUCCESS);
                                event.setContent(pushResponse.getMsg());
                            } else if (eventCode.equals(PushKeys.SERVER_PUSH_BING_COUPON)) {//绑定优惠券
                                event.setEventCode(RxEventCodes.SERVER_PUSH_BING_COUPON);
                                event.setContent(pushResponse.getMsg());
                            } else if (eventCode.equals(PushKeys.SERVER_PUSH_RETURN_CAR)) {//后台还车成功
                                event.setEventCode(RxEventCodes.SERVER_PUSH_RETURN_CAR);
                                event.setContent(pushResponse.getMsg());
                            }
                            RxBus rxBus = RxBus.getInstance();
                            rxBus.post(event);
                        }
                    } catch (Exception e) {
                        Logger.e("可能推送的消息不能解析");
                    }
                }
                break;
        }
    }
}
