package www.qisu666.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import www.qisu666.com.R;

import java.util.List;
import java.util.Map;


//充电费用适配器
public class ChargeFeeAdapter extends BaseAdapter {
    private Context context;
    private List<Map<String,Object>> list;
    public ChargeFeeAdapter(Context context, List<Map<String, Object>> list) {
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_charge_standard, null);
            holder = new ViewHolder();
            holder.tv_start_time = (TextView) convertView.findViewById(R.id.tv_start_time);
            holder.tv_end_time = (TextView) convertView.findViewById(R.id.tv_end_time);
            holder.tv_consumption_amount = (TextView) convertView.findViewById(R.id.tv_consumption_amount);
            holder.tv_electricity_fee = (TextView) convertView.findViewById(R.id.tv_electricity_fee);
            holder.tv_kilowatt_hour_fee = (TextView) convertView.findViewById(R.id.tv_kilowatt_hour_fee);
            holder.tv_service_fee = (TextView) convertView.findViewById(R.id.tv_service_fee);
            holder.tv_kilowatt_hour_service_fee = (TextView) convertView.findViewById(R.id.tv_kilowatt_hour_service_fee);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_start_time.setText(list.get(position).get("time_slot").toString().split("-")[0]);
        holder.tv_end_time.setText(list.get(position).get("time_slot").toString().split("-")[1]);
        holder.tv_consumption_amount.setText(list.get(position).get("charge_electricity_slot").toString()+"度");
        holder.tv_electricity_fee.setText("￥"+ list.get(position).get("charge_fee").toString());
//        holder.tv_kilowatt_hour_fee.setText("（"+ (Float.valueOf(list.get(position).get("charge_electricity_slot").toString()) == 0 ? "0.00" : TextUtils.isEmpty(list.get(position).get("charge_fee").toString()) ?
//                "0" : NumberUtils.getRoundNumber(Float.valueOf(list.get(position).get("charge_fee").toString()) / Float.valueOf(list.get(position).get("charge_electricity_slot").toString()), 2))+"元/度）");
        holder.tv_kilowatt_hour_fee.setText("（" + list.get(position).get("charge_fee_per").toString() + "元/度）");
        holder.tv_service_fee.setText("￥"+ list.get(position).get("service_fee").toString());
        holder.tv_kilowatt_hour_service_fee.setText("（" + list.get(position).get("service_fee_per").toString() + "元/度）");
//        holder.tv_kilowatt_hour_service_fee.setText("（"+(Float.valueOf(list.get(position).get("charge_electricity_slot").toString()) == 0 ? "0.00" : TextUtils.isEmpty(list.get(position).get("service_fee").toString()) ?
//                "0" : NumberUtils.getRoundNumber(Float.valueOf(list.get(position).get("service_fee").toString()) / Float.valueOf(list.get(position).get("charge_electricity_slot").toString()), 2))+"元/度）");
        return convertView;
    }

    class ViewHolder{
        TextView tv_start_time;
        TextView tv_end_time;
        TextView tv_consumption_amount;
        TextView tv_electricity_fee;
        TextView tv_kilowatt_hour_fee;
        TextView tv_service_fee;
        TextView tv_kilowatt_hour_service_fee;
    }

}
