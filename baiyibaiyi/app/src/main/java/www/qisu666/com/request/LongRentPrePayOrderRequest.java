package www.qisu666.com.request;

/**
 * 短租预支付
 */
public class LongRentPrePayOrderRequest extends RequestBaseParams {
    private String carRentOrderNumber;//短租订单号
    private String carSharingPayType;//支付类型
    private String couponNo;

    public String getCarRentOrderNumber() {
        return carRentOrderNumber;
    }

    public void setCarRentOrderNumber(String carRentOrderNumber) {
        this.carRentOrderNumber = carRentOrderNumber;
    }

    public String getCarSharingPayType() {
        return carSharingPayType;
    }

    public void setCarSharingPayType(String carSharingPayType) {
        this.carSharingPayType = carSharingPayType;
    }

    public String getCouponNo() {
        return couponNo;
    }

    public void setCouponNo(String couponNo) {
        this.couponNo = couponNo;
    }
}
