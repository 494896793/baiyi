package www.qisu666.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import www.qisu666.com.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wujiancheng on 2017/7/22.
 * 城市列表Adapter
 */

public class CityListAdapter extends BaseAdapter<String> {

    private LayoutInflater mInflater;
    private List<String> mData;
    private Context mContext;

    public CityListAdapter(Context context, List<String> data) {
        super(context, data);
        this.mContext = context;
        this.mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.listitem_cities, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (mData != null) {
            String cityName = mData.get(position);
            holder.tvCityName.setText(cityName);
        }

        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.tvCityName)
        TextView tvCityName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
