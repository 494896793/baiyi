package www.qisu666.com.callback;

import java.util.List;

/**
 * Created by wujiancheng on 2017/8/1.
 * 网点列表
 */

public class ParkListResp {
    private List<CarResp> carInfoList;
    private ParkResp depot;

    public List<CarResp> getCarInfoList() {
        return carInfoList;
    }

    public void setCarInfoList(List<CarResp> carInfoList) {
        this.carInfoList = carInfoList;
    }

    public ParkResp getDepot() {
        return depot;
    }

    public void setDepot(ParkResp depot) {
        this.depot = depot;
    }
}
