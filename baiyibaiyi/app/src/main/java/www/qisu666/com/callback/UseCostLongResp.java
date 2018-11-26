package www.qisu666.com.callback;

import java.io.Serializable;

/**
 * 短租每分钟计费
 */
public class UseCostLongResp implements Serializable {
    private long orderBeginTime;//订单开始时间
    private String orderMileage;//套餐内行驶里程
    private int orderPrePayMoney;//订单预支付金额
    private long orderEndTime;//套餐结束时间
    private long systemTime;//系统时间
    private String battery;//车辆电量
    private String enduranceMileage;//车辆续航
    private String name;//套餐名称
    private String days;//套餐天数
    private String useTime;//订单使用时长（毫秒单位）
    private String outUseTime;//订单超时时长（毫秒单位）
    private String outTimeMilage;//订单超时里程
    private int outTimeCost;//订单超时花费
    private String latitude;
    private String longitude;

    public long getOrderBeginTime() {
        return orderBeginTime;
    }

    public void setOrderBeginTime(long orderBeginTime) {
        this.orderBeginTime = orderBeginTime;
    }

    public String getOrderMileage() {
        return orderMileage;
    }

    public void setOrderMileage(String orderMileage) {
        this.orderMileage = orderMileage;
    }

    public int getOrderPrePayMoney() {
        return orderPrePayMoney;
    }

    public void setOrderPrePayMoney(int orderPrePayMoney) {
        this.orderPrePayMoney = orderPrePayMoney;
    }

    public long getOrderEndTime() {
        return orderEndTime;
    }

    public void setOrderEndTime(long orderEndTime) {
        this.orderEndTime = orderEndTime;
    }

    public long getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(long systemTime) {
        this.systemTime = systemTime;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getEnduranceMileage() {
        return enduranceMileage;
    }

    public void setEnduranceMileage(String enduranceMileage) {
        this.enduranceMileage = enduranceMileage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }

    public String getOutUseTime() {
        return outUseTime;
    }

    public void setOutUseTime(String outUseTime) {
        this.outUseTime = outUseTime;
    }

    public String getOutTimeMilage() {
        return outTimeMilage;
    }

    public void setOutTimeMilage(String outTimeMilage) {
        this.outTimeMilage = outTimeMilage;
    }

    public int getOutTimeCost() {
        return outTimeCost;
    }

    public void setOutTimeCost(int outTimeCost) {
        this.outTimeCost = outTimeCost;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
