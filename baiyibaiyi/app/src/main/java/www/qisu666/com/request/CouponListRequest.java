package www.qisu666.com.request;

/**
 * Created by wujiancheng on 2017/9/8.
 */

public class CouponListRequest extends RequestBaseParams {
    private String customerId;
    private String type;
    private int page;
    private int size;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
