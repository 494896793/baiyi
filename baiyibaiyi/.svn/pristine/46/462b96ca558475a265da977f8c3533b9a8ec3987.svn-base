package www.qisu666.com.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import www.qisu666.com.R;
import www.qisu666.com.rx.RxBus;
import www.qisu666.com.rx.RxBusEvent;
import www.qisu666.com.rx.RxEventCodes;
import www.qisu666.com.rx.RxTimeCountDown;
import www.qisu666.com.utils.CacheUtils;
import www.qisu666.com.utils.DateUtils;
import www.qisu666.com.utils.Logger;

import rx.Subscriber;
import rx.Subscription;

/**
 * Created by wujiancheng on 2017/9/25.
 * 短租用车倒计时，剩余时间
 */

public class LongRentLeftTimeView extends LinearLayout {
    private static final String TAG = LongRentLeftTimeView.class.getSimpleName();
    private TextView tvTimeDown;
    private ImageView ivLongRentHelp;
    private Subscription subscription;
    private OnTimeDownFinishListener mOnTimeDownFinishListener;
    private OnTimeTipClickListener mOnTimeTipClickListener;

    private int tipFlag = 0;//60：表示60--30分钟内的提示过了，30：表示30--10分钟内的提示过了，10：表示10--0分钟内的提示过了
    private int minute60 = 60 * 60;//60分钟
    private int minute30 = 30 * 60;//30分钟
    private int minute10 = 10 * 60;//10分钟

    public LongRentLeftTimeView(Context context) {
        super(context);
        init();
    }

    public LongRentLeftTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LongRentLeftTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_long_rent_left_time, this);
        tvTimeDown = (TextView) view.findViewById(R.id.tvTimeDown);
        ivLongRentHelp = (ImageView) view.findViewById(R.id.ivLongRentHelp);
        ivLongRentHelp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnTimeTipClickListener) {
                    mOnTimeTipClickListener.onTimeTipClickListener();
                }
            }
        });

        tipFlag = CacheUtils.getIn().getLongRentTipFlag(getContext());
    }

//    /**
//     * 设置到期提示
//     */
//    public void setTimeTip(String timestamp) {
//        try {
//            String endTime = DateUtils.timestampToString(timestamp, "MM月dd日 HH:mm");
//            String tip = "使用至 " + endTime + "，提前还车则套餐结束，\n超出部分按分时租赁收费";
//            String replace = "img";
//            SpannableString spannableString = new SpannableString(tip + " " + replace);
//            //获取图片
//            CustomImageSpan span = new CustomImageSpan(getContext(), R.mipmap.help_gray_icon, CustomImageSpan.ALIGN_FONT_CENTER);
//            spannableString.setSpan(span, (tip + " ").length(), (tip + " " + replace).length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//            tvTimeTip.setText(spannableString);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }

//    String replace = "离我最近";
//
//    SpannableString spannableString = new SpannableString(parkName + " " + replace);
//
//    //获取图片
//    CustomImageSpan span = new CustomImageSpan(activity, R.mipmap.nearest_icon, CustomImageSpan.ALIGN_FONT_CENTER);
//
//    spannableString.setSpan(span, (parkName + " ").length(), (parkName + " " + replace).length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//    tvParkName.setText(spannableString);

    /**
     * 设置剩余时间
     */
    public void setLeftTime(int minute) {
        tvTimeDown.setText(DateUtils.time2HourMinute(minute));
    }

    /**
     * 设置倒计时
     *
     * @param seconds 秒
     */
    public void setTimeDown(int seconds) {
        stopTime();
        subscription = RxTimeCountDown.timeCountDown(seconds)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        if (mOnTimeDownFinishListener != null) {
                            mOnTimeDownFinishListener.onTimeDownFinish();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        String time = DateUtils.time2HourMinuteSecond(integer);
                        tvTimeDown.setText(time);
                        if (integer <= minute60 && integer > minute30) {
                            showLongRentTip(60);
                        } else if (integer <= minute30 && integer > minute10) {
                            showLongRentTip(30);
                        } else if (integer <= minute10 && integer > 0) {
                            showLongRentTip(10);
                        }
                    }
                });
    }

    public void destroyView() {
        stopTime();
    }

    public void stopTime() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    public interface OnTimeDownFinishListener {
        void onTimeDownFinish();
    }

    public void setOnTimeDownFinishListener(OnTimeDownFinishListener onTimeDownFinishListener) {
        this.mOnTimeDownFinishListener = onTimeDownFinishListener;
    }

    public interface OnTimeTipClickListener {
        void onTimeTipClickListener();
    }

    public void setOnTimeTipClickListener(OnTimeTipClickListener onTimeTipClickListener) {
        this.mOnTimeTipClickListener = onTimeTipClickListener;
    }

    /**
     * 记录当前是在哪个时间段，并如果在这个时间段没提示过的话，那么提示
     *
     * @param tipFlag
     */
    private void showLongRentTip(int tipFlag) {
        if (this.tipFlag == tipFlag) {
            return;
        }
        this.tipFlag = tipFlag;
        CacheUtils.getIn().setLongRentTipFlag(getContext(), tipFlag);
        RxBusEvent event = new RxBusEvent();
        event.setEventCode(RxEventCodes.CODE_LONG_RENT_BEFORE_FINISH_TIP);
        RxBus.getInstance().post(event);
    }
}
