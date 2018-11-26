package www.qisu666.com.activity;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.adapter.MoneyLogsAdapter;
import www.qisu666.com.app.OrderType;
import www.qisu666.com.callback.MoneyLogsResp;
import www.qisu666.com.callback.UserInfoResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.request.MoneyDetailRequest;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.ResultUtil;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.view.MoneyTipView;
import com.liaoinstan.springview.container.DefaultFooter;
import com.liaoinstan.springview.container.DefaultHeader;
import com.liaoinstan.springview.widget.SpringView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 明细
 */
public class MoneyLogsActivity extends BaseActivity {

    @BindView(R.id.tvTitleName)
    TextView tvTitleName;
    @BindView(R.id.tvTitleRight)
    TextView tvTitleRight;
    @BindView(R.id.iv_nomoney)
    ImageView ivNomoney;
    @BindView(R.id.llytNoMoneyDetail)
    LinearLayout llytNoMoneyDetail;
    @BindView(R.id.springView)
    SpringView springView;
    @BindView(R.id.lvMoneyList)
    ListView lvMoneyList;
    @BindView(R.id.rl_money_detail_all)
    RelativeLayout rlMoneyDetailAll;
    private MoneyLogsAdapter adapter;
    private MoneyTipView viewShow;
    private int type = OrderType.ALL_TYPE;
    private List<MoneyLogsResp> datas;
    private int curRequestIndex = 1;

    @Override
    public void setView() {
        setContentView(R.layout.activity_money_logs);
    }

    @Override
    public void initDatas() {
        tvTitleName.setText("资金明细");
        tvTitleRight.setText("筛选");
        init();
        onPullDownToRefresh();
    }

    @Override
    public void onComplete(String result, int type) {
        if (isSuccess(result)) {
            List<MoneyLogsResp> data = ResultUtil.getList(result, MoneyLogsResp.class);
            List<MoneyLogsResp> tmp = new ArrayList<>();
            if (data != null) {
                int size = data.size();
                for (int i = 0; i < size; i++) {
                    if (data.get(i).getMoney() == 0) {
                        tmp.add(data.get(i));
                    }
                }
                data.removeAll(tmp);

                stopFresh();
                if (data.size() > 0) {
                    if (curRequestIndex == 1) {
                        lvMoneyList.setVisibility(View.VISIBLE);
                        llytNoMoneyDetail.setVisibility(View.GONE);
                        datas.clear();
                    }
                    datas.addAll(data);
                    adapter.notifyDataSetChanged();
                } else {
                    if (curRequestIndex == 1) {
                        lvMoneyList.setVisibility(View.GONE);
                        llytNoMoneyDetail.setVisibility(View.VISIBLE);
                    } else {
                        ToastUtil.show(mContext, "没有更多订单了");
                    }
                }
            } else {
                lvMoneyList.setVisibility(View.GONE);
                llytNoMoneyDetail.setVisibility(View.VISIBLE);
                stopFresh();
            }
            if (findViewById(R.id.rl_view_monet_tip) != null) {
                rlMoneyDetailAll.removeView(viewShow);
            }
        } else {
            lvMoneyList.setVisibility(View.GONE);
            llytNoMoneyDetail.setVisibility(View.VISIBLE);
            stopFresh();
        }
    }

    @Override
    public void onFailure(String msg, int type) {
        lvMoneyList.setVisibility(View.GONE);
        llytNoMoneyDetail.setVisibility(View.VISIBLE);
        stopFresh();
    }

    @OnClick({R.id.ivTitleLeft, R.id.llytTitleRight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivTitleLeft:
                finish();
                break;
            case R.id.llytTitleRight://筛选
                showTip();
                break;
        }
    }

