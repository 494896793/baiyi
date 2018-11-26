package www.qisu666.com.request;

/**
 * 短租超时额外金额支付
 */
public class LongRentExtraPayRequest extends RequestBaseParams {
    private String customerId;
    private String carRentPayType;
    private String carRentOrderNumber;
    private String couponCode;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCarRentPayType() {
        return carRentPayType;
    }

    public void setCarRentPayType(String carRentPayType) {
        this.carRentPayType = carRentPayType;
    }

    public String getCarRentOrderNumber() {
        return carRentOrderNumber;
    }

    public void setCarRentOrderNumber(String carRentOrderNumber) {
        this.carRentOrderNumber = carRentOrderNumber;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }
}
