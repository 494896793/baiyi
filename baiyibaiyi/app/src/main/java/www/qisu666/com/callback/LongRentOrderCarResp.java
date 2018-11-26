package www.qisu666.com.callback;

import java.io.Serializable;

/**
 * 短租响应值
 */
public class LongRentOrderCarResp implements Serializable {
    private String carRentOrderNumber;//订单号 string
    private String systemTime;
    private String orderTime;

    public String getCarRentOrderNumber() {
        return carRentOrderNumber;
    }

    public void setCarRentOrderNumber(String carRentOrderNumber) {
        this.carRentOrderNumber = carRentOrderNumber;
    }

    public String getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(String systemTime) {
        this.systemTime = systemTime;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
}
