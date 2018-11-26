package www.qisu666.com.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.adapter.CityListAdapter;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.callback.CityListResp;
import www.qisu666.com.request.CityListRequest;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by wujiancheng on 2017/7/22.
 * 城市列表
 */

public class CityListActivity extends BaseActivity {
    @BindView(R.id.tvTitleName)
    TextView tvTitleName;
    @BindView(R.id.lvCities)
    ListView lvCities;
    private static int CITY_LIST = 1;
    private List<CityListResp> cityInfo = new ArrayList<>();
//    private String searchCityCode;
    private int positionNow = 0;
    private int cityPosition = -1;
    private CityListAdapter mCityListAdapter;
    private List<String> mData = new ArrayList<>();

    @Override
    public void setView() {
        setContentView(R.layout.activity_city_list);
    }

    @Override
    public void initDatas() {
        mCityListAdapter = new CityListAdapter(mContext, mData);
        lvCities.setAdapter(mCityListAdapter);
        tvTitleName.setText("选择城市");
        queryCityList();
    }

    @Override
    public void onComplete(String result, int type) {
        if (type == CITY_LIST) {
            List<CityListResp> citys = getList(result, CityListResp.class);
            if (citys != null && citys.size() > 0) {
                if (cityInfo == null) {
                    cityInfo = new ArrayList<>();
                }
                cityInfo.clear();
                cityInfo.addAll(citys);
                setCityList(cityInfo);
            }
        }
    }

    @Override
    public void onFailure(String msg, int type) {

    }

    @OnClick({R.id.ivTitleLeft})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivTitleLeft:
                finish();
                break;
        }
    }

    /**
     * 获取城市列表
     */
    private void queryCityList() {
        CityListRequest sqc = new CityListRequest();
        sqc.setMethod(RequestUrls.QUERY_CITY_LIST);
        doGet(sqc, CITY_LIST, "", true);
    }

    /**
     * 设置搜索城市
     */
    private void setCityList(final List<CityListResp> cityList) {
        if (this.mData == null) {
            this.mData = new ArrayList<String>();
        } else {
            this.mData.clear();
        }
        if (cityList != null && cityList.size() > 0) {
            for (int i = 0; i < cityList.size(); i++) {
                this.mData.add(cityList.get(i).getName());
            }
            cityPosition = getCityPosition(cityList);
            if (cityPosition == -1) {//没找到当前城市，则添加当前城市
                String nowCity = CacheUtils.getIn().getStr(CacheUtils.curCity);
                if (nowCity != null) {
                    this.mData.add(nowCity);
                }
            }
        }
        //适配器
        if (this.mData != null && this.mData.size() > 0) {
            mCityListAdapter.notifyDataSetChanged();
            lvCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int cityListSize = mData.size();
                    if (cityPosition == -1) {
                        cityListSize = cityListSize - 1;
                    }
                    if (cityListSize > position) {
                        if (cityInfo.size() > position) {
//                            searchCityCode = cityInfo.get(position).getCode();
//                            Logger.i("选择的城市code == " + searchCityCode);

                            CityListResp cityListResp = cityInfo.get(position);
                            if (null == cityListResp) {
                                return;
                            }

                            RxBusEvent event = new RxBusEvent();
                            event.setEventCode(RxEventCodes.CODE_CHOOSE_CITY);
                            event.setContent(cityListResp);
                            RxBus.getInstance().post(event);

                            finish();
                        }
                    } else {
//                        setMyPosition();
                    }
                }
            });

        }
    }

    private int getCityPosition(List<CityListResp> cityList) {
        for (int i = 0; i < cityList.size(); i++) {
            if (UserUtils.getCityCode().equals(cityList.get(i).getCode())) {
                return i;
            }
        }
        return -1;
    }

}
