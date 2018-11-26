package www.qisu666.com.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import www.qisu666.com.R
import www.qisu666.com.utils.DateUtils
import www.qisu666.com.utils.StringUtils
import www.qisu666.com.utils.TVUtils
import kotlinx.android.synthetic.main.view_use_car_cost.view.*

/**
 * Created by wujiancheng on 2017/11/4.
 * 分时租赁 时长，里程，费用
 */
class UseCarCostView : FrameLayout {
    var onUseCarHelpClickListener: OnUseCarHelpClickListener? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    fun init() {
        LayoutInflater.from(context).inflate(R.layout.view_use_car_cost, this)
        tvUseCarHelp.setOnClickListener {
            onUseCarHelpClickListener?.onUseCarHelpClick()
        }
    }

    /**
     * @param useTimeStr 使用时长，秒
     * @param useMileage 使用里程，公里
     * @param useCost 费用，分
     */
    fun setData(useTimeStr: String, useMileage: String, useCost: Int) {
        if (!StringUtils.isEmpty(useTimeStr)) {
            var useTime = useTimeStr.toLong()
            useTime = useTime / 1000 / 60//分钟
            tvOverUseTime.text = DateUtils.minuteToDay(useTime)
            tvOverUseTime.text = useTime.toString()
        }
        tvOverUseFee.text = "${TVUtils.toString(useCost.div(100.0))}"
        tvOverMileage.text = "${TVUtils.toString2(useMileage)}"
    }

    interface OnUseCarHelpClickListener {
        fun onUseCarHelpClick()
    }

}