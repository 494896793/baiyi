package www.qisu666.com.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;
import www.qisu666.com.R;
import www.qisu666.com.adapter.SearchMapPointAdapter;
import www.qisu666.com.adapter.SearchParkAdapter;
import www.qisu666.com.app.MyApplication;
import www.qisu666.com.callback.ParksResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.map.search.SearchAddrManager;
import www.qisu666.com.request.FrequentParksRequest;
import www.qisu666.com.request.ParkPointListRequest;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.InstanceUtils;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.StringUtils;
import www.qisu666.com.utils.TVUtils;
import www.qisu666.com.utils.UserUtils;
import www.qisu666.com.view.NestedListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by wujiancheng on 2017/7/22.
 * 搜索网点
 */

public class SearchParkActivity extends BaseActivity {
    private static final int CHOOSE_CITY = 1;
    private static final int SEARCH_PARK_LIST = 1;
    private static final int QUERY_FREQUENT_PARK_LIST = 2;//常用网点
    @BindView(R.id.tvTitleRight)
    TextView tvTitleRight;//城市
    @BindView(R.id.etSearchKeyWord)
    EditText etSearchKeyWord;
    @BindView(R.id.lvNearParkResult)
    NestedListView lvNearParkResult;
    @BindView(R.id.llytNearParkList)
    LinearLayout llytNearParkList;
    @BindView(R.id.tvNoFrequentTip)
    TextView tvNoFrequentTip;
    @BindView(R.id.lvSearchMapList)
    NestedListView lvSearchMapList;
    @BindView(R.id.tvNearParkNumber)
    TextView tvNearParkNumber;
    @BindView(R.id.tvCurrentAddr)
    TextView tvCurrentAddr;
    @BindView(R.id.llytCurrentAddrText)
    LinearLayout llytCurrentAddrText;
    @BindView(R.id.tvCurrentAddrText)
    TextView tvCurrentAddrText;

    private int from = 0;//默认从主页进来搜索

    private SearchParkAdapter mNearParkAdapter;
    private SearchMapPointAdapter mSearchMapPointAdapter;
    private List<ParksResp> parksResps = new ArrayList<>();//网点列表
    private List<ParksResp> parkInfoDataNearParks = new ArrayList<>();//最近10个网点
    private List<Map<String, String>> searchMapPoints = new ArrayList<>();//搜索地图出来的点列表
    private String longtitude;//搜索的经度
    private String latitude;//搜索的纬度
    private String searchKeyWord = "";//搜索的关键字
    private String searchCityCode = Config.DEFAULT_CITY_CODE;//深圳的区号

    private String mySearchAddress;
    private String cityCode = Config.DEFAULT_CITY_CODE;

    @Override
    public void setView() {
        setContentView(R.layout.activity_search_park);
    }

    @Override
    public void initDatas() {
        mNearParkAdapter = new SearchParkAdapter(this, parkInfoDataNearParks);
        lvNearParkResult.setAdapter(mNearParkAdapter);
        mSearchMapPointAdapter = new SearchMapPointAdapter(this, searchMapPoints);
        lvSearchMapList.setAdapter(mSearchMapPointAdapter);

        String address = CacheUtils.getIn().getStr(CacheUtils.curLocationSite);
        tvCurrentAddr.setText(address);
        /**
         * 获取网点全部数据列表
         */
        cityCode = UserUtils.getCityCode();
        getParkInfo();

        searchNearby();
        setOnItemClickListener();
        //记录来自哪个页面的搜索，默认主页
        from = getIntent().getIntExtra("from", 0);
        //常用网点
        if (from == RxEventCodes.CODE_SEARCH_PARK_FROM_PRE_ORDER_HOUR_RENT
                || from == RxEventCodes.CODE_SEARCH_PARK_FROM_USE_CAR) {
            getFrequentParkInfo();
            etSearchKeyWord.setHint("您要去哪儿");
        }

        observeRxEventCode();
    }

    private void observeRxEventCode() {
        busSubscription = RxBus.getInstance()
                .toObservable(RxBusEvent.class)
                .subscribe(new Action1<RxBusEvent>() {
                    @Override
                    public void call(RxBusEvent rxBusEvent) {
                        int eventCode = rxBusEvent.getEventCode();
                        switch (eventCode) {
                            case RxEventCodes.CODE_SEARCH_PARK://搜索网点
                            case RxEventCodes.CODE_SEARCH_PARK_FROM_PRE_ORDER_HOUR_RENT://来自时租预约前的页面
                            case RxEventCodes.CODE_SEARCH_PARK_FROM_USE_CAR://来自用车页面
                                finish();
                                break;
                        }
                    }
                });
    }

