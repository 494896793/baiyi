package www.qisu666.com.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.callback.CarResp;
import www.qisu666.com.utils.DateUtils;
import www.qisu666.com.utils.TVUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wujiancheng on 2017/9/27.
 * 日间、夜间计费
 */

public class FeeUnitMoneyView extends LinearLayout {
    private FeeViewHolder feeViewHolder;

    public FeeUnitMoneyView(Context context) {
        super(context);
        init();
    }

    public FeeUnitMoneyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FeeUnitMoneyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_fee_unit_money, this);
        feeViewHolder = new FeeViewHolder(view);
    }

    public void setData(CarResp carResp) {
        if (null == carResp) {
            return;
        }
        //日间
        String dayRateHour = DateUtils.getDayTimeSection(carResp.getNightBeginRateHour(), carResp.getNightEndRateHour());
        if (!TextUtils.isEmpty(dayRateHour)) {
            feeViewHolder.tvDayTime.setText("日间(" + dayRateHour + ")：");
        }

        //电度费
        String tvElectricityMoney=carResp.getElectricityMoney()+"";
        if(!TextUtils.isEmpty(tvElectricityMoney)){
            feeViewHolder.tvElectricityMoney.setText(TVUtils.toString(Integer.parseInt(tvElectricityMoney) / 100.00));
        }

        //日间单价
        String dayMilesUnit = carResp.getMilesMoney();
        String dayTimeUnit = carResp.getTimeMoney();
        if (!TextUtils.isEmpty(dayMilesUnit)) {
            feeViewHolder.tvFeePerKM.setText(TVUtils.toString(Integer.parseInt(dayMilesUnit) / 100.00));
            feeViewHolder.tvFeePerKMs.setText(TVUtils.toString(Integer.parseInt(dayMilesUnit) / 100.00));
        }
        if (!TextUtils.isEmpty(dayTimeUnit)) {
            feeViewHolder.tvFeePerMin.setText(TVUtils.toString(Integer.parseInt(dayTimeUnit) / 100.00));
        }

        //夜间
        String nightRateHour = DateUtils.getNightTimeSection(carResp.getNightBeginRateHour(), carResp.getNightEndRateHour());
        if (!TextUtils.isEmpty(nightRateHour)) {
            feeViewHolder.tvNightTime.setText("夜间(" + nightRateHour + ")：");
        }
        //夜间单价
        int nightMilesUnit = carResp.getNightMilesUnit();
        int nightTimeUnit = carResp.getNightTimeUnit();
        feeViewHolder.tvFeePerKMNight.setText(TVUtils.toString(nightMilesUnit / 100.00));
        feeViewHolder.tvFeePerMinNight.setText(TVUtils.toString(nightTimeUnit / 100.00));
    }

    static class FeeViewHolder {
        @BindView(R.id.tvDayTime)
        TextView tvDayTime;
        @BindView(R.id.tvFeePerKM)
        TextView tvFeePerKM;
        @BindView(R.id.tvFeePerMin)
        TextView tvFeePerMin;
        @BindView(R.id.tvNightTime)
        TextView tvNightTime;
        @BindView(R.id.tvFeePerKMNight)
        TextView tvFeePerKMNight;
        @BindView(R.id.tvFeePerMinNight)
        TextView tvFeePerMinNight;
        @BindView(R.id.tvFeePerKMs)
        TextView tvFeePerKMs;
        @BindView(R.id.tvElectricityMoney)
        TextView tvElectricityMoney;

        FeeViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
