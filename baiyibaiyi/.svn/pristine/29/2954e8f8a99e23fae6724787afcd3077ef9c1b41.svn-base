package www.qisu666.com.request;

import www.qisu666.com.callback.UserInfoResp;
import www.qisu666.com.utils.CacheUtils;

/**
 * Created by wujiancheng on 2017/9/7.
 */

public class SystemArgumentRequest extends RequestBaseParams {

    private String customerId;
    private String addressType;

    public SystemArgumentRequest() {
        UserInfoResp userInfoResp = CacheUtils.getIn().getUserInfo();
        if (null != userInfoResp) {
            customerId = userInfoResp.getId();
        }
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }
}
