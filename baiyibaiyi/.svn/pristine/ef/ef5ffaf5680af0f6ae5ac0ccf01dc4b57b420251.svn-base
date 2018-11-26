package www.qisu666.com.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.callback.SystemConfigResp;
import www.qisu666.com.callback.UserInfoResp;
import www.qisu666.com.callback.ViolationListResp;
import www.qisu666.com.constant.Config;
import www.qisu666.com.constant.RequestUrls;
import www.qisu666.com.request.ViolationListRequest;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.ToastUtil;
import www.qisu666.com.view.CustomAlertDialog;
import com.liaoinstan.springview.container.DefaultFooter;
import com.liaoinstan.springview.container.DefaultHeader;
import com.liaoinstan.springview.widget.SpringView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wujiancheng on 2017/9/4.
 * 车辆违章
 */

public class TrafficViolationActivity extends BaseActivity {
    private static final String TAG = TrafficViolationActivity.class.getSimpleName();
    @BindView(R.id.tvTitleName)
    TextView tvTitleName;
    @BindView(R.id.springView)
    SpringView springView;
    @BindView(R.id.rvViolateList)
    RecyclerView rvViolateList;
    @BindView(R.id.rlytNoData)
    RelativeLayout rlytNoData;
    @BindView(R.id.llytViolateWarn)
    LinearLayout llytViolateWarn;

    private static final int REQUEST_VIOLATE_LIST = 1;
    private List<ViolationListResp.ResultBean.DatasBean> mTotalData = new ArrayList<>();
    private List<ViolationListResp.ResultBean.DatasBean> noSolves;
    private int noSolveNum = 0;//目前展示的待处理的条数
    private AnimalsHeadersAdapter adapter;
    private int page = 1;
    private int numPerPage = 5;
    private static final int REFRESH = 1;
    private static final int LOAD_MORE = 2;
    private int loadType = REFRESH;
    private static final String NO_SOLVE = "1";
    private static final String HAS_SOLVE = "2";

    @Override
    public void setView() {
        setContentView(R.layout.activity_traffic_violation);
    }

