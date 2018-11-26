package www.qisu666.com.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import www.qisu666.com.R
import www.qisu666.com.utils.DensityUtil
import kotlinx.android.synthetic.main.layout_company_discount.view.*
import kotlinx.android.synthetic.main.view_recommend_park.view.*

/**
 * Created by wujiancheng on 2018/1/24.
 */
class RecommendParkView : FrameLayout {
    private var onRecommendParkClickListener: OnRecommendParkClickListener? = null
    private val isHigh = 1
    private val isShort = 2
    //输入框的高度状态
    private var editTextHeightStatus = isShort

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.view_recommend_park, this)

        tvDiscountCompany.text = "当前推荐位置"
        //输入的内容
        etRecommendContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                tvTextLength.text = "${s?.length ?: 0}/100"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        tvCommitRecommend.setOnClickListener {
            onRecommendParkClickListener?.onRecommendParkClick(etRecommendContent.text.toString().trim())
        }
    }

    fun setData(recommendParkName: String) {
        tvRecommendParkName.text = recommendParkName
    }

    interface OnRecommendParkClickListener {
        fun onRecommendParkClick(remark: String)
    }

    /**
     * 点击提交推荐信息
     */
    fun setOnRecommendParkClickListener(onRecommendParkClickListener: OnRecommendParkClickListener) {
        this.onRecommendParkClickListener = onRecommendParkClickListener
    }

    /**
     * 设置提交按钮的显示与隐藏
     */
    fun setCommitBtnVisibility(visibility: Boolean) {
        tvCommitRecommend.visibility = when (visibility) {
            true -> View.VISIBLE
            false -> View.GONE
        }
    }

    /**
     * 设置输入框的高度
     */
    fun setRecommendContentHeight(keyboardStatus: Boolean) {
        val content = etRecommendContent.text.toString().trim()
        val params = etRecommendContent.layoutParams
        when (keyboardStatus) {
            true -> {
                if (isShort == editTextHeightStatus) {
                    params.height = DensityUtil().dp2px(context, 120F)
                    etRecommendContent.layoutParams = params
                    editTextHeightStatus = isHigh
                }
            }
            false -> {
                if (content.isEmpty()) {//输入内容不为空则高度不变小，为空才变小
                    if (isHigh == editTextHeightStatus) {
                        params.height = DensityUtil().dp2px(context, 47F)
                        etRecommendContent.layoutParams = params
                        editTextHeightStatus = isShort
                    }
                }
            }
        }
    }
}