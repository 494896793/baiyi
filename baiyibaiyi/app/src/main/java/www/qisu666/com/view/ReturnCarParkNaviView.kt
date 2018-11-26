package www.qisu666.com.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.amap.api.maps.model.LatLng
import www.qisu666.com.R
import www.qisu666.com.activity.SearchParkActivity
import www.qisu666.com.utils.NavigationUtils
import kotlinx.android.synthetic.main.view_return_car_park_navi.view.*

/**
 * Created by wujiancheng on 2017/11/2.
 * 还车网点选择导航
 */
class ReturnCarParkNaviView : FrameLayout {
    var from = 0//标记从哪个页面进入搜索页面，默认主页

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
        LayoutInflater.from(context).inflate(R.layout.view_return_car_park_navi, this)
        //选择还车点
        llytReturnAddr.setOnClickListener {
            val intent = Intent(context, SearchParkActivity::class.java)
            intent.putExtra("from", from)
            context.startActivity(intent)
        }
    }

    fun setData(activity: Activity, returnParkName: String, returnParkAddr: String, latLng: LatLng) {
        tvParkName.text = returnParkName
        tvParkAddr.text = returnParkAddr
        rlytNavigate.visibility = View.VISIBLE
        ivNavigate.setOnClickListener { NavigationUtils.goNavigation(activity, latLng, 0) }
    }
}