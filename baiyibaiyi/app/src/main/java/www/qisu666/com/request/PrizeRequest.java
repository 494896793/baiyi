package www.qisu666.com.request;

/**
 * 抽奖
 */
public class PrizeRequest extends RequestBaseParams {
    private String customerId;
    private String type;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
