package www.qisu666.com.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import www.qisu666.com.R
import kotlinx.android.synthetic.main.view_battery.view.*

/**
 * Created by wujiancheng on 2018/1/12.
 * 电池
 */
class BatteryView : FrameLayout {
    var layoutInflater: LayoutInflater? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        layoutInflater = LayoutInflater.from(context)
        layoutInflater?.inflate(R.layout.view_battery, this)
    }

    /**
     * 设置电池的百分比0---100
     */
    fun setBatteryPercent(percent: Double) {
        llytBatteryContainer.removeAllViews()
        val levelBattery = percent.toInt().div(10)
        for (level in 0 until 10) {
            val viewBatteryItem = layoutInflater?.inflate(R.layout.view_battery_item, llytBatteryContainer, false)
            if (level < levelBattery) {
                if (levelBattery >= 8) {
                    viewBatteryItem?.setBackgroundResource(R.drawable.bg_battery_green)
                } else if (levelBattery >= 4) {
                    viewBatteryItem?.setBackgroundResource(R.drawable.bg_battery_orange)
                } else {
                    viewBatteryItem?.setBackgroundResource(R.drawable.bg_battery_red)
                }
            } else {
                viewBatteryItem?.setBackgroundResource(R.drawable.bg_battery_gray)
            }
            llytBatteryContainer.addView(viewBatteryItem)
        }
    }
}