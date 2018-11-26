package www.qisu666.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.callback.PersonSystemMsgResp;
import www.qisu666.com.utils.DateUtils;

import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 系统消息
 */
public class SystemMsgAdapter extends android.widget.BaseAdapter {
    private Context mContext;
    private List<PersonSystemMsgResp.Datas> data;

    public SystemMsgAdapter(Context context, List<PersonSystemMsgResp.Datas> data) {
        this.mContext = context;
        this.data = data;
    }

    public void setData(List<PersonSystemMsgResp.Datas> data) {
        if (data != null) {
            this.data = data;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    @Override
    public PersonSystemMsgResp.Datas getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_system_msg, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final PersonSystemMsgResp.Datas item = getItem(position);
        if (item != null) {
            try {
                holder.tvMsgTime.setText(DateUtils.timestampToString(item.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.tvMsgTitle.setText(item.getTitle());
            holder.tvMsgContent.setText(item.getContent());
        }

        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.tvMsgTime)
        TextView tvMsgTime;
        @BindView(R.id.tvMsgTitle)
        TextView tvMsgTitle;
        @BindView(R.id.tvMsgContent)
        TextView tvMsgContent;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
