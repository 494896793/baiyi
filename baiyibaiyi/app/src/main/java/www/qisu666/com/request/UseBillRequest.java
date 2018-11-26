package www.qisu666.com.request;

public class UseBillRequest extends RequestBaseParams {
    private String customerId;
    private String carSharingPayType;
    private String carSharingOrderNumber;
    private String couponCode;
    private String isCompanyDiscount;//是否接受企业折扣(1 : 接受 , 0 : 不接受)
    private String companyId;//企业Id

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCarSharingPayType() {
        return carSharingPayType;
    }

    public void setCarSharingPayType(String carSharingPayType) {
        this.carSharingPayType = carSharingPayType;
    }

    public String getCarSharingOrderNumber() {
        return carSharingOrderNumber;
    }

    public void setCarSharingOrderNumber(String carSharingOrderNumber) {
        this.carSharingOrderNumber = carSharingOrderNumber;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getIsCompanyDiscount() {
        return isCompanyDiscount;
    }

    public void setIsCompanyDiscount(String isCompanyDiscount) {
        this.isCompanyDiscount = isCompanyDiscount;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
