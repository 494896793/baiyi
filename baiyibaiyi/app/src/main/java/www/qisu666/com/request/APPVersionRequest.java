package www.qisu666.com.request;

public class APPVersionRequest extends RequestBaseParams {
    private String appVersionRecords;
    private String appVersionType;
    private String addressType;

    public String getAppVersionRecords() {
        return appVersionRecords;
    }

    public void setAppVersionRecords(String appVersionRecords) {
        this.appVersionRecords = appVersionRecords;
    }

    public String getAppVersionType() {
        return appVersionType;
    }

    public void setAppVersionType(String appVersionType) {
        this.appVersionType = appVersionType;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }
}
