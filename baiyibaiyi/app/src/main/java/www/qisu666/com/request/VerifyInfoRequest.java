package www.qisu666.com.request;

public class VerifyInfoRequest {
    private String driverIdcard;
    private String idcard;
    private String name;
    private String sex;


    public VerifyInfoRequest() {
    }

    public VerifyInfoRequest(String driverIdcard, String idcard, String name, String sex) {
        this.driverIdcard = driverIdcard;
        this.idcard = idcard;
        this.name = name;
        this.sex = sex;
    }

    public String getDriverIdcard() {
        return driverIdcard;
    }

    public void setDriverIdcard(String driverIdcard) {
        this.driverIdcard = driverIdcard;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
