package www.qisu666.com.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import www.qisu666.com.app.MyApplication;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.utils.weixinpay.MD5;
import www.qisu666.com.utils.weixinpay.PayData;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import org.xmlpull.v1.XmlPullParser;
import org.xutils.common.util.KeyValue;

import java.io.StringReader;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 微信支付异步调用
 * Created by xiao on 2015/12/18.
 */
public class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {
    private static final String TAG = "GetPrepayIdTask";
    private MyApplication application;
    private String money;
    private String orderNo;
    private PayReq req;
    private Map<String, String> resultunifiedorder;
    private IWXAPI msgApi;
    private String body;
    private int payMode;

    public GetPrepayIdTask(Context mContext, String money, String orderId, PayReq req, int payMode, String body) {
        this.money = money;
        this.orderNo = orderId;
        this.req = req;
        this.payMode = payMode;
        this.body = body;
        application = (MyApplication) mContext.getApplicationContext();
        this.msgApi = application.msgApi;
    }


    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Map<String, String> doInBackground(Void... params) {
        String url = PayData.WEIXIN_PAYURL;
        String entity = genProductArgs();
        Logger.e("转化后的xml文件entity=" + entity);
        byte[] buf = HttpUtils.httpPost(url, entity);
        String content = new String(buf);
        Logger.e("orion", content);
        Map<String, String> xml = decodeXml(content);
        return xml;
    }

    @Override
    protected void onPostExecute(Map<String, String> result) {
        Logger.i(TAG, "prepay_id=====" + result.get("prepay_id"));
        String prepayId = result.get("prepay_id");
        if (TextUtils.isEmpty(prepayId)) {//签名错误
            RxBusEvent event = new RxBusEvent();
            event.setEventCode(RxEventCodes.CODE_CLOSE_WX_CLIENT_TIP);
            RxBus.getInstance().post(event);
        }
        resultunifiedorder = result;
        genPayReq(req);
        sendPayReq();
    }


    private String genProductArgs() {
        StringBuffer xml = new StringBuffer();
        try {
            String notify_url = "";
//            if (payMode == PayMode.RECHARGE_PAY) {
            notify_url = PayData.WEIXINBACKURL;
//            } else if (payMode == PayMode.GOODS_PAY) {
//                body = PayMode.GOODS_PAY_STR;
//                notify_url = application.commandShare.getSettings(Configure.HTTP.WEIXINPAYGOODSBACKURL);
//            } else if (payMode == PayMode.SPECIAL_CAR_PAY) {
//                body = PayMode.SPECIAL_CAR_PAY_STR;
//                notify_url = application.commandShare.getSettings(Configure.HTTP.WEIXINPAY_SPECIAL_BACKURL);
//            }else if(payMode == PayMode.STORE_CAR_PAY){
//                body = PayMode.STORE_PAY_STR;
//                notify_url = application.commandShare.getSettings(Configure.HTTP.WEIXINPAY_STORE_BACKURL);
//            }

            String nonceStr = genNonceStr();
            xml.append("</xml>");
            List<KeyValue> packageParams = new LinkedList<KeyValue>();
            packageParams.add(new KeyValue("appid", PayData.APP_ID));
            body = URLDecoder.decode(body, "ISO-8859-1");
            packageParams.add(new KeyValue("body", body));
            Logger.e("增加参数body=" + body);
            packageParams.add(new KeyValue("mch_id", PayData.MCH_ID));
            packageParams.add(new KeyValue("nonce_str", nonceStr));
            packageParams.add(new KeyValue("notify_url", notify_url));
            packageParams.add(new KeyValue("out_trade_no", orderNo));
//            packageParams.add(new KeyValue("spbill_create_ip", application.ip));
            packageParams.add(new KeyValue("total_fee", money + ""));
            packageParams.add(new KeyValue("trade_type", "APP"));

            String sign = genPackageSign(packageParams);
            Logger.e(TAG, "第一步的签名:" + sign);
            packageParams.add(new KeyValue("sign", sign));

            String xmlstring = toXml(packageParams);
            Logger.e("xml文件:" + xmlstring);
//            byte[] tmp = xmlstring.toString().getBytes("UTF-8");
//            String tmpXML = new String(tmp, "ISO-8859-1");
//            Logger.e("最终输出xml" + tmpXML);

            return xmlstring;
        } catch (Exception e) {
            Logger.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }
    }

    private String genAppSign(List<KeyValue> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).key);
            sb.append('=');
            sb.append(params.get(i).value);
            sb.append('&');
        }
        sb.append("key=");
        sb.append(PayData.API_KEY);

        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        Log.e("orion", appSign);
        return appSign;
    }

    /**
     * 生成签名
     */
    private String genPackageSign(List<KeyValue> params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).key);
            sb.append('=');
            sb.append(params.get(i).value);
            sb.append('&');
        }
        sb.append("key=");
        sb.append(PayData.API_KEY);

        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return packageSign;
    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    public Map<String, String> decodeXml(String content) {
        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if (!"xml".equals(nodeName)) {
                            //实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion", e.toString());
        }
        return null;
    }

    private String toXml(List<KeyValue> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<" + params.get(i).key + ">");


            sb.append(params.get(i).value);
            sb.append("</" + params.get(i).key + ">");
        }
        sb.append("</xml>");

        Log.e("orion", sb.toString());
        return sb.toString();
    }

    private void genPayReq(PayReq req) {
        req.appId = PayData.APP_ID;
        req.partnerId = PayData.MCH_ID;
        req.prepayId = resultunifiedorder.get("prepay_id");
        Logger.e(TAG, "prepayId==========" + req.prepayId);
        req.packageValue = "Sign=WXPay";
        req.nonceStr = genNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());
        List<KeyValue> signParams = new LinkedList<KeyValue>();
        signParams.add(new KeyValue("appid", req.appId));
        signParams.add(new KeyValue("noncestr", req.nonceStr));
        signParams.add(new KeyValue("package", req.packageValue));
        signParams.add(new KeyValue("partnerid", req.partnerId));
        signParams.add(new KeyValue("prepayid", req.prepayId));
        signParams.add(new KeyValue("timestamp", req.timeStamp));

        req.sign = genAppSign(signParams);
        Log.e("orion", signParams.toString());
    }

    private void sendPayReq() {
        msgApi.registerApp(PayData.APP_ID);
        msgApi.sendReq(req);
    }

    //中文转换成json unicode
    private static String chinaToUnicode(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            int chr1 = str.charAt(i);
            if (chr1 >= 19968 && chr1 <= 171941) {//汉字范围 \u4e00-\u9fa5 (中文)
                result += "\\u" + Integer.toHexString(chr1);
            } else {
                result += str.charAt(i);
            }
        }
        return result;
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }
}
