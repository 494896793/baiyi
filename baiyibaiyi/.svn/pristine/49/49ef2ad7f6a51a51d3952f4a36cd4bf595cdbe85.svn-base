package www.qisu666.com.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import www.qisu666.com.R;
import www.qisu666.com.adapter.ChargingStatisticsAdapter;
import www.qisu666.com.app.MyApplication;
import www.qisu666.com.bean.ChongdianTongji;
import www.qisu666.com.bean.EventMsg;
import www.qisu666.com.bean.RxBus;
import www.qisu666.com.event.LoginEvent;
import www.qisu666.com.event.Message;
import www.qisu666.com.request.MyNetwork;
import www.qisu666.com.request.utils.FlatFunction;
import www.qisu666.com.request.utils.MyMessageUtils;
import www.qisu666.com.request.utils.ResultSubscriber;
import www.qisu666.com.request.utils.RxNetHelper;

import www.qisu666.com.sdk.stationMap.JsonUtil;
import www.qisu666.com.sdk.stationMap.StationLocation;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.JsonUtils;
import www.qisu666.com.utils.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


/**
 * A simple {@link Fragment} subclass.
 * 充电统计
 */
public class ChargingStatisticsFragment extends BaseFragment {

    private View view;

    private PullToRefreshListView pull_refresh_load_recycler_view;
    private ChargingStatisticsAdapter adapter;
    private List<ChongdianTongji.MyOrderList> mLists = new ArrayList<>();

    //当前数据分页
    private int currentPage = 1;
    //每页数据条数
    private static final int PAGE_NUM = 10;

    private boolean requested = false;

    private ImageView ivGG;

    public ChargingStatisticsFragment() {
        // Required empty public constructor
    }

    @Subscribe
    public void onEventMainThread(LoginEvent event) {

        connToServer();
    }


    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_charging_statistics, container, false);
        pull_refresh_load_recycler_view = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_load_recycler_view);
        ivGG = view.findViewById(R.id.iv_gg);
        if (CacheUtils.getIn().getUserInfo()!=null&&CacheUtils.getIn().getUserInfo().getId()!=null) {
            connToServer();
        }

        RxBus.getInstance().toObservable().map(new Function<Object, EventMsg>() {
            @Override
            public EventMsg apply(Object o) throws Exception {
                return (EventMsg) o;
            }
        }).subscribe(new Consumer<EventMsg>() {
            @Override
            public void accept(EventMsg eventMsg) throws Exception {
                if (eventMsg != null) {
                    if (eventMsg.getType() == 1) {
                        connToServer();
                    } else if (eventMsg.getType() == 2) {
                        pull_refresh_load_recycler_view.setVisibility(View.GONE);
                        ivGG.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //设置模式
        pull_refresh_load_recycler_view.setMode(PullToRefreshBase.Mode.BOTH);
        pull_refresh_load_recycler_view.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            //下拉刷新时会回调的方法
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                currentPage = 1;
                connToServer();
            }

            //上啦加载时执行的方法
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                currentPage++;
                connToServer();
            }
        });
    }

    /**
     * 发送 C102 请求，获取充电统计
     */
    private void connToServer() {

        String url = "api/charge/records/query";
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", CacheUtils.getIn().getUserInfo().getId());    //
        map.put("orderType", "1");
        map.put("optType", "2");
        map.put("pageIndex", String.valueOf(PAGE_NUM));
        map.put("pageNum", String.valueOf(currentPage));

        MyNetwork.getMyApi()
                .carRequest(url, MyMessageUtils.addBody(map))
                .map(new FlatFunction<>(Object.class))
                .compose(RxNetHelper.<Object>io_main())
                .subscribe(new ResultSubscriber<Object>() {
                    @Override
                    public void onSuccessCode(Message object) {

                    }

                    @Override
                    public void onSuccess(Object object) {
                        ChongdianTongji bean=new ChongdianTongji();
                        List<ChongdianTongji.MyOrderList> orderList=new ArrayList<>();
                        String s = JsonUtils.objectToJson(object);
                        List<String> stringlist = JsonUtils.jsonToList(s);
                        try {
                            JSONArray array = new JSONArray(stringlist.toString());
                            int count = array.length();
                            LogUtil.e("aaa" + "返回结果-----数据大小----" + count);
                            if (count == 0) { //没有数据
                                LogUtil.e("aaaa" + "返回结果-----没有数据");
                            } else {
                                LogUtil.e("aaaa" + "返回结果-----有数据----");
                                for (int i = 0; i < count; i++) {
                                    JSONObject object2 = array.getJSONObject(i);
                                    String tongJi=object2.optString("nameValuePairs");
                                    ChongdianTongji.MyOrderList stationLocation = JsonUtil.parse(tongJi, ChongdianTongji.MyOrderList.class);
                                    orderList.add(stationLocation);
//                            list.add(JsonUtils.jsonToMap(oString));//添加桩子
                                }
                            }
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                        bean.setMyOrderList(orderList);


                        pull_refresh_load_recycler_view.onRefreshComplete();
                        mLoadingDialog.dismiss();
                        try {
                            if (bean.getMyOrderList().size() == 0) {
//                                ivGG.setVisibility(View.VISIBLE);
//                                pull_refresh_load_recycler_view.setVisibility(View.GONE);
                                return;
                            } else {
                                ivGG.setVisibility(View.GONE);
                                pull_refresh_load_recycler_view.setVisibility(View.VISIBLE);
                            }
                        } catch (Throwable t) {
                            t.printStackTrace();
                            ToastUtil.showToast("已全部加载！");
                            return;
                        }
                        if (currentPage == 1) {
                            mLists = bean.getMyOrderList();
                            adapter = new ChargingStatisticsAdapter(getActivity(), mLists);
                            pull_refresh_load_recycler_view.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            mLists.addAll(bean.getMyOrderList());
                            adapter.setList(mLists);
                        }
                    }

                    @Override
                    public void onFail(Message<Object> bean) {
                        try {
                            pull_refresh_load_recycler_view.onRefreshComplete();
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                });


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !requested) {
            connToServer();
            requested = true;
        }
    }

    @Override
    public int setLayoutResId() {
        return 0;
    }

    @Override
    public void initDatas(View view) {

    }

    @Override
    public void onComplete(String result, int type) {

    }

    @Override
    public void onFailure(String msg, int type) {

    }
}
