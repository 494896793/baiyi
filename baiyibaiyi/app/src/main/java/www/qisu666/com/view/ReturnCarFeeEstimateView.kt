package www.qisu666.com.view

import android.content.Context
import android.content.Intent
import android.text.Spanned
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import www.qisu666.com.R
import www.qisu666.com.activity.SearchParkActivity
import kotlinx.android.synthetic.main.view_return_car_fee_estimate.view.*

/**
 * Created by wujiancheng on 2017/11/2.
 * 还车网点费用估计
 */
class ReturnCarFeeEstimateView : FrameLayout {
    var from = 0

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
        LayoutInflater.from(context).inflate(R.layout.view_return_car_fee_estimate, this)
        llytReturnAddr.setOnClickListener {
            val intent = Intent(context, SearchParkActivity::class.java)
            intent.putExtra("from", from)
            context.startActivity(intent)
        }
    }

    fun setData(returnAddr: String, returnFee: String) {
        tvReturnAddr.text = returnAddr
        tvReturnFee.text = returnFee
    }

    fun setData(returnAddr: String, returnFee: Spanned) {
        tvReturnAddr.text = returnAddr
        tvReturnFee.text = returnFee
    }
}