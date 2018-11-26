package www.qisu666.com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.adapter.OrderListAdapter;
import www.qisu666.com.app.CarOrderState;
import www.qisu666.com.callback.OrderListResp;
import www.qisu666.com.callback.UserInfoResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.event.AllorederCloseEvent;
import www.qisu666.com.fragment.BaseFragment;
import www.qisu666.com.receiver.PublicReceiver;
import www.qisu666.com.request.OrderListRequest;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.ResultUtil;
import www.qisu666.com.utils.SBUtils;
import www.qisu666.com.utils.ToastUtil;
import com.liaoinstan.springview.container.DefaultFooter;
import com.liaoinstan.springview.container.DefaultHeader;
import com.liaoinstan.springview.widget.SpringView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 订单列表
 */
public class OrderListActivity extends BaseFragment {
    private static final String TAG = OrderListActivity.class.getSimpleName();
    private static final int REQUEST_CODE_ORDER_DETAIL = 1;

    @BindView(R.id.rlytNoData)
    RelativeLayout rlytNoData;
    @BindView(R.id.springView)
    SpringView springView;
    @BindView(R.id.lvOrderList)
    ListView lvOrderList;
    private List<OrderListResp> mData;
    private OrderListAdapter orderListAdapter;
    private int curPage = 1;
    private boolean isNoData = false;//默认为false
    private OrderListResp orderItem;//选择的订单
    private static int ORDER_LIST = 0;
    private PublicReceiver mCloseReceiver;

    private int toMain = 0;//1:后台还车后跳转到订单列表后，按返回键跳到主页


    private void observeEvent() {
        busSubscription = RxBus.getInstance()
                .toObservable(RxBusEvent.class)
                .subscribe(new Action1<RxBusEvent>() {
                    @Override
                    public void call(RxBusEvent rxBusEvent) {
                        switch (rxBusEvent.getEventCode()) {
                            case RxEventCodes.REFRESH_COUPON:
                                toMain = (int) rxBusEvent.getContent();
                                //评价完成后刷新列表
                                pullDownToRefresh();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    @Override
    public int setLayoutResId() {
        return R.layout.activity_order_list;
    }

    @Override
    public void initDatas(View view) {
        curPage = 1;

        mData = new ArrayList<>();
        orderListAdapter = new OrderListAdapter(getActivity(), mData);
        lvOrderList.setAdapter(orderListAdapter);

        toMain = getArguments().getInt("toMain", 0);

        initViews();
        mCloseReceiver = new PublicReceiver(getActivity(), SBUtils.close_history);
        mCloseReceiver.setBeanReceive(new PublicReceiver.IBeanReceive() {
            @Override
            public void getBean() {
                if (toMain == 1) {
                    activityUtil.jumpTo(ControlerActivity.class);
                }
                EventBus.getDefault().post(new AllorederCloseEvent());
            }
        });

        getOrderListData(curPage + "");

        observeEvent();
    }

    @Override
    public void onComplete(String result, int type) {
        if (isSuccess(result)) {
            if (type == ORDER_LIST) {
                List<OrderListResp> data = ResultUtil.getList(result, OrderListResp.class);
                setOrderListData(data);
                if (curPage == 1 && null != data && data.size() < 1) {
                    rlytNoData.setVisibility(View.VISIBLE);
                } else {
                    rlytNoData.setVisibility(View.GONE);
                }
            }
        } else {
            if (type == ORDER_LIST) {
                stopFresh();
            }
        }
    }

    @Override
    public void onFailure(String msg, int type) {
        stopFresh();
    }

    private void initViews() {
        springView.setHeader(new DefaultHeader(getActivity()));
        springView.setFooter(new DefaultFooter(getActivity()));
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                pullDownToRefresh();
            }

            @Override
            public void onLoadmore() {
                pullUpToLoadMore();
            }
        });
        lvOrderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logger.i(TAG, "点击position=" + position);
                if (mData.size() > position && position >= 0) {
                    orderItem = mData.get(position);
                    if (CarOrderState.Car_Order_Status_Cancel.equals(orderItem.getStatus())) {//取消
                        return;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("orderItem", orderItem);
                    bundle.putInt("toMain", toMain);

                    UserInfoResp userInfoResp = CacheUtils.getIn().getUserInfo();
                    if (null != userInfoResp) {
                        Intent intent;
                        if (Config.ORDER_CATEGORY_LONG_RENT.equals(orderItem.getOrderCategory())) {//短租
                            intent = new Intent(getActivity(), OrderDetailLongRentActivity.class);
                        } else {//时租
                            String isCompanyOrder = orderItem.getIsCompanyOrder();
                            UserInfoResp.Company company = userInfoResp.getCompany();
                            if (null != company) {
                                //企业用户
                                if (CarOrderState.Car_Order_Status_nopay.equals(orderItem.getPayStatus())) {
                                    //未支付
                                    intent = new Intent(getActivity(), OrderDetailCompanyActivity.class);
                                }else {
                                    //已支付
                                    if ("true".equals(isCompanyOrder)){
                                        //用企业支付
                                        intent = new Intent(getActivity(), OrderDetailCompanyActivity.class);
                                    }else {
                                        //企业用户用个人支付
                                        intent = new Intent(getActivity(), OrderDetailActivity.class);
                                    }
                                }
                            } else {
                                //个人用户
                                intent = new Intent(getActivity(), OrderDetailActivity.class);
                            }
                        }
                        intent.putExtras(bundle);
                        startActivityForResult(intent, REQUEST_CODE_ORDER_DETAIL);
                    }
                }
            }
        });
    }



    private void stopFresh() {
        if (null != springView) {
            springView.onFinishFreshAndLoad();//停止刷新
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            getActivity().unregisterReceiver(mCloseReceiver);
        } catch (Exception e) {
            mCloseReceiver = null;
        }
    }



    /**
     * 下拉刷新数据
     */
    private void pullDownToRefresh() {
        curPage = 1;
        getOrderListData(curPage + "");
    }

    /**
     * 上拉加载更多
     */
    private void pullUpToLoadMore() {
        curPage++;
        getOrderListData(curPage + "");
    }

    public void getOrderListData(String page) {
        OrderListRequest data = new OrderListRequest(page, "5");
        UserInfoResp userInfoResp = CacheUtils.getIn().getUserInfo();
        if (null != userInfoResp) {
            data.setCustomerId(userInfoResp.getId());
        }
        data.setMethod(RequestUrls.QUERY_HISTORY_ORDER);
        doGet(data, ORDER_LIST, Config.LOADING_STRING, true);
    }

    public void setOrderListData(List<OrderListResp> result) {
        stopFresh();
        if (result != null && result.size() > 0) {
            if (curPage == 1) {
                mData.clear();
            }
            mData.addAll(result);
            orderListAdapter.notifyDataSetChanged();
            isNoData = false;
        } else {
            if (curPage > 1) {
                stopFresh();
                ToastUtil.show(getActivity(), "没有更多订单了");
            }
            isNoData = true;
        }
    }
}
