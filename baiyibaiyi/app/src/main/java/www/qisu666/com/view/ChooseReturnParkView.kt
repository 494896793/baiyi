package www.qisu666.com.view

import android.content.Context
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import www.qisu666.com.R
import www.qisu666.com.callback.ParksResp
import www.qisu666.com.utils.HighlightUtil
import www.qisu666.com.utils.StringUtils
import www.qisu666.com.utils.logI
import kotlinx.android.synthetic.main.view_choose_return_park.view.*


/**
 * Created by wujiancheng on 2017/11/6.
 * 选择还车网点
 */
class ChooseReturnParkView : FrameLayout {
    var onReturnCarClickListener: OnReturnCarClickListener? = null
    var onNavigateClickListener: OnNavigateClickListener? = null
    var parksResp: ParksResp? = null

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
        LayoutInflater.from(context).inflate(R.layout.view_choose_return_park, this)

        tvReturnCar.setOnClickListener {
            onReturnCarClickListener?.onReturnCarClick(parksResp)
        }
        //导航
        llytNavigate.setOnClickListener({
            onNavigateClickListener?.onNavigateClick(parksResp)
        })
    }

    fun setData(parksResp: ParksResp) {
        this.parksResp = parksResp
        val parkName = parksResp.parkName
        if (parksResp.isNearest) {//离我最近
            val replace = "replace"
            val spannableString = SpannableString(parkName + " " + replace)
            //获取图片
            val span = CustomImageSpan(context, R.mipmap.rg_16, CustomImageSpan.ALIGN_FONT_CENTER)
            spannableString.setSpan(span, (parkName + " ").length, (parkName + " " + replace).length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            tvParkName.text = spannableString
        } else {
            tvParkName.text = parkName
        }
        tvParkNamedDetail.text = parksResp.parkAddress
        tvDistance.text = parksResp.distance
    }

    /**
     * 设置还车折扣信息，和预约剩余时间
     */
    fun setDiscountInfo(parksResp: ParksResp, leftTime: String) {
//        logI("设置还车折扣信息，和预约剩余时间")
        if (null != parksResp.discountLimit) {//折扣网点
            val info = "在此网点还车享${parksResp.discountLimit.div(10)}折优惠"
            if (!StringUtils.isEmpty(leftTime)) {
//                logI("有打折信息")
                tvReturnCarDiscount.text = Html.fromHtml(HighlightUtil.convertHightlightText("$info，请在${leftTime}内还车", leftTime, "#0B1222"))
            } else {
                tvReturnCarDiscount.text = info
//                logI("没打折信息")
            }

            tvReturnCarDiscount.visibility = View.VISIBLE
        } else {
            tvReturnCarDiscount.visibility = View.GONE
        }
    }

    /**
     * 设置还车按钮文字，请求地理围栏的过程中不能点击
     */
    fun setReturnCarButtonText(text: String) {
        tvReturnCar.text = text
    }

    /**
     * 设置还车按钮是否可以点击
     */
    fun setReturnCarButtonEnable(isEnable: Boolean) {
        tvReturnCar.isEnabled = isEnable
    }

    interface OnReturnCarClickListener {
        fun onReturnCarClick(parksResp: ParksResp?)
    }

    interface OnNavigateClickListener {
        fun onNavigateClick(parksResp: ParksResp?)
    }
}