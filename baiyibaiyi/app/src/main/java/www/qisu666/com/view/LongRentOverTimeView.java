package www.qisu666.com.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.callback.UseCostLongResp;
import www.qisu666.com.utils.DateUtils;
import www.qisu666.com.utils.StringUtils;
import www.qisu666.com.utils.TVUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wujiancheng on 2017/9/25.
 * 短租用车超时视图
 */

public class LongRentOverTimeView extends LinearLayout {
    private ViewHolder viewHolder;
    private OnTimeTipClickListener mOnTimeTipClickListener;

    public LongRentOverTimeView(Context context) {
        super(context);
        init();
    }

    public LongRentOverTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LongRentOverTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_long_rent_over_time, this);
        viewHolder = new ViewHolder(view);
    }

    public void setData(UseCostLongResp useCostLongResp) {
        viewHolder.tvComboName.setText(useCostLongResp.getName());
        viewHolder.tvComboDays.setText("时长:" + useCostLongResp.getDays() + "天");
        viewHolder.tvComboMileage.setText("里程:" + TVUtils.toString2(useCostLongResp.getOrderMileage()) + "公里");
        viewHolder.tvComboFee.setText("费用:" + TVUtils.toString(useCostLongResp.getOrderPrePayMoney() / 100.0) + "元");

        String overUseTimeStr = useCostLongResp.getOutUseTime();
        if (!StringUtils.isEmpty(overUseTimeStr)) {
            int overUseTime = Integer.parseInt(overUseTimeStr);
            overUseTime = overUseTime / 1000 / 60;//分钟
            viewHolder.tvOverUseTime.setText(DateUtils.minuteToDay(overUseTime));
        }
        viewHolder.tvOverUseFee.setText(TVUtils.toString(useCostLongResp.getOutTimeCost() / 100.0) + "元");
        viewHolder.tvOverMileage.setText(useCostLongResp.getOutTimeMilage() + "公里");

        //分时租赁帮助
        viewHolder.tvOverHelp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnTimeTipClickListener) {
                    mOnTimeTipClickListener.onTimeTipClickListener();
                }
            }
        });
    }

    static class ViewHolder {
        @BindView(R.id.tvComboName)
        TextView tvComboName;
        @BindView(R.id.tvComboDays)
        TextView tvComboDays;
        @BindView(R.id.tvComboMileage)
        TextView tvComboMileage;
        @BindView(R.id.tvComboFee)
        TextView tvComboFee;
        @BindView(R.id.tvOverHelp)
        TextView tvOverHelp;
        @BindView(R.id.tvOverUseTime)
        TextView tvOverUseTime;
        @BindView(R.id.tvOverUseFee)
        TextView tvOverUseFee;
        @BindView(R.id.tvOverMileage)
        TextView tvOverMileage;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnTimeTipClickListener {
        void onTimeTipClickListener();
    }

    public void setOnTimeTipClickListener(OnTimeTipClickListener onTimeTipClickListener) {
        this.mOnTimeTipClickListener = onTimeTipClickListener;
    }
}
