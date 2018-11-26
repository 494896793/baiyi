package www.qisu666.com.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import www.qisu666.com.R;
import www.qisu666.com.callback.ParksResp;
import www.qisu666.com.utils.Logger;
import www.qisu666.com.utils.TVUtils;
import www.qisu666.com.utils.UserUtils;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/4/19.
 * 网点列表
 */

public class ParkPointListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ParkPointListAdapter.class.getSimpleName();
    private Context context;
    private List<ParksResp> mData;
    private OnItemClickListener mOnItemClickListener;

    public ParkPointListAdapter(Context context, List<ParksResp> data) {
        this.context = context;
        this.mData = data;
    }

    /**
     * 更新数据
     *
     * @param data 更新的数据
     */
    public void setData(List<ParksResp> data) {
        if (mData != null) {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listitem_park_point_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            ParksResp dataBean = mData.get(position);
            if (null != dataBean) {
                viewHolder.tvParkName.setText(dataBean.getParkName());
                double distance = 0;
                LatLng latLng = dataBean.getLatlng();
                String userLat = UserUtils.getLatitude();
                String userLng = UserUtils.getLongitude();
                if (!TextUtils.isEmpty(userLat) && !TextUtils.isEmpty(userLng) && null != latLng) {
                    distance = AMapUtils.calculateLineDistance(latLng, new LatLng(Double.valueOf(userLat), Double.valueOf(userLng)));
                }

                if (distance <= 1000) {
                    viewHolder.tvParkDistance.setText(TVUtils.toStringInt(distance + "") + "m");
                } else {
                    BigDecimal bd = new BigDecimal(distance / 1000.00);
                    BigDecimal dis = bd.setScale(2, BigDecimal.ROUND_HALF_UP);//保留两位小数点，四舍五入
                    viewHolder.tvParkDistance.setText(dis + "KM");
                }

                viewHolder.tvParkAddr.setText(dataBean.getParkAddress());

                viewHolder.listitemContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mOnItemClickListener) {
                            mOnItemClickListener.onItemClick(v, mData, position);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (null != mData) {
            Logger.i(TAG, "getItemCount==" + mData.size());
            return mData.size();
        }
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.listitem_container)
        LinearLayout listitemContainer;
        @BindView(R.id.tv_park_name)
        TextView tvParkName;
        @BindView(R.id.tv_park_distance)
        TextView tvParkDistance;
        @BindView(R.id.tv_park_addr)
        TextView tvParkAddr;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, List<ParksResp> data, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
