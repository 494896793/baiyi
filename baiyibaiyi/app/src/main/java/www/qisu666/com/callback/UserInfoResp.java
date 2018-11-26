package www.qisu666.com.callback;

import java.io.Serializable;

public class UserInfoResp implements Serializable {

    private String id;
    private String phone;
    private String name;
    private String sex;
    private String idcard;
    private String driverNumber;
    private String email;
    private String portraitUrl;//头像URL
    private String driverNumberurl;//驾驶证
    private String idcardUrl;//身份证
    private String handCardUrl;//手持身份证
    private String status;
    private String moneyStatus;
    private String balance;
    private Integer money;//充值余额
    private Integer giftMoney;//赠送余额
    private String deposit;
    private String shouldDeposit;//应交押金额度
    private String couponsNums;
    private String description;
    private String companyId;
    private String token = "";
    private int driverNumberUpdate;
    private int isNewCustomer;//是否为新用户
    private String unDoWzCount;//违章数量

    private Company company;
    private String quota;//": 100,                //个人初始额度
    private String quotaRemain;//": 12            //个人剩余额度
    private String lastRefundStatus;//最新退款状态
    /**
     * 用户最新的订单号
     */
    private String out_trade_no;

    public String getId() {
        return id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getDriverNumber() {
        return driverNumber;
    }

    public void setDriverNumber(String driverNumber) {
        this.driverNumber = driverNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public void setPortraitUrl(String portraitUrl) {
        this.portraitUrl = portraitUrl;
    }

    public String getDriverNumberurl() {
        return driverNumberurl;
    }

    public void setDriverNumberurl(String driverNumberurl) {
        this.driverNumberurl = driverNumberurl;
    }

    public String getIdcardUrl() {
        return idcardUrl;
    }

    public void setIdcardUrl(String idcardUrl) {
        this.idcardUrl = idcardUrl;
    }

    public String getHandCardUrl() {
        return handCardUrl;
    }

    public void setHandCardUrl(String handCardUrl) {
        this.handCardUrl = handCardUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMoneyStatus() {
        return moneyStatus;
    }

    public void setMoneyStatus(String moneyStatus) {
        this.moneyStatus = moneyStatus;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public Integer getMoney() {
        if (money == null){
            return 0;
        }
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getGiftMoney() {
        if (giftMoney == null){
            return 0;
        }
        return giftMoney;
    }

    public void setGiftMoney(Integer giftMoney) {
        this.giftMoney = giftMoney;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getCouponsNums() {
        return couponsNums;
    }

    public void setCouponsNums(String couponsNums) {
        this.couponsNums = couponsNums;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getDriverNumberUpdate() {
        return driverNumberUpdate;
    }

    public void setDriverNumberUpdate(int driverNumberUpdate) {
        this.driverNumberUpdate = driverNumberUpdate;
    }

    public int getIsNewCustomer() {
        return isNewCustomer;
    }

    public void setIsNewCustomer(int isNewCustomer) {
        this.isNewCustomer = isNewCustomer;
    }

    public String getUnDoWzCount() {
        return unDoWzCount;
    }

    public void setUnDoWzCount(String unDoWzCount) {
        this.unDoWzCount = unDoWzCount;
    }

    public class Company {
        private String companyName;//": "宝岗能源", //企业名称
        private int discount;//折扣
        private String id;//": 1,                   //企业id
        private String status;//": 1                //企业状态  1 正常 其他 删除
        private String discountLimitPerson;//1：全部支付都有企业折扣，0：只有企业支付有企业折扣

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getDiscount() {
            return discount;
        }

        public void setDiscount(int discount) {
            this.discount = discount;
        }

        public String getDiscountLimitPerson() {
            return discountLimitPerson;
        }

        public void setDiscountLimitPerson(String discountLimitPerson) {
            this.discountLimitPerson = discountLimitPerson;
        }
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public String getQuotaRemain() {
        return quotaRemain;
    }

    public void setQuotaRemain(String quotaRemain) {
        this.quotaRemain = quotaRemain;
    }

    public String getShouldDeposit() {
        return shouldDeposit;
    }

    public void setShouldDeposit(String shouldDeposit) {
        this.shouldDeposit = shouldDeposit;
    }

    public String getLastRefundStatus() {
        return lastRefundStatus;
    }

    public void setLastRefundStatus(String lastRefundStatus) {
        this.lastRefundStatus = lastRefundStatus;
    }
}
