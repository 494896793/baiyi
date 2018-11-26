package www.qisu666.com.request;

/**
 * 短租还车
 */
public class ReturnCarLongRentRequest extends RequestBaseParams {
    private String customerId;
    private String carRentOrderNumber;
    private String longitude;
    private String latitude;
    private String depotId;
    private String isCheck;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCarRentOrderNumber() {
        return carRentOrderNumber;
    }

    public void setCarRentOrderNumber(String carRentOrderNumber) {
        this.carRentOrderNumber = carRentOrderNumber;
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

    public String getDepotId() {
        return depotId;
    }

    public void setDepotId(String depotId) {
        this.depotId = depotId;
    }

    public String getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(String isCheck) {
        this.isCheck = isCheck;
    }
}
