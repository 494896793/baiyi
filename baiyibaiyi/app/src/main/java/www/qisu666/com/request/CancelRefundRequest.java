package www.qisu666.com.request;

/**
 * Created by wujiancheng on 2017/8/15.
 */

public class CancelRefundRequest extends RequestBaseParams {
    private String customerId;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
