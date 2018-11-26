package www.qisu666.com.request;

public class OrderListRequest extends RequestBaseParams {
    private String customerId;
    private String page;
    private String size;

    public OrderListRequest(String page, String size) {
        this.page = page;
        this.size = size;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
