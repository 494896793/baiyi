package www.qisu666.com.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import www.qisu666.com.R
import kotlinx.android.synthetic.main.view_battery_item.view.*

/**
 * Created by wujiancheng on 2018/1/12.
 * 电池条
 */
class BatteryItemView : FrameLayout {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.view_battery_item, this)
    }

    /**
     * @resId R.drawable.xxx
     */
    fun setBackgound(resId: Int) {
        viewBatteryItem.setBackgroundResource(resId)
    }
}