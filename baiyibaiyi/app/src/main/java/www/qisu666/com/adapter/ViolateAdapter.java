package www.qisu666.com.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.callback.ViolationListResp;
import www.qisu666.com.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wujiancheng on 2017/9/4.
 * 违章列表
 */

public class ViolateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ViolateAdapter.class.getSimpleName();
    public static int ITEM_HEADER = 1;
    public static int ITEM_CONTENT = 2;
    private Context mContext;
    private List<ViolationListResp.ResultBean.DatasBean> mData;

    public ViolateAdapter(Context context,List<ViolationListResp.ResultBean.DatasBean> data){
        this.mContext = context;
        this.mData = data;
    }

    public ViolationListResp.ResultBean.DatasBean getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).itemType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_HEADER) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_traffic_violation_header, parent, false);
            return new ViewHolderHeader(view);
        } else if (viewType == ITEM_CONTENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_traffic_violation, parent, false);
            return new ViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Logger.i(TAG, "onBindViewHolder position=" + position);
        if (mData != null && mData.size() > 0 && position < mData.size()) {
            ViolationListResp.ResultBean.DatasBean datasBean = mData.get(position);
            if (null != datasBean) {
                int viewType = datasBean.itemType;
                if (viewType == ITEM_HEADER) {
                    ViewHolderHeader holder = (ViewHolderHeader) viewHolder;
                    if ("1".equals(datasBean.getStatus())) {
                        holder.tvViolateHeader.setText("待处理违章");
                    } else {
                        holder.tvViolateHeader.setText("已处理违章");
                    }
                } else if (viewType == ITEM_CONTENT) {
                    ViewHolder holder = (ViewHolder) viewHolder;
                    holder.tvViolationOrder.setText(datasBean.getOrderId());
                    holder.tvViolationTime.setText(datasBean.getPeccancyDate());
                    holder.tvViolationAddr.setText(datasBean.getPeccancyArea());
                    holder.tvViolationNo.setText(datasBean.getPeccancyCode());
                    holder.tvViolationType.setText(datasBean.getPeccancyAct());
                    String status = "";
                    int colorId = R.color.color_black_333333;
                    if ("1".equals(datasBean.getStatus())) {
                        status = "待处理";
                        colorId = R.color.color_red_e71100;
                    } else {
                        status = "已处理";
                    }
                    holder.tvViolationStatus.setText(status);
                    holder.tvViolationStatus.setTextColor(ContextCompat.getColor(mContext, colorId));
                }
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
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
