package www.qisu666.com.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import www.qisu666.com.R
import www.qisu666.com.utils.CacheUtils
import www.qisu666.com.utils.StringUtils
import www.qisu666.com.utils.TVUtils
import www.qisu666.com.utils.ToastUtil
import kotlinx.android.synthetic.main.layout_order_discount.view.*

/**
 * Created by wujiancheng on 2017/11/22.
 * 还车完成账单和订单中的折扣优惠信息
 */
class OrderDiscountView : FrameLayout {
    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        val layoutInflater = LayoutInflater.from(context)
        layoutInflater.inflate(R.layout.layout_order_discount, this)
    }

    fun isViewDiscountLineVisibility() {
        if (llytNightDiscount.visibility == View.GONE
                && llytParkDiscount.visibility == View.GONE
                && llytCompanyDiscount.visibility == View.GONE
                && llytCouponDiscount.visibility == View.GONE) {
            viewDiscountLine.visibility = View.GONE
        } else {
            viewDiscountLine.visibility = View.VISIBLE
        }
    }

    /**
     * 夜间优惠
     */
    fun setNightDiscountData(nightDiscountMoney: String?) {
        if (!StringUtils.isEmpty(nightDiscountMoney) && "0" != nightDiscountMoney) {
            llytNightDiscount.visibility = View.VISIBLE
            //夜间优惠金额
            tvNightDiscount.text = "${TVUtils.toString(Integer.parseInt(nightDiscountMoney) / 100.00)}元"
        } else {
            llytNightDiscount.visibility = View.GONE
        }
    }

    /**
     * 网点折扣
     */
    fun setParkDiscountData(parkDiscountMoney: String?, parkDiscount: String?) {
        if (!StringUtils.isEmpty(parkDiscountMoney) && "0" != parkDiscountMoney) {
            //网点折扣
            tvParkDiscountMoney.text = "${TVUtils.toString(Integer.parseInt(parkDiscountMoney) / 100.00)}元"
            if (!StringUtils.isEmpty(parkDiscount) && "0" != parkDiscount) {
                tvParkDiscount.text = "(${TVUtils.toString1(parkDiscount?.toFloat()?.div(10.0F) ?: 0F)}折)"
            } else {
                tvParkDiscount.visibility = View.GONE
            }
        } else {
            llytParkDiscount.visibility = View.GONE
        }
    }

    /**
     * 企业折扣
     */
    fun setCompanyDiscountData(companyDiscountMoney: String?) {
        //企业折扣
        if (!StringUtils.isEmpty(companyDiscountMoney) && "0" != companyDiscountMoney) {
            val userInfoResp = CacheUtils.getIn().userInfo
            if (null != userInfoResp) {
                val company = userInfoResp.company
                if (null != company) {
                    val discount = company.discount
                    if (discount != 0) {
                        tvCompanyDiscount.text = ("(${TVUtils.toString1(discount.div(10.0F))}折)")
                    } else {
                        tvCompanyDiscount.visibility = View.GONE
                    }
                } else {
                    tvCompanyDiscount.visibility = View.GONE
                }
            } else {
                tvCompanyDiscount.visibility = View.GONE
            }
            tvCompanyDiscountMoney.text = "${TVUtils.toString(Integer.parseInt(companyDiscountMoney) / 100.00)}元"
        } else {
            llytCompanyDiscount.visibility = View.GONE
        }
    }

    /**
     * 优惠券
     */
    fun setCouponDiscountData(couponDiscountMoney: String?) {
        if (!StringUtils.isEmpty(couponDiscountMoney) && "0" != couponDiscountMoney) {
            if (StringUtils.isIntOrFloat(couponDiscountMoney)) {
                val couponD = couponDiscountMoney?.toDouble()
                if (couponD?.times(100) == 0.0) {
                    llytCouponDiscount.visibility = View.GONE
                } else {
                    tvCouponDiscountMoney.text = "${TVUtils.toString(couponD?.div(100.00) ?: 0.00)}元"
                    llytCouponDiscount.visibility = View.VISIBLE
                }
            } else {
                llytCouponDiscount.visibility = View.GONE
            }
        } else {
            llytCouponDiscount.visibility = View.GONE
        }
    }

    /**
     * 不计免赔
     */
    fun setInsuranceMoneyData(insuranceMoney: String?, insuranceRemark: String?) {
        if (!StringUtils.isEmpty(insuranceMoney) && "0" != insuranceMoney) {
            //不计免赔金额
            tvInsuranceAmount.text = "${TVUtils.toString(Integer.parseInt(insuranceMoney) / 100.00)}元"
            tvInsuranceRemark.text = "($insuranceRemark)"
        } else {
            llytInsurance.visibility = View.GONE
        }
    }
}