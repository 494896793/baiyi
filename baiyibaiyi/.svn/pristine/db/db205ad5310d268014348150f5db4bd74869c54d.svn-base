package www.qisu666.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.callback.ParksResp;
import www.qisu666.com.utils.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wujiancheng on 2017/7/24.
 * 网点搜索adapter
 */

public class SearchParkAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<ParksResp> mData;

    public SearchParkAdapter(Context context, List<ParksResp> data) {
        super(context, data);
        this.mContext = context;
        this.mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_search_park, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (mData != null && position < mData.size()) {
            ParksResp parksResp = mData.get(position);
            if (null != parksResp) {
                holder.tvSearchParkName.setText(parksResp.getParkName());
                Logger.i("parksResp.getDistance()=" + parksResp.getDistance());
                if (parksResp.getDistance() == null || "".equals(parksResp.getDistance())) {
                    holder.tvSearchDistance.setVisibility(View.GONE);
                } else {
                    holder.tvSearchDistance.setText(parksResp.getDistance());
                    holder.tvSearchDistance.setVisibility(View.VISIBLE);
                }

                holder.tvSearchParkDetail.setText(parksResp.getParkAddress());

            }
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.tvSearchParkName)
        TextView tvSearchParkName;
        @BindView(R.id.tvSearchDistance)
        TextView tvSearchDistance;
        @BindView(R.id.tvSearchParkDetail)
        TextView tvSearchParkDetail;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
