package www.qisu666.com.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.utils.GlobalParams;

import java.util.List;
import java.util.Map;



//电站详情适配器
public class StationDetailAdapter extends BaseAdapter {
   private Context context;
   private List<Map<String, Object>> list;

   public StationDetailAdapter(Context context, List<Map<String, Object>> list) {
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
       ViewHolder viewHolder = null;
       if (convertView == null) {
           viewHolder = new ViewHolder();
           convertView = LayoutInflater.from(context).inflate(R.layout.item_lv_charging, null);
           viewHolder.img_type = (ImageView) convertView.findViewById(R.id.img_type);
           viewHolder.tv_no = (TextView) convertView.findViewById(R.id.tv_no);
           viewHolder.tv_info = (TextView) convertView.findViewById(R.id.tv_info);
           viewHolder.tv_state = (TextView) convertView.findViewById(R.id.tv_state);
           viewHolder.layout_soc = (LinearLayout) convertView.findViewById(R.id.layout_soc);
           viewHolder.tv_soc = (TextView) convertView.findViewById(R.id.tv_soc);
           convertView.setTag(viewHolder);
       } else {
           viewHolder = (ViewHolder) convertView.getTag();
       }
       Map<String, Object> map = list.get(position);
       viewHolder.img_type.setImageResource(map.get("chargePileEfficiency").toString().equals("01") ? R.mipmap.yd_16:R.mipmap.yd_17);
       viewHolder.tv_no.setText(map.get("chargePileNum").toString());
       StringBuilder builder = new StringBuilder();

       builder.append(map.get("chargePileBel").toString().equals("01") ? "公共桩":"个人桩").append(" ")
               .append(map.get("chargeInterface").toString().equals("01") ? "国标":"特斯拉").append(" ")
               .append(GlobalParams.chargeTypes[Integer.valueOf(map.get("chargePileType").toString())]).append(" ")
               .append(map.get("chargePilePower").toString()+"kW");
       viewHolder.tv_info.setText(builder.toString());

       int state = Integer.valueOf(map.get("pileState").toString());
       if(state==0 || state==1 || state==3 || state==4 ||  state==5 || state==7){
           viewHolder.tv_state.setText(GlobalParams.pileStates[state]);
       } else {
           viewHolder.tv_state.setText("占用");
       }
       viewHolder.tv_state.setTextColor(ContextCompat.getColor(context, getStateColor(state)));

       if(map.get("soc") != null){
           if(!"200".equals(map.get("soc").toString())){
               viewHolder.tv_soc.setText(map.get("soc").toString()+"%)");
               viewHolder.layout_soc.setVisibility(View.VISIBLE);
           } else {
               viewHolder.layout_soc.setVisibility(View.GONE);
           }
       }
       return convertView;
   }

   private int getStateColor(int state){
       int color;
       switch (state){
           case 0: color = R.color.new_primary; break;
           case 1: color = R.color.warn_notice_color; break;
           case 3: color = R.color.new_primary; break;
           case 4: color = R.color.new_primary;  break;
           case 5: color = R.color.new_primary; break;
           case 7: color = R.color.new_primary; break;
           default: color = R.color.new_primary;  break;
       }
       return color;
   }

   class ViewHolder {
       ImageView img_type;
       TextView tv_no;
       TextView tv_info;
       TextView tv_state;
       LinearLayout layout_soc;
       TextView tv_soc;
   }
}
