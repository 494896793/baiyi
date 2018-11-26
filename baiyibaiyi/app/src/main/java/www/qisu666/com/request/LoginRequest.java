package www.qisu666.com.request;

/**
 * Created by wujiancheng on 2017/8/15.
 */

public class LoginRequest extends RequestBaseParams {
    private String customerPhone;
    private String customerEnrollType;
    private String customerEnrollIp;
    private String customerCityCode;

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEnrollType() {
        return customerEnrollType;
    }

    public void setCustomerEnrollType(String customerEnrollType) {
        this.customerEnrollType = customerEnrollType;
    }

    public String getCustomerEnrollIp() {
        return customerEnrollIp;
    }

    public void setCustomerEnrollIp(String customerEnrollIp) {
        this.customerEnrollIp = customerEnrollIp;
    }

    public String getCustomerCityCode() {
        return customerCityCode;
    }

    public void setCustomerCityCode(String customerCityCode) {
        this.customerCityCode = customerCityCode;
    }
}