    @Override
    public void onComplete(String result, int type) {
        if (isSuccess(result)) {
            if (type == SEARCH_PARK_LIST) {//网点列表
                List<ParksResp> parks = getList(result, ParksResp.class);
                if (parks != null && parks.size() > 0) {
                    parksResps.clear();
                    parksResps.addAll(parks);
                }
                //地图页进来，不是显示常用网点，而是显示附近网点
                if (from != RxEventCodes.CODE_SEARCH_PARK_FROM_PRE_ORDER_HOUR_RENT
                        && from != RxEventCodes.CODE_SEARCH_PARK_FROM_USE_CAR) {
                    lvSearchMapList.setVisibility(View.GONE);
                    mSearchMapPointAdapter.notifyDataSetChanged();

                    llytNearParkList.setVisibility(View.VISIBLE);
                    tvNearParkNumber.setText("附近网点");

                    //当前经纬度
                    longtitude = MyApplication.getApplication().longitude;
                    latitude = MyApplication.getApplication().latitude;
                    searchParkInfo();
                }

            } else if (type == QUERY_FREQUENT_PARK_LIST) {//常用网点
                List<ParksResp> parks = getList(result, ParksResp.class);
                setFrequentParksData(parks);
            }
        } else {
            if (type == SEARCH_PARK_LIST) {//网点列表
                //地图页进来，不是显示常用网点，而是显示附近网点
                if (from != RxEventCodes.CODE_SEARCH_PARK_FROM_PRE_ORDER_HOUR_RENT
                        && from != RxEventCodes.CODE_SEARCH_PARK_FROM_USE_CAR) {
                    llytNearParkList.setVisibility(View.GONE);
                }
            } else if (type == QUERY_FREQUENT_PARK_LIST) {//常用网点
                lvNearParkResult.setVisibility(View.GONE);
                tvNoFrequentTip.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onFailure(String msg, int type) {
        if (type == SEARCH_PARK_LIST && llytNearParkList != null) {//网点列表
            //地图页进来，不是显示常用网点，而是显示附近网点
            if (from != RxEventCodes.CODE_SEARCH_PARK_FROM_PRE_ORDER_HOUR_RENT
                    && from != RxEventCodes.CODE_SEARCH_PARK_FROM_USE_CAR) {
                llytNearParkList.setVisibility(View.GONE);
            }
        } else if (type == QUERY_FREQUENT_PARK_LIST && llytNearParkList != null) {//常用网点
            lvNearParkResult.setVisibility(View.GONE);
            tvNoFrequentTip.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.ivTitleLeft, R.id.llytTitleRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivTitleLeft:
                finish();
                break;
            case R.id.llytTitleRight://列表
//                Intent intent = new Intent();
//                intent.setClass(this, CityListActivity.class);
//                startActivityForResult(intent, CHOOSE_CITY);

                Intent intent = new Intent();
                intent.setClass(this, AreaListActivity.class);
                intent.putExtra("from", from);
                startActivity(intent);

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_CITY:
                if (resultCode == RESULT_OK) {
                    String cityName = data.getStringExtra("cityName");
                    Logger.i("收到的城市 = " + cityName);
                    tvTitleRight.setText(cityName);

                    String cityCodeSelected = data.getStringExtra("cityCode");
                    //切换城市，重新请求网点列表数据
                    if (!cityCode.equals(cityCodeSelected)) {
                        cityCode = cityCodeSelected;
                        parksResps.clear();
                        parkInfoDataNearParks.clear();
                        mNearParkAdapter.notifyDataSetChanged();
                        mSearchMapPointAdapter.notifyDataSetChanged();

                        getParkInfo();
                    }
                }
                break;
        }
    }

    /**
     * 搜索附近的数据
     */
    private void searchNearby() {
        etSearchKeyWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    searchKeyWord = etSearchKeyWord.getText().toString();
                    if (etSearchKeyWord.getSelectionStart() > 0) {
                        searchBranches(searchKeyWord);
                    }

                } else {
                    searchMapPoints.clear();
                    mSearchMapPointAdapter.notifyDataSetChanged();
                    lvSearchMapList.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 搜索网点列表
     */
    private void getParkInfo() {
        ParkPointListRequest data = new ParkPointListRequest();
        data.setCityCode(cityCode);
        data.setMethod(RequestUrls.QUERY_PARK_LIST);
        doGet(data, SEARCH_PARK_LIST, Config.LOADING_STRING, true);
    }

    /**
     * 获取常用的网点
     */
    private void getFrequentParkInfo() {
        String customerId = UserUtils.getCustomerId();
        if (!StringUtils.isEmpty(customerId)) {
            FrequentParksRequest request = new FrequentParksRequest();
            request.setCustomerId(customerId);
            request.setMethod(RequestUrls.QUERY_FREQUENT_PARKS);
            doGet(request, QUERY_FREQUENT_PARK_LIST, Config.LOADING_STRING, true);
        } else {
            lvNearParkResult.setVisibility(View.GONE);
            tvNoFrequentTip.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 网点排序
     *
     * @param
     */
    private void sortParkDataNew(List<ParksResp> parksResps) {
        if (parksResps != null && parksResps.size() > 0) {
            Collections.sort(parksResps, new Comparator<ParksResp>() {
                @Override
                public int compare(ParksResp lhs, ParksResp rhs) {
                    Double d1 = InstanceUtils.insDoubleBc(lhs.getLongitude(),
                            lhs.getLatitude(), Double.parseDouble(longtitude), Double.parseDouble(latitude));
                    if (d1 > 1000) {
//                    BigDecimal bg = new BigDecimal(d1 / 1000.00);
//                    double f1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        lhs.setDistance(TVUtils.toString(d1 / 1000.00) + "KM");
                    } else {
                        lhs.setDistance(TVUtils.toStringInt(d1 + "") + "m");
                    }

                    Double d2 = InstanceUtils.insDoubleBc(rhs.getLongitude(),
                            rhs.getLatitude(), Double.parseDouble(longtitude), Double.parseDouble(latitude));
                    if (d2 > 1000) {
//                        BigDecimal bg2 = new BigDecimal(d2 / 1000.00);
//                        double f2 = bg2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        rhs.setDistance(TVUtils.toString(d2 / 1000.00) + "KM");
                    } else {
                        rhs.setDistance(TVUtils.toStringInt(d2 + "") + "m");
                    }
                    return d1.compareTo(d2);
                }
            });
        }
    }

    /**
     * 高德搜索
     */
    public void searchBranches(String searchContent) {
        SearchAddrManager.Companion.searchAddrByCityCode(mContext, searchContent, searchCityCode, new Inputtips.InputtipsListener() {
            @Override
            public void onGetInputtips(List<Tip> tipList, int rCode) {
                if (rCode == 1000) {
                    if (null != tipList && tipList.size() > 0) {

                        List<Map<String, String>> tmp = new ArrayList<>();

                        for (int i = 0; i < tipList.size(); i++) {
                            if (i > 9) {
                                break;
                            }
                            Tip tip = tipList.get(i);
                            if (tip != null && tip.getPoint() != null) {
                                Map<String, String> searchDataMap = new HashMap<>();
                                searchDataMap.put("lat", String.valueOf(tip.getPoint().getLatitude()));
                                searchDataMap.put("long", String.valueOf(tip.getPoint().getLongitude()));
                                searchDataMap.put("addressName", tip.getName());
                                searchDataMap.put("district", tip.getDistrict());
                                searchDataMap.put("addressDetail", tip.getAddress());
                                tmp.add(searchDataMap);
//                        searchContent();
                            }
                        }
                        llytCurrentAddrText.setVisibility(View.GONE);
                        tvNoFrequentTip.setVisibility(View.GONE);

                        tvCurrentAddrText.setVisibility(View.GONE);
                        tvNearParkNumber.setText("附近网点(以下距离为搜索地点到网点距离)");

                        llytNearParkList.setVisibility(View.GONE);

                        searchMapPoints.clear();
                        searchMapPoints.addAll(tmp);
                        mSearchMapPointAdapter.setKeyWord(searchKeyWord);
                        mSearchMapPointAdapter.notifyDataSetChanged();
                        lvSearchMapList.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void searchContent(Map<String, String> searchDataMap) {

        mySearchAddress = searchDataMap.get("addressName");
        etSearchKeyWord.setText(mySearchAddress);
        etSearchKeyWord.setSelection(mySearchAddress.length());

        latitude = searchDataMap.get("lat");
        longtitude = searchDataMap.get("long");
        Logger.i("高德地图搜索的经纬度==" + latitude + ",," + longtitude);
        CacheUtils.getIn().saveStr(CacheUtils.Searchlatitude, latitude);
        CacheUtils.getIn().saveStr(CacheUtils.Searchlongitude, longtitude);
        CacheUtils.getIn().saveStr(CacheUtils.searchaddr, mySearchAddress);
        searchParkInfo();
    }

    /**
     * 附近的10个点
     */
    private void searchParkInfo() {
        if (parksResps != null) {
            parkInfoDataNearParks.clear();
            List<ParksResp> tmp = new ArrayList<>();
            tmp.addAll(parksResps);
            sortParkDataNew(tmp);
            if (tmp.size() > 10) {
                for (int i = 0; i < 10; i++) {
                    parkInfoDataNearParks.add(tmp.get(i));
                }
            } else {
                parkInfoDataNearParks.addAll(tmp);
            }
            llytNearParkList.setVisibility(View.VISIBLE);
            lvNearParkResult.setVisibility(View.VISIBLE);
            tvNoFrequentTip.setVisibility(View.GONE);
            //更新附近网点列表数据
            mNearParkAdapter.setData(parkInfoDataNearParks);
        }
    }

    /**
     * 常用的网点
     */
    private void setFrequentParksData(List<ParksResp> parks) {
        //当前经纬度
        longtitude = MyApplication.getApplication().longitude;
        latitude = MyApplication.getApplication().latitude;
        //地图搜索的列表隐藏
        lvSearchMapList.setVisibility(View.GONE);
//                mSearchMapPointAdapter.notifyDataSetChanged();
        //常用网点列表显示
        tvNearParkNumber.setText("常用网点");

        if (parks != null && parks.size() > 0) {
            parkInfoDataNearParks.clear();
            if (parks.size() > 5) {
                for (int i = 0; i < 5; i++) {
                    parkInfoDataNearParks.add(parks.get(i));
                }
            } else {
                parkInfoDataNearParks.addAll(parks);
            }
            llytNearParkList.setVisibility(View.VISIBLE);
            lvNearParkResult.setVisibility(View.VISIBLE);
            tvNoFrequentTip.setVisibility(View.GONE);
            //更新附近网点列表数据
            mNearParkAdapter.setData(parkInfoDataNearParks);
        } else {
            lvNearParkResult.setVisibility(View.GONE);
            tvNoFrequentTip.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置列表的点击事件
     */
    private void setOnItemClickListener() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (null != inputMethodManager) {
            inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }

        //关键字搜索出来的列表的点击事件,点击后以当前点击的位置的经纬度，搜索附近的网点
        lvSearchMapList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> searchDataMap = searchMapPoints.get(position);
                searchContent(searchDataMap);

//                llytNearParkList.setVisibility(View.VISIBLE);
                lvSearchMapList.setVisibility(View.GONE);
            }
        });
        //点击附近的网点列表
        lvNearParkResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClick(parkInfoDataNearParks, position);
            }
        });
    }

    /**
     * 点击列表后将数据传到首页地图
     */
    private void itemClick(List<ParksResp> parksResps, int position) {
        if (position < parksResps.size()) {
            ParksResp parksResp = parksResps.get(position);
            if (null != parksResp) {
                RxBusEvent event = new RxBusEvent();
                if (from == 0) {//从主页进入
                    event.setEventCode(RxEventCodes.CODE_SEARCH_PARK);
                } else if (from == RxEventCodes.CODE_SEARCH_PARK_FROM_PRE_ORDER_HOUR_RENT) {//来自时租预约前的页面
                    event.setEventCode(RxEventCodes.CODE_SEARCH_PARK_FROM_PRE_ORDER_HOUR_RENT);
                } else if (from == RxEventCodes.CODE_SEARCH_PARK_FROM_USE_CAR) {//来自用车页面
                    event.setEventCode(RxEventCodes.CODE_SEARCH_PARK_FROM_USE_CAR);
                }

                event.setContent(parksResp);
                RxBus.getInstance().post(event);
            }
        }
    }
}
