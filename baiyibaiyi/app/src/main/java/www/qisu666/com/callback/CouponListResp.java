package www.qisu666.com.callback;

import java.util.List;

/**
 * 优惠券展示列表
 */
public class CouponListResp {
    private List<CouponBean> couponBatchVo;
    private String usedNumber;
    private String canUseNumber;
    private String outTimeNumber;

    public List<CouponBean> getCouponBatchVo() {
        return couponBatchVo;
    }

    public void setCouponBatchVo(List<CouponBean> couponBatchVo) {
        this.couponBatchVo = couponBatchVo;
    }

    public String getUsedNumber() {
        return usedNumber;
    }

    public void setUsedNumber(String usedNumber) {
        this.usedNumber = usedNumber;
    }

    public String getCanUseNumber() {
        return canUseNumber;
    }

    public void setCanUseNumber(String canUseNumber) {
        this.canUseNumber = canUseNumber;
    }

    public String getOutTimeNumber() {
        return outTimeNumber;
    }

    public void setOutTimeNumber(String outTimeNumber) {
        this.outTimeNumber = outTimeNumber;
    }
}
