package www.qisu666.com.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.utils.HighlightUtil;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wujiancheng on 2017/7/24.
 * 通过地图搜索出来的地址的adapter
 */

public class SearchMapPointAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<Map<String, String>> mData;
    private String mKeyWord = "";

    public SearchMapPointAdapter(Context context, List<Map<String, String>> data) {
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
            Map<String, String> map = mData.get(position);
            if (null != map) {
                holder.tvPositionIcon.setImageResource(R.mipmap.search_current_position);
                String addressName = map.get("addressName");
                addressName = HighlightUtil.convertHightlightText(addressName,mKeyWord,"#02b2e4");
                holder.tvSearchParkName.setText(Html.fromHtml(addressName));
                holder.tvSearchDistance.setVisibility(View.GONE);

                holder.tvSearchParkDetail.setText(map.get("addressDetail"));

            }
        }
        return convertView;
    }

    public void setKeyWord(String keyWord) {
        this.mKeyWord = keyWord;
    }

    static class ViewHolder {
        @BindView(R.id.tvPositionIcon)
        ImageView tvPositionIcon;
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
