package www.qisu666.com.request;

public class MoneyDetailRequest extends RequestBaseParams {
    private String category;//类别押金
    private String customerId;//会员ID
    private int page;//页码，从1开始
    private int size;//每页显示记录数

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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
