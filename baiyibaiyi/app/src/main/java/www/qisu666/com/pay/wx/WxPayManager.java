package www.qisu666.com.pay.wx;

import android.app.Activity;

import www.qisu666.com.activity.BaseActivity;
import www.qisu666.com.app.MyApplication;
import www.qisu666.com.app.PayMode;
import www.qisu666.com.callback.WeixinPayData;
import www.qisu666.com.constant.Config;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.utils.GetPrepayIdTask;
import www.qisu666.com.utils.NetWorkUtils;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.utils.weixinpay.PayData;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;

/**
 * Created by Administrator on 2017/5/26.
 * 微信支付的管理类
 */

public class WxPayManager {
    private static final String TAG = WxPayManager.class.getSimpleName();
    private IWXAPI api;

    private GetPrepayIdTask getPrepayId;

//    /**
//     * 微信充值
//     *
//     * @param context   context
//     * @param wxPayResp 微信下单的参数
//     * @param type      微信支付类型-》0:余额充值，1：押金充值
//     */
//    public void pay(Context context, WxPayResp wxPayResp, int type) {
//        //设置微信充值类型
//        App.getInstance().setWxPayType(type);
//
//        WxPayResp.Data data = wxPayResp.getData();
//        api = WXAPIFactory.createWXAPI(context, data.getAppId());
//        api.registerApp(data.getAppId());
//
//        PayReq req = new PayReq();
//        req.appId = data.getAppId();
//        req.partnerId = data.getPartnerId();
//        req.prepayId = data.getPrepayId();
//        req.packageValue = data.getWxPackage();
//        req.nonceStr = data.getNonceStr();
//        req.timeStamp = data.getTimeStamp();
//        req.sign = data.getPaySign();
//        api.sendReq(req);
//    }

    /**
     * @param activity
     * @param application
     * @param data
     * @param money
     */
    public void pay(Activity activity, MyApplication application, WeixinPayData data, int money, int payFrom) {
        MyApplication.getApplication().setPayFrom(payFrom);
        PayReq req = new PayReq();
        PayData.API_KEY = data.getApiKey();
        PayData.APP_ID = data.getAppId();
        MyApplication.getApplication().setAPP_ID(data.getAppId());
        PayData.MCH_ID = data.getMchId();
        PayData.WEIXINBACKURL = data.getNotifyUrl();
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).doCheck("请稍后...", true);
        }
        application.msgApi.registerApp(PayData.APP_ID);
        if (getPrepayId != null) {
            getPrepayId.cancel(true);
            getPrepayId = null;
        }
        if (!NetWorkUtils.isWXAppInstalledAndSupported(activity)) {
            ToastUtil.show(activity, Config.NO_HAS_INSTALL_WX);
//            activity.closeDialog();
            RxBusEvent event = new RxBusEvent();
            event.setEventCode(RxEventCodes.CODE_CLOSE_WX_CLIENT_TIP);
            RxBus.getInstance().post(event);
            return;
        }
//        money = 1;
        getPrepayId = new GetPrepayIdTask(activity, money + "", data.getOutTradeNo(), req, PayMode.RECHARGE_PAY, data.getBody());
        getPrepayId.execute();
    }
}
