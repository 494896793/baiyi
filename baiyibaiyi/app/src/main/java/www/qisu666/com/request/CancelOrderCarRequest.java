package www.qisu666.com.request;

public class CancelOrderCarRequest extends RequestBaseParams {
    private String carSharingOrderNumber;

    public String getCarSharingOrderNumber() {
        return carSharingOrderNumber;
    }

    public void setCarSharingOrderNumber(String carSharingOrderNumber) {
        this.carSharingOrderNumber = carSharingOrderNumber;
    }
}
