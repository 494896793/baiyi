package www.qisu666.com.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.alipay.sdk.app.PayTask;

/**
 * 支付宝支付
 * Created by xiao on 2015/12/18.
 */
public class AliPayUtils {
    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public static void pay(final Context mContext, final String payInfo, final Handler uiHandler) {
//        if (TextUtils.isEmpty(AliConstants.PARTNER) || TextUtils.isEmpty(AliConstants.RSA_PRIVATE)
//                || TextUtils.isEmpty(AliConstants.SELLER)) {
//            new AlertDialog.Builder(mContext)
//                    .setTitle("警告")
//                    .setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
//                    .setPositiveButton("确定",
//                            new DialogInterface.OnClickListener() {
//                                public void onClick(
//                                        DialogInterface dialoginterface, int i) {
//
//
//                                }
//                            }).show();
//            return;
//        }
// 订单
//        String url = "";
//        MyApplication application = (MyApplication) mContext.getApplicationContext();
//        String payModeStr = "";
//        if (payMode == PayMode.RECHARGE_PAY) {
//            url = application.commandShare.getSettings(Configure.HTTP.ALIBACKURL);
//            payModeStr = PayMode.RECHARGE_PAY_STR;
//        } else if (payMode == PayMode.GOODS_PAY) {
//            url = application.commandShare.getSettings(Configure.HTTP.ALIPAYGOODSBACKURL);
//            payModeStr = PayMode.GOODS_PAY_STR;
//        } else if (payMode == PayMode.SPECIAL_CAR_PAY) {
//            url = application.commandShare.getSettings(Configure.HTTP.ALIPAY_SPECIAL_BACKURL);
//            payModeStr = PayMode.SPECIAL_CAR_PAY_STR;
//        } else if (payMode == PayMode.STORE_CAR_PAY) {
//            url = application.commandShare.getSettings(Configure.HTTP.ALI_STORE_PAYURL);
//            payModeStr = PayMode.STORE_PAY_STR;
//        }
//        String orderInfo = getOrderInfo(mContext, PayMode.COMPANY_NAME, payModeStr, money, ChargeId, url);
        // 对订单做RSA 签名
//        String sign = sign(orderInfo);
//        try {
//            // 仅需对sign 做URL编码
//            sign = URLEncoder.encode(sign, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        // 完整的符合支付宝参数规范的订单信息
//        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
//                + getSignType();
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask((Activity) mContext);
                // 调用支付接口，获取支付结果
                final String result = alipay.pay(payInfo, false);
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        PayResult payResult = new PayResult(result);
                        // 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
//                        String resultInfo = payResult.getResult();
                        String resultStatus = payResult.getResultStatus();
                        if ("9000".equals(resultStatus)) {
                            ToastUtil.show(mContext, "支付成功");
                        } else {
                            ToastUtil.show(mContext, "支付失败");
                        }
                        // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    }
                });
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * check whether the device has authentication alipay account.
     * 查询终端设备是否存在支付宝认证账户
     */
//    public void check(final Context mContext) {
//        Runnable checkRunnable = new Runnable() {
//            @Override
//            public void run() {
//                // 构造PayTask 对象
//                PayTask payTask = new PayTask((Activity) mContext);
//                // 调用查询接口，获取查询结果
//                boolean isExist = payTask.checkAccountIfExist();
//                Message msg = new Message();
//                msg.what = SDK_CHECK_FLAG;
//                msg.obj = isExist;
//                mHandler.sendMessage(msg);
//            }
//        };
//        Thread checkThread = new Thread(checkRunnable);
//        checkThread.start();
//    }

//    /**
//     * create the order info. 创建订单信息
//     */
//    public static String getOrderInfo(Context mContext, String subject, String body, String price, String ChargeId, String url) {
//        // 签约合作者身份ID
//        String orderInfo = "partner=" + "\"" + AliConstants.PARTNER + "\"";
//        // 签约卖家支付宝账号
//        orderInfo += "&seller_id=" + "\"" + AliConstants.SELLER + "\"";
//        // 商户网站唯一订单号
//        orderInfo += "&out_trade_no=" + "\"" + ChargeId + "\"";
//        // 商品名称
//        orderInfo += "&subject=" + "\"" + subject + "\"";
//        // 商品详情
//        orderInfo += "&body=" + "\"" + body + "\"";
//        // 商品金额
//        orderInfo += "&total_fee=" + "\"" + price + "\"";
//        // 服务器异步通知页面路径
//        MyApplication application = (MyApplication) mContext.getApplicationContext();
//        orderInfo += "&notify_url=" + "\"" + url
//                + "\"";
//        // 服务接口名称， 固定值
//        orderInfo += "&service=\"mobile.securitypay.pay\"";
//        // 支付类型， 固定值
//        orderInfo += "&payment_type=\"1\"";
//        // 参数编码， 固定值
//        orderInfo += "&_input_charset=\"utf-8\"";
//        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
//        //orderInfo += "&return_url=\"\"";
//        return orderInfo;
//    }
//
//    /**
//     * sign the order info. 对订单信息进行签名
//     *
//     * @param content 待签名订单信息
//     */
//    public static String sign(String content) {
//        return SignUtils.sign(content, AliConstants.RSA_PRIVATE);
//    }
//
//    /**
//     * get the sign type we use. 获取签名方式
//     */
//    public static String getSignType() {
//        return "sign_type=\"RSA\"";
//    }
}