    /**
     * 筛选
     */
    private void showTip() {
        if (viewShow == null) {
            viewShow = new MoneyTipView(mContext);
            viewShow.setListener(new MoneyTipView.ItemListener() {
                @Override
                public void all() {
                    if (type != OrderType.ALL_TYPE) {
                        type = OrderType.ALL_TYPE;
                        onPullDownToRefresh();
                    }
                    if (findViewById(R.id.rl_view_monet_tip) != null) {
                        rlMoneyDetailAll.removeView(viewShow);
                    }
                }

                @Override
                public void add() {
                    if (type != OrderType.CHARGE_TYPE) {
                        type = OrderType.CHARGE_TYPE;
                        onPullDownToRefresh();
                    }
                    if (findViewById(R.id.rl_view_monet_tip) != null) {
                        rlMoneyDetailAll.removeView(viewShow);
                    }
                }

                @Override
                public void defund() {
                    if (type != OrderType.REFUND_TYPE) {
                        type = OrderType.REFUND_TYPE;
                        onPullDownToRefresh();
                    }
                    if (findViewById(R.id.rl_view_monet_tip) != null) {
                        rlMoneyDetailAll.removeView(viewShow);
                    }
                }

                @Override
                public void delete() {
                    if (type != OrderType.PAY_TYPE) {
                        type = OrderType.PAY_TYPE;
                        onPullDownToRefresh();
                    }
                    if (findViewById(R.id.rl_view_monet_tip) != null) {
                        rlMoneyDetailAll.removeView(viewShow);
                    }
                }

                @Override
                public void removev() {
                    if (findViewById(R.id.rl_view_monet_tip) != null) {
                        rlMoneyDetailAll.removeView(viewShow);
                    }
                }
            });
        }
        if (layoutParams == null) {
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        rlMoneyDetailAll.addView(viewShow, layoutParams);
    }

    private void stopFresh() {
        if (null != springView) {
            springView.onFinishFreshAndLoad();//停止刷新
        }
    }

    private void init() {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        if (adapter == null) {
            adapter = new MoneyLogsAdapter(mContext, datas);
        }
        lvMoneyList.setAdapter(adapter);

        springView.setHeader(new DefaultHeader(this));
        springView.setFooter(new DefaultFooter(this));
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                onPullDownToRefresh();
            }

            @Override
            public void onLoadmore() {
                onPullUpToRefresh();
            }
        });
    }

    private void onRefreshData() {
        if (type == OrderType.ALL_TYPE) {
            queryMoneyDetail("", curRequestIndex);
        } else if (type == OrderType.CHARGE_TYPE) {//充值
            queryMoneyDetail(OrderType.BALANCE + "," + OrderType.DEPOSIT + "," + OrderType.GIFT_MONEY, curRequestIndex);
        } else if (type == OrderType.REFUND_TYPE) {//退款
            queryMoneyDetail(OrderType.REFUND, curRequestIndex);
        } else if (type == OrderType.PAY_TYPE) {//消费
            queryMoneyDetail(OrderType.TIMESHARING + "," + OrderType.LONG_RENT_PRE_PAY + "," + OrderType.LONG_RENT_PAY, curRequestIndex);
        }
    }

    /**
     * 查询资金明细
     *
     * @param category
     * @param page
     */
    private void queryMoneyDetail(String category, int page) {
        UserInfoResp mUser = CacheUtils.getIn().getUserInfo();
        if (mUser == null) {
            return;
        }
        MoneyDetailRequest data = new MoneyDetailRequest();
        data.setCategory(category);
        data.setCustomerId(mUser.getId());
        data.setPage(page);
        data.setSize(10);
        data.setMethod(RequestUrls.USER_MONEY_LOG);
        doGet(data, 0, Config.LOADING_STRING, true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if (viewShow != null && findViewById(R.id.rl_view_monet_tip) != null) {
                rlMoneyDetailAll.removeView(viewShow);
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void onPullDownToRefresh() {
        curRequestIndex = 1;
        onRefreshData();
    }


    public void onPullUpToRefresh() {
        curRequestIndex++;
        onRefreshData();
    }
}
