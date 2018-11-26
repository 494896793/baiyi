package www.qisu666.com.callback;

import java.io.Serializable;
import java.util.List;

/**
 * 优惠券选择列表
 */
public class CouponListChooseResp implements Serializable {
    private List<CouponBean> canUseList;
    private List<CouponBean> notCanUseList;

    public List<CouponBean> getCanUseList() {
        return canUseList;
    }

    public void setCanUseList(List<CouponBean> canUseList) {
        this.canUseList = canUseList;
    }

    public List<CouponBean> getNotCanUseList() {
        return notCanUseList;
    }

    public void setNotCanUseList(List<CouponBean> notCanUseList) {
        this.notCanUseList = notCanUseList;
    }
}
