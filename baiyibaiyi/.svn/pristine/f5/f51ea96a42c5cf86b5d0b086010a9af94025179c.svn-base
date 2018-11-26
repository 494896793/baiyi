package www.qisu666.com.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.callback.ParksResp;
import www.qisu666.com.callback.ParkListByCityResp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/4/19.
 */

public class AreaListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ParkListByCityResp> mData;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    private int selectPos = 0;

    public AreaListAdapter(Context context, List<ParkListByCityResp> data) {
        this.mContext = context;
        this.mData = data;
    }

    /**
     * 更新数据
     *
     * @param data
     */
    public void setData(List<ParkListByCityResp> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.listitem_area_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ParkListByCityResp dataBean = mData.get(position);
            if (null == dataBean) {
                return;
            }
            int num = 0;
            List<ParksResp> parksResps = dataBean.getParkList();
            if (null != parksResps) {
                num = parksResps.size();
            }
            viewHolder.tvAreaName.setText(dataBean.getName() + "(" + num + ")");
            viewHolder.tvAreaName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnItemClickListener) {
                        mOnItemClickListener.onItemClick(v, mData, position);
                    }
                }
            });

            if (selectPos == position) {//选中
                viewHolder.tvAreaName.setTextColor(ContextCompat.getColor(mContext, R.color.content_bg));
                viewHolder.tvAreaName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            } else {
                viewHolder.tvAreaName.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                viewHolder.tvAreaName.setBackgroundColor(ContextCompat.getColor(mContext, R.color.content_bg));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (null != mData) {
            return mData.size();
        }
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_area_name)
        TextView tvAreaName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, List<ParkListByCityResp> data, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    /**
     * list中点击的位置
     *
     * @param selectPos
     */
    public void setSelectPos(int selectPos) {
        this.selectPos = selectPos;
    }
}
