package www.qisu666.com.callback;

import java.io.Serializable;

public class UseFeeResp implements Serializable {
    private int sharingOrderTime;//":11,
    private int sharingOrderMilesMoney;//":0,
    private int sharingOrderTimeMoney;//":165,
    private int sharingOrderTotalMoney;//":165,
    private String sharingOrderMiles;//":"0.0"
    private String enduranceMileage;
    private String battery;
    private String longitude;
    private String latitude;

    public int getSharingOrderTime() {
        return sharingOrderTime;
    }

    public void setSharingOrderTime(int sharingOrderTime) {
        this.sharingOrderTime = sharingOrderTime;
    }

    public int getSharingOrderMilesMoney() {
        return sharingOrderMilesMoney;
    }

    public void setSharingOrderMilesMoney(int sharingOrderMilesMoney) {
        this.sharingOrderMilesMoney = sharingOrderMilesMoney;
    }

    public int getSharingOrderTimeMoney() {
        return sharingOrderTimeMoney;
    }

    public void setSharingOrderTimeMoney(int sharingOrderTimeMoney) {
        this.sharingOrderTimeMoney = sharingOrderTimeMoney;
    }

    public int getSharingOrderTotalMoney() {
        return sharingOrderTotalMoney;
    }

    public void setSharingOrderTotalMoney(int sharingOrderTotalMoney) {
        this.sharingOrderTotalMoney = sharingOrderTotalMoney;
    }

    public String getSharingOrderMiles() {
        return sharingOrderMiles;
    }

    public void setSharingOrderMiles(String sharingOrderMiles) {
        this.sharingOrderMiles = sharingOrderMiles;
    }

    public String getEnduranceMileage() {
        return enduranceMileage;
    }

    public void setEnduranceMileage(String enduranceMileage) {
        this.enduranceMileage = enduranceMileage;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}