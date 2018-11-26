package www.qisu666.com.callback;

import java.util.List;

public class RechargeBalanceResp {
    private String title;
    private String isStart;
    private List<RechargeGiftDetail> rechargeGiftDetail;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsStart() {
        return isStart;
    }

    public void setIsStart(String isStart) {
        this.isStart = isStart;
    }

    public List<RechargeGiftDetail> getRechargeGiftDetail() {
        return rechargeGiftDetail;
    }

    public void setRechargeGiftDetail(List<RechargeGiftDetail> rechargeGiftDetail) {
        this.rechargeGiftDetail = rechargeGiftDetail;
    }

    public static class RechargeGiftDetail {
        private int rechargeMoneyMin;// 充值金额最小上限
        private int rechargeMoneyMax;// 充值金额最大上限
        private int giftMoney;// 赠送金额

        public RechargeGiftDetail() {
        }

        public RechargeGiftDetail(int rechargeMoneyMin, int rechargeMoneyMax, int giftMoney) {
            this.rechargeMoneyMin = rechargeMoneyMin;
            this.rechargeMoneyMax = rechargeMoneyMax;
            this.giftMoney = giftMoney;
        }

        public int getRechargeMoneyMin() {
            return rechargeMoneyMin;
        }

        public void setRechargeMoneyMin(int rechargeMoneyMin) {
            this.rechargeMoneyMin = rechargeMoneyMin;
        }

        public int getRechargeMoneyMax() {
            return rechargeMoneyMax;
        }

        public void setRechargeMoneyMax(int rechargeMoneyMax) {
            this.rechargeMoneyMax = rechargeMoneyMax;
        }

        public int getGiftMoney() {
            return giftMoney;
        }

        public void setGiftMoney(int giftMoney) {
            this.giftMoney = giftMoney;
        }
    }
}
