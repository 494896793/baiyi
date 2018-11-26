package www.qisu666.com.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import www.qisu666.com.R
import www.qisu666.com.utils.SharedPreferencesUtils
import kotlinx.android.synthetic.main.view_kf_ear_phone.view.*

/**
 * Created by wujiancheng on 2018/1/22.
 */
class KfEarPhoneViews : FrameLayout {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.view_kf_ear_phone, this)
    }

    fun setData(activity: Activity) {
        val kfPPWs = KfPPWs(activity)
        ivEarPhone.setOnClickListener {
            kfPPWs.showKfPPW()
        }
        kfPPWs.setOnUsingGuideClickListener(object : KfPPWs.OnUsingGuideClickListener {
            override fun onUsingGuideClick() {
                ivKfUnread.visibility = View.GONE
            }

        })

        val isRead = SharedPreferencesUtils.getBoolean(context, "isReadUsingGuide", false)
        if (isRead) {
            ivKfUnread.visibility = View.GONE
        } else {
            ivKfUnread.visibility = View.VISIBLE
        }
    }
}