    @Override
    public void initDatas() {

        initView();
        initRefreshLayout();
        loadType = REFRESH;
        requestViolateList(page);

        tvTitleName.setText("违章记录");

        //违章数量
        UserInfoResp userInfoResp = CacheUtils.getIn().getUserInfo();
        if (null != userInfoResp) {
            String violateNum = userInfoResp.getUnDoWzCount();
            if (!TextUtils.isEmpty(violateNum) && !"0".equals(violateNum)) {
                llytViolateWarn.setVisibility(View.VISIBLE);
            } else {
                llytViolateWarn.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onComplete(String result, int type) {
        if (isSuccess(result)) {
            if (type == REQUEST_VIOLATE_LIST) {
                ViolationListResp.ResultBean resultBean = getBean(result, ViolationListResp.ResultBean.class);
                if (null == resultBean) {
                    return;
                }
                List<ViolationListResp.ResultBean.DatasBean> datas = resultBean.getDatas();
                if (null != datas && datas.size() > 0) {
                    setData(datas);
                } else {
                    if (loadType == REFRESH) {
                        rlytNoData.setVisibility(View.VISIBLE);
                    } else {
                        ToastUtil.show(mContext, "没有更多数据了");
                        rlytNoData.setVisibility(View.GONE);
                    }
                }
            }
        } else {
            if (type == REQUEST_VIOLATE_LIST) {
                if (loadType == REFRESH) {
                    rlytNoData.setVisibility(View.VISIBLE);
                } else {
                    rlytNoData.setVisibility(View.GONE);
                }
            }
        }
        if (null != springView) {
            springView.onFinishFreshAndLoad();//停止刷新
        }
    }

    @Override
    public void onFailure(String msg, int type) {
        if (type == REQUEST_VIOLATE_LIST) {
            if (null != springView) {
                springView.onFinishFreshAndLoad();//停止刷新
            }
            if (loadType == REFRESH) {
                rlytNoData.setVisibility(View.VISIBLE);
            } else {
                rlytNoData.setVisibility(View.GONE);
            }
        }
    }

    private void setData(List<ViolationListResp.ResultBean.DatasBean> datas) {
        //要将待处理的和已处理的区分一下
        List<ViolationListResp.ResultBean.DatasBean> hasSolves = new ArrayList<>();
        noSolves = new ArrayList<>();
        for (ViolationListResp.ResultBean.DatasBean bean : datas) {
            if (null != bean) {
                if (NO_SOLVE.equals(bean.getStatus())) {
                    noSolves.add(bean);
                } else if (HAS_SOLVE.equals(bean.getStatus())) {
                    hasSolves.add(bean);
                }
            }
        }
        if (loadType == REFRESH) {
            page = 1;
            mTotalData.clear();
            mTotalData.addAll(noSolves);
            noSolveNum = noSolves.size();
            mTotalData.addAll(hasSolves);
        } else if (loadType == LOAD_MORE) {
            page++;
            mTotalData.addAll(noSolveNum, noSolves);
            noSolveNum += noSolves.size();
            mTotalData.addAll(hasSolves);
        }
        adapter.notifyDataSetChanged();
    }

    @OnClick({R.id.ivTitleLeft, R.id.llytViolateWarn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivTitleLeft:
                finish();
                break;
            case R.id.llytViolateWarn:
                final SystemConfigResp info = CacheUtils.getIn().getSystem_Info();
                if (null != info) {
                    final CustomAlertDialog alertDialog = CustomAlertDialog.getAlertDialog(mContext, true, true);
                    alertDialog.setMessage("确认拨打" + info.getKfphone())
                            .setBtnCancelColor(R.color.color_gray_999999)
                            .setBtnConfirmColor(R.color.color_blue_02b2e4)
                            .setOnNegativeClickListener("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                }
                            })
                            .setOnPositiveClickListener("呼叫", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uri = Uri.parse("tel:" + info.getKfphone());
                                    Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                                    startActivity(intent);
                                    alertDialog.dismiss();
                                }
                            }).show();
                }
                break;
        }
    }

    private void requestViolateList(int page) {
        ViolationListRequest request = new ViolationListRequest();
        UserInfoResp userInfoResp = CacheUtils.getIn().getUserInfo();
        if (null != userInfoResp) {
            request.setCustomerId(userInfoResp.getId());
        }
        request.setPage(page);
        request.setRows(numPerPage);
        request.setMethod(RequestUrls.QUERY_VIOLATION_LIST);
        doGet(request, REQUEST_VIOLATE_LIST, Config.LOADING_STRING, true);
    }

    private void initRefreshLayout() {
        springView.setHeader(new DefaultHeader(this));
        springView.setFooter(new DefaultFooter(this));
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
    }

    private void pullDownToRefresh() {
        loadType = REFRESH;
        page = 1;
        requestViolateList(page);
    }

    private void pullUpToLoadMore() {
        loadType = LOAD_MORE;
        requestViolateList(page + 1);
    }

    private void initView() {

        // Set adapter populated with example dummy data
        adapter = new AnimalsHeadersAdapter(mTotalData);
        rvViolateList.setAdapter(adapter);

        // Set layout manager
        int orientation = getLayoutManagerOrientation(getResources().getConfiguration().orientation);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, orientation, false);
//        rvViolateList.setLinearLayout();
        rvViolateList.setLayoutManager(layoutManager);

