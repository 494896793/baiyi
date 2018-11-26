package www.qisu666.com.request;

public class OrderCarRequest extends RequestBaseParams {
    private String customerId;
    private String carNumber;
    private String isCompanyOrder = "";//是否企业订单"true","false"
    private String companyId = "";
    private String isInsurance;//是否不计免赔订单 (是:1  否:0)
    private String beforehandParkId;//预还车网点ID

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getIsCompanyOrder() {
        return isCompanyOrder;
    }

    public void setIsCompanyOrder(String isCompanyOrder) {
        this.isCompanyOrder = isCompanyOrder;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getIsInsurance() {
        return isInsurance;
    }

    public void setIsInsurance(String isInsurance) {
        this.isInsurance = isInsurance;
    }

    public String getBeforehandParkId() {
        return beforehandParkId;
    }

    public void setBeforehandParkId(String beforehandParkId) {
        this.beforehandParkId = beforehandParkId;
    }
}
