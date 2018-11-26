package www.qisu666.com.request;

public class ShareContentRequest extends RequestBaseParams {
    private String shareType;

    public ShareContentRequest(){}

    public ShareContentRequest(String shareType) {
        this.shareType = shareType;
    }

    public String getShareType() {
        return shareType;
    }

    public void setShareType(String shareType) {
        this.shareType = shareType;
    }
}