        // Add the sticky headers decoration
        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
        rvViolateList.addItemDecoration(headersDecor);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                headersDecor.invalidateHeaders();
            }
        });
    }

    private int getLayoutManagerOrientation(int activityOrientation) {
        if (activityOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            return LinearLayoutManager.VERTICAL;
        } else {
            return LinearLayoutManager.HORIZONTAL;
        }
    }

    public class AnimalsHeadersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
            implements StickyRecyclerHeadersAdapter<AnimalsHeadersAdapter.ViewHolderHeader> {
        private List<ViolationListResp.ResultBean.DatasBean> mData = new ArrayList<>();

        public AnimalsHeadersAdapter(List<ViolationListResp.ResultBean.DatasBean> data) {
            this.mData = data;
            setHasStableIds(true);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_traffic_violation, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            Logger.i(TAG, "onBindViewHolder position=" + position);
            if (mData != null && mData.size() > 0 && position < mData.size()) {
                ViolationListResp.ResultBean.DatasBean datasBean = mData.get(position);
                if (null != datasBean && viewHolder instanceof AnimalsHeadersAdapter.ViewHolder) {
                    AnimalsHeadersAdapter.ViewHolder holder = (ViewHolder) viewHolder;
                    holder.tvViolationOrder.setText(datasBean.getOrderId());
                    holder.tvViolationTime.setText(datasBean.getPeccancyDate());
                    holder.tvViolationAddr.setText(datasBean.getPeccancyArea());
                    holder.tvViolationNo.setText(datasBean.getPeccancyCode());
                    holder.tvViolationType.setText(datasBean.getPeccancyAct());
                    String status = "";
                    int colorId = R.color.color_black_333333;
                    if (NO_SOLVE.equals(datasBean.getStatus())) {
                        status = "待处理";
                        colorId = R.color.color_red_e71100;
                    } else {
                        status = "已处理";
                    }
                    holder.tvViolationStatus.setText(status);
                    holder.tvViolationStatus.setTextColor(ContextCompat.getColor(mContext, colorId));
                    holder.tvViolationStatus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Logger.i(TAG, "onBindViewHolder onClick position=" + position);
                        }
                    });

                    if (position == noSolveNum - 1) {
                        holder.divideView.setVisibility(View.GONE);
                    } else {
                        holder.divideView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        public ViolationListResp.ResultBean.DatasBean getItem(int position) {
            if (position >= 0 && position < mData.size()) {
                return mData.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public long getHeaderId(int position) {
//            Logger.i(TAG, "headerId=" + position);
            if (getItem(position) == null) {
                return -1;
            } else {
                if (NO_SOLVE.equals(getItem(position).getStatus())) {
                    return 1;
                }
                return 2;
            }
        }

        @Override
        public ViewHolderHeader onCreateHeaderViewHolder(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_traffic_violation_header, parent, false);
            return new ViewHolderHeader(view);
        }

        @Override
        public void onBindHeaderViewHolder(ViewHolderHeader holder, final int position) {
            Logger.i(TAG, "onBindHeaderViewHolder position==" + position);
            if (mData != null && mData.size() > 0 && position < mData.size()) {
                ViolationListResp.ResultBean.DatasBean datasBean = mData.get(position);
                if (null != datasBean) {
                    if (NO_SOLVE.equals(datasBean.getStatus())) {
                        holder.tvViolateHeader.setText("待处理违章");
                    } else {
                        holder.tvViolateHeader.setText("已处理违章");
                    }
                    holder.tvViolateHeader.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Logger.i(TAG, "onBindHeaderViewHolder onClick position==" + position);
                        }
                    });
                }
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tvViolationOrder)
            TextView tvViolationOrder;
            @BindView(R.id.tvViolationTime)
            TextView tvViolationTime;
            @BindView(R.id.tvViolationAddr)
            TextView tvViolationAddr;
            @BindView(R.id.tvViolationNo)
            TextView tvViolationNo;
            @BindView(R.id.tvViolationType)
            TextView tvViolationType;
            @BindView(R.id.tvViolationStatus)
            TextView tvViolationStatus;
            @BindView(R.id.divideView)
            View divideView;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }

        class ViewHolderHeader extends RecyclerView.ViewHolder {
            @BindView(R.id.tvViolateHeader)
            TextView tvViolateHeader;

            ViewHolderHeader(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
}
