package www.qisu666.com.request;

/**
 * Created by wujiancheng on 17-10-17.
 * 查询停车费报销审核进度
 */

public class ParkingFeeAuditStatusRequest extends RequestBaseParams {
    private String orderId;//true string 订单ID
    private String parkingFeeId;//true string 停车费记录ID
    private String orderCategory;//true string 订单类型('rentOrder'代表短租订单)

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getParkingFeeId() {
        return parkingFeeId;
    }

    public void setParkingFeeId(String parkingFeeId) {
        this.parkingFeeId = parkingFeeId;
    }

    public String getOrderCategory() {
        return orderCategory;
    }

    public void setOrderCategory(String orderCategory) {
        this.orderCategory = orderCategory;
    }
}