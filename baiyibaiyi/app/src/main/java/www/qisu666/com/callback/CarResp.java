package www.qisu666.com.callback;

import java.io.Serializable;

/**
 * 复活车辆信息
 */
public class CarResp implements Serializable {
    private float batteryResidual;// 车辆剩余电量
    private String canUseMileage;
    private String carBrand;// 车辆品牌
    private String carColor;// 车辆颜色
    private String carImgUri;//车辆图片
    private String smallCarImgUri;//车辆图片
    private String carSetsNums;//车辆座位数
    private String milesMoney;// 每公里单价
    private String timeMoney;// 每小时单价
    private String longitude;// 经度
    private String latitude;// 纬度
    private String carNumber;// 车牌
    private float mileage = 0;// 里程
    private String models;//车型
    private String timeUnit;// 时间单位
    private String parkId;//车辆所在停车场
    private String modelsId;// 车型代码
    private String carSharingOrderNo;// 订单号
    private String cityCode;//城市编号
    private String systemTime;//系统时间
    private String orderTime;//订单时间
    private int electricityMoney; //电度费
    private int bootType;

    public int getBootType() {
        return bootType;
    }

    public void setBootType(int bootType) {
        this.bootType = bootType;
    }

    //夜间开始时间：
    private String nightBeginRateHour;

    public int getElectricityMoney() {
        return electricityMoney;
    }

    public void setElectricityMoney(int electricityMoney) {
        this.electricityMoney = electricityMoney;
    }

    //夜间结束时间
    private String nightEndRateHour;
    //夜间时长计费
    private int nightTimeUnit;
    //夜间里程计费：
    private int nightMilesUnit;
    //不计免赔价格：
    private int insuranceMoney;
    //不计免赔封顶：
    private int maxInsuranceMoney;
    //是否是红包车(1 是，0 不是)
    private int isRedPkCar;

    public float getBatteryResidual() {
        return batteryResidual;
    }

    public void setBatteryResidual(float batteryResidual) {
        this.batteryResidual = batteryResidual;
    }

    public String getCanUseMileage() {
        return canUseMileage;
    }

    public void setCanUseMileage(String canUseMileage) {
        this.canUseMileage = canUseMileage;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public String getCarImgUri() {
        return carImgUri;
    }

    public void setCarImgUri(String carImgUri) {
        this.carImgUri = carImgUri;
    }

    public String getCarSetsNums() {
        return carSetsNums;
    }

    public void setCarSetsNums(String carSetsNums) {
        this.carSetsNums = carSetsNums;
    }

    public String getMilesMoney() {
        return milesMoney;
    }

    public void setMilesMoney(String milesMoney) {
        this.milesMoney = milesMoney;
    }

    public String getTimeMoney() {
        return timeMoney;
    }

    public void setTimeMoney(String timeMoney) {
        this.timeMoney = timeMoney;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public float getMileage() {
        return mileage;
    }

    public void setMileage(float mileage) {
        this.mileage = mileage;
    }

    public String getModels() {
        return models;
    }

    public void setModels(String models) {
        this.models = models;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getParkId() {
        return parkId;
    }

    public void setParkId(String parkId) {
        this.parkId = parkId;
    }

    public String getModelsId() {
        return modelsId;
    }

    public void setModelsId(String modelsId) {
        this.modelsId = modelsId;
    }

    public String getCarSharingOrderNo() {
        return carSharingOrderNo;
    }

    public void setCarSharingOrderNo(String carSharingOrderNo) {
        this.carSharingOrderNo = carSharingOrderNo;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(String systemTime) {
        this.systemTime = systemTime;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getSmallCarImgUri() {
        return smallCarImgUri;
    }

    public void setSmallCarImgUri(String smallCarImgUri) {
        this.smallCarImgUri = smallCarImgUri;
    }

    public String getNightBeginRateHour() {
        return nightBeginRateHour;
    }

    public void setNightBeginRateHour(String nightBeginRateHour) {
        this.nightBeginRateHour = nightBeginRateHour;
    }

    public String getNightEndRateHour() {
        return nightEndRateHour;
    }

    public void setNightEndRateHour(String nightEndRateHour) {
        this.nightEndRateHour = nightEndRateHour;
    }

    public int getNightTimeUnit() {
        return nightTimeUnit;
    }

    public void setNightTimeUnit(int nightTimeUnit) {
        this.nightTimeUnit = nightTimeUnit;
    }

    public int getNightMilesUnit() {
        return nightMilesUnit;
    }

    public void setNightMilesUnit(int nightMilesUnit) {
        this.nightMilesUnit = nightMilesUnit;
    }

    public int getInsuranceMoney() {
        return insuranceMoney;
    }

    public void setInsuranceMoney(int insuranceMoney) {
        this.insuranceMoney = insuranceMoney;
    }

    public int getMaxInsuranceMoney() {
        return maxInsuranceMoney;
    }

    public void setMaxInsuranceMoney(int maxInsuranceMoney) {
        this.maxInsuranceMoney = maxInsuranceMoney;
    }

    public int getIsRedPkCar() {
        return isRedPkCar;
    }

    public void setIsRedPkCar(int isRedPkCar) {
        this.isRedPkCar = isRedPkCar;
    }
}
