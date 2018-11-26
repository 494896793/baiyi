package www.qisu666.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.activity.ChargingDetailActivity;
import www.qisu666.com.bean.ChongDianJiLu;
import www.qisu666.com.utils.StringUtil;
import www.qisu666.com.utils.TVUtils;

import java.util.List;



//充电记录 适配器
public class ChargingRecordAdapter extends BaseAdapter {

    private Context context;
    private List<ChongDianJiLu.MyOrderList> list;

    public ChargingRecordAdapter(Context context, List<ChongDianJiLu.MyOrderList> list) {
        this.context = context;
        this.list = list;
    }

    //更新数据
    public void setList(List<ChongDianJiLu.MyOrderList> lists) {
        list = lists;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public ChongDianJiLu.MyOrderList getItem(int i) {
        return list == null ? null : list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final MyViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_rv_charging_record, parent, false);
            holder = new MyViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }
        final int posi = position;

        holder.tv_station_name.setText(list.get(position).getChargeStationName());
//        holder.tv_time.setText(StringUtil.formatDateWithoutSecond(list.get(position).getChargeEndTime()));
        holder.tv_time.setText(list.get(position).getChargeEndTime());
        holder.tv_payment_amount.setText("￥" + TVUtils.toString(Double.valueOf(list.get(position).getPayMoney())/100.00));
        holder.tv_actual_amount.setText("￥" + TVUtils.toString(Double.valueOf(list.get(position).getRealMoney())/100.00));
        holder.tv_return_amount.setText("￥" + TVUtils.toString(Double.valueOf(list.get(position).getRebackMoney())/100.00));
        holder.tv_charging_amount.setText(list.get(position).getChargeElectricity() + "度");
        holder.tv_charging_duration.setText(list.get(position).getChargeStartTime().length() > 5 ? StringUtil.formatTime(list.get(position).getChargeTime()) : "00:00:00");

        holder.llChongdian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ChargingDetailActivity.class);
                intent.putExtra("title", holder.tv_station_name.getText().toString());
                intent.putExtra("outTradeNo", list.get(posi).getOutTradeNo());
                intent.putExtra("terminalNumber", list.get(posi).getTerminalNumber());
                context.startActivity(intent);
            }
        });

        return convertView;
    }


    static class MyViewHolder {
        TextView tv_station_name;
        TextView tv_time;
        TextView tv_payment_amount;
        TextView tv_actual_amount;
        TextView tv_return_amount;
        TextView tv_charging_amount;
        TextView tv_charging_duration;
        LinearLayout llChongdian;

        public MyViewHolder(View itemView) {

            tv_station_name = (TextView) itemView.findViewById(R.id.tv_station_name);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_payment_amount = (TextView) itemView.findViewById(R.id.tv_payment_amount);
            tv_actual_amount = (TextView) itemView.findViewById(R.id.tv_actual_amount);
            tv_return_amount = (TextView) itemView.findViewById(R.id.tv_return_amount);
            tv_charging_amount = (TextView) itemView.findViewById(R.id.tv_charging_amount);
            tv_charging_duration = (TextView) itemView.findViewById(R.id.tv_charging_duration);
            llChongdian = (LinearLayout) itemView.findViewById(R.id.ll_chongdian);
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {

//                }
//            });
        }
    }


}
