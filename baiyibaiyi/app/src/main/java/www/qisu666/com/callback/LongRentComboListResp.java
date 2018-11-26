package www.qisu666.com.callback;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wujiancheng on 2017/9/21.
 * 短租套餐
 */

public class LongRentComboListResp implements Serializable {
    private String id;//套餐Id
    private String name;//套餐名称
    private String money;//套餐金额
    private String beforeMoney;//套餐原价金额
    private String startTime;//开始时间
    private String endTime;//结束时间
    private int days;//套餐天数
    private List<DiscountInfo> discountInfo;//短租折扣详细信息
    private String systemTime;
    private String giveDays;//赠送金额
    private String isFestival;//是否是春节活动
    private FestivalShareVo festivalShareVo;//春节分享内容

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getBeforeMoney() {
        return beforeMoney;
    }

    public void setBeforeMoney(String beforeMoney) {
        this.beforeMoney = beforeMoney;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public List<DiscountInfo> getDiscountInfo() {
        return discountInfo;
    }

    public void setDiscountInfo(List<DiscountInfo> discountInfo) {
        this.discountInfo = discountInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static class DiscountInfo implements Serializable {
        private String name;//折扣名称
        private String detail;//套餐折扣详情

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDetail() {
            return detail;
        }

        public void setDetail(String detail) {
            this.detail = detail;
        }
    }

    public String getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(String systemTime) {
        this.systemTime = systemTime;
    }

    public String getGiveDays() {
        return giveDays;
    }

    public void setGiveDays(String giveDays) {
        this.giveDays = giveDays;
    }

    public String getIsFestival() {
        return isFestival;
    }

    public void setIsFestival(String isFestival) {
        this.isFestival = isFestival;
    }

    public FestivalShareVo getFestivalShareVo() {
        return festivalShareVo;
    }

    public void setFestivalShareVo(FestivalShareVo festivalShareVo) {
        this.festivalShareVo = festivalShareVo;
    }
    public static class FestivalShareVo implements Serializable {
        private String title;
        private String content;
        private String shareUrl;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getShareUrl() {
            return shareUrl;
        }

        public void setShareUrl(String shareUrl) {
            this.shareUrl = shareUrl;
        }
    }
}
