package www.qisu666.com.callback;

public class WeixinPayData {

    /**
     * apiKey : 0ced4de9ce787142b79c4edb29bba55a
     * appId : wx2d15873b43aa9af8
     * body : 佰壹出行会员充值
     * mchId : 1334915801
     * notifyUrl : http://interface.xcloudtrip.com/services/i/wx
     * outTradeNo : C-B-52-1462245209513
     */

    private String apiKey;
    private String appId;
    private String body;
    private String mchId;
    private String notifyUrl;
    private String outTradeNo;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }
}
