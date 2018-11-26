package www.qisu666.com.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import www.qisu666.com.R;
import www.qisu666.com.adapter.CarListAdapter;
import www.qisu666.com.callback.CarResp;
import www.qisu666.com.callback.ParksResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.request.GetCarListRequest;
import www.qisu666.com.utils.NavigationUtils;
import www.qisu666.com.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by wujiancheng on 2017/7/30.
 * 指定网点中的车辆列表
 */

public class CarListActivity extends BaseActivity {
    private static final int CAR_LIST = 0;
    public static final int REQUEST_CODE_ORDER = 1;
    @BindView(R.id.tvTitleName)
    TextView tvTitleName;
    @BindView(R.id.tvParkName)
    TextView tvParkName;
    @BindView(R.id.tvParkNamedDetail)
    TextView tvParkNamedDetail;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.lvCarList)
    ListView lvCarList;

    private ParksResp parksResp;
    private CarListAdapter carListAdapter;
    private List<CarResp> mData = new ArrayList<>();

    @Override
    public void setView() {
        setContentView(R.layout.activity_car_list);
    }

    @Override
    public void initDatas() {
        Intent intent = getIntent();
        parksResp = (ParksResp) intent.getSerializableExtra("parksResp");
        carListAdapter = new CarListAdapter(this, mData);
        carListAdapter.setParkInfo(parksResp);
        lvCarList.setAdapter(carListAdapter);

        if (null != parksResp) {
            tvTitleName.setText(parksResp.getParkName());
            tvParkName.setText(parksResp.getParkName());
            tvParkNamedDetail.setText(parksResp.getParkAddress());
            tvDistance.setText(parksResp.getDistance());
        }

        getCarList();
    }

    @Override
    public void onComplete(String result, int type) {
        if (isSuccess(result)) {
            if (type == CAR_LIST) {
                List<CarResp> list = getList(result, CarResp.class);
                if (null != list) {
                    mData.clear();
                    mData.addAll(list);
                    carListAdapter.notifyDataSetChanged();
                }
            }

        }
    }

    @Override
    public void onFailure(String msg, int type) {

    }

    @OnClick({R.id.ivTitleLeft, R.id.llytNavigate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivTitleLeft:
                finish();
                break;
            case R.id.llytNavigate://导航
                if (null != parksResp) {
                    String lat = parksResp.getLatitude();
                    String lng = parksResp.getLongitude();
                    if (StringUtils.isIntOrFloat(lat) && StringUtils.isIntOrFloat(lng)) {
                        LatLng latLng = new LatLng(Double.valueOf(lat), Double.valueOf(lng));
                        NavigationUtils.goNavigation(CarListActivity.this, latLng, 2);
                    }
                }
                break;
        }
    }

    /**
     * 获取车辆列表
     */
    private void getCarList() {
        GetCarListRequest data = new GetCarListRequest();
        data.setDepotId(parksResp.getId());
        data.setMethod(RequestUrls.QUERY_CAR_LIST_BY_PARK_ID);
        doGet(data, CAR_LIST, Config.LOADING_STRING, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                if (requestCode == REQUEST_CODE_ORDER) {
                    //确认用车后，车辆列表关闭
                    finish();
                }
                break;
        }
    }
}
