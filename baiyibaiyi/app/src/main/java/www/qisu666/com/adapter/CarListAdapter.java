package www.qisu666.com.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.activity.CarListActivity;
import www.qisu666.com.activity.UseCarPreOrderingActivity;
import www.qisu666.com.callback.CarResp;
import www.qisu666.com.callback.ParkResp;
import www.qisu666.com.callback.ParksResp;
import www.qisu666.com.utils.DateUtils;
import www.qisu666.com.utils.TVUtils;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wujiancheng on 2017/7/30.
 * 指定网点的车辆列表
 */

public class CarListAdapter extends BaseAdapter<CarResp> {
    private Activity mContext;
    private List<CarResp> mData;
    private LayoutInflater mInflate;
    private ParksResp parkInfo;

    public CarListAdapter(Activity context, List<CarResp> data) {
        super(context, data);
        this.mContext = context;
        this.mData = data;
        mInflate = LayoutInflater.from(context);
    }

    /**
     * 停车场信息
     *
     * @param parkInfo
     */
    public void setParkInfo(ParksResp parkInfo) {
        this.parkInfo = parkInfo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = mInflate.inflate(R.layout.listitem_cars, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        if (position == 0) {
//            holder.viewTopLine.setVisibility(View.VISIBLE);
//        } else {
//            holder.viewTopLine.setVisibility(View.GONE);
//        }
        if (null != mData && position < mData.size()) {
            final CarResp carResp = mData.get(position);
            if (null != carResp) {
                int isRedCar = carResp.getIsRedPkCar();
//                是否是红包车(1 是，0 不是)
                if (isRedCar == 1) {
                    holder.ivFlagRedPacketCar.setVisibility(View.VISIBLE);
                } else {
                    holder.ivFlagRedPacketCar.setVisibility(View.GONE);
                }
                Glide.with(mContext).load(carResp.getSmallCarImgUri()).into(holder.ivCarType);
                holder.tvCarNumber.setText(carResp.getCarNumber());
                holder.car_set_tx.setText(carResp.getCarSetsNums() + "座");
                holder.tvCarBrand.setText(carResp.getCarBrand() + carResp.getModels());
                holder.car_color_tx.setText(carResp.getCarColor());
                String battery = carResp.getBatteryResidual() + "";
                if (!"null".equals(battery) && !"".equals(battery)) {
                    double batteryD = Double.valueOf(battery);
                    int batteryPercent = (int) batteryD;
                    holder.tvBatteryPercent.setText(batteryPercent + "%");
                    if (batteryPercent > 80) {
                        holder.elect_img.setImageResource(R.mipmap.yc_18);
//                        holder.tvBatteryPercent.setTextColor(ContextCompat.getColor(mContext, R.color.color_green_59d66f));
                    } else if (batteryPercent > 60) {
                        holder.elect_img.setImageResource(R.mipmap.yc_19);
//                        holder.tvBatteryPercent.setTextColor(ContextCompat.getColor(mContext, R.color.color_orange_ff811b));
                    } else if (batteryPercent > 40) {
                        holder.elect_img.setImageResource(R.mipmap.yc_20);
//                        holder.tvBatteryPercent.setTextColor(ContextCompat.getColor(mContext, R.color.color_red_e71100));
                    } else if (batteryPercent >= 20) {
                        holder.elect_img.setImageResource(R.mipmap.yc_21);
//                        holder.tvBatteryPercent.setTextColor(ContextCompat.getColor(mContext, R.color.color_red_e71100));
                    } else if (batteryPercent >= 10) {
                        holder.elect_img.setImageResource(R.mipmap.yc_16);
//                        holder.tvBatteryPercent.setTextColor(ContextCompat.getColor(mContext, R.color.color_red_e71100));
                    } else {
                        holder.elect_img.setImageResource(R.mipmap.yc_17);
                    }
                }
                String milesMoneyUnit = carResp.getMilesMoney();
                String timeMoneyUnit = carResp.getTimeMoney();
                String electricityMoney = carResp.getElectricityMoney() + "";
                boolean isNightTimeSection = DateUtils.isNightTimeSection(carResp.getNightBeginRateHour(), carResp.getNightEndRateHour());
                if (isNightTimeSection) {
                    //当前时间属于夜间时间段
                    milesMoneyUnit = carResp.getNightMilesUnit() + "";
                    timeMoneyUnit = carResp.getNightTimeUnit() + "";
                }

                if (electricityMoney == null || electricityMoney.equals("")) {
                    if (null != milesMoneyUnit && !"".equals(milesMoneyUnit) && null != timeMoneyUnit && !"".equals(timeMoneyUnit)) {
                        holder.tvCarFee.setText("计费：" + Html.fromHtml("<font color='#51E7D3'>" + TVUtils.toString(Integer.parseInt(milesMoneyUnit) / 100.00) + "</font>元/公里+<font color='#51E7D3'>" + TVUtils.toString(Integer.parseInt(timeMoneyUnit) / 100.00) + "</font>元/分钟"));
                    } else if (null != milesMoneyUnit && !"".equals(milesMoneyUnit)) {
                        holder.tvCarFee.setText("计费：" + Html.fromHtml("<font color='#51E7D3'>" + TVUtils.toString(Integer.parseInt(milesMoneyUnit) / 100.00) + "</font>元/公里"));
                    } else {
                        holder.tvCarFee.setText("计费：" + Html.fromHtml("<font color='#51E7D3'>" + TVUtils.toString(Integer.parseInt(timeMoneyUnit) / 100.00) + "</font>元/分钟"));
                    }
                } else {
                    if (null != milesMoneyUnit && !"".equals(milesMoneyUnit) && null != timeMoneyUnit && !"".equals(timeMoneyUnit)) {
                        holder.tvCarFee.setText("计费：里程费+电度费+时长费\n" + Html.fromHtml("<font color='#51E7D3'>" + TVUtils.toString(Integer.parseInt(milesMoneyUnit) / 100.00) + "</font>元/公里+<font color='#51E7D3'>" + TVUtils.toString(Integer.parseInt(electricityMoney) / 100.00) + "</font>元+<font color='#51E7D3'>" + TVUtils.toString(Integer.parseInt(timeMoneyUnit) / 100.00) + "</font>元/分钟"));
                    } else if (null != milesMoneyUnit && !"".equals(milesMoneyUnit)) {
                        holder.tvCarFee.setText("计费：里程费+电度费\n" + Html.fromHtml("<font color='#51E7D3'>" + TVUtils.toString(Integer.parseInt(milesMoneyUnit) / 100.00) + "</font>元/公里+<font color='#51E7D3'>" + TVUtils.toString(Integer.parseInt(electricityMoney) / 100.00) + "</font>元"));
                    } else {
                        holder.tvCarFee.setText("计费：时长费+电度费\n" + Html.fromHtml("<font color='#51E7D3'>" + TVUtils.toString(Integer.parseInt(timeMoneyUnit) / 100.00) + "</font>元/分钟+<font color='#51E7D3'>" + TVUtils.toString(Integer.parseInt(electricityMoney) / 100.00) + "</font>元"));
                    }
                }

                //订车
                holder.tvOrderCar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, UseCarPreOrderingActivity.class);
                        intent.putExtra("carInfo", carResp);
                        if (null != parkInfo) {
                            intent.putExtra("parkInfo", getParkInfo(parkInfo));
                        }
                        mContext.startActivityForResult(intent, CarListActivity.REQUEST_CODE_ORDER);
                    }
                });
            }

        }
        return convertView;
    }

    private ParkResp getParkInfo(ParksResp park) {
        ParkResp parkResp = new ParkResp();
        parkResp.setParkName(park.getParkName());
        parkResp.setParkAddress(park.getParkAddress());
        parkResp.setLatitude(park.getLatitude());
        parkResp.setLongitude(park.getLongitude());
        parkResp.setParkType(park.getParkType());
        parkResp.setId(park.getId());
        parkResp.setDistance(park.getDistance());
        return parkResp;
    }

    static class ViewHolder {
        @BindView(R.id.car_set_tx)
        TextView car_set_tx;
        @BindView(R.id.car_color_tx)
        TextView car_color_tx;
        @BindView(R.id.elect_img)
        ImageView elect_img;
        //        @BindView(R.id.viewTopLine)
//        View viewTopLine;
        @BindView(R.id.ivFlagRedPacketCar)
        ImageView ivFlagRedPacketCar;
        @BindView(R.id.ivCarType)
        ImageView ivCarType;
        @BindView(R.id.tvCarNumber)
        TextView tvCarNumber;
        @BindView(R.id.tvCarBrand)
        TextView tvCarBrand;
        @BindView(R.id.tvOrderCar)
        TextView tvOrderCar;
        @BindView(R.id.tvBatteryText)
        TextView tvBatteryText;
        @BindView(R.id.tvBatteryPercent)
        TextView tvBatteryPercent;
        @BindView(R.id.viewLine)
        View viewLine;
        @BindView(R.id.tvCarFeeText)
        TextView tvCarFeeText;
        @BindView(R.id.tvCarFee)
        TextView tvCarFee;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
