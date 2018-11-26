package www.qisu666.com.callback;


import com.amap.api.maps.model.LatLng;

import java.io.Serializable;

public class CarParkResp implements Serializable {
    private CarResp carBaseInfo; // 车辆信息
    private ParkResp parkBaseinfo;// 停车场信息
    private String orderType;//订单类型
    private LongRentRecoverResp userResurgenceRentOrderInfo;//短租基本恢复信息
    private LongRentRecoverComboResp packageInfo;//短租套餐恢复信息
    private BeforeHandPark beforehandPark;//还车点

    public CarResp getCarBaseInfo() {
        return carBaseInfo;
    }

    public void setCarBaseInfo(CarResp carBaseInfo) {
        this.carBaseInfo = carBaseInfo;
    }

    public ParkResp getParkBaseinfo() {
        return parkBaseinfo;
    }

    public void setParkBaseinfo(ParkResp parkBaseinfo) {
        this.parkBaseinfo = parkBaseinfo;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public LongRentRecoverResp getUserResurgenceRentOrderInfo() {
        return userResurgenceRentOrderInfo;
    }

    public void setUserResurgenceRentOrderInfo(LongRentRecoverResp userResurgenceRentOrderInfo) {
        this.userResurgenceRentOrderInfo = userResurgenceRentOrderInfo;
    }

    public LongRentRecoverComboResp getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(LongRentRecoverComboResp packageInfo) {
        this.packageInfo = packageInfo;
    }

    public BeforeHandPark getBeforehandPark() {
        return beforehandPark;
    }

    public void setBeforehandPark(BeforeHandPark beforehandPark) {
        this.beforehandPark = beforehandPark;
    }

    public static class BeforeHandPark implements Serializable {
        private int id;//: 468,
        private String parkName;//: "西丽体育",
        private String parkAddress;//: "广东省深圳市南山区"
        private String latitude;//: "22.5733460000",
        private String longitude;//: "113.9563090000",

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getParkName() {
            return parkName;
        }

        public void setParkName(String parkName) {
            this.parkName = parkName;
        }

        public String getParkAddress() {
            return parkAddress;
        }

        public void setParkAddress(String parkAddress) {
            this.parkAddress = parkAddress;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public LatLng getLatlng() {
            return new LatLng(Double.parseDouble(latitude),
                    Double.parseDouble(longitude));
        }
    }
}
