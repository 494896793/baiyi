package www.qisu666.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import www.qisu666.com.R;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;



//电站列表适配器
public class StationListAdapter extends BaseAdapter {
   private Context context;
   private List<Map<String, Object>> stations;
   private OnChargeBtnClickListener onChargeBtnClickListener;

   public StationListAdapter(Context context, List<Map<String, Object>> stations, OnChargeBtnClickListener onChargeBtnClickListener) {
       this.context = context;
       this.stations = stations;
       this.onChargeBtnClickListener = onChargeBtnClickListener;
   }

   public void  setList(List<Map<String, Object>> stations){
       this.stations = stations;
       notifyDataSetChanged();
   }




   @Override
   public int getCount() {
       return stations.size();
   }
   @Override
   public Object getItem(int position) {
       return stations.get(position);
   }
   @Override
   public long getItemId(int position) {
       return position;
   }


   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder viewHolder = null;
       if (convertView == null) {
           viewHolder = new ViewHolder();
           convertView = LayoutInflater.from(context).inflate(R.layout.item_rv_collection, null);
           viewHolder.tv_nearby_title = (TextView) convertView.findViewById(R.id.tv_nearby_title);
           viewHolder.tv_nearby_fee = (TextView) convertView.findViewById(R.id.tv_nearby_fee);
           viewHolder.tv_nearby_addr = (TextView) convertView.findViewById(R.id.tv_nearby_addr);
           viewHolder.tv_nearby_distance = (TextView) convertView.findViewById(R.id.tv_nearby_distance);
           viewHolder.tv_nearby_free_fast = (TextView) convertView.findViewById(R.id.tv_nearby_free_fast);
           viewHolder.tv_nearby_free_slow = (TextView) convertView.findViewById(R.id.tv_nearby_free_slow);
           viewHolder.tv_nearby_type = (TextView) convertView.findViewById(R.id.tv_nearby_type);
           viewHolder.tv_nearby_charge = (TextView) convertView.findViewById(R.id.tv_nearby_charge);
           convertView.setTag(viewHolder);
       } else {
           viewHolder = (ViewHolder) convertView.getTag();
       }
     try {
         final Map m = stations.get(position);
         viewHolder.tv_nearby_title.setText(m.get("stationName").toString());

         BigDecimal money_str = new BigDecimal(m.get("chargeFeePer").toString());
         double money = money_str.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
         viewHolder.tv_nearby_fee.setText(String.valueOf(money) + "元/度");
         viewHolder.tv_nearby_addr.setText(m.get("chargeAddress").toString());
         BigDecimal b = new BigDecimal(m.get("chargeDistance").toString());
         double dis = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
         viewHolder.tv_nearby_distance.setText(String.valueOf(dis) + "km");
         viewHolder.tv_nearby_free_fast.setText("空闲" + m.get("pileFastNumFree").toString());
         viewHolder.tv_nearby_free_slow.setText("空闲" + m.get("pileSlowNumFree").toString());
//        viewHolder.tv_nearby_type.setText(m.get("charge_method").toString().equals("00") ? "交流" : m.get("charge_method").toString().equals("01") ? "直流" : "直流/交流");
         viewHolder.tv_nearby_type.setText("总桩数" + m.get("totalPileCount").toString());

         viewHolder.tv_nearby_charge.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 onChargeBtnClickListener.onChargeClick(new LatLng(Double.valueOf(m.get("latitude").toString()), Double.valueOf(m.get("longitude").toString())));
             }
         });
     }catch (Throwable t){t.printStackTrace();}
       return convertView;
   }

   class ViewHolder {
       TextView tv_nearby_title;
       TextView tv_nearby_fee;
       TextView tv_nearby_addr;
       TextView tv_nearby_distance;
       TextView tv_nearby_free_fast;
       TextView tv_nearby_free_slow;
       TextView tv_nearby_type;
       TextView tv_nearby_charge;
   }

   public void loadData(List<Map<String, Object>> stations){
       this.stations.addAll(stations);
       notifyDataSetChanged();
   }

   public void refreshData(List<Map<String, Object>> stations){
       this.stations = stations;
       notifyDataSetChanged();
   }

   public interface OnChargeBtnClickListener{
       void onChargeClick(LatLng latLng);
   }

}